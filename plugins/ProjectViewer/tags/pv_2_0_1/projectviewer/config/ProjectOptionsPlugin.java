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

//{{{ Imports
import org.gjt.sp.jedit.gui.OptionsDialog;

import projectviewer.vpt.VPTProject;
//}}}

/**
 *  Provides an interface for querying objects about plugin specific option
 *	panes for the ProjectOptions dialog.
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 */
public interface ProjectOptionsPlugin {

	/**
	 *	Method called every time the project options dialog box is displayed.
	 *	Any option panes created by the plugin should be added here.
	 *
	 *	@param	p	The project whose options will be shown. Guaranteed not to
	 *				be null.
	 */
	public void createOptionPanes(OptionsDialog od, VPTProject p);

}

