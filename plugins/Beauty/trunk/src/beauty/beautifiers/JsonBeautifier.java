package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.json.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class JsonBeautifier extends Beautifier {

    private static JSONParser parser = null;

    public String beautify(String text) throws ParserException {
        try {
            // set up the parser
            StringReader input = new StringReader( text );
            ANTLRInputStream antlrInput = new ANTLRInputStream( input );
            JSONLexer lexer = new JSONLexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            JSONParser jsonParser = new JSONParser( tokens );

            // add an error handler that stops beautifying on any parsing error
            jsonParser.setErrorHandler(new JSONErrorHandler());
            
            // parse and beautify the buffer contents
            ParseTree tree = jsonParser.json();
            ParseTreeWalker walker = new ParseTreeWalker();
            JSONBeautyListener listener = new JSONBeautyListener(text.length());
            walker.walk( listener, tree );

            return listener.getText();
            
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}
