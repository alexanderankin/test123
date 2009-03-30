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
package poptions;

import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;

import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;


/**
 * Implements the PV interface to provide option panes to projects.
 *
 * @author	Marcelo Vanzin
 * @version	$Id$
 */

public class ProjectOptionsService implements OptionsService
{

	/**
	 * This method should return the option pane to be shown. As with
	 * regular jEdit option panes, the label to be show in the dialog
	 * should be defined by the "option.[pane_name].label" property.
	 *
	 * @param	proj	The project that will be edited.
	 *
	 * @return An OptionPane instance.
	 */
	public OptionPane getOptionPane(VPTProject proj)
	{
		return null;
	}


	/**
	 * This should return an OptionGroup to be shown.
	 *
	 * @param	proj	The project that will be edited.
	 *
	 * @return An OptionGroup instance.
	 */
	public OptionGroup getOptionGroup(VPTProject proj)
	{
		OptionGroup grp = new OptionGroup("poptions");
		grp.addOptionPane(new MainOptionPane(proj));
		grp.addOptionPane(new EditingOptionPane(proj));
		return grp;
	}

}

