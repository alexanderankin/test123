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
package projectviewer.tree;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.gjt.sp.util.Log;
import projectviewer.*;


/**
 * Listens to the project JTree and responds to file selections.
 *
 * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
 * @version $Revision$
 */
public class ProjectTreeSelectionListener
  implements TreeSelectionListener, MouseListener, ChangeListener,
             TreeModelListener, Runnable
{

  private ProjectViewer viewer;
  private Launcher launcher;
  private JTree currentTree;
  private TreePath selectionPath;

  private int lastClickButton;
  private long lastClickTime;
  private Object lastClickTarget;

  
  /**
   * Create a new <code>ProjectTreeSelectionListener    
    */
  public ProjectTreeSelectionListener(ProjectViewer aViewer, Launcher aLauncher) {
    viewer = aViewer;
    launcher = aLauncher;
    lastClickTime = 0L;
  }
    
  // MouseListener interfaces
    
  /**
   * Determines when the user clicks on the JTree.
   */
  public void mouseClicked(MouseEvent evt) {
    if( isDoubleClick(evt) && isFileClicked( evt ) ) {
      ProjectFile file = viewer.getSelectedFile();
      
      if ( file.isOpened() )  {
        if ( isCurrentBuffer( file ) )
           launcher.closeFile( file );
        else
           launcher.showFile( file );
                
      } else {
        launcher.launchFile( file );
      }
    }
  }

  /**
   * Because IBM's JDK doesn't support <code>getClickCount()</code> for <code>JTree</code>
   * properly, we have to do this.
   */
  private boolean isDoubleClick(MouseEvent evt) {
    if (evt.getClickCount() == 2) return true;

    Object target = viewer.getCurrentTree()
      .getPathForLocation(evt.getX(), evt.getY()).getLastPathComponent();

    if (target == lastClickTarget &&
        target == viewer.getSelectedNode() &&
        lastClickButton == evt.getModifiers() &&
        (System.currentTimeMillis() - lastClickTime < 500L))
    {
      lastClickTarget = null;
      return true;
    }
    lastClickButton = evt.getModifiers();
    lastClickTarget = target;
    lastClickTime = System.currentTimeMillis();
    return false;
  }

  public void mousePressed(MouseEvent evt)  { }
  public void mouseReleased(MouseEvent evt) { }
  public void mouseEntered(MouseEvent evt)  { }
  public void mouseExited(MouseEvent evt)   { }

  // ChangeListener interfaces (JTabbedPane)
  
  /**
   * Listen to tab changes.
   */
  public void stateChanged( ChangeEvent evt ) {
    checkState();
    if ( currentTree != null ) getCurrentModel().removeTreeModelListener( this );
    currentTree = viewer.getCurrentTree();
    getCurrentModel().addTreeModelListener( this );
  }

  // TreeSelectionListener interfaces
  
  /**
   * Receive notification that the tree selection has changed.
   */
  public void valueChanged(TreeSelectionEvent e) {
    lastClickTarget = null;
    checkState();
  }
  
  // TreeModelListener interfaces
  
  /**
   * Invoked after a node (or a set of siblings) has changed in some way.
   */
  public void treeNodesChanged(TreeModelEvent e) {
    //Log.log( Log.DEBUG, this, "Tree Node Changed" );
    handleTreeModelEvent( e );
  }
 
  /**
   * Invoked after nodes have been inserted into the tree.
   */
  public void treeNodesInserted(TreeModelEvent e) {
    handleTreeModelEvent( e );
  }
  
  /**
   * Invoked after nodes have been removed from the tree.
   */
  public void treeNodesRemoved(TreeModelEvent e) { }
          
  /**
   * Invoked after the tree has drastically changed structure from a
   * given node down.
   */
  public void treeStructureChanged(TreeModelEvent e) { }
  
  /**
   * Call on the current tree to select the given node.
   */
  public void run() {
    currentTree.setSelectionPath( selectionPath );
  }
  
  /**
   * Returns <code>true</code> if a node is selected and the given
   * mouse event points to the specified node.
   */
  private boolean isFileClicked( MouseEvent evt ) {
    if ( !viewer.isFileSelected() ) return false;
    
    Object selectedNode = viewer.getSelectedNode();
    Object clickedNode  = viewer.getCurrentTree()
      .getPathForLocation( evt.getX(), evt.getY() ).getLastPathComponent();
      
    return selectedNode.equals( clickedNode ); 
  }
  
  /**
   * Handle the given <code>TreeModelEvent</code>.  This method will
   * find the first added/changed node and select it.
   */
  private void handleTreeModelEvent( TreeModelEvent evt ) {
    Object node = getChild( evt.getTreePath(), evt.getChildIndices()[0] );
    if ( !( node instanceof ProjectFile ) ) return;
    selectionPath = buildPathFrom( evt, node );
    SwingUtilities.invokeLater( this );
  }
  
  /**
   * Returns the node pointed to by the given path and index.
   */
  private Object getChild( TreePath path, int index ) {
    return getCurrentModel().getChild( path.getLastPathComponent(), index );
  }
  
  /**
   * Build a <code>TreePath</code> from the given <code>TreeModelEvent</code>
   * and a child.
   */
  private TreePath buildPathFrom( TreeModelEvent evt, Object child ) {
    return evt.getTreePath().pathByAddingChild( child );
  }
  
  /**
   * Returns the <code>TreeModel</code> of the current tree.
   */
  private TreeModel getCurrentModel() {
    return currentTree.getModel();
  }
          
  /**
   * Check the current node, setting the button/status states as
   * necessary.
   */
  private void checkState() {
    Object node = viewer.getSelectedNode();
    if (node == null) return;
      
    viewer.enableButtonsForNode( node );
    
    if ( node instanceof ProjectFile )
      viewer.setStatus( ( (ProjectFile) node ).getPath() );
            
    else if ( node instanceof Project)
      viewer.setStatus( node + " [" + ( (Project) node ).getRoot().getPath() + "]" );
            
    else if ( node instanceof ProjectDirectory)
      viewer.setStatus( ( (ProjectDirectory) node ).getPath() );
        
    else
      viewer.setStatus( "" );
  }
  
  /**
   * Returns <code>true</code> if the given file is the current buffer.
   */
  private boolean isCurrentBuffer( ProjectFile aFile ) {
    return aFile.pathEquals( viewer.getView().getBuffer().getPath() );
  }
  
}
