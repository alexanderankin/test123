package cppcheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.gjt.sp.jedit.View;

public class Runner implements Runnable
{
	private View view;
	private Vector<String> paths;

	public Runner(View view, String path)
	{
		this.view = view;
		paths = new Vector<String>();
		paths.add(path);
	}

	public Runner(View view, Vector<String> paths)
	{
		this.view = view;
		this.paths = new Vector<String>(paths);
	}

	public abstract static class LineHandler
	{
		void start(int nFiles) {}
		void startTask(String cmdLine) {}
		abstract void handle(String line);
		void endTask(String cmdLine) {}
		void end() {}
	}

	public void run()
	{
		Vector<String> cmd = new Vector<String>();
		cmd.add(OptionPane.getPath());
		OptionPane.addArgs(cmd);
		int numArgs = cmd.size();
		String [] args = new String[numArgs + 1];
		for (int i = 0; i < numArgs; i++)
			args[i] = cmd.get(i);
		OutputHandler outputHandler = new OutputHandler(view);
		ErrorHandler errorHandler = new ErrorHandler(view);
		outputHandler.start(paths.size());
		errorHandler.start(paths.size());
		for (String path: paths)
		{
			args[numArgs] = path;
			String cmdLine = getCommandString(args);
			outputHandler.startTask(cmdLine);
			errorHandler.startTask(cmdLine);
			try
			{
				Process p = Runtime.getRuntime().exec(args);
				StreamConsumer osc = new StreamConsumer(p.getInputStream(),
					outputHandler);
				osc.start();
				StreamConsumer esc = new StreamConsumer(p.getErrorStream(),
					errorHandler);
				esc.start();
				p.waitFor();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			outputHandler.endTask(cmdLine);
			errorHandler.endTask(cmdLine);
		}
		outputHandler.end();
		errorHandler.end();
	}

	private String getCommandString(String [] args)
	{
		StringBuilder sb = new StringBuilder();
		for (String arg: args)
		{
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(arg);
		}
		return sb.toString();
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
