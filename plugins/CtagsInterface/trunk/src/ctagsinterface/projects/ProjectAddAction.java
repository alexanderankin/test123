/**
 * 
 */
package ctagsinterface.projects;

import java.awt.event.ActionEvent;

import projectviewer.action.Action;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import ctagsinterface.index.TagIndex.OriginType;
import ctagsinterface.main.CtagsInterfacePlugin;

public class ProjectAddAction extends Action
{
	public ProjectAddAction()
	{
		super("add-project-tags");
	}
	@Override
	public String getText()
	{
		return "Add project to tag database";
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
			CtagsInterfacePlugin.insertOrigin(OriginType.PROJECT,
				project.getName());
		}
	}
}