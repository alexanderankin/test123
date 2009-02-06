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

import java.awt.event.ActionEvent;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTNode;

/**
 * Opens all selected file nodes. This action is only shown in the
 * context menu when multiple files are selected.
 *
 * @author Marcelo Vanzin
 * @since PV 3.0.0
 *
 */
public class OpenSelectedAction extends Action
{

	public OpenSelectedAction()
	{
		super("projectviewer_wrapper_open_selected");
	}

	public String getText()
	{
		return jEdit.getProperty("projectviewer.action.open_selected");
	}

	public void actionPerformed(ActionEvent e)
	{
		JTree tree = viewer.getCurrentTree();
		TreePath[] paths = tree.getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			VPTNode n = (VPTNode) paths[i].getLastPathComponent();
			if (n.canOpen()) {
				n.open();
			}
		}
	}

	public void prepareForNode(VPTNode node)
	{
		boolean visible = false;
		if (node == null) {
			JTree tree = viewer.getCurrentTree();
			TreePath[] paths = tree.getSelectionPaths();

			for (int i = 0; i < paths.length; i++) {
				VPTNode n = (VPTNode) paths[i].getLastPathComponent();
				if (n.canOpen()) {
					visible = true;
					break;
				}
			}
		}
		cmItem.setVisible(visible);
	}

}
