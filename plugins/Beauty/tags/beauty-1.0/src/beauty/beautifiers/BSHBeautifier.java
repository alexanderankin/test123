package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.bsh.*;

public class BSHBeautifier extends Beautifier {

    private static BSHParser parser = null;

    public String beautify( String text ) throws ParserException {
        //System.out.println(text);
        try {
            // protect unicode escaped character sequences
            //text = text.replaceAll("\\\\u", "\\\\\\\\u");

            // set up the parser
            StringReader is = new StringReader( text );
            if ( parser == null ) {
                parser = new BSHParser( is );
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

}
