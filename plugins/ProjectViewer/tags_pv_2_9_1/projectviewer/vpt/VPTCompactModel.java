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
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with this program; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package projectviewer.vpt;

//{{{ Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import org.gjt.sp.util.Log;
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
public class VPTCompactModel extends ProjectCustomTreeModel
{

	private static final String SEPARATOR = "/";


	/**
	 * Create a new <code>VPTCompactModel</code>.
	 *
	 * @param	rootNode	The root node of the tree.
	 */
	public VPTCompactModel(VPTNode rootNode)
	{
		super(rootNode);
	}


	public int getChildCount(Object parent)
	{
		if (parent instanceof CompactDirectoryNode) {
			CompactDirectoryNode cd = (CompactDirectoryNode) parent;
			return cd.getFiles().size();
		}
		return super.getChildCount(parent);
	}


	public Object getChild(Object parent,
						   int index)
	{
		if (parent instanceof CompactDirectoryNode) {
			CompactDirectoryNode cd = (CompactDirectoryNode) parent;
			return cd.getFiles().get(index);
		}
		return super.getChild(parent, index);
	}


	public int getIndexOfChild(Object parent,
							   Object child)
	{
		if (parent instanceof CompactDirectoryNode) {
			CompactDirectoryNode cd = (CompactDirectoryNode) parent;
			return cd.getFiles().indexOf(child);
		}
		return super.getIndexOfChild(parent, child);
	}


	public TreeNode[] getPathToRoot(TreeNode aNode)
	{
		if (aNode.isLeaf() && !aNode.getAllowsChildren()) {
			/*
			 * For leaf nodes, need first to check whether there is
			 * a parent node that contains the queried child before
			 * delegating to the superclass.
			 */
			VPTNode node = (VPTNode) aNode;
			VPTProject proj = VPTNode.findProjectFor(node);
			List<VPTNode> dirs = getCachedChildren(proj);

			for (VPTNode _dir : dirs) {
				CompactDirectoryNode cd = (CompactDirectoryNode) _dir;
				if (cd.getFiles().contains(node)) {
					return buildPathToRoot(cd, node);
				}
			}
		}
		return super.getPathToRoot(aNode);
	}


	protected List<VPTNode> getChildren(VPTProject project)
	{
		List<VPTNode> cd = getCompressedDirectories(project);
		for (int i = 0; i < project.getChildCount(); i++) {
			VPTNode child = (VPTNode) project.getChildAt(i);
			if (child.isFile()) {
				cd.add(child);
			}
		}
		return cd;
	}


	protected String getName()
	{
        return "projectviewer.compacttab";
	}


	private List<VPTNode> getCompressedDirectories(VPTProject node)
	{
		List<VPTNode> list = new ArrayList<VPTNode>();
		getCompressedDirectories(new StringBuffer(), node, list);
		return list;
	}


	private void getCompressedDirectories(StringBuffer leading,
										  VPTNode node,
										  List<VPTNode> appendTo)
	{
		int oldLenght = leading.length();

		if (node.isDirectory() && hasFile(node)) {
			leading.append(node.getName());
			appendTo.add(new CompactDirectoryNode(node, leading.toString()));
			leading.setLength(oldLenght);
		}

		for (int i = 0; i < node.getChildCount(); i++) {
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
	}


	private boolean hasFile(VPTNode node)
	{
		for (int i = 0; i < node.getChildCount(); i++) {
			VPTNode child = (VPTNode) node.getChildAt(i);
			if (child.isFile()) {
				return true;
			}
		}
		return false;
	}


	//{{{ class _CompactDirectoryNode_
	private static class CompactDirectoryNode extends VPTDirectory
	{

		private VPTNode dir;
		private String name;
		private List<VPTNode> files = new ArrayList<VPTNode>();

		//{{{ +CompactDirectoryNode(VPTNode, String) : <init>
		public CompactDirectoryNode(VPTNode dir, String name) {
			super(((VPTDirectory)dir).getURL());
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

		//{{{ +getName() : String
		public String getName() {
			return name;
		} //}}}

		//{{{ +getFiles() : List
		public List getFiles() {
			return files;
		} //}}}

		//{{{ +getClipType() : boolean
		public int getClipType() {
			return VPTCellRenderer.CLIP_START;
		} //}}}

		public int compareTo(VPTNode node)
		{
			if (node instanceof CompactDirectoryNode) {
				return compareName(node);
			} else {
				return -1 * node.compareTo(this);
			}
		}

	} //}}}

}

