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
			return null;

		TreeNode node = getNodeAt(root, dot);
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
			IAsset asset = getAsset(node);
			if (asset == null)
			{
				TreeNode ret = getNodeAt(node, offset);
				if (ret != null)
					return ret;
			}
			else if (assetContains(asset, offset))
			{
				TreeNode ret = getNodeAt(node, offset);
				if (ret != null)
					return ret;
				return node;
			}
		}
		IAsset asset = getAsset(parent);
		if ((asset != null) && assetContains(asset, offset))
			return parent;
		return null;
	}

	private static boolean assetContains(IAsset asset, int offset)
	{
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
         * @param pos TODO: explain what pos means.
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

        private Comparator assetComparator = new Comparator() {
		public int compare( Object a, Object b ) {
			IAsset ia = getAsset((TreeNode)a);
			IAsset ib = getAsset((TreeNode)b);

			// check nulls
			if (ia == null && ib == null) {
				return 0;
			}
			if (ia != null && ib == null) {
				return -1;
			}
			if (ia == null && ib != null) {
				return 1;
			}
			// neither are null, check offset
			javax.swing.text.Position ap = ia.getStart();
			javax.swing.text.Position bp = ib.getStart();
			Integer ai = new Integer(ap.getOffset());
			Integer bi = new Integer(bp.getOffset());
			return ai.compareTo(bi);
		}
	};
}

