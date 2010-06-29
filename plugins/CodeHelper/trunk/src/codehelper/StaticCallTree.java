package codehelper;

import gatchan.jedit.lucene.LucenePlugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import marker.FileMarker;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.ThreadUtilities;

import projectviewer.ProjectPlugin;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

import common.gui.HelpfulJTable;

import ctagsinterface.index.TagIndex;
import ctagsinterface.main.CtagsInterfacePlugin;
import ctagsinterface.main.Tag;

@SuppressWarnings("serial")
public class StaticCallTree extends JPanel
{
	private View view;
	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private HelpfulJTable table;
	private DefaultTableModel tableModel;
	private JSplitPane sp;

	public StaticCallTree(View view)
	{
		this.view = view;
		setLayout(new BorderLayout());
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		tree = new JTree(model);
		tree.setCellRenderer(new CallerTreeCellRenderer());
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {
			public void treeWillCollapse(TreeExpansionEvent event)
					throws ExpandVetoException
			{
			}
			public void treeWillExpand(TreeExpansionEvent event)
					throws ExpandVetoException
			{
				TreePath tp = event.getPath();
				MarkerTreeNode node = (MarkerTreeNode) tp.getLastPathComponent();
				expand(node, node.getName());
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e)
			{
				TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
				if (tp == null)
					return;
				MarkerTreeNode node = (MarkerTreeNode) tp.getLastPathComponent();
				if (e.isPopupTrigger())
				{
					tree.setSelectionPath(tp);
					node.goTo();
				}
				else
					updateMarkerView(node);
			}
		});
		tableModel = new DefaultTableModel(new String [] {"Line", "Call"}, 0);
		table = new HelpfulJTable();
		table.getColumnModel().setColumnMargin(10);
		table.setModel(tableModel);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				FileMarker m = ((FileMarkerWrapper) table.getValueAt(
					table.getSelectedRow(),1)).m;
				m.jump(StaticCallTree.this.view);
			}
		});
		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			new JScrollPane(tree), new JScrollPane(table));
		sp.setOneTouchExpandable(true);
		add(sp, BorderLayout.CENTER);
	}

	private void updateMarkerView(MarkerTreeNode node)
	{
		tableModel.setRowCount(0);
		for (FileMarker m: node.markers)
			tableModel.addRow(new Object[] {Integer.valueOf(m.getLine() + 1),
				new FileMarkerWrapper(m)});
	}
	public void showTreeFor(String text)
	{
		sp.setDividerLocation(0.5d);
		root.removeAllChildren();
		root.setUserObject(text);
		addLoadingChild(root);
		model.nodeStructureChanged(root);
		tableModel.setRowCount(0);
		expand(root, text);
	}
	
	public void expand(final DefaultMutableTreeNode parent, final String text)
	{
		if (parent instanceof MarkerTreeNode)
		{
			MarkerTreeNode mtn = (MarkerTreeNode)parent;
			if (mtn.expanded)
				return;
			mtn.expanded = true;
		}
		ThreadUtilities.runInBackground(new Runnable() {
			private MarkerTreeNode lastNode = null;
			public void run() {
				ProjectPlugin pv = (ProjectPlugin)
					jEdit.getPlugin("projectviewer.ProjectPlugin");
				if (pv == null)
					return;
				Vector<Object> results = new Vector<Object>();
				String name = getLuceneIndexName(); 
				LucenePlugin.search(name, text, 100, results);
				HashMap<String, Vector<Tag>> tagsPerFile = new
					HashMap<String, Vector<Tag>>();
				Tag lastTag = null;
				for (Object o: results)
				{
					if (! (o instanceof FileMarker))
						continue;
					final FileMarker m = (FileMarker) o;
					final String file = m.file;
					if (! tagsPerFile.containsKey(file))
						tagsPerFile.put(file, getTagsOfFile(file));
					Vector<Tag> tags = tagsPerFile.get(file);
					if ((tags == null) || tags.isEmpty())
						continue;
					Tag nearestTag = getContainingTag(tags, m, text);
					if (nearestTag == null)
						continue;
					if (lastTag == nearestTag)
						lastNode.addMarker(m);
					else
					{
						final Tag newChild = lastTag = nearestTag;
						lastNode = new MarkerTreeNode(m, newChild);
						final MarkerTreeNode fLastNode = lastNode;
						SwingUtilities.invokeLater(new Runnable() {
							public void run()
							{
								parent.insert(fLastNode,
									parent.getChildCount() - 1);
								model.nodeStructureChanged(parent);
							}
						});
					}
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						parent.remove(parent.getChildCount() - 1);
						model.nodeStructureChanged(parent);
					}
				});
			}
		});
	}

	private static class LuceneIndexSelector
	{
		static String index = null;
		static public String selectIndex()
		{
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run()
					{
						index = LucenePlugin.instance.chooseIndex();	
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return index;
		}
	}
	private String getLuceneIndexName()
	{
		VPTProject project = ProjectViewer.getActiveProject(view);
		if (project != null)
			return project.getName();
		return LuceneIndexSelector.selectIndex();
	}

	private final static String[] CONTAINER_KINDS = {
		"function", "subroutine", "macro"
	};

	private boolean isContainerTag(Tag tag)
	{
		String kind = tag.getKind();
		for (String k: CONTAINER_KINDS)
			if (k.equals(kind))
				return true;
		return false;
	}
	private Tag getContainingTag(Vector<Tag> tags, FileMarker marker,
		String text)
	{
		int line = marker.getLine() + 1;
		Tag nearestTag = null;
		int nearestLine = -1;
		for (Tag tag: tags)
		{
			String tagName = tag.getName();
			int tagLine = tag.getLine();
			if (tagName.equals(text) && (tagLine == line))
			{
				nearestTag = null;
				break;
			}
			if ((tagLine > nearestLine) && (tagLine < line))
			{
				if (isContainerTag(tag))
				{
					nearestLine = tagLine;
					nearestTag = tag;
				}
			}
		}
		return nearestTag;
	}
	private void addLoadingChild(DefaultMutableTreeNode parent)
	{
		parent.add(new DefaultMutableTreeNode("Loading, please wait..."));
	}
	private Vector<Tag> getTagsOfFile(String file)
	{
		Vector<Tag> tags = CtagsInterfacePlugin.query("_path:" +
			TagIndex.escape(file), 1000000);
		// Now update the file of all tags...
		for (Tag tag: tags)
			tag.setFile(file);
		return tags;
	}
	private class MarkerTreeNode extends DefaultMutableTreeNode
	{
		Vector<FileMarker> markers;
		boolean expanded;
		Tag tag;
		public MarkerTreeNode(FileMarker marker, Tag tag)
		{
			markers = new Vector<FileMarker>();
			markers.add(marker);
			this.tag = tag;
			expanded = false;
			addLoadingChild(this);
		}
		public void addMarker(FileMarker marker)
		{
			markers.add(marker);
		}
		public String toString()
		{
			StringBuffer s = new StringBuffer();
			s.append(tag.getName());
			String signature = tag.getExtension("signature");
			if (signature != null && signature.length() > 0)
				s.append(signature);
			String ns = tag.getNamespace();
			if ((ns != null) && (ns.length() > 0))
				s.append(" - " + ns);
			return s.toString();
		}
		public String getName()
		{
			return tag.getName();
		}
		public void goTo()
		{
			CtagsInterfacePlugin.jumpToTag(view, tag);
		}
	}

	private class CallerTreeCellRenderer extends DefaultTreeCellRenderer
	{

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			DefaultTreeCellRenderer r = (DefaultTreeCellRenderer)
				super.getTreeCellRendererComponent(tree, value, sel,
				expanded, leaf, row, hasFocus);
			if (value instanceof MarkerTreeNode)
				r.setIcon(((MarkerTreeNode)value).tag.getIcon());
			else
				r.setIcon(null);
			setToolTipText(value.toString());
			return r;
		}
	}
	private class FileMarkerWrapper
	{
		public FileMarker m;
		public FileMarkerWrapper(FileMarker m)
		{
			this.m = m;
		}
		public String toString()
		{
			return m.getLineText();
		}
	}
}
