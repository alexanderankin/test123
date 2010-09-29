package bzr;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;

/** Option pane for BzrPlugin */
public class OptionPane extends AbstractOptionPane {

    public JTextField bzrPathField;
    String oldPath;

    public OptionPane() {
        super("Bzr");
    }

    protected void _init() {
        oldPath = jEdit.getProperty("bzr.path", "bzr");
        bzrPathField = new JTextField(oldPath);
        JPanel comp = new JPanel();
        comp.setLayout(new BoxLayout(comp, BoxLayout.X_AXIS));
        comp.add(bzrPathField);
        JButton browse = new JButton(
            jEdit.getProperty("options.bzr.browsebutton-label"));
        browse.addActionListener(new BrowseHandler());
        comp.add(browse);
        addComponent(jEdit.getProperty("options.bzr.path"), comp);
    }

    protected void _save() {
        String newPath = bzrPathField.getText();
        if (newPath != oldPath) {
            jEdit.setProperty("bzr.path", newPath);
        }
    }

    class BrowseHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            VFSFileChooserDialog dialog = new VFSFileChooserDialog(
                jEdit.getActiveView(), bzrPathField.getText(),
                VFSBrowser.OPEN_DIALOG, false, true);
            String[] files = dialog.getSelectedFiles();
            if (files != null) {
                bzrPathField.setText(files[0]);
            }
        }
    }
}

