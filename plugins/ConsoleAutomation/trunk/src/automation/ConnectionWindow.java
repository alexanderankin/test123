package automation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private final Connection c;
	private final JTextPane console;
	private final HistoryTextField input;
	private final JButton send;
	private final JButton clear;
	private final JButton toEnd;
	private final JButton ctrlC;
	private final List<StringBuilder> output;
	private StringBuilder currentOutput;
	private int charsToRemove = 0;

	public ConnectionWindow(Connection c)
	{
		this.c = c;
		setLayout(new BorderLayout());
		console = new JTextPane();
		add(new JScrollPane(console), BorderLayout.CENTER);
		console.setEditable(false);
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
		clear = new JButton("Clear");
		buttonPanel.add(clear);
		clear.addActionListener(this);
		toEnd = new JButton("End");
		buttonPanel.add(toEnd);
		toEnd.addActionListener(this);
		ctrlC = new JButton("Ctrl+C");
		buttonPanel.add(ctrlC);
		ctrlC.addActionListener(this);
		output = new ArrayList<StringBuilder>();
		output.add(currentOutput = new StringBuilder());
		c.setOutputHandler(this);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				console.requestFocusInWindow();
			}
		});
	}

	public Connection getConnection()
	{
		return c;
	}
	public void handle(char c)
	{
		synchronized(output)
		{
			if (c == '\b')
			{
				int len = currentOutput.length();
				if (len > 0)
					currentOutput.setLength(len - 1);
				else
					charsToRemove++;
			}
			else
			{
				currentOutput.append(c);
				if (charsToRemove > 0)
					charsToRemove--;
				if (c == '\n') 
					output.add(currentOutput = new StringBuilder());
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				Document d = console.getDocument();
				try
				{
					boolean atEnd = (console.getCaretPosition() == d.getLength());
					synchronized(output)
					{
						if (charsToRemove > 0)
						{
							int len = d.getLength();
							d.remove(len - charsToRemove, charsToRemove);
							charsToRemove = 0;
						}
						for (StringBuilder sb: output)
							d.insertString(d.getLength(), sb.toString(), null);
						output.clear();
						output.add(currentOutput = new StringBuilder());
					}
					if (atEnd)
						console.setCaretPosition(d.getLength());
				} catch (BadLocationException e)
				{
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
			else if (e.getSource() == clear)
				console.getDocument().remove(0, console.getDocument().getLength());
			else if (e.getSource() == toEnd)
				console.setCaretPosition(console.getDocument().getLength());
			else if (e.getSource() == ctrlC)
				c.send(String.valueOf((char) 3));
			input.setText("");
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Could not perform action, exception: " + Arrays.toString(
				e1.getStackTrace()));
		}
	}
}
