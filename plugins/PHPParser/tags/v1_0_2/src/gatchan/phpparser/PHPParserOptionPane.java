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
  private JTextField filesToParse;
  private JCheckBox parserActivated;
  private JCheckBox parseOnLoad;
  private JCheckBox parseOnSave;

  public PHPParserOptionPane() {
    super("gatchan.phpparser.files");
  }

  protected void _init() {
    parserActivated = new JCheckBox(jEdit.getProperty("options.gatchan-phpparser.activateParser.label"));
    parserActivated.setSelected(jEdit.getBooleanProperty("gatchan.phpparser.activateParser"));
    filesToParse = new JTextField(jEdit.getProperty("gatchan.phpparser.files.glob"));
    parseOnLoad = new JCheckBox(jEdit.getProperty("options.gatchan-phpparser.parseOnLoad.label"));
    parseOnLoad.setSelected(jEdit.getBooleanProperty("gatchan.phpparser.parseOnLoad"));
    parseOnSave = new JCheckBox(jEdit.getProperty("options.gatchan-phpparser.parseOnSave.label"));
    parseOnSave.setSelected(jEdit.getBooleanProperty("gatchan.phpparser.parseOnSave"));


    parserActivated.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setParserOptionState();
      }
    });
    setParserOptionState();
    addComponent(parserActivated);
    addComponent(parseOnLoad);
    addComponent(parseOnSave);
    addComponent(jEdit.getProperty("options.gatchan-phpparser.fileschoose.label"), filesToParse);
  }

  private void setParserOptionState() {
    final boolean active = parserActivated.isSelected();
    if (active) {
      parseOnLoad.setEnabled(true);
      parseOnSave.setEnabled(true);
      filesToParse.setEnabled(true);
    } else {
      parseOnLoad.setEnabled(false);
      parseOnSave.setEnabled(false);
      filesToParse.setEnabled(false);
    }
  }


  protected void _save() {
    jEdit.setProperty("gatchan.phpparser.files.glob", filesToParse.getText());
    jEdit.setBooleanProperty("gatchan.phpparser.activateParser", parserActivated.isSelected());
    jEdit.setBooleanProperty("gatchan.phpparser.parseOnLoad", parseOnLoad.isSelected());
    jEdit.setBooleanProperty("gatchan.phpparser.parseOnSave", parseOnSave.isSelected());
  }
}
