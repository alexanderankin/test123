
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


import eclipseicons.EclipseIconsPlugin;

import errorlist.DefaultErrorSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.util.*;


public class MarkdownParser extends SideKickParser {

    private static final String NAME = "markdown";
    private View currentView = null;
    private Pattern setextH1 = Pattern.compile( "^=+?$" );
    private Pattern setextH2 = Pattern.compile( "^-+?$" );
    private enum BlockType { ROOT, HEADER1, HEADER2, HEADER3, HEADER4, HEADER5, HEADER6, HEADER1S, HEADER2S, PARAGRAPH, QUOTE, CODE, BLANK};
    private String line;

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

            // load options
            boolean showParagraphs = jEdit.getBooleanProperty( "sidekick.markdown.showParagraphs", true );
            boolean showQuotes = jEdit.getBooleanProperty( "sidekick.markdown.showQuotes", true );
            boolean showCode = jEdit.getBooleanProperty( "sidekick.markdown.showCode", true );

            // set up sidekick data structure
            int level = BlockType.ROOT.ordinal();
            String filename = buffer.getPath();
            SideKickParsedData parsedData = new MarkdownSideKickParsedData( filename );
            Node rootNode = new Node( filename );
            rootNode.setStartLocation( new Location( 0, 0 ) );
            rootNode.setStart( new SideKickPosition( 0 ) );
            rootNode.setLevel( level );
            parsedData.root = new DefaultMutableTreeNode( rootNode );
            parsedData.tree = new DefaultTreeModel( parsedData.root );
            DefaultMutableTreeNode root = parsedData.root;

            // set up reading buffer
            StringReader sr = new StringReader( buffer.getText( 0, buffer.getLength() ) );
            BufferedReader lineReader = new BufferedReader( sr );
            int lineIndex = 1;

            // read a line
            line = lineReader.readLine();
            Node n = null;

            while ( line != null ) {
                BlockType bt = getBlockType( line, lineReader );

                n = new Node( trim( line ) );
                n.setStartLocation( new Location( lineIndex, 0 ) );
                n.setStart( createStartPosition( buffer, n ) );
                n.setEndLocation( new Location( lineIndex, line.length() ) );
                n.setEnd( createEndPosition( buffer, n ) );
                n.setIcon( EclipseIconsPlugin.getIcon( "hierarchicalLayout.gif" ) );

                switch ( bt ) {
                    case HEADER1:
                    case HEADER2:
                    case HEADER3:
                    case HEADER4:
                    case HEADER5:
                    case HEADER6:
                        level = bt.ordinal();
                        break;
                    case HEADER1S:
                        level = BlockType.HEADER1.ordinal();
                        n.setName( trim( line ) );
                        line = lineReader.readLine();  // read ========
                        n.setEndLocation( new Location( ++lineIndex, line.length() ) );
                        n.setEnd( createEndPosition( buffer, n ) );
                        break;
                    case HEADER2S:
                        level = BlockType.HEADER2.ordinal();
                        n.setName( trim( line ) );
                        line = lineReader.readLine();   // read -------
                        n.setEndLocation( new Location( ++lineIndex, line.length() ) );
                        n.setEnd( createEndPosition( buffer, n ) );
                        break;
                    case PARAGRAPH:
                        level = bt.ordinal();
                        lineIndex += skipToBlankLine( lineReader );
                        if ( !showParagraphs ) {
                            n = null;
                        }
                        else {
                            n.setIcon( EclipseIconsPlugin.getIcon( "topic_small.gif" ) );
                            int length = line == null ? 0 : line.length();    // covers end of file situation
                            n.setEndLocation( new Location( lineIndex, length ) );
                            n.setEnd( createEndPosition( buffer, n ) );
                        }
                        break;
                    case QUOTE:
                        level = BlockType.PARAGRAPH.ordinal();
                        lineIndex += skipToBlankLine( lineReader );
                        if ( !showQuotes ) {
                            n = null;
                        }
                        else {
                            n.setIcon( EclipseIconsPlugin.getIcon( "run_co.gif" ) );
                            int length = line == null ? 0 : line.length();    // covers end of file situation
                            n.setEndLocation( new Location( lineIndex, length ) );
                            n.setEnd( createEndPosition( buffer, n ) );
                        }
                        break;
                    case CODE:
                        level = BlockType.PARAGRAPH.ordinal();
                        lineIndex += skipToEndOfCode( lineReader );
                        if ( !showCode ) {
                            n = null;
                        }
                        else {
                            n.setIcon( EclipseIconsPlugin.getIcon( "class_obj.gif" ) );
                            int length = line == null ? 0 : line.length();    // covers end of file situation
                            n.setEndLocation( new Location( lineIndex, length ) );
                            n.setEnd( createEndPosition( buffer, n ) );
                        }
                        break;
                    case BLANK:
                        n = null;
                        break;
                }


                // add the node at the appropriate level in the tree
                if ( n != null ) {
                    n.setLevel( level );
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                    DefaultMutableTreeNode parent = ( DefaultMutableTreeNode )root.getLastLeaf();
                    if ( parent == null || parent.equals( root ) ) {
                        root.add( treeNode );
                    }
                    else {
                        Node parentNode = ( Node )parent.getUserObject();
                        while ( parentNode.getLevel() >= level ) {
                            parent = ( DefaultMutableTreeNode )parent.getParent();
                            if ( parent == null || parent.equals( root ) ) {
                                root.add( treeNode );
                                break;
                            }
                            parentNode = ( Node )parent.getUserObject();
                        }
                        parent.add( treeNode );
                        parentNode.setEndLocation( n.getEndLocation() );
                        parentNode.setEnd( n.getEnd() );
                    }
                    rootNode.setEndLocation( n.getEndLocation() );
                    rootNode.setEnd( n.getEnd() );
                    n = null;
                }

                line = lineReader.readLine();
                ++lineIndex;
            }

            lineReader.close();
            return parsedData;
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private BlockType getBlockType( String line, BufferedReader lineReader ) throws IOException {
        if ( line == null || line.isBlank() ) {
            return BlockType.BLANK;
        }
        if ( line.startsWith( "######" ) ) {
            return BlockType.HEADER6;
        }
        if ( line.startsWith( "#####" ) ) {
            return BlockType.HEADER5;
        }
        if ( line.startsWith( "####" ) ) {
            return BlockType.HEADER4;
        }
        if ( line.startsWith( "###" ) ) {
            return BlockType.HEADER3;
        }
        if ( line.startsWith( "##" ) ) {
            return BlockType.HEADER2;
        }
        if ( line.startsWith( "#" ) ) {
            return BlockType.HEADER1;
        }
        lineReader.mark(1024);
        String nextLine = lineReader.readLine();
        lineReader.reset();
        Matcher m = setextH1.matcher( nextLine );
        if ( m.matches() ) {
            return BlockType.HEADER1S;
        }
        m = setextH2.matcher( nextLine );
        if ( m.matches() ) {
            return BlockType.HEADER2S;
        }
        if ( line.startsWith( ">" ) ) {
            return BlockType.QUOTE;
        }
        if ( line.startsWith( "    " ) || line.startsWith( "\t" ) ) {
            return BlockType.CODE;
        }
        return BlockType.PARAGRAPH;
    }

    /**
     * @return true if line contains no characters or only whitespace characters
     */
    private boolean isBlankLine( String line ) {
        if ( line == null ) {
            return true;
        }
        return line.isBlank();
    }

    private int skipToBlankLine( BufferedReader lineReader ) throws IOException {
        line = lineReader.readLine();
        int count = 1;
        while ( !isBlankLine( line ) ) {
            ++count;
            line = lineReader.readLine();
        }
        return count;
    }

    private int skipToEndOfCode( BufferedReader lineReader ) throws IOException {
        line = lineReader.readLine();
        int count = 1;

        // code blocks start with 4 or more spaces or a single tab
        while ( line != null && ( line.startsWith( "    " ) || line.startsWith( "\t" ) ) ) {
            ++count;
            line = lineReader.readLine();
        }
        return count;
    }

    /**
     * Removes leading #, >, and whitespace from the start of the given line and
     * removes trailing # and whitespace from the end of the given line.
     * @return the trimmed line
     */
    private String trim( String line ) {
        if ( line == null || line.length() == 0 ) {
            return "";
        }
        StringBuilder sb = new StringBuilder( line.trim() );
        while ( sb.charAt( 0 ) == '#' || sb.charAt( 0 ) == '>' || sb.charAt( 0 ) == ' ' || sb.charAt( 0 ) == '\t' ) {
            sb.deleteCharAt( 0 );
        }
        while ( sb.charAt( sb.length() - 1 ) == '#' ) {
            sb.deleteCharAt( sb.length() - 1 );
        }
        return sb.toString();
    }

    private SideKickPosition createStartPosition( Buffer buffer, Node n ) {
        return new SideKickPosition( ElementUtil.createStartPosition( buffer, n ).getOffset() );
    }

    private SideKickPosition createEndPosition( Buffer buffer, Node n ) {
        return new SideKickPosition( ElementUtil.createEndPosition( buffer, n ).getOffset() );
    }
}
