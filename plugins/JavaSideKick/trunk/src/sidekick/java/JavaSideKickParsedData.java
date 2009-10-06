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

package sidekick.java;

// Imports
import sidekick.*;
import javax.swing.tree.*;
import java.util.*;

import sidekick.java.node.TigerNode;


/**
 * Stores a buffer structure tree.
 *
 * Plugins can extend this class to persist plugin-specific information.
 * For example, the XML plugin stores code completion-related structures using
 * a subclass.
 */
public class JavaSideKickParsedData extends SideKickParsedData {

    /**
     * @param fileName The file name being parsed, used as the root of the
     * tree.
     */
    public JavaSideKickParsedData( String fileName ) {
        super( fileName );
    }

    
    /**
     * Overridden to search TigerNodes rather than TreeNodes.  Not all tree nodes
     * may be showing, need the deepest asset at the cursor position for code
     * completion.
     * @param pos the caret position within the code
     */
    public IAsset getAssetAtOffset( int pos ) {
        Object userObject = ( ( DefaultMutableTreeNode ) root ).getUserObject();
        if ( !( userObject instanceof TigerNode ) ) {
            return null;
        }

        TigerNode tn = getTigerNodeAtOffset( ( TigerNode ) userObject, pos );
        return tn;
    }

    
    /**
     * Drill down to the node containing the given position.  
     * @param tn The node to check the children of.
     * @param pos The caret position within the code.
     */
    private TigerNode getTigerNodeAtOffset( TigerNode tn, int pos ) {
        for ( int i = 0; i < tn.getChildCount(); i++ ) {
            TigerNode child = tn.getChildAt( i );
            int start = child.getStart().getOffset();
            // 'end' may not be filled in.  If the user is typing and is using
            // code completion, the parser will not be able to calculate end
            // positions. The check for start >= end allows for this case and
            // allows finding the node whose start position is closest to the
            // caret position.
            int end = child.getEnd().getOffset();
            if ( (pos >= start && pos <= end) || (start >= end) ) {
                return getTigerNodeAtOffset( child, pos );
            }
        }
        return tn;
    }


    // overridden to handle Extends and Implements nodes
    protected boolean canAddToPath( TreeNode node ) {
        try {
            TigerNode tn = ( TigerNode ) getAsset( node );
            return ( tn.getOrdinal() == TigerNode.EXTENDS || tn.getOrdinal() == TigerNode.IMPLEMENTS ) ? false : true;
        }
        catch ( Exception e ) {
            return super.canAddToPath( node );
        }
    }

}