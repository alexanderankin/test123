package codehelper;

import gatchan.jedit.lucene.LucenePlugin;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import marker.FileMarker;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.ThreadUtilities;

import ctagsinterface.db.Query;
import ctagsinterface.db.TagDB;
import ctagsinterface.main.CtagsInterfacePlugin;
import ctagsinterface.main.Tag;

import projectviewer.ProjectPlugin;
import projectviewer.ProjectViewer;

@SuppressWarnings("serial")
public class StaticCallTree extends JPanel
{
	private View view;
	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	public StaticCallTree(View view)
	{
		this.view = view;
		setLayout(new BorderLayout());
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		tree = new JTree(model);
		tree.setRootVisible(false);
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
				expand(node, node.function);
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
				MarkerTreeNode node = (MarkerTreeNode) tp.getLastPathComponent();
				node.goTo();
			}
		});
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	public void showTreeFor(String text)
	{
		addLoadingChild(root);
		model.nodeStructureChanged(root);
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
			public void run() {
				final Vector<MarkerTreeNode> children = new Vector<MarkerTreeNode>();
				ProjectPlugin pv = (ProjectPlugin)
					jEdit.getPlugin("projectviewer.ProjectPlugin");
				if (pv == null)
					return;
				Vector<Object> results = new Vector<Object>();
				String name = ProjectViewer.getActiveProject(view).getName(); 
				LucenePlugin.search(name, text, 10, results);
				for (Object o: results)
				{
					if (! (o instanceof FileMarker))
						return;
					final FileMarker m = (FileMarker) o;
					final Tag best = getFunctionContainingReference(m, text);
					if (best == null)
						continue;
					children.add(new MarkerTreeNode(m, best.getName()));
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							parent.insert(new MarkerTreeNode(m, best.getName()),
								parent.getChildCount() - 1);
							model.nodeStructureChanged(parent);
						}
					});
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						parent.remove(parent.getChildCount() - 1);
					}
				});
			}
		});
	}

	private void addLoadingChild(DefaultMutableTreeNode parent)
	{
		parent.add(new DefaultMutableTreeNode("Loading, please wait..."));
	}
	private Tag getFunctionContainingReference(FileMarker m, String text)
	{
		int line = m.getLine() + 1;	// FileMarker starts from line 0
		Query q = new Query();
		q.addColumn(TagDB.TAGS_TABLE + ".*");
		q.addColumn(TagDB.FILES_TABLE + "." + TagDB.FILES_NAME);
		q.addTable(TagDB.TAGS_TABLE);
		q.addTable(TagDB.FILES_TABLE);
		q.addCondition(TagDB.TAGS_TABLE + "." + TagDB.TAGS_FILE_ID + "=" +
			TagDB.FILES_TABLE + "." + TagDB.FILES_ID);
		q.addCondition(TagDB.FILES_TABLE + "." + TagDB.FILES_NAME + "=" +
			"'" + m.file + "'");
		q.addCondition(TagDB.TAGS_TABLE + "." + TagDB.TAGS_LINE + "<=" + line);
		q.setOrder(TagDB.TAGS_LINE + " DESC");
		q.setLimit(1);
		Vector<Tag> tags = CtagsInterfacePlugin.query(q);
		if ((tags == null) || tags.isEmpty())
			return null;
		Tag t = tags.get(0);
		if (t.getName().equals(text) && (t.getLine() == line))
			return null;
		return t;
	}
	private class MarkerTreeNode extends DefaultMutableTreeNode
	{
		FileMarker marker;
		String function;
		boolean expanded;
		public MarkerTreeNode(FileMarker marker, String function)
		{
			this.marker = marker;
			this.function = function;
			expanded = false;
			addLoadingChild(this);
		}
		
		public String toString()
		{
			return function;
		}
		public void goTo()
		{
			marker.jump(view);
		}
	}
}
