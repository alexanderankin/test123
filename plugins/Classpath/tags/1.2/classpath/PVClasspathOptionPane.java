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
import common.gui.pathbuilder.PathBuilder;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import projectviewer.vpt.VPTProject;
//}}}

/**
 * An option pane to configure a project-specific classpath.
 *
 * @author <a href="mailto:damienradtke@gmail.com">Damien Radtke</a>
 * @version 1.2
 */
public class PVClasspathOptionPane extends AbstractOptionPane {

	//{{{ Private members
	private VPTProject project;
	private PathBuilder path;
	//}}}

	//{{{ PVClasspathOptionPane(VPTProject)
	/**
	 * Constructs a new option pane for the provided project.
	 *
	 * @param project The project to configure.
	 */
	public PVClasspathOptionPane(VPTProject project) {
		super("project.classpath");
		this.project = project;
	} //}}}

	//{{{ _init() : void
	/**
	 * Initialize this option pane.
	 */
	@Override
	protected void _init() {
		path = new PathBuilder(jEdit.getProperty("options.classpath.path"));
		path.setFileFilter(new ClasspathFilter());
		String cp = project.getProperty("java.classpath");
		if (cp != null)
			path.setPath(cp);

		addComponent(path);
	} //}}}

	//{{{ _save() : void
	/**
	 * Saves the classpath for this project.
	 */
	@Override
	protected void _save() {
		project.setProperty("java.classpath", path.getPath());
		ClasspathPlugin.updateClasspath();
	} //}}}
}
