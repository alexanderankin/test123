package ctagsinterface.dockables;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;

import ctagsinterface.main.Logger;

@SuppressWarnings("serial")
public class Progress extends JPanel
{
	private JTabbedPane tabs;
	private HashMap<Logger, ProgressTab> progressTabs =
		new HashMap<Logger, ProgressTab>();

	public Progress(View view)
	{
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		add(tabs, BorderLayout.CENTER);
		tabs.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isMiddleMouseButton(e))
				{
					int i = tabs.getSelectedIndex();
					if (i < 0)
						return;
					ProgressTab tab = (ProgressTab) tabs.getComponentAt(i);
					synchronized(progressTabs)
					{
						progressTabs.remove(tab.logger);
						tabs.removeTabAt(i);
					}
				}
			}
		});
	}
	public void add(Logger logger, String s)
	{
		getTab(logger).append(s);
	}
	private ProgressTab getTab(Logger logger)
	{
		ProgressTab tab;
		synchronized(progressTabs)
		{
			tab = progressTabs.get(logger);
			if (tab == null)
			{
				tab = new ProgressTab(logger);
				tabs.addTab(logger.name(), tab);
				progressTabs.put(logger, tab);
			}
		}
		return tab;
	}
	public void beginTask(Logger logger, String task)
	{
		getTab(logger).beginTask(task);
	}
	public void endTask(Logger logger)
	{
		getTab(logger).endTask();
	}
	private static class ProgressTab extends JPanel
	{
		private static final String IDLE = "Idle";
		private Logger logger;
		private JTextArea textArea;
		private ArrayList<String> toAdd = new ArrayList<String>();
		private JButton close;
		private JLabel status;

		public ProgressTab(Logger logger)
		{
			this.logger = logger;
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			add(top, BorderLayout.NORTH);
			top.setLayout(new BorderLayout());
			close = new JButton("Close");
			top.add(close, BorderLayout.EAST);
			status = new JLabel(IDLE);
			top.add(status, BorderLayout.CENTER);
			textArea = new JTextArea();
			add(new JScrollPane(textArea), BorderLayout.CENTER);
		}
		public void append(String s)
		{
			boolean update;
			synchronized(toAdd)
			{
				update = toAdd.isEmpty();
				toAdd.add(s);
			}
			if (update)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						synchronized(toAdd)
						{
							for (String s: toAdd)
								textArea.append(s + "\n");
							toAdd.clear();
						}
						textArea.setCaretPosition(textArea.getText().length());
					}
				});			
			}
		}
		public void beginTask(String task)
		{
			status.setText(task);
		}
		public void endTask()
		{
			status.setText(IDLE);
		}
	}
}
