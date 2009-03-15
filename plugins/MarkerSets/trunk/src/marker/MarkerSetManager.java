package marker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import marker.MarkerSetsPlugin.ChangeListener;
import marker.MarkerSetsPlugin.Event;
import marker.tree.FileTreeBuilder;
import marker.tree.FlatTreeBuilder;
import marker.tree.FolderTreeBuilder;
import marker.tree.MarkerTreeBuilder;
import marker.tree.SourceLinkTree;
import marker.tree.SourceLinkTree.SourceLinkParentNode;
import marker.tree.SourceLinkTree.SourceLinkTreeModelListener;
import marker.tree.SourceLinkTree.SourceLinkTreeNodeRenderer;

import org.gjt.sp.jedit.View;


@SuppressWarnings("serial")
public class MarkerSetManager extends JPanel {
	
	private View view;
	private SourceLinkTree markers;
	private JComboBox structure;
	private MarkerTreeBuilder [] builders;
	private JComboBox active;
	private boolean selfUpdate;
	
	public MarkerSetManager(View view)
	{
		super(new BorderLayout());
		this.view = view;
		JPanel northPanel = new JPanel();
		add(northPanel, BorderLayout.NORTH);
		builders = new MarkerTreeBuilder[] {
			new FlatTreeBuilder(),
			new FileTreeBuilder(),
			new FolderTreeBuilder()
		};
		JPanel structurePanel = new JPanel();
		structurePanel.setAlignmentX(0);
		northPanel.add(structurePanel);
		structurePanel.add(new JLabel("Group markers by:"));
		structure = new JComboBox(builders);
		structurePanel.add(structure);
		markers = new SourceLinkTree(view);
		markers.setCellRenderer(new MarkerSetRenderer());
		add(new JScrollPane(markers), BorderLayout.CENTER);
		updateTree();
		MarkerSetsPlugin.addChangeListener(new ChangeListener() {
			public void changed(Event e, FileMarker m, MarkerSet ms) {
				updateTree();
			}
		});
		structure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MarkerTreeBuilder builder =
					(MarkerTreeBuilder) structure.getSelectedItem();
				markers.setBuilder(builder);
			}
		});
		structure.setSelectedIndex(0);
		JPanel activePanel = new JPanel();
		northPanel.add(activePanel);
		activePanel.add(new JLabel("Active marker set:"));
		active = new JComboBox(MarkerSetsPlugin.getMarkerSetNames());
		activePanel.add(active);
		active.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				MarkerSetsPlugin.setActiveMarkerSet(active.getSelectedItem().toString());
			}
		});
		JButton newMarkerSet = new JButton("New...");
		northPanel.add(newMarkerSet);
		newMarkerSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MarkerSetsPlugin.setActiveMarkerSet(MarkerSetManager.this.view);
			}
		});
		markers.addSourceLinkTreeModelListener(new MarkerTreeListener());
		selfUpdate = false;
	}
	
	public void updateTree()
	{
		if (selfUpdate)
			return;
		markers.clear();
		Vector<String> names = MarkerSetsPlugin.getMarkerSetNames();
		for (String name: names)
		{
			MarkerSet ms = MarkerSetsPlugin.getMarkerSet(name);
			SourceLinkParentNode msNode = markers.addSourceLinkParent(ms);
			Vector<FileMarker> children = ms.getMarkers();
			for (FileMarker marker: children)
				msNode.addSourceLink(marker);
		}
		for (int i = 0; i < markers.getRowCount(); i++)
			markers.expandRow(i);
	}

	private class MarkerTreeListener implements SourceLinkTreeModelListener
	{
		public void nodeRemoved(DefaultMutableTreeNode node,
			SourceLinkParentNode parent, Vector<DefaultMutableTreeNode> leafs)
		{
			if (parent == null)
				return;
			selfUpdate = true;
			MarkerSet ms = (MarkerSet) parent.getUserObject();
			if (node == parent)
				MarkerSetsPlugin.removeMarkerSet(ms);
			else {
				for (DefaultMutableTreeNode leaf: leafs)
					ms.remove((FileMarker) leaf.getUserObject());
			}
			selfUpdate = false;
		}
	}

	private class MarkerSetRenderer extends SourceLinkTreeNodeRenderer
	{
		private HashMap<MarkerSet, MarkerSetIcon> icons;
		
		public MarkerSetRenderer() {
			super();
			icons = new HashMap<MarkerSet, MarkerSetIcon>();
		}
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			Component c = super.getTreeCellRendererComponent(tree, value, sel,
				expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			if (obj instanceof MarkerSet)
			{
				MarkerSet ms = (MarkerSet) obj;
				JLabel l = (JLabel) c;
				l.setText(ms.getName());
				MarkerSetIcon icon = icons.get(ms);
				if (icon == null)
				{
					icon = new MarkerSetIcon(ms);
					icons.put(ms, icon);
				}
				l.setIcon(icon);
			}
			return c;
		}
		
		private class MarkerSetIcon implements Icon
		{
			private MarkerSet ms;
			public MarkerSetIcon(MarkerSet ms) {
				this.ms = ms;
			}
			public Color getColor() {
				return ms.getColor();
			}
			public int getIconHeight() {
				return getDefaultIconHeight();
			}
			public int getIconWidth() {
				return getDefaultIconWidth();
			}
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Color prevColor = g.getColor();
				g.setColor(ms.getColor());
				g.fillRect(0, 0, getIconWidth(), getDefaultIconHeight());
				g.setColor(prevColor);
				
			}
		}
	}
}
