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
import javax.swing.tree.TreeSelectionModel;

import marker.FileMarker;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;


@SuppressWarnings("serial")
public class SourceLinkTree extends JTree
{
	private View view;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;
	private MarkerTreeBuilder builder;
	private HashSet<SourceLinkTreeModelListener> listeners;
	private boolean multiple;
	
	public SourceLinkTree(View view)
	{
		this.view = view;
		multiple = true;
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1)
					return;
				TreePath tp = SourceLinkTree.this.getPathForLocation(
					e.getX(), e.getY());
				if (tp == null)
					return;
				final DefaultMutableTreeNode node =
					(DefaultMutableTreeNode) tp.getLastPathComponent();
				Object obj = node.getUserObject();
				if (obj instanceof FileMarker) {
					FileMarker marker = (FileMarker) obj;
					marker.jump(SourceLinkTree.this.view);
				}
				e.consume();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON3)
					return;
				TreePath tp = SourceLinkTree.this.getPathForLocation(
					e.getX(), e.getY());
				if (tp == null)
					return;
				final DefaultMutableTreeNode node =
					(DefaultMutableTreeNode) tp.getLastPathComponent();
				SourceLinkTree.this.setSelectionPath(tp);
				JPopupMenu p = new JPopupMenu();
				// Allow node-specific context menu items 
				if (node instanceof PopupMenuProvider)
					((PopupMenuProvider) node).addPopupMenuItems(p);
				// Allow SourceLinkParent objects to provide context menu items
				// for all descendant nodes.
				for (Object pathNode: tp.getPath())
				{
					if (pathNode instanceof SourceLinkParentNode)
						((SourceLinkParentNode) pathNode).addSubtreePopupMenuItems(p, node);
				}
				// Common items for all nodes
				p.add(new AbstractAction("Remove") {
					public void actionPerformed(ActionEvent e) {
						removeNode(node);
					}
				});
				p.show(SourceLinkTree.this, e.getX(), e.getY());
				e.consume();
			}
		});
	}

	public void allowMultipleResults(boolean multiple)
	{
		if (this.multiple == multiple)
			return;
		this.multiple = multiple;
		if ((! multiple) && (root.getChildCount() > 0))
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getLastChild();
			clear();
			root.add(node);
			model.nodeStructureChanged(root);
		}
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
		if (! multiple)
			clear();
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

	// An interface for nodes to provide their pop-up menus
	
	private interface PopupMenuProvider
	{
		void addPopupMenuItems(JPopupMenu popup);
	}

	/*
	 * SourceLinkLeafNode represents a file marker node.
	 */
	
	private class SourceLinkLeafNode extends DefaultMutableTreeNode
		implements PopupMenuProvider
	{
		public SourceLinkLeafNode(FileMarker marker)
		{
			super(marker);
		}
		private FileMarker getMarker()
		{
			return (FileMarker) getUserObject();
		}
		public void addPopupMenuItems(JPopupMenu popup)
		{
			if (popup == null)
				return;
			popup.add(new AbstractAction("Open in split pane") {
				public void actionPerformed(ActionEvent e) {
					view.splitHorizontally();
					goToNode(view);
				}
			});
			popup.add(new AbstractAction("Open in new view") {
				public void actionPerformed(ActionEvent e) {
					View newView = jEdit.newView(view);
					jEdit.openFile(newView, getMarker().file);
					goToNode(newView);
				}
			});
		}
		private void goToNode(View v)
		{
			getMarker().jump(v);
		}
	}
	
	// An interface for source link parent objects to provide pop-up menus for their
	// descendant nodes, which are created by the source link tree.
	
	public interface SubtreePopupMenuProvider
	{
		void addPopupMenuItemsFor(JPopupMenu popup, SourceLinkParentNode parent,
			DefaultMutableTreeNode node);
	}

	/*
	 * SourceLinkParentNode represents a root node for a list of
	 * FileMarker nodes (SourceLinkNode objects) that can be
	 * arranged in various ways underneath it.
	 */
	
	public class SourceLinkParentNode extends DefaultMutableTreeNode
		implements PopupMenuProvider
	{
		private MarkerTreeBuilder builder;
		
		public SourceLinkParentNode(Object userObject, MarkerTreeBuilder builder)
		{
			super(userObject);
			this.builder = builder;;
		}
		public void addSourceLink(FileMarker marker)
		{
			add(new SourceLinkLeafNode(marker));
			updateStructure();
			model.nodeStructureChanged(this);
		}
		@SuppressWarnings("unchecked")
		public int [] getFileAndMarkerCounts()
		{
			int [] counts = new int[] { 0, 0 };
			HashSet<String> files = new HashSet<String>();
			Enumeration nodes = this.breadthFirstEnumeration();
			while (nodes.hasMoreElements())
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					nodes.nextElement();
				Object obj = node.getUserObject();
				if (obj instanceof FileMarker)
				{
					counts[1]++;
					FileMarker marker = (FileMarker) obj;
					if (files.add(marker.file))
						counts[0]++;
				}
			}
			return counts;
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
			Vector<DefaultMutableTreeNode> leafs = collectMarkers(this);
			removeAllChildren();
			builder.buildSubTree(this, leafs);
			model.nodeStructureChanged(this);
		}
		public void setBuilder(MarkerTreeBuilder builder)
		{
			if (this.builder == builder)
				return;
			this.builder = builder;
			rebuild();
		}
		private Vector<DefaultMutableTreeNode> collectMarkers(
			DefaultMutableTreeNode node)
		{
			Vector<DefaultMutableTreeNode> leafs =
				new Vector<DefaultMutableTreeNode>();
			DefaultMutableTreeNode leaf = node.getFirstLeaf();
			do
			{
				if (leaf instanceof SourceLinkParentNode)
					break;	// Do not collect source link parents...
				if (! (leaf.getUserObject() instanceof FileMarker))
					continue;
				leafs.add(leaf);
				leaf = leaf.getNextLeaf();
			} while ((leaf != null) && node.isNodeDescendant(leaf));
			return leafs;
		}
		// Returns the markers under the given node
		public Vector<FileMarker> getFileMarkers(DefaultMutableTreeNode node)
		{
			Vector<FileMarker> markers = new Vector<FileMarker>();
			Vector<DefaultMutableTreeNode> markerNodes = collectMarkers(node);
			for (DefaultMutableTreeNode markerNode: markerNodes)
			{
				FileMarker marker = (FileMarker) markerNode.getUserObject();
				markers.add(marker);
			}
			return markers;
		}
		// This allows the user object of the SourceLinkParent node to
		// provide context menu actions for all children.
		public void addSubtreePopupMenuItems(JPopupMenu popup, DefaultMutableTreeNode node)
		{
			if (popup == null)
				return;
			Object userObj = getUserObject();
			if (userObj instanceof SubtreePopupMenuProvider)
				((SubtreePopupMenuProvider) userObj).addPopupMenuItemsFor(popup, this, node);
		}
		public void addPopupMenuItems(JPopupMenu popup)
		{
			if (popup == null)
				return;
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
					l.setText((marker.getLine() + 1) + ": " + marker.getLineText());
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
		if (node.getParent() != null)
			model.removeNodeFromParent(node);
		else
		{
			int i = root.getIndex(node);
			root.remove(i);
			model.nodesWereRemoved(root, new int [] {i}, new Object [] { node });
		}
	}
}
