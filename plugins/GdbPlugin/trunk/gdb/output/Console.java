package gdb.output;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class Console extends JPanel implements ActionListener {

	private JTextArea textarea;
	private JTextField userText = null;
	private InputHandler handler;
	
	public interface InputHandler {
		void handle(String line);
	}
	
	public Console() {
		this(null);
	}
	public Console(InputHandler handler) {
		this.handler = handler;
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
		if (handler != null) {
			tb.add(new JLabel("Command:"));
			userText = new JTextField(30);
			tb.add(userText);
			JButton send = new JButton("Send");
			send.addActionListener(this);
			tb.add(send);
		}
		add(tb, BorderLayout.NORTH);
		textarea = new JTextArea();
		add(new JScrollPane(textarea), BorderLayout.CENTER);
	}
	public synchronized void append(String s) {
		textarea.append(s);
		// Auto-scroll
		textarea.setCaretPosition(textarea.getDocument().getLength());
	}
	public void clear() {
		textarea.setText("");
	}
	public void actionPerformed(ActionEvent arg0) {
		String line = userText.getText();
		if (line.length() > 0) {
			handler.handle(line);
			userText.setText("");
		}
	}
}
