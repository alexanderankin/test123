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
	
	public static final VPTNodeType ROOT 		= new VPTNodeType("root");
	public static final VPTNodeType PROJECT 	= new VPTNodeType("project");
	public static final VPTNodeType DIRECTORY 	= new VPTNodeType("directory");
	public static final VPTNodeType FILE 		= new VPTNodeType("file");
	
	//}}}
	
	//{{{ Attributes
	
	protected VPTNodeType	nodeType;
	protected VPTNode		vParent;
	protected String		name;
	
	//}}}
	
	//{{{ Constructors 
	
	protected VPTNode(VPTNodeType type, String name) {
		this(type, name, type != FILE);
	}
	
	protected VPTNode(VPTNodeType type, String name, boolean allowsChildren) {
		this.nodeType	= type;
		this.name		= name;
		setAllowsChildren(allowsChildren);
	}
	
	//}}}
	
	//{{{ Public methods
	
	//{{{ add(MutableTreeNode) method
	/**	Keeps the children list sorted. */
	public void add(MutableTreeNode newChild) {
		super.add(newChild);
		Collections.sort(children, new VPTNodeComparator());
	} //}}}
	
	//{{{ delete() method
	/**
	 *	The "delete()" method should remove the resource from the project and
	 *	from the disk, if applicable. The default is to call the "remove" method,
	 *	which simply removes the resource from the project.
	 *
	 *	@return		Whether the deletion was successful or not.
	 */
	public boolean delete() {
		remove();
		return true;
	} //}}}

	//{{{ remove() method
	/**
	 *	The "remove()" method should remove the resource from the project, but
	 *	not from the disk (when applicable).
	 */
	public void remove() {
		super.remove(this);
		setParent(null);
	} //}}}

	//{{{ isFile() method	
	/** Returns true if this node is a file. */
	public boolean isFile() {
		return (nodeType == FILE);
	} //}}}
	
	//{{{ isDirectory() method	
	/** Returns true if this node is a file. */
	public boolean isDirectory() {
		return (nodeType == DIRECTORY);
	} //}}}
	
	//{{{ isProject() method	
	/** Returns true if this node is a file. */
	public boolean isProject() {
		return (nodeType == PROJECT);
	} //}}}
	
	//{{{ isRoot() method
	/** Returns whether this node is a root node. */
	public boolean isRoot() {
		return (nodeType == ROOT);
	} //}}}
	
	//{{{ isOpened() method
	/**
	 *	Tells if the resource is currently opened in jEdit. This only makes 
	 *	sense for files, so the default just returns "false" and is
	 *	overridden in the file implementation.
	 */
	public boolean isOpened() {
		return false;
	} //}}}

	//{{{ setParent(VPTNode) method
	public void setParent(VPTNode parent) {
		super.setParent(parent);
		vParent = parent;
	} //}}}
	
	//{{{ getName() method
	/**
	 *	Returns the name of this node. The name is the text that will appear
	 *	in the project tree.
	 */
	public String getName() {
		return name;
	} //}}}
	
	//{{{ setName(String) method
	/**	Changes the name of the node. */
	public void setName(String name) {
		this.name = name;
	} //}}}
	
	//{{{ canWrite() method
	/**
	 *	Returns whether the underlying resource can be written to. It makes 
	 *	more sense for files and directories, for example, to check if it is
	 *	possible to delete them.
	 */
	public boolean canWrite() {
		return false;
	} //}}}
	
	//{{{ toString() method
	/** Returns a string representation of the current node. */
	public String toString() {
		return "VPTNode [" + getName() + "]";
	} //}}}
	
	//{{{ canOpen() method
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
	
	//{{{ open() method
	/**
	 *	"Opens" the node. The default implementation does nothing. If a node can
	 *	be opened, it should implement the opening action in this method.
	 */
	public void open() {
	
	} //}}}
	
	//{{{ close() method
	/**
	 *	"Closes" the node. This should "undo" what was done by 
	 *	{@link #open() open()}, normally.
	 */
	public void close() {
	
	} //}}}
	
	//{{{ getNodePath() method
	/**
	 *	Returns a String representing a "path" for this node. This can be any
	 *	arbitrary String, but the idea is to have the string represent some kind
	 *	of URL or file path. This makes more sense for nodes that deal with
	 *	files and directories, or even URL links.
	 */
	public abstract String getNodePath(); //}}}
	
	//{{{ compareToNode(VPTNode) method
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

	//}}}

	//{{{ GUI stuff
	
	//{{{ getIcon(boolean) method
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public abstract Icon getIcon(boolean expanded); //}}}
	
	//{{{ getForegroundColor(boolean) method
	/**
	 *	Returns the node's foreground color.
	 *
	 *	@param	sel		If the node is currently selected.
	 */
	public Color getForegroundColor(boolean sel) {
		return (sel ? treeSelectionForeground : treeNoSelectionForeground);
	} //}}}

	//{{{ getBackgroundColor(boolean) method
	/**
	 *	Returns the node's background color.
	 *
	 *	@param	sel		If the node is currently selected.
	 */
	public Color getBackgroundColor(boolean sel) {
		return (sel ? treeSelectionBackground : treeNoSelectionBackground);
	} //}}}
	
	//}}}
	
	//{{{ VPTNodeType class
	/**
	 *	Class to provide a type-safe enumeration for node types.
	 */
	public static class VPTNodeType {
		private String name = null;
		
		public VPTNodeType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	} //}}}

	//{{{ VPTNodeComparator class
	/**
 	 *	Compares two VPTNode objects. It makes assumptions about the base nodes
	 *	provided by the plugin. If the nodes are not recognized by any of the
	 *	"isSomething" methods, the {@link VPTNode.compareToNode(VPTNode), 
	 *	compareToNode(VPTNode)}	method is called.
	 */
	protected static class VPTNodeComparator implements Comparator {

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
		}
		
	} //}}}
	
}
