package gdb.launch;

import gdb.options.LaunchConfigOptionPane;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class LaunchConfigurationListDialog extends JDialog {
	private static final String LaunchConfigListDialogProp =
		Plugin.OPTION_PREFIX + "launch_config_dialog.title";
	private JButton ok;
/*
	private JButton cancel;
	private JButton apply;
*/
	private LaunchConfigOptionPane optionPane;
	
	public LaunchConfigurationListDialog(Frame frame) {
		super(frame, jEdit.getProperty(LaunchConfigListDialogProp), true);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		optionPane = new LaunchConfigOptionPane(); 
		add(optionPane, c);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 0, 5, 5));
		ok = new JButton("Ok");
		buttons.add(ok);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(true);
			}
		});
/*
		cancel = new JButton("Cancel");
		buttons.add(cancel);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(false);
			}
		});
		apply = new JButton("Apply");
		buttons.add(apply);
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				optionPane.save();
			}
		});
*/
		c.gridy = 1;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		add(buttons, c);
		pack();
	}
	private void close(boolean accepted) {
		if (accepted)
			optionPane.save();
		setVisible(false);
	}
	
}
