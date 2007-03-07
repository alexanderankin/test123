package gdb.views;

import gdb.CommandManager;
import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class Watches extends JPanel {
	static private TreeModel emptyTreeModel = new DefaultTreeModel(null);
	private JTree tree;
	private CommandManager commandManager = null;
	private DefaultMutableTreeNode root;
	private Vector<Watch> watches = new Vector<Watch>();

	public Watches() {
		setLayout(new BorderLayout());
		
		// Buttons for adding/removing watches
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String expr = JOptionPane.showInputDialog("Expression:");
				if (expr == null)
					return;
				Watch w = new Watch(expr, "");
				watches.add(w);
				w.eval();
				root.add(w);
				updateTree();
			}
		});
		tb.add(add);
		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath tp = tree.getSelectionPath();
				if (tp == null)
					return;
				Object [] path = tp.getPath();
				Watch w = (Watch)(path[1]);
				watches.remove(w);
				updateTree();
			}
		});
		tb.add(remove);
		JButton removeAll = new JButton("Remove All");
		removeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				watches.clear();
				root.removeAllChildren();
				updateTree();
			}
		});
		tb.add(removeAll);
		add(tb, BorderLayout.NORTH);
		
		tree = new JTree();
		root = new DefaultMutableTreeNode("Watches");
		tree.setModel(emptyTreeModel);
		tree.setRootVisible(false);
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	public void setCommandManager(CommandManager cm) {
		commandManager = cm;
	}
	public void update() {
		root.removeAllChildren();
		for (int i = 0; i < watches.size(); i++) {
			watches.get(i).eval();
		}
	}
	public void sessionEnded() {
		tree.setModel(emptyTreeModel);
	}

	public void updateTree() {
		root.removeAllChildren();
		for (int i = 0; i < watches.size(); i++)
			root.add(watches.get(i));
		tree.setModel(new DefaultTreeModel(root));
	}
	
	private class Watch extends DefaultMutableTreeNode {
		private String name;
		private String value;
		public Watch(String name, String value) {
			this.name = name;
			this.value = value;
		}
		public String toString() {
			return name + " = " + value;
		}
		public void eval() {
			if (commandManager == null)
				return;
			commandManager.add("-data-evaluate-expression " + name,
				new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					value = res.getStringValue("value");
					if (value == null)
						value = "";
					updateTree();
				}
			});
		}
	}
}
