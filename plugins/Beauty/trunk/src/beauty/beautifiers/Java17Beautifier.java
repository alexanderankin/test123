
package beauty.beautifiers;

import beauty.parsers.ErrorListener;
import beauty.parsers.ParserException;
import beauty.parsers.java.java.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.*;
import org.gjt.sp.jedit.jEdit;

// This is a newer parser based on Antlr and supports java 17 language.
public class Java17Beautifier extends Beautifier {
    private int bracketStyle = 1;    // JavaParser.ATTACHED;

    private boolean breakElse = false;

    private boolean padParens = false;

    private boolean padOperators = false;

    private int blankLinesBeforePackage = 0;

    private int blankLinesAfterPackage = 1;

    private int blankLinesAfterImports = 2;

    private boolean sortImports = true;

    private boolean groupImports = true;

    private int blankLinesBetweenImportGroups = 1;

    private int blankLinesAfterClassBody = 2;

    private int blankLinesBeforeMethods = 1;

    private int blankLinesAfterMethods = 1;

    private boolean sortModifiers = true;

    private int collapseMultipleBlankLinesTo = 1;

    private int wrapLongLineLength = 120;

    public void init() {
        bracketStyle = jEdit.getIntegerProperty( "beauty.java.bracketStyle", JavaParserBeautyListener.ATTACHED );
        breakElse = jEdit.getBooleanProperty( "beauty.java.breakElse", true );
        padParens = jEdit.getBooleanProperty( "beauty.java.padParens", false );
        padOperators = jEdit.getBooleanProperty( "beauty.java.padOperators", false );
        blankLinesBeforePackage = jEdit.getIntegerProperty( "beauty.java8.blankLinesBeforePackage", 0 );
        blankLinesAfterPackage = jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterPackage", 1 );
        blankLinesAfterImports = jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterImports", 2 );
        sortImports = jEdit.getBooleanProperty( "beauty.java8.sortImports", true );
        groupImports = jEdit.getBooleanProperty( "beauty.java8.groupImports", true );
        blankLinesBetweenImportGroups = jEdit.getIntegerProperty( "beauty.java8.blankLinesBetweenImportGroups", 1 );
        blankLinesAfterClassBody = jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterClassBody", 2 );
        blankLinesBeforeMethods = jEdit.getIntegerProperty( "beauty.java8.blankLinesBeforeMethods", 1 );
        blankLinesAfterMethods = jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterMethods", 1 );
        sortModifiers = jEdit.getBooleanProperty( "beauty.java8.sortModifiers", true );
        collapseMultipleBlankLinesTo = jEdit.getIntegerProperty( "beauty.java8.collapseMultipleBlankLinesTo", 1 );
        wrapLongLineLength = jEdit.getIntegerProperty( "beauty.java8.wrapLongLineLength", 120 );
    }


    public String beautify( String text ) throws ParserException {
        ErrorListener errorListener = null;
        try {
            // set up the parser
            StringReader input = new StringReader( text );

            CharStream antlrInput = CharStreams.fromReader( input );

            JavaLexer lexer = new JavaLexer( antlrInput );

            CommonTokenStream tokens = new CommonTokenStream( lexer );

            JavaParser javaParser = new JavaParser( tokens );


            // for debugging
            String trace = System.getProperty( "beauty.java.trace" );
            javaParser.setTrace( "true".equals( trace ) );


            // set up the formatting options
            JavaParserBeautyListener listener = new JavaParserBeautyListener( text.length() + 2048, tokens );
            listener.setIndentWidth( getIndentWidth() );
            listener.setUseSoftTabs( getUseSoftTabs() );
            listener.setBracketStyle( bracketStyle );
            listener.setBreakElse( breakElse );
            listener.setPadParens( padParens );
            listener.setPadOperators( padOperators );
            listener.setBlankLinesBeforePackage( blankLinesBeforePackage );
            listener.setBlankLinesAfterPackage( blankLinesAfterPackage );
            listener.setBlankLinesAfterImports( blankLinesAfterImports );
            listener.setSortImports( sortImports );
            listener.setGroupImports( groupImports );
            listener.setBlankLinesBetweenImportGroups( blankLinesBetweenImportGroups );
            listener.setBlankLinesAfterClassBody( blankLinesAfterClassBody );
            listener.setBlankLinesBeforeMethods( blankLinesBeforeMethods );
            listener.setBlankLinesAfterMethods( blankLinesAfterMethods );
            listener.setSortModifiers( sortModifiers );
            listener.setCollapseMultipleBlankLinesTo( collapseMultipleBlankLinesTo );
            listener.setWrapLongLineLength( wrapLongLineLength );
            // add an error listener to the parser to capture any real errors
            javaParser.removeErrorListeners();
            errorListener = new ErrorListener();
            javaParser.addErrorListener( errorListener );
            javaParser.setErrorHandler( new DefaultErrorStrategy() );


            // parse the buffer text
            ParseTreeWalker walker = new ParseTreeWalker();

            ParseTree tree = javaParser.compilationUnit();
            walker.walk( listener, tree );
            return listener.getText();
        }

        catch (  Exception e ) {
            java.util.List<ParserException> errors = errorListener.getErrors();

            if ( errors != null && errors.size() > 0 ) {
                throw errors.get( 0 );
            }
            else {
                throw new ParserException( e );
            }
        }
    }


    public void setBracketStyle( int style ) {
        bracketStyle = style;
    }


    public void setBreakElse( boolean b ) {
        breakElse = b;
    }


    public void setPadParens( boolean pad ) {
        padParens = pad;
    }

}
