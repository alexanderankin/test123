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

import java.awt.datatransfer.*;
import java.io.File;
import java.util.*;
import javax.swing.tree.TreeNode;
import org.gjt.sp.jedit.*;


/**
 * A project file.
 */
public class ProjectFile
implements ProjectArtifact, Comparable, Transferable
{

   private String name;
   private TreeNode parent;
   private String path;


   /**
    * Create a new <code>ProjectFile</code>.
    */
   public ProjectFile(String aName, TreeNode aParent, String aPath)
   {
      name = aName;
      parent = aParent;
      path = aPath;
   }

   /**
    * Returns the name of the file.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Returns the path of this file.  This path is the phyiscal location of
    * the file in local filesystem, and is not necessarily the same as this
    * file's location within its {@link ProjectDirectory} structure.
    */
   public String getPath()
   {
      return path;
   }

   /**
    * Returns <code>true</code> if the given path equals the path of this file.
    */
   public boolean pathEquals(String otherPath)
   {
      return path.equals(otherPath);
   }

   /**
    * Returns <code>true</code> if the given file equals.
    *
    * <p>SPECIFIED IN: java.lang.Object</p>
    */
   public boolean equals(Object obj)
   {
      if (super.equals(obj))
         return true;
      if (!(obj instanceof ProjectFile))
         return false;
      ProjectFile otherFile = (ProjectFile) obj;
      return name.equals(otherFile.name) && parent.equals(otherFile.parent);
   }

   /**
    * Returns the hash code for this object.
    *
    * <p>SPECIFIED IN: java.lang.Object</p>
    */
   public int hashCode() {
      return name.hashCode() + parent.hashCode();
   }

   /**
    * Returns the {@FileView} this file belongs to.
    */
   public FileView getView()
   {
      return ProjectArtifacts.getView(this);
   }

   /**
    * Returns <code>true</code> if this file is opened in the jEdit editor.
    */
   public boolean isOpened()
   {
      return getBuffer() != null;
   }

   /**
    * Returns the buffer for the given file, or <code>null</code> if
    * this file isn't currently in a buffer.
    */
   public Buffer getBuffer()
   {
      return jEdit.getBuffer( getPath() );
   }

   /**
    * Convert this project file to a <code>java.io.File</code>.
    */
   public File toFile()
   {
      return new File(getPath());
   }

   /**
    * Returns the name of the file.
    */
   public String toString()
   {
      return getName();
   }

   /**
    * Compares the names of the files.
    *
    * <p>SPECIFIED IN: java.lang.Comparable</p>
    */
   public int compareTo(Object obj)
   {
      return getName().compareTo(((ProjectFile) obj).getName());
   }

   /**
    * Returns an array of DataFlavor objects indicating the flavors the data 
    * can be provided in.
    *
    * <p>SPECIFIED IN: java.awt.datatransfer.Transferable</p>
    */
   public DataFlavor[] getTransferDataFlavors()
   {
      return new DataFlavor[] { DataFlavor.javaFileListFlavor };
   }

   /**
    * Returns whether or not the specified data flavor is supported for
    * this object.
    *
    * <p>SPECIFIED IN: java.awt.datatransfer.Transferable</p>
    */
   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      return flavor.equals( DataFlavor.javaFileListFlavor );
   }

   /**
    * Returns an object which represents the data to be transferred.
    *
    * <p>SPECIFIED IN: java.awt.datatransfer.Transferable</p>
    */
   public Object getTransferData( DataFlavor flavor )
   throws UnsupportedFlavorException
   {
      if ( !isDataFlavorSupported( flavor ) )
         throw new UnsupportedFlavorException( flavor );

      List fileList = new ArrayList(1);
      fileList.add( toFile() );
      return fileList;
   }

   /**
    * Returns <code>null</code>.
    */
   public TreeNode getChildAt(int childIndex)
   {
      return null;
   }

   /**
    * Returns <code>0</code>.
    */
   public int getChildCount()
   {
      return 0;
   }

   /**
    * Returns the parent <code>TreeNode</code> of the receiver.
    */
   public TreeNode getParent()
   {
      return parent;
   }

   /**
    * Returns <code>-1</code>.
    */
   public int getIndex(TreeNode node)
   {
      return -1;
   }

   /**
    * Returns <code>true</code>.
    */
   public boolean getAllowsChildren()
   {
      return false;
   }

   /**
    * Returns <code>true</code>.
    */
   public boolean isLeaf()
   {
      return true;
   }

   /**
    * Returns <code>null</code>.
    */
   public Enumeration children()
   {
      return null;
   }

}
