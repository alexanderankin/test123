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
import projectviewer.importer.VFSFileImporter;
//}}}

/**
 *	Action that when executed imports files from jEdit's VFS into a node.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VFSFileImportAction extends Action {

	//{{{ +getText() : String
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.vfs_import");
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		new VFSFileImporter(viewer.getSelectedNode(), viewer).doImport();
	} //}}}

	//{{{ +prepareForNode(VPTNode) : void
	/** Enable action only for projects and directories. */
	public void prepareForNode(VPTNode node) {
		cmItem.setVisible( (node != null) &&
			(node.isProject() || node.isDirectory()) );
	} //}}}

}

