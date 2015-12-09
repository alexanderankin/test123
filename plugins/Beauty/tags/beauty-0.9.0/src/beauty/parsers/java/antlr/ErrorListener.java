package beauty.parsers.java.antlr;

import org.antlr.v4.runtime.*;

import java.util.*;
import beauty.parsers.ParserException;


public class ErrorListener extends BaseErrorListener {

    private List<ParserException> errors = new ArrayList<ParserException>();

    public List<ParserException> getErrors() {
        return errors;
    }

    @Override
    public void syntaxError( Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e ) {
        int length = 0;
        if ( e != null && e.getOffendingToken() != null ) {
            int startOffset = e.getOffendingToken().getStartIndex();
            int endOffset = e.getOffendingToken().getStopIndex();
            length = endOffset - startOffset;
        }
        ParserException pe = new ParserException( msg, line - 1, charPositionInLine, length);
        errors.add( pe );
    }
}