package gatchan.jedit.lucene;

import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchResults extends JPanel
{
	private JTextField searchField;
	private JList list;
	private JComboBox indexes;
	private MyModel model;
//	private JTextPane preview;

	public SearchResults()
	{
		super(new BorderLayout());
		String[] items = LucenePlugin.instance.getIndexes();
		if (items == null)
		{
			indexes = new JComboBox();
		}
		else
		{

			indexes = new JComboBox(items);
		}
		JPanel panel = new JPanel(new BorderLayout());
		add(panel, BorderLayout.NORTH);
		panel.add(new JLabel("Search for:"), BorderLayout.WEST);
		searchField = new JTextField(40);
		searchField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				search(searchField.getText());
			}
		});
		
		panel.add(searchField, BorderLayout.CENTER);
		panel.add(indexes, BorderLayout.EAST);
		model = new MyModel();
		list = new JList(model);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				String path = (String) list.getSelectedValue();
				jEdit.openFile(jEdit.getActiveView(), path);
			}
		});
//		HTMLDocument htmlDocument = new HTMLDocument();
//		preview = new JTextPane(htmlDocument);
//		preview.setContentType("text/html");
		add(new JScrollPane(list), BorderLayout.CENTER);
//		add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(list), preview), BorderLayout.CENTER);
	}

	public void search(String text)
	{
		Index index = LucenePlugin.instance.getIndex((String) indexes.getSelectedItem());
		if (index == null)
			return;

		final java.util.List<String> files = new ArrayList<String>();
		index.search(text, new ResultProcessor()
		{
			public boolean process(float score, Result result)
			{
				files.add(result.getPath());
				return true;
			}
		});
		model.setFiles(files);
	}

	private static class MyModel extends AbstractListModel
	{
		private java.util.List<String> files = new ArrayList<String>();

		public void setFiles(java.util.List<String> files)
		{
			this.files = files;
			fireContentsChanged(this, 0, files.size());
		}

		public int getSize()
		{
			return files.size();
		}

		public Object getElementAt(int index)
		{
			return files.get(index);
		}
	}
}