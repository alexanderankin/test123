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

import java.util.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.gjt.sp.util.Log;
import projectviewer.*;


/**
 * A tree model that represents all files in all projects without any relationship
 * to the folders they are a child of. 
 */
class AllProjectsTreeModel
  implements TreeModel, BranchOwner
{
  
  private final static String TITLE = ProjectViewer.ALL_PROJECTS;
  private final static String NO_PROJECTS = "No projects found...";
  
  private List prjModels;
  private List listeners;
  
  /**
   * Create a new <code>AllProjectsTreeModel</code>.
   */
  public AllProjectsTreeModel() {
    prjModels = new ArrayList();
    listeners = new ArrayList();
  }
  
  /**
   * Add a model.
   */
  protected void addModel( ProjectTreeModel model ) {
    prjModels.add( model );
    model.setBranchOwner( this );
  }
  
  /**
   * Returns the child of <code>parent</code> at index.
   */    
  public Object getChild(Object parent, int index) {
    if ( isRoot( parent ) ) return getProject( index );
    return findProjectModelFor( parent ).getChild( parent, index );
  }
  
  /**
   * Returns the number of children of <code>parent</code>.
   */
  public int getChildCount(Object parent) {
    //Log.log( Log.DEBUG, this, "Getting child count for: " + parent );
    if ( isRoot( parent ) ) return prjModels.size();
    return findProjectModelFor( parent ).getChildCount( parent );
  }

  /**
   * Returns the index of child in parent.
   */  
  public int getIndexOfChild(Object parent, Object child) {
    if ( isRoot( parent ) ) return getIndexOfProject( child );
    return findProjectModelFor( parent ).getIndexOfChild( parent, child );
  }
  
  /**
   * Returns the root of the tree.
   */
  public Object getRoot() {
    if ( ProjectManager.getInstance().getProjectCount() == 0 ) 
      return NO_PROJECTS;
    return TITLE;
  }
  
  /**
   * Returns true if node is a leaf.
   */
  public boolean isLeaf(Object node) {
    return node instanceof ProjectFile;
  }

  /**
   * Adds a listener for the events posted after the tree changes.
   */
  public void addTreeModelListener(TreeModelListener l) {
    listeners.add( l );
    for ( Iterator i = prjModels.iterator(); i.hasNext(); )
      ( (TreeModel) i.next() ).addTreeModelListener( l );
  }

  /**
   * Removes a listener previously added with {@link #addTreeModelListener(TreeModelListener)}.
   */  
  public void removeTreeModelListener(TreeModelListener l) {
    listeners.remove( l );
    for ( Iterator i = prjModels.iterator(); i.hasNext(); )
      ( (TreeModel) i.next() ).removeTreeModelListener( l );
  }
  
  /**
   * Messaged when the user has altered the value for the item identified by
   * <code>path</code> to <code>newValue</code>.
   *
   * <p><i>Note:</i> Since this tree is immutable, this method does nothing.
   */
  public void valueForPathChanged(TreePath path, Object newValue) {
  }
  
  /**
   * Returns the path to the specified branch.
   */
  public Object[] getPathTo( Branch branch ) {
    return new Object[] { getRoot() }; 
  }
  
  /**
   * Finds the {@link ProjectTreeModel} that is managing this node.
   */
  protected ProjectTreeModel findProjectModelFor( Object node ) {
    if ( node instanceof Project )
      return findProjectModelFor( (Project) node );
      
    for ( Iterator i = prjModels.iterator(); i.hasNext(); ) {
      ProjectTreeModel each = (ProjectTreeModel) i.next();
      if ( each.getProject().isProjectArtifact( node ) )
        return each;
    }
    return null;
  }
  
  /**
   * Finds the {@link ProjectTreeModel} that is managing this node.
   */
  protected ProjectTreeModel findProjectModelFor( Project prj ) {
    for ( Iterator i = prjModels.iterator(); i.hasNext(); ) {
      ProjectTreeModel each = (ProjectTreeModel) i.next();
      if ( each.getProject().equals( prj ) ) return each;
    }
    return null;
  }
  
  /**
   * Returns the {@link Project} at the specified index.
   */
  protected Project getProject( int index ) {
    return getModel( index ).getProject();
  }
  
  /**
   * Returns the {@link ProjectTreeModel} at the specified index. 
   */
  protected ProjectTreeModel getModel( int index ) {
    return (ProjectTreeModel) prjModels.get( index );
  }

  /**
   * Returns <code>true</code> if the current node is the root.
   */
  protected boolean isRoot( Object node ) {
    return getRoot().equals( node );
  }
  
  /**
   * Find the index of the given project.
   */
  private int getIndexOfProject( Object project ) {
    for ( int i=0; i<prjModels.size(); i++ ) {
      if ( getProject( i ).equals( project ) )
        return i;
    }
    return -1;
  }
  
}
