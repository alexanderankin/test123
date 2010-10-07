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
package sidekick.java;

import errorlist.*;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import sidekick.ExpansionModel;
import sidekick.SideKickCompletion;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.SideKickUpdate;

import sidekick.java.node.*;
import sidekick.java.options.*;
import sidekick.java.parser.*;

import sidekick.util.ElementUtil;
import sidekick.util.Location;
import sidekick.util.Range;

public class JavaParser extends SideKickParser implements EBComponent {
    private View currentView = null;
    private OptionValues optionValues = new OptionValues();

    private JavaCompletionFinder completionFinder = null;

    public static final int JAVA_PARSER = 1;
    public static final int JAVACC_PARSER = 2;

    // which type of parser, java or javacc
    private int parser_type = JAVA_PARSER;

    public static final String COMPILATION_UNIT = "javasidekick.compilationUnit";

    /**
     * Defaults to parsing java files.
     */
    public JavaParser() {
        this( JAVA_PARSER );
    }

    /**
     * @param type one of 'java' or 'javacc'
     */
    public JavaParser( int type ) {
        super( type == JAVACC_PARSER ? "javacc" : "java" );
        switch ( type ) {
            case JAVACC_PARSER:
                parser_type = JAVACC_PARSER;
                break;
            default:
                parser_type = JAVA_PARSER;
        }
        EditBus.addToBus( this );
    }

    /**
     * This method is called when a buffer using this parser is selected
     * in the specified view.
     * @param editPane The edit pane
     * @since SideKick 0.3.1
     */
    public void activate( EditPane editPane ) {
        super.activate( editPane );
        currentView = editPane.getView();
    }

    public void deactivate( EditPane editPane ) {
        completionFinder = null;
        super.deactivate( editPane );
    }

    /**
     * Reparse if the option settings have changed.
     */
    public void handleMessage( EBMessage msg ) {
        // reparse on properties changed
        // TODO: fix this, should only parse if properties for this plugin
        // have changed.
        if ( ( msg instanceof PropertiesChanged ) ) {
            if ( currentView != null ) {
                currentView = jEdit.getActiveView();
            }
            EditBus.send( new SideKickUpdate( currentView ) );
        }
    }

    /**
     * Parse the current buffer in the current view.
     */
    public SideKickParsedData parse() {
        if ( currentView == null ) {
            currentView = jEdit.getActiveView();
        }
        return parse( currentView.getBuffer(), null );
    }

    /**
     * Parse the contents of the given buffer.
     * @param buffer the buffer to parse
     */
    public SideKickParsedData parse( Buffer buffer, DefaultErrorSource errorSource ) {
        ByteArrayInputStream input = null;
        String filename = buffer.getPath();
        SideKickParsedData parsedData = new JavaSideKickParsedData( filename );
        DefaultMutableTreeNode root = parsedData.root;
        TigerParser parser = null;
        CUNode compilationUnit = null;
        try {
            // re-init the labeler
            TigerLabeler.setOptionValues( optionValues );

            if ( buffer.getLength() <= 0 ) {
                return parsedData;
            }
            // read the source code directly from the Buffer rather than from the
            // file.  This means:
            // 1) a modifed buffer can be parsed without a save
            // 2) reading the buffer should be faster than reading from the file, and
            // 3) jEdit has that 'gzip file on disk' option which won't parse.
            input = new ByteArrayInputStream( buffer.getText( 0, buffer.getLength() ).getBytes() );
            parser = new TigerParser( input );
            int tab_size = buffer.getTabSize();
            switch ( parser_type ) {
                case JAVACC_PARSER:
                    compilationUnit = parser.getJavaCCRootNode( tab_size );
                    break;
                default:
                    compilationUnit = parser.getJavaRootNode( tab_size );
                    break;
            }
            if ( "true".equals( jEdit.getProperty( "javasidekick.dump" ) ) ) {
                System.out.println( compilationUnit.dump() );
            }

            // compilationUnit is root node
            compilationUnit.setName( buffer.getName() );
            compilationUnit.setFilename( filename );
            compilationUnit.setResults( parser.getResults() );
            compilationUnit.setStart( ElementUtil.createStartPosition( buffer, compilationUnit ) );
            compilationUnit.setEnd( ElementUtil.createEndPosition( buffer, compilationUnit ) );
            root.setUserObject( compilationUnit );
            buffer.setProperty( COMPILATION_UNIT, compilationUnit );

            ExpansionModel expansionModel = new ExpansionModel();
            expansionModel.add();   // cu
            parsedData.expansionModel = expansionModel.getModel();

            // maybe show imports, but don't expand them
            if ( optionValues.getShowImports() == true ) {
                List<ImportNode> imports = compilationUnit.getImportNodes();
                if ( imports != null && !imports.isEmpty() ) {
                    TigerNode importsParent = new ImportNode("Imports");
                    importsParent.setStart(ElementUtil.createStartPosition(buffer, imports.get(0)));
                    importsParent.setEnd(ElementUtil.createEndPosition(buffer, imports.get(imports.size() - 1)));
                    DefaultMutableTreeNode importsNode = new DefaultMutableTreeNode( importsParent );
                    root.add( importsNode );
                    expansionModel.inc();
                    for ( TigerNode anImport : imports ) {
                        anImport.setStart( ElementUtil.createStartPosition( buffer, anImport ) );
                        anImport.setEnd( ElementUtil.createEndPosition( buffer, anImport ) );
                        importsNode.add( new DefaultMutableTreeNode( anImport ) );
                    }
                }
            }

            // show constructors, fields, methods, etc
            addChildren( root, buffer, expansionModel );
            /*
            if ( compilationUnit.getChildren() != null ) {
            Collections.sort( compilationUnit.getChildren(), nodeSorter );
                for ( Iterator it = compilationUnit.getChildren().iterator(); it.hasNext(); ) {
                    TigerNode child = ( TigerNode ) it.next();

                    if ( canShow( child ) ) {
                        child.setStart( ElementUtil.createStartPosition( buffer, child ) );
                        child.setEnd( ElementUtil.createEndPosition( buffer, child ) );
                        DefaultMutableTreeNode cuChild = new DefaultMutableTreeNode( child );
                        root.add( cuChild );
                        expansionModel.add();   // class node
                        addChildren( buffer, cuChild, child, expansionModel );
                    }
                }
                }
                */
        }
        catch ( ParseException e ) {    // NOPMD
            // removed exception handling, all ParseExceptions are now caught
            // and accumulated in the parser, then dealt with in handleErrors.
        }
        catch ( TokenMgrError e ) {      // NOPMD
            // don't worry about this one, most likely it is due to attempting
            // to parse during code completion.
        }
        finally {
            try {
                input.close();
            }
            catch ( Exception e ) {     // NOPMD
                // not to worry
            }
        }

        // only handle errors when buffer is saved or code completion is off. Otherwise,
        // there will be a lot of spurious errors shown when code completion is on and the
        // user is in the middle of typing something.
        boolean complete_instant = jEdit.getBooleanProperty( "sidekick.complete-instant.toggle", true );
        boolean complete_delay = jEdit.getBooleanProperty( "sidekick.complete-delay.toggle", true );
        boolean complete_on = complete_instant || complete_delay;
        if ( !complete_on && errorSource != null ) {
			handleErrors( errorSource, parser, buffer );
        }

        return parsedData;
    }

    // the parser accumulates errors as it parses.  This method passed them all to
    // the ErrorList plugin.
    private void handleErrors( DefaultErrorSource errorSource, TigerParser parser, Buffer buffer ) {
        /* only show errors for java files.  If the default edit mode is "java" then by default,
        the parser will be invoked by SideKick.  It's annoying to get parse error messages for
        files that aren't actually java files.  Do parse buffers that have yet to be saved, they
        might be java or javacc files eventually.  Otherwise, require a ".java" extension on
        the file. */
        if ( optionValues.getShowErrors() &&
				(!buffer.isDirty() || !optionValues.getIgnoreDirtyBuffers()) && 
				( ( buffer.getPath() == null || buffer.getPath().endsWith( ".java" ) ) || 
				buffer.getMode().getName().equals( "javacc" ) ) ) {
            errorSource.clear();
            for ( Iterator it = parser.getErrors().iterator(); it.hasNext(); ) {
                ErrorNode en = ( ErrorNode ) it.next();
                Exception e = en.getException();
                ParseException pe = null;
                Range range = new Range();
                if ( e instanceof ParseException ) {
                    pe = ( ParseException ) e;
                    range = getExceptionLocation( pe );
                }
				// This is a fix for hard tabs
				// Hard tabs mess up column counting, so this would occasionally cause out-of-index errors
				int tab = (Integer) buffer.getMode().getProperty("tabSize");
				String sub = "";
				for (int i = 0; i < tab; i++) {
					sub += " ";
				}
				String startLineText = buffer.getLineText(range.startLine);
				int index;
				while ((index = startLineText.indexOf("\t")) != -1) {
					startLineText = startLineText.replace("\t", sub);
					if (range.startColumn > index) range.startColumn -= (tab-1);
					if (range.endColumn > index) range.endColumn -= (tab-1);
				}
				// Add the error
                errorSource.addError( ErrorSource.ERROR, buffer.getPath(), range.startLine, range.startColumn, range.endColumn, e.getMessage() );
            }
        }
    }

    private void addChildren( DefaultMutableTreeNode node, Buffer buffer, ExpansionModel expansionModel ) {
        TigerNode parent = ( TigerNode ) node.getUserObject();
        List<TigerNode> children = parent.getChildren();
        if ( children != null && children.size() > 0 ) {

            // don't sort enum values, but do sort everything else
            if ( parent.getOrdinal() != TigerNode.ENUM ) {
                Collections.sort( children, nodeSorter );
            }

            // add the children as tree nodes
            for ( TigerNode child : children ) {
                child.setStart( ElementUtil.createStartPosition( buffer, child ) );
                child.setEnd( ElementUtil.createEndPosition( buffer, child ) );
                if ( canShow( child ) ) {
                    // update expansion model
                    int ordinal = child.getOrdinal();
                    if ( ordinal == TigerNode.ENUM ) {
                        // don't expand enum nodes
                        expansionModel.inc();
                    }
                    else if ( ordinal == TigerNode.CLASS && optionValues.getExpandClasses() ) {
                        // maybe expand inner classes, depends on option setting
                        expansionModel.add();
                    }
                    else if (ordinal == TigerNode.CONSTRUCTOR || ordinal == TigerNode.METHOD) {
                        // constructors and methods get an 'inc'
                        expansionModel.inc();
                    }

                    // create a tree node for the child and recursively add the childs children
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode( child );
                    node.add( childNode );
                    addChildren( childNode, buffer, expansionModel );
                }
                else {
                    // need to fill in start and end positions for code completion
                    setChildPositions( buffer, child );
                }
            }
        }
    }

    private void setChildPositions( Buffer buffer, TigerNode tn ) {
        for ( int i = 0; i < tn.getChildCount(); i++ ) {
            TigerNode child = tn.getChildAt( i );
            child.setStart( ElementUtil.createStartPosition( buffer, child ) );
            child.setEnd( ElementUtil.createEndPosition( buffer, child ) );
            setChildPositions( buffer, child );
        }
    }

    /**
     * @return attempts to return a Location indicating the location of a parser
     * exception.  If the ParseException contains a Token reference, all is well,
     * otherwise, this method attempts to parse the message string for the
     * exception.
     */
    private Range getExceptionLocation( ParseException pe ) {
        Token t = pe.currentToken;
        if ( t != null ) {
            return new Range( new Location( t.next.beginLine - 1, t.next.beginColumn ), new Location( t.next.endLine - 1, t.next.endColumn ) );
        }

        // ParseException message look like: "Parse error at line 116, column 5.  Encountered: }"
        try {
            Pattern p = Pattern.compile( "(.*?)(\\d+)(.*?)(\\d+)(.*?)" );
            Matcher m = p.matcher( pe.getMessage() );
			System.out.println("message: "+pe.getMessage());
            if ( m.matches() ) {
                String ln = m.group( 2 );
                String cn = m.group( 4 );
                int line_number = -1;
                int column_number = 0;
                if ( ln != null )
                    line_number = Integer.parseInt( ln );
                if ( cn != null )
                    column_number = Integer.parseInt( cn );
                return line_number > -1 ? new Range( new Location( line_number - 1, column_number - 1 ), new Location( line_number - 1, column_number ) ) : null;
            }
            return new Range();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return new Range();
        }
    }

    // single place to check the filter settings, that is, check to see if it
    // is okay to show a particular node
    private boolean canShow( TigerNode node ) {
        if ( !isVisible( node ) ) {      // visibility based on option settings
            return false;
        }
        if ( !node.isVisible() ) {        // visibility based on the node itself
            return false;
        }
        if ( node.getOrdinal() == TigerNode.BLOCK ) {
            return false;
        }
        if ( node.getOrdinal() == TigerNode.INITIALIZER ) {
            return optionValues.getShowInitializers();
        }
        if ( node.getOrdinal() == TigerNode.EXTENDS ) {
            return optionValues.getShowGeneralizations();
        }
        if ( node.getOrdinal() == TigerNode.IMPLEMENTS ) {
            return optionValues.getShowGeneralizations();
        }
        if ( node.getOrdinal() == TigerNode.FIELD && optionValues.getShowFields() ) {
            if ( ( ( FieldNode ) node ).isPrimitive() ) {
                return optionValues.getShowPrimitives();
            }
            return true;
        }
        if ( node.getOrdinal() == TigerNode.VARIABLE ) {
            return optionValues.getShowVariables();
        }
        if ( node.getOrdinal() == TigerNode.THROWS ) {
            return optionValues.getShowThrows();
        }
        return true;
    }

    // check if a node should be visible based on the 'top level' or 'member visible' settings
    private boolean isVisible( TigerNode tn ) {
        if ( ( tn.getOrdinal() == TigerNode.CLASS || tn.getOrdinal() == TigerNode.INTERFACE ) && tn.getParent() != null && tn.getParent().getOrdinal() == TigerNode.COMPILATION_UNIT ) {
            int visible_level = optionValues.getTopLevelVisIndex();
            switch ( visible_level ) {
                case MutableModifier.TOPLEVEL_VIS_PUBLIC:
                    return ModifierSet.isPublic( tn.getModifiers() );

                case MutableModifier.TOPLEVEL_VIS_PACKAGE:
                    return true;
            }
        }

        int visible_level = optionValues.getMemberVisIndex();
        switch ( visible_level ) {
            case MutableModifier.MEMBER_VIS_PACKAGE:
                return ModifierSet.isPackage( tn.getModifiers() ) || ModifierSet.isProtected( tn.getModifiers() ) || ModifierSet.isPublic( tn.getModifiers() );
            case MutableModifier.MEMBER_VIS_PROTECTED:
                return ModifierSet.isProtected( tn.getModifiers() ) || ModifierSet.isPublic( tn.getModifiers() );
            case MutableModifier.MEMBER_VIS_PUBLIC:
                return ModifierSet.isPublic( tn.getModifiers() );
            default:
                return true;
        }
    }

    private Comparator<TigerNode> nodeSorter =
        new Comparator<TigerNode>() {
            /**
             * Compares a TigerNode to another TigerNode for sorting. 
             * @param tna A TigerNode to compare.
             * @param tnb A TigerNode to compare.
             * @return a negative integer, zero, or a positive integer as the first TigerNode is
             * less than, equal to, or greater than the second TigerNode.
             */
            public int compare( TigerNode tna, TigerNode tnb ) {
                int sortBy = optionValues.getSortBy();
                switch ( sortBy ) {     // NOPMD, no breaks are necessary here
                    case OptionValues.SORT_BY_LINE:
                        Integer my_line = new Integer( tna.getStartLocation().line );
                        Integer other_line = new Integer( tnb.getStartLocation().line );
                        return my_line.compareTo( other_line );
                    case OptionValues.SORT_BY_VISIBILITY:
                        Integer my_vis = new Integer( ModifierSet.visibilityRank( tna.getModifiers() ) );
                        Integer other_vis = new Integer( ModifierSet.visibilityRank( tnb.getModifiers() ) );
                        int comp = my_vis.compareTo( other_vis );
                        return comp == 0 ? compareNames( tna, tnb ) : comp;
                    case OptionValues.SORT_BY_NAME:
                    default:
                        return compareNames( tna, tnb );
                }
            }

            private int compareNames( TigerNode tna, TigerNode tnb ) {
                // sort by name
                Integer my_ordinal = new Integer( tna.getOrdinal() );
                Integer other_ordinal = new Integer( tnb.getOrdinal() );
                int comp = my_ordinal.compareTo( other_ordinal );
                return comp == 0 ? tna.getName().toLowerCase().compareTo( tnb.getName().toLowerCase() ) : comp;
            }
        };


    /**
     * @return true, this parser does support code completion
     */
    public boolean supportsCompletion() {
        return true;
    }


    public SideKickCompletion complete( EditPane editPane, int caret ) {
        if ( completionFinder == null )
            completionFinder = new JavaCompletionFinder();
        return completionFinder.complete( editPane, caret );
    }

    public JPanel getPanel() {
        return new JavaModeToolBar( this );
    }
}
