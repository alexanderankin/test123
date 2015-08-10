package beauty.beautifiers;

import java.io.*;

import beauty.parsers.html.*;
import beauty.parsers.ParserException;


/**
 * Format an html document.
 * How this all works:
 * <ol>
 * <li> the parser is created using javacc and HtmlParser.jj.
 * <li> the <code>beautify</code> method passed some text to the
 * parser.
 * <li> the parser creates an HtmlDocument from the text.  The HtmlDocument
 * is basically a flat list of all tags and text in the document.
 * <li> the document is processed by an HtmlCollector, which matches up start
 * tags with end tags.
 * <li> the document is processed by an HtmlScrubber, which cleans up excess
 * whitespace.
 * <li> the document is processed by an HtmlFormatter, which does indenting and
 * line wrapping as appropriate.
 * </ol>
 */
public class HtmlFormat extends Beautifier {

    public String beautify( String text ) throws ParserException {
        StringReader reader = new StringReader( text );
        HtmlFormatter formatter = null;
        try {
            HtmlParser parser = new HtmlParser( reader );
            ///parser.setLineSeparator(getLineSeparator());
            HtmlDocument document = parser.HtmlDocument();
            document.setLineSeparator( getLineSeparator() );
            document.accept( new HtmlCollector() );
            int scrubber_params = HtmlScrubber.TAGS_DOWNCASE | HtmlScrubber.ATTR_DOWNCASE | HtmlScrubber.TRIM_SPACES;
            if ("jsp".equals(getEditMode())) {
                scrubber_params = HtmlScrubber.TRIM_SPACES;       
            }
            document.accept( new HtmlScrubber( scrubber_params ) );
            formatter = new HtmlFormatter();
            if ( getWrapMode().equals( "none" ) ) {
                formatter.setRightMargin( Integer.MAX_VALUE );
            }
            else {
                formatter.setRightMargin( getWrapMargin() );
            }

            formatter.setLineSeparator( getLineSeparator() );
            formatter.setIndent( getIndentWidth() );
            document.accept( formatter );
        }
        catch ( Exception e ) {
            throw new ParserException(e);
        }
        finally {
            reader.close();
        }

        return formatter != null ? formatter.toString() : text;
    }
}