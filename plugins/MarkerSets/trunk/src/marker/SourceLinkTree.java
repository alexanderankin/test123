package marker;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

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
				TreePath tp = SourceLinkTree.this.getPathForLocation(
					e.getX(), e.getY());
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tp.getLastPathComponent();
				Object obj = node.getUserObject();
				if (obj instanceof FileMarker) {
					FileMarker marker = (FileMarker) obj;
					marker.jump(SourceLinkTree.this.view);
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
			} while (leaf != null);
			return leafs;
		}
	}
	
	static public class SourceLinkTreeNodeRenderer extends DefaultTreeCellRenderer
	{
		public SourceLinkTreeNodeRenderer()
		{
			setOpenIcon(null);
			setClosedIcon(null);
			setLeafIcon(null);
		}
	}
	
	public interface MarkerTreeBuilder
	{
		boolean rebuildOnChange();
		void buildSubTree(SourceLinkParentNode parent,
			Vector<DefaultMutableTreeNode> markerNodes);
	}
	
	static public class FlatTreeBuilder implements MarkerTreeBuilder
	{
		public boolean rebuildOnChange()
		{
			return false;
		}
		public void buildSubTree(SourceLinkParentNode parent,
			Vector<DefaultMutableTreeNode> markerNodes)
		{
			for (DefaultMutableTreeNode markerNode: markerNodes)
				parent.add(markerNode);
		}
	}
	
	static public class FileTreeBuilder implements MarkerTreeBuilder
	{
		public boolean rebuildOnChange()
		{
			return true;
		}
		public void buildSubTree(SourceLinkParentNode parent,
			Vector<DefaultMutableTreeNode> markerNodes)
		{
			// First, group the marker nodes by file
			HashMap<String, Vector<DefaultMutableTreeNode>> fileNodes =
				new HashMap<String, Vector<DefaultMutableTreeNode>>();
			for (DefaultMutableTreeNode markerNode: markerNodes)
			{
				FileMarker marker = (FileMarker) markerNode.getUserObject();
				String file = marker.file;
				Vector<DefaultMutableTreeNode> markers = fileNodes.get(file);
				if (markers == null)
				{
					markers = new Vector<DefaultMutableTreeNode>();
					fileNodes.put(file, markers);
				}
				markers.add(markerNode);
			}
			// Now, build the sub tree
			Vector<String> paths = new Vector<String>(fileNodes.keySet());
			Collections.sort(paths);
			for (String path: paths)
			{
				DefaultMutableTreeNode pathNode = new DefaultMutableTreeNode(path);
				parent.add(pathNode);
				for (DefaultMutableTreeNode markerNode: fileNodes.get(path))
					pathNode.add(markerNode);
			}
		}
	}
	
	static public class FolderTreeBuilder implements MarkerTreeBuilder
	{
		public boolean rebuildOnChange()
		{
			return true;
		}
		@SuppressWarnings("unchecked")
		public void buildSubTree(SourceLinkParentNode parent,
			Vector<DefaultMutableTreeNode> markerNodes)
		{
			// First break all paths to parts
			HashMap<String, Object> paths = new HashMap<String, Object>();
			HashMap<String, Object> current;
			for (DefaultMutableTreeNode markerNode: markerNodes)
			{
				FileMarker marker = (FileMarker) markerNode.getUserObject();
				current = paths;
				String path = marker.file;
				String [] parts = path.split("[/\\\\]");
				for (int i = 0; i < parts.length - 1; i++)
				{
					HashMap<String, Object> child =
						(HashMap<String, Object>) current.get(parts[i]);
					if (child == null)
					{
						child = new HashMap<String, Object>();
						current.put(parts[i], child);
					}
					current = child;
				}
				String filePart = parts[parts.length - 1];
				Vector<DefaultMutableTreeNode> markers =
					(Vector<DefaultMutableTreeNode>) current.get(filePart);
				if (markers == null)
				{
					markers = new Vector<DefaultMutableTreeNode>();
					current.put(filePart, markers);
				}
				markers.add(markerNode);
			}
			// Now, consolidate matching parts
			consolidateTree("", parent, paths);
		}
		
		@SuppressWarnings("unchecked")
		private void consolidateTree(String parent,
			DefaultMutableTreeNode parentNode, HashMap<String, Object> paths)
		{
			Vector<String> roots = new Vector<String>(paths.keySet());
			Collections.sort(roots);
			HashMap<String, Object> current = paths;
			for (String r: roots)
			{
				String childPath = parent + File.separator + r;
				Object child = current.get(r);
				if (child instanceof HashMap)
				{
					current = (HashMap<String, Object>) child;
					if (current.keySet().size() == 1)
						consolidateTree(childPath, parentNode, current);
					else
					{
						DefaultMutableTreeNode childNode =
							new DefaultMutableTreeNode(childPath);
						parentNode.add(childNode);
						consolidateTree("", childNode, current);
					}
				}
				else
				{
					DefaultMutableTreeNode fileNode =
						new DefaultMutableTreeNode(childPath);
					parentNode.add(fileNode);
					Vector<DefaultMutableTreeNode> markers =
						(Vector<DefaultMutableTreeNode>) child;
					for (DefaultMutableTreeNode marker: markers)
						fileNode.add(marker);
				}
			}
		}
	}

}
