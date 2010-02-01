/*
 * SideKickParsedData.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2004 Slava Pestov
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

package sidekick;

//{{{ Imports
import javax.swing.tree.*;
import java.util.*;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.util.Log;

import javax.swing.text.Position;
import sidekick.enhanced.SourceAsset;

//}}}

/**
 * Stores a buffer structure tree.
 *
 * @modified   $Id$
 * @version    $Revision$
 * Plugins can extend this class to persist plugin-specific information.
 * For example, the XML plugin stores code completion-related structures using
 * a subclass.
 */
public class SideKickParsedData
{
        //{{{ getParsedData() method
        /**
         * Returns an instance of this class for the specified view.
         *
         * Note that this will only return a valid object after the
         * <code>SideKick.parse()</code> method is called.
         *
         * @param view The view.
         */
        public static SideKickParsedData getParsedData(View view)
        {
                return (SideKickParsedData)view.getRootPane().getClientProperty(
                        SideKickPlugin.PARSED_DATA_PROPERTY);
        } //}}}

        //{{{ setParsedData() method
        /**
         * Sets the instance of this class for the specified view.
         *
         * @param view The view.
         * @param data The instance.
         */
        public static void setParsedData(View view, SideKickParsedData data)
        {
                Log.log(Log.DEBUG,SideKickParsedData.class,
                        "Setting parsed data of " + view + " to " + data);
                view.getRootPane().putClientProperty(
                        SideKickPlugin.PARSED_DATA_PROPERTY,
                        data);
        } //}}}

        public DefaultTreeModel tree;

        /**
         * Plugin parsers should add nodes to the root node.
         */
        public DefaultMutableTreeNode root;
        
        /**
         * Plugin parsers may set the tree expansion model based on options settings
         * or other considerations.  This is a list of row numbers to be expanded.
         * The <code>sidekick.ExpansionModel</code> class can help create this list.
         */
        public List<Integer> expansionModel = null;


        //{{{ SideKickParsedData constructor
        /**
         * @param fileName The file name being parsed, used as the root of the
         * tree.
         */
        public SideKickParsedData(String fileName)
        {
		// root node is missing an asset at this point, so make a
		// temporary asset for it that covers the entire range of the
		// buffer
		SourceAsset asset = new SourceAsset(fileName, 0, new Position(){
				public int getOffset() {
					return 0;
				}
		});
		asset.setEnd(new Position(){
				public int getOffset() {
					return Integer.MAX_VALUE;
				}
		});
                root = new DefaultMutableTreeNode(asset);
                tree = new DefaultTreeModel(root);
        } //}}}

	//{{{ getTreePathForPosition() method
        /**
         * @param dot
         */
        public TreePath getTreePathForPosition(int dot)
        {
                if(root.getChildCount() == 0) {
                        return null;
		}

		IAsset asset = getAsset(root);
		if (asset != null && !assetContains(asset, dot))
			// root does not contain the dot (???)
			return null;

		TreeNode node = getNodeAt(root, dot);
		if (node == null)
			node = root;
		List<TreeNode> nodeList = new ArrayList<TreeNode>();
		while (node != null)
		{
			nodeList.add(node);
			node = node.getParent();
		}
		Collections.reverse(nodeList);
		return new TreePath(nodeList.toArray());

        } //}}}


	protected TreeNode getNodeAt(TreeNode parent, int offset)
	{
		for (int i = 0;i<parent.getChildCount();i++)
		{
			TreeNode node = parent.getChildAt(i);
			// does one of this node's children contain the dot?
			TreeNode child = getNodeAt(node, offset);
			if (child != null)
				// yes: return it
				return child;
			// no: does this node itself contain the dot?
			if (assetContains(getAsset(node), offset))
				// yes: return it
				return node;
		}
		// the following seems redundant
		// if (assetContains(getAsset(parent), offset))
		//	return parent;
		return null;
	}

	protected FoldHandler getFoldHandler()
	{
		return null;
	}
	
	private static boolean assetContains(IAsset asset, int offset)
	{
		if (asset == null)
			return false;
		return offset >= asset.getStart().getOffset()
		    && offset < asset.getEnd().getOffset();
	}

	//{{{ canAddToPath() method
	/**
	 * Subclasses can override this to handle special case nodes that may not
	 * be suitable for adding to the path.  See JavaSideKick for an example.
	 * @param node a TreeNode that is being considered for adding to a tree path.
	 * @return true if it is okay to add the node.  This default implementation
	 * always returns true.
	 */
	protected boolean canAddToPath(TreeNode node) {
		return true;
	} //}}}

        //{{{ getAssetAtPosition() method

        /**
         * @deprecated use {@link #getAssetAtOffset(int)} instead
         */
        public Asset getAssetAtPosition(int pos)
        {
                return (Asset) getAssetAtOffset(pos);
        } //}}}

        //{{{ getAssetAtOffset() method
        /**
         *
         * @param pos the offset from the beginning of the buffer
         */
        public IAsset getAssetAtOffset(int pos)
        {
                TreePath path = getTreePathForPosition(pos);
                if(path == null)
                        return null;
                return (IAsset) ((DefaultMutableTreeNode)path
                        .getLastPathComponent()).getUserObject();
        } //}}}

	//{{{ getAsset() method
	/**
	 * Convenience method to get the IAsset from the user object in the node
	 * @param node a DefaultMutableTreeNode.  Anything else will cause this method
	 * to return null.
	 * @return the IAsset contained in the user object in the node.
	 */
        public IAsset getAsset( TreeNode node ) {
	       if ( !( node instanceof DefaultMutableTreeNode ) )
	       {
	       	       return null;
	       }
               Object userObject = ( ( DefaultMutableTreeNode ) node ).getUserObject();
               if ( !( userObject instanceof IAsset ) ) {
                       return null;
               }
               IAsset asset = ( IAsset ) userObject;
               return asset;
	} //}}}
}

