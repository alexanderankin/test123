/*
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
package projectviewer.event;

import java.util.*;
import projectviewer.*;


/**
 * A project event.
 */
public class ProjectEvent
  extends EventObject
{
  
  private Object artifact;
  private int index;
  
  /**
   * Create a new <code>ProjectEvent</code>.
   */
  public ProjectEvent( Project project ) {
    super( project );
  }
  
  /**
   * Create a new <code>ProjectEvent</code>.
   */
  public ProjectEvent( Project project, Object artifact ) {
    this( project, artifact, -1 );
  }
  
  /**
   * Create a new <code>ProjectEvent</code>.
   */
  public ProjectEvent( Project project, Object anArtifact, int anIndex ) {
    super( project );
    artifact = anArtifact;
    index = anIndex;
  }
  
  /**
   * Returns the artifact as a {@link ProjectDirectory}.
   */
  public ProjectDirectory getProjectDirectory() {
    return (ProjectDirectory) artifact;
  }
  
  /**
   * Returns the project file.
   */
  public ProjectFile getProjectFile() {
    return (ProjectFile) artifact;
  }
  
  /**
   * Returns the {@link Project}.
   */
  public Project getProject() {
    return (Project) getSource();
  }
  
  /**
   * Returns the project artifact.
   */
  public Object getArtifact() {
    return artifact;
  }
  
  /**
   * Returns the index of the project child involved.  This index is relative
   * to the file's parent directory.
   */
  public int getIndex() {
    return index;
  }
  
  /**
   * Returns the path to the child involved.
   */
  public List getPath() {
    if ( artifact instanceof ProjectFile )
      return getProject().getRoot().getPathToFile( getProjectFile() );
    if ( artifact instanceof ProjectDirectory )
      return getProject().getRoot().getPathToDirectory( getProjectDirectory() );
    return null;
  }
  
}
