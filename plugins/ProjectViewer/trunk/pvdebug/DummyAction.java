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
package pvdebug;

//{{{ Imports
import java.awt.event.ActionEvent;

import javax.swing.Icon;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.vpt.VPTNode;
import projectviewer.action.Action;
//}}}

/**
 *	An action to be used for debugging purposes.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class DummyAction extends Action {

	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return "Dummy";
	} //}}}

	//{{{ getIcon() method
	/** Returns the icon for the action. */
	public Icon getIcon() {
		return GUIUtilities.loadIcon("Floppy.png");
	} //}}}

	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(viewer, "Dummy action test");
	} //}}}

}

