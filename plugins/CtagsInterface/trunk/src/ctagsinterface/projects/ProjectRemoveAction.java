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

public class ProjectRemoveAction extends Action
{
	public ProjectRemoveAction()
	{
		super("remove-project-tags");
	}
	@Override
	public String getText()
	{
		return "Remove project from tag database";
	}
	public void actionPerformed(ActionEvent arg0)
	{
		if (viewer != null)
		{
			VPTNode sel = viewer.getSelectedNode();
			while (sel != null && (! sel.isProject()))
				sel = (VPTNode) sel.getParent();
			if (sel == null)
				return;
			VPTProject p = (VPTProject) sel;
			CtagsInterfacePlugin.deleteOrigin(OriginType.PROJECT, p.getName());
		}
	}
}