package gatchan.jedit.lucene;

import marker.FileMarker;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StandardUtilities;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchResults extends JPanel implements EBComponent
{
	private JTextField searchField;
	private JList list;
	private JComboBox indexes;
	private MyModel model;
	private IndexComboBoxModel indexModel;
//	private JTextPane preview;

	public SearchResults()
	{
		super(new BorderLayout());
		String[] items = LucenePlugin.instance.getIndexes();
		indexModel = new IndexComboBoxModel(items);
		indexes = new JComboBox(indexModel);

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
				Object obj = list.getSelectedValue();
				View view = jEdit.getActiveView();
				if (obj instanceof String)
				{
					String path = (String) list.getSelectedValue();
					jEdit.openFile(view, path);
				}
				else if (obj instanceof FileMarker)
				{
					((FileMarker) obj).jump(view);
				}
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

		final java.util.List<Object> files = new ArrayList<Object>();
		index.search(text, new ResultProcessor()
		{
			public boolean process(float score, Result result)
			{
				if (result instanceof LineResult)
				{
					LineResult lr = (LineResult) result;
					FileMarker marker = new FileMarker(lr.getPath(),
						lr.getLine() - 1, lr.getText());
					files.add(marker);
				}
				else
				{
					String s = result.getPath();
					files.add(s);
				}
				return true;
			}
		});
		model.setFiles(files);
	}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	} //}}}

	public void handleMessage(EBMessage message)
	{
		if (message instanceof LuceneIndexUpdate)
		{
			String[] items = LucenePlugin.instance.getIndexes();
			indexModel.setIndexes(items);
		}
	}

	private static class MyModel extends AbstractListModel
	{
		private java.util.List<Object> files = new ArrayList<Object>();

		public void setFiles(java.util.List<Object> files)
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

	private static class IndexComboBoxModel extends AbstractListModel implements ComboBoxModel
	{
		private String[] indexes;

		private String selectedItem;

		private IndexComboBoxModel(String[] indexes)
		{
			setIndexes(indexes);
		}

		public int getSize()
		{
			return indexes.length;
		}

		public Object getElementAt(int index)
		{
			return indexes[index];
		}

		public void setIndexes(String[] indexes)
		{
			this.indexes = indexes;
			if (indexes.length == 0)
			{
				selectedItem = null;
			}
			else
			{
				boolean selectedStillExists = false;
				for (String index : indexes)
				{
					if (StandardUtilities.objectsEqual(selectedItem, index))
					{
						selectedStillExists = true;
						break;
					}
				}
				if (!selectedStillExists)
					selectedItem = indexes[0];
			}
			fireContentsChanged(this, 0, indexes.length);
		}

		public void setSelectedItem(Object selectedItem)
		{
			this.selectedItem = (String) selectedItem;
		}

		public Object getSelectedItem()
		{
			return selectedItem;
		}
	}
}
