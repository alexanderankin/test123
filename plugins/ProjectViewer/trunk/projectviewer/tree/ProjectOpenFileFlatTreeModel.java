/*  $Id$
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.tree;

import java.util.*;
import javax.swing.tree.TreePath;

import org.gjt.sp.util.Log;

import projectviewer.*;
import projectviewer.event.*;

/** A tree model that represents all files in a project without any relationship
 * to the folders they are a child of.
 */
public final class ProjectOpenFileFlatTreeModel extends ProjectFileFlatTreeModel {

	/** Create a new <code>ProjectFilesFlatTreeModel</code>.
	 *
	 * @param  aProject  Description of Parameter
	 */
	public ProjectOpenFileFlatTreeModel(Project aProject) {
		super(aProject);
	}

	/** Notification that a project file has been opened.
	 *
	 * @param  evt  Description of Parameter
	 */
	public void fileOpened(ProjectEvent evt) {
		addProjectFile(evt.getProjectFile());
		fireNodeChanged(getPathToRoot(), files.indexOf(evt.getProjectFile()));
	}

	/** Notification that a project file has been closed.
	 *
	 * @param  evt  Description of Parameter
	 */
	public void fileClosed(ProjectEvent evt) {
		removeProjectFile(evt.getProjectFile());
	}

	/** Notification that a project file has been removed.
	 *
	 * @param  evt  Description of Parameter
	 */
	public void fileRemoved(ProjectEvent evt) {
		if (files.contains(evt.getProjectFile()))
			super.fileRemoved(evt);
	}

	/** Returns <code>true</code> if the given file should be added to the
	 * model.  This method by default returns <code>true</code>, so subclasses
	 * should override this method to filter out any files.
	 *
	 * @param  aFile  Description of Parameter
	 * @return        Description of the Returned Value
	 */
	protected boolean accept(ProjectFile aFile) {
		return aFile.isOpened();
	}

   /** Returns the path to the root.
    *
    * @return    The pathToRoot value
    */
   private TreePath getPathToRoot() {
      List path = new ArrayList(1);
      path.add(getRoot());
      return toTreePath(path);
   }
}

