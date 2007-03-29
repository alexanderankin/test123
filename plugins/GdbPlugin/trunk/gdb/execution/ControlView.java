package gdb.execution;

import gdb.core.Debugger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlView extends JPanel {

	public ControlView() {
		JButton go = new JButton("Go!");
		add(go);
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().go();
			}
		});
		JButton step = new JButton("Step");
		add(step);
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().step();
			}
		});
		JButton next = new JButton("Next");
		add(next);
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().next();
			}
		});
		JButton ret = new JButton("Return");
		add(ret);
		ret.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().finishCurrentFunction();
			}
		});
		JButton pause = new JButton("Pause");
		add(pause);
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().pause();
			}
		});
		JButton quit = new JButton("Quit");
		add(quit);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Debugger.getInstance().quit();
			}
		});
	}
	
}
