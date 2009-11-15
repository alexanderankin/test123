package automation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
	private PrintStream writer;
	private InputStreamReader reader;
	private CharHandler outputHandler;
	private LineHandler expectPrefixHandler;
	private LineHandler expectLineHandler;
	private Object expectHandlerLock = new Object();
	private ArrayList<StringBuilder> expectBuffer =
		new ArrayList<StringBuilder>();

	public interface CharHandler
	{
		void handle(char c);
	}
	public interface LineHandler
	{
		// Returns true to continue reading, false to stop reading
		boolean handle(String line);
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
		writer = new PrintStream(telnet.getOutputStream());
		start();
	}
	public void disconnect() throws IOException
	{
		reader.close();
		writer.close();
		telnet.disconnect();
	}
	public void send(String s) throws IOException
	{
		if (! s.endsWith("\n"))
			s = s + "\n";
		synchronized (expectHandlerLock)
		{
			expectBuffer.clear();
		}
		for (char c: s.toCharArray())
		{
			if (outputHandler != null)
				outputHandler.handle(c);
		}
		writer.print(s);
		writer.flush();
	}
	public String expectSubstr(String s, boolean prefix)
		throws IOException, InterruptedException
	{
		SubstrHandler h = new SubstrHandler(s);
		synchronized(expectHandlerLock)
		{
			if (prefix)
				expectPrefixHandler = h;
			else
				expectLineHandler = h;
			consumeBuffer();
			// If the expected text has been found, no need to wait
			if ((prefix && (expectPrefixHandler == null)) ||
				((!prefix) && (expectLineHandler == null)))
			{
				return h.line;
			}
		}
		synchronized(h)
		{
			h.wait();
		}
		return h.line;
	}
	public Matcher expectPattern(Pattern p, boolean prefix)
		throws IOException, InterruptedException
	{
		PatternHandler h = new PatternHandler(p);
		synchronized(expectHandlerLock)
		{
			if (prefix)
				expectPrefixHandler = h;
			else
				expectLineHandler = h;
			consumeBuffer();
			// If the expected text has been found, no need to wait
			if ((prefix && (expectPrefixHandler == null)) ||
				((!prefix) && (expectLineHandler == null)))
			{
				return h.m;
			}
		}
		synchronized(h)
		{
			h.wait();
		}
		return h.m;
	}
	// Attach an output listener
	public void setOutputHandler(CharHandler h)
	{
		outputHandler = h;
	}

	private void consumeBuffer()
	{
		while (expectBuffer.size() > 0)
		{
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
}
