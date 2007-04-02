package gdb.output;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class Console extends JPanel {

	JTextArea textarea;
	
	public Console() {
		setLayout(new BorderLayout());
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
		});
		tb.add(clear);
		add(tb, BorderLayout.NORTH);
		textarea = new JTextArea();
		add(new JScrollPane(textarea), BorderLayout.CENTER);
	}
	public synchronized void append(String s) {
		textarea.append(s);
	}
	public void clear() {
		textarea.setText("");
	}
}
