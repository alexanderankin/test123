package automation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.telnet.TelnetClient;

public class Connection
{
	private String name;
	private String host;
	private int port;
	private TelnetClient telnet;
	private PrintWriter writer;
	private InputStreamReader reader;
	private CharHandler outputHandler;
	private LineHandler expectPrefixHandler;
	private LineHandler expectLineHandler;
	private Object expectHandlerLock = new Object();
	private ArrayList<StringBuilder> expectBuffer =
		new ArrayList<StringBuilder>();
	private boolean abortScript = false;
	private Thread scriptThread = new ScriptThread();
	private ArrayList<Runnable> scripts =
		new ArrayList<Runnable>();
	static private ThreadLocal<Connection> tlsConnection =
		new ThreadLocal<Connection>();

	public interface CharHandler
	{
		void handle(char c);
	}
	public interface LineHandler
	{
		// Returns true to continue reading, false to stop reading
		boolean handle(String line);
	}

	// Public methods

	static Connection getCurrentConnection()
	{
		return tlsConnection.get();
	}

	public Connection(String name, String host, int port)
	{
		this.name = name;
		this.host = host;
		this.port = port;
	}
	public String getName()
	{
		return name;
	}
	public void connect() throws UnknownHostException, IOException
	{
		telnet = new TelnetClient();
		telnet.connect(host, port);
		reader = new InputStreamReader(telnet.getInputStream());
		writer = new PrintWriter(telnet.getOutputStream());
		start();
		scriptThread.start();
	}
	public void disconnect() throws IOException
	{
		reader.close();
		writer.close();
		telnet.disconnect();
	}
	public void send(String s) throws IOException
	{
		if (abortScript)
			return;
		if (! s.endsWith("\n"))
			s = s + "\n";
		synchronized (expectHandlerLock)
		{
			expectBuffer.clear();
		}
		/*
		for (char c: s.toCharArray())
		{
			if (outputHandler != null)
				outputHandler.handle(c);
		}
		*/
		writer.print(s);
		writer.flush();
	}
	public String expectSubstr(String s, boolean prefix) throws InterruptedException
	{
		if (abortScript)
			return null;
		SubstrHandler h = new SubstrHandler(s);
		synchronized(expectHandlerLock)
		{
			if (prefix)
				expectPrefixHandler = h;
			else
				expectLineHandler = h;
			consumeBuffer();
			if (abortScript)
				return null;
			// If the expected text has been found, no need to wait
			if (! expectHandlerExists(prefix))
				return h.line;
		}
		synchronized(h)
		{
			if (! expectHandlerExists(prefix))
			{
				if (abortScript)
					return null;
			}
			else
				h.wait();
		}
		if (abortScript)
			return null;
		return h.line;
	}
	private boolean expectHandlerExists(boolean prefix)
	{
		synchronized(expectHandlerLock)
		{
			return ((prefix && (expectPrefixHandler != null)) ||
				((! prefix) && (expectLineHandler != null)));
		}
	}
	public Matcher expectPattern(Pattern p, boolean prefix)
		throws IOException, InterruptedException
	{
		if (abortScript)
			return null;
		PatternHandler h = new PatternHandler(p);
		synchronized(expectHandlerLock)
		{
			if (prefix)
				expectPrefixHandler = h;
			else
				expectLineHandler = h;
			consumeBuffer();
			if (abortScript)
				return null;
			// If the expected text has been found, no need to wait
			if (! expectHandlerExists(prefix))
				return h.m;
		}
		synchronized(h)
		{
			if (! expectHandlerExists(prefix))
			{
				if (abortScript)
					return null;
			}
			else
				h.wait();
		}
		if (abortScript)
			return null;
		return h.m;
	}
	// Attach an output listener
	public void setOutputHandler(CharHandler h)
	{
		outputHandler = h;
	}

	// Macro support

	public void addScript(Runnable script)
	{
		synchronized (scripts)
		{
			scripts.add(script);
			if (scripts.size() == 1)	// Was empty before
				scripts.notifyAll();
		}
	}
	public void abortScript()
	{
		synchronized(expectHandlerLock)
		{
			if ((expectLineHandler != null) || (expectPrefixHandler != null))
				abortScript = true;
		}
	}

	// Private methods

	private void notifyExpectHandler()
	{
		synchronized(expectHandlerLock)
		{
			if (expectLineHandler != null)
			{
				synchronized(expectLineHandler)
				{
					expectLineHandler.notifyAll();
				}
				expectLineHandler = null;
			}
			else if (expectPrefixHandler != null)
			{
				synchronized(expectPrefixHandler)
				{
					expectPrefixHandler.notifyAll();
				}
				expectPrefixHandler = null;
			}
		}
	}
	private void consumeBuffer()
	{
		while (expectBuffer.size() > 0)
		{
			if (abortScript)
				return;
			String s = expectBuffer.get(0).toString();
			expectBuffer.remove(0);
			if (expectLineHandler != null)
			{
				if (! expectLineHandler.handle(s))
					expectLineHandler = null;
			}
			if (expectPrefixHandler != null)
			{
				if (! expectPrefixHandler.handle(s))
					expectPrefixHandler = null;
			}
			if ((expectLineHandler == null) &&
				(expectPrefixHandler == null))
			{
				return;
			}
		}
	}
	private void start()
	{
		Runnable r = new Runnable() {
			public void run() {
				try
				{
					processOutput();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		new Thread(r).start();
	}
	private void processOutput() throws IOException
	{
		expectBuffer.add(new StringBuilder(""));
		StringBuilder s = new StringBuilder();
		int i;
		while ((i = reader.read()) != -1)
		{
			if (abortScript)
				notifyExpectHandler();
			char c = (char) i;
			if (outputHandler != null)
				outputHandler.handle(c);
			if (c == '\n')
			{
				synchronized(expectHandlerLock)
				{
					if (expectLineHandler != null)
					{
						if (! expectLineHandler.handle(s.toString()))
							expectLineHandler = null;
					}
					else
						expectBuffer.add(new StringBuilder());
				}
				s.setLength(0);
				continue;
			}
			s.append(c);
			synchronized(expectHandlerLock)
			{
				if (expectPrefixHandler != null)
				{
					if (! expectPrefixHandler.handle(s.toString()))
						expectPrefixHandler = null;
				}
				else
				{
					if (expectBuffer.size() == 0)
						expectBuffer.add(new StringBuilder());
					expectBuffer.get(expectBuffer.size()-1).append(c);
				}
			}
		}
	}

	private static class SubstrHandler implements LineHandler
	{
		private String s;	// the substring to look for
		public String line;	// the line where the substring was found
		public SubstrHandler(String s)
		{
			this.s = s;
		}
		public boolean handle(String line)
		{
			if (line.contains(s))
			{
				this.line = line;
				synchronized(this)
				{
					notifyAll();
				}
				return false;
			}
			return true;
		}
	}
	private static class PatternHandler implements LineHandler
	{
		private Pattern p;
		public Matcher m;
		public PatternHandler(Pattern p)
		{
			this.p = p;
		}
		public boolean handle(String line)
		{
			m = p.matcher(line);
			if (m.find())
			{
				synchronized(this)
				{
					notifyAll();
				}
				return false;
			}
			return true;
		}
	}
	private class ScriptThread extends Thread
	{
		@Override
		public void run()
		{
			tlsConnection.set(Connection.this);
			boolean abort = false;
			while (! abort)
			{
				Runnable r = null;
				synchronized (scripts)
				{
					if (scripts.size() == 0)
					{
						try {
							scripts.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							abort = true;
							continue;
						}
					}
					r = scripts.remove(0);
				}
				r.run();
				abortScript = false;
			}
		}
	}
}
