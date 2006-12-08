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
                ArrayList _path = new ArrayList();
		if (getTreePathForPosition(root, dot, _path)) {
			_path.add(root);
		}
                if(_path.size() == 0)
                {
                        // nothing found
			Log.log(Log.DEBUG, this, "+++ nothing found");
			return null;
                }
                else
                {
			Collections.reverse(_path);
			return new TreePath(_path.toArray());
                }
        } //}}}


     //{{{ getTreePathForPosition() method
     /* danson, updated so that I can pick the next node closest to the dot.  This helps
         with comments in java code -- since comments don't have a node associated
         with them, placing the cursor in a method comment, for example, would cause
         sidekick to highlight the class node rather than the associated method node.
         The following modifications will cause the node immediately following the
         cursor location to be highlighted in the tree. */
        /**
         * TODO: please document what these parameters mean.

         * @param node
         * @param dot
         * @param path
         */
        protected boolean getTreePathForPosition( TreeNode node, int dot, List path )
	{
                IAsset asset = getAsset( node );
                int childCount = node.getChildCount();

		// check if any of our children contain the caret
		// hertzhaft: I put this test first so that trees that
		// don't reflect the file order continue to work
		for ( int i = childCount - 1; i >= 0; i-- )
		{
			TreeNode _node = node.getChildAt( i );
			if ( getTreePathForPosition( _node, dot, path ) )
			{
				path.add( _node );
				return true;
			}
		}

		// if here, the dot is not in any of our children
                // check if the caret in inside this tag
                if ( asset != null && dot >= asset.getStart().getOffset()
		  && dot < asset.getEnd().getOffset() )
		{
			// find the next child
                        List children = new ArrayList();
                        for ( int i = 0; i < childCount; i ++ )
			{
                                children.add( node.getChildAt( i ) );
                        }
			if ( children.size()  == 0 )
			{
				return true;
			}

			// sort child nodes by offset
                        Collections.sort( children, assetComparator );

			// check if the dot is before the first child, if so,
			// we want the parent node, otherwise, clicking the mouse
			// directly on the text for the parent node would cause
			// the first child to get highlighted.  Really need a range
			// the assets...
			IAsset firstChild = getAsset((TreeNode)children.get(0));
			if (firstChild == null || firstChild.getStart().getOffset() > dot)
			{
				return true;
			}
                        for ( Iterator it = children.iterator(); it.hasNext(); )
			{
                            TreeNode tn = ( TreeNode ) it.next();
                            IAsset ias = getAsset(tn);
                            if ( ias == null || ias.getStart().getOffset() < dot )
			    {
                                    continue;
                            }
                            else
			    {
			    	if (canAddToPath(tn))
				{
					path.add( tn );
				}
                                break;
                            }
                        }
                        return true;
                }
                else
		{
                        return false;
                }
        } //}}}

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

