package gatchan.phpparser;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.parser.PHPParseErrorEvent;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParserListener;
import gatchan.phpparser.parser.PHPParser;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

/**
 * The PHP Error source that will receive the
 * errors from the parser and give them to the ErrorList api.
 *
 * @author Matthieu Casanova
 */
public final class PHPErrorSource implements PHPParserListener, EBComponent {

  private DefaultErrorSource errorSource;

  private boolean shortOpenTagWarning;

  /**
   * Instantiate the PHP error source.
   */
  public PHPErrorSource(DefaultErrorSource errorSource) {
    this.errorSource = errorSource;
    propertiesChanged();
    EditBus.addToBus(this);
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
    Log.log(Log.DEBUG, PHPErrorSource.class, "warn " + e.getMessage()+" "+e.getMessageClass()+" "+!shortOpenTagWarning+" "+(e.getMessageClass() == PHPParser.MESSAGE_SHORT_OPEN_TAG));
    if (!shortOpenTagWarning && e.getMessageClass() == PHPParser.MESSAGE_SHORT_OPEN_TAG) {
      Log.log(Log.DEBUG, PHPErrorSource.class, "return ");
      return;
    }
    errorSource.addError(ErrorSource.WARNING,
                         e.getPath(),
                         e.getBeginLine() - 1,
                         e.getBeginColumn() - 1,
                         e.getEndColumn(),
                         e.getMessage());
  }

  public void handleMessage(EBMessage message) {
    if (message instanceof PropertiesChanged) {
      propertiesChanged();
    }
  }

  private void propertiesChanged() {
    boolean shortOpenTagWarning = jEdit.getBooleanProperty("gatchan.phpparser.warnings.shortOpenTag");
    Log.log(Log.DEBUG, PHPErrorSource.class, "hoho " + shortOpenTagWarning);
    if (this.shortOpenTagWarning != shortOpenTagWarning)    {
      this.shortOpenTagWarning = shortOpenTagWarning;
    }
  }

  protected void finalize() throws Throwable {
    EditBus.removeFromBus(this);
  }
}
