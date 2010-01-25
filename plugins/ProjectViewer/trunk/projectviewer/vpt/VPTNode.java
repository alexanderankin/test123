/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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
package projectviewer.vpt;

//{{{ Imports
import java.util.Collections;

import java.awt.Color;
import java.awt.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Node implementation for the Virtual Project Tree. Keeps track of children
 *	and provides basic functionality for the nodes.
 *
 *	<p>Based on the <code>TreeNode</code> class from the VirtualProjectTree
 *	plugin by Shad Stafford.</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public abstract class VPTNode extends DefaultMutableTreeNode
								implements Comparable<VPTNode>
{

	//{{{ Constants

	protected final  static Color treeSelectionForeground 	= UIManager.getColor("Tree.selectionForeground");
	protected final  static Color treeNoSelectionForeground = UIManager.getColor("Tree.textForeground");
	protected final  static Color treeSelectionBackground 	= UIManager.getColor("Tree.selectionBackground");
	protected final  static Color treeNoSelectionBackground = UIManager.getColor("Tree.textBackground");

	//}}}

	//{{{ Static members

	//{{{ +_findProjectFor(VPTNode)_ : VPTProject
	/**
	 *	Returns the project associated with the node, or null if the node
	 *	is the root of the project tree.
	 *
	 *	This is not thread-safe since we don't know which project
	 *	to lock while doing the search, but oh well...
	 */
	public static VPTProject findProjectFor(VPTNode node) {
		if (node.isRoot()) {
			return null;
		} else {
			while (node != null && !node.isProject()) {
				node = (VPTNode) node.getParent();
			}
			return (VPTProject) node;
		}
	} //}}}

	//}}}

	//{{{ Attributes

	protected String	name;

	//}}}

	//{{{ #VPTNode(String, boolean) : <init>
	protected VPTNode(String name, boolean allowsChildren) {
		this.name = name;
		setAllowsChildren(allowsChildren);
	}

	//}}}

	//{{{ Public methods

	//{{{ Thread-safe versions of DefaultMutableTreeNode methods.

	public void insert(MutableTreeNode newChild,
					   int childIndex)
	{
		lock(true);
		try {
			super.insert(newChild, childIndex);
		} finally {
			unlock(true);
		}
	}


	public void remove(int childIndex)
	{
		lock(true);
		try {
			super.remove(childIndex);
		} finally {
			unlock(true);
		}
	}

	public void remove(MutableTreeNode aChild)
	{
		lock(true);
		try {
			super.remove(aChild);
		} finally {
			unlock(true);
		}
	}

	public void removeAllChildren()
	{
		lock(true);
		try {
			super.removeAllChildren();
		} finally {
			unlock(true);
		}
	}

	public void removeFromParent()
	{
		VPTProject parent = findProjectFor(this);
		if (parent != null) {
			parent.getLock().writeLock().lock();
		}
		try {
			super.removeFromParent();
		} finally {
			parent.getLock().writeLock().unlock();
		}
	}

	//}}}

	//{{{ +sortChildren() : void
	/**
	 *	Sort the children list for this node using the default node comparator.
	 *	The trees containing the node are not notified of the update.
	 */
	public void sortChildren() {
		sortChildren(false);
	} //}}}

	//{{{ +sortChildren(boolean) : void
	/**
	 *	Sort the children list for this node using the default node comparator.
	 *	The trees containing the node are not notified of the update.
	 *
	 *	@since PV 2.1.3.4
	 */
	public void sortChildren(boolean recurse) {
		lock(true);
		try {
			if (children != null) {
				if (children.size() > 1)
					Collections.sort(children);
				if (recurse)
					for (int i = 0; i < children.size(); i++)
						((VPTNode)children.get(i)).sortChildren(true);
			}
		} finally {
			unlock(true);
		}
	} //}}}

	//{{{ +delete() : boolean
	/**
	 *	The "delete()" method should remove the resource from the the disk,
	 *	if applicable. This method does not call remove().
	 *
	 *	@return		Whether the deletion was successful or not.
	 */
	public boolean delete() {
		return false;
	} //}}}

	//{{{ +isFile() : boolean
	/** Returns true if this node is a VPTFile. */
	public final boolean isFile() {
		return (getClass() == VPTFile.class);
	} //}}}

	//{{{ +isDirectory() : boolean
	/** Returns true if this node is a VPTDirectory. */
	public final boolean isDirectory() {
		return (getClass() ==  VPTDirectory.class);
	} //}}}

	//{{{ +isProject() : boolean
	/** Returns true if this node is a VPTProject. */
	public final boolean isProject() {
		return (getClass() == VPTProject.class);
	} //}}}

	//{{{ +isGroup() : boolean
	/** Whether this instance if a VPTGroup or any subclass of it. */
	public boolean isGroup() {
		return (this instanceof VPTGroup);
	} //}}}

	//{{{ +isRoot() : boolean
	/** Returns whether this node is the root node. */
	public final boolean isRoot() {
		return (this == VPTRoot.getInstance());
	} //}}}

	//{{{ +isOpened() : boolean
	/**
	 *	Tells if the resource is currently opened in jEdit. This only makes
	 *	sense for files, so the default just returns "false" and is
	 *	overridden in the file implementation.
	 */
	public boolean isOpened() {
		return false;
	} //}}}

	//{{{ +getName() : String
	/**
	 *	Returns the name of this node. The name is the text that will appear
	 *	in the project tree.
	 */
	public String getName() {
		return name;
	} //}}}

	//{{{ +setName(String) : void
	/**	Changes the name of the node. */
	public void setName(String name) {
		this.name = name;
	} //}}}

	//{{{ +canWrite() : boolean
	/**
	 *	Returns whether the underlying resource can be written to. It makes
	 *	more sense for files and directories, for example, to check if it is
	 *	possible to delete them.
	 */
	public boolean canWrite() {
		return false;
	} //}}}


	/**
	 *	Returns whether the underlying resource can be renamed.
	 *
	 *	@return Default implementation returns true.
	 *	@since PV 3.0.0
	 */
	public boolean canRename()
	{
		return true;
	}


	//{{{ +toString() : String
	/** Returns a string representation of the current node. */
	public String toString() {
		return "VPTNode [" + getName() + "]";
	} //}}}

	//{{{ +canOpen() : boolean
	/**
	 *	This method should return whether it makes sense to "open" the node.
	 *	For example, for file nodes, it should be reasonable to open the file
	 *	in a jEdit buffer, so this method should return "true" and implement
	 *	{@link #open() open()} and {@link #close() close()} to execute
	 *	the opening and closing operations.
	 */
	public boolean canOpen() {
		return false;
	} //}}}

	//{{{ +open() : void
	/**
	 *	"Opens" the node. The default implementation does nothing. If a node can
	 *	be opened, it should implement the opening action in this method.
	 */
	public void open() {

	} //}}}

	//{{{ +close() : void
	/**
	 *	"Closes" the node. This should "undo" what was done by
	 *	{@link #open() open()}, normally.
	 */
	public void close() {

	} //}}}

	//{{{ +*getNodePath()* : String
	/**
	 *	Returns a String representing a "path" for this node. This can be any
	 *	arbitrary String, but the idea is to have the string represent some kind
	 *	of URL or file path. This makes more sense for nodes that deal with
	 *	files and directories, or even URL links.
	 */
	public abstract String getNodePath(); //}}}

	//{{{ +compareTo(VPTNode) : int
	/**
	 *	This method is used to sort the nodes in the trees. The rough hierarchy
	 *	is Root -> Groups -> Projects -> "allows children" -> leaves, so try
	 *	to keep that consistent.
	 *
	 *	<p>IT'S VERY IMPORTANT TO IMPLEMENT THIS METHOD CORRECTLY. Especially
	 *	for nodes that allow children nodes, since VPTDirectory expects
	 *	nodes of these kinds to take care of the comparison themselves.
	 *	There's danger of infinite recursion if you don't take this into
	 *	account.</p>
	 *
	 *	@since PV 3.0.0
	 */
	public int compareTo(VPTNode node)
	{
		return 1;
	} //}}}

	//{{{ +findIndexForChild(VPTNode) : int
	/**
	 *	Do a binary search with the goal of finding in what index of the child
	 *	array of this node the given child would be inserted to maintain order
	 *	according to the comparison rules defined by the compareToNode() methods.
	 *
	 *	@param	child	The child to be inserted.
	 *	@return	The index where to put the child as to maintain the child array
	 *			in ascendant order.
	 */
	public int findIndexForChild(VPTNode child) {
		lock(false);
		try {
			if (children == null || children.size() == 0) return 0;

			int b = 0, e = children.size(), i = e/2;
			VPTNode n;

			while (e - b > 1) {
				n = (VPTNode) children.get(i);
				int comp = child.compareTo(n);

				if (comp < 0) {
					e = i;
				} else if (comp == 0) {
					i++;
					b = e = i;
				} else {
					b = i;
				}
				i = (e+b)/2;
			}
			if (b == children.size()) return b;
			n = (VPTNode) children.get(b);
			return (child.compareTo(n) < 0 ? b : b + 1);
		} finally {
			unlock(false);
		}
	} //}}}

	//{{{ +setParent(MutableTreeNode) : void
	/**
	 *	Sets the parent for the node. If the node is an openable node (e.g.,
	 *	a file), it registers itself in the parent project, or unregisters
	 *	itself from the project in case the parent is being set to null.
	 */
	public void setParent(MutableTreeNode newParent) {
		VPTProject currp = findProjectFor(this);
		VPTProject newp = (newParent != null)
			? findProjectFor((VPTNode)newParent) : null;

		if (currp != null) {
			currp.getLock().writeLock().lock();
		}
		if (newp != null) {
			newp.getLock().writeLock().lock();
		}

		try {
			if (canOpen()) {
				if (newParent == null) {
					if (currp != null) {
						currp.unregisterNodePath(this);
					}
				} else {
					if (newp != null) {
						newp.registerNodePath(this);
					}
				}
			}
			super.setParent(newParent);
		} finally {
			if (currp != null) {
				currp.getLock().writeLock().unlock();
			}
			if (newp != null) {
				newp.getLock().writeLock().unlock();
			}
		}
	} //}}}

	//{{{ +persistChildren() : boolean
	/**
	 *	This method should return whether the children of this node should
	 *	be persisted when the node is saved to the project config file. The
	 *	default is "true".
	 *
	 *	<p>Nodes that provide run time children (for example, allowing for the
	 *	exploration of the contents of a JAR file) should override this method
	 *	and return "false".</p>
	 *
	 *	<p>This only makes sense for nodes that allow children in the first
	 *	place.</p>
	 *
	 *	@since PV 2.1.0
	 */
	public boolean persistChildren() {
		return true;
	} //}}}

	//{{{ +getChildWithName(String) : VPTNode
	/**
	 *	Returns the child directly under this node that has the given
	 *	name. This doesn't look into the children's children.
	 *
	 *	@since	PV 2.1.0
	 */
	public VPTNode getChildWithName(String name) {
		lock(false);
		try {
			if (getAllowsChildren()) {
				for (int i = 0; i < getChildCount(); i++) {
					if (((VPTNode)getChildAt(i)).getName().equals(name))
						return (VPTNode) getChildAt(i);
				}
			}
			return null;
		} finally {
			unlock(false);
		}
	} //}}}

	//}}}

	//{{{ GUI stuff

	//{{{ +*getIcon(boolean)* : Icon
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public abstract Icon getIcon(boolean expanded); //}}}

	//{{{ +getForegroundColor(boolean) : Color
	/**
	 *	Returns the node's foreground color.
	 *
	 *	@param	sel		If the node is currently selected.
	 */
	public Color getForegroundColor(boolean sel) {
		return (sel ? treeSelectionForeground : treeNoSelectionForeground);
	} //}}}

	//{{{ +getBackgroundColor(boolean) : Color
	/**
	 *	Returns the node's background color.
	 *
	 *	@param	sel		If the node is currently selected.
	 */
	public Color getBackgroundColor(boolean sel) {
		return (sel ? treeSelectionBackground : treeNoSelectionBackground);
	} //}}}

	//{{{ +getClipType() : int
	/**
	 *	This method controls how the CellRenderer implementation will clip the
	 *	name of the node when it doesn't fit in the tree.
	 *
	 *	@see	VPTCellRenderer
	 *	@return	CLIP_NOCLIP in the default implementation, override to change it.
	 *	@since	PV 2.1.0
	 */
	public int getClipType() {
		return VPTCellRenderer.CLIP_NOCLIP;
	} //}}}


	/**
	 * Called when the tree panel receives a request to notify that
	 * information about the node has changed. This allows the node
	 * to take any needed actions (such as flushing cached information).
	 *
	 * @since PV 3.0.0
	 */
	protected void changed()
	{

	}

	//}}}


	/**
	 * Returns the lock object for the parent project of this node,
	 * or null if there is no parent project.
	 */
	protected ReadWriteLock getLock()
	{
		VPTProject p = findProjectFor(this);
		return (p != null) ? p.getLock() : null;
	}


	/** Convenience method for locking. */
	protected void lock(boolean write)
	{
		ReadWriteLock lock = getLock();
		if (lock != null) {
			Lock l = write ? lock.writeLock() : lock.readLock();
			l.lock();
		}
	}


	/** Convenience method for unlocking. */
	protected void unlock(boolean write)
	{
		ReadWriteLock lock = getLock();
		if (lock != null) {
			Lock l = write ? lock.writeLock() : lock.readLock();
			l.unlock();
		}
	}


	protected int compareName(VPTNode other) {
		if (ProjectViewerConfig.getInstance().getCaseInsensitiveSort()) {
			return getName().compareToIgnoreCase(other.getName());
		} else {
			return getName().compareTo(other.getName());
		}
	}

}

