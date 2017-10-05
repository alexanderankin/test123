/*
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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import sidekick.java.parser.antlr.*;

public class JavaParser extends SideKickParser implements EBComponent {

    // {{{ Private members
    private View currentView = null;
    private OptionValues optionValues = new OptionValues();
    private JavaCompletionFinder completionFinder = null;

    // which type of parser, java or javacc
    private int parser_type = JAVA_PARSER;
    // }}}

    // {{{ Static members
    public static final int JAVA_PARSER = 1;
    public static final int JAVACC_PARSER = 2;
    public static final int JAVA_8_PARSER = 3;
    public static final String COMPILATION_UNIT = "javasidekick.compilationUnit";
    // }}}

    // {{{ JavaParser()
    /**
     * Defaults to parsing java files.
     */
    public JavaParser() {
        this( JAVA_PARSER );
    }    // }}}

    // {{{ JavaParser(int)
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
                //boolean useAntlrParser = jEdit.getBooleanProperty("sidekick.java.useAntlrParser", false);
                //parser_type = useAntlrParser ? JAVA_8_PARSER : JAVA_PARSER;
                parser_type = JAVA_8_PARSER;
        }
    }    // }}}

    // {{{ activate(EditPane) : void
    /**
     * This method is called when a buffer using this parser is selected
     * in the specified view.
     * @param editPane The edit pane
     * @since SideKick 0.3.1
     */
    public void activate( EditPane editPane ) {
        super.activate( editPane );
        currentView = editPane.getView();
    }    // }}}

    // {{{ deactivate(EditPane) : void
    public void deactivate( EditPane editPane ) {
        completionFinder = null;
        super.deactivate( editPane );
    }    // }}}

    // {{{ handleMessage(EBMessage) : void
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
    }    // }}}
    
    public int getParserType() {
        return parser_type;   
    }

    // {{{ parse() : SideKickParsedData
    /**
     * Parse the current buffer in the current view.
     */
    public SideKickParsedData parse() {
        if ( currentView == null ) {
            currentView = jEdit.getActiveView();
        }
        return parse( currentView.getBuffer(), null );
    }    // }}}

    // {{{ parse(Buffer, DefaultErrorSource) : SideKickParsedData
    /**
     * Parse the contents of the given buffer.
     * @param buffer the buffer to parse
     */
    public SideKickParsedData parse( Buffer buffer, DefaultErrorSource errorSource ) {
        /// DONE: remove this when the Antlr parser is complete
        //boolean useAntlrParser = jEdit.getBooleanProperty("sidekick.java.useAntlrParser", false);
        if (parser_type != JAVACC_PARSER) {
            parser_type = JAVA_8_PARSER; //useAntlrParser ? JAVA_8_PARSER : JAVA_PARSER;   
        }
        ///
        
        Reader input = null;
        String filename = buffer.getPath();
        SideKickParsedData parsedData = new JavaSideKickParsedData( filename );
        DefaultMutableTreeNode root = parsedData.root;
        CUNode compilationUnit = null;
        List<sidekick.java.node.ErrorNode> errorList = null;
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
            String contents = buffer.getText( 0, buffer.getLength() );
            input = new StringReader( contents );
            int tab_size = buffer.getTabSize();

            switch ( parser_type ) {
                case JAVACC_PARSER:
                    // use the javacc parser for javacc files
                    TigerParser tigerParser = new TigerParser( input );
                    compilationUnit = tigerParser.getJavaCCRootNode( tab_size );
                    compilationUnit.setResults( tigerParser.getResults() );
                    errorList = tigerParser.getErrors();
                    break;
                default:    //case JAVA_8_PARSER:
                    // use the Antlr parser for java files
                    // set up the Antlr parser to read the buffer
                    input = new StringReader( contents );
                    ANTLRInputStream antlrInput = new ANTLRInputStream( input );
                    Java8Lexer lexer = new Java8Lexer( antlrInput );
                    CommonTokenStream tokens = new CommonTokenStream( lexer );
                    Java8Parser java8Parser = new Java8Parser( tokens );
                    
                    // for debugging, set "javasidekick.useTrace" to "true" as a
                    // jEdit property to turn on antlr parser trace. This can be 
                    // very verbose and grind jEdit to a halt.
                    java8Parser.setTrace("true".equals( jEdit.getProperty( "javasidekick.useTrace" ) ));
        
                    // add an error listener to the parser to capture any errors
                    java8Parser.removeErrorListeners();
                    ErrorListener errorListener = new ErrorListener();
                    java8Parser.addErrorListener( errorListener );
        
                    // parse the buffer contents
                    ParseTree tree = java8Parser.compilationUnit();
                    ParseTreeWalker walker = new ParseTreeWalker();
                    
                    // temporary property to be able to switch between Java 8 parser
                    // and Java 9 parser while Java 9 parser is work in progress
                    if ("true".equals(jEdit.getProperty("javasidekick.useJava9"))) {
                        Java9SideKickListener listener = new Java9SideKickListener();
                        walker.walk( listener, tree );
                        
                        // build the tree
                        compilationUnit = listener.getCompilationUnit();
                        compilationUnit.setResults( listener.getResults() );
                    }
                    else {
                        Java8SideKickListener listener = new Java8SideKickListener();
                        walker.walk( listener, tree );
                        
                        // build the tree
                        compilationUnit = listener.getCompilationUnit();
                        compilationUnit.setResults( listener.getResults() );
                    }
        
                    
                    // convert the errors, if any
                    List<ParserException> parserExceptions = errorListener.getErrors();
                    errorList = new ArrayList<sidekick.java.node.ErrorNode>();
                    for (ParserException pe : parserExceptions) {
                        sidekick.java.node.ErrorNode errorNode = new sidekick.java.node.ErrorNode(pe);
                        errorList.add(errorNode);
                    }
                    break;
                /*
                // DONE: remove this when the antlr parser is complete
                default:
                    // use the javacc parser for java files
                    TigerParser parser = new TigerParser( input );
                    compilationUnit = parser.getJavaRootNode( tab_size );
                    compilationUnit.setResults( parser.getResults() );
                    errorList = parser.getErrors();
                */
            }
            
            // for debugging, set "javasidekick.dump" to "true" as a jEdit
            // property. This will print out the CUNode and children.
            if ( "true".equals( jEdit.getProperty( "javasidekick.dump" ) ) ) {
                System.out.println( compilationUnit.dump() );
            }

            // compilationUnit is root node
            compilationUnit.setName( buffer.getName() );
            compilationUnit.setFilename( filename );
            compilationUnit.setStart( ElementUtil.createStartPosition( buffer, compilationUnit ) );
            compilationUnit.setEnd( ElementUtil.createEndPosition( buffer, compilationUnit ) );
            root.setUserObject( compilationUnit );
            buffer.setProperty( COMPILATION_UNIT, compilationUnit );

            ExpansionModel expansionModel = new ExpansionModel();
            expansionModel.add();            // cu
            parsedData.expansionModel = expansionModel.getModel();

            // maybe show imports, but don't expand them
            if ( optionValues.getShowImports() == true ) {
                ImportNode imports = compilationUnit.getImportNode();
                if (imports != null && imports.getChildCount() > 0) {
                    imports.setStart( ElementUtil.createStartPosition( buffer, imports ) );
                    imports.setEnd( ElementUtil.createEndPosition( buffer, imports ) );
                    DefaultMutableTreeNode importsNode = new DefaultMutableTreeNode(imports);
                    root.add(importsNode);
                    expansionModel.inc();
                    
                    for (TigerNode anImport : compilationUnit.getImportNodes()) {
                        anImport.setStart( ElementUtil.createStartPosition( buffer, anImport ) );
                        anImport.setEnd( ElementUtil.createEndPosition( buffer, anImport ) );
                        importsNode.add( new DefaultMutableTreeNode( anImport ) );
                    }
                }
            }

            // show constructors, fields, methods, etc
            addChildren( root, buffer, expansionModel );
        } catch ( Exception e ) {       // NOPMD
            // there can be a lot of exceptions thrown if parse on keystroke is
            // enabled for code completion.
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch ( Exception e ) {                // NOPMD
                // not to worry, StringReader won't actually throw an exception.
            }
        }

        // only handle errors when buffer is saved or code completion is off. Otherwise,
        // there will be a lot of spurious errors shown when code completion is on and the
        // user is in the middle of typing something.
        boolean complete_instant = jEdit.getBooleanProperty( "sidekick.complete-instant.toggle", true );
        boolean complete_delay = jEdit.getBooleanProperty( "sidekick.complete-delay.toggle", true );
        boolean complete_on = complete_instant || complete_delay;
        if ( ! complete_on && errorSource != null ) {
            handleErrors( errorSource, errorList, buffer );
        }
        return parsedData;
    }    // }}}

    // {{{ handleErrors(DefaultErrorSource, TigerParser, Buffer) : void
    /**
     * The parser accumulates errors as it parses.  This method passed them all to
     * the ErrorList plugin.
     */
    private void handleErrors( DefaultErrorSource errorSource, List<sidekick.java.node.ErrorNode> errorList, Buffer buffer ) {
        /* only show errors for java files.  If the default edit mode is "java" then by default,
        the parser will be invoked by SideKick.  It's annoying to get parse error messages for
        files that aren't actually java files.  Do parse buffers that have yet to be saved, they
        might be java or javacc files eventually.  Otherwise, require a ".java" extension on
        the file. */
        if ( optionValues.getShowErrors() && ( !buffer.isDirty() || !optionValues.getIgnoreDirtyBuffers() ) && ( ( buffer.getPath() == null || buffer.getPath().endsWith( ".java" ) ) || buffer.getMode().getName().equals( "javacc" ) ) ) {
            errorSource.clear();
            for ( Iterator it = errorList.iterator(); it.hasNext(); ) {
                sidekick.java.node.ErrorNode en = ( sidekick.java.node.ErrorNode ) it.next();
                Exception e = en.getException();
                Range range = new Range();
                if ( e instanceof ParseException ) {
                    ParseException pe = ( ParseException ) e;
                    range = getExceptionLocation( pe );
                }
                else if (e instanceof ParserException) {
                    ParserException pe = (ParserException) e;
                    range = getExceptionLocation( pe );
                }
                // This is a fix for hard tabs
                // Hard tabs mess up column counting, so this would occasionally cause out-of-index errors
                int tab = ( Integer ) buffer.getMode().getProperty( "tabSize" );
                StringBuilder sub = new StringBuilder();
                for ( int i = 0; i < tab; i++ ) {
                    sub.append( ' ' );
                }
                String startLineText = buffer.getLineText( range.startLine );
                int index;
                while ( ( index = startLineText.indexOf( '\t' ) ) != -1 ) {
                    startLineText = startLineText.replace( "\t", sub.toString() );
                    if ( range.startColumn > index ) {
                        range.startColumn -= ( tab - 1 );
                    }
                    if ( range.endColumn > index ) {
                        range.endColumn -= ( tab - 1 );
                    }
                }
                // Add the error
                errorSource.addError( ErrorSource.ERROR, buffer.getPath(), range.startLine, range.startColumn, range.endColumn, e.getMessage() );
            }
        }
    }    // }}}

    // {{{ addChildren(DefaultMutableTreeNode, Buffer, ExpansionModel) : void
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
                    } else if ( ordinal == TigerNode.CLASS && optionValues.getExpandClasses() ) {
                        // maybe expand inner classes, depends on option setting
                        expansionModel.add();
                    } else if ( ordinal == TigerNode.CONSTRUCTOR || ordinal == TigerNode.METHOD ) {
                        // constructors and methods get an 'inc'
                        expansionModel.inc();
                    }

                    // create a tree node for the child and recursively add the childs children
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode( child );
                    node.add( childNode );
                    addChildren( childNode, buffer, expansionModel );
                } else {
                    // need to fill in start and end positions for code completion
                    setChildPositions( buffer, child );
                }
            }
        }
    }    // }}}

    // {{{ setChildPositions(Buffer, TigerNode) : void
    private void setChildPositions( Buffer buffer, TigerNode tn ) {
        for ( int i = 0; i < tn.getChildCount(); i++ ) {
            TigerNode child = tn.getChildAt( i );
            child.setStart( ElementUtil.createStartPosition( buffer, child ) );
            child.setEnd( ElementUtil.createEndPosition( buffer, child ) );
            setChildPositions( buffer, child );
        }
        tn.setStart(ElementUtil.createStartPosition(buffer, tn));
        tn.setEnd(ElementUtil.createEndPosition(buffer, tn));
            
    }    // }}}

    // {{{ getExceptionLocation(ParseException) : Range
    /**
     * @return attempts to return a Location indicating the location of a parser
     * exception.  If the ParseException contains a Token reference, all is well,
     * otherwise, this method attempts to parse the message string for the
     * exception.
     */
    private Range getExceptionLocation( ParseException pe ) {
        sidekick.java.parser.Token t = pe.currentToken;
        if ( t != null ) {
            return new Range( new Location( t.next.beginLine - 1, t.next.beginColumn ), new Location( t.next.endLine - 1, t.next.endColumn ) );
        }

        // ParseException message look like: "Parse error at line 116, column 5.  Encountered: }"
        try {
            Pattern p = Pattern.compile( "(.*?)(\\d+)(.*?)(\\d+)(.*?)" );
            Matcher m = p.matcher( pe.getMessage() );
            if ( m.matches() ) {
                String ln = m.group( 2 );
                String cn = m.group( 4 );
                int line_number = -1;
                int column_number = 0;
                if ( ln != null ) {
                    line_number = Integer.parseInt( ln );
                }
                if ( cn != null ) {
                    column_number = Integer.parseInt( cn );
                }
                return line_number > -1 ? new Range( new Location( line_number - 1, column_number - 1 ), new Location( line_number - 1, column_number ) ) : null;
            }
            return new Range();
        } catch ( Exception e ) {
            e.printStackTrace();
            return new Range();
        }
    }    // }}}
    
    private Range getExceptionLocation( ParserException pe) {
        Location start = new Location(pe.getLineNumber(), pe.getColumn());
        Location end = new Location(pe.getLineNumber(), pe.getColumn() + pe.getLength());
        return new Range(start, end);
    }
    
    

    // {{{ canShow(TigerNode) : boolean
    // single place to check the filter settings, that is, check to see if it
    // is okay to show a particular node
    private boolean canShow( TigerNode node ) {
        if ( !isVisible( node ) ) {            // visibility based on option settings
            return false;
        }
        if ( !node.isVisible() ) {            // visibility based on the node itself
            return false;
        }
        if ( node.getOrdinal() == Integer.MAX_VALUE) {
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
    }    // }}}

    // {{{ isVisible(TigerNode) : boolean
    // check if a node should be visible based on the 'top level' or 'member visible' settings
    private boolean isVisible( TigerNode tn ) {
        if ( ( tn.getOrdinal() == TigerNode.CLASS || tn.getOrdinal() == TigerNode.INTERFACE || tn.getOrdinal() == TigerNode.MODULE) && 
               tn.getParent() != null && tn.getParent().getOrdinal() == TigerNode.COMPILATION_UNIT ) {
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
    }    // }}}

    private Comparator<TigerNode> nodeSorter = new Comparator<TigerNode>() {
        /**
         * Compares a TigerNode to another TigerNode for sorting.
         * @param tna A TigerNode to compare.
         * @param tnb A TigerNode to compare.
         * @return a negative integer, zero, or a positive integer as the first TigerNode is
         * less than, equal to, or greater than the second TigerNode.
         */
        public int compare( TigerNode tna, TigerNode tnb ) {
            int sortBy = optionValues.getSortBy();
            switch ( sortBy ) {                // NOPMD, no breaks are necessary here
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
        if ( completionFinder == null ) {
            completionFinder = new JavaCompletionFinder();
        }
        return completionFinder.complete( editPane, caret );
    }  

    public JPanel getPanel() {
        return new JavaModeToolBar( this );
    }  
}
