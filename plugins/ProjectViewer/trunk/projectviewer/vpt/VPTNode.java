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
import java.util.Comparator;
import java.util.Collections;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
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
public abstract class VPTNode extends DefaultMutableTreeNode {

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

	protected String		name;

	//}}}

	//{{{ #VPTNode(String) : <init>

	protected VPTNode(String name) {
		this.name = name;
		setAllowsChildren(getClass() != VPTFile.class);
	} //}}}

	//{{{ #VPTNode(String, boolean) : <init>
	protected VPTNode(String name, boolean allowsChildren) {
		this.name = name;
		setAllowsChildren(allowsChildren);
	}

	//}}}

	//{{{ Public methods

	//{{{ +sortChildren() : void
	/**
	 *	Sort the children list for this node using the default node comparator.
	 *	The trees containing the node are not notified of the update.
	 */
	public void sortChildren() {
		if (children != null && children.size() > 1)
			Collections.sort(children, new VPTNodeComparator());
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
	/** Returns true if this node is a file. */
	public final boolean isFile() {
		return (getClass() == VPTFile.class);
	} //}}}

	//{{{ +isDirectory() : boolean
	/** Returns true if this node is a file. */
	public final boolean isDirectory() {
		return (getClass() == VPTDirectory.class);
	} //}}}

	//{{{ +isProject() : boolean
	/** Returns true if this node is a file. */
	public final boolean isProject() {
		return (getClass() == VPTProject.class);
	} //}}}

	//{{{ +isRoot() : boolean
	/** Returns whether this node is a root node. */
	public final boolean isRoot() {
		return (getClass() == VPTRoot.class);
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

	//{{{ +compareToNode(VPTNode) : int
	/**
	 *	This method will only get called by nodes which are not recognized
	 *	by the default Comparator provided by the class VPTNodeComparator.
	 *	It's purpose is to be implemented by other types of node unknown
	 *	to the default node hierarchy, so that they can be sorted within
	 *	the tree. Since this is not going to be called on the classes
	 *	provided by the plugin, the return value does not matter. For other
	 *	implementing classes, the return value should be as the normal
	 *	"compareTo(Object)" method returns.
	 */
	public int compareToNode(VPTNode node) {
		return 1;
	}
	//}}}

	//{{{ +findIndexForChild(VPTNode) : int
	/**
	 *	Do a binary search with the goal of finding in what index of the child
	 *	array of this node the given child would be inserted to maintain order
	 *	according to the {@link VPTNodeComparator VPTNodeComparator} rules.
	 *
	 *	@param	child	The child to be inserted.
	 *	@return	The index where to put the child as to maintain the child array
	 *			in ascendant order.
	 */
	public int findIndexForChild(VPTNode child) {
		if (children == null || children.size() == 0) return 0;

		VPTNodeComparator c = new VPTNodeComparator();
		int b = 0, e = children.size(), i = e/2;
		VPTNode n;

		while (e - b > 1) {
			n = (VPTNode) children.get(i);
			int comp = c.compare(child,n);

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
		return (c.compare(child,n) < 0 ? b : b + 1);
	} //}}}

	//{{{ +setParent(MutableTreeNode) : void
	/**
	 *	Sets the parent for the node. If the node is an openable node (e.g.,
	 *	a file), it registers itself in the parent project, or unregisters
	 *	itself from the project in case the parent is being set to null.
	 */
	public void setParent(MutableTreeNode newParent) {
		super.setParent(newParent);
		if (newParent == null) {
			if (canOpen()) {
				VPTProject p = findProjectFor(this);
				if (p != null) {
					p.unregisterNodePath(this);
				}
			}
		} else {
			if (canOpen()) {
				VPTProject p = findProjectFor(this);
				if (p != null) {
					p.registerNodePath(this);
				}
			}
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

	//}}}

	//{{{ +class _VPTNodeComparator_
	/**
 	 *	Compares two VPTNode objects. It makes assumptions about the base nodes
	 *	provided by the plugin. If the nodes are not recognized by any of the
	 *	"isSomething" methods, the {@link VPTNode#compareToNode(VPTNode)
	 *	compareToNode(VPTNode)}	method is called.
	 */
	public static class VPTNodeComparator implements Comparator {

		//{{{ +compare(Object, Object) : int
		public int compare(Object o1, Object o2) {
			if (o1 == o2) return 0;

			VPTNode node1 = (VPTNode) o1;
			VPTNode node2 = (VPTNode) o2;

			if (node1.isFile()) {
				if(node2.isFile()) {
					return node1.getName().compareTo(node2.getName());
				} else {
					return 1;
				}
			} else if (node1.isDirectory()) {
				if (node2.isFile()) {
					return -1;
				} else if (node2.isDirectory()) {
					return node1.getName().compareTo(node2.getName());
				} else {
					return 1;
				}
			} else if (node1.isProject()) {
				if (node2.isProject()) {
					return node1.getName().compareTo(node2.getName());
				} else if (node2.isFile() || node2.isDirectory()) {
					return -1;
				} else {
					return 1;
				}
			} else if (node1.isRoot()){
				return -1;
			} else {
				return node1.compareToNode(node2);
			}
		} //}}}

	} //}}}

}

