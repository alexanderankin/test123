package gdb.options;

import gdb.launch.LaunchConfiguration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

import common.gui.FileTextField;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class LaunchConfigEditor extends JDialog {

	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String CONFIGURATION_LABEL = PREFIX + "configuration_label";
	static final String PROGRAM_LABEL = PREFIX + "program_label";
	static final String ARGUMENTS_LABEL = PREFIX + "arguments_label";
	static final String DIRECTORY_LABEL = PREFIX + "directory_label";
	static final String ENVIRONMENT_LABEL = PREFIX + "environment_label";

	static final String CONFIGURATION_TOOLTIP = PREFIX + "configuration_tooltip";
	static final String PROGRAM_TOOLTIP = PREFIX + "program_tooltip";
	static final String ARGUMENTS_TOOLTIP= PREFIX + "arguments_tooltip";
	static final String DIRECTORY_TOOLTIP = PREFIX + "directory_tooltip";
	static final String ENVIRONMENT_TOOLTIP = PREFIX + "environment_tooltip";

	private JTextField configurationTF;
	private FileTextField programTF;
	private JTextField argumentsTF;
	private JTextField directoryTF;
	private JTextField environmentTF;
	private JPanel fields;
	private int y = 0;
	
	private LaunchConfiguration config = null;
	boolean accepted = false;

	public LaunchConfigEditor(LaunchConfiguration config) {
		this(jEdit.getActiveView(), config);
	}
	public LaunchConfigEditor(JDialog parent, LaunchConfiguration config) {
		super(parent, "Edit launch configuration", true);
		setConfig(config);
	}
	public LaunchConfigEditor(JFrame parent, LaunchConfiguration config) {
		super(parent, "Edit launch configuration", true);
		setConfig(config);
	}
	void setConfig(LaunchConfiguration config) {
		this.config = config;
		configurationTF = new JTextField(config.getName(), 40);
		programTF = new FileTextField(config.getProgram(), true);
		argumentsTF = new JTextField(config.getArguments(), 40);
		directoryTF = new JTextField(config.getDirectory(), 40);
		environmentTF = new JTextField(config.getEnvironment(), 40);
		fields = new JPanel(new GridBagLayout());
		addField(CONFIGURATION_LABEL, configurationTF);
		addField(PROGRAM_LABEL, programTF);
		addField(ARGUMENTS_LABEL, argumentsTF);
		addField(DIRECTORY_LABEL, directoryTF);
		addField(ENVIRONMENT_LABEL, environmentTF);
		configurationTF.setToolTipText(jEdit.getProperty(CONFIGURATION_TOOLTIP));
		programTF.getTextField().setToolTipText(jEdit.getProperty(PROGRAM_TOOLTIP));
		argumentsTF.setToolTipText(jEdit.getProperty(ARGUMENTS_TOOLTIP));
		directoryTF.setToolTipText(jEdit.getProperty(DIRECTORY_TOOLTIP));
		environmentTF.setToolTipText(jEdit.getProperty(ENVIRONMENT_TOOLTIP));
		JPanel buttons = new JPanel();
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(true);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(false);
			}
		});
		buttons.add(ok);
		buttons.add(cancel);
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = gc.gridy = 0;
		gc.weighty = 1;
		getContentPane().add(fields, gc);
		gc.gridy++;
		gc.weighty = 0;
		getContentPane().add(buttons, gc);
		pack();
	}
	
	public boolean accepted() {
		return accepted;
	}
	
	private JLabel getLabel(String prop) {
		return new JLabel(jEdit.getProperty(prop));
	}
	private void addField(String prop, JComponent comp) {
		JLabel l = getLabel(prop);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = y++;
		gc.weightx = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		fields.add(l, gc);
		gc.gridx = 1;
		gc.weightx = 1;
		gc.fill = GridBagConstraints.BOTH;
		fields.add(comp, gc);
	}
	
	private void close(boolean accepted) {
		this.accepted = accepted;
		if (accepted && config != null) {
			config.set(configurationTF.getText(), programTF.getTextField().getText(),
					argumentsTF.getText(), directoryTF.getText(),
					environmentTF.getText());
		}
		setVisible(false);
	}
}
