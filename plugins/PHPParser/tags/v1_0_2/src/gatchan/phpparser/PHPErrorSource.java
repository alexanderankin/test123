package gatchan.phpparser;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.parser.PHPParseErrorEvent;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParserListener;

/**
 * The PHP Error source that will receive the
 * errors from the parser and give them to the ErrorList api.
 *
 * @author Matthieu Casanova
 */
public final class PHPErrorSource extends DefaultErrorSource implements PHPParserListener {

  /**
   * Instantiate the PHP error source.
   */
  public PHPErrorSource() {
    super("PHP Error Source");
  }

  public void parseError(final PHPParseErrorEvent e) {
    if (e.getBeginLine() != e.getEndLine()) {
      addError(ErrorSource.ERROR,
              e.getPath(),
              e.getBeginLine() - 1,
              e.getBeginColumn() - 1,
              e.getBeginColumn(),
              e.getMessage());
    } else {
      addError(ErrorSource.ERROR,
              e.getPath(),
              e.getBeginLine() - 1,
              e.getBeginColumn() - 1,
              e.getEndColumn(),
              e.getMessage());
    }
  }

  public void parseMessage(final PHPParseMessageEvent e) {
    addError(ErrorSource.WARNING,
            e.getPath(),
            e.getBeginLine() - 1,
            e.getBeginColumn() - 1,
            e.getEndColumn(),
            e.getMessage());
  }
}
