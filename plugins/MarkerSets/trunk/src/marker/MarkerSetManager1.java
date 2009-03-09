package marker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import marker.MarkerSetsPlugin.ChangeListener;
import marker.MarkerSetsPlugin.Event;
import marker.SourceLinkTree.SourceLinkParentNode;

import org.gjt.sp.jedit.View;

@SuppressWarnings("serial")
public class MarkerSetManager1 extends JPanel {
	private SourceLinkTree markers;
	private JComboBox structure;
	private SourceLinkTree.MarkerTreeBuilder [] builders;
	
	public MarkerSetManager1(View view)
	{
		super(new BorderLayout());
		builders = new SourceLinkTree.MarkerTreeBuilder[] {
			new SourceLinkTree.FlatTreeBuilder(),
			new SourceLinkTree.FileTreeBuilder(),
			new SourceLinkTree.FolderTreeBuilder()
		};
		JPanel structurePanel = new JPanel();
		structurePanel.setAlignmentX(0);
		add(structurePanel, BorderLayout.NORTH);
		structurePanel.add(new JLabel("Group markers by:"));
		structure = new JComboBox(builders);
		structurePanel.add(structure);
		markers = new SourceLinkTree(view);
		markers.setCellRenderer(new MarkerSetRenderer());
		add(new JScrollPane(markers), BorderLayout.CENTER);
		updateTree();
		MarkerSetsPlugin.addChangeListener(new ChangeListener() {
			public void changed(Event e, Object o) {
				updateTree();
			}
		});
		structure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SourceLinkTree.MarkerTreeBuilder builder =
					(SourceLinkTree.MarkerTreeBuilder) structure.getSelectedItem();
				markers.setBuilder(builder);
			}
		});
		structure.setSelectedIndex(0);
	}
	
	public void updateTree()
	{
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
	
	private class MarkerSetRenderer extends DefaultTreeCellRenderer
	{
		private HashMap<MarkerSet, MarkerSetIcon> icons;
		private int iconWidth, iconHeight;
		
		public MarkerSetRenderer() {
			icons = new HashMap<MarkerSet, MarkerSetIcon>();
			iconWidth = getOpenIcon().getIconWidth();
			iconHeight = getOpenIcon().getIconHeight();
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
			MarkerSet ms = null;
			if (obj instanceof MarkerSet)
			{
				ms = (MarkerSet) obj;
				value = ms.getName();
			}
			Component c = super.getTreeCellRendererComponent(tree, value, sel,
				expanded, leaf, row, hasFocus);
			if (ms != null)
			{
				MarkerSetIcon icon = icons.get(ms);
				if (icon == null)
				{
					icon = new MarkerSetIcon(ms);
					icons.put(ms, icon);
				}
				setIcon(icon);
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
				return iconHeight;
			}
			public int getIconWidth() {
				return iconWidth;
			}
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Color prevColor = g.getColor();
				g.setColor(ms.getColor());
				g.fillRect(0, 0, iconWidth, iconHeight);
				g.setColor(prevColor);
				
			}
		}
	}
}
