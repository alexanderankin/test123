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

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.util.Task;
import org.gjt.sp.util.ThreadUtilities;
//}}}

/**
 * A task that updates the classpath. This is run in a background
 * thread when ClasspathPlugin.updateClasspath() is called.
 *
 * @author <a href="mailto:damienradtke@gmail.com">Damien Radtke</a>
 * @version 1.2
 */
public class ClasspathUpdateTask extends Task {

	//{{{ Private members
	int tasks;
	//}}}

	//{{{ ClasspathUpdateTask()
	/**
	 * Constructs a new ClasspathUpdateTask
	 */
	public ClasspathUpdateTask() {
		this.tasks = 4;
		//this.setCancellable(false);
		this.setLabel(jEdit.getProperty("classpath.taskLabel"));
	} //}}}

	//{{{ _run() : void
	/**
	 * Updates the classpath taking into account both the global
	 * classpath and any open projects.
	 */
	public void _run() {
		// use a set to make sure no location is added twice
		// this takes care of any possible redundancies
		TreeSet<String> path = new TreeSet<String>();

		// First, add the classpath for any open projects
		this.setValue(tasks++);
		if (jEdit.getPlugin("projectviewer.ProjectPlugin") != null) {
			for (Object p : ClasspathPlugin.projects) {
				projectviewer.vpt.VPTProject project = (projectviewer.vpt.VPTProject) p;
				String cp = project.getProperty("java.classpath");
				if (cp != null && cp.length() > 0)
					path.add(cp);
			}
		}

		// Now add the global classpath
		this.setValue(tasks++);
		String cp = jEdit.getProperty("java.customClasspath", "");
		if (cp.length() > 0)
			path.add(cp);

		// Include installed jars?
		this.setValue(tasks++);
		if (jEdit.getBooleanProperty("java.classpath.includeInstalled")) {
			PluginJAR[] jars = jEdit.getPluginJARs();
			for (int i = 0; i<jars.length; i++)
				path.add(jars[i].getPath());
		}

		// Last, do the system classpath
		this.setValue(tasks++);
		if (jEdit.getBooleanProperty("java.classpath.includeSystem")) {
			String system = System.getProperty("java.class.path");
			if (system.length() > 0)
				path.add(system);
		}

		// Now set the property
		this.setValue(tasks);
		StringBuffer pathBuffer = new StringBuffer();
		Iterator<String> iterator = path.iterator();
		while (iterator.hasNext()) {
			String elem = iterator.next();
			pathBuffer.append(elem);
			if (iterator.hasNext())
				pathBuffer.append(File.pathSeparator);
		}
		jEdit.setProperty("java.classpath", pathBuffer.toString());

		// If necessary, update console's var
		try
		{
			ClasspathPlugin.updateEnv(jEdit.getBooleanProperty("java.classpath.includeWorking"));
		}
		catch (Exception e)
		{
		}

		// Tell anyone listening that the classpath was updated
		EditBus.send(new ClasspathUpdate());
	} //}}}
}
