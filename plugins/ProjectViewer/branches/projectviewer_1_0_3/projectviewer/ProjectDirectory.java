/*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package projectviewer;

import java.io.File;
import java.util.*;
import javax.swing.tree.TreeNode;


/**
 * A project directory.
 */
public class ProjectDirectory
   implements ProjectArtifact, Comparable {

   protected String name;
   protected TreeNode parent;
   protected List files;
   protected List dirs;
   protected String path;


   /**
    * Create a new <code>ProjectDirectory</code>.
    */
   public ProjectDirectory(String aName, TreeNode aParent) {
      this(aName, aParent, null);
   }

   /**
    * Create a new <code>ProjectDirectory</code>.
    */
   public ProjectDirectory(String aName, TreeNode aParent, String aPath) {
      name     = aName;
      parent   = aParent;
      path     = aPath;
      files    = new ArrayList();
      dirs     = new ArrayList();
   }

   /**
    * Returns the name of the directory.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name of the view.
    */
   public void setName(String aName)
   {
      name = aName;
   }

   /**
    * Returns an iteration of project files.
    */
   public Iterator files() {
      return files.iterator();
   }

   /**
    * Returns an iteration of project directories.
    */
   public Iterator directories() {
      return dirs.iterator();
   }

   /**
    * Returns the path of this directory.  This path is the phyiscal location of
    * the directory in local filesystem, and is not necessarily the same as this
    * directory's location within its {@link ProjectDirectory} structure.
    */
   public String getPath() {
      return path;
   }

   /**
    * Add a file.
    */
   public ProjectFile addFile(String file, String path) {
      ProjectFile prjFile = new ProjectFile(file, this, path);
      files.add(prjFile);
      Collections.sort(files);
      return prjFile;
   }

   /**
    * Add a file.  This will add a {@link ProjectFile} to this directory, 
    * regardless of where <code>file</code>'s parent directory is.
    */
   public ProjectFile addFile(File file) {
      return addFile(file.getName(), file.getAbsolutePath());
   }

   /**
    * Add a directory.  This will add a {@link ProjectDirectory} to this
    * directory, regardless of where <code>dir</code>'s parent directory
    * is.
    */
   public ProjectDirectory addDirectory(File dir) {
      return addDirectory(dir.getName(), dir.getAbsolutePath());
   }

   /**
    * Add a directory.
    */
   public ProjectDirectory addDirectory(String name) {
      return addDirectory(name, null);
   }

   /**
    * Add a directory.
    */
   public ProjectDirectory addDirectory(String name, String path) {
      ProjectDirectory dir = new ProjectDirectory(name, this, path);
      dirs.add(dir);
      Collections.sort(dirs);
      return dir;
   }

   /**
    * Remove a project file.
    */
   public void removeFile(ProjectFile file) {
      files.remove(file);
   }

   /**
    * Remove a project directory.
    */
   public void removeDirectory(ProjectDirectory dir) {
      dirs.remove(dir);
   }

   /**
    * Returns the indexed project file.
    */
   public ProjectFile getFile(int index) {
      return (ProjectFile) files.get(index);
   }

   /**
    * Returns a file of the specified name.
    */
   public ProjectFile getFile(String fileName) {
      for (Iterator i = files.iterator(); i.hasNext();) {
         ProjectFile each = (ProjectFile) i.next();
         if (each.getName().equals(fileName)) return each;
      }
      return null;
   }

   /**
    * Returns the indexed project directory.
    */
   public ProjectDirectory getDirectory(int index) {
      return (ProjectDirectory) dirs.get(index);
   }

   /**
    * Returns a directory of the given name.
    */
   public ProjectDirectory getDirectory(String name) {
      for (Iterator i = dirs.iterator(); i.hasNext();) {
         ProjectDirectory each = (ProjectDirectory) i.next();
         if (each.getName().equals(name))
            return each;
      }
      return null;
   }

   /**
    * Returns the number project files.
    */
   public int getFileCount() {
      return files.size();
   }

   /**
    * Returns the number project directory.
    */
   public int getDirectoryCount() {
      return dirs.size();
   }

   /**
    * Returns <code>true</code> if the given file already exists in this
    * directory.
    */
   public boolean containsFile(ProjectFile file) {
      return files.contains(file);
   }

   /**
    * Returns <code>true</code> if the given file of name already exists in
    * thid directory.
    */
   public boolean containsFile(String fileName) {
      for (Iterator i = files.iterator(); i.hasNext();) {
         ProjectFile each = (ProjectFile) i.next();
         if (each.getName().equals(fileName))
            return true;
      }
      return false;
   }

   /**
    * Returns <code>true</code> if a directory of the given name exists under
    * this directory.
    */
   public boolean containsDirectory(String dirName) {
      for (Iterator i = files.iterator(); i.hasNext();) {
         ProjectDirectory each = (ProjectDirectory) i.next();
         if (each.getName().equals(dirName))
            return true;
      }
      return false;
   }

   /**
    * Returns the {@FileView} this directory belongs to.
    */
   public FileView getView() {
      return ProjectArtifacts.getView(this);
   }

   /**
    * Returns <code>true</code> if the given directory equals.
    */
   public boolean equals(Object obj) {
      if (super.equals(obj))
         return true;
      if (!(obj instanceof ProjectDirectory))
         return false;
      ProjectDirectory otherFile = (ProjectDirectory) obj;
      if (!name.equals(otherFile.name))
         return false;
      if (parent == null && otherFile.parent == null)
         return true;
      return parent.equals(otherFile.parent);
   }

   /**
    * Returns the name of the directory.
    *
    * <p>SPECIFIED IN: java.lang.Object</p>
    */
   public String toString() {
      return getName();
   }

   /**
    * Compares the names of the directories.
    *
    * <p>SPECIFIED IN: java.lang.Comparable</p>
    */
   public int compareTo(Object obj) {
      return getName().compareTo(((ProjectDirectory) obj).getName());
   }

   /**
    * Returns the index file or directory.
    */
   public TreeNode getChildAt(int childIndex) {
      if (childIndex < dirs.size())
         return getDirectory(childIndex);
      else
         return getFile(childIndex - dirs.size());
   }

   /**
    * Returns the number of files and directories.
    */
   public int getChildCount() {
      return getDirectoryCount() + getFileCount();
   }

   /**
    * Returns the parent <code>TreeNode</code> of the receiver.
    */
   public TreeNode getParent() {
      return parent;
   }

   /**
    * Returns <code>-1</code>.
    */
   public int getIndex(TreeNode node) {
      return (node instanceof ProjectFile) ? files.indexOf(node) : dirs.indexOf(node);
   }

   /**
    * Returns <code>true</code>.
    */
   public boolean getAllowsChildren() {
      return true;
   }

   /**
    * Returns <code>true</code>.
    */
   public boolean isLeaf() {
      return false;
   }

   /**
    * Returns all directories and files.
    */
   public Enumeration children() {
      ArrayList children = new ArrayList();
      children.addAll(dirs);
      children.addAll(files);
      return Collections.enumeration(children);
   }

}
