/*
Copyright (C) 2010  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import common.gui.FileTextField;

@SuppressWarnings("serial")
public class LaunchConfigEditor extends JDialog
{
	public static final String MSG = Plugin.MESSAGE_PREFIX;
	private static final String GEOMETRY = "launch.config.editor.geometry";

	private JTextField configuration;
	private FileTextField script;
	private JTextField arguments;
	private JPanel fields;
	private int y = 0;
	
	private LaunchConfig config = null;
	boolean accepted = false;
	boolean valid = false;
	
	public LaunchConfigEditor(LaunchConfig config) {
		this(jEdit.getActiveView(), config);
	}
	public LaunchConfigEditor(JDialog parent, LaunchConfig config) {
		super(parent, "Edit launch configuration", true);
		setConfig(config);
	}
	private void loadGeometry() {
		GUIUtilities.loadGeometry(this, GEOMETRY);
	}
	public LaunchConfigEditor(JFrame parent, LaunchConfig config) {
		super(parent, "Edit launch configuration", true);
		setConfig(config);
	}
	void setConfig(LaunchConfig config) {
		addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
            	saveGeometry();	
            }
        });
		this.config = config;
		configuration = new JTextField(config.name, 40);
		configuration.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				JTextField tf = (JTextField) arg0;
				String s = tf.getText();
				valid = s.matches("^[\\w \\.]+$");
				return valid;
			}
		});
		script = new FileTextField(config.script, true);
		arguments = new JTextField(config.arguments, 40);
		fields = new JPanel(new GridBagLayout());
		addField(MSG + "configuration", configuration);
		addField(MSG + "script", script);
		addField(MSG + "arguments", arguments);
		configuration.setToolTipText(jEdit.getProperty(MSG + "configuration_tooltip"));
		script.getTextField().setToolTipText(jEdit.getProperty(MSG + "script_tooltip"));
		arguments.setToolTipText(jEdit.getProperty(MSG + "arguments_tooltip"));
		JPanel buttons = new JPanel();
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (valid)
					close(true);
				else
					JOptionPane.showMessageDialog(LaunchConfigEditor.this,
						jEdit.getProperty(MSG + "invalid_launch_config_name"),
						"Error", JOptionPane.ERROR_MESSAGE);
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
		loadGeometry();
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
			config.set(configuration.getText().trim(),
				script.getTextField().getText(), arguments.getText());
		}
		saveGeometry();
		setVisible(false);
	}
	private void saveGeometry() {
		GUIUtilities.saveGeometry(this, GEOMETRY);
	}
}
