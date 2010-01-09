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
	private Process process;
	private StateListener stateListener;
	private OutputHandler outputHandler;

	public Runner(View view, Vector<String> paths)
	{
		this.view = view;
		this.paths = new Vector<String>(paths);
		outputHandler = new OutputHandler();
	}

	public OutputHandler getOutputHandler()
	{
		return outputHandler;
	}

	public abstract static class LineHandler
	{
		void start(int nFiles) {}
		void startTask(String cmdLine) {}
		abstract void handle(String line);
		void endTask(String cmdLine) {}
		void end() {}
	}

	public interface StateListener
	{
		void start();
		void end();
	}

	public void setStateListener(StateListener sl)
	{
		stateListener = sl;
	}

	public void abort()
	{
		if (process != null)
			process.destroy();
	}

	public void run()
	{
		if (stateListener != null)
			stateListener.start();
		Vector<String> cmd = new Vector<String>();
		cmd.add(OptionPane.getPath());
		OptionPane.addArgs(cmd);
		int numArgs = cmd.size();
		String [] args = new String[numArgs + 1];
		for (int i = 0; i < numArgs; i++)
			args[i] = cmd.get(i);
		ErrorHandler errorHandler = new ErrorHandler(view);
		outputHandler.start(paths.size());
		errorHandler.start(paths.size());
		// Abort cppcheck if jEdit is closed during its execution
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());
		for (String path: paths)
		{
			args[numArgs] = path;
			String cmdLine = getCommandString(args);
			outputHandler.startTask(cmdLine);
			errorHandler.startTask(cmdLine);
			try
			{
				process = Runtime.getRuntime().exec(args);
				StreamConsumer osc = new StreamConsumer(process.getInputStream(),
					outputHandler);
				osc.start();
				StreamConsumer esc = new StreamConsumer(process.getErrorStream(),
					errorHandler);
				esc.start();
				process.waitFor();
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
		if (stateListener != null)
			stateListener.end();
	}

	private class ShutdownThread extends Thread
	{
		public void run()
		{
			if (process != null)
				process.destroy();
		}
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
