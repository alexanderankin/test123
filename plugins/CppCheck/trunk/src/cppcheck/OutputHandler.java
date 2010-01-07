package cppcheck;

import org.gjt.sp.jedit.View;

import cppcheck.Runner.LineHandler;

public class OutputHandler implements LineHandler
{
	private View view;
	private CppCheckDockable dockable;

	public OutputHandler(View view)
	{
		this.view = view;
	}

	public void handle(String line)
	{
		System.err.println(line);
		dockable = (CppCheckDockable)
			view.getDockableWindowManager().getDockable("cppcheck");
		if (dockable != null)
			dockable.addOutputLine(line);
	}

	public void start(String path)
	{
		handle("Started: " + path);
	}
	
	public void end(String path)
	{
		handle("Ended: " + path);
	}
}
