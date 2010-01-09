package cppcheck;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import cppcheck.Plugin.Listener;

@SuppressWarnings("serial")
public class CppCheckDockable extends JPanel 
{
	private JTabbedPane runners;

	class CppCheckTab extends JPanel implements OutputHandler.Listener 
	{
		private JProgressBar progress;
		private JTextPane textPane;
		private JButton abort;
		private Runner r;

		public CppCheckTab(Runner r)
		{
			this.r = r;
			setLayout(new BorderLayout());
			JPanel top = new JPanel(new BorderLayout());
			add(top, BorderLayout.NORTH);
			progress = new JProgressBar(0, 100);
			progress.setStringPainted(true);
			top.add(progress, BorderLayout.CENTER);
			abort = new JButton("Abort");
			abort.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					CppCheckTab.this.r.abort();
				}
			});
			top.add(abort, BorderLayout.EAST);
			textPane = new JTextPane();
			add(new JScrollPane(textPane), BorderLayout.CENTER);
			OutputHandler oh = r.getOutputHandler();
			oh.addListener(this);
		}

		public void end(Runner r)
		{
			OutputHandler oh = r.getOutputHandler();
			oh.removeListener(this);
		}

		public void setProgress(final int percent)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					progress.setValue(percent);				
				}
			});
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

	public CppCheckDockable()
	{
		setLayout(new BorderLayout());
		runners = new JTabbedPane();
		add(runners, BorderLayout.CENTER);
		Plugin.addListener(new Listener()
		{
			public void added(Runner r)
			{
				CppCheckTab tab = createTab(r);
				runners.addTab(r.toString(), tab);
			}
			public void removed(Runner r)
			{
				for (int i = 0; i < runners.getTabCount(); i++)
				{
					if (runners.getTitleAt(i).equals(r.toString()))
					{
						CppCheckTab tab = (CppCheckTab) runners.getTabComponentAt(i);
						tab.end(r);
						runners.remove(tab);
					}
				}
			}
		});
	}

	public CppCheckTab createTab(Runner r)
	{
		return new CppCheckTab(r);
	}
}
