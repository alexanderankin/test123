package perl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PerlProcess
{
	private Process p;
	private StreamConsumer outputConsumer;
	private StreamConsumer errorConsumer;
	private String command;
	public PerlProcess(LaunchConfig config)
	{
		String perl = GeneralOptionPane.getPerlPath();
		if (perl.contains(" ") && ! perl.startsWith("\""))
			perl = "\"" + perl + "\"";
		command = perl + " " + config.script + " " + config.arguments;
		try
		{
			p = Runtime.getRuntime().exec(command);
		} catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("PerlProcess: Failed to run " + command);
			return;
		}
		outputConsumer = new StreamConsumer(p.getInputStream());
		errorConsumer = new StreamConsumer(p.getErrorStream());
	}
	public void startConsumingOutput()
	{
		outputConsumer.run();
		errorConsumer.run();
	}
	public String toString()
	{
		return command;
	}
	public StreamConsumer getOutput()
	{
		return outputConsumer;
	}
	public StreamConsumer getError()
	{
		return errorConsumer;
	}
	public OutputStream getInput()
	{
		return p.getOutputStream();
	}
	public interface LineHandler
	{
		void handle(String line);
	}
	public static class StreamConsumer
	{
		private BufferedReader reader;
		private List<LineHandler> handlers;
		public StreamConsumer(InputStream is)
		{
			reader = new BufferedReader(new InputStreamReader(is));
			handlers = new ArrayList<LineHandler>();
		}
		public void addHandler(LineHandler handler)
		{
			handlers.add(handler);
		}
		public void removeHandler(LineHandler handler)
		{
			handlers.remove(handler);
		}
		public void run()
		{
			try
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					for (LineHandler handler: handlers)
						handler.handle(line);
				}
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();  
			}
		}
	}
}
