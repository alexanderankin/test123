/**
 * 
 */
package cppcheck;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Vector;

import projectviewer.action.Action;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

public class ProjectCheckAction extends Action
{
	public ProjectCheckAction()
	{
		super("cppcheck-project");
	}
	@Override
	public String getText()
	{
		return "CppCheck project";
	}
	public void actionPerformed(ActionEvent arg0)
	{
		if (viewer != null)
		{
			VPTNode node = viewer.getSelectedNode();
			if (node == null)
				node = viewer.getRoot();
			VPTProject project = VPTNode.findProjectFor(node);
			if (project == null)
				return;
			Collection<VPTNode> nodes = project.getOpenableNodes();
			Vector<String> files = new Vector<String>();
			for (VPTNode n: nodes)
			{
				if (n.isFile())
					files.add(n.getNodePath());
			}
			Plugin.checkPaths(viewer.getView(), files);
		}
	}
}