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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

import projectviewer.event.*;


/**
 * A project.
 */
public class Project
  implements EBComponent
{

  protected final static int PROJECT_KEY_UNSET = -1;
  
  private ProjectDirectory root;
  private int key;
  private String name;
  private Map files;
  private List listeners;
  
  /**
   * Create a new <code>Project</code>.
   */
  public Project( String name, ProjectDirectory root ) {
    this( name, root, PROJECT_KEY_UNSET );
  }
  
  /**
   * Create a new <code>Project</code>.
   *
   * <p>Note: This is a constructor intended to be used internally.</p>
   */
  Project( String aName, ProjectDirectory aRoot, int aKey ) {
    name = aName;
    root = aRoot;
    key = aKey;
    files = new HashMap();
    listeners = new ArrayList();
  }
  
  /**
   * Returns the project root.
   */
  public ProjectDirectory getRoot() {
    return root;
  }
  
  /**
   * Returns the name of the project.
   */
  public String getName() {
    return name;
  }
   
  /**
   * Import the specified files.
   */
  public void importFiles( List files ) {
    for ( Iterator i = files.iterator(); i.hasNext(); )
      importFile( (ProjectFile) i.next() );
  }
  
  /**
   * Import this given file.
   */
  public synchronized void importFile( ProjectFile aFile ) {
    if ( isProjectFile( aFile ) ) return;
    
    if ( aFile.isKeyUnset() ) {
      aFile.setKey( files.size() + 1 );
    }
    ProjectDirectory dir = findDirectory( aFile );
    dir.addFile( aFile );
    fireFileAdded( aFile );
    files.put( aFile.getPath(), aFile );
  }
  
  /**
   * Returns <code>true</code> if the specified file identifies a file
   * thata is in this project.
   */
  public boolean isProjectFile( ProjectFile aFile ) {
    return files.containsKey( aFile.getPath() );
  }
  
  /**
   * Returns <code>true</code> if the specified path identifies a file
   * in this project.
   */
  public boolean isProjectFile( String aFilePath ) {
    return files.containsKey( aFilePath );
  }
  
  /**
   * Remove the specified project file.
   */
  public void removeFile( ProjectFile aFile ) {
    files.remove( aFile.getPath() );
    
    List path = getRoot().getPathToFile( aFile );
    ProjectDirectory dir = (ProjectDirectory) path.get( path.size() - 1 );
    int fileIndex = dir.getIndexOfChild( aFile );
    dir.removeFile( aFile );
    fireFileRemoved( aFile, fileIndex );
    pruneDirectories( path );
  }
  
  /**
   * Remove all project files.
   */
  public void removeFiles() {
    while ( getRoot().getChildCount() != 0 ) {
      Object child = getRoot().getChild( 0 );
      if ( child instanceof ProjectFile ) {
        ProjectFile each = (ProjectFile) child;
        int childIndex = getRoot().getIndexOfChild( each );
        getRoot().removeFile( each );
        fireFileRemoved( each, childIndex );
      } else {
        ProjectDirectory each = (ProjectDirectory) child;
        int childIndex = getRoot().getIndexOfChild( each );
        getRoot().removeDirectory( each );
        fireDirectoryRemoved( each, childIndex );        
      }
    }
  }
  
  /**
   * Returns an iteration of project files.
   */
  public Iterator projectFiles() {
    return files.values().iterator();
  }
  
  /**
   * Returns the name of the project.
   */
  public String toString() {
    return getName();
  }
  
  /**
   * Add a project listener.
   */
  public void addProjectListener( ProjectListener listener ) {
    listeners.add( listener );
  }
  
  /**
   * Remove a project listener.
   */
  public void removeProjectListener( ProjectListener listener ) {
    listeners.remove( listener );
  }
  
  /**
   * Handle any buffer updates or closes and notify the Project Viewer instance
   * that is running.
   */
  public void handleMessage( EBMessage message ) {
    if ( !(message instanceof BufferUpdate) ) return;
      
    BufferUpdate update = (BufferUpdate) message;
    
    if ( update.getWhat().equals(BufferUpdate.LOADED) ) {
      ProjectFile file = getFile( update.getBuffer().getPath() );
      if ( file != null ) fireFileOpened( file );
    }
    
    if (update.getWhat().equals(BufferUpdate.CLOSED)) {
      ProjectFile file = getFile( update.getBuffer().getPath() );
      if ( file != null ) fireFileClosed( file );
    }
  }
  
  /**
   * Returns <code>true</code> if the given path denotes a file that falls
   * under this project's root directory and is not already a project file.
   */
  public boolean canAddInProject( String path ) {
    return !isProjectFile( path ) && path.startsWith( getRoot().getPath() );
  }

  /**
   * Returns <code>true</code> if the given object is a project artifact ( file or directory ).
   */
  public boolean isProjectArtifact( Object obj ) {
    if ( obj instanceof ProjectFile && isProjectFile( ((ProjectFile) obj).getPath() ) )
      return true;
    if ( obj instanceof ProjectDirectory )
      return getRoot().getPathToFile( ( (ProjectDirectory) obj ).toFile() ) != null;
    return false;
  }
  
  /**
   * Returns <code>true</code> if the project's key is unset.
   */
  boolean isKeyUnset() {
    return key == PROJECT_KEY_UNSET;
  }
  
  /**
   * Set the project's key.
   */
  void setKey( int aKey ) {
    key = aKey;
  }
  
  /**
   * Returns the project key.
   */
  int getKey() {
    return key;
  }
  
  /**
   * Activate this project.  This method will cause the project to dispatch
   * project events based on jEdit application events.
   */
  void activate() {
    EditBus.addToBus( this );
  }
  
  /**
   * Deactivate this proejct.  This method will cause the project to stop
   * dispatching project events.
   */
  void deactivate() {
    EditBus.removeFromBus( this );
  }
  
  /**
   * Prune the directories in the path.
   */
  private void pruneDirectories( List path ) {
    for ( int i = path.size() - 1; i > 0; i-- ) {
      ProjectDirectory each = (ProjectDirectory) path.get( i );
      if ( each.getChildCount() != 0 ) return;
      
      ProjectDirectory parent = (ProjectDirectory) path.get( i - 1 );
      int dirIndex = parent.getIndexOfChild( each );
      parent.removeDirectory( each );
      fireDirectoryRemoved( each, dirIndex );
    }
  }
  
  /**
   * Fire notification that a project file has been opened.
   */
  private void fireFileOpened( ProjectFile aFile ) {
    ProjectEvent evt = new ProjectEvent( this, aFile );
    for ( int i=0; i<listeners.size(); i++ )
      ( (ProjectListener) listeners.get(i) ).fileOpened( evt );
  }
  
  /**
   * Fire notification that a project file has been closed.
   */
  private void fireFileClosed( ProjectFile aFile ) {
    ProjectEvent evt = new ProjectEvent( this, aFile );
    for ( int i=0; i<listeners.size(); i++ )
      ( (ProjectListener) listeners.get(i) ).fileClosed( evt );
  }
  
  /**
   * Fire notification that a project file has been removed.
   */
  private void fireFileRemoved( ProjectFile aFile, int index ) {
    ProjectEvent evt = new ProjectEvent( this, aFile, index );
    for ( int i=0; i<listeners.size(); i++ )
      ( (ProjectListener) listeners.get(i) ).fileRemoved( evt );
  }
  
  /**
   * Fire notification that a project file has been added.
   */
  private void fireFileAdded( ProjectFile aFile ) {
    ProjectEvent evt = new ProjectEvent( this, aFile );
    //Log.log( Log.DEBUG, this, "Firing file added: file(" + aFile + ")" );
    for ( int i=0; i<listeners.size(); i++ )
      ( (ProjectListener) listeners.get(i) ).fileAdded( evt );
  }
  
  /**
   * Fire notification that a project directory has been added.
   */
  private void fireDirectoryAdded( ProjectDirectory aDirectory ) {
    ProjectEvent evt = new ProjectEvent( this, aDirectory );
    for ( int i=0; i<listeners.size(); i++ )
      ( (ProjectListener) listeners.get(i) ).directoryAdded( evt );
  }
  
  /**
   * Fire notification that a project directory has been removed.
   */
  private void fireDirectoryRemoved( ProjectDirectory aDirectory, int index ) {
    ProjectEvent evt = new ProjectEvent( this, aDirectory, index );
    for ( int i=0; i<listeners.size(); i++ )
      ( (ProjectListener) listeners.get(i) ).directoryRemoved( evt );
  }
  
  /**
   * Returns a project file identified by the specified path.
   */
  private ProjectFile getFile( String aFilePath ) {
    return (ProjectFile) files.get( aFilePath );
  }
  
  /**
   * Find the given file's subdirectory.
   *
   * @return <code>aFile</code>'s sub project directory, or <code>null</code> if
   * the project file does not exist in a subdirectory of the project's root.
   */
  private ProjectDirectory findDirectory( ProjectFile aFile ) {
    List path = getRoot().getPathToFile( aFile.toFile() );
    if ( path == null ) return null;

    ProjectDirectory dir = getRoot();
    for ( int i=1; i<path.size(); i++ ) {
      File each = (File) path.get(i);
      if ( each.isFile() ) break;
      if ( dir.isSubDirectory( each ) ) {
        dir = dir.getSubDirectory( each );
      } else {
        dir = dir.addSubDirectory( each );
        fireDirectoryAdded( dir );
      }
    }
    return dir;
  }
    
}
