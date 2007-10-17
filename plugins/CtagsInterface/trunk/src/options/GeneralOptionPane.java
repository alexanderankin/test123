package options;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane {

	static public final String OPTION = "options.CtagsInterface.";
	static public final String MESSAGE = "messages.CtagsInterface.";
	static public final String CTAGS = OPTION + "ctags";
	static public final String CMD = OPTION + "cmd";
	JTextField ctags;
	JTextField cmd;
	
	public GeneralOptionPane() {
		super("CtagsInterface-General");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		ctags = new JTextField(jEdit.getProperty(CTAGS), 40);
		addComponent(jEdit.getProperty(MESSAGE + "ctags"), ctags);

		cmd = new JTextField(jEdit.getProperty(CMD), 40);
		addComponent(jEdit.getProperty(MESSAGE + "cmd"), cmd);
	}

	@Override
	public void _save() {
		jEdit.setProperty(CTAGS, ctags.getText());
		jEdit.setProperty(CMD, cmd.getText());
	}

	public static String getCtags() {
		String s = jEdit.getProperty(CTAGS);
		if (s == null || s.length() == 0)
			return "ctags";
		return s;
	}
	public static String getCmd() {
		String s = jEdit.getProperty(CMD);
		if (s == null)
			return "";
		return s;
	}
}
