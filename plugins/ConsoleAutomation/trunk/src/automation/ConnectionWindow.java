package automation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
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
	private JButton clear;
	private ArrayList<StringBuilder> output;
	private StringBuilder currentOutput;

	public ConnectionWindow(Connection c)
	{
		this.c = c;
		setLayout(new BorderLayout());
		console = new JTextPane();
		add(new JScrollPane(console), BorderLayout.CENTER);
		JPanel top = new JPanel(new BorderLayout());
		add(top, BorderLayout.NORTH);
		input = new HistoryTextField("automation.send-history");
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}
		});
		top.add(input, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		top.add(buttonPanel, BorderLayout.EAST);
		send = new JButton("Send");
		buttonPanel.add(send);
		send.addActionListener(this);
		expect = new JButton("Expect");
		buttonPanel.add(expect);
		expect.addActionListener(this);
		clear = new JButton("Clear");
		buttonPanel.add(clear);
		clear.addActionListener(this);
		output = new ArrayList<StringBuilder>();
		output.add(currentOutput = new StringBuilder());
		c.setOutputHandler(this);
	}

	public void handle(char c)
	{
		synchronized(output)
		{
			currentOutput.append(c);
			if (c == '\n')
				output.add(currentOutput = new StringBuilder());
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				Document d = console.getDocument();
				try
				{
					synchronized(output)
					{
						for (StringBuilder sb: output)
							d.insertString(d.getLength(), sb.toString(), null);
						output.clear();
						output.add(currentOutput = new StringBuilder());
					}
					console.setCaretPosition(d.getLength());
				} catch (BadLocationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
			else if (e.getSource() == clear)
				console.getDocument().remove(0, console.getDocument().getLength());
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
