package cppcheck;

import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {

	public static final String OPTION = Plugin.OPTION;
	public static final String MESSAGE = Plugin.MESSAGE;
	public static final String PATH = OPTION + "cppcheck";
	public static final String FORCE = OPTION + "force";
	public static final String INCLUDE = OPTION + "include";
	private JTextField path;
	private JCheckBox force;
	private JTextField include;

	public OptionPane() {
		super("CppCheck");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		path = new JTextField(getPath(), 40);
		addComponent(jEdit.getProperty(MESSAGE + "path"), path);
		force = new JCheckBox(jEdit.getProperty(MESSAGE + "force"), isForce());
		addComponent(force);
		include = new JTextField(getInclude(), 40);
		addComponent(jEdit.getProperty(MESSAGE + "include"), include);
	}

	@Override
	public void _save() {
		jEdit.setProperty(PATH, path.getText());
		jEdit.setBooleanProperty(FORCE, force.isSelected());
		jEdit.setProperty(INCLUDE, include.getText());
	}

	public static String getPath()
	{
		String s = jEdit.getProperty(PATH);
		if ((s == null) || (s.length() == 0))
			return "cppcheck";
		if (s.contains(" ") && (! s.startsWith("\"")))
			s = "\"" + s + "\"";
		return s;
	}

	public static boolean isForce()
	{
		return jEdit.getBooleanProperty(FORCE);
	}

	public static String getInclude()
	{
		return jEdit.getProperty(INCLUDE);
	}

	public static void addArgs(Vector<String> args)
	{
		if (isForce())
			args.add("-f");
		String incPath = getInclude();
		if (incPath != null)
		{
			String [] paths = incPath.split("[:;]");
			for (String path: paths)
			{
				if (path.matches("^\\s*$"))
					continue;
				args.add("-I");
				args.add(path);
			}
		}
	}
}
