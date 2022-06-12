package sidekick.antlr4.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.*;

import sidekick.util.ParseError;

public class SideKickErrorListener extends BaseErrorListener {

    private List<ParseError> errors = new ArrayList<ParseError>();

    public List<ParseError> getErrors() {
        return errors;
    }

    public <T extends Token> void syntaxError( Recognizer<T, ?> recognizer,
											  T offendingSymbol,
											  int line,
											  int charPositionInLine,
											  String msg,
											  RecognitionException e ) {
        int length = 0;
        if ( e != null && e.getOffendingToken() != null ) {
            int startOffset = e.getOffendingToken().getStartIndex();
            int endOffset = e.getOffendingToken().getStopIndex();
            length = endOffset - startOffset;
        }
        ParseError pe = new ParseError( msg, line - 1, charPositionInLine, length );
        errors.add( pe );
    }
}