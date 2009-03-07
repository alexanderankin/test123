package marker;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

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
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);
		markers.setCellRenderer(renderer);
		add(markers, BorderLayout.CENTER);
	}
	
	public void init()
	{
		Vector<String> names = MarkerSetsPlugin.getMarkerSetNames();
		Collections.sort(names);
		for (String name: names)
		{
			DefaultMutableTreeNode msNode = new DefaultMutableTreeNode(name);
			root.add(msNode);
			MarkerSet ms = MarkerSetsPlugin.getMarkerSet(name);
			for (FileMarker marker: ms.getMarkers())
				msNode.add(new DefaultMutableTreeNode(marker));
		}
	}
	
}
