
/*
 * Copyright (c) 2019, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sidekick.markdown;


import errorlist.DefaultErrorSource;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.util.*;


public class MarkdownParser extends SideKickParser {

    private static final String NAME = "markdown";
    private View currentView = null;
    public static boolean showAll = false;

    public MarkdownParser() {
        super( NAME );
    }

    /**
     * Parse the current buffer in the current view.
     * TODO: is this used anymore?
     */
    public void parse() {
        if ( currentView != null ) {
            parse( currentView.getBuffer(), null );
        }
    }

    /**
     * Parse the contents of the given buffer.
     * @param buffer the buffer to parse
     * @param errorSource where to send any error messages
     * @return data for the tree
     */
    public SideKickParsedData parse( Buffer buffer, DefaultErrorSource errorSource ) {
        try {
            String filename = buffer.getPath();
            SideKickParsedData parsedData = new MarkdownSideKickParsedData( filename );
            Node rootNode = new Node( filename, 0 );
            rootNode.setStartLocation( new Location( 0, 0 ) );
            parsedData.root = new DefaultMutableTreeNode( rootNode );
            parsedData.tree = new DefaultTreeModel(parsedData.root);
            DefaultMutableTreeNode root = parsedData.root;
            StringReader sr = new StringReader( buffer.getText( 0, buffer.getLength() ) );
            BufferedReader lineReader = new BufferedReader( sr );
            int lineIndex = 1;
            String line = lineReader.readLine();
            String previousLine = line;
            Node n = null;

            while ( line != null ) {

                // check if header line
                int level = isHeaderLine( line );
                if ( level > 0 ) {

                    // it's a # header line
                    n = new Node( line, level );
                    n.setStartLocation( new Location( lineIndex, 0 ) );
                    n.setEndLocation( new Location( lineIndex, line.length() ) );
                }
                else if ( ( level = isSetextHeaderLine( line ) ) > 0 ) {

                    // previous line is a header line
                    n = new Node( previousLine, level );
                    n.setStartLocation( new Location( lineIndex - 1, 0 ) );
                    n.setEndLocation( new Location( lineIndex - 1, previousLine.length() ) );
                }
                if ( level > 0 ) {
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                    DefaultMutableTreeNode parent = ( DefaultMutableTreeNode )root.getLastLeaf();
                    Node lastNode = ( Node )parent.getUserObject();
                    while ( lastNode.getLevel() >= level ) {
                        parent = ( DefaultMutableTreeNode )parent.getParent();
                        if ( root.equals( parent ) ) {
                            break;
                        }
                        lastNode = ( Node )parent.getUserObject();
                    }
                    parent.add( treeNode );
                }
                previousLine = line;
                line = lineReader.readLine();
                ++lineIndex;
            }
            ElementUtil.convert( buffer, root );
            lineReader.close();
            return parsedData;
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return -1 if line is not a header line or the header level if it is
     */
    private int isHeaderLine( String line ) {
        int level = -1;
        if ( line.startsWith( "#" ) ) {
            level = 1;
            for ( int i = 1; i < 6; i++ ) {
                if ( line.charAt( i ) == '#' ) {
                    ++level;
                }
                else {
                    return level;
                }
            }
        }
        return level;
    }

    private int isSetextHeaderLine( String line ) {
        if ( line.startsWith( "=" ) ) {
            return 1;
        }
        if ( line.startsWith( "-" ) ) {
            return 2;
        }
        return -1;
    }
}
