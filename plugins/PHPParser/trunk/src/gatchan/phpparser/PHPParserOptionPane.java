package gatchan.phpparser;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;

/**
 * The option pane of the PHPParserPlugin. You will set the files you want to parse here
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

  public static final String PROP_WARN_SHORT_OPENTAG = "gatchan.phpparser.warnings.shortOpenTag";
  public static final String PROP_WARN_FORENDFOR = "gatchan.phpparser.warnings.forEndFor";
  public static final String PROP_WARN_SWITCHENDSWITCH = "gatchan.phpparser.warnings.switchEndSwitch";
  public static final String PROP_WARN_IFENDIF = "gatchan.phpparser.warnings.ifEndSwitch";
  public static final String PROP_WARN_WHILEENDWHILE = "gatchan.phpparser.warnings.whileEndWhile";
  public static final String PROP_WARN_FOREACHENDFOREACH = "gatchan.phpparser.warnings.foreachEndForeach";
  public static final String PROP_WARN_UNUSED_PARAMETERS = "gatchan.phpparser.warnings.methodanalysis.unusedParameters";
  public static final String PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED = "gatchan.phpparser.warnings.methodanalysis.unassignedVariable";
  public static final String PROP_WARN_UNNECESSARY_GLOBAL = "gatchan.phpparser.warnings.methodanalysis.unnecessaryGlobal";

  /** Instantiate the option pane of the PHP Parser. */
  public PHPParserOptionPane() {
    super("gatchan.phpparser.files");
  }

  /** Initialize the form. This method is automatically called by jEdit */
  protected void _init() {
    final JLabel warningLabels = new JLabel("Warnings");

    shortOpenTag = new JCheckBox("'<?' used instead of '<?php'");
    shortOpenTag.setSelected(jEdit.getBooleanProperty(PROP_WARN_SHORT_OPENTAG));

    forEndFor = new JCheckBox("report for() : endfor; syntax");
    forEndFor.setSelected(jEdit.getBooleanProperty(PROP_WARN_FORENDFOR));

    switchEndSwitch = new JCheckBox("report switch() : switch; syntax");
    switchEndSwitch.setSelected(jEdit.getBooleanProperty(PROP_WARN_SWITCHENDSWITCH));

    ifEndSwitch = new JCheckBox("report if() : endif; syntax");
    ifEndSwitch.setSelected(jEdit.getBooleanProperty("gatchan.phpparser.warnings.ifEndSwitch"));

    whileEndWhile = new JCheckBox("report while() : endwhile; syntax");
    whileEndWhile.setSelected(jEdit.getBooleanProperty(PROP_WARN_WHILEENDWHILE));

    foreachEndForeach = new JCheckBox("report foreach() : endforeach; syntax");
    foreachEndForeach.setSelected(jEdit.getBooleanProperty(PROP_WARN_FOREACHENDFOREACH));

    final JLabel methodAnalysisLabel = new JLabel("Method analysis");
    unusedParameter = new JCheckBox("unused parameters");
    unusedParameter.setSelected(jEdit.getBooleanProperty(PROP_WARN_UNUSED_PARAMETERS));

    unassignedVariable = new JCheckBox("variable may be unassigned");
    unassignedVariable.setSelected(jEdit.getBooleanProperty(PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED));

    unnecessaryGlobal = new JCheckBox("variable may be unassigned");
    unnecessaryGlobal.setSelected(jEdit.getBooleanProperty(PROP_WARN_UNNECESSARY_GLOBAL));

    addComponent(warningLabels);
    addComponent(shortOpenTag);
    addComponent(forEndFor);
    addComponent(switchEndSwitch);
    addComponent(ifEndSwitch);
    addComponent(whileEndWhile);
    addComponent(foreachEndForeach);

    addComponent(methodAnalysisLabel);
    addComponent(unusedParameter);
    addComponent(unassignedVariable);
    addComponent(unnecessaryGlobal);
  }

  /** Save the properties. This method is automatically called by jEdit */
  protected void _save() {
    jEdit.setBooleanProperty(PROP_WARN_SHORT_OPENTAG, shortOpenTag.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_FORENDFOR, forEndFor.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_SWITCHENDSWITCH, switchEndSwitch.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_IFENDIF, ifEndSwitch.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_WHILEENDWHILE, whileEndWhile.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_FOREACHENDFOREACH, foreachEndForeach.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_UNUSED_PARAMETERS, unusedParameter.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_VARIABLE_MAY_BE_UNASSIGNED, unassignedVariable.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_UNNECESSARY_GLOBAL, unnecessaryGlobal.isSelected());
  }
}
