package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.css.*;

import org.gjt.sp.jedit.jEdit;

public class CSSBeautifier extends Beautifier {

    private static CSS3Parser parser = null;

    public String beautify(String text) throws ParserException {
        try {
            // set up the parser
            StringReader is = new StringReader(text);
            if (parser == null) {
                parser = new CSS3Parser(is);
            } else {
                parser.ReInit(is);
                parser.resetTokenSource();
            }

            // set the parser settings
            parser.setIndentWidth(getIndentWidth());
            parser.setTabSize(getTabWidth());
            parser.setLineSeparator(getLineSeparator());
            parser.setPadCombinators(jEdit.getBooleanProperty("beauty.css.padCombinators", true));
            parser.setPadAttribs(jEdit.getBooleanProperty("beauty.css.padAttribs", true));
            parser.setPadOperators(jEdit.getBooleanProperty("beauty.css.padOperators", true));
            parser.setPadPrio(jEdit.getBooleanProperty("beauty.css.padPrio", true));
            parser.setPadTerm(jEdit.getBooleanProperty("beauty.css.padTerm", true));
            parser.setPadParams(jEdit.getBooleanProperty("beauty.css.padParams", true));
            parser.setPadSelectors(jEdit.getBooleanProperty("beauty.css.padSelectors", true));
            parser.setInitialIndentLevel(initialLevel);
 
            // do the parse
            parser.parse();
            return parser.getText();
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}
