/* $Id$
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

/** A super class tree modeling project artifacts. */
abstract class ProjectTreeModel implements TreeModel, Branch {

	protected Project project;
	private List listeners;
	private BranchOwner owner;

	/** Create a new <code>ProjectFilesFlatTreeModel</code>.
	 *
	 * @param  aProject  Description of Parameter
	 */
	public ProjectTreeModel(Project aProject) {
		listeners = new ArrayList();
		project = aProject;
	}

	/** Convert the given <code>Object</code> to an <code>Object</code> array.
	 *
	 * @param  obj  Description of Parameter
	 * @return      Description of the Returned Value
	 */
	private static Object[] toArray(Object obj) {
		Object[] arr = new Object[1];
		arr[0] = obj;
		return arr;
	}

	/** Convert the given <code>int</code> to an <code>int</code> array.
	 *
	 * @param  index  Description of Parameter
	 * @return        Description of the Returned Value
	 */
	private static int[] toArray(int index) {
		int[] arr = new int[1];
		arr[0] = index;
		return arr;
	}

	/** Set the branch owner.
	 *
	 * @param  anOwner  The new branchOwner value
	 */
	public void setBranchOwner(BranchOwner anOwner) {
		owner = anOwner;
	}

	/** Returns the project this tree is modeling.
	 *
	 * @return    The project value
	 */
	public Project getProject() {
		return project;
	}

	/** Returns true if node is a leaf.
	 *
	 * @param  node  Description of Parameter
	 * @return       The leaf value
	 */
	public boolean isLeaf(Object node) {
		return node instanceof ProjectFile;
	}

	/** Returns the root of the tree.
	 *
	 * @return    The root value
	 */
	public Object getRoot() {
		return project;
	}

	/** Adds a listener for the events posted after the tree changes.
	 *
	 * @param  l  The feature to be added to the TreeModelListener attribute
	 */
	public void addTreeModelListener(TreeModelListener listener) {
		//Log.log( Log.DEBUG, this, "addTreeModelListener -> "+listener.toString() );
		listeners.add(listener);
	}

	/** Removes a listener previously added with {@link #addTreeModelListener(TreeModelListener)}.
	 *
	 * @param  l  Description of Parameter
	 */
	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}

	/** Messaged when the user has altered the value for the item identified by
	 * <code>path</code> to <code>newValue</code>.
	 *
	 * <p><i>Note:</i> Since this tree is immutable, this method does nothing.
	 *
	 * @param  path      Description of Parameter
	 * @param  newValue  Description of Parameter
	 */
	public void valueForPathChanged(TreePath path, Object newValue) { }

	/** Convert the given list to a <code>TreePath</code>, prepending
	 * branch path is required.
	 *
	 * @param  path  Description of Parameter
	 * @return       Description of the Returned Value
	 */
	protected TreePath toTreePath(List path) {
		if(owner != null) {
			Object[] root = owner.getPathTo(this);
			for(int i = root.length - 1; i >= 0; i--)
				path.add(0, root[i]);
		}
		return new TreePath(path.toArray());
	}

	/** Fired an event notifying listeners that the tree structure has changed.
	 */
	protected void fireStructureChanged() {
		TreeModelEvent evt = new TreeModelEvent(this, new TreePath(getRoot()));
		for(int i = 0; i < listeners.size(); i++)
			((TreeModelListener)listeners.get(i)).treeStructureChanged(evt);
	}

	/** Fired an event notifying listeners that a node was inserted.
	 *
	 * @param  path  Description of Parameter
	 * @param  node  Description of Parameter
	 */
	protected void fireNodeInserted(TreePath path, Object node) {
		//Log.log( Log.DEBUG, this, "Node Inserted: path(" + path + ") node(" + node + ")" );
		int idx = getIndexOfChild(path.getLastPathComponent(), node);
		TreeModelEvent evt = new TreeModelEvent(this, path, toArray(idx), toArray(node));
		for(int i = 0; i < listeners.size(); i++)
			((TreeModelListener)listeners.get(i)).treeNodesInserted(evt);
	}

	/** Fired an event notifying listeners that a node was removed.
	 *
	 * @param  path   Description of Parameter
	 * @param  index  Description of Parameter
	 * @param  node   Description of Parameter
	 */
	protected void fireNodeRemoved(TreePath path, int index, Object node) {
		//Log.log( Log.DEBUG, this, "Node Removed: path(" + path + ") node(" + node + ")" );
		TreeModelEvent evt = new TreeModelEvent(this, path, toArray(index), toArray(node));
		for(int i = 0; i < listeners.size(); i++)
			((TreeModelListener)listeners.get(i)).treeNodesRemoved(evt);
	}

	/** Fire node changed.
	 *
	 * @param  path   Description of Parameter
	 * @param  index  Description of Parameter
	 */
	protected void fireNodeChanged(TreePath path, int index) {
		//Log.log( Log.DEBUG, this, "fireNodeChanged("+path.toString()+"), listeners.size()="+listeners.size() );
		TreeModelEvent evt = new TreeModelEvent(this, path, toArray(index), null);
		for(int i = 0; i < listeners.size(); i++) {
			//Log.log( Log.DEBUG, this, "  "+i+" "+((TreeModelListener)listeners.get(i)).toString());
			((TreeModelListener)listeners.get(i)).treeNodesChanged(evt);
		}
	}

}

