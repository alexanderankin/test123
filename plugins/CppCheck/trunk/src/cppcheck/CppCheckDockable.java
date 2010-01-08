package cppcheck;

import java.awt.BorderLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

@SuppressWarnings("serial")
public class CppCheckDockable extends JPanel
{
	private JProgressBar progress;
	private JTextPane textPane;
	private Pattern progressPattern = Pattern.compile(
		"(\\d+)/(\\d+) files checked (\\d+)% done");
	//1/35 files checked 2% done

	public CppCheckDockable()
	{
		setLayout(new BorderLayout());
		progress = new JProgressBar(0, 100);
		progress.setStringPainted(true);
		add(progress, BorderLayout.NORTH);
		textPane = new JTextPane();
		add(new JScrollPane(textPane), BorderLayout.CENTER);
	}

	public void addOutputLine(final String line)
	{
		Matcher m = progressPattern.matcher(line);
		String s = null;
		int pp = 0;
		if (m.find())
		{
			pp = Integer.valueOf(m.group(3)).intValue();
			s = m.group(3) + "% (" + m.group(1) + "/" + m.group(2) + " files)";
		}
		final String progressString = s;
		final int progressPercent = pp;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (progressString != null)
				{
					progress.setValue(progressPercent);
					progress.setString(progressString);
				}
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
