package git;


import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class OptionPane extends AbstractOptionPane {
	
	public JTextField gitPathField;
	String oldPath;
	
	public OptionPane() {
		super("Git");
	}

	protected void _init() {
		oldPath = jEdit.getProperty("git.path", "git");
		gitPathField = new JTextField(oldPath);
		addComponent(jEdit.getProperty("options.git.path"), gitPathField);
	}

	protected void _save() {
		String newPath = gitPathField.getText();
		if (newPath != oldPath) {
			jEdit.setProperty("git.path", newPath);
		}
	}
}

