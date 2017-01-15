/*
* SideKickParsedData.java
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

package sidekick.css;

// Imports
import sidekick.*;
import sidekick.util.*;
import javax.swing.tree.*;
import java.util.*;

import sidekick.css.parser.CSSNode;


/**
 * Stores a buffer structure tree.
 *
 * Plugins can extend this class to persist plugin-specific information.
 * For example, the XML plugin stores code completion-related structures using
 * a subclass.
 */
public class CSSParsedData extends SideKickParsedData {
    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public CSSParsedData( String fileName ) {
        super( fileName );
    }

    public TreePath getTreePathForPosition(int dot)
    {
        if(root.getChildCount() == 0) {
            return null;
        }

        Object userObject = ( ( DefaultMutableTreeNode ) root ).getUserObject();
        if ( userObject == null) {
            return null;
        }
        if ( !( userObject instanceof SideKickAsset ) ) {
            return null;
        }
        SideKickAsset asset = (SideKickAsset)userObject;
        CSSNode css_node = (CSSNode)asset.getElement();
        DefaultMutableTreeNode node = root;
        if ( nodeContains(css_node, dot) ) {
            node = getLeafNode(root, dot);
        }
        List<TreeNode> nodeList = new ArrayList<TreeNode>();
        while (node != null)
        {
            nodeList.add(node);
            node = (DefaultMutableTreeNode)node.getParent();
        }
        Collections.reverse(nodeList);
        return new TreePath(nodeList.toArray());

    }

    private DefaultMutableTreeNode getLeafNode(DefaultMutableTreeNode node, int dot) {
        Enumeration en = node.children();
        while ( en.hasMoreElements() ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)en.nextElement();
            SideKickAsset asset = (SideKickAsset)child.getUserObject();
            CSSNode css_node = (CSSNode)asset.getElement();
            if (nodeContains(css_node, dot)) {
                return getLeafNode(child, dot);
            }
        }
        return node;
    }

    private boolean nodeContains(CSSNode node, int dot) {
        int start = node.getStartPosition().getOffset();
        int end = node.getEndPosition().getOffset();
        return start <= dot && dot <= end ? true : false;
    }

    // overriden to search CSSNodes rather than TreeNodes.  Not all tree nodes
    // may be showing, need the deepest asset at the cursor position for code
    // completion.
    public IAsset getAssetAtOffset( int pos ) {
        if ( pos < 0 ) {
            return null;
        }
        Object userObject = ( ( DefaultMutableTreeNode ) root ).getUserObject();
        if ( userObject == null) {
            return null;
        }
        if ( !( userObject instanceof SideKickAsset ) ) {
            return null;
        }
        SideKickAsset returnable = (SideKickAsset) userObject;
        CSSNode rootNode = (CSSNode)returnable.getElement();
        if ( !rootNode.hasChildren() ) {
            return returnable;
        }

        for ( CSSNode child : rootNode.getChildren() ) {
            if ( pos >= child.getStartPosition().getOffset() && pos <= child.getEndPosition().getOffset() ) {
                CSSNode node = getCSSNodeAtOffset( child, pos );
                SideKickAsset asset = new SideKickAsset(node);
                asset.setStart(node.getStartPosition());
                asset.setEnd(node.getEndPosition());
                return asset;

            }
        }
        return returnable;
    }


    private CSSNode getCSSNodeAtOffset( CSSNode tn, int pos ) {
        for ( CSSNode child : tn.getChildren() ) {
            try {
                if ( pos >= child.getStartPosition().getOffset() && pos <= child.getEndPosition().getOffset() ) {
                    return getCSSNodeAtOffset( child, pos );
                }
            }
            catch ( NullPointerException e ) {
                // I was getting an NPE here...
                //e.printStackTrace();
            }
        }
        return tn;
    }
}
