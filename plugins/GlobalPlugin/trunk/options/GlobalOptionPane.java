package options;

import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
	public static final String JUMP_IMMEDIATELY_OPTION = PREFIX + "jump_immediately";
	public static final String JUMP_IMMEDIATELY_LABEL = JUMP_IMMEDIATELY_OPTION + ".label";
	public static final String AUTO_UPDATE_DB_OPTION = PREFIX + "auto_update_db";
	public static final String AUTO_UPDATE_DB_LABEL = AUTO_UPDATE_DB_OPTION + ".label";
	public static final String AUTO_UPDATE_ON_SAVE_OPTION = PREFIX + "auto_update_on_save";
	public static final String AUTO_UPDATE_ON_SAVE_LABEL = AUTO_UPDATE_ON_SAVE_OPTION + ".label";
	public static final String AUTO_UPDATE_PERIODICALLY_OPTION = PREFIX + "auto_update_periodically";
	public static final String AUTO_UPDATE_PERIODICALLY_LABEL = AUTO_UPDATE_PERIODICALLY_OPTION + ".label";
	public static final String AUTO_UPDATE_SECONDS_OPTION = PREFIX + "auto_update_seconds";
	
	JTextField globalPathTF;
	JTextField identifierRegExpTF;
	JCheckBox jumpImmediatelyCB;
	JCheckBox autoUpdateDatabaseCB;
	JRadioButton autoUpdateOnSave;
	JRadioButton autoUpdatePeriodically;
	JTextField autoUpdateSeconds;
	
	public GlobalOptionPane() {
		super("GlobalPlugin");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		globalPathTF = new JTextField(jEdit.getProperty(GLOBAL_PATH_OPTION));
		addComponent(jEdit.getProperty(GLOBAL_PATH_LABEL), globalPathTF);
		identifierRegExpTF = new JTextField(jEdit.getProperty(IDENTIFIER_REGEXP_OPTION));
		addComponent(jEdit.getProperty(IDENTIFIER_REGEXP_LABEL), identifierRegExpTF);
		jumpImmediatelyCB = new JCheckBox(
			jEdit.getProperty(JUMP_IMMEDIATELY_LABEL),
			jEdit.getBooleanProperty(JUMP_IMMEDIATELY_OPTION));
		addComponent(jumpImmediatelyCB);
		autoUpdateDatabaseCB = new JCheckBox(
			jEdit.getProperty(AUTO_UPDATE_DB_LABEL),
			jEdit.getBooleanProperty(AUTO_UPDATE_DB_OPTION));
		addComponent(autoUpdateDatabaseCB);
		JPanel onSave = new JPanel();
		addComponent(onSave);
		autoUpdateOnSave = new JRadioButton(
			jEdit.getProperty(AUTO_UPDATE_ON_SAVE_LABEL),
			jEdit.getBooleanProperty(AUTO_UPDATE_ON_SAVE_OPTION));
		onSave.add(autoUpdateOnSave);
		JPanel periodic = new JPanel();
		addComponent(periodic);
		autoUpdatePeriodically = new JRadioButton(
			jEdit.getProperty(AUTO_UPDATE_PERIODICALLY_LABEL),
			jEdit.getBooleanProperty(AUTO_UPDATE_PERIODICALLY_OPTION));
		periodic.add(autoUpdatePeriodically);
		autoUpdateSeconds = new JTextField(String.valueOf(
			getAutoUpdateSeconds()));
		autoUpdateSeconds.setInputVerifier(new InputVerifier() {
			public boolean verify(JComponent input) {
				return autoUpdateSeconds.getText().matches("\\d+");
			}
		});
		periodic.add(autoUpdateSeconds);
		ButtonGroup autoUpdateGroup = new ButtonGroup();
		autoUpdateGroup.add(autoUpdateOnSave);
		autoUpdateGroup.add(autoUpdatePeriodically);
	}
	public void _save()
	{
		jEdit.setProperty(GLOBAL_PATH_OPTION, globalPathTF.getText());
		jEdit.setProperty(IDENTIFIER_REGEXP_OPTION, identifierRegExpTF.getText());
		jEdit.setBooleanProperty(JUMP_IMMEDIATELY_OPTION, jumpImmediatelyCB.isSelected());
		jEdit.setBooleanProperty(AUTO_UPDATE_DB_OPTION, autoUpdateDatabaseCB.isSelected());
		jEdit.setBooleanProperty(AUTO_UPDATE_ON_SAVE_OPTION, autoUpdateOnSave.isSelected());
		jEdit.setBooleanProperty(AUTO_UPDATE_PERIODICALLY_OPTION, autoUpdatePeriodically.isSelected());
		jEdit.setIntegerProperty(AUTO_UPDATE_SECONDS_OPTION,
			Integer.valueOf(autoUpdateSeconds.getText()).intValue());
	}
	
	static public boolean isJumpImmediately()
	{
		return jEdit.getBooleanProperty(JUMP_IMMEDIATELY_OPTION);
	}
	static public boolean isAutoUpdateDB()
	{
		return jEdit.getBooleanProperty(AUTO_UPDATE_DB_OPTION);
	}
	static public boolean isAutoUpdateOnSave()
	{
		return jEdit.getBooleanProperty(AUTO_UPDATE_ON_SAVE_OPTION);
	}
	static public boolean isAutoUpdatePeriodically()
	{
		return jEdit.getBooleanProperty(AUTO_UPDATE_PERIODICALLY_OPTION);
	}
	static public int getAutoUpdateSeconds()
	{
		return jEdit.getIntegerProperty(AUTO_UPDATE_SECONDS_OPTION, 180);
	}

}
