package cppcheck;

import java.util.Iterator;
import java.util.Vector;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

public class Plugin extends EditPlugin
{
	public static final String OPTION = "options.cppcheck.";
	public static final String MESSAGE = "messages.cppcheck.";
	private static DefaultErrorSource errorSource;

	public void start()
	{
		errorSource = new DefaultErrorSource("CppCheck");
		ErrorSource.registerErrorSource(errorSource);
	}

	public void stop()
	{
		ErrorSource.unregisterErrorSource(errorSource);
		errorSource = null;
	}

	public static DefaultErrorSource getErrorSource()
	{
		return errorSource;
	}

	public static void checkCurrentBuffer(View view)
	{
		checkPath(view, view.getBuffer().getPath());
	}

	public static void checkDirectory(View view, String directory)
	{
		checkPath(view, directory);
	}

	public static void checkCurrentProject(View view)
	{
		VPTProject p = ProjectViewer.getActiveProject(view);
		if (p == null)
			return;
		Vector<String> files = new Vector<String>();
		Iterator<VPTNode> nodes = p.getOpenableNodes().iterator();
		while (nodes.hasNext()) {
			VPTNode node = nodes.next();
			files.add(node.getNodePath());
		}
		checkPaths(view, files);
	}

	public static void clear(View view)
	{
		errorSource.clear();
	}

	private static void runInBackground(Runnable r)
	{
		Thread t = new Thread(r);
		t.start();
	}

	private static void checkPath(View view, String path)
	{
		runInBackground(new Runner(view, path));
	}
	public static void checkPaths(View view, Vector<String> paths)
	{
		runInBackground(new Runner(view, paths));
	}
}
