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
//}}}

/**
 * Stores a buffer structure tree.
 *
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
                root = new DefaultMutableTreeNode(fileName);
                tree = new DefaultTreeModel(root);
        } //}}}

        //{{{ getTreePathForPosition() method
        public TreePath getTreePathForPosition(int dot)
        {
                if(root.getChildCount() == 0)
                        return null;

                ArrayList _path = new ArrayList();
                for(int i = root.getChildCount() - 1; i >= 0; i--)
                {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                root.getChildAt(i);
                        if(getTreePathForPosition(node,dot,_path))	
                        {
                                _path.add(node);
                                break;
                        }
                }

                if(_path.size() == 0)
                {
                        // nothing found, so go to the root
                        return new TreePath( new Object[] {root} );
                }
                else
                {
                        Object[] path = new Object[_path.size() + 1];
                        path[0] = root;
                        int len = _path.size();
                        for(int i = 0; i < len; i++)
                                path[i + 1] = _path.get(len - i - 1);

                        TreePath treePath = new TreePath(path);
                        return treePath;
                }
        } //}}}
	

        //{{{ getTreePathForPosition() method
        // danson, updated so that I can pick the next node closest to the dot.  This helps
        // with comments in java code -- since comments don't have a node associated
        // with them, placing the cursor in a method comment, for example, would cause
        // sidekick to highlight the class node rather than the associated method node.
        // The following modifications will cause the node immediately following the
        // cursor location to be highlighted in the tree.
        private boolean getTreePathForPosition( TreeNode node, int dot, List path ) 
	{
                IAsset asset = getAsset( node );
                if ( asset == null ) 
		{
                        return false;
                }
                int childCount = node.getChildCount();
                
                // check if the caret in inside this tag
                if ( dot >= asset.getStart().getOffset() && dot <= asset.getEnd().getOffset() ) 
		{
                        // check if any of our children contain the caret
                        for ( int i = childCount - 1; i >= 0; i-- ) 
			{
                                TreeNode _node = node.getChildAt( i );
                                if ( getTreePathForPosition( _node, dot, path ) ) 
				{
                                        path.add( _node );
                                        return true;
                                }
                        }
                        
                        // if here, then the dot is in this node, but not in any of the children
                        // find the closest child
                        List children = new ArrayList();
                        for ( int i = 0; i < childCount; i ++ ) 
			{
                                children.add( node.getChildAt( i ) );
                        }
                        Collections.sort( children, new Comparator() {
                                    public int compare( Object a, Object b ) {
                                            javax.swing.text.Position ap = getAsset((TreeNode)a).getStart();
                                            javax.swing.text.Position bp = getAsset((TreeNode)b).getStart();
                                            // check nulls
                                            if (ap == null && bp == null) {
                                                    return 0;   
                                            }
                                            if (ap == null && bp == null) {
                                                    return -1;   
                                            }
                                            if (ap != null && bp == null) {
                                                    return 1;                                
                                            }
                                            // neither are null, check offset
                                            Integer ai = new Integer(ap.getOffset());
                                            Integer bi = new Integer(bp.getOffset());
                                            return ai.compareTo(bi);
                                    }
                                }
                                        );
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
			    	if (canAddToPath(tn)) {
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
