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

import javax.swing.JComponent;
import javax.swing.JTree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;

import projectviewer.event.ProjectViewerEvent;

import projectviewer.gui.GroupMenu;

import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
//}}}

/**
 *	Action to move a project or group into another group.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class MoveNodeAction extends Action {

	//{{{ getMenuItem() method
	/** Instantiates a GroupMenu and returns it. */
	public JComponent getMenuItem() {
		if (cmItem == null) {
			cmItem = new GroupMenu(getText(), false, this);
		}
		return cmItem;
	} //}}}

	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.move_node");
	} //}}}

	//{{{ actionPerformed(ActionEvent) method
	/** Moves the selected node to the selected destination. */
	public void actionPerformed(ActionEvent ae) {
		VPTNode dest = (VPTNode) ae.getSource();
		VPTNode toMove = viewer.getSelectedNode();
		VPTGroup oldParent = (VPTGroup) toMove.getParent();

		ProjectViewer.removeNodeFromParent(toMove);

		if (!viewer.getRoot().isNodeDescendant(dest)) {
			viewer.setRootNode(dest);
		}

		ProjectViewer.insertNodeInto(toMove, dest);
		ProjectManager.getInstance().saveProjectList();
		ProjectManager.getInstance().fireDynamicMenuChange();

		JTree tree = viewer.getCurrentTree();
		TreePath path = new TreePath(((DefaultTreeModel)tree.getModel()).getPathToRoot(toMove));
		tree.expandPath(path);

		// fire event
		ProjectViewer.fireNodeMovedEvent(toMove, oldParent);
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Show only for projects and (non-root) groups. */
	public void prepareForNode(VPTNode node) {
		if (node != null &&
				(node.isProject()
					|| (node.isGroup() && !node.isRoot()))) {
			cmItem.setVisible(true);

			GroupMenu gm = (GroupMenu) cmItem;
			gm.setIgnore(node);
			gm.populate(gm, VPTRoot.getInstance(), viewer.getView());
		} else {
			cmItem.setVisible(false);
		}
	}//}}}

}

