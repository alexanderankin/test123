package automation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.HistoryTextField;

import automation.Connection.CharHandler;
import automation.Connection.EventHandler;
import automation.Connection.StringHandler;

@SuppressWarnings("serial")
public class ConnectionWindow extends JPanel implements CharHandler, EventHandler,
	ActionListener
{
	private static final String SENDING = "Sending";
	private static boolean DEBUG = false;
	private final Connection c;
	private final JTextPane console;
	private final HistoryTextField input;
	private final JButton send;
	private final JButton clear;
	private final JButton toEnd;
	private final JButton ctrlC;
	private final JButton toBuffer;
	private final List<StringBuilder> output;
	private StringBuilder currentOutput;
	private Object outputSync= new Object(); 
	private int charsToRemove = 0;
	private final JTextField connectionString;
	private final JLabel actionTypeLbl;
	private final JTextField actionTextLbl;
	private final JLabel lastSentLbl;
	private final JTextField lastSentTextLbl;
	private int awtTaskCount = 0;
	private boolean setActionTextScheduled = false;
	private Object setActionTextSync = new Object();
	private String actionType;
	private String actionText;
	private String lastSentText;
	private JPanel actionPanel;

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
		JPanel inputContainer = new JPanel();
		inputContainer.setLayout(new BoxLayout(inputContainer,
			BoxLayout.PAGE_AXIS));
		inputContainer.add(new JPanel());
		inputContainer.add(input);
		inputContainer.add(new JPanel());
		top.add(inputContainer, BorderLayout.CENTER);
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
		toBuffer = new JButton("->Buffer");
		buttonPanel.add(toBuffer);
		toBuffer.addActionListener(this);
		output = new ArrayList<StringBuilder>();
		output.add(currentOutput = new StringBuilder());
		JPanel bottomPanel = new JPanel(new BorderLayout());
		add(bottomPanel, BorderLayout.SOUTH);
		connectionString = new JTextField(c.getHost() + ":" + c.getPort());
		connectionString.setEditable(false);
		bottomPanel.add(connectionString, BorderLayout.EAST);

		actionPanel = new JPanel();
		bottomPanel.add(actionPanel, BorderLayout.WEST);
		actionTypeLbl = new JLabel("<idle>");
		actionTypeLbl.setOpaque(true);
		actionTypeLbl.setBackground(Color.yellow);
		actionPanel.add(actionTypeLbl);
		actionTextLbl = new JTextField();
		actionTextLbl.setEditable(false);
		actionPanel.add(actionTextLbl);
		lastSentLbl = new JLabel("Sent:");
		lastSentLbl.setOpaque(true);
		lastSentLbl.setBackground(Color.green);
		lastSentLbl.setVisible(false);
		actionPanel.add(lastSentLbl);
		lastSentTextLbl = new JTextField();
		lastSentTextLbl.setEditable(false);
		lastSentTextLbl.setVisible(false);
		actionPanel.add(lastSentTextLbl);

		c.setOutputHandler(this);
		c.setEventHandler(this);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				System.err.println("FocusGained");
				input.requestFocusInWindow();
			}
		});
	}

	public Connection getConnection()
	{
		return c;
	}
	public void handle(char c)
	{
		synchronized(outputSync)
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
				if (c == '\n') 
					output.add(currentOutput = new StringBuilder());
			}
			awtTaskCount++;
			if (awtTaskCount > 1)
				return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				Document d = console.getDocument();
				try
				{
					boolean atEnd = (console.getCaretPosition() == d.getLength());
					synchronized(outputSync)
					{
						if (DEBUG)
							System.err.println("ConnectionWindow - awtTaskCount=" + awtTaskCount);
						awtTaskCount = 0;
						if (charsToRemove > 0)
						{
							int len = d.getLength();
							if (len < charsToRemove)
								charsToRemove = len;
							d.remove(len - charsToRemove, charsToRemove);
							charsToRemove = 0;
						}
					}
					for (int i = 0; i < output.size() - 1; i++)
						d.insertString(d.getLength(), output.get(i).toString(), null);
					synchronized(outputSync)
					{
						d.insertString(d.getLength(), currentOutput.toString(), null);
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

	private void copyToBuffer()
	{
		Buffer b = jEdit.newFile(jEdit.getActiveView());
		if (b == null)
		{
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Could not create new buffer");
			return;
		}
		Document document = console.getDocument();
		String text;
		try {
			text = document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Could not get text from console: " + e.getMessage());
			return;
		}
		b.insert(0, text);
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
			else if (e.getSource() == toBuffer)
				copyToBuffer();
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

	private void setAction(final String type, final String text)
	{
		synchronized (setActionTextSync)
		{
			actionType = type;
			actionText = text;
			if (setActionTextScheduled)
				return;
			setActionTextScheduled = true;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				synchronized (setActionTextSync)
				{
					setActionTextScheduled = false;
					actionTypeLbl.setText(actionType);
					boolean showActionText = (actionText != null);
					actionTextLbl.setVisible(showActionText);
					if (showActionText)
						actionTextLbl.setText(actionText);
					boolean showLastSent = (lastSentText != null);
					lastSentLbl.setVisible(showLastSent);
					lastSentTextLbl.setVisible(showLastSent);
					if (showLastSent)
						lastSentTextLbl.setText(lastSentText);
					actionPanel.revalidate();
				}
			}
		});
	}
	public void expecting(StringHandler h)
	{
		if (h == null)
			idle();
		else
			setAction("Expecting", h.desc());
	}

	public void sending(String s)
	{
		setAction(SENDING, s);
		synchronized (setActionTextSync)
		{
			lastSentText = s;
		}
	}

	public void idle()
	{
		setAction("idle", null);
	}
}
