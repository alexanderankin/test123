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
import java.awt.event.ActionListener;

import javax.swing.Icon;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import projectviewer.importer.FileImporter;
//}}}

/**
 *	Action that when executed imports files into a node.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class FileImportAction extends Action {
	
	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.import");
	} //}}}
	
	//{{{ getIcon() method
	/**
	 *	Returns the icon to be shown on the toolbar button. The default
	 *	implementation returns "null" so that actions that will only be
	 *	used in the context menu don't need to implement this.
	 */
	public Icon getIcon() {
		return GUIUtilities.loadIcon("New.png");
	} //}}}
	
	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		new FileImporter(viewer.getSelectedNode(), viewer).doImport();
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (cmItem != null) {
			cmItem.setVisible( (node != null) && !node.isRoot() && !node.isFile() );
		}
	} //}}}

}
