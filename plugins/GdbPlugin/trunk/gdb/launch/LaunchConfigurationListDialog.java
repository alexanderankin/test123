package gdb.launch;

import gdb.options.LaunchConfigOptionPane;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class LaunchConfigurationListDialog extends JDialog {
	private static final String LaunchConfigListDialogProp =
		Plugin.OPTION_PREFIX + "launch_config_dialog.title";
	private static final String VIEW_GEOMETRY = "launch.config.list.dialog.view.geometry";
	private static final String FLOAT_GEOMETRY = "launch.config.list.dialog.float.geometry";
	private JButton ok;
/*
	private JButton cancel;
	private JButton apply;
*/
	private LaunchConfigOptionPane optionPane;
	private String geometryProp;
	
	public LaunchConfigurationListDialog(Frame frame) {
		super(frame, jEdit.getProperty(LaunchConfigListDialogProp), true);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
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
		geometryProp = (frame instanceof View) ? VIEW_GEOMETRY : FLOAT_GEOMETRY;
		GUIUtilities.loadGeometry(this, geometryProp);
	}
	private void close(boolean accepted) {
		if (accepted)
			optionPane.save();
		GUIUtilities.saveGeometry(this, geometryProp);
		setVisible(false);
	}
	
}
