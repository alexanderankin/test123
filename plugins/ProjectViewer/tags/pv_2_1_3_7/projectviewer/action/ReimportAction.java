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
package projectviewer.action;

//{{{ Imports
import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTNode;
import projectviewer.importer.ReImporter;
//}}}

/**
 *	Removes all the files below the root from a project, and then calls the
 *	initial project importer.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ReimportAction extends Action {

	public ReimportAction() {
		super("projectviewer_wrapper_reimport");
	}

	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.reimport");
	} //}}}

	//{{{ actionPerformed(ActionEvent) method
	/** Reimports files below the project root. */
	public void actionPerformed(ActionEvent ae) {
		VPTNode n = viewer.getSelectedNode();
		new ReImporter(n, viewer).doImport();
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		cmItem.setVisible(node != null && (node.isProject() || node.isDirectory()));
	} //}}}

}

