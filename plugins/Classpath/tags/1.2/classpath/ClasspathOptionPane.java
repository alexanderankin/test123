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
import java.io.File;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.util.ThreadUtilities;
//}}}

/**
 * The global option pane where classpath elements can be
 * configured.
 *
 * @author <a href="mailto:damienradtke@gmail.com">Damien Radtke</a>
 * @version 1.2
 */
public class ClasspathOptionPane extends AbstractOptionPane {

	//{{{ Private members
	private PathBuilder path;
	private JCheckBox includeWorking;
	private JCheckBox includeSystem;
	private JCheckBox includeInstalled;
	//}}}

	//{{{ ClasspathOptionPane()
	/**
	 * Constructs the option pane.
	 */
	public ClasspathOptionPane() {
		super("classpath");
	} //}}}

	//{{{ _init() : void
	/**
	 * Initializes the option pane.
	 */
	@Override
	protected void _init() {
		path = new PathBuilder(jEdit.getProperty("options.classpath.path"));
		path.setPath(jEdit.getProperty("java.customClasspath"));
		path.setFileFilter(new ClasspathFilter());

		includeWorking = new JCheckBox(jEdit.getProperty("options.classpath.includeWorking"),
				jEdit.getBooleanProperty("java.classpath.includeWorking"));
		includeSystem = new JCheckBox(jEdit.getProperty("options.classpath.includeSystem"),
				jEdit.getBooleanProperty("java.classpath.includeSystem"));
		includeInstalled = new JCheckBox(jEdit.getProperty("options.classpath.includeInstalled"),
				jEdit.getBooleanProperty("java.classpath.includeInstalled"));

		includeSystem.setToolTipText(System.getProperty("java.class.path"));

		addComponent(includeWorking);
		addComponent(includeSystem);
		addComponent(includeInstalled);
		addComponent(path);
	} //}}}

	//{{{ _save() : void
	/**
	 * Saves the current settings.
	 */
	@Override
	protected void _save() {
		String cp = path.getPath();
		boolean inclWorking = includeWorking.isSelected();
		boolean inclSystem = includeSystem.isSelected();
		boolean inclInstalled = includeInstalled.isSelected();

		jEdit.setProperty("java.customClasspath", cp);
		jEdit.setBooleanProperty("java.classpath.includeWorking", inclWorking);
		jEdit.setBooleanProperty("java.classpath.includeSystem", inclSystem);
		jEdit.setBooleanProperty("java.classpath.includeInstalled", inclInstalled);

		ClasspathPlugin.updateClasspath();
	} //}}}
}
