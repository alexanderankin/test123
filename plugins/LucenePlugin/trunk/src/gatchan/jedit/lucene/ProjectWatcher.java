/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
 * Copyright (C) 2009, 2011 Shlomy Reinstein
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.lucene;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PluginUpdate;

import projectviewer.event.StructureUpdate;
import projectviewer.event.ViewerUpdate;

public class ProjectWatcher
{
	public ProjectWatcher()
	{
		EditBus.addToBus(this);
	}
	
	@EBHandler
	public void handlePluginUpdate(PluginUpdate pu) 
	{
		if (PluginUpdate.LOADED.equals(pu.getWhat()) && LucenePlugin.instance.isProjectViewerAvailable())
		{
			View view = jEdit.getActiveView();
			LucenePlugin.instance.setCurrentIndex(view, LucenePlugin.instance.getProjectName(view));
		}
	}

	@EBHandler
	public void handleStructureUpdate(StructureUpdate su) throws IndexInterruptedException
	{
		if (su.getType() == StructureUpdate.Type.PROJECT_REMOVED)
			checkRemoveProjectIndex(su.getNode().getName());
	}

	@EBHandler
	public void handleViewerUpdate(ViewerUpdate vu)
	{
		if (vu.getType() == ViewerUpdate.Type.PROJECT_LOADED)
			LucenePlugin.instance.setCurrentIndex(vu.getView(), vu.getNode().getName());
	}

	private static void checkRemoveProjectIndex(String project) throws IndexInterruptedException
	{
		Index index = LucenePlugin.instance.getIndex(project);
		if (index == null)
			return;
		int res = JOptionPane
			.showConfirmDialog(jEdit.getActiveView(), "Remove Lucene index of project '" + project + "'?",
					   "Lucene plugin", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION)
			LucenePlugin.instance.removeIndex(project);
	}

	public void stop()
	{
		EditBus.removeFromBus(this);
	}
}
