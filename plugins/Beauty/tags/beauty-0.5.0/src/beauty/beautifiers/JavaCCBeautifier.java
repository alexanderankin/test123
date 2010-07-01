package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.javacc.*;


public class JavaCCBeautifier extends Beautifier {

    private static JavaCCParser parser = null;

    public String beautify( String text ) throws ParserException {
        try {
            // protect unicode escaped character sequences
            text = text.replaceAll("\\\\u", "\\\\\\\\u");

            // set up the parser            
            StringReader is = new StringReader( text );
            if ( parser == null ) {
                parser = new JavaCCParser( is );
            }
            else {
                parser.ReInit( is );
            }
            
            // do the parse
            parser.parse();
            
            // restore the unicode sequences
            text = parser.getText();
            text = text.replaceAll("\\\\\\\\u", "\\\\u");
            
            return text;
        }
        catch ( Exception e ) {
            throw new ParserException(e);
        }
    }
}
