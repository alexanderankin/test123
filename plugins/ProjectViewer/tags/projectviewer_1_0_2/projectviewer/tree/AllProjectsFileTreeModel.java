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
package projectviewer.tree;

import projectviewer.*;


/**
 * A tree model that represents all files in all project and their relationship
 * to project folders. 
 */
public class AllProjectsFileTreeModel
  extends AllProjectsTreeModel
{
  
  private ProjectFileTreeModel delegate;
  
  /**
   * Create a new <code>AllProjectsFileTreeModel</code>.
   */
  public AllProjectsFileTreeModel() {
    delegate = new ProjectFileTreeModel( null );
  }
  
  /**
   * Returns the child of <code>parent</code> at index.
   */    
  public Object getChild(Object parent, int index) {
    if ( isRoot( parent ) )
      return ProjectManager.getInstance().getProject( index );
    return delegate.getChild( parent, index );
  }
  
  /**
   * Returns the number of children of <code>parent</code>.
   */
  public int getChildCount(Object parent) {
    if ( isRoot( parent ) )
      return ProjectManager.getInstance().getProjectCount();
    return delegate.getChildCount( parent );
  }

  /**
   * Returns the index of child in parent.
   */  
  public int getIndexOfChild(Object parent, Object child) {
    if ( isRoot( parent ) )
      return ProjectManager.getInstance().getIndexOfProject( (Project) child );
    return delegate.getIndexOfChild( parent, child );
  }
  
}
