package marker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import marker.MarkerSetsPlugin.ChangeListener;
import marker.MarkerSetsPlugin.Event;

import org.gjt.sp.jedit.View;

@SuppressWarnings("serial")
public class MarkerSetManager extends JPanel {
	private View view;
	private DefaultTreeModel model;
	private JTree markers;
	private DefaultMutableTreeNode root;
	
	public MarkerSetManager(View view)
	{
		super(new BorderLayout());
		this.view = view;
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		markers = new JTree(model);
		markers.setRootVisible(false);
		markers.setShowsRootHandles(true);
		DefaultTreeCellRenderer renderer = new MarkerSetRenderer();
		markers.setCellRenderer(renderer);
		add(markers, BorderLayout.CENTER);
		updateTree();
		markers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				TreePath tp = markers.getPathForLocation(e.getX(), e.getY());
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tp.getLastPathComponent();
				Object obj = node.getUserObject();
				if (obj instanceof FileMarker) {
					FileMarker marker = (FileMarker) obj;
					marker.jump(MarkerSetManager.this.view);
				}
			}
		});
		MarkerSetsPlugin.addChangeListener(new ChangeListener() {
			public void changed(Event e, Object o) {
				updateTree();
			}
		});
	}
	
	public void updateTree()
	{
		Vector<String> names = MarkerSetsPlugin.getMarkerSetNames();
		for (String name: names)
		{
			MarkerSet ms = MarkerSetsPlugin.getMarkerSet(name);
			DefaultMutableTreeNode msNode = new DefaultMutableTreeNode(ms);
			root.add(msNode);
			Vector<FileMarker> children = ms.getMarkers();
			for (FileMarker marker: children)
				msNode.add(new DefaultMutableTreeNode(marker));
		}
		model.nodeStructureChanged(root);
	}
	
	private class MarkerSetRenderer extends DefaultTreeCellRenderer
	{
		public MarkerSetRenderer() {
			setOpenIcon(null);
			setClosedIcon(null);
			setLeafIcon(null);
		}
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			if (obj instanceof MarkerSet)
				value = ((MarkerSet)obj).getName();
			return super.getTreeCellRendererComponent(tree, value, sel, expanded,
				leaf, row, hasFocus);
		}
	}
}
