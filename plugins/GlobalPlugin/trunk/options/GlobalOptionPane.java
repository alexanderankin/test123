package options;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import browser.GlobalPlugin;

@SuppressWarnings("serial")
public class GlobalOptionPane extends AbstractOptionPane {
	
	public static final String PREFIX = GlobalPlugin.OPTION_PREFIX;
	public static final String GLOBAL_PATH_OPTION = PREFIX + "global_path";
	public static final String GLOBAL_PATH_LABEL = GLOBAL_PATH_OPTION + ".label";
	public static final String IDENTIFIER_REGEXP_OPTION = PREFIX + "identifier_regexp";
	public static final String IDENTIFIER_REGEXP_LABEL= IDENTIFIER_REGEXP_OPTION + ".label";
	
	JTextField globalPathTF;
	JTextField identifierRegExpTF;
	
	public GlobalOptionPane() {
		super("GlobalPlugin");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		globalPathTF = new JTextField(jEdit.getProperty(GLOBAL_PATH_OPTION));
		addComponent(jEdit.getProperty(GLOBAL_PATH_LABEL), globalPathTF);
		identifierRegExpTF = new JTextField(jEdit.getProperty(IDENTIFIER_REGEXP_OPTION));
		addComponent(jEdit.getProperty(IDENTIFIER_REGEXP_LABEL), identifierRegExpTF);
	}
	public void save()
	{
		jEdit.setProperty(GLOBAL_PATH_OPTION, globalPathTF.getText());
		jEdit.setProperty(IDENTIFIER_REGEXP_OPTION, identifierRegExpTF.getText());
	}
	
}
