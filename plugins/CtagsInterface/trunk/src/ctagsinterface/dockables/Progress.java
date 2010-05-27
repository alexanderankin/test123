package ctagsinterface.dockables;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;

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
	private HashMap<Logger, ArrayList<String>> toAdd =
		new HashMap<Logger, ArrayList<String>>();
	private HashMap<Logger, JTextArea> textAreas =
		new HashMap<Logger, JTextArea>();

	public Progress(View view)
	{
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		add(tabs, BorderLayout.CENTER);
	}
	public void add(final Logger logger, String s)
	{
		synchronized(logger)
		{
			ArrayList<String> arr = toAdd.get(logger);
			boolean update = false;
			if (arr == null)
			{
				arr = new ArrayList<String>();
				toAdd.put(logger, arr);
				update = true;
			}
			arr.add(s);
			if (update)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						updateTab(logger);
					}
				});			
			}
		}
	}
	private void updateTab(final Logger logger)
	{
		JTextArea textArea = textAreas.get(logger);
		if (textArea == null)
		{
			textArea = new JTextArea();
			tabs.addTab(logger.name(), new JScrollPane(textArea));
			textAreas.put(logger, textArea);
		}
		synchronized(logger)
		{
			for (String s: toAdd.get(logger))
				textArea.append(s + "\n");
			textArea.setCaretPosition(textArea.getText().length());
			toAdd.clear();
		}
	}
}
