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
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

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

    // overriden to search TigerNodes rather than TreeNodes.  Not all tree nodes
    // may be showing, need the deepest asset at the cursor position for code
    // completion.
    public IAsset getAssetAtOffset( int pos ) {
        Object userObject = ( ( DefaultMutableTreeNode ) root ).getUserObject();
        if ( !( userObject instanceof TigerNode ) ) {
            return null;
        }
        TigerNode rootNode = (TigerNode)userObject;
        if (rootNode.getChildCount() == 0) {
            System.out.println("returning rootNode");
            return rootNode;
        }
        
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            TigerNode child = rootNode.getChildAt(i);
            if ( pos >= child.getStart().getOffset() && pos <= child.getEnd().getOffset() ) {
                TigerNode tn = getTigerNodeAtOffset(child, pos);
                
                ///
                System.out.println("returning " + tn);
                if (tn.getChildCount() > 0) {
                    System.out.println("children:");
                    for (Iterator it = tn.getChildren().iterator(); it.hasNext(); ) {
                        System.out.println(it.next());   
                    }
                }
                ///
                
                return tn;
            }
        }
        System.out.println("returning rootNode");
        return rootNode;
    }
    
    private TigerNode getTigerNodeAtOffset(TigerNode tn, int pos) {
        for (int i = 0; i < tn.getChildCount(); i++) {
            TigerNode child = tn.getChildAt(i);
            try {
                if ( pos >= child.getStart().getOffset() && pos <= child.getEnd().getOffset() ) {
                    
                    
                    return getTigerNodeAtOffset(child, pos);
                }
            }
            catch(NullPointerException e) {
                e.printStackTrace();
                System.out.println("child = " + child + ", start=" + child.getStart() + ", end=" + child.getEnd());
            }
        }
        return tn;
    }

}
