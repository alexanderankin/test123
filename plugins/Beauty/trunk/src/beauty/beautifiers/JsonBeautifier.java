package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.json.*;

public class JsonBeautifier extends Beautifier {

    private static JsonParser parser = null;

    public String beautify(String text) throws ParserException {
        try {
            // set up the parser
            StringReader is = new StringReader(text);
            if (parser == null) {
                parser = new JsonParser(is);
            } else {
                parser.ReInit(is);
                parser.resetTokenSource();
            }

            // set the parser settings
            parser.setIndentWidth(getIndentWidth());
            parser.setTabSize(getTabWidth());
            parser.setLineSeparator(getLineSeparator());
 
            // do the parse
            parser.parse();
            return parser.getText();
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}
