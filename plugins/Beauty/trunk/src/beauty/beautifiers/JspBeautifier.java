package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.jsp.*;

public class JspBeautifier extends Beautifier {

    private static JspParser parser = null;

    public String beautify( String text ) throws ParserException {
        //System.out.println(text);
        try {
            // protect unicode escaped character sequences
            //text = text.replaceAll("\\\\u", "\\\\\\\\u");

            // set up the parser
            StringReader is = new StringReader( text );
            if ( parser == null ) {
                parser = new JspParser( is );
            }
            else {
                parser.ReInit( is );
                parser.resetTokenSource();
            }

            // set the parser settings
            parser.setIndentWidth(getIndentWidth());
            parser.setTabSize(getTabWidth());
            parser.setLineSeparator(getLineSeparator());

            // do the parse
            parser.CompilationUnit();

            // restore the unicode sequences
            text = parser.getText();
            //text = text.replaceAll("\\\\\\\\u", "\\\\u");

            return text;
        }
        catch ( Exception e ) {
            throw new ParserException(e);
        }
    }

}
