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

  public PHPParserOptionPane() {
    super("gatchan.phpparser.files");
  }

  protected void _init() {
    JLabel warningLabels = new JLabel("Warnings");

    shortOpenTag = new JCheckBox("'<?' used instead of '<?php'");
    shortOpenTag.setSelected(jEdit.getBooleanProperty("gatchan.phpparser.warnings.shortOpenTag"));
    addComponent(warningLabels);
    addComponent(shortOpenTag);
  }

  protected void _save() {
    jEdit.setBooleanProperty("gatchan.phpparser.warnings.shortOpenTag", shortOpenTag.isSelected());
  }
}
