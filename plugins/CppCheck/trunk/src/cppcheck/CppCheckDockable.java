package cppcheck;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

@SuppressWarnings("serial")
public class CppCheckDockable extends JPanel
{
	private JTextPane textPane;

	public CppCheckDockable()
	{
		setLayout(new BorderLayout());
		textPane = new JTextPane();
		add(new JScrollPane(textPane), BorderLayout.CENTER);
	}

	public void addOutputLine(final String line)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				int len = textPane.getDocument().getLength();
				try
				{
					textPane.getDocument().insertString(len, line + "\n", null);
				}
				catch (BadLocationException e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
