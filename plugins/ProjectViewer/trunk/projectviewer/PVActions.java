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
 * GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

//{{{ Imports
import java.util.Iterator;

import org.gjt.sp.jedit.View;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.action.EditProjectAction;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *  A collection of actions accessible through jEdit's Action mechanism, and
 *	other utility methods that may be interesting for interacting with the
 *	plugin.
 *
 *	@author		Marcelo Vanzin
 *  @version	$Id$
 */
public final class PVActions {

	//{{{ editProject(View) method
	/** If a project is currently opened, open its properties dialog. */
	public static void editProject(View view) {
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer == null) return;
		VPTNode sel = viewer.getSelectedNode();
		if (sel == null) {
			sel = viewer.getRoot();
		}
		if (sel != null && !sel.isRoot()) {
			EditProjectAction action = new EditProjectAction();
			action.setViewer(viewer);
			action.actionPerformed(null);
		}
	} //}}}

	//{{{ openAllProjectFiles(View) method
	/** If a project is currently active, open all its files. */
	public static void openAllProjectFiles(View view) {
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer == null) return;
		VPTNode sel = viewer.getRoot();
		if (!sel.isRoot()) {
			for (Iterator i = ((VPTProject)sel).getFiles().iterator(); i.hasNext(); ) {
				((VPTFile)i.next()).open();
			}
		}
	} //}}}

	//{{{ removeAllProjectFiles(View) method
	/** Removes all the children from the project active in the view. */
	public static void removeAllProjectFiles(View view) {
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer == null) return;
		VPTNode sel = viewer.getRoot();
		if (!sel.isRoot()) {
			((VPTProject)sel).removeAllChildren();
			if (ProjectViewerConfig.getInstance().getSaveOnChange()) {
				ProjectManager.getInstance().saveProject((VPTProject)sel);
			}
			ProjectViewer.nodeStructureChanged((VPTProject)sel);
		}
	} //}}}

	//{{{ getCurrentProject(View) method
	/**
	 *	Returns the active project. If no viewer is opened for the given view,
	 *	returns the last known active project (config.getLastProject()). Is one
	 *	exists but is currently being used in "All Projects" mode, return null.
	 *
	 *	@return	The currently active project, or null if no project is active and
	 *			ProjectViewerConfig.getLastProject() returns null.
	 */
	public static VPTProject getCurrentProject(View view) {
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer != null) {
			if (viewer.getRoot().isProject()) {
				return (VPTProject) viewer.getRoot();
			} else {
				return null;
			}
		} else {
			String pName = ProjectViewerConfig.getInstance().getLastProject();
			return (pName != null) ?
					ProjectManager.getInstance().getProject(pName) : null;
		}
	} //}}}

}

