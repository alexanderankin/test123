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
