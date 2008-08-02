package qt4jedit;

import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class Qt4jEditOptions extends AbstractOptionPane {
	
	public JTextField pathToAssistantField;
	String oldPath;
	
	public Qt4jEditOptions() {
		super("Qt4jEdit");
	}

	protected void _init() {
		oldPath = jEdit.getProperty("qt4jedit.path-to-assistant", "assistant");
		pathToAssistantField = new JTextField(oldPath);
		addComponent(jEdit.getProperty("options.qt4jedit.path-to-assistant"), pathToAssistantField);
	}

	protected void _save() {
		String newPath = pathToAssistantField.getText();
		if (newPath != oldPath) {
			jEdit.setProperty("qt4jedit.path-to-assistant", newPath);
		}
	}
}

