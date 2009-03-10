package marker.tree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import marker.FileMarker;

import org.gjt.sp.jedit.View;


@SuppressWarnings("serial")
public class SourceLinkTree extends JTree
{
	private View view;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;
	private MarkerTreeBuilder builder;
	private HashSet<SourceLinkTreeModelListener> listeners;
	
	public SourceLinkTree(View view)
	{
		this.view = view;
		builder = new FlatTreeBuilder();
		root = new DefaultMutableTreeNode();
		listeners = new HashSet<SourceLinkTreeModelListener>(); 
		model = new DefaultTreeModel(root);
		setModel(model);
		setRootVisible(false);
		setShowsRootHandles(true);
		DefaultTreeCellRenderer renderer = new SourceLinkTreeNodeRenderer();
		setCellRenderer(renderer);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isConsumed())
					return;
				TreePath tp = SourceLinkTree.this.getPathForLocation(
					e.getX(), e.getY());
				if (tp == null)
					return;
				final DefaultMutableTreeNode node =
					(DefaultMutableTreeNode) tp.getLastPathComponent();
				switch (e.getButton()) {
				case MouseEvent.BUTTON3:
					JPopupMenu p;
					if (node instanceof SourceLinkParentNode)
						p = ((SourceLinkParentNode) node).getPopupMenu();
					else
						p = new JPopupMenu();
					p.add(new AbstractAction("Remove") {
						public void actionPerformed(ActionEvent e) {
							removeNode(node);
						}
					});
					p.show(SourceLinkTree.this, e.getX(), e.getY());
					break;
				case MouseEvent.BUTTON1:
					Object obj = node.getUserObject();
					if (obj instanceof FileMarker) {
						FileMarker marker = (FileMarker) obj;
						marker.jump(SourceLinkTree.this.view);
					}
					break;
				}
			}
		});
	}

	public void setBuilder(MarkerTreeBuilder builder)
	{
		if (this.builder == builder)
			return;
		this.builder = builder;
		for (int i = 0; i < root.getChildCount(); i++)
		{
			SourceLinkParentNode node = (SourceLinkParentNode)
				root.getChildAt(i);
			node.setBuilder(builder);
		}
	}
	
	public void clear()
	{
		root.removeAllChildren();
		model.nodeStructureChanged(root);
	}
	
	public SourceLinkParentNode addSourceLinkParent(Object parent)
	{
		SourceLinkParentNode node = new SourceLinkParentNode(parent, builder);
		root.add(node);
		model.nodeStructureChanged(root);
		return node;
	}

	public void removeSourceLinkParent(Object parent)
	{
		for (int i = 0; i < root.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				root.getChildAt(i);
			if (node.getUserObject() == parent)
				removeNode(node);
		}
	}

	public interface SourceLinkTreeModelListener
	{
		void nodeRemoved(DefaultMutableTreeNode node, SourceLinkParentNode parent,
			Vector<DefaultMutableTreeNode> nodeLeafs);
	}
	
	public void addSourceLinkTreeModelListener(SourceLinkTreeModelListener l)
	{
		listeners.add(l);
	}
	
	public void removeSourceLinkTreeModelListener(SourceLinkTreeModelListener l)
	{
		listeners.remove(l);
	}
	
	/*
	 * SourceLinkParentNode represents a root node for a list of
	 * FileMarker nodes (SourceLinkNode objects) that can be
	 * arranged in various ways underneath it.
	 */
	
	public class SourceLinkParentNode extends DefaultMutableTreeNode
	{
		private MarkerTreeBuilder builder;
		private JPopupMenu popup;
		
		public SourceLinkParentNode(Object userObject, MarkerTreeBuilder builder)
		{
			super(userObject);
			this.builder = builder;;
		}
		public void addSourceLink(FileMarker marker)
		{
			add(new DefaultMutableTreeNode(marker));
			updateStructure();
			model.nodeStructureChanged(this);
		}
		public void removeSourceLink(FileMarker marker)
		{
			for (int i = 0; i < root.getChildCount(); i++)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					root.getChildAt(i);
				if (node.getUserObject() == marker)
					removeNode(node);
			}
			updateStructure();
		}
		private void updateStructure()
		{
			if (builder.rebuildOnChange())
				rebuild();
		}
		private void rebuild()
		{
			Vector<DefaultMutableTreeNode> leafs = collectLeafs();
			removeAllChildren();
			builder.buildSubTree(this, leafs);
			model.nodeStructureChanged(this);
		}
		public void setBuilder(MarkerTreeBuilder builder)
		{
			if (this.builder == builder)
				return;
			this.builder = builder;
			popup = null; // Change list of builders in popup ... 
			rebuild();
		}
		private Vector<DefaultMutableTreeNode> collectLeafs()
		{
			Vector<DefaultMutableTreeNode> leafs =
				new Vector<DefaultMutableTreeNode>();
			if (isLeaf())
				return leafs;
			DefaultMutableTreeNode leaf = getFirstLeaf();
			do
			{
				leafs.add(leaf);
				leaf = leaf.getNextLeaf();
			} while ((leaf != null) && isNodeDescendant(leaf));
			return leafs;
		}
		public JPopupMenu getPopupMenu()
		{
			if (popup != null)
				return popup;
			popup = new JPopupMenu();
			if (! (builder instanceof FolderTreeBuilder)) {
				popup.add(new AbstractAction("Group by folder") {
					public void actionPerformed(ActionEvent e) {
						setBuilder(new FolderTreeBuilder());
					}
				});
			}
			if (! (builder instanceof FileTreeBuilder))	{
				popup.add(new AbstractAction("Group by file") {
					public void actionPerformed(ActionEvent e) {
						setBuilder(new FileTreeBuilder());
					}
				});
			}
			if (! (builder instanceof FlatTreeBuilder)) {
				popup.add(new AbstractAction("Flat list") {
					public void actionPerformed(ActionEvent e) {
						setBuilder(new FlatTreeBuilder());
					}
				});
			}
			return popup;
		}
	}
	
	static public class SourceLinkTreeNodeRenderer extends DefaultTreeCellRenderer
	{
		private int iconWidth, iconHeight;
		
		public SourceLinkTreeNodeRenderer()
		{
			iconWidth = getOpenIcon().getIconWidth();
			iconHeight = getOpenIcon().getIconHeight();
			setOpenIcon(null);
			setClosedIcon(null);
			setLeafIcon(null);
		}
		public int getDefaultIconWidth()
		{
			return iconWidth;
		}
		public int getDefaultIconHeight()
		{
			return iconHeight;
		}
		public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus)
		{
			Component c = super.getTreeCellRendererComponent(tree, value, sel,
				expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			if (obj instanceof FileMarker)
			{
				if (! (node.getParent() instanceof SourceLinkParentNode))
				{
					// No need to include path in marker
					FileMarker marker = (FileMarker) obj;
					JLabel l = (JLabel) c;
					l.setText(marker.getLine() + ": " + marker.getLineText());
				}
			}
			return c;
		}

	}

	@SuppressWarnings("unchecked")
	private void removeNode(DefaultMutableTreeNode node)
	{
		if (listeners.size() > 0)
		{
			// Find the source link parent and the leafs of the node.
			DefaultMutableTreeNode parent = node;
			while (parent != null && !(parent instanceof SourceLinkParentNode))
				parent = (DefaultMutableTreeNode) parent.getParent();
			Vector<DefaultMutableTreeNode> leafs =
				new Vector<DefaultMutableTreeNode>();
			Enumeration nodes = node.depthFirstEnumeration();
			while (nodes.hasMoreElements())
			{
				DefaultMutableTreeNode n = (DefaultMutableTreeNode)
					nodes.nextElement();
				if (n.isLeaf())
					leafs.add(n);
			}
			for (SourceLinkTreeModelListener listener: listeners)
				listener.nodeRemoved(node, (SourceLinkParentNode) parent,
					leafs);
		}
		model.removeNodeFromParent(node);
	}
}
