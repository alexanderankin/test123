package gdb.execution;

import gdb.core.Debugger;
import gdb.core.GdbState;
import gdb.core.GdbView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class ControlView extends GdbView {

	private JButton go;
	private JButton step;
	private JButton next;
	private JButton ret;
	private JButton until;
	private JButton pause;
	private JButton quit;

	public ControlView() {
		go = new JButton("Go!");
		add(go);
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().go();
			}
		});
		step = new JButton("Step");
		add(step);
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().step();
			}
		});
		next = new JButton("Next");
		add(next);
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().next();
			}
		});
		ret = new JButton("Return");
		add(ret);
		ret.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().finishCurrentFunction();
			}
		});
		until = new JButton("Until");
		add(until);
		until.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().runToCursor();
			}
		});
		pause = new JButton("Pause");
		add(pause);
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().pause();
			}
		});
		quit = new JButton("Quit");
		add(quit);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().quit();
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
