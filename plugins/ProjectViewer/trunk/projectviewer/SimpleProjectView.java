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

import javax.swing.tree.TreeModel;

import projectviewer.tree.*;


/**
 * A project view for one project.
 */
public class SimpleProjectView
  implements ProjectView
{
  
  private Project project;
  
  /**
   * Create a new <code>SimpleProjectView</code>.
   */
  public SimpleProjectView( Project aProject ) {
    setProject( aProject );
  }
  
  /**
   * Returns the current project.
   */
  public Project getCurrentProject() {
    return project;
  }
  
  /**
   * Set the project.
   */
  void setProject( Project aProject ) {
    project = aProject;
  }
  
  /**
   * Activate the view.
   */
  public void activate() {
    project.activate();
  }
  
  /**
   * Deactivate the view.
   */
  public void deactivate() {
    project.deactivate();
  }
  
  /**
   * Returns the tree model for the folder view.
   */
  public TreeModel getFolderViewModel() {
    return new ProjectFileTreeModel( project );
  }
  
  /**
   * Returns the tree model for the file view. 
   */
  public TreeModel getFileViewModel() {
    return new ProjectFileFlatTreeModel( project );
  }
  
  /**
   * Returns the tree model for the working files view.
   */
  public TreeModel getWorkingFileViewModel() {
    return new ProjectOpenFileFlatTreeModel( project );
  }
  
}
