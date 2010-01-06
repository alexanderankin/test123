package cppcheck;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {

	public static final String OPTION = Plugin.OPTION;
	public static final String MESSAGE = Plugin.MESSAGE;
	public static final String PATH = OPTION + "cppcheck";
	private JTextField path;
	
	public OptionPane() {
		super("CppCheck");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		path = new JTextField(getPath(), 40);
		addComponent(jEdit.getProperty(MESSAGE + "path"), path);
	}

	@Override
	public void _save() {
		jEdit.setProperty(PATH, path.getText());
	}

	public static String getPath()
	{
		String s = jEdit.getProperty(PATH);
		if ((s == null) || (s.length() == 0))
			return "cppcheck";
		if (s.contains(" "))
			s = "\"" + s + "\"";
		return s;
	}
}
