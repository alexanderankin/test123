package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.java.*;

import org.gjt.sp.jedit.jEdit;

public class JavaBeautifier extends Beautifier {

    private static JavaParser parser = null;
    
    private int bracketStyle = JavaParser.ATTACHED;
    private boolean breakElse = false;
    private boolean padParens = false;

    
    public void init() {
        bracketStyle = jEdit.getIntegerProperty("beauty.java.bracketStyle", JavaParser.ATTACHED);
        breakElse = jEdit.getBooleanProperty("beauty.java.breakElse", false);
        padParens = jEdit.getBooleanProperty("beauty.java.padParens", false);
    }
    
    public String beautify( String text ) throws ParserException {
        //System.out.println(text);
        try {
            // protect unicode escaped character sequences
            //text = text.replaceAll("\\\\u", "\\\\\\\\u");

            // set up the parser
            StringReader is = new StringReader( text );
            if ( parser == null ) {
                parser = new JavaParser( is );
            }
            else {
                parser.ReInit( is );
                parser.resetTokenSource();
            }

            // set the parser settings
            parser.setIndentWidth(getIndentWidth());
            parser.setTabSize(getTabWidth());
            parser.setLineSeparator(getLineSeparator());
            parser.setBracketStyle(bracketStyle);
            parser.setBreakElse(breakElse);
            parser.setPadParens(padParens);

            // do the parse
            parser.parse();

            // restore the unicode sequences
            text = parser.getText();
            //text = text.replaceAll("\\\\\\\\u", "\\\\u");

            return text;
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
