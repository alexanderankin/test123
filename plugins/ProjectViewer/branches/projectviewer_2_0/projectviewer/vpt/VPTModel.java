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
import java.io.File;
import java.util.Enumeration;
//}}}

/**
 *	Tree model for the Virtual Project Tree. Based on the <code>TreeModel</code>
 *	class by Shad Stafford.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTModel extends javax.swing.tree.DefaultTreeModel {
	
	//{{{ Constructors 
	public VPTModel(VPTNode rootNode) {
		super(rootNode);
	}
	
	public VPTModel(VPTNode rootNode, boolean askAllowsChildren) {
		super(rootNode, askAllowsChildren);
	}
	//}}}

	//{{{ getNodeForFile(File) method 
	/**
	 * Recurses through the tree until it finds a node that
	 * matches the file. Returns null if a matching node is
	 * not found.
	 */
	public VPTNode getNodeForFile(File file)  {
		return findNodeForFile(((VPTNode)getRoot()).children(), file);
	} //}}}	

	//{{{ findNodeForFile(Enumaeration, File) method 
	/** Helper method for getNodeForFile(). */
	private VPTNode findNodeForFile(Enumeration nodes, File file) {
		VPTNode node = null;
		while (nodes.hasMoreElements())  {
			node = (VPTNode) nodes.nextElement();
			if (node.isFile())  {
				if (((VPTFile)node).getFile().equals(file)) {
					return node;
				}
				node = null;
			} else {
				// not a file so must be project or directory, recurse into its children
				node = findNodeForFile(node.children() , file);
				if (node != null) {
					return node;
				}
			}
		}
		return node;
	} //}}}

}

