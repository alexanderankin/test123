package automation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.gjt.sp.jedit.jEdit;

import automation.Connection.CharHandler;

@SuppressWarnings("serial")
public class ConnectionWindow extends JPanel implements CharHandler,
	ActionListener
{
	private Connection c;
	private JTextPane console;
	private JTextField input;
	private JButton send;
	private JButton expect;

	public ConnectionWindow(Connection c)
	{
		this.c = c;
		setLayout(new BorderLayout());
		console = new JTextPane();
		add(console, BorderLayout.CENTER);
		JPanel top = new JPanel();
		add(top, BorderLayout.NORTH);
		input = new JTextField(40);
		top.add(input);
		send = new JButton("Send");
		top.add(send);
		send.addActionListener(this);
		expect = new JButton("Expect");
		top.add(expect);
		expect.addActionListener(this);
		c.setOutputHandler(this);
	}

	public void handle(char c)
	{
		Document d = console.getDocument();
		try
		{
			System.out.print(c);
			d.insertString(d.getLength(), String.valueOf(c), null);
		} catch (BadLocationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			String s = input.getText();
			if (e.getSource() == send)
				c.send(s);
			else if (e.getSource() == expect)
				c.expectSubstr(s, true);
			input.setText("");
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Could not perform action, exception: " + e1.getStackTrace());
		}
	}
}
