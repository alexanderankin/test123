package android;

import javax.swing.JLabel;

import common.gui.FileTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/** Option pane for Android plugin */
public class OptionPane extends AbstractOptionPane {

    private FileTextField sdkPathField;
    private FileTextField antPathField;
    private String oldSdkPath;
    private String oldAntPath;

    public OptionPane() {
        super("android");
    }

    protected void _init() {
        oldSdkPath = jEdit.getProperty("android.sdk.path", "");
        oldAntPath = jEdit.getProperty("android.ant.path", "");

        addComponent(new JLabel(jEdit.getProperty("options.android.instructions")));
        
        sdkPathField = new FileTextField(oldSdkPath, false);
        addComponent(jEdit.getProperty("options.android.sdk.path"), sdkPathField);

        antPathField = new FileTextField(oldAntPath, false);
        addComponent(jEdit.getProperty("options.android.ant.path"), antPathField);

    }

    protected void _save() {
        String newPath = sdkPathField.getTextField().getText();
        if (newPath != oldSdkPath) {
            jEdit.setProperty("android.sdk.path", newPath);
        }
        newPath = antPathField.getTextField().getText();
        if (newPath != oldAntPath) {
            jEdit.setProperty("android.ant.path", newPath);
        }
    }

}

