/*
Copyright (c) 2006, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the <ORGANIZATION> nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package sidekick.util;

import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import org.gjt.sp.jedit.Buffer;

public class ElementUtil {

    /**
     * Convert the start of a Location to a Position.
     * Need to create Positions for each node. The javacc parser finds line and
     * column location, need to convert this to a Position in the buffer. The
     * SideKickElement contains a column offset based on the current tab size as
     * set in the Buffer, need to use getOffsetOfVirtualColumn to account for
     * soft and hard tab handling.
     *
     * Note that this method will also set the start position in the given SideKickElement.
     *
     * @param buffer the buffer containing the child element/text in question
     * @param element the SideKickElement representing some text in the buffer
     * @return  a Position representing the offset from the start of the buffer
     * to the start of the element
     */
    public static Position createStartPosition( Buffer buffer, SideKickElement element ) {
        int line_offset = buffer.getLineStartOffset(
                    Math.max( element.getStartLocation().line - 1, 0 ) );
        int[] totalVirtualWidth = new int[ 1 ];
        int column_offset = buffer.getOffsetOfVirtualColumn(
                    Math.max( element.getStartLocation().line - 1, 0 ),
                    Math.max( element.getStartLocation().column - 1, 0 ),
                    totalVirtualWidth );
        if ( column_offset == -1 ) {
            column_offset = totalVirtualWidth[ 0 ];
        }
        Position p = createPosition( line_offset, column_offset );
        element.setStartPosition( p );
        return p;
    }

    /**
     * Convert the end of a Location to a Position.
     * Need to create Positions for each node. The javacc parser finds line and
     * column location, need to convert this to a Position in the buffer. The
     * SideKickElement contains a column offset based on the current tab size as
     * set in the Buffer, need to use getOffsetOfVirtualColumn to account for
     * soft and hard tab handling.
     *
     * Note that this method will also set the end position in the given SideKickElement.
     *
     * @param buffer the buffer containing the child element/text in question
     * @param element the SideKickElement representing some text in the buffer
     * @return  a Position representing the offset from the start of the buffer
     * to the end of the element
     */
    public static Position createEndPosition( Buffer buffer, SideKickElement element ) {
        int line_offset = buffer.getLineStartOffset(
                    Math.max( element.getEndLocation().line - 1, 0 ) );
        int[] totalVirtualWidth = new int[ 1 ];
        int column_offset = buffer.getOffsetOfVirtualColumn(
                    Math.max( element.getEndLocation().line - 1, 0 ),
                    Math.max( element.getEndLocation().column - 1, 0 ),
                    totalVirtualWidth );
        if ( column_offset == -1 ) {
            column_offset = totalVirtualWidth[ 0 ];
        }
        Position p = createPosition( line_offset, column_offset );
        element.setEndPosition( p );
        return p;
    }

    public static Position createPosition( int line_offset, int column_offset ) {
        final int lo = line_offset;
        final int co = column_offset;
        return new Position() {
                   public int getOffset() {
                       return lo + co;
                   }
               };

    }

    /**
     * Assumes the user objects in the given node, and child nodes, are
     * objects that implement SideKickElement.  This method removes the
     * SideKickElement from each node and replaces it with a SideKickAsset
     * that wraps the original SideKickElement.
     * @param buffer the Buffer representing the text that is to be displayed
     * in SideKick.  This is used to calculate positions for the individual
     * tree nodes.
     * @param node the root node of the tree to convert.
     */
    public static void convert( Buffer buffer, DefaultMutableTreeNode node ) {
        final Buffer _buffer = buffer;
        final DefaultMutableTreeNode _node = node;
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    _convert( _buffer, _node );
                }
            }
        );
    }

    private static void _convert( Buffer buffer, DefaultMutableTreeNode node ) {
        // convert the children of the node
        Enumeration children = node.children();
        while ( children.hasMoreElements() ) {
            _convert( buffer, ( DefaultMutableTreeNode ) children.nextElement() );
        }

        // convert the node itself
        if ( ( node.getUserObject() instanceof SideKickElement ) ) {
            SideKickElement userObject = ( SideKickElement ) node.getUserObject();
            Position start_position = createStartPosition( buffer, userObject );
            Position end_position = createEndPosition( buffer, userObject );
            SideKickAsset asset = new SideKickAsset( userObject );
            asset.setStart( start_position );
            asset.setEnd( end_position );
            node.setUserObject( asset );
        }
    }


}