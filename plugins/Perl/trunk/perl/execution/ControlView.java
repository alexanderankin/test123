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

package perl.execution;

import perl.core.Debugger;
import perl.core.GdbState;
import perl.core.GdbView;
import perl.launch.LaunchConfiguration;
import perl.launch.LaunchConfigurationListDialog;
import perl.launch.LaunchConfigurationManager;
import perl.launch.LaunchConfigurationManager.ChangeListener;
import perl.options.GeneralOptionPane;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;

@SuppressWarnings("serial")
public class ControlView extends GdbView implements EBComponent {

	private JComboBox config;
	private JButton go;
	private JButton step;
	private JButton next;
	private JButton ret;
	private JButton until;
	private JButton pause;
	private JButton quit;
	private JButton toggleBreakpoint;
	private JButton editLaunchConfigs;
	private LaunchConfigurationManager mgr;
	private JPanel configPanel;
	
	private static class ActionInvoker implements ActionListener {
		String action;
		ActionInvoker(String actionName) {
			action = actionName;
		}
		public void actionPerformed(ActionEvent arg0) {
			jEdit.getAction(action).invoke(jEdit.getActiveView());
		}
	}
	
	public ControlView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createRigidArea(new Dimension(0, 5)));
		createProgramListPanel();
		JPanel execPanel = new JPanel();
		execPanel.setLayout(new FlowLayout());
		add(execPanel);
		go = new JButton("Go!");
		execPanel.add(go);
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (GdbState.getState() == GdbState.State.IDLE) {
					mgr.setDefaultIndex(config.getSelectedIndex());
					mgr.save();
				}
				jEdit.getAction(Debugger.GO_ACTION).invoke(jEdit.getActiveView());
			}
		});
		step = new JButton("Step");
		execPanel.add(step);
		step.addActionListener(new ActionInvoker(Debugger.STEP_ACTION));
		next = new JButton("Next");
		execPanel.add(next);
		next.addActionListener(new ActionInvoker(Debugger.NEXT_ACTION));
		ret = new JButton("Return");
		execPanel.add(ret);
		ret.addActionListener(new ActionInvoker(Debugger.FINISH_ACTION));
		until = new JButton("Until");
		execPanel.add(until);
		until.addActionListener(new ActionInvoker(Debugger.UNTIL_ACTION));
		pause = new JButton("Pause");
		execPanel.add(pause);
		pause.addActionListener(new ActionInvoker(Debugger.PAUSE_ACTION));
		quit = new JButton("Quit");
		execPanel.add(quit);
		quit.addActionListener(new ActionInvoker(Debugger.QUIT_ACTION));
		toggleBreakpoint = new JButton("Toggle breakpoint");
		execPanel.add(toggleBreakpoint);
		toggleBreakpoint.addActionListener(new ActionInvoker(Debugger.TOGGLE_BREAKPOINT_ACTION));
		initialize();
		EditBus.addToBus(this);
	}

	private void createProgramListPanel() {
		configPanel = new JPanel();
		configPanel.add(new JLabel("Program:"));
		mgr = LaunchConfigurationManager.getInstance();
		config = new JComboBox();
		updateLaunchConfigs();
		mgr.addChangeListener(new ChangeListener() {
			public void changed() {
				updateLaunchConfigs();
			}
		});
		configPanel.add(config);
		editLaunchConfigs = new JButton("Edit program list");
		configPanel.add(editLaunchConfigs);
		editLaunchConfigs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LaunchConfigurationListDialog dlg =
					new LaunchConfigurationListDialog(
							(Frame)SwingUtilities.getWindowAncestor(ControlView.this));
				dlg.setVisible(true);
			}
		});
		add(configPanel);
		configPanel.setVisible(jEdit.getBooleanProperty(
				GeneralOptionPane.SHOW_PROGRAM_LIST_IN_PANEL_PROP));
	}

	void updateLaunchConfigs() {
		LaunchConfiguration selectedConfig =
			(LaunchConfiguration) config.getSelectedItem();
		if (selectedConfig == null)
			selectedConfig = mgr.getDefault();
		config.setModel(new DefaultComboBoxModel(mgr.get()));
		config.setSelectedItem(selectedConfig);
	}
	
	void initialize() {
		switch (GdbState.getState()) {
		case RUNNING:
			running();
			break;
		case IDLE:
			sessionEnded();
			break;
		case PAUSED:
			update();
			break;
		}
	}
	@Override
	public void running() {
		config.setEnabled(false);
		go.setEnabled(false);
		step.setEnabled(false);
		next.setEnabled(false);
		ret.setEnabled(false);
		until.setEnabled(false);
		pause.setEnabled(true);
		quit.setEnabled(false);
	}

	@Override
	public void sessionEnded() {
		config.setEnabled(true);
		go.setEnabled(true);
		step.setEnabled(false);
		next.setEnabled(false);
		ret.setEnabled(false);
		until.setEnabled(false);
		pause.setEnabled(false);
		quit.setEnabled(false);
	}

	@Override
	public void update() {
		go.setEnabled(true);
		step.setEnabled(true);
		next.setEnabled(true);
		ret.setEnabled(true);
		until.setEnabled(true);
		pause.setEnabled(false);
		quit.setEnabled(true);
	}

	public void handleMessage(EBMessage msg) {
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
	}

	private void propertiesChanged() {
		configPanel.setVisible(jEdit.getBooleanProperty(
				GeneralOptionPane.SHOW_PROGRAM_LIST_IN_PANEL_PROP));
	}
	
}
