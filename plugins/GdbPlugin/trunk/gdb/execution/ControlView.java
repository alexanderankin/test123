package gdb.execution;

import gdb.core.Debugger;
import gdb.core.GdbState;
import gdb.core.GdbView;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class ControlView extends GdbView {

	private JButton go;
	private JButton step;
	private JButton next;
	private JButton ret;
	private JButton until;
	private JButton pause;
	private JButton quit;
	private JButton toggleBreakpoint;
	
	public ControlView() {
		setLayout(new GridLayout(0, 1));
		JPanel execPanel = new JPanel();
		execPanel.setLayout(new FlowLayout());
		add(execPanel);
		go = new JButton("Go!");
		execPanel.add(go);
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().go();
			}
		});
		step = new JButton("Step");
		execPanel.add(step);
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().step();
			}
		});
		next = new JButton("Next");
		execPanel.add(next);
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().next();
			}
		});
		ret = new JButton("Return");
		execPanel.add(ret);
		ret.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().finishCurrentFunction();
			}
		});
		until = new JButton("Until");
		execPanel.add(until);
		until.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().runToCursor();
			}
		});
		pause = new JButton("Pause");
		execPanel.add(pause);
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().pause();
			}
		});
		quit = new JButton("Quit");
		execPanel.add(quit);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().quit();
			}
		});
		JPanel miscPanel = new JPanel();
		miscPanel.setLayout(new FlowLayout());
		add(miscPanel);
		toggleBreakpoint = new JButton("Toggle breakpoint");
		miscPanel.add(toggleBreakpoint);
		toggleBreakpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().toggleBreakpoint(jEdit.getActiveView());
			}
		});
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
