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
public final class PHPErrorSource implements PHPParserListener {

  private DefaultErrorSource errorSource;

  /**
   * Instantiate the PHP error source.
   */
  public PHPErrorSource(DefaultErrorSource errorSource) {
    this.errorSource = errorSource;
  }

  public void parseError(final PHPParseErrorEvent e) {
    if (e.getBeginLine() != e.getEndLine()) {
      errorSource.addError(ErrorSource.ERROR,
                           e.getPath(),
                           e.getBeginLine() - 1,
                           e.getBeginColumn() - 1,
                           e.getBeginColumn(),
                           e.getMessage());
    } else {
      errorSource.addError(ErrorSource.ERROR,
                           e.getPath(),
                           e.getBeginLine() - 1,
                           e.getBeginColumn() - 1,
                           e.getEndColumn(),
                           e.getMessage());
    }
  }

  public void parseMessage(final PHPParseMessageEvent e) {
    errorSource.addError(ErrorSource.WARNING,
                         e.getPath(),
                         e.getBeginLine() - 1,
                         e.getBeginColumn() - 1,
                         e.getEndColumn(),
                         e.getMessage());
  }
}
