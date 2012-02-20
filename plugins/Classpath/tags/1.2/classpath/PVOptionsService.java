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
import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.OptionGroup;
//}}}

/**
 * A project options service that lets us configure project-specific
 * classpaths.
 *
 * @author <a href="mailto:damienradtke@gmail.com">Damien Radtke</a>
 * @version 1.2
 */
public class PVOptionsService implements OptionsService {
	
	//{{{ getOptionPane(VPTProject) : OptionPane
	/**
	 * Gets a new option pane for the given project.
	 *
	 * @param project The project to configure.
	 * @return A new option pane to configure its classpath.
	 */
	public OptionPane getOptionPane(VPTProject project) {
		return new PVClasspathOptionPane(project);
	} //}}}

	//{{{ getOptionGroup(VPTProject) : OptionGroup
	/**
	 * Gets the option group, in this case null.
	 *
	 * @param project The project to configure.
	 * @return null, since we aren't using an option group.
	 */
	public OptionGroup getOptionGroup(VPTProject project) {
		return null;
	} //}}}
}
