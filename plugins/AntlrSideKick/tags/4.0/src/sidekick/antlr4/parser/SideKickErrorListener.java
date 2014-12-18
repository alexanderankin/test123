package sidekick.antlr4.parser;

import org.antlr.v4.runtime.*;

import java.util.*;

public class SideKickErrorListener extends BaseErrorListener {

    private List<ParseException> errors = new ArrayList<ParseException>();

    public List<ParseException> getErrors() {
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
        ParseException pe = new ParseException( msg, line - 1, charPositionInLine, length );
        errors.add( pe );
    }
}