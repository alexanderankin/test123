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
public abstract class VPTNode extends DefaultMutableTreeNode implements Comparable {
	
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
		this.nodeType	= type;
		this.name		= name;
		setAllowsChildren(nodeType != FILE);
	}
	
	//}}}
	
	//{{{ Public methods
	
	//{{{ add(MutableTreeNode) method
	/**
	 *	Overrides the add() method of the superclass, registering VPTFiles added
	 *	to projects (for performance reasons).
	 */
	public void add(MutableTreeNode newChild) {
		super.add(newChild);
		
		if (((VPTNode)newChild).isFile()) {
			VPTNode proj = (VPTNode) newChild.getParent();
			while (!proj.isProject()) {
				proj = (VPTNode) proj.getParent();
			}
			((VPTProject)proj).registerFile((VPTFile)newChild);
		}
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
	
	//{{{ compareTo(Object) method
	public int compareTo(Object o) {
		if (o == this) return 0;
		if (o instanceof VPTNode) {
			VPTNode node = (VPTNode) o;
			if (this.isFile()) {
				if(node.isFile()) {
					return getName().compareTo(node.getName());
				} else {
					return 1; 
				}
			} else if (this.isDirectory()) {
				if (node.isFile()) {
					return -1;
				} else if (node.isDirectory()) {
					return getName().compareTo(node.getName());
				} else {
					return 1;
				}
			} else if (this.isProject()) {
				if (node.isProject()) {
					return getName().compareTo(node.getName());
				} else if (node.isFile() || node.isDirectory()) {
					return -1;
				} else {
					return 1;
				}
			} else {
				// root node
				return -1;
			}
		} else {
			return getName().compareTo(o.toString());
		}
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
	
	//{{{ Inner classes
	
	//{{{ VPTNodeType class
	/**
	 *	Class to provide a type-safe enumeration for node types.
	 */
	public static class VPTNodeType {
		private String name = null;
		
		private VPTNodeType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	} //}}}
	
	//}}}

}
