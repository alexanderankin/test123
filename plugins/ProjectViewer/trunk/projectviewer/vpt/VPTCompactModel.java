/*
* :tabSize=4:indentSize=4:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU General Public License
*  as published by the Free Software Foundation; either version 2
*  of the License, or any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more detaProjectTreeSelectionListenerils.
*
*  You should have received a copy of the GNU General Public License
*  along with this program; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package projectviewer.vpt;

//{{{ Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import bsh.Interpreter;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.MiscUtilities;

import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	A tree model that compress the view by displaying packed directories.
 *
 *	<p>A directory is displayed only if it contains at least one (non-hidden) file.
 *	The relative path is used to display these directories.
 *	</p>
 *	<p>Example:</p>
 *	<table>
 *		<tr><td>-</td><td>MyProject</td></tr>
 *		<tr><td></td><td>-</td><td>src/net/sf/myproject/ui</td></tr>
 *		<tr><td></td><td></td><td></td><td>MyFrame.java</td></tr>
 *		<tr><td></td><td></td><td></td><td>MyDialog.java</td></tr>
 *		<tr><td></td><td>-</td><td>src/net/sf/myproject/actions</td></tr>
 *		<tr><td></td><td></td><td></td><td>MyAction.java</td></tr>
 *		<tr><td></td><td>+</td><td>src/net/sf/myproject/mybigpackage</td></tr>
 *	</table>
 *
 *	@author		francisdobi
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class VPTCompactModel extends DefaultTreeModel {

	//{{{ Private members
	private static final String SEPARATOR = "/";
	private Map cache = new HashMap();
	//}}}

	//{{{ +VPTCompactModel(VPTNode) : <init>
	/**
	*	Create a new <code>VPTCompactModel</code>.
	*
	*	@param rootNode	The root node of the tree.
	*/
	public VPTCompactModel(VPTNode rootNode) {
		super(rootNode, true);
	}
	//}}}

	//{{{ +getChildCount(Object) : int
	public int getChildCount(Object parent) {
		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return node.getChildCount();
		} else if (node.isProject()) {
			return getProjectChildren((VPTProject)node).size();
		} else if (node instanceof CompactDirectoryNode) {
			CompactDirectoryNode cd = (CompactDirectoryNode) node;
			return cd.getFiles().size();
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent.getClass() = " + parent.getClass());
		return 0; // shouldn't reach here
	} //}}}

	//{{{ +getChild(Object, int) : Object
	public Object getChild(Object parent, int index) {
		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return node.getChildAt(index);
		} else if (node.isProject()) {
			return getProjectChildren((VPTProject)node).get(index);
		} else if (node instanceof CompactDirectoryNode) {
			CompactDirectoryNode cd = (CompactDirectoryNode) node;
			return cd.getFiles().get(index);
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent.getClass() = " + parent.getClass());
		return null; // shouldn't reach here
	} //}}}

	//{{{ +getIndexOfChild(Object, Object) : int
	public int getIndexOfChild(Object parent, Object child) {
		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return super.getIndexOfChild(parent, child);
		} else if (node.isProject()) {
			List l = getProjectChildren((VPTProject)node);
			return l.indexOf(child);
		} else if (node instanceof CompactDirectoryNode) {
			CompactDirectoryNode cd = (CompactDirectoryNode) node;
			return cd.getFiles().indexOf(child);
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent.getClass() = " + parent.getClass());
		return -1; // shouldn't reach here
	} //}}}

	//{{{ +nodeStructureChanged(TreeNode) : void
	public void nodeStructureChanged(TreeNode node) {
		cache.clear();
		super.nodeStructureChanged(node);
	} //}}}

	//{{{ +nodesWereInserted(TreeNode, int[]) : void
	public void nodesWereInserted(TreeNode node, int[] childIndices) {
		nodeStructureChanged(VPTNode.findProjectFor((VPTNode)node));
	} //}}}

	//{{{ +nodesWereRemoved(TreeNode, int[], Object[]) : void
	public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren) {
		nodeStructureChanged(VPTNode.findProjectFor((VPTNode)node));
	} //}}}

	//{{{ #getCompressedDirectories(VPTProject) : List
	protected List getCompressedDirectories(VPTProject node) {
		if (cache.get(node)!=null)
			return (List)cache.get(node);
		Log.log(Log.DEBUG, this, "not cached: "+node);
		List list=new ArrayList();
		getCompressedDirectories(new StringBuffer(), node, list);
		cache.put(node, list);
		return list;
	} //}}}

	//{{{ #getCompressedDirectories(StringBuffer, VPTNode, List) : void
	protected void getCompressedDirectories(StringBuffer leading, VPTNode node, List appendTo) {
		int oldLenght = leading.length();
		if (node.isDirectory() && hasFile(node)) {
			leading.append(node.getName());
			appendTo.add(new CompactDirectoryNode(node, leading.toString()));
			leading.setLength(oldLenght);
		}
		for(int i = 0; i < node.getChildCount(); i++) {
			VPTNode child = (VPTNode) node.getChildAt(i);
			String n;
			if (node.isProject()) {
				n = "";
			} else {
				n = node.getName();
			}
			leading.append(n).append(SEPARATOR);
			getCompressedDirectories(leading, child, appendTo);
			leading.setLength(oldLenght);
		}
	} //}}}

	//{{{ #hasFile(VPTNode) : boolean
	protected boolean hasFile(VPTNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			VPTNode child = (VPTNode) node.getChildAt(i);
			if (child.isFile()) {
				return true;
			}
		}
		return false;
	} //}}}

	//{{{ #getProjectChildren(VPTProject) : List
	protected List getProjectChildren(VPTProject project) {
		List cd = getCompressedDirectories(project);
		for (int i = 0; i < project.getChildCount(); i++) {
			VPTNode child = (VPTNode) project.getChildAt(i);
			if (child.isFile()) {
				cd.add(child);
			}
		}
		return cd;
	} //}}}

	//{{{ +class _CompactDirectoryNode_
	public static class CompactDirectoryNode extends VPTDirectory {

		private static Interpreter interpreter;

		private VPTNode dir;
		private String name;
		private List files = new ArrayList();

		//{{{ +CompactDirectoryNode(VPTNode, String) : <init>
		public CompactDirectoryNode(VPTNode dir, String name) {
			super(((VPTDirectory)dir).getFile());
			this.dir = dir;
			this.name = name;
			for (int i = 0; i < dir.getChildCount(); i++)
			{
				VPTNode child = (VPTNode) dir.getChildAt(i);
				if (child.isFile()) {
					files.add(child);
				}
			}
		} //}}}

		//{{{ +getDir() : VPTNode
		public VPTNode getDir() {
			return dir;
		} //}}}

		//{{{ +getName() : String
		public String getName() {
			return name;
		} //}}}

		//{{{ +getFiles() : List
		public List getFiles() {
			return files;
		} //}}}

		//{{{ +remove(VPTNode) : void
		public void remove(VPTNode node) {
			files.remove(node);
		} //}}}

		//{{{ +add(VPTNode) : void
		public void add(VPTNode node) {
			files.add(node);
		} //}}}

		//{{{ +getClipType() : boolean
		public int getClipType() {
			return VPTCellRenderer.CLIP_START;
		} //}}}

	} //}}}

}

