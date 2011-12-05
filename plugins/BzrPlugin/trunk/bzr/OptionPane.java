package bzr;

import common.gui.FileTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/** Option pane for BzrPlugin */
public class OptionPane extends AbstractOptionPane {

    public FileTextField bzrPathField;
    String oldPath;

    public OptionPane() {
        super("bzr");
    }

    protected void _init() {
        oldPath = jEdit.getProperty("bzr.path", "bzr");
        bzrPathField = new FileTextField(oldPath, false);
        addComponent(jEdit.getProperty("options.bzr.path"), bzrPathField);
    }

    protected void _save() {
        String newPath = bzrPathField.getTextField().getText();
        if (newPath != oldPath) {
            jEdit.setProperty("bzr.path", newPath);
        }
    }

}

