package gatchan.phpparser;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;

/**
 * The option pane of the PHPParserPlugin.
 *
 * @author Matthieu Casanova
 */
public final class PHPParserOptionPane extends AbstractOptionPane {
  private JCheckBox shortOpenTag;
  private JCheckBox forEndFor;
  private JCheckBox switchEndSwitch;
  private JCheckBox ifEndSwitch;
  private JCheckBox whileEndWhile;
  private JCheckBox foreachEndForeach;

  private JCheckBox unusedParameter;
  private JCheckBox unassignedVariable;
  private JCheckBox unnecessaryGlobal;
  private JCheckBox caseSemicolon;
  private JCheckBox php5Enabled;
  private JCheckBox loadOnStartup;
  private JCheckBox deprecatedVarToken;
  private JCheckBox conditionalExpressionCheck;

  public static final String PROP_PHP5_SUPPORT = "gatchan.phpparser.php5support";
  public static final String PROP_WARN_SHORT_OPENTAG = "gatchan.phpparser.warnings.shortOpenTag";
  public static final String PROP_WARN_FORENDFOR = "gatchan.phpparser.warnings.forEndFor";
  public static final String PROP_WARN_SWITCHENDSWITCH = "gatchan.phpparser.warnings.switchEndSwitch";
  public static final String PROP_WARN_IFENDIF = "gatchan.phpparser.warnings.ifEndSwitch";
  public static final String PROP_WARN_WHILEENDWHILE = "gatchan.phpparser.warnings.whileEndWhile";
  public static final String PROP_WARN_FOREACHENDFOREACH = "gatchan.phpparser.warnings.foreachEndForeach";
  public static final String PROP_WARN_UNUSED_PARAMETERS = "gatchan.phpparser.warnings.methodanalysis.unusedParameters";
  public static final String PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED = "gatchan.phpparser.warnings.methodanalysis.unassignedVariable";
  public static final String PROP_WARN_CASE_SEMICOLON = "gatchan.phpparser.warnings.warnings.caseSemicolon";
  public static final String PROP_WARN_UNNECESSARY_GLOBAL = "gatchan.phpparser.warnings.methodanalysis.unnecessaryGlobal";
  public static final String PROP_WARN_DEPRECATED_VAR_TOKEN = "gatchan.phpparser.warnings.deprecatedphp4.varToken";
  public static final String PROP_WARN_CONDITIONAL_EXPRESSION_CHECK = "gatchan.phpparser.warnings.types.conditionalExpressionCheck";

  /** Instantiate the option pane of the PHP Parser. */
  public PHPParserOptionPane() {
    super("gatchan.phpparser.files");
  }

  /** Initialize the form. This method is automatically called by jEdit */
  protected void _init() {
    addComponent(loadOnStartup = new JCheckBox(jEdit.getProperty("options.gatchan.phpparser.loadOnStartup.text")));
    String startupMode = jEdit.getProperty("plugin.gatchan.phpparser.PHPParserPlugin.activate");
    loadOnStartup.setSelected("startup".equals(startupMode));

    addComponent(php5Enabled = createCheckBox(PROP_PHP5_SUPPORT));
    addComponent(new JLabel("Warnings"));
    addComponent(deprecatedVarToken = createCheckBox(PROP_WARN_DEPRECATED_VAR_TOKEN));
    addComponent(shortOpenTag = createCheckBox(PROP_WARN_SHORT_OPENTAG));
    addComponent(forEndFor = createCheckBox(PROP_WARN_FORENDFOR));
    addComponent(switchEndSwitch = createCheckBox(PROP_WARN_SWITCHENDSWITCH));
    addComponent(ifEndSwitch = createCheckBox(PROP_WARN_IFENDIF));
    addComponent(whileEndWhile = createCheckBox(PROP_WARN_WHILEENDWHILE));
    addComponent(foreachEndForeach = createCheckBox(PROP_WARN_FOREACHENDFOREACH));
    addComponent(caseSemicolon = createCheckBox(PROP_WARN_CASE_SEMICOLON));
    addComponent(conditionalExpressionCheck = createCheckBox(PROP_WARN_CONDITIONAL_EXPRESSION_CHECK));
    addComponent(new JLabel("Method analysis"));
    addComponent(unusedParameter = createCheckBox(PROP_WARN_UNUSED_PARAMETERS));
    addComponent(unassignedVariable = createCheckBox(PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED));
    addComponent(unnecessaryGlobal = createCheckBox(PROP_WARN_UNNECESSARY_GLOBAL));
  }

  private JCheckBox createCheckBox(String property) {
    JCheckBox checkbox = new JCheckBox(jEdit.getProperty(property + ".text"));
    checkbox.setSelected(jEdit.getBooleanProperty(property));
    return checkbox;
  }

  /** Save the properties. This method is automatically called by jEdit */
  protected void _save() {
    if (loadOnStartup.isSelected()) {
      jEdit.setProperty("plugin.gatchan.phpparser.PHPParserPlugin.activate", "startup");
    } else {
      jEdit.setProperty("plugin.gatchan.phpparser.PHPParserPlugin.activate", "defer");
    }
    jEdit.setBooleanProperty(PROP_PHP5_SUPPORT, php5Enabled.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_DEPRECATED_VAR_TOKEN, deprecatedVarToken.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_SHORT_OPENTAG, shortOpenTag.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_FORENDFOR, forEndFor.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_SWITCHENDSWITCH, switchEndSwitch.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_IFENDIF, ifEndSwitch.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_WHILEENDWHILE, whileEndWhile.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_FOREACHENDFOREACH, foreachEndForeach.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_UNUSED_PARAMETERS, unusedParameter.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED, unassignedVariable.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_UNNECESSARY_GLOBAL, unnecessaryGlobal.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_CASE_SEMICOLON, caseSemicolon.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_CONDITIONAL_EXPRESSION_CHECK, conditionalExpressionCheck.isSelected());
  }
}
