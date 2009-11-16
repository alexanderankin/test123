package automation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class MacroPanel extends JPanel {

	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private JButton refresh;
	private ConsoleAutomationPlugin plugin;

	public MacroPanel()
	{
		plugin = (ConsoleAutomationPlugin)
			jEdit.getPlugin("automation.ConsoleAutomationPlugin");
		setLayout(new BorderLayout());
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		populateTree();
		tree = new JTree(model);
		add(tree, BorderLayout.CENTER);
		tree.setRootVisible(false);
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(tp);
				Object [] path = tp.getPath();
				String key =
					((DefaultMutableTreeNode)(path[path.length - 2])).getUserObject().toString();
				String macro =
					((DefaultMutableTreeNode)(path[path.length - 1])).getUserObject().toString();
				if (e.isPopupTrigger())
					plugin.runMacro(key, macro);
				else if (e.getClickCount() == 1)
					plugin.editMacro(key, macro);
			}
		});
		refresh = new JButton("Refresh");
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
		HashMap<String, Vector<String>> macros = plugin.getMacros();
		Set<String> keys = macros.keySet();
		for (String key: keys)
		{
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(key);
			root.add(child);
			Vector<String> keyMacros = macros.get(key);
			if (keyMacros == null)
				continue;
			for (String macro: keyMacros)
				child.add(new DefaultMutableTreeNode(macro));
		}
	}
}
