package automation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.HistoryTextField;

import automation.Connection.CharHandler;

@SuppressWarnings("serial")
public class ConnectionWindow extends JPanel implements CharHandler,
	ActionListener
{
	private Connection c;
	private JTextPane console;
	private HistoryTextField input;
	private JButton send;
	private JButton expect;

	public ConnectionWindow(Connection c)
	{
		this.c = c;
		setLayout(new BorderLayout());
		console = new JTextPane();
		add(new JScrollPane(console), BorderLayout.CENTER);
		JPanel top = new JPanel(new BorderLayout());
		add(top, BorderLayout.NORTH);
		input = new HistoryTextField("automation.send-history");
		top.add(input, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		top.add(buttonPanel, BorderLayout.EAST);
		send = new JButton("Send");
		buttonPanel.add(send);
		send.addActionListener(this);
		expect = new JButton("Expect");
		buttonPanel.add(expect);
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
			console.setCaretPosition(d.getLength());
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
