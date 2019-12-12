
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
    private enum BlockType { HEADER1, HEADER2, HEADER3, HEADER4, HEADER5, HEADER6, HEADER1S, HEADER2S, PARAGRAPH, QUOTE, CODE, BLANK};

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
            String filename = buffer.getPath();
            SideKickParsedData parsedData = new MarkdownSideKickParsedData( filename );
            Node rootNode = new Node( filename );
            rootNode.setLevel( 0 );
            rootNode.setStartLocation( new Location( 0, 0 ) );
            parsedData.root = new DefaultMutableTreeNode( rootNode );
            parsedData.tree = new DefaultTreeModel( parsedData.root );
            DefaultMutableTreeNode root = parsedData.root;

            // set up reading buffer
            StringReader sr = new StringReader( buffer.getText( 0, buffer.getLength() ) );
            BufferedReader lineReader = new BufferedReader( sr );
            int lineIndex = 1;

            // read a line
            String line = lineReader.readLine();
            String previousLine = null;
            Node n = null;
            DefaultMutableTreeNode currentTreeNode = parsedData.root;

            while ( line != null ) {
                BlockType bt = getBlockType( line );
                int level = 0;

                switch ( bt ) {
                    case HEADER1:
                        level = 1;
                        n = new Node( trim( line ) );
                        n.setLevel( level );
                        n.setStartLocation( new Location( lineIndex, 0 ) );
                        n.setEndLocation( new Location( lineIndex, line.length() ) );
                        n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        break;
                    case HEADER2:
                        level = 2;
                        n = new Node( trim( line ) );
                        n.setLevel( level );
                        n.setStartLocation( new Location( lineIndex, 0 ) );
                        n.setEndLocation( new Location( lineIndex, line.length() ) );
                        n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        break;
                    case HEADER3:
                        level = 3;
                        n = new Node( trim( line ) );
                        n.setLevel( level );
                        n.setStartLocation( new Location( lineIndex, 0 ) );
                        n.setEndLocation( new Location( lineIndex, line.length() ) );
                        n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        break;
                    case HEADER4:
                        level = 4;
                        n = new Node( trim( line ) );
                        n.setLevel( level );
                        n.setStartLocation( new Location( lineIndex, 0 ) );
                        n.setEndLocation( new Location( lineIndex, line.length() ) );
                        n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        break;
                    case HEADER5:
                        level = 5;
                        n = new Node( trim( line ) );
                        n.setLevel( level );
                        n.setStartLocation( new Location( lineIndex, 0 ) );
                        n.setEndLocation( new Location( lineIndex, line.length() ) );
                        n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        break;
                    case HEADER6:
                        level = 6;
                        n = new Node( trim( line ) );
                        n.setLevel( level );
                        n.setStartLocation( new Location( lineIndex, 0 ) );
                        n.setEndLocation( new Location( lineIndex, line.length() ) );
                        n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        break;
                    case HEADER1S:
                        if ( isBlankLine( previousLine ) ) {
                            level = -1;
                        }
                        else {
                            level = 1;
                            n = new Node( trim( previousLine ) );
                            n.setLevel( level );
                            n.setStartLocation( new Location( lineIndex - 1, 0 ) );
                            n.setEndLocation( new Location( lineIndex - 1, previousLine.length() ) );
                            n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        }
                        break;
                    case HEADER2S:
                        if ( isBlankLine( previousLine ) ) {
                            level = -1;
                        }
                        else {
                            level = 2;
                            n = new Node( trim( previousLine ) );
                            n.setLevel( level );
                            n.setStartLocation( new Location( lineIndex - 1, 0 ) );
                            n.setEndLocation( new Location( lineIndex - 1, previousLine.length() ) );
                            n.setIcon(EclipseIconsPlugin.getIcon("hierarchicalLayout.gif"));
                        }
                        break;
                    case PARAGRAPH:
                        if ( showParagraphs ) {
                            n = new Node( trim( line ) );
                            n.setLevel( -2 );
                            n.setStartLocation( new Location( lineIndex, 0 ) );
                            n.setEndLocation( new Location( lineIndex, line.length() ) );
                            n.setStart(ElementUtil.createStartPosition(buffer, n));
                            n.setEnd(ElementUtil.createEndPosition(buffer, n));
                            n.setIcon(EclipseIconsPlugin.getIcon("topic_small.gif"));
                            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                            currentTreeNode.add( treeNode );
                        }
                        lineIndex += skipToBlankLine( lineReader );
                        break;
                    case QUOTE:
                        if ( showQuotes ) {
                            n = new Node( trim( line ) );
                            n.setLevel( -2 );
                            n.setStartLocation( new Location( lineIndex, 0 ) );
                            n.setEndLocation( new Location( lineIndex, line.length() ) );
                            n.setStart(ElementUtil.createStartPosition(buffer, n));
                            n.setEnd(ElementUtil.createEndPosition(buffer, n));
                            n.setIcon(EclipseIconsPlugin.getIcon("run_co.gif"));
                            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                            currentTreeNode.add( treeNode );
                        }
                        lineIndex += skipToBlankLine( lineReader );
                        break;
                    case CODE:
                        if ( showCode ) {
                            n = new Node( trim( line ) );
                            n.setLevel( -2 );
                            n.setStartLocation( new Location( lineIndex, 0 ) );
                            n.setEndLocation( new Location( lineIndex, line.length() ) );
                            n.setStart(ElementUtil.createStartPosition(buffer, n));
                            n.setEnd(ElementUtil.createEndPosition(buffer, n));
                            n.setIcon(EclipseIconsPlugin.getIcon("class_obj.gif"));
                            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                            currentTreeNode.add( treeNode );
                        }
                        lineIndex += skipToEndOfCode( lineReader );
                        break;
                    case BLANK:
                        level = -1;
                        break;
                }


                if ( level > 0 ) {
                    n.setStart(ElementUtil.createStartPosition(buffer, n));
                    n.setEnd(ElementUtil.createEndPosition(buffer, n));
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( n );
                    DefaultMutableTreeNode parent = ( DefaultMutableTreeNode )root.getLastLeaf();
                    Node lastNode = ( Node )parent.getUserObject();
                    if ( lastNode.getLevel() == -2 ) {
                        parent = ( DefaultMutableTreeNode )parent.getParent();
                        lastNode = ( Node )parent.getUserObject();
                    }
                    while ( lastNode.getLevel() >= level ) {
                        parent = ( DefaultMutableTreeNode )parent.getParent();
                        if ( root.equals( parent ) ) {
                            break;
                        }
                        lastNode = ( Node )parent.getUserObject();
                        if ( lastNode.getLevel() == -2 ) {
                            parent = ( DefaultMutableTreeNode )parent.getParent();
                            lastNode = ( Node )parent.getUserObject();
                        }
                    }
                    parent.add( treeNode );
                    currentTreeNode = treeNode;
                }
                previousLine = line;
                line = lineReader.readLine();
                ++lineIndex;
            }
            //ElementUtil.convert( buffer, root );
            lineReader.close();
            return parsedData;
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private BlockType getBlockType( String line ) {
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
        Matcher m = setextH1.matcher( line );
        if ( m.matches() ) {
            return BlockType.HEADER1S;
        }
        m = setextH2.matcher( line );
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
        String line = lineReader.readLine();
        int count = 1;
        while ( !isBlankLine( line ) ) {
            ++count;
            line = lineReader.readLine();
        }
        return count;
    }
    
    private int skipToEndOfCode( BufferedReader lineReader ) throws IOException {
        String line = lineReader.readLine();
        int count = 1;
        while ( line != null && (line.startsWith( "    " ) || line.startsWith( "\t" )) ) {
            ++count;
            line = lineReader.readLine();
        }
        return count;
    }

    /**
     * Removes leading #, >, and whitespace from the start of the given line and
     * removes trailing # from the end of the given line.
     * @return the trimmed line
     */
    private String trim( String line ) {
        if ( line == null || line.length() == 0 ) {
            return "";
        }
        StringBuilder sb = new StringBuilder( line );
        while ( sb.charAt( 0 ) == '#' || sb.charAt( 0 ) == '>' || sb.charAt( 0 ) == ' ' || sb.charAt( 0 ) == '\t' ) {
            sb.deleteCharAt( 0 );
        }
        while ( sb.charAt( sb.length() - 1 ) == '#' ) {
            sb.deleteCharAt( sb.length() - 1 );
        }
        return sb.toString();
    }
}
