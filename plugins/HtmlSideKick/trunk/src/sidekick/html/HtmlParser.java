/*
Copyright (c) 2005, Dale Anson
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
package sidekick.html;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import sidekick.*;
import sidekick.enhanced.SourceTree;
import sidekick.html.parser.html.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;
import errorlist.*;


public class HtmlParser extends SideKickParser {
    private View currentView = null;
    public static boolean showAll = false;

    public HtmlParser() {
        super( "html" );
    }
    
    
    public void parse() {
        if ( currentView != null ) {
            parse( currentView.getBuffer(), null );
        }
    }

    /**
     * Parse the contents of the given buffer.
     * @param buffer the buffer to parse
     */
    public SideKickParsedData parse( Buffer buffer, DefaultErrorSource errorSource ) {
        String filename = buffer.getPath();
        SideKickParsedData parsedData = new HtmlSideKickParsedData( filename );
        DefaultMutableTreeNode root = parsedData.root;
        
        StringReader reader = new StringReader(buffer.getText( 0, buffer.getLength() ));
        HtmlTreeBuilder builder = null;
        try {
            sidekick.html.parser.html.HtmlParser parser = new sidekick.html.parser.html.HtmlParser(reader);
            HtmlDocument document = parser.HtmlDocument();
            document.accept( new HtmlCollector() );
            document.accept( new HtmlScrubber( HtmlScrubber.DEFAULT_OPTIONS | HtmlScrubber.TRIM_SPACES) );
            builder = new HtmlTreeBuilder(root);
            builder.setShowAll(showAll);
            document.accept( builder );
            
            // need to convert the HtmlDocument.HtmlElements that are currently the 
            // user objects in the tree nodes to SideKick Assets
            convert(buffer, root);
            
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        return parsedData;
    }
    
    private void convert(Buffer buffer, DefaultMutableTreeNode node) {
        // convert the children of the node
        Enumeration children = node.children();
        while(children.hasMoreElements()) {
            convert(buffer, (DefaultMutableTreeNode)children.nextElement());
        }
        
        // convert the node itself
        if (!(node.getUserObject() instanceof IAsset)) {
            HtmlDocument.HtmlElement userObject = (HtmlDocument.HtmlElement)node.getUserObject();
            HtmlAsset asset = new HtmlAsset(userObject.toString());
            asset.setStart(createStartPosition(buffer, userObject));
            asset.setEnd(createEndPosition(buffer, userObject));
            node.setUserObject(asset);    
        }
    }


    /**
     * Need to create Positions for each node.  The javacc parser finds line and
     * column location, need to convert this to a Position in the buffer.  The 
     * TigerNode contains a column offset based on the current tab size as set in
     * the Buffer, need to use getOffsetOfVirtualColumn to account for soft and
     * hard tab handling.
     */
    private Position createStartPosition( Buffer buffer, HtmlDocument.HtmlElement child ) {
        final int line_offset = buffer.getLineStartOffset( Math.max( child.getStartLocation().line - 1, 0 ) );
        final int col_offset = buffer.getOffsetOfVirtualColumn( Math.max( child.getStartLocation().line - 1, 0 ),
                Math.max( child.getStartLocation().column - 1, 0 ), null );
        return new Position() {
                   public int getOffset() {
                       return line_offset + col_offset;
                   }
               };
    }


    /**
     * Need to create Positions for each node.  The javacc parser finds line and
     * column location, need to convert this to a Position in the buffer.  The 
     * TigerNode contains a column offset based on the current tab size as set in
     * the Buffer, need to use getOffsetOfVirtualColumn to account for soft and
     * hard tab handling.
     */
    private Position createEndPosition( Buffer buffer, HtmlDocument.HtmlElement child ) {
        final int line_offset = buffer.getLineStartOffset( Math.max( child.getEndLocation().line - 1, 0 ) );
        final int col_offset = buffer.getOffsetOfVirtualColumn( Math.max( child.getEndLocation().line - 1, 0 ),
                Math.max( child.getEndLocation().column - 1, 0 ), null );
        return new Position() {
                   public int getOffset() {
                       return line_offset + col_offset;
                   }
               };
    }

}

