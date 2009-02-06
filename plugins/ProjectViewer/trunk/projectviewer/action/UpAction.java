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
 *
 * @author ezust
 * @since pv 2.1.3.6
 *
 */
public class UpAction extends Action
{

	public UpAction() {
		super("projectviewer_wrapper_goup");
	}

	public String getText()
	{
		return jEdit.getProperty("projectviewer.action.goup");
	}

	public void actionPerformed(ActionEvent e)
	{
		JTree tree = viewer.getCurrentTree();
		View v = viewer.getView();
		TreePath path = tree.getSelectionPath();
		TreePath pp = path.getParentPath();
		if (pp == null)
		{
			VPTNode n = viewer.getActiveNode(v);
			TreeNode p = n.getParent();
			if (p != null)
			{
				viewer.setActiveNode(v, (VPTNode) p);
			}
		}
		else {
			tree.setSelectionPath(pp);
		}
	}
}
