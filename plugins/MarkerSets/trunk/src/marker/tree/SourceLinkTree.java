package marker.tree;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JLabel;
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
	
	public SourceLinkTree(View view)
	{
		this.view = view;
		builder = new FlatTreeBuilder();
		root = new DefaultMutableTreeNode();
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
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tp.getLastPathComponent();
				switch (e.getButton()) {
				case MouseEvent.BUTTON3:
					model.removeNodeFromParent(node);
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
				model.removeNodeFromParent(node);
		}
	}
	
	public class SourceLinkParentNode extends DefaultMutableTreeNode
	{
		private MarkerTreeBuilder builder;
		
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
					model.removeNodeFromParent(node);
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
			rebuild();
		}
		
		private Vector<DefaultMutableTreeNode> collectLeafs() {
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
					l.setText(marker.line + ": " + marker.getLineText());
				}
			}
			return c;
		}

	}

}
