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

import java.util.List;
import javax.swing.tree.TreePath;

import org.gjt.sp.util.Log;

import projectviewer.*;
import projectviewer.event.*;

/** A tree model that represents all files in a project and their relationship
 * to project folders.
 */
public class ProjectFileTreeModel extends ProjectTreeModel implements ProjectListener {

   /** Create a new <code>ProjectFilesFlatTreeModel</code>.
    *
    * @param  aProject  Description of Parameter
    */
   public ProjectFileTreeModel(Project aProject) {
      super(aProject);
      if (project != null)
         project.addProjectListener(this);
   }

   /** Returns the child of <code>parent</code> at index.
    *
    * @param  parent  Description of Parameter
    * @param  index   Description of Parameter
    * @return         The child value
    */
   public Object getChild(Object parent, int index) {
      if (parent instanceof Project)
         return ((Project) parent).getRoot().getChild(index);

      if (parent instanceof ProjectDirectory)
         return ((ProjectDirectory) parent).getChild(index);

      return null;
   }

   /** Returns the number of children of <code>parent</code>.
    *
    * @param  parent  Description of Parameter
    * @return         The childCount value
    */
   public int getChildCount(Object parent) {
      if (parent instanceof Project)
         return ((Project) parent).getRoot().getChildCount();

      if (parent instanceof ProjectDirectory)
         return ((ProjectDirectory) parent).getChildCount();

      return -1;
   }

   /** Returns the index of child in parent.
    *
    * @param  parent  Description of Parameter
    * @param  child   Description of Parameter
    * @return         The indexOfChild value
    */
   public int getIndexOfChild(Object parent, Object child) {
      if (parent instanceof Project)
         return ((Project) parent).getRoot().getIndexOfChild(child);

      if (parent instanceof ProjectDirectory)
         return ((ProjectDirectory) parent).getIndexOfChild(child);
	  
      return -1;
   }

   /** Notification that a project file has been opened.
    *
    * @param  evt  Description of Parameter
    */
   public void fileOpened(ProjectEvent evt) {
      fireNodeChanged(evt);
   }

   /** Notification that a project file has been closed.
    *
    * @param  evt  Description of Parameter
    */
   public void fileClosed(ProjectEvent evt) {
      fireNodeChanged(evt);
   }

   /** Notification that a project file has been added.
    *
    * @param  evt  Description of Parameter
    */
   public void fileAdded(ProjectEvent evt) {
      fireNodeInserted(buildPathTo(evt), evt.getArtifact());
   }

   /** Notification that a project file has been removed.
    *
    * @param  evt  Description of Parameter
    */
   public void fileRemoved(ProjectEvent evt) {
      fireNodeRemoved(buildPathTo(evt), evt.getIndex(), evt.getProjectFile());
   }

   /** Notification that a project directory has been added.
    *
    * @param  evt  Description of Parameter
    */
   public void directoryAdded(ProjectEvent evt) {
      fireNodeInserted(buildPathTo(evt), evt.getArtifact());
   }

   /** Notification that a project directory has been removed.
    *
    * @param  evt  Description of Parameter
    */
   public void directoryRemoved(ProjectEvent evt) {
      fireNodeRemoved(buildPathTo(evt), evt.getIndex(), evt.getProjectDirectory());
   }
   
   public void buildFileSelected(ProjectEvent evt) {
      // no-op  
   }

   /** Fire an event to the given file.
    *
    * @param  evt  Description of Parameter
    */
   private void fireNodeChanged(ProjectEvent evt) {
      TreePath path = buildPathTo(evt);
      Object lastComponent = path.getLastPathComponent();
      int index = (lastComponent instanceof Project) ?
            ((Project) lastComponent).getRoot().getIndexOfChild(evt.getProjectFile()) :
            ((ProjectDirectory) lastComponent).getIndexOfChild(evt.getProjectFile());
      fireNodeChanged(path, index);
   }

   /** Build a tree path to the specified project file, excluding the file itself.
    *
    * @param  evt  Description of Parameter
    * @return      Description of the Returned Value
    */
   private TreePath buildPathTo(ProjectEvent evt) {
      List path = evt.getPath();
      path.set(0, project);
      return toTreePath(path);
   }

}

