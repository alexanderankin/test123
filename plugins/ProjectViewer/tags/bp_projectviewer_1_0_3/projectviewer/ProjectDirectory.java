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
import javax.swing.tree.TreePath;

import org.gjt.sp.util.Log;


/**
 * A project directory.
 */
public class ProjectDirectory {

  private String fullPath;
  private List subdirectories;
  private List files;
  private String name;
  private String pathWithSep;

  private static Comparator comparator;
  
  /**
   * Create a new <code>ProjectDirectory</code>.
   */
  public ProjectDirectory( File aDirectory ) {
    this( aDirectory.getAbsolutePath() );
  }
  
  /**
   * Create a new <code>ProjectDirectory</code>.
   */
  public ProjectDirectory( String fullDirectoryPath ) {
    fullPath = fullDirectoryPath;
    subdirectories = new ArrayList();
    files = new ArrayList();
  }
  
  /**
   * Returns the path of this directory.
   */
  public String getPath() {
    return fullPath;
  }
  
  /**
   * Returns the number of files in the directory.  This method
   * differs from {@link #getChildCount()} in that the other method
   * returns the number of project files <b>and</b> directories.
   */
  public int getFileCount() {
    return files.size();
  }
  
  /**
   * Returns the {@link ProjectFile} at the specified index.
   */
  public ProjectFile getFile( int index ) {
    return (ProjectFile) files.get( index );
  }
  
  /**
   * Returns the named file.
   */
  public ProjectFile getFile( String name ) {
    for ( Iterator i = files(); i.hasNext(); ) {
      ProjectFile each = (ProjectFile) i.next();
      if ( each.getName().equals( name ) )
        return each;
    }
    return null;
  }
  
  /**
   * Returns this project directory's subdirectories.
   */
  public Iterator subdirectories() {
    return subdirectories.iterator();
  }
  
  /**
   * Returns the named subdirectory.
   */
  public ProjectDirectory getSubDirectory( String name ) {
    for ( Iterator i = subdirectories(); i.hasNext(); ) {
      ProjectDirectory each = (ProjectDirectory) i.next();
      if ( each.getName().equals( name ) )
        return each;
    }
    return null;    
  }
  
  /**
   * Returns a <code>java.io.File</code> representation of this directory.
   */
  public File toFile() {
    return new File( getPath() );
  }
  
  /**
   * Returns this project directory's files.
   */
  public Iterator files() {
    return files.iterator();
  }
  
  /**
   * Returns the name of the directory.
   */
  public String getName() {
    if ( name == null )
      name = getPath().substring( getPath().lastIndexOf( File.separatorChar ) + 1 );
    return name;
  }
  
  /**
   * Returns list of directories need to reach the given project file. 
   */
  public List getPathToFile( ProjectFile aFile ) {
    return getProjectPath( aFile.toFile() );
  }
  
  /**
   * Returns list of directories need to reach the given project file. 
   */
  public List getPathToDirectory( ProjectDirectory aDirectory ) {
    return getProjectPath( aDirectory.toFile() );
  }
  
  /**
   * Returns <code>true</code> if the given file is under this directory
   * or its subdirectories.
   */
  public boolean isDescendant( ProjectFile aFile ) {
    if ( pathWithSep == null )
      pathWithSep = getPath() + File.separator;
    return aFile.getPath().startsWith( pathWithSep );
  }
  
  /**
   * Returns list of directories needed to reach the given project artifact. 
   */
  List getProjectPath( File anArtifact ) {
    List list = getPathToFile( anArtifact );
    list.set( 0, this );
    ProjectDirectory dir = this;
    for ( int i=1; i<list.size(); i++ ) {
      dir = dir.getSubDirectory( (File) list.get(i) );
      list.set( i, dir ); 
    }
    return list;
  }
  
  /**
   * Returns the indexed child, whether it be a sub directory of file. 
   */
  public Object getChild( int index ) {
    if ( index < subdirectories.size() )
      return subdirectories.get( index );
    else {
      return files.get( index - subdirectories.size() );
    }
  }
  
  /**
   * Returns the number of children under this directory.
   */
  public int getChildCount() {
    int count = files.size() + subdirectories.size();
    return count;
  }
  
  /**
   * Returns the index of the specified child.
   */
  public int getIndexOfChild( Object child ) {
    if ( child instanceof ProjectFile )
      return subdirectories.size() + files.indexOf( child );
    
    if ( child instanceof ProjectDirectory )
      return subdirectories.indexOf( child );
      
    return -1;
  }

  /**
   * Returns a comparator for project directories.
   */
  public static Comparator getComparator() {
    if ( comparator == null ) {
      comparator = new DirectoryComparator();
    }
    return comparator;
  }

  /**
   * Add a file to this directory.
   */
  void addFile( ProjectFile file ) {
    files.add( file );
    Collections.sort( files, ProjectFile.getComparator() );
  }
  
  /**
   * Remove the specified file.
   */
  void removeFile( ProjectFile file ) {
    files.remove( file );
  }
  
  /**
   * Remove a project directory.
   */
  void removeDirectory( ProjectDirectory dir ) {
    subdirectories.remove( dir );
  }
  
  /**
   * Add the specified subdirectory.
   */
  ProjectDirectory addSubDirectory( File aSubDirectory ) {
    if ( isSubDirectory( aSubDirectory ) ) return null;
    ProjectDirectory dir = new ProjectDirectory( aSubDirectory );
    subdirectories.add( dir );
    Collections.sort( subdirectories, getComparator() );
    return dir;
  }
  
  /**
   * Returns the subdirectory identified by the given file.
   */
  ProjectDirectory getSubDirectory( File aSubDirectory ) {
    return getSubDirectory( aSubDirectory.getName() );
  }
  
  /**
   * Returns <code>true</code> if the given directory already exists as
   * a project subdirectory.
   */
  boolean isSubDirectory( File aSubDirectory ) {
    return getSubDirectory( aSubDirectory ) != null;
  }
  
  /**
   * Returns the path to the specified child, or <code>null</code> if the
   * given file is not a descendent.
   */
  TreePath getTreePath( File child ) {
    List path = getPathToFile( child );
    if ( path == null ) return null;
    return new TreePath( path.toArray() );
  }
  
  /**
   * Returns list of directories need to reach the given file.
   *
   * <p>Note: The path returns is a list <code>java.io.File</code> objects.
   * use {@link getPathToFile(ProjectFile)} instead if you want a list of
   * <code>ProjectDirectory</code>s.
   * </p>
   */
  List getPathToFile( File aFile ) {
    List path = new ArrayList();
    
    if ( equalsFile( aFile ) ) {
      path.add( aFile );
      return path;
    }
    
    File dir = aFile.getParentFile();
    while ( dir != null && !equalsFile( dir ) ) {
      path.add( 0, dir );
      dir = dir.getParentFile();
    }
    
    if ( dir == null ) return null;
    path.add( 0, dir );
    return path;
  }
  
  /**
   * Returns <code>true</code> if the given <code>java.io.File</code> is
   * equivalent to this project directory.
   */
  boolean equalsFile( File aDir ) {
    return fullPath.equals( aDir.getAbsolutePath() );
  }
  
  /**
   * Returns the directory name.
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
    if ( ! (obj instanceof ProjectDirectory) ) return false;
    return getPath().equals( ((ProjectDirectory) obj).getPath() );
  }
  
  /**
   * Returns the hash code.
   */
  public int hashCode() {
    return getPath().hashCode();
  }

  /**
   * A class for comparing directories.
   */
  private static class DirectoryComparator implements Comparator {

    /**
     * Compare two directory objects.
     */
    public int compare( Object obj1, Object obj2 ) {
      ProjectDirectory dir1 = (ProjectDirectory) obj1;
      ProjectDirectory dir2 = (ProjectDirectory) obj2;
      return dir1.getName().compareTo(dir2.getName());
    }

  }

  
}

