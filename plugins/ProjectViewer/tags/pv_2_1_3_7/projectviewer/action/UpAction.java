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
