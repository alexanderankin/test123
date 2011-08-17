package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.jsp.*;

import org.gjt.sp.jedit.jEdit;

// TODO: wrap attributes isn't right, last attribute and end tag do not line up
// correctly with the rest of the tag.  If there is only one attribute, don't
// put it on a separate line.
public class JspBeautifier extends Beautifier {

    private static JspParser parser = null;

    public String beautify(String text) throws ParserException {
        try {
            // protect unicode escaped character sequences
            //text = text.replaceAll("\\\\u", "\\\\\\\\u");
 
            // set up the parser
            StringReader is = new StringReader(text);
            if (parser == null) {
                parser = new JspParser(is);
            } else {
                parser.ReInit(is);
                parser.resetTokenSource();
            }

            // set the parser settings
            parser.setIndentWidth(getIndentWidth());
            parser.setTabSize(getTabWidth());
            parser.setLineSeparator(getLineSeparator());
            parser.setPadSlashEnd(jEdit.getBooleanProperty("beauty.jsp.padSlashEnd", false));
            parser.setPadTagEnd(jEdit.getBooleanProperty("beauty.jsp.padTagEnd", false));
            parser.setWrapAttributes(jEdit.getBooleanProperty("beauty.jsp.wrapAttributes", false));
            parser.setCollapseBlankLines(jEdit.getBooleanProperty("beauty.jsp.collapseBlankLines", true));

            // do the parse
            parser.parse();

            // restore the unicode sequences
            text = parser.getText();
            //text = text.replaceAll("\\\\\\\\u", "\\\\u");
 
            return text;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}
