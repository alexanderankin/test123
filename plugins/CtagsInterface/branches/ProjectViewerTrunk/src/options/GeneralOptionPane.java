package options;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import ctags.CtagsInterfacePlugin;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane {

	static private final String CUSTOM_DB = "Custom";
	static public final String OPTION = CtagsInterfacePlugin.OPTION;
	static public final String MESSAGE = CtagsInterfacePlugin.MESSAGE;
	static public final String CTAGS = OPTION + "ctags";
	static public final String CMD = OPTION + "cmd";
	static public final String PATTERN = OPTION + "pattern";
	static public final String UPDATE_ON_LOAD = OPTION + "updateOnLoad";
	static public final String UPDATE_ON_SAVE = OPTION + "updateOnSave";
	static public final String BACKGROUND = OPTION + "background";
	static public final String PREVIEW_TOOLBAR = OPTION + "previewToolbar";
	static public final String PREVIEW_WRAP = OPTION + "previewWrap";
	static public final String PREVIEW_DELAY = OPTION + "previewDelay";
	static public final String DB_SELECTED_PRESET = OPTION + "dbSelectedPreset";
	static public final String DB_PRESETS = OPTION + "dbPresets";
	static public final String DB_CLASS = OPTION + "dbClass";
	static public final String DB_CONNECTION = OPTION + "dbConnection";
	static public final String DB_USER = OPTION + "dbUser";
	static public final String DB_PASSWORD = OPTION + "dbPassword";
	JTextField ctags;
	JTextField cmd;
	JTextField pattern;
	JCheckBox updateOnLoad;
	JCheckBox updateOnSave;
	JCheckBox background;
	JCheckBox previewToolbar;
	JCheckBox previewWrap;
	JTextField previewDelay;
	JComboBox dbPreset;
	JTextField dbClass;
	JTextField dbConnection;
	JTextField dbUser;
	JTextField dbPassword;
	
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

		JPanel previewPanel = new JPanel();
		previewPanel.setLayout(new GridLayout(0, 1));
		previewPanel.setBorder(new TitledBorder(jEdit.getProperty(
			MESSAGE + "previewTitle")));
		previewToolbar = new JCheckBox(jEdit.getProperty(MESSAGE + "previewToolbar"),
				getPreviewToolbar());
		previewPanel.add(previewToolbar);
		previewWrap = new JCheckBox(jEdit.getProperty(MESSAGE + "previewWrap"),
				jEdit.getBooleanProperty(PREVIEW_WRAP));
		previewPanel.add(previewWrap);
		JPanel previewDelayPanel = new JPanel(new BorderLayout());
		previewDelayPanel.add(new JLabel(jEdit.getProperty(MESSAGE + "previewDelay")),
			BorderLayout.WEST);
		previewDelay = new JTextField(String.valueOf(
			jEdit.getIntegerProperty(PREVIEW_DELAY)), 5);
		previewDelay.setInputVerifier(new InputVerifier() {
			public boolean verify(JComponent c) {
				try {
					Integer.valueOf(previewDelay.getText());
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});
		previewDelayPanel.add(previewDelay, BorderLayout.EAST);
		previewPanel.add(previewDelayPanel);
		addComponent(previewPanel);

		JPanel dbPanel = new JPanel();
		dbPanel.setLayout(new GridBagLayout());
		dbPanel.setBorder(new TitledBorder(jEdit.getProperty(
			MESSAGE + "dbTitle")));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = c.gridy = 0;
		JPanel dbPresetPanel = new JPanel();
		dbPresetPanel.add(new JLabel(jEdit.getProperty(MESSAGE + "dbPreset")));
		Vector<String> presets = new Vector<String>();
		presets.add(CUSTOM_DB);
		String presetStr = jEdit.getProperty(DB_PRESETS);
		if ((presetStr != null) && (! presetStr.isEmpty())) {
			String [] items = presetStr.split(",");
			for (String item: items)
				presets.add(item);
		}
		dbPreset = new JComboBox(presets);
		dbPresetPanel.add(dbPreset);
		JButton savePreset = new JButton("Save");
		dbPresetPanel.add(savePreset);
		JButton savePresetAs = new JButton("Save as...");
		dbPresetPanel.add(savePresetAs);
		final JButton removePreset = new JButton("Remove");
		dbPresetPanel.add(removePreset);
		dbPanel.add(dbPresetPanel, c);
		JPanel dbClassPanel = new JPanel();
		dbClassPanel.add(new JLabel(jEdit.getProperty(MESSAGE + "dbClass")));
		dbClass = new JTextField(jEdit.getProperty(DB_CLASS), 40);
		dbClassPanel.add(dbClass);
		c.gridy++;
		dbPanel.add(dbClassPanel, c);
		JPanel dbConnectionPanel = new JPanel();
		dbConnectionPanel.add(new JLabel(jEdit.getProperty(MESSAGE + "dbConnection")));
		dbConnection = new JTextField(jEdit.getProperty(DB_CONNECTION), 40);
		dbConnectionPanel.add(dbConnection);
		c.gridy++;
		dbPanel.add(dbConnectionPanel, c);
		JPanel dbUserPanel = new JPanel();
		dbUserPanel.add(new JLabel(jEdit.getProperty(MESSAGE + "dbUser")));
		dbUser = new JTextField(jEdit.getProperty(DB_USER), 20);
		dbUserPanel.add(dbUser);
		c.gridy++;
		dbPanel.add(dbUserPanel, c);
		JPanel dbPasswordPanel = new JPanel();
		dbPasswordPanel.add(new JLabel(jEdit.getProperty(MESSAGE + "dbPassword")));
		dbPassword = new JTextField(jEdit.getProperty(DB_PASSWORD), 20);
		dbPasswordPanel.add(dbPassword);
		c.gridy++;
		dbPanel.add(dbPasswordPanel, c);
		addComponent(dbPanel);

		dbPreset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String preset = (String) dbPreset.getSelectedItem();
				boolean isCustom = preset.equals(CUSTOM_DB); 
				if (isCustom)
					preset = "";
				else
					preset = "." + preset;
				removePreset.setEnabled(! isCustom);
				dbClass.setText(jEdit.getProperty(DB_CLASS + preset));
				dbConnection.setText(jEdit.getProperty(DB_CONNECTION + preset));
				dbUser.setText(jEdit.getProperty(DB_USER + preset));
				dbPassword.setText(jEdit.getProperty(DB_PASSWORD + preset));
			}
		});
		savePreset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePreset((String) dbPreset.getSelectedItem(), false);
			}			
		});
		savePresetAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String preset = JOptionPane.showInputDialog("DB preset name:");
				if ((preset == null) || preset.isEmpty() || (! preset.matches("\\w+")))
					return;
				savePreset(preset, true);
			}
		});
		removePreset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = dbPreset.getSelectedIndex();
				dbPreset.removeItemAt(index);
				savePresetList();
				if (index == dbPreset.getItemCount())
					index--;
				dbPreset.setSelectedIndex(index);
			}
		});
		String selected = jEdit.getProperty(DB_SELECTED_PRESET);
		if (! presets.contains(selected))
			selected = CUSTOM_DB;
		dbPreset.setSelectedItem(selected);
	}
	
	@Override
	public void _save() {
		jEdit.setProperty(CTAGS, ctags.getText());
		jEdit.setProperty(CMD, cmd.getText());
		jEdit.setProperty(PATTERN, pattern.getText());
		jEdit.setBooleanProperty(UPDATE_ON_LOAD, updateOnLoad.isSelected());
		jEdit.setBooleanProperty(UPDATE_ON_SAVE, updateOnSave.isSelected());
		jEdit.setBooleanProperty(BACKGROUND, background.isSelected());
		jEdit.setBooleanProperty(PREVIEW_TOOLBAR, previewToolbar.isSelected());
		jEdit.setBooleanProperty(PREVIEW_WRAP, previewWrap.isSelected());
		jEdit.setIntegerProperty(PREVIEW_DELAY, Integer.valueOf(previewDelay.getText()));
		jEdit.setProperty(DB_SELECTED_PRESET, (String) dbPreset.getSelectedItem());
		jEdit.setProperty(DB_CLASS, dbClass.getText());
		jEdit.setProperty(DB_CONNECTION, dbConnection.getText());
		jEdit.setProperty(DB_USER, dbUser.getText());
		jEdit.setProperty(DB_PASSWORD, dbPassword.getText());
	}

	private void savePresetList() {
		StringBuffer presets = new StringBuffer();
		for (int i = 0; i < dbPreset.getItemCount(); i++) {
			String preset = (String) dbPreset.getItemAt(i);
			if (preset.equals(CUSTOM_DB))
				continue;
			if (presets.length() > 0)
				presets.append(",");
			presets.append(preset);
		}
		jEdit.setProperty(DB_PRESETS, presets.toString());
	}
	
	private void savePreset(String preset, boolean checkNew) {
		if (checkNew) {
			int i;
			for (i = 0; i < dbPreset.getItemCount(); i++) {
				if (dbPreset.getItemAt(i).equals(preset))
					break;
			}
			if (i == dbPreset.getItemCount()) {
				// New preset!
				((DefaultComboBoxModel)dbPreset.getModel()).insertElementAt(preset, 1);
				savePresetList();
			}
		}
		if (preset.equals(CUSTOM_DB))
			preset = "";
		else
			preset = "." + preset;
		jEdit.setProperty(DB_CLASS + preset, dbClass.getText());
		jEdit.setProperty(DB_CONNECTION + preset, dbConnection.getText());
		jEdit.setProperty(DB_USER + preset, dbUser.getText());
		jEdit.setProperty(DB_PASSWORD + preset, dbPassword.getText());
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
	public static boolean getPreviewToolbar() {
		return jEdit.getBooleanProperty(PREVIEW_TOOLBAR, true);
	}
	public static boolean getPreviewWrap() {
		return jEdit.getBooleanProperty(PREVIEW_WRAP, true);
	}
	public static void setPreviewWrap(boolean wrap) {
		jEdit.setBooleanProperty(PREVIEW_WRAP, wrap);
	}
	public static int getPreviewDelay() {
		return jEdit.getIntegerProperty(PREVIEW_DELAY, 0);
	}
	public static String getDbClass() {
		return jEdit.getProperty(DB_CLASS);
	}
	public static String getDbConnection() {
		return jEdit.getProperty(DB_CONNECTION);
	}
	public static String getDbUser() {
		return jEdit.getProperty(DB_USER);
	}
	public static String getDbPassword() {
		return jEdit.getProperty(DB_PASSWORD);
	}
}
