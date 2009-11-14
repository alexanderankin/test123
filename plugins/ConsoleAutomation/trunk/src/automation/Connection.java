package automation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection
{
	private String host;
	private int port;
	private Socket socket;
	private BufferedWriter writer;
	private InputStreamReader reader;
	private CharHandler outputHandler;
	private LineHandler expectPrefixHandler;
	private LineHandler expectLineHandler;
	private Object expectHandlerLock = new Object();

	public interface CharHandler
	{
		void handle(char c);
	}
	public interface LineHandler
	{
		// Returns true to continue reading, false to stop reading
		boolean handle(String line);
	}
	public Connection(String host, int port)
	{
		this.host = host;
		this.port = port;
	}
	public void connect() throws UnknownHostException, IOException
	{
		socket = new Socket(host, port);
		reader = new InputStreamReader(socket.getInputStream());
		writer = new BufferedWriter(new OutputStreamWriter(
			socket.getOutputStream()));
		start();
	}
	public void disconnect() throws IOException
	{
		reader.close();
		writer.close();
		socket.close();
	}
	public void send(String s) throws IOException
	{
		if (! s.endsWith("\n"))
			s = s + "\n";
		writer.write(s);
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
