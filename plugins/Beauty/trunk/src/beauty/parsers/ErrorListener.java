package beauty.parsers;


import java.util.*;

import org.antlr.v4.runtime.*;


public class ErrorListener extends BaseErrorListener {
    private List<ParserException> errors = new ArrayList<ParserException>();

    public List<ParserException> getErrors() {
        return errors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        int length = 0;

        if (e != null && e.getOffendingToken() != null) {
            int startOffset = e.getOffendingToken().getStartIndex();
            int endOffset = e.getOffendingToken().getStopIndex();
            length = endOffset - startOffset;
        }
        ParserException pe = new ParserException(wrapLongLine(msg), line, charPositionInLine, length);
        errors.add(pe);
    }

    /**
     * The antlr output is sometimes very verbose, so this method
     * takes a long line as input, splits it into lines of no more than80 characters.
     */
    private String wrapLongLine(String s) {
        StringTokenizer st = new StringTokenizer(s, " ");
        StringBuilder sb = new StringBuilder(s.length());
        sb.append('\n');    // it's a long line so start it on a new line
        int lineLength = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (lineLength + token.length() > 80) {
                sb.append('\n');
                lineLength = 0;
            }
            sb.append(token).append(' ');
            lineLength += token.length();
        }
        return sb.toString();
    }

    
}
