package automation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class MacroPanel extends JPanel {

	private final JTree tree;
	private final DefaultTreeModel model;
	private final DefaultMutableTreeNode root;
	private final ConsoleAutomationPlugin plugin;

	public MacroPanel()
	{
		plugin = (ConsoleAutomationPlugin)
			jEdit.getPlugin("automation.ConsoleAutomationPlugin");
		setLayout(new BorderLayout());
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		populateTree();
		tree = new JTree(model);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		tree.setRootVisible(false);
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(tp);
				Object [] path = tp.getPath();
				String key = null;
				if (path.length > 2)
					key = ((DefaultMutableTreeNode)
						(path[path.length - 2])).getUserObject().toString();
				String macro =
					((DefaultMutableTreeNode)(path[path.length - 1])).getUserObject().toString();
				if (key != null)
				{
					if (e.isPopupTrigger())
						plugin.runMacro(key, macro);
					else if (e.getClickCount() == 1)
						plugin.editMacro(key, macro);
				}
			}
		});
		JButton refresh = new JButton("Refresh");
		add(refresh, BorderLayout.SOUTH);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				populateTree();
				model.nodeStructureChanged(root);
			}
		});
	}

	private void populateTree()
	{
		root.removeAllChildren();
		Map<String, Vector<String>> macros = plugin.getMacros();
		Set<Entry<String, Vector<String>>> entries = macros.entrySet();
		for (Entry<String, Vector<String>> entry: entries)
		{
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(entry.getKey());
			root.add(child);
			Vector<String> keyMacros = entry.getValue();
			if (keyMacros == null)
				continue;
			for (String macro: keyMacros)
				child.add(new DefaultMutableTreeNode(macro));
		}
	}
}
