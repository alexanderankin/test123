
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
            DefaultMutableTreeNode root = parsedData.root;

            StringReader sr = new StringReader( buffer.getText( 0, buffer.getLength() ) );
            BufferedReader lineReader = new BufferedReader( sr );

            Pattern headerPattern = Pattern.compile( "^#{1,6}.*?" );

            int lineIndex = 1;
            String line = lineReader.readLine();
            String previousLine = line;

            while ( line != null ) {

                // check if header line
                Matcher m = headerPattern.matcher( line );
                if ( m.matches() ) {

                    // it's a # header line
                    Node n = new Node( line );
                    n.setStartLocation( new Location( lineIndex, 0 ) );
                    n.setEndLocation( new Location( lineIndex, line.length() ) );
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                    root.add( treeNode );
                }
                else if ( isSetextHeaderLine( line ) ) {

                    // previous line is a header line
                    Node n = new Node( previousLine );
                    n.setStartLocation( new Location( lineIndex - 1, 0 ) );
                    n.setEndLocation( new Location( lineIndex - 1, previousLine.length() ) );
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                    root.add( treeNode );
                }
                previousLine = line;
                line = lineReader.readLine();
                ++lineIndex;
            }
            ElementUtil.convert( buffer, root );
            return parsedData;
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isSetextHeaderLine( String line ) {
        return line.startsWith( "=" ) || line.startsWith( "-" );
    }
}
