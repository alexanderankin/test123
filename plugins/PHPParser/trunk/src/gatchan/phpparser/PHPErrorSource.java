package gatchan.phpparser;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.parser.PHPParseErrorEvent;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParserListener;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * The PHP Error source that will receive the errors from the parser and give them to the ErrorList api.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public final class PHPErrorSource implements PHPParserListener {

  private DefaultErrorSource errorSource;

  private boolean shortOpenTagWarning;
  private boolean forEndFor;
  private boolean whileEndWhile;
  private boolean ifEndIf;
  private boolean switchEndSwitch;
  private boolean foreachEndForeach;
  private boolean unusedParameters;
  private boolean variableMayBeUnassigned;
  private boolean unnecessaryGlobal;
  private boolean caseSemicolon;
  private boolean deprecatedVarToken;
  private boolean conditionalExpressionCheck;
  private boolean methodFieldsNameCheck;
  private boolean phpClosingMissing;

  /** Instantiate the PHP error source. */
  public PHPErrorSource() {
    Log.log(Log.DEBUG, PHPErrorSource.class, "New PHPErrorSource");
    propertiesChanged();
  }

  public void setErrorSource(DefaultErrorSource errorSource) {
    this.errorSource = errorSource;
    propertiesChanged();
  }

  public void parseError(PHPParseErrorEvent e) {
    if (e.getBeginLine() == e.getEndLine()) {
      errorSource.addError(ErrorSource.ERROR,
                           e.getPath(),
                           e.getBeginLine() - 1,
                           e.getBeginColumn() - 1,
                           e.getEndColumn(),
                           e.getMessage());
    } else {
      errorSource.addError(ErrorSource.ERROR,
                           e.getPath(),
                           e.getBeginLine() - 1,
                           e.getBeginColumn() - 1,
                           e.getBeginColumn(),
                           e.getMessage());
    }
  }

  public void parseMessage(PHPParseMessageEvent e) {
    if ((!shortOpenTagWarning && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_SHORT_OPEN_TAG) ||
        (!forEndFor && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_FOR_ENDFOR_TAG) ||
        (!ifEndIf && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_IF_ENDIF_TAG) ||
        (!switchEndSwitch && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_SWITCH_ENDSWITCH_TAG) ||
        (!foreachEndForeach && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_FOREACH_ENDFOREACH_TAG) ||
        (!unusedParameters && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_UNUSED_PARAMETERS) ||
        (!variableMayBeUnassigned && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_VARIABLE_MAY_BE_UNASSIGNED) ||
        (!unnecessaryGlobal && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_UNNECESSARY_GLOBAL) ||
        (!caseSemicolon && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_CASE_SEMICOLON) ||
        (!deprecatedVarToken && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_DEPRECATED_VAR_TOKEN) ||
        (!conditionalExpressionCheck && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_CONDITIONAL_EXPRESSION_CHECK) ||
        (!methodFieldsNameCheck && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_METHOD_FIELD_WITH_SAME_NAME) ||
        (!phpClosingMissing && e.getMessageClass() == PHPParseMessageEvent.MESSAGE_PHP_CLOSING_MISSING) ||
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

  private void propertiesChanged() {
    shortOpenTagWarning = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_SHORT_OPENTAG);
    forEndFor = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_FORENDFOR);
    ifEndIf = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_IFENDIF);
    whileEndWhile = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_WHILEENDWHILE);
    switchEndSwitch = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_SWITCHENDSWITCH);
    foreachEndForeach = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_FOREACHENDFOREACH);
    unusedParameters = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_UNUSED_PARAMETERS);
    variableMayBeUnassigned = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED);
    unnecessaryGlobal = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_UNNECESSARY_GLOBAL);
    caseSemicolon = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_CASE_SEMICOLON);
    deprecatedVarToken = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_DEPRECATED_VAR_TOKEN);
    conditionalExpressionCheck = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_CONDITIONAL_EXPRESSION_CHECK);
    methodFieldsNameCheck = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_MESSAGE_METHOD_FIELD_WITH_SAME_NAME);
    phpClosingMissing = jEdit.getBooleanProperty(PHPParserOptionPane.PROP_WARN_MESSAGE_PHP_CLOSING_MISSING);
  }
}
