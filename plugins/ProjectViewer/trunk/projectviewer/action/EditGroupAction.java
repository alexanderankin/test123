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
import java.awt.Component;
import java.awt.event.ActionEvent;

import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
//}}}

/**
 *	Action that when executed creates a new group or edits an existing one.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class EditGroupAction extends Action {

	//{{{ Private members
	private boolean add;
	private VPTGroup parent;
	private Component gui;
	//}}}

	//{{{ +EditGroupAction(boolean) : <init>
	public EditGroupAction(boolean add) {
		this(add, null, null);
	} //}}}

	//{{{ +EditGroupAction(boolean, VPTGroup) : <init>
	public EditGroupAction(boolean add, VPTGroup parent, Component gui) {
		this.add = add;
		this.parent = parent;
		this.gui = gui;
	} //}}}

	//{{{ +getText() : String
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.a_e_project");
	} //}}}

	//{{{ +prepareForNode(VPTNode) : void
	/** Enable action only for group nodes. */
	public void prepareForNode(VPTNode node) {
		if (node != null && node.isGroup()) {
			if (node.isRoot() && !add) {
				cmItem.setVisible(false);
			} else {
				cmItem.setVisible(true);
				((JMenuItem)cmItem).setText( (add) ?
					jEdit.getProperty("projectviewer.action.add_group") :
					jEdit.getProperty("projectviewer.action.edit_group"));
			}
		} else {
			cmItem.setVisible(false);
		}
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		VPTGroup grp = (parent == null) ?
			(VPTGroup) viewer.getSelectedNode() : parent;

		if (add) {
			String name = getGroupName(null);
			if (name != null && name.length() > 0) {
				VPTGroup newGroup = new VPTGroup(name);
				ProjectViewer.insertNodeInto(newGroup, grp);

				JTree tree = viewer.getCurrentTree();
				TreePath path = new TreePath(((DefaultTreeModel)tree.getModel()).getPathToRoot(newGroup));
				tree.expandPath(path);

				ProjectViewer.fireGroupAddedEvent(newGroup);
			}
		} else {
			String name = getGroupName(grp.getName());
			if (name != null && name.length() > 0 && !name.equals(grp.getName())) {
				grp.setName(name);
				ProjectViewer.nodeChanged(grp);
			} else {
				return;
			}
		}

		ProjectManager.getInstance().fireDynamicMenuChange();
		ProjectManager.getInstance().saveProjectList();
	} //}}}

	//{{{ -getGroupName(String) : String
	private String getGroupName(String original) {
		Component parent = (viewer != null) ? viewer : gui;
		return JOptionPane.showInputDialog(parent,
				jEdit.getProperty("projectviewer.action.group.enter_name"),
				original);
	} //}}}

}

