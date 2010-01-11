package cppcheck;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
					abort();
				}
			});
			top.add(abort, BorderLayout.EAST);
			textPane = new JTextPane();
			add(new JScrollPane(textPane), BorderLayout.CENTER);
			OutputHandler oh = r.getOutputHandler();
			oh.addListener(this);
		}

		public void abort()
		{
			r.abort();
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
				final Runner fr = r;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						CppCheckTab tab = createTab(fr);
						runners.addTab(fr.toString(), tab);
					}
				});
			}
			public void removed(Runner r)
			{
				final Runner fr = r;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						for (int i = runners.getTabCount() - 1; i >= 0; i--)
						{
							if (runners.getTitleAt(i).equals(fr.toString()))
							{
								CppCheckTab tab = (CppCheckTab)
									runners.getComponent(i);
								//if (tab != null)
									tab.end(fr);
								runners.remove(i);
								break;
							}
						}
					}
				});
			}
		});
		runners.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isMiddleMouseButton(e))
				{
					closeCurrentTab();
				}
			}
		});
	}

	public CppCheckTab createTab(Runner r)
	{
		return new CppCheckTab(r);
	}

	public void closeCurrentTab()
	{
		final int selected = runners.getSelectedIndex();
		if (selected < 0)
			return;
		CppCheckTab tab = (CppCheckTab) runners.getComponent(selected);
		if (tab != null)
			tab.abort();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				runners.remove(selected);
			}
		});
	}
}
