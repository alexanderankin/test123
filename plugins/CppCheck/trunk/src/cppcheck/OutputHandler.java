package cppcheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.View;

import cppcheck.Runner.LineHandler;

public class OutputHandler extends LineHandler
{
	private View view;
	private CppCheckDockable dockable;
	private int numFiles;
	private int completedFiles = 0;
	private Pattern progressPattern = Pattern.compile(
		"(\\d+)/(\\d+) files checked (\\d+)% done");
	//1/35 files checked 2% done

	public OutputHandler(View view)
	{
		this.view = view;
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
		dockable = (CppCheckDockable)
			view.getDockableWindowManager().getDockable("cppcheck");
		if (dockable != null)
		{
			dockable.addOutputLine(line);
			if (percent != -1)
				dockable.setProgress(percent);
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
		dockable = (CppCheckDockable)
			view.getDockableWindowManager().getDockable("cppcheck");
		if (dockable == null)
			return;
		dockable.setProgress(percent);
	}

	public void end()
	{
		
	}
}
