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
import org.gjt.sp.jedit.*;


/**
 * A project file.
 */
public class ProjectFile
  implements Transferable
{
  
  private final static int KEY_UNSET = -1;
  
  private static Comparator comparator;
  
  private String fullPath;
  private String name;
  private boolean opened;
  private int key;
  
  /**
   * Create a new <code>ProjectFile</code>.
   */
  public ProjectFile( String fullFilePath ) {
    this( KEY_UNSET, fullFilePath );
  }
  
  /**
   * Create a new <code>ProjectFile</code>.
   *
   * <p>Note: This is a constructor intended to be used internally.</p>
   */
  ProjectFile( int aKey, String fullFilePath ) {
    key = aKey;
    fullPath = fullFilePath;
  }
  
  /**
   * Returns <code>true</code> if project file exists.
   */
  public boolean exists() {
    return toFile().exists();
  }
  
  /**
   * Returns underlying <code>java.io.File</code>. 
   */
  public File toFile() {
    return new File( fullPath );
  }
  
  /**
   * Returns <code>true</code> if this file is opened.
   */
  public boolean isOpened() {
    return isInBuffer();
  }
  
  /**
   * Returns the path identifying this file.
   */
  public String getPath() {
    return fullPath;
  }
  
  /**
   * Returns the name of the file, with the path structure.
   */
  public String getName() {
    if ( name == null )
      name = getPath().substring( getPath().lastIndexOf( File.separatorChar ) + 1 );
    return name;
  }
  
  /**
   * Returns the buffer for the given file, or <code>null</code> if
   * this file isn't currently in a buffer.
   */
  public Buffer getBuffer() {
    return jEdit.getBuffer( getPath() );
  }
  
  /**
   * Returns <code>true</code> if this file is currently in a JEdit buffer.
   */
  public boolean isInBuffer() {
    return getBuffer() != null;
  }
  
  /**
   * Returns <code>true</code> if the given path equals the path of
   * this file.
   */
  public boolean pathEquals( String aPath ) {
    return getPath().equals( aPath );
  }
  
  /**
   * Returns the name of the file.
   */
  public String toString() {
    return getName();
  }
  
  /**
   * Returns <code>true</code> if the specified this object
   * equals <code>obj</code>.
   */
  public boolean equals( Object obj ) {
    if ( super.equals( obj ) ) return true;
    if ( !( obj instanceof ProjectFile ) ) return false;
    return pathEquals( ((ProjectFile) obj).getPath() );
  }
  
  /**
   * Returns the hash code.
   */
  public int hashCode() {
    return getPath().hashCode();
  }
  
  /**
   * Returns a comparator for project files.
   */
  public static Comparator getComparator() {
    if ( comparator == null )
      comparator = new FileComparator();
    return comparator;
  }
  
  /**
   * Returns an array of DataFlavor objects indicating the flavors the data 
   * can be provided in.
   */
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] { DataFlavor.javaFileListFlavor };
  }
  
  /**
   * Returns whether or not the specified data flavor is supported for
   * this object.
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals( DataFlavor.javaFileListFlavor );
  }
  
  /**
   * Returns an object which represents the data to be transferred. 
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
   * Returns <code>true</code> if the project's key is unset.
   */
  boolean isKeyUnset() {
    return key == KEY_UNSET;
  }
  
  /**
   * Set the project's key.
   */
  void setKey( int aKey ) {
    key = aKey;
  }
  
  /**
   * Returns the key.
   */
  int getKey() {
    return key;
  }
  
  /**
   * Performs a simple cast.
   */
  private static ProjectFile toProjectFile( Object obj ) {
    return (ProjectFile) obj;
  }
  
  /**
   * A compare class.
   */
  private static class FileComparator
    implements Comparator
  {
    
    /**
     * Compare the two objects.
     */
    public int compare( Object obj1, Object obj2 ) {
      return toProjectFile( obj1 ).getName().compareTo( toProjectFile( obj2 ).getName() );
    }
    
  }

}

