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
 * The PHP Error source that will receive the errors from the parser and give them to the ErrorList api.
 *
 * @author Matthieu Casanova
 */
public final class PHPErrorSource implements PHPParserListener, EBComponent {

  private DefaultErrorSource errorSource;

  private boolean shortOpenTagWarning;
  private boolean forEndFor;
  private boolean whileEndWhile;
  private boolean ifEndIf;
  private boolean switchEndSwitch;
  private boolean foreachEndForeach;

  /** Instantiate the PHP error source. */
  public PHPErrorSource(DefaultErrorSource errorSource) {
    Log.log(Log.DEBUG, PHPErrorSource.class, "New PHPErrorSource");
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
    if ((!shortOpenTagWarning && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_SHORT_OPEN_TAG) ||
        (!forEndFor && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_FOR_ENDFOR_TAG) ||
        (!ifEndIf && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_IF_ENDIF_TAG) ||
        (!switchEndSwitch && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_SWITCH_ENDSWITCH_TAG) ||
        (!foreachEndForeach && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_FOREACH_ENDFOREACH_TAG) ||
        (!whileEndWhile && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_WHILE_ENDWHILE_TAG)) {
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
    boolean shortOpenTagWarning = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_SHORT_OPENTAG);
    if (this.shortOpenTagWarning != shortOpenTagWarning) {
      this.shortOpenTagWarning = shortOpenTagWarning;
    }
    boolean forEndFor = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_FORENDFOR);
    if (this.forEndFor != forEndFor) {
      this.forEndFor = forEndFor;
    }
    boolean ifEndIf = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_IFENDIF);
    if (this.ifEndIf != ifEndIf) {
      this.ifEndIf = ifEndIf;
    }
    boolean whileEndWhile = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_WHILEENDWHILE);
    if (this.whileEndWhile != whileEndWhile) {
      this.whileEndWhile = whileEndWhile;
    }
    boolean switchEndSwitch = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_SWITCHENDSWITCH);
    if (this.switchEndSwitch != switchEndSwitch) {
      this.switchEndSwitch = switchEndSwitch;
    }
    boolean foreachEndForeach = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_FOREACHENDFOREACH);
    if (this.foreachEndForeach != foreachEndForeach) {
      this.foreachEndForeach = foreachEndForeach;
    }
  }

  protected void finalize() throws Throwable {
    Log.log(Log.DEBUG, PHPErrorSource.class, "Removing From BUS");
    EditBus.removeFromBus(this);
  }
}
