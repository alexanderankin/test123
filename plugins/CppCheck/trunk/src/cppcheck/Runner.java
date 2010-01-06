package cppcheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class Runner
{
	// Runs CppCheck on a file / directory tree.
	public void run(String path)
	{
		Vector<String> what = new Vector<String>();
		what.add(path);
		run(what);
	}
	
	interface LineHandler
	{
		void handle(String line);
	}

	public void run(Vector<String> what)
	{
		String path = OptionPane.getPath();
		String [] args = new String[2];
		args[0] = path;
		for (String file: what)
		{
			args[1] = file;
			try
			{
				Process p = Runtime.getRuntime().exec(args);
				StreamConsumer osc = new StreamConsumer(p.getInputStream(),
					new OutputHandler());
				osc.start();
				StreamConsumer esc = new StreamConsumer(p.getErrorStream(),
					new ErrorHandler());
				esc.start();
				p.waitFor();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class StreamConsumer extends Thread
	{
		private InputStream is;
		private LineHandler handler;
		public StreamConsumer(InputStream is, LineHandler handler)
		{
			this.is = is;
			this.handler = handler;
		}
		public void run()
		{
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				while ((line = br.readLine()) != null)
				{
					if (handler != null)
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
