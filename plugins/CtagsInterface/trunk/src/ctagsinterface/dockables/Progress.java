package ctagsinterface.dockables;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;

@SuppressWarnings("serial")
public class Progress extends JPanel
{
	private JTextArea textArea;
	private ArrayList<String> toAdd = new ArrayList<String>();

	public Progress(View view)
	{
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}
	public void add(String s)
	{
		synchronized(textArea)
		{
			toAdd.add(s);
			if (toAdd.size() == 1)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						synchronized(textArea)
						{
							for (String s: toAdd)
								textArea.append(s + "\n");
							textArea.setCaretPosition(textArea.getText().length());
							toAdd.clear();
						}
					}
				});			
			}
		}
	}
}
