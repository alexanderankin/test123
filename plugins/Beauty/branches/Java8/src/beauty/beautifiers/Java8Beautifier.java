package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.java.antlr.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.gjt.sp.jedit.jEdit;

public class Java8Beautifier extends Beautifier {

    private int bracketStyle = 1;   // JavaParser.ATTACHED;
    private boolean breakElse = false;
    private boolean padParens = false;

    
    public void init() {
        /*
        bracketStyle = jEdit.getIntegerProperty("beauty.java.bracketStyle", JavaParser.ATTACHED);
        breakElse = jEdit.getBooleanProperty("beauty.java.breakElse", false);
        padParens = jEdit.getBooleanProperty("beauty.java.padParens", false);
        */
    }
    
    public String beautify( String text ) throws ParserException {
        //System.out.println(text);
        try {
            // protect unicode escaped character sequences
            //text = text.replaceAll("\\\\u", "\\\\\\\\u");

            // set up the parser
            StringReader input = new StringReader( text );
            ANTLRInputStream antlrInput = new ANTLRInputStream( input );
            Java8Lexer lexer = new Java8Lexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            Java8Parser javaParser = new Java8Parser( tokens );
            
            // for debugging
            String trace = System.getProperty("beauty.java8.trace");
            javaParser.setTrace(true);
            
            // set the parser settings
            /*
            parser.setIndentWidth(getIndentWidth());
            parser.setTabSize(getTabWidth());
            parser.setLineSeparator(getLineSeparator());
            parser.setBracketStyle(bracketStyle);
            parser.setBreakElse(breakElse);
            parser.setPadParens(padParens);
            */
            
            // parse and beautify the buffer contents
            ParseTree tree = javaParser.compilationUnit();
            ParseTreeWalker walker = new ParseTreeWalker();
            Java8BeautyListener listener = new Java8BeautyListener(16 * 1024, softTabs, tabWidth, tokens);
            walker.walk( listener, tree );

            return listener.getText();
        }
        catch ( Exception e ) {
            throw new ParserException(e);
        }
    }
    
    public void setBracketStyle(int style) {
        bracketStyle = style;   
    }
    
    public void setBreakElse(boolean b) {
        breakElse = b;   
    }
    
    public void setPadParens(boolean pad) {
        padParens = pad;   
    }

}
