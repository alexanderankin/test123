/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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
package projectviewer.config;

import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;

import projectviewer.vpt.VPTProject;


/**
 * Defines the service for showing custom options panes when editing a
 * project.
 *
 * @author	Marcelo Vanzin
 * @since	PV 3.0.0
 * @version	$Id$
 */

public interface OptionsService
{

	/**
	 * This method should return the option pane to be shown. As with
	 * regular jEdit option panes, the label to be shown in the dialog
	 * should be defined by the "option.[pane_name].label" property.
	 *
	 * @param	proj	The project that will be edited.
	 *
	 * @return An OptionPane instance, or null for no option pane.
	 */
	public OptionPane getOptionPane(VPTProject proj);


	/**
	 * This should return an OptionGroup to be shown. As with regular
	 * jEdit option groups, the label to be shown in the dialog
	 * should be defined by the "option.[group_name].label" property.
	 *
	 * @param	proj	The project that will be edited.
	 *
	 * @return An OptionGroup instance, or null for no option group.
	 */
	public OptionGroup getOptionGroup(VPTProject proj);

}

