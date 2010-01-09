package cppcheck;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cppcheck.Plugin.Listener;
import cppcheck.Runner.LineHandler;

public class OutputHandler extends LineHandler
{
	private int numFiles;
	private int completedFiles = 0;
	private Pattern progressPattern = Pattern.compile(
		"(\\d+)/(\\d+) files checked (\\d+)% done");
	//1/35 files checked 2% done
	private Vector<Listener> listeners = new Vector<Listener>();

	public interface Listener
	{
		void addOutputLine(String line);
		void setProgress(int percent);
	}

	public void addListener(Listener l)
	{
		listeners.add(l);
	}
	public void removeListener(Listener l)
	{
		listeners.remove(l);
	}

	public void start(int numFiles)
	{
		this.numFiles = numFiles;
	}

	public void startTask(String cmdLine)
	{
		handle("Started: " + cmdLine);
	}

	public void handle(String line)
	{
		System.err.println(line);
		Matcher m = progressPattern.matcher(line);
		int percent = -1;
		if (m.find())
		{
			percent = Integer.valueOf(m.group(3)).intValue();
			percent = (percent + completedFiles * 100) / numFiles; 
		}
		for (Listener ol: listeners)
		{
			ol.addOutputLine(line);
			if (percent != -1)
				ol.setProgress(percent);
		}
	}
	
	public void endTask(String cmdLine)
	{
		handle("Ended: " + cmdLine);
		completedFiles++;
		int percent = (int) Math.round(((double)completedFiles / numFiles) * 100);
		updateProgress(percent);
	}

	private void updateProgress(int percent)
	{
		for (Listener ol: listeners)
			ol.setProgress(percent);
	}

	public void end()
	{
	}
}
