package classpath;

import java.io.File;
import java.util.TreeSet;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.util.Log;

public class ClasspathPlugin extends EBPlugin {

	private static TreeSet projects;

	/**
	 * Start method
	 */
	public void start() {
		projects = new TreeSet();

		refreshProjects();
		updateClasspath();
	}

	public void stop() {}

	/**
	 * Refreshes the set of open projects if ProjectViewer is installed
	 */
	public static void refreshProjects() {
		projects.clear();

		// do nothing if ProjectViewer isn't installed
		if (jEdit.getPlugin("projectviewer.ProjectPlugin") == null)
			return;

		View[] views = jEdit.getViews();
		for (int i = 0; i<views.length; i++) {
			projectviewer.ProjectViewer viewer = projectviewer.ProjectViewer.getViewer(views[i]);
			projectviewer.vpt.VPTProject project = viewer.getActiveProject(views[i]);
			if (project != null)
				projects.add(project);
		}
	}

	/**
	 * Update the classpath
	 * This method takes into account both the global classpath and all open projects
	 */
	public static void updateClasspath() {
		StringBuilder cpBuilder = new StringBuilder();

		// First, add the classpath for any open projects
		if (jEdit.getPlugin("projectviewer.ProjectPlugin") != null) {
			for (Object p : projects) {
				projectviewer.vpt.VPTProject project = (projectviewer.vpt.VPTProject) p;
				String cp = project.getProperty("java.classpath");
				if (cp != null && cp.length() > 0)
					cpBuilder.append(cp+File.pathSeparator);
			}
		}

		// Now add the global classpath
		String cp = jEdit.getProperty("java.customClasspath", "");
		if (cp.length() > 0)
			cpBuilder.append(cp+File.pathSeparator);

		// Include installed jars?
		if (jEdit.getBooleanProperty("java.classpath.includeInstalled")) {
			PluginJAR[] jars = jEdit.getPluginJARs();
			for (int i = 0; i<jars.length; i++)
				cpBuilder.append(jars[i].getPath()+File.pathSeparator);
		}

		// Last, do the system classpath
		if (jEdit.getBooleanProperty("java.classpath.includeSystem")) {
			String system = System.getProperty("java.class.path");
			if (system.length() > 0)
				cpBuilder.append(system+File.pathSeparator);
		}

		// Now set the property
		int end = cpBuilder.length()-1;
		if (cpBuilder.charAt(end) == File.pathSeparatorChar)
			cpBuilder.deleteCharAt(end);

		jEdit.setProperty("java.classpath", cpBuilder.toString());

		// If necessary, update console's var
		updateEnv(jEdit.getBooleanProperty("java.classpath.includeWorking"));

		// Tell anyone listening that the classpath was updated
		EditBus.send(new ClasspathUpdate());
	}

	/**
	 * Sets the console's CLASSPATH environment variable to the set classpath
	 */
	public static void updateEnv(boolean includeWorking) {
		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			String cp = jEdit.getProperty("java.classpath");
			if (includeWorking && cp.length() > 0)
				cp = "."+File.pathSeparator+cp;

			console.ConsolePlugin.setSystemShellVariableValue("CLASSPATH", cp);
		}
	}

	/**
	 * Returns the classpath
	 * @param refresh optional parameter to refresh the classpath first
	 */
	public static String getClasspath() {
		return getClasspath(false);
	}

	public static String getClasspath(boolean refresh) {
		if (refresh)
			updateClasspath();

		return jEdit.getProperty("java.classpath");
	}

	/**
	 * We need to keep track of a couple key events to make sure the classpath stays up-to-date:
	 * - when a project is opened or closed
	 * - when a project's properties have been updated (might be new addition/removal to classpath)
	 * - when a view is opened or closed
	 * - when a plugin jar is (un)loaded (only if installed plugins should be included)
	 */
	public void handleMessage(EBMessage msg) {
		// only check project events if ProjectViewer is installed
		if (jEdit.getPlugin("projectviewer.ProjectPlugin") != null) {
		   	if (msg instanceof projectviewer.event.ViewerUpdate) {
				projectviewer.event.ViewerUpdate update = (projectviewer.event.ViewerUpdate) msg;

				// The viewer's current 'active project' is old, so remove it
				projectviewer.ProjectViewer viewer = update.getViewer();
				// this may happen on first load
				if (viewer == null)
					return;

				projectviewer.vpt.VPTProject old = viewer.getActiveProject(viewer.getView());
				if (old != null)
					projects.remove(old);

				if (update.getType().equals(projectviewer.event.ViewerUpdate.Type.PROJECT_LOADED)) {
					// New project
					projects.add(update.getNode());
				}

				updateClasspath();
			}
			else if (msg instanceof projectviewer.event.ProjectUpdate) {
				// Re-update the classpath of a project's properties were changed
				projectviewer.event.ProjectUpdate update = (projectviewer.event.ProjectUpdate) msg;
				if (update.getType().equals(projectviewer.event.ProjectUpdate.Type.PROPERTIES_CHANGED))
					updateClasspath();
			}
		}

		if (msg instanceof ViewUpdate) {
			ViewUpdate update = (ViewUpdate) msg;

			// If a view was opened refresh the set of projects
			Object what = update.getWhat();
			if (what.equals(ViewUpdate.CREATED))
				refreshProjects();
		}
		else if (msg instanceof PluginUpdate) {
			// We only care about this if we need to include installed jars
			if (jEdit.getBooleanProperty("java.classpath.includeInstalled"))
				updateClasspath();
		}
	}

}
