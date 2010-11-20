/*
Copyright (C) 2007  Shlomy Reinstein

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

package perl.launch;

import perl.options.LaunchConfigOptionPane;

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
		addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
            	saveGeometry();	
            }
        });
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
		saveGeometry();
		setVisible(false);
	}
	private void saveGeometry() {
		GUIUtilities.saveGeometry(this, geometryProp);
	}
	
}
