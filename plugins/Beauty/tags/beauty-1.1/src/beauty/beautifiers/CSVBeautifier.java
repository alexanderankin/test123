package beauty.beautifiers;

import java.io.*;
import java.util.*;

import beauty.parsers.ParserException;
import beauty.parsers.csv.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class CSVBeautifier extends Beautifier {

    private static CSVParser parser = null;

    public String beautify(String text) throws ParserException {
        try {
            // set up the parser
            StringReader input = new StringReader( text );
            CharStream antlrInput = CharStreams.fromReader(input);
            CSVLexer lexer = new CSVLexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            CSVParser csvParser = new CSVParser( tokens );

            // add an error handler that stops beautifying on any parsing error
            csvParser.setErrorHandler(new CSVErrorHandler());
            
            // parse and beautify the buffer contents
            ParseTree tree = csvParser.csvFile();
            ParseTreeWalker walker = new ParseTreeWalker();
            CSVBeautyListener listener = new CSVBeautyListener();
            walker.walk( listener, tree );

            return listener.getText();
            
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}
