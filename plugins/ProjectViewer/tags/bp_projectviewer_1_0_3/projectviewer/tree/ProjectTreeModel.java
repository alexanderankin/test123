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
 * A super class tree modeling project artifacts.
 */
abstract class ProjectTreeModel
  implements TreeModel, Branch
{
  
  protected Project project;
  private List listeners;
  private BranchOwner owner;
  
  /**
   * Create a new <code>ProjectFilesFlatTreeModel</code>.
   */
  public ProjectTreeModel( Project aProject ) {
    listeners = new ArrayList();
    project = aProject;
  }
  
  /**
   * Set the branch owner.
   */
  public void setBranchOwner( BranchOwner anOwner ) {
    owner = anOwner;
  }
  
  /**
   * Returns the project this tree is modeling.
   */
  public Project getProject() {
    return project;
  }
    
  /**
   * Adds a listener for the events posted after the tree changes.
   */
  public void addTreeModelListener(TreeModelListener l) {
    listeners.add( l );
  }

  /**
   * Removes a listener previously added with {@link #addTreeModelListener(TreeModelListener)}.
   */  
  public void removeTreeModelListener(TreeModelListener l) {
    listeners.remove( l );
  }
  
  /**
   * Messaged when the user has altered the value for the item identified by
   * <code>path</code> to <code>newValue</code>.
   *
   * <p><i>Note:</i> Since this tree is immutable, this method does nothing.
   */
  public void valueForPathChanged(TreePath path, Object newValue) {}
  
  /**
   * Returns true if node is a leaf.
   */
  public boolean isLeaf(Object node) {
    return node instanceof ProjectFile;
  }

  /**
   * Returns the root of the tree.
   */
  public Object getRoot() {
    return project;
  }
  
  /**
   * Convert the given list to a <code>TreePath</code>, prepending 
   * branch path is required.
   */
  protected TreePath toTreePath( List path ) {
    if ( owner != null ) {
      Object[] root = owner.getPathTo(this);
      for ( int i=root.length-1; i>=0; i-- )
        path.add( 0, root[i] );
    }
    return new TreePath( path.toArray() );
  }
  
  /**
   * Fired an event notifying listeners that the tree structure has changed.
   */
  protected void fireStructureChanged() {
    TreeModelEvent evt = new TreeModelEvent( this, new TreePath( getRoot() ) );
    for ( int i=0; i<listeners.size(); i++ )
      ( (TreeModelListener) listeners.get(i) ).treeStructureChanged( evt );
  }
  
  /**
   * Fired an event notifying listeners that a node was inserted.
   */
  protected void fireNodeInserted( TreePath path, Object node ) {
    //Log.log( Log.DEBUG, this, "Node Inserted: path(" + path + ") node(" + node + ")" ); 
    int idx = getIndexOfChild( path.getLastPathComponent(), node );
    TreeModelEvent evt = new TreeModelEvent( this, path, toArray( idx ), toArray( node ) );
    for ( int i=0; i<listeners.size(); i++ )
      ( (TreeModelListener) listeners.get(i) ).treeNodesInserted( evt );
  }
  
  /**
   * Fired an event notifying listeners that a node was removed.
   */
  protected void fireNodeRemoved( TreePath path, int index, Object node ) {
    //Log.log( Log.DEBUG, this, "Node Removed: path(" + path + ") node(" + node + ")" );
    TreeModelEvent evt = new TreeModelEvent( this, path, toArray( index ), toArray( node ) );
    for ( int i=0; i<listeners.size(); i++ )
      ( (TreeModelListener) listeners.get(i) ).treeNodesRemoved( evt );
  }
  
  /**
   * Fire node changed.
   */
  protected void fireNodeChanged( TreePath path, int index ) {
    TreeModelEvent evt = new TreeModelEvent( this, path, toArray( index ), null );
    for ( int i=0; i<listeners.size(); i++ )
      ( (TreeModelListener) listeners.get(i) ).treeNodesChanged( evt );
  }
  
  /**
   * Convert the given <code>Object</code> to an <code>Object</code> array.
   */
  private static Object[] toArray( Object obj ) {
    Object[] arr = new Object[1];
    arr[0] = obj;
    return arr;
  }
  
  /**
   * Convert the given <code>int</code> to an <code>int</code> array.
   */
  private static int[] toArray( int index ) {
    int[] arr = new int[1];
    arr[0] = index;
    return arr;
  }
  
}
