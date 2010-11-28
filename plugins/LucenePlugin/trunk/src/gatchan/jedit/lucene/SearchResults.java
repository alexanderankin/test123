package gatchan.jedit.lucene;

import gatchan.jedit.lucene.Index.ActivityListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Collections;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import marker.FileMarker;
import marker.MarkerSetsPlugin;
import marker.tree.FileTreeBuilder;
import marker.tree.SourceLinkTree;
import marker.tree.SourceLinkTree.SourceLinkParentNode;
import marker.tree.SourceLinkTree.SubtreePopupMenuProvider;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.*;

@SuppressWarnings("serial")
public class SearchResults extends JPanel implements DefaultFocusComponent
{
	public static final String CURRENT_BUFFER = "__current buffer__";
	public static final String ALL_BUFFERS = "__all buffers__";

	private static final String LUCENE_SEARCH_INDEX = "lucene.search.index";
	private static final String DOCKABLE_NAME = "lucene-search";
	private static final String MESSAGE_IDLE = "";
	private static final String MESSAGE_INDEXING = "Indexing";
	private final HistoryTextField searchField;
	private final JTextField type;
	private final JPanel mainPanel;
	private final JList list;
	private final JCheckBox lineResults;
	private final JSpinner maxResults;
	private final JComboBox indexes;
	private final MyModel model;
	private final SourceLinkTree tree;
	private final IndexComboBoxModel indexModel;
	private final JLabel indexStatus;
	private final RolloverButton clear;
	private final RolloverButton multi;
	private boolean multiStatus;
	private boolean horizontalLayout;
	private boolean firstTimeLayout = true;
//	private JTextPane preview;
	private final JLabel textLabel;
	private final JLabel fileTypeLabel;
	private final JLabel maxResultsLabel;
	private boolean shortLabels;
	private ActionListener indexActionListener;

	public SearchResults(View v)
	{
		super(new BorderLayout());
		KeyListener kl = v.getDockableWindowManager().closeListener(DOCKABLE_NAME);
		
		lineResults = new JCheckBox(getLabel("lucene.line-based"));
		lineResults.setToolTipText(jEdit.getProperty("lucene.results.line-based.tooltip"));
		lineResults.setSelected(true);
		fileTypeLabel = new JLabel(getLabel("lucene.file-type"));
		type = new JTextField(6);
		type.addKeyListener(kl);
		type.setToolTipText(jEdit.getProperty("lucene.file-type.tooltip"));
		maxResultsLabel = new JLabel(getLabel("lucene.max-results"));
		maxResults = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
		//JPanel maxPanel = new JPanel(new BorderLayout());
		//maxPanel.add(BorderLayout.WEST, new JLabel("Max results:"));
		//maxPanel.add(BorderLayout.EAST, maxResults);
		maxResults.setToolTipText(jEdit.getProperty("lucene.max.results.tooltip"));
		indexStatus = new JLabel();
		String[] items = LucenePlugin.instance.getIndexes();
		indexModel = new IndexComboBoxModel(items);
		indexes = new JComboBox(indexModel);
		indexes.setToolTipText(jEdit.getProperty("lucene.index-combo.tooltip"));
		indexActionListener = new ActionListener()
		{
			private Index prevIndex;
			private ActivityListener listener = new ActivityListener()
			{
				public void indexingEnded(Index index)
				{
					indexStatus.setText(MESSAGE_IDLE);
				}
				public void indexingStarted(Index index)
				{
					indexStatus.setText(MESSAGE_INDEXING);
				}
			}; 
			public void actionPerformed(ActionEvent e)
			{
				Index index = getSelectedIndex();
				if (index == null)
					return;
				if (prevIndex != null)
					prevIndex.removeActivityListener(listener);
				prevIndex = index;
				indexStatus.setText(index.isChanging() ? MESSAGE_INDEXING : MESSAGE_IDLE);
				index.addActivityListener(listener);
				jEdit.setProperty(LUCENE_SEARCH_INDEX, index.getName());
			}
		};
		indexes.addActionListener(indexActionListener);
		// panel.add(new JLabel("Search for:"), BorderLayout.WEST);
		textLabel = new JLabel(getLabel("lucene.search-string"));
		searchField = new HistoryTextField("lucene.search-history");
		searchField.addKeyListener(kl);
		if (OptionPane.getSearchStringLength() != 0)
			searchField.setColumns(OptionPane.getSearchStringLength());
		searchField.setToolTipText(jEdit.getProperty("lucene.search-string.tooltip"));

		final MyActionListener actionListener = new MyActionListener();
		searchField.addActionListener(actionListener);
		type.addActionListener(actionListener);
		clear = new RolloverButton(GUIUtilities.loadIcon(
			jEdit.getProperty("hypersearch-results.clear.icon")));
		clear.setToolTipText(jEdit.getProperty(
			"hypersearch-results.clear.label"));
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				model.setFiles(Collections.emptyList());
				tree.clear();
			}
		});
		multi = new RolloverButton();
		multi.setToolTipText(jEdit.getProperty(
			"hypersearch-results.multi.label"));
		multi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				toggleMultiStatus();
			}
		});
		multiStatus = true;
		updateMultiStatus();

		setLayoutByGeometry();
		
		model = new MyModel();
		list = new JList(model);
		tree = new SourceLinkTree(jEdit.getActiveView());

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
		mainPanel = new JPanel(new CardLayout());
		mainPanel.add(new JScrollPane(list), "list");
		mainPanel.add(new JScrollPane(tree), "tree");
		tree.setBuilder(new FileTreeBuilder());
		//add(mainPanel, new JScrollPane(tree));
		add(mainPanel, BorderLayout.CENTER);
//		add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(list), preview), BorderLayout.CENTER);
		if (indexes.getItemCount() > 0)
		{
			String lastIndex = jEdit.getProperty(LUCENE_SEARCH_INDEX);
			int index = 0;
			for (int i = 1; i < indexes.getItemCount(); i++)
			{
				if (indexes.getItemAt(i).equals(lastIndex))
				{
					index = i;
					break;
				}
			}
			indexes.setSelectedIndex(index);
		}

		maxResults.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (searchField.getText().length() != 0)
					actionListener.actionPerformed(null);
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				setLayoutByGeometry();
			}
		});

		indexes.addMouseListener(new MouseAdapter()
		{
			private JPopupMenu menu;
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (GUIUtilities.isRightButton(e.getModifiers()))
				{
					menu = new JPopupMenu();
					JMenuItem refresh = new JMenuItem("refresh");
					refresh.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							String selectedIndex = (String) indexes.getSelectedItem();
							Task task = new ReindexTask(selectedIndex, new Runnable()
							{
								public void run()
								{
									indexes.setEnabled(true);
								}
							});
							indexes.setEnabled(false);
							ThreadUtilities.runInBackground(task);
						}
					});
					menu.add(refresh);
					GUIUtilities.showPopupMenu(menu, indexes, e.getX(),  e.getY());
				}
			}
		});
		propertiesChanged(true);
	}

	private void setLayoutByGeometry()
	{
		boolean horizontal = (getWidth() > getHeight());
		if ((! firstTimeLayout) && (horizontal == horizontalLayout))
			return;
		firstTimeLayout = false;
		horizontalLayout = horizontal;
		Component c = ((BorderLayout)getLayout()).getLayoutComponent(
			BorderLayout.NORTH);
		if (c != null)
			remove(c);
		if (horizontal)
		{
			JPanel searchPanel = new JPanel(new BorderLayout());
			add(searchPanel, BorderLayout.NORTH);
			searchPanel.add(textLabel, BorderLayout.WEST);
			JPanel searchFieldContainer = new JPanel();
			searchFieldContainer.setLayout(new BoxLayout(searchFieldContainer,
				BoxLayout.PAGE_AXIS));
			searchFieldContainer.add(new JPanel());
			searchFieldContainer.add(searchField);
			searchFieldContainer.add(new JPanel());
			searchPanel.add(searchFieldContainer, BorderLayout.CENTER);
			JPanel p = new JPanel();
			searchPanel.add(BorderLayout.EAST, p);
			p.add(fileTypeLabel);
			p.add(type);
			p.add(lineResults);
			p.add(maxResultsLabel);
			p.add(maxResults);
			p.add(indexes);
			p.add(clear);
			p.add(multi);
			p.add(indexStatus);
		}
		else
		{
			JPanel searchPanel = new JPanel();
			searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
			add(searchPanel, BorderLayout.NORTH);
			JPanel p = new JPanel(new BorderLayout());
			searchPanel.add(p);
			p.add(textLabel, BorderLayout.WEST);
			p.add(searchField, BorderLayout.CENTER);
			p = new JPanel(new BorderLayout());
			searchPanel.add(p);
			JPanel typePanel = new JPanel();
			typePanel.add(fileTypeLabel, BorderLayout.WEST);
			typePanel.add(type, BorderLayout.CENTER);
			p.add(typePanel, BorderLayout.WEST);
			p.add(new JPanel(), BorderLayout.CENTER);
			p = new JPanel(new BorderLayout());
			searchPanel.add(p, BorderLayout.CENTER);
			p.add(lineResults);
			p = new JPanel(new BorderLayout());
			searchPanel.add(p);
			JPanel maxResultsPanel = new JPanel();
			maxResultsPanel.add(maxResultsLabel, BorderLayout.WEST);
			maxResultsPanel.add(maxResults, BorderLayout.CENTER);
			p.add(maxResultsPanel, BorderLayout.WEST);
			p.add(new JPanel(), BorderLayout.CENTER);
			JPanel bottom = new JPanel(new BorderLayout());
			searchPanel.add(bottom);
			p = new JPanel();
			bottom.add(p, BorderLayout.WEST);
			p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
			p.add(new JPanel());
			p.add(indexes);
			p.add(new JPanel());
			p = new JPanel();
			p.add(clear);
			p.add(multi);
			p.add(indexStatus);
			bottom.add(p, BorderLayout.EAST);
		}
		revalidate();
	}

	private static String getLabel(String prefix)
	{
		return jEdit.getProperty(prefix + ".label." +
			(OptionPane.getUseShortLabels() ? "short" : "long"));
	}

	private void updateMultiStatus()
	{
		if (multiStatus)
			multi.setIcon(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.multi.multiple.icon")));
		else
			multi.setIcon(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.multi.single.icon")));
	}

	private void toggleMultiStatus()
	{
		multiStatus = (! multiStatus);
		updateMultiStatus();
		tree.allowMultipleResults(multiStatus);
	}

	private Index getSelectedIndex()
	{
		String indexName = (String) indexes.getSelectedItem();
		if (indexName == null)
			return null;
		if (indexName == CURRENT_BUFFER)
		{
			Index index = new TemporaryIndex(CURRENT_BUFFER);
			jEdit.getActiveView().getBuffer().getPath();
			index.addFile(jEdit.getActiveView().getBuffer().getPath());
			index.optimize();
			index.commit();
			return index;
		}
		if (indexName == ALL_BUFFERS)
		{
			TemporaryIndex index = new TemporaryIndex(ALL_BUFFERS);
			Buffer[] buffers = jEdit.getBuffers();
			for (Buffer buffer : buffers)
			{
				index.addFile(buffer.getPath());
			}
			index.optimize();
			index.commit();
			return index;
		}
		return LucenePlugin.instance.getIndex(indexName);
	}

	public void search(String text, String fileType)
	{
		Index index = getSelectedIndex();
		if (index == null)
			return;
		int max = (Integer) maxResults.getValue();
		searchField.setText(text);
		type.setText(fileType);
		ThreadUtilities.runInBackground(new SearchQuery(index, text, fileType, max,
			lineResults.isSelected()));
	}

	public void goToNextResult()
	{
		if (tree != null)
			tree.goToNextLink();
	}

	public void goToPreviousResult()
	{
		if (tree != null)
			tree.goToPreviousLink();
	}

	public void setCurrentIndex(String name)
	{
		indexes.setSelectedItem(name);
		indexActionListener.actionPerformed(null);
		indexes.repaint();
	}

	//{{{ addNotify() method
	@Override
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
		// It is not sufficient to register to the edit bus. If new indexes
		// were created while this dockable was hidden (e.g. with MyDoggy),
		// it did not receive the edit bus messages and needs to refresh the
		// index list.
		indexModel.setIndexes(LucenePlugin.instance.getIndexes());
	} //}}}

	//{{{ removeNotify() method
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	} //}}}

	@EBHandler
	public void handleMessage(LuceneIndexUpdate msg)
	{
		Vector<String> items = new Vector<String>();
		for (int i = 0; i < indexModel.getSize(); i++)
		{
			String indexName = (String) indexModel.getElementAt(i);
			if (indexName != CURRENT_BUFFER && indexName != ALL_BUFFERS)
				items.add(indexName);
		}
		String indexName = (String) msg.getSource();
		if (msg.getWhat() == LuceneIndexUpdate.What.CREATED)
			items.add(indexName);
		else
			items.remove(indexName);
		String [] names = new String[items.size()];
		items.toArray(names);
		indexModel.setIndexes(names);
	}

	@EBHandler
	public void handlePropertiesChanged(PropertiesChanged msg)
	{
		propertiesChanged(false);
	}

	private void propertiesChanged(boolean firstTime)
	{
		boolean updateLayout = false;
		int columns = OptionPane.getSearchStringLength();
		if ((columns != 0) && (firstTime || (columns != searchField.getColumns())))
		{
			updateLayout = true;
			searchField.setColumns(columns);
		}
		boolean newShortLabels = OptionPane.getUseShortLabels();
		if (firstTime || (shortLabels != newShortLabels))
		{
			updateLayout = true;
			shortLabels = newShortLabels;
			lineResults.setText(getLabel("lucene.line-based"));
			fileTypeLabel.setText(getLabel("lucene.file-type"));
			maxResultsLabel.setText(getLabel("lucene.max-results"));
			textLabel.setText(getLabel("lucene.search-string"));
		}
		if (updateLayout)
			revalidate();
	}

	private class SearchQuery extends Task
	{
		private final Index index;
		private final String text;
		private final String fileType;
		private final int max;
		private final boolean lineResult;

		private SearchQuery(Index index, String text, String fileType, int max, boolean lineResult)
		{
			this.index = index;
			this.text = text;
			this.fileType = fileType;
			this.max = max;
			this.lineResult = lineResult;
		}

		@Override
		public void _run()
		{
			Log.log(Log.NOTICE, this, "Search for " + text + " in file type: " + fileType);
			ResultProcessor processor;
			final java.util.List<Object> files = new ArrayList<Object>();
			if (lineResult)
				processor = new MarkerListQueryProcessor(index, files, max);
			else
				processor = new FileListQueryProcessor(index, files, max);
			index.search(text, fileType, max, processor);
			ThreadUtilities.runInDispatchThread(new Runnable()
			{
				public void run()
				{
					if (lineResult)
					{
						SearchRootNode rootNode = new SearchRootNode(text);
						SourceLinkParentNode parent = tree.addSourceLinkParent(rootNode);
						FileMarker prev = null;
						int count = 0;
						for (Object o: files)
						{
							FileMarker marker = (FileMarker) o;
							if (marker.equals(prev))
							{
								Vector<FileMarker.Selection> selections = marker.getSelections();
								for (FileMarker.Selection selection: selections)
									prev.addSelection(selection);
							}
							else
							{
								parent.addSourceLink(marker);
								prev = marker;
								count++;
							}
						}
						rootNode.addChildCount(count);
						TreeModel model = tree.getModel();
						if (model instanceof DefaultTreeModel)
							((DefaultTreeModel)model).nodeChanged(parent);
						((CardLayout) mainPanel.getLayout()).show(mainPanel, "tree");
					}
					else
					{
						model.setFiles(files);
						((CardLayout) mainPanel.getLayout()).show(mainPanel, "list");
					}
				}
			});
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
			this.indexes = new String[indexes.length + 2];
			System.arraycopy(indexes, 0, this.indexes, 2, indexes.length);
			this.indexes[0] = CURRENT_BUFFER;
			this.indexes[1] = ALL_BUFFERS;
			if (indexes.length == 0)
			{
				selectedItem = null;
			}
			else
			{
				boolean selectedStillExists = false;
				for (String index : this.indexes)
				{
					if (StandardUtilities.objectsEqual(selectedItem, index))
					{
						selectedStillExists = true;
						break;
					}
				}
				if (!selectedStillExists)
					selectedItem = this.indexes[0];
			}
			fireContentsChanged(this, 0, this.indexes.length);
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

	private static class SearchRootNode implements SubtreePopupMenuProvider
	{
		private String text;
		SearchRootNode(String searchText)
		{
			text = searchText;
		}
		public String toString()
		{
			return text;
		}
		public void addPopupMenuItemsFor(JPopupMenu popup,
				final SourceLinkParentNode parent,
				final DefaultMutableTreeNode node)
		{
			if (popup == null)
				return;
			popup.add(new AbstractAction("Toggle marker(s)")
			{
				public void actionPerformed(ActionEvent e)
				{
					Vector<FileMarker> markers = parent.getFileMarkers(node);
					for (FileMarker marker: markers)
						MarkerSetsPlugin.toggleMarker(marker);
				}
			});
		}
		public void addChildCount(int count)
		{
			text = text + " (" + count + ")";
		}
	}

	private class MyActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String search = searchField.getText().trim();
			String fileType = type.getText().trim();
			search(search, fileType);
		}
	}

	public void focusOnDefaultComponent()
	{
		searchField.requestFocusInWindow();		
	}
}
