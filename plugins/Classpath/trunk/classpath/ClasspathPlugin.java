/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package classpath;

//{{{ Imports
import java.io.File;
import java.util.TreeSet;
import java.util.Iterator;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.util.ThreadUtilities;
//}}}

/**
 * The classpath plugin provides a unified method for defining
 * a classpath, both globally and on a per-project basis. The current
 * classpath can be obtained via the static getClasspath() method,
 * or through the environment variable $CLASSPATH in the Console's
 * system shell.
 *
 * @author <a href="mailto:damienradtke@gmail.com">Damien Radtke</a>
 */
public class ClasspathPlugin extends EBPlugin {

	protected static TreeSet projects;

	//{{{ start() : void
	/**
	 * Start the plugin.
	 */
	public void start() {
		projects = new TreeSet();
		updateClasspath();
	} //}}}

	//{{{ stop() : void
	/**
	 * Stop the plugin.
	 */
	public void stop() {

	} //}}}

	//{{{ updateClasspath() : void
	/**
	 * Starts a new ClasspathUpdateTask.
	 */
	public static void updateClasspath() {
		ThreadUtilities.runInBackground(new ClasspathUpdateTask());
	} //}}}

	//{{{ updateEnv(boolean) : void
	/**
	 * Sets the CLASSPATH environment variable.
	 *
	 * @param includeWorking Include the current working directory
	 *                       (".") as part of the classpath.
	 */
	public static void updateEnv(boolean includeWorking) {
		String cp = jEdit.getProperty("java.classpath");
		if (includeWorking)
			cp = "." + (cp.length() > 0 ? File.pathSeparator : "") + cp;

		try
		{
			BeanShell.getNameSpace().setVariable("CLASSPATH", cp);
		}
		catch (Exception e)
		{
		}

		// update the system shell, if it's installed
		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			console.ConsolePlugin.setSystemShellVariableValue("CLASSPATH", cp);
		}
	} //}}}

	//{{{ getClasspath() : String
	/**
	 * Returns the classpath without refreshing.
	 */
	public static String getClasspath() {
		return getClasspath(false);
	} //}}}

	//{{{ getClasspath(boolean) : String
	/**
	 * Returns the classpath.
	 *
	 * @param refresh If we should refresh the classpath before returning it.
	 *                Refreshing here is not recommended as it may slow jEdit down.
	 * @return The configured classpath.
	 */
	public static String getClasspath(boolean refresh) {
		if (refresh)
			updateClasspath();

		return jEdit.getProperty("java.classpath");
	} //}}}

	//{{{ handleMessage(EBMessage) : void
	/**
	 * We need to keep track of a couple key events to make sure the classpath stays up-to-date:
	 * - when a project is opened or closed
	 * - when a project's properties have been updated (might be new addition/removal to classpath)
	 * - when a view is opened or closed
	 * - when a plugin jar is (un)loaded (only if installed plugins should be included)
	 *
	 * @param msg The EBMessage object.
	 */
	@EBHandler
	public void handleMessage(EBMessage msg) {
		// only check project events if ProjectViewer is installed
		if (jEdit.getPlugin("projectviewer.ProjectPlugin") != null) {
			if (msg instanceof projectviewer.event.ViewerUpdate) {
				projectviewer.event.ViewerUpdate update = (projectviewer.event.ViewerUpdate) msg;

				// The viewer's current 'active project' is old, so remove it
				projectviewer.ProjectViewer viewer = update.getViewer();
				if (viewer != null) {
					projectviewer.vpt.VPTProject old = viewer.getActiveProject(viewer.getView());
					if (old != null)
						projects.remove(old);
				}

				if (update.getType().equals(projectviewer.event.ViewerUpdate.Type.PROJECT_LOADED)) {
					// New project
					if (update.getNode() != null
							&& update.getNode() instanceof projectviewer.vpt.VPTProject)
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

		if (msg instanceof PluginUpdate) {
			// We only care about this if we need to include installed jars
			if (jEdit.getBooleanProperty("java.classpath.includeInstalled"))
				updateClasspath();
		}
	} //}}}

}
