
package beauty.beautifiers;

import beauty.parsers.ErrorListener;
import beauty.parsers.ParserException;
import beauty.parsers.json.*;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.gjt.sp.jedit.jEdit;


public class JsonBeautifier extends Beautifier {

    public String beautify( String text ) throws ParserException {
        ErrorListener errorListener = null;
        try {

            // set up the parser
            StringReader input = new StringReader( text );
            CharStream antlrInput = CharStreams.fromReader( input );
            JSONLexer lexer = new JSONLexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            JSONParser jsonParser = new JSONParser( tokens );
            JSONBeautyListener listener = new JSONBeautyListener( getUseSoftTabs(), getTabWidth() );
            listener.setBracketStyle( jEdit.getIntegerProperty( "beauty.json.bracketStyle", JSONBeautyListener.BROKEN ) );

            // add an error handler that stops beautifying on any parsing error
            jsonParser.removeErrorListeners();
            errorListener = new ErrorListener();
            jsonParser.addErrorListener( errorListener );
            jsonParser.setErrorHandler( new DefaultErrorStrategy() );

            // parse and beautify the buffer contents
            ParseTreeWalker walker = new ParseTreeWalker();
            ParseTree tree = jsonParser.json();
            walker.walk( listener, tree );

            return listener.getText();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            java.util.List<ParserException> errors = errorListener.getErrors();

            if ( errors != null && errors.size() > 0 ) {
                StringBuilder sb = new StringBuilder();
                for (ParserException pe : errors) {
                    sb.append(pe.getMessage());
                }
                throw new ParserException(sb.toString());
            }
            else {
                throw new ParserException( e );
            }
        }
    }
}
