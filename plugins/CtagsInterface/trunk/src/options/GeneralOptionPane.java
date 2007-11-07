package options;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import ctags.CtagsInterfacePlugin;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane {

	static public final String OPTION = CtagsInterfacePlugin.OPTION;
	static public final String MESSAGE = CtagsInterfacePlugin.MESSAGE;
	static public final String CTAGS = OPTION + "ctags";
	static public final String CMD = OPTION + "cmd";
	static public final String PATTERN = OPTION + "pattern";
	static public final String UPDATE_ON_LOAD = OPTION + "updateOnLoad";
	static public final String UPDATE_ON_SAVE = OPTION + "updateOnSave";
	static public final String BACKGROUND = OPTION + "background";
	static public final String PREVIEW_WRAP = OPTION + "previewWrap";
	JTextField ctags;
	JTextField cmd;
	JTextField pattern;
	JCheckBox updateOnLoad;
	JCheckBox updateOnSave;
	JCheckBox background;
	JCheckBox previewWrap;
	
	public GeneralOptionPane() {
		super("CtagsInterface-General");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		ctags = new JTextField(jEdit.getProperty(CTAGS), 40);
		addComponent(jEdit.getProperty(MESSAGE + "ctags"), ctags);

		cmd = new JTextField(jEdit.getProperty(CMD), 40);
		addComponent(jEdit.getProperty(MESSAGE + "cmd"), cmd);
		
		pattern = new JTextField(jEdit.getProperty(PATTERN), 40);
		addComponent(jEdit.getProperty(MESSAGE + "pattern"), pattern);
		
		updateOnLoad = new JCheckBox(jEdit.getProperty(MESSAGE + "updateOnLoad"),
			jEdit.getBooleanProperty(UPDATE_ON_LOAD));
		addComponent(updateOnLoad);
		updateOnSave = new JCheckBox(jEdit.getProperty(MESSAGE + "updateOnSave"),
			jEdit.getBooleanProperty(UPDATE_ON_SAVE));
		addComponent(updateOnSave);
		
		background = new JCheckBox(jEdit.getProperty(MESSAGE + "background"),
			jEdit.getBooleanProperty(BACKGROUND));
		addComponent(background);

		previewWrap = new JCheckBox(jEdit.getProperty(MESSAGE + "previewWrap"),
				jEdit.getBooleanProperty(PREVIEW_WRAP));
		addComponent(previewWrap);
	}

	@Override
	public void _save() {
		jEdit.setProperty(CTAGS, ctags.getText());
		jEdit.setProperty(CMD, cmd.getText());
		jEdit.setProperty(PATTERN, pattern.getText());
		jEdit.setBooleanProperty(UPDATE_ON_LOAD, updateOnLoad.isSelected());
		jEdit.setBooleanProperty(UPDATE_ON_SAVE, updateOnSave.isSelected());
		jEdit.setBooleanProperty(BACKGROUND, background.isSelected());
		jEdit.setBooleanProperty(PREVIEW_WRAP, previewWrap.isSelected());
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
	public static String getPattern() {
		String s = jEdit.getProperty(PATTERN);
		if (s == null)
			return "";
		return s;
	}
	public static boolean getUpdateOnSave() {
		return jEdit.getBooleanProperty(UPDATE_ON_SAVE, true);
	}
	public static boolean getUpdateOnLoad() {
		return jEdit.getBooleanProperty(UPDATE_ON_LOAD, true);
	}
	public static boolean getUpdateInBackground() {
		return jEdit.getBooleanProperty(BACKGROUND, true);
	}
	public static boolean getPreviewWrap() {
		return jEdit.getBooleanProperty(PREVIEW_WRAP, true);
	}
}
