/* $Id$
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

/** Listens to the project JTree and responds to file selections.
 *
 * @author     <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 */
public final class ProjectTreeSelectionListener implements TreeSelectionListener, MouseListener, ChangeListener, TreeModelListener, Runnable {

	private ProjectViewer viewer;
	private Launcher launcher;
	private JTree currentTree;
	private TreePath selectionPath;

	private int lastClickButton;
	private long lastClickTime;
	private Object lastClickTarget;

	private boolean reportsClickCountCorrectly;


	/** Create a new <code>ProjectTreeSelectionListener
	 *
	 * @param  aViewer    Description of Parameter
	 * @param  aLauncher  Description of Parameter
	 */
	public ProjectTreeSelectionListener(ProjectViewer aViewer, Launcher aLauncher) {
		viewer = aViewer;
		launcher = aLauncher;
		lastClickTime = 0L;
		reportsClickCountCorrectly = false;
	}

	// MouseListener interfaces

	/** Determines when the user clicks on the JTree.
	 *
	 * @param  evt  Description of Parameter
	 */
	public void mouseClicked(MouseEvent evt) {
		if(isDoubleClick(evt) && isFileClicked(evt)) {
			ProjectFile file = viewer.getSelectedFile();

			if(file.isOpened()) {
				if(isCurrentBuffer(file))
					launcher.closeFile(file);
				else
					launcher.showFile(file);

			}
			else {
				launcher.launchFile(file);
			}
		}
	}

	public void mousePressed(MouseEvent evt) { }

	public void mouseReleased(MouseEvent evt) { }

	public void mouseEntered(MouseEvent evt) { }

	public void mouseExited(MouseEvent evt) { }

	// ChangeListener interfaces (JTabbedPane)

	/** Listen to tab changes.
	 *
	 * @param  evt  Description of Parameter
	 */
	public void stateChanged(ChangeEvent evt) {
		checkState();
		if(currentTree != null)
			getCurrentModel().removeTreeModelListener(this);
		currentTree = viewer.getCurrentTree();
		getCurrentModel().addTreeModelListener(this);
	}

	// TreeSelectionListener interfaces

	/** Receive notification that the tree selection has changed.
	 *
	 * @param  e  Description of Parameter
	 */
	public void valueChanged(TreeSelectionEvent e) {
		lastClickTarget = null;
		checkState();
	}

	// TreeModelListener interfaces

	/** Invoked after a node (or a set of siblings) has changed in some way.
	 *
	 * @param  e  Description of Parameter
	 */
	public void treeNodesChanged(TreeModelEvent e) {
		//Log.log( Log.DEBUG, this, "Tree Node Changed" );
		handleTreeModelEvent(e);
	}

	/** Invoked after nodes have been inserted into the tree.
	 *
	 * @param  e  Description of Parameter
	 */
	public void treeNodesInserted(TreeModelEvent e) {
		handleTreeModelEvent(e);
	}

	/** Invoked after nodes have been removed from the tree.
	 *
	 * @param  e  Description of Parameter
	 */
	public void treeNodesRemoved(TreeModelEvent e) { }

	/** Invoked after the tree has drastically changed structure from a
	 * given node down.
	 *
	 * @param  e  Description of Parameter
	 */
	public void treeStructureChanged(TreeModelEvent e) { }

	/** Call on the current tree to select the given node. */
	public void run() {
		currentTree.setSelectionPath(selectionPath);
	}

	/** Because IBM's JDK doesn't support <code>getClickCount()</code> for <code>JTree</code>
	 * properly, we have to do this.
	 *
	 * @param  evt  Description of Parameter
	 * @return      The doubleClick value
	 */
	private boolean isDoubleClick(MouseEvent evt) {
		if(reportsClickCountCorrectly)
			return evt.getClickCount() == 2;
		if(evt.getClickCount() == 2) {
			reportsClickCountCorrectly = true;
			return true;
		}

		TreePath path = viewer.getCurrentTree().getPathForLocation(evt.getX(), evt.getY());
		if(path == null) {
			lastClickTarget = null;
			return false;
		}

		Object target = path.getLastPathComponent();

		if(target == lastClickTarget &&
				target == viewer.getSelectedNode() &&
				lastClickButton == evt.getModifiers() &&
				(System.currentTimeMillis() - lastClickTime < 500L)) {
			lastClickTarget = null;
			return true;
		}

		lastClickButton = evt.getModifiers();
		lastClickTarget = target;
		lastClickTime = System.currentTimeMillis();
		return false;
	}

	/** Returns <code>true</code> if a node is selected and the given
	 * mouse event points to the specified node.
	 *
	 * @param  evt  Description of Parameter
	 * @return      The fileClicked value
	 */
	private boolean isFileClicked(MouseEvent evt) {
		if(!viewer.isFileSelected())
			return false;

		Object selectedNode = viewer.getSelectedNode();

		TreePath path = viewer.getCurrentTree().getPathForLocation(evt.getX(), evt.getY());
		if(path == null)
			return false;
		Object clickedNode = path.getLastPathComponent();

		return selectedNode.equals(clickedNode);
	}

	/** Returns the node pointed to by the given path and index.
	 *
	 * @param  path   Description of Parameter
	 * @param  index  Description of Parameter
	 * @return        The child value
	 */
	private Object getChild(TreePath path, int index) {
		return getCurrentModel().getChild(path.getLastPathComponent(), index);
	}

	/** Returns the <code>TreeModel</code> of the current tree.
	 *
	 * @return    The currentModel value
	 */
	private TreeModel getCurrentModel() {
		return currentTree.getModel();
	}

	/** Returns <code>true</code> if the given file is the current buffer.
	 *
	 * @param  aFile  Description of Parameter
	 * @return        The currentBuffer value
	 */
	private boolean isCurrentBuffer(ProjectFile aFile) {
		return aFile.pathEquals(viewer.getView().getBuffer().getPath());
	}

	/** Handle the given <code>TreeModelEvent</code>.  This method will
	 * find the first added/changed node and select it.
	 *
	 * @param  evt  Description of Parameter
	 */
	private void handleTreeModelEvent(TreeModelEvent evt) {
		Object node = getChild(evt.getTreePath(), evt.getChildIndices()[0]);
		if(!(node instanceof ProjectFile))
			return;
		selectionPath = buildPathFrom(evt, node);
		SwingUtilities.invokeLater(this);
	}

	/** Build a <code>TreePath</code> from the given <code>TreeModelEvent</code>
	 * and a child.
	 *
	 * @param  evt    Description of Parameter
	 * @param  child  Description of Parameter
	 * @return        Description of the Returned Value
	 */
	private TreePath buildPathFrom(TreeModelEvent evt, Object child) {
		return evt.getTreePath().pathByAddingChild(child);
	}

	/** Check the current node, setting the button/status states as
	 * necessary.
	 */
	private void checkState() {
		Object node = viewer.getSelectedNode();
		if(node == null)
			return;

		viewer.enableButtonsForNode(node);

		if(node instanceof ProjectFile)
			viewer.setStatus(((ProjectFile)node).getPath());

		else if(node instanceof Project)
			viewer.setStatus(node + " [" + ((Project)node).getRoot().getPath() + "]");

		else if(node instanceof ProjectDirectory)
			viewer.setStatus(((ProjectDirectory)node).getPath());

		else
			viewer.setStatus("");
	}

}

