package sidekick.java.parser.antlr;

import org.antlr.v4.runtime.*;

import java.util.*;

import sidekick.java.node.ErrorNode;
import sidekick.java.parser.ParseException;

public class JavaSideKickErrorListener extends BaseErrorListener {

    private List<ErrorNode> exceptions = new ArrayList<ErrorNode>();

    @Override
    public void syntaxError( Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e ) {
        ParseException pe = new ParseException( "Parse error at line " + line + ", column " + charPositionInLine + ". " + msg );
        addException( pe );
    }
    
    private void addException( ParseException pe ) {
        ErrorNode en = new ErrorNode( pe );
        exceptions.add( en );
    }

    public List<ErrorNode> getErrors() {
        return exceptions;
    }
}
