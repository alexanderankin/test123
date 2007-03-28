package gdb.execution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class ControlView extends JPanel {

	public ControlView() {
		JButton go = new JButton("Go!");
		add(go);
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Plugin.go(jEdit.getActiveView());
			}
		});
		JButton step = new JButton("Step");
		add(step);
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Plugin.step(jEdit.getActiveView());
			}
		});
		JButton next = new JButton("Next");
		add(next);
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Plugin.next(jEdit.getActiveView());
			}
		});
		JButton ret = new JButton("Return");
		add(ret);
		ret.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Plugin.finishCurrentFunction(jEdit.getActiveView());
			}
		});
		JButton pause = new JButton("Pause");
		add(pause);
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Plugin.pause(jEdit.getActiveView());
			}
		});
		JButton quit = new JButton("Quit");
		add(quit);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Plugin.quit(jEdit.getActiveView());
			}
		});
	}
	
}
