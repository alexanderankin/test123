package gatchan.phpparser;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The option pane of the PHPParserPlugin.
 * You will set the files you want to parse here
 *
 * @author Matthieu Casanova
 */
public class PHPParserOptionPane extends AbstractOptionPane {
  private JCheckBox shortOpenTag;
  private JCheckBox forEndFor;
  private JCheckBox switchEndSwitch;
  private JCheckBox ifEndSwitch;
  private JCheckBox whileEndWhile;
  public static final String PROP_WARN_SHORT_OPENTAG = "gatchan.phpparser.warnings.shortOpenTag";
  public static final String PROP_WARN_FORENDFOR = "gatchan.phpparser.warnings.forEndFor";
  public static final String PROP_WARN_SWITCHENDSWITCH = "gatchan.phpparser.warnings.switchEndSwitch";
  public static final String PROP_WARN_IFENDIF = "gatchan.phpparser.warnings.ifEndSwitch";
  public static final String PROP_WARN_WHILEENDWHILE = "gatchan.phpparser.warnings.whileEndWhile";

  public PHPParserOptionPane() {
    super("gatchan.phpparser.files");
  }

  protected void _init() {
    JLabel warningLabels = new JLabel("Warnings");

    shortOpenTag = new JCheckBox("'<?' used instead of '<?php'");
    shortOpenTag.setSelected(jEdit.getBooleanProperty(PROP_WARN_SHORT_OPENTAG));

    forEndFor= new JCheckBox("report for() : endfor; syntax");
    forEndFor.setSelected(jEdit.getBooleanProperty(PROP_WARN_FORENDFOR));

    switchEndSwitch= new JCheckBox("report switch() : switch; syntax");
    switchEndSwitch.setSelected(jEdit.getBooleanProperty(PROP_WARN_SWITCHENDSWITCH));

    ifEndSwitch= new JCheckBox("report if() : endif; syntax");
    ifEndSwitch.setSelected(jEdit.getBooleanProperty("gatchan.phpparser.warnings.ifEndSwitch"));

    whileEndWhile= new JCheckBox("report while() : endwhile; syntax");
    whileEndWhile.setSelected(jEdit.getBooleanProperty(PROP_WARN_WHILEENDWHILE));
    addComponent(warningLabels);
    addComponent(shortOpenTag);
    addComponent(forEndFor);
    addComponent(switchEndSwitch);
    addComponent(ifEndSwitch);
    addComponent(whileEndWhile);
  }

  protected void _save() {
    jEdit.setBooleanProperty(PROP_WARN_SHORT_OPENTAG, shortOpenTag.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_FORENDFOR, forEndFor.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_SWITCHENDSWITCH, switchEndSwitch.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_IFENDIF, ifEndSwitch.isSelected());
    jEdit.setBooleanProperty(PROP_WARN_WHILEENDWHILE, whileEndWhile.isSelected());
  }
}
