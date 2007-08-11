package options;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import browser.GlobalPlugin;

@SuppressWarnings("serial")
public class GlobalOptionPane extends AbstractOptionPane {
	
	public static final String OPTION_PREFIX = GlobalPlugin.OPTION_PREFIX;
	public static final String GLOBAL_PATH_OPTION = OPTION_PREFIX + "global_path";
	public static final String GLOBAL_PATH_LABEL = GLOBAL_PATH_OPTION + ".label";
	
	JTextField globalPathTF;
	
	public GlobalOptionPane() {
		super("GlobalPlugin");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		globalPathTF = new JTextField(jEdit.getProperty(GLOBAL_PATH_OPTION));
		addComponent(jEdit.getProperty(GLOBAL_PATH_LABEL), globalPathTF);
	}
	public void save()
	{
		jEdit.setProperty(GLOBAL_PATH_OPTION, globalPathTF.getText());
	}
	
}
