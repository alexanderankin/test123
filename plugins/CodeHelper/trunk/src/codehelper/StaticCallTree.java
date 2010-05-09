package codehelper;

import gatchan.jedit.lucene.LucenePlugin;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
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
		root.removeAllChildren();
		root.setUserObject(text);
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
				ProjectPlugin pv = (ProjectPlugin)
					jEdit.getPlugin("projectviewer.ProjectPlugin");
				if (pv == null)
					return;
				Vector<Object> results = new Vector<Object>();
				String name = ProjectViewer.getActiveProject(view).getName(); 
				LucenePlugin.search(name, text, 10, results);
				HashMap<String, Vector<Tag>> tagsPerFile = new
					HashMap<String, Vector<Tag>>();
				for (Object o: results)
				{
					if (! (o instanceof FileMarker))
						continue;
					final FileMarker m = (FileMarker) o;
					final String file = m.file;
					int line = m.getLine() + 1;
					if (! tagsPerFile.containsKey(file))
						tagsPerFile.put(file, getTagsOfFile(file));
					Vector<Tag> tags = tagsPerFile.get(m.file);
					if ((tags == null) || tags.isEmpty())
						continue;
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
							nearestLine = tagLine;
							nearestTag = tag;
						}
					}
					if (nearestTag == null)
						continue;
					final Tag newChild = nearestTag; 
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							parent.insert(new MarkerTreeNode(m, newChild.getName()),
								parent.getChildCount() - 1);
							model.nodeStructureChanged(parent);
						}
					});
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

	private void addLoadingChild(DefaultMutableTreeNode parent)
	{
		parent.add(new DefaultMutableTreeNode("Loading, please wait..."));
	}
	private Vector<Tag> getTagsOfFile(String file)
	{
		int fileId = CtagsInterfacePlugin.getDB().getSourceFileID(file);
		if (fileId == -1)
			return null;
		Query q = new Query(TagDB.TAGS_TABLE + ".*", TagDB.TAGS_TABLE,
			TagDB.TAGS_FILE_ID + "=" + fileId);
		return CtagsInterfacePlugin.query(q);
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
