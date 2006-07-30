package xmlindenter;


import beauty.beautifiers.Beautifier;
import beauty.parsers.ParserException;

/**
 * danson, Added to integrate with Beauty plugin.
 */
public class XmlBeautifier extends Beautifier {
    
    public String beautify(String text) throws ParserException {
        try {
            return XmlIndenterPlugin.indent(text, getIndentWidth(), !getUseSoftTabs());
        }
        catch(Exception e) {
            throw new ParserException(e);   
        }
    }
}
