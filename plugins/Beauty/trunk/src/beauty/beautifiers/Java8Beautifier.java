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
        bracketStyle = jEdit.getIntegerProperty("beauty.java.bracketStyle", Java8BeautyListener.ATTACHED);
        breakElse = jEdit.getBooleanProperty("beauty.java.breakElse", true);
        padParens = jEdit.getBooleanProperty("beauty.java.padParens", false);
    }
    
    public String beautify( String text ) throws ParserException {
        //System.out.println(text);
        ErrorListener errorListener = null;
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
            javaParser.setTrace("true".equals(trace));
            
            // add an error listener to the parser to capture any errors
            javaParser.removeErrorListeners();
            errorListener = new ErrorListener();
            javaParser.addErrorListener( errorListener );

            ParseTree tree = javaParser.compilationUnit();
            ParseTreeWalker walker = new ParseTreeWalker();
            Java8BeautyListener listener = new Java8BeautyListener(text.length() + 2048, tokens);

            // set the formatting settings
            listener.setIndentWidth(getIndentWidth());
            listener.setUseSoftTabs(getUseSoftTabs());
            listener.setBracketStyle(bracketStyle);
            listener.setBreakElse(breakElse);
            listener.setPadParens(padParens);

            // parse and beautify the buffer contents
            walker.walk( listener, tree );

            return listener.getText();
        }
        catch ( Exception e ) {
            List<ParserException> errors = errorListener.getErrors();
            if (errors != null && errors.size() > 0) {
                throw errors.get(0);   
            }
            else {
                throw new ParserException(e);   
            }
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
