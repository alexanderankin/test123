/* $Id$
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
public class ProjectFileFlatTreeModel extends ProjectTreeModel implements ProjectListener {

   protected List files;

   /** Create a new <code>ProjectFileFlatTreeModel</code>.
    *
    * @param  aProject  Description of Parameter
    */
   public ProjectFileFlatTreeModel(Project aProject) {
      super(aProject);
      //Log.log(Log.DEBUG,this,"ProjectFileFlatTreeModel::init()");
      files = new ArrayList();
      load();
      project.addProjectListener(this);
   }

   /** Returns the child of <code>parent</code> at index.
    *
    * @param  parent  Description of Parameter
    * @param  index   Description of Parameter
    * @return         The child value
    */
   public Object getChild(Object parent, int index) {
      return files.get(index);
   }

   /** Returns the number of children of <code>parent</code>.
    *
    * @param  parent  Description of Parameter
    * @return         The childCount value
    */
   public int getChildCount(Object parent) {
      return files.size();
   }

   /** Returns the index of child in parent.
    *
    * @param  parent  Description of Parameter
    * @param  child   Description of Parameter
    * @return         The indexOfChild value
    */
   public int getIndexOfChild(Object parent, Object child) {
      return files.indexOf(child);
   }

   /** Notification that a project file has been opened.
    *
    * @param  evt  Description of Parameter
    */
   public void fileOpened(ProjectEvent evt) {
      fireNodeChanged(getPathToRoot(), files.indexOf(evt.getProjectFile()));
   }

   /** Notification that a project file has been closed.
    *
    * @param  evt  Description of Parameter
    */
   public void fileClosed(ProjectEvent evt) {
      fireNodeChanged(getPathToRoot(), files.indexOf(evt.getProjectFile()));
   }

   /** Notification that a project file has been added.
    *
    * @param  evt  Description of Parameter
    */
   public void fileAdded(ProjectEvent evt) {
      addProjectFile(evt.getProjectFile());
   }

   /** Notification that a project file has been removed.
    *
    * @param  evt  Description of Parameter
    */
   public void fileRemoved(ProjectEvent evt) {
      removeProjectFile(evt.getProjectFile());
   }

   /** Notification that a project directory has been added.
    *
    * @param  evt  Description of Parameter
    */
   public void directoryAdded(ProjectEvent evt) { }

   /** Notification that a project directory has been removed.
    *
    * @param  evt  Description of Parameter
    */
   public void directoryRemoved(ProjectEvent evt) {
      ProjectDirectory dir = evt.getProjectDirectory();
      int i = 0;
      while (i < files.size()) {
         ProjectFile each = (ProjectFile) files.get(i);
         //Log.log( Log.DEBUG, this, "Checking file: " + each.getPath() );
         if (dir.isDescendant(each))
            removeProjectFile(each);
         else
            i++;
      }
   }

   /** Add a file.
    *
    * @param  aFile  The feature to be added to the ProjectFile attribute
    */
   protected void addProjectFile(ProjectFile aFile) {
      if (files.contains(aFile))
         return;
      files.add(aFile);
      Collections.sort(files, ProjectFile.getComparator());
      fireNodeInserted(getPathToRoot(), aFile);
   }

   /** Remove a file.
    *
    * @param  aFile  Description of Parameter
    */
   protected void removeProjectFile(ProjectFile aFile) {
      int fileIndex = files.indexOf(aFile);
      //Log.log( Log.DEBUG, this, "Removing file " + aFile.getPath() );
      files.remove(aFile);
      //Log.log( Log.DEBUG, this, "File List Size: " + files.size() );
      fireNodeRemoved(getPathToRoot(), fileIndex, aFile);
   }

   public void buildFileSelected(ProjectEvent evt) {
      // no-op  
   }

   /** Returns <code>true</code> if the given file should be added to the
    * model.  This method by default returns <code>true</code>, so subclasses
    * should override this method to filter out any files.
    *
    * @param  aFile  Description of Parameter
    * @return        Description of the Returned Value
    */
   protected boolean accept(ProjectFile aFile) {
      return true;
   }

   /** Load the given project's files. */
   protected void load() {
      loadFiles(project.getRoot(), files);
      Collections.sort(files, ProjectFile.getComparator());
      //Log.log(Log.DEBUG,this,"ProjectFileFlatTreeModel::load() files.size()="+files.size());
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

   /** Load all files from the given directory (and subdirectory) to <code>files</code>.
    *
    * @param  dir    Description of Parameter
    * @param  files  Description of Parameter
    */
   private void loadFiles(ProjectDirectory dir, List files) {
      //Log.log(Log.DEBUG,this,"ProjectFileFlatTreeModel::loadFiles() : "+System.currentTimeMillis());
      for (Iterator i = dir.subdirectories(); i.hasNext(); ) {
         loadFiles((ProjectDirectory) i.next(), files);
      }
      ProjectFile each;
      for (Iterator i = dir.files(); i.hasNext(); ) {
         each = (ProjectFile) i.next();
         if (accept(each))
            files.add(each);
      }
   }

}

