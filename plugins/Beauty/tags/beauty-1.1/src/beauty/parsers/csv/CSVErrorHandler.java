package beauty.parsers.csv;

import org.antlr.v4.runtime.*;

/**
 * Handler that stops beautifying on any parsing error.
 */
public class CSVErrorHandler extends DefaultErrorStrategy {
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        throw new RuntimeException(e);
    }

    @Override
    public Token recoverInline(Parser recognizer)
        throws RecognitionException
    {
        throw new RuntimeException(new InputMismatchException(recognizer));
    }

    @Override
    public void sync(Parser recognizer) { }
}
