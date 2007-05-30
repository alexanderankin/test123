package gdb.execution;

import gdb.core.Debugger;
import gdb.core.GdbState;
import gdb.core.GdbView;
import gdb.launch.LaunchConfiguration;
import gdb.launch.LaunchConfigurationManager;
import gdb.launch.LaunchConfigurationManager.ChangeListener;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class ControlView extends GdbView {

	private JComboBox config;
	private JButton go;
	private JButton step;
	private JButton next;
	private JButton ret;
	private JButton until;
	private JButton pause;
	private JButton quit;
	private JButton toggleBreakpoint;
	private LaunchConfigurationManager mgr;
	
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
		JPanel execPanel = new JPanel();
		execPanel.setLayout(new FlowLayout());
		add(execPanel);
		JPanel configPanel = new JPanel();
		configPanel.add(new JLabel("Program:"));
		mgr = LaunchConfigurationManager.getInstance();
		Vector<LaunchConfiguration> configs = mgr.get();
		config = new JComboBox(configs);
		mgr.addChangeListener(new ChangeListener() {
			public void changed() {
				String selected =
					((LaunchConfiguration) config.getSelectedItem()).getName();
				config.setModel(new DefaultComboBoxModel(mgr.get()));
				int index = mgr.getNames().indexOf(selected);
				if (index == -1)
					index = 0;
				config.setSelectedIndex(index);
			}
		});
		configPanel.add(config);
		execPanel.add(configPanel);
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
	
}
