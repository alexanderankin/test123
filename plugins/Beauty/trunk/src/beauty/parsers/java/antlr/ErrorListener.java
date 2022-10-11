
package beauty.parsers.java.antlr;

import beauty.parsers.ParserException;

import java.util.*;

import org.antlr.v4.runtime.*;

public class ErrorListener extends BaseErrorListener {

    private List<ParserException> errors = new ArrayList<ParserException>();

    public List<ParserException> getErrors() {
        return errors;
    }

    public <T extends Token> void syntaxError( Recognizer<T, ?> recognizer, T offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e ) {
        int length = 0;
        if ( e != null && e.getOffendingToken() != null ) {
            int startOffset = e.getOffendingToken().getStartIndex();
            int endOffset = e.getOffendingToken().getStopIndex();
            length = endOffset - startOffset;
        }

        ParserException pe = new ParserException( msg, line, charPositionInLine, length );
        errors.add( pe );
    }
}

