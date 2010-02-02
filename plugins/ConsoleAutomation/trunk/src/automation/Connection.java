package automation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.telnet.TelnetClient;

public class Connection
{
	private static final int SLEEP_PERIOD = 200;
	private final String name;
	private final String host;
	private final int port;
	private TelnetClient telnet;
	private PrintWriter writer;
	private InputStreamReader reader;
	private CharHandler outputHandler;
	private EventHandler eventHandler;
	private StringHandler expectHandler;
	private final Object expectHandlerLock = new Object();
	private final List<StringBuilder> expectBuffer = new ArrayList<StringBuilder>();
	private boolean scriptAborted = false;
	private final Thread scriptThread = new ScriptThread();
	private final List<Runnable> scripts = new ArrayList<Runnable>();
	static private ThreadLocal<Connection> tlsConnection =
		new ThreadLocal<Connection>();
	private boolean abort = false;

	public interface CharHandler
	{
		void handle(char c);
	}
	public interface StringHandler
	{
		// Returns true to stop reading, false to continue reading
		boolean handle(String s);
		// Returns the matched value on success
		Object value();
		// Whether this handler handles prefixes (or only whole lines)
		boolean prefix();
		// Returns a description for this handler
		String desc();
	}
	public interface EventHandler
	{
		void expecting(StringHandler h);
		void sending(String s);
		void idle();
	}

	// Public methods

	public static Connection getCurrentConnection()
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
	public String getHost()
	{
		return host;
	}
	public int getPort()
	{
		return port;
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
		abort();
		reader.close();
		writer.close();
		telnet.disconnect();
	}
	public void send(String s) throws IOException 
	{
		if (scriptAborted)
			return;
		if ((s.length() == 0) || (! s.endsWith("\n")))
			s = s + "\n";
		synchronized (expectHandlerLock)
		{
			expectBuffer.clear();
		}
		if (eventHandler != null)
			eventHandler.sending(s);
		writer.print(s);
		writer.flush();
		if (eventHandler != null)
			eventHandler.idle();
	}

	private void setExpectHandler(StringHandler h)
	{
		expectHandler = h;
		if (eventHandler != null)
			eventHandler.expecting(h);
	}
	private Object expect(StringHandler h) throws InterruptedException
	{
		if (scriptAborted)
			return null;
		boolean cont;
		synchronized(expectHandlerLock)
		{
			setExpectHandler(h);
			consumeBuffer();
			// If the expected text has been found, no need to wait
			cont = (expectHandler != null);
		}
		if (cont)
		{
			synchronized(h)
			{
				h.wait();
			}
		}
		if (scriptAborted)
			return null;
		return h.value();
	}
	public String expectSubstr(String s, boolean prefix) throws InterruptedException
	{
		Object value = expect(new SubstrHandler(s, prefix));
		return (String) value;
	}
	public Matcher expectPattern(Pattern p, boolean prefix) throws InterruptedException
	{
		Object value = expect(new PatternHandler(p, prefix));
		return (Matcher) value;
	}
	// Attach an output listener
	public void setOutputHandler(CharHandler h)
	{
		outputHandler = h;
	}
	public void setEventHandler(EventHandler h)
	{
		eventHandler = h;
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
		synchronized (expectHandlerLock)
		{
			scriptAborted = true;
			notifyExpectHandler();
		}
	}

	// Private methods

	private void notifyExpectHandler()
	{
		if (expectHandler != null)
		{
			synchronized(expectHandler)
			{
				expectHandler.notifyAll();
			}
			setExpectHandler(null);
		}
	}
	private void consumeBuffer()
	{
		while (! expectBuffer.isEmpty())
		{
			if (scriptAborted)
				return;
			String s = expectBuffer.get(0).toString();
			expectBuffer.remove(0);
			if ((expectHandler != null) && expectHandler.handle(s))
			{
				notifyExpectHandler();
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
	private void processChar(char c, StringBuilder s)
	{
		if (outputHandler != null)
			outputHandler.handle(c);
		if (c == '\n')
		{
			synchronized(expectHandlerLock)
			{
				if (expectHandler == null)
					expectBuffer.add(new StringBuilder());
				else if ((! expectHandler.prefix()) &&
						 expectHandler.handle(s.toString()))
				{
					notifyExpectHandler();
				}
			}
			s.setLength(0);
			return;
		}
		s.append(c);
		synchronized(expectHandlerLock)
		{
			if ((expectHandler == null) || (! expectHandler.prefix()))
			{
				if (expectBuffer.isEmpty())
					expectBuffer.add(new StringBuilder());
				expectBuffer.get(expectBuffer.size()-1).append(c);
			}
			else if (expectHandler.handle(s.toString()))
				notifyExpectHandler();
		}
	}
	private synchronized void abort()
	{
		abort = true;
	}
	private synchronized boolean isAborted()
	{
		return abort;
	}
	private void processOutput() throws IOException
	{
		expectBuffer.add(new StringBuilder(""));
		StringBuilder s = new StringBuilder();
		int i;
		while (! isAborted())
		{
			while (reader.ready())
			{
				if (isAborted())
					return;
				i = reader.read();
				if (i == -1)
					return;
				processChar((char) i, s);
			}
			try
			{
				Thread.sleep(SLEEP_PERIOD);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class SubstrHandler implements StringHandler
	{
		private boolean prefix;
		private final String subStr;	// the substring to look for
		private String line;	// the line where the substring was found
		public SubstrHandler(String s, boolean prefix)
		{
			subStr = s;
			this.prefix = prefix;
		}
		public boolean handle(String s)
		{
			line = s;
			return s.contains(subStr);
		}
		public Object value()
		{
			return line;
		}
		public boolean prefix()
		{
			return prefix;
		}
		public String desc()
		{
			return "Substr: " + subStr;
		}
	}
	private static class PatternHandler implements StringHandler
	{
		private boolean prefix;
		private final Pattern p;
		private Matcher m;
		public PatternHandler(Pattern p, boolean prefix)
		{
			this.p = p;
			this.prefix = prefix;
		}
		public boolean handle(String s)
		{
			m = p.matcher(s);
			return m.find();
		}
		public Object value()
		{
			return m;
		}
		public boolean prefix()
		{
			return prefix;
		}
		public String desc()
		{
			return "Pattern: " + p.toString();
		}
	}
	private class ScriptThread extends Thread
	{
		@Override
		public void run()
		{
			tlsConnection.set(Connection.this);
			while (! isAborted())
			{
				Runnable r = null;
				synchronized (scripts)
				{
					if (! scripts.isEmpty())
						r = scripts.remove(0);
				}
				if (r != null)
				{
					scriptAborted = false;
					r.run();
					if (eventHandler != null)
						eventHandler.idle();
				}
				else
				{
					try
					{
						Thread.sleep(SLEEP_PERIOD);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
