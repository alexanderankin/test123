/* $Id$
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

import java.util.Iterator;
import javax.swing.tree.TreeModel;

import org.gjt.sp.util.Log;

import projectviewer.tree.*;

/** A project view for all projects.
 *
 */
public final class AllProjectsView implements ProjectView {

	private ProjectViewer viewer;

	/** Create a new <code>AllProjectsView</code>.
	 *
	 *@param  aViewer  Description of Parameter
	 */
	public AllProjectsView(ProjectViewer aViewer) {
		viewer = aViewer;
	}

	/** Returns the current project.
	 *
	 *@return    The currentProject value
	 */
	public Project getCurrentProject() {
		Object node = viewer.getSelectedNode();
		//Log.log( Log.DEBUG, this, "Finding project for artifact " + node );
		if (node instanceof Project)
			return (Project) node;
		for (Iterator i = projects(); i.hasNext(); ) {
			Project each = (Project) i.next();
			if (each.isProjectArtifact(node))
				return each;
		}
		return null;
	}

	/** Returns the tree model for the folder view.
	 *
	 *@return    The folderViewModel value
	 */
	public TreeModel getFolderViewModel() {
		return new AllProjectsFileTreeModel();
	}

	/** Returns the tree model for the file view.
	 *
	 *@return    The fileViewModel value
	 */
	public TreeModel getFileViewModel() {
		return new AllProjectsFileFlatTreeModel();
	}

	/** Returns the tree model for the working files view.
	 *
	 *@return    The workingFileViewModel value
	 */
	public TreeModel getWorkingFileViewModel() {
		return new AllProjectsOpenFileFlatTreeModel();
	}

	/** Activate the view. */
	public void activate() {
		for (Iterator i = projects(); i.hasNext(); )
			((Project) i.next()).activate();
	}

	/** Deactivate the view. */
	public void deactivate() {
		for (Iterator i = projects(); i.hasNext(); )
			((Project) i.next()).deactivate();
	}

	/** Shortcut to {@link ProjectManager#projects()}.
	 *
	 *@return    Description of the Returned Value
	 */
	private Iterator projects() {
		return ProjectManager.getInstance().projects();
	}

}

