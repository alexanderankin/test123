package gdb.views;

import gdb.CommandManager;
import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class LocalVariables extends JPanel {
	static private TreeModel emptyTreeModel = new DefaultTreeModel(null);
	private JTree tree;
	private CommandManager commandManager = null;
	private DefaultMutableTreeNode root;
	
	public LocalVariables() {
		setLayout(new BorderLayout());
		tree = new JTree();
		root = new DefaultMutableTreeNode("Locals");
		tree.setModel(emptyTreeModel);
		tree.setRootVisible(false);
		add(new JScrollPane(tree));
	}

	public void setCommandManager(CommandManager cm) {
		commandManager = cm;
	}
	public void update(int frame) {
		root.removeAllChildren();
		commandManager.add("-stack-list-arguments 1 " + frame + " " + frame,
				new StackArgumentsResultHandler());
		commandManager.add("-stack-list-locals 2", new LocalsResultHandler());
	}

	private class StackArgumentsResultHandler implements ResultHandler {
		public void handle(String msg, GdbResult res) {
			if (! msg.equals("done"))
				return;
			Object frameArgs = res.getValue("stack-args/0/frame/args");
			if (frameArgs == null)
				return;
			if (frameArgs instanceof Vector) {
				Vector<Object> args = (Vector<Object>)frameArgs;
				for (int i = 0; i < args.size(); i++) {
					Hashtable<String, Object> hash =
						(Hashtable<String, Object>)args.get(i);
					String name = hash.get("name").toString();
					String value = hash.get("value").toString();
					root.add(createTreeNode(name, value));
				}
			}
		}
	}
	
	private class LocalsResultHandler implements ResultHandler {
		public void handle(String msg, GdbResult res) {
			if (! msg.equals("done"))
				return;
			Object locals = res.getValue("locals");
			if (locals == null)
				return;
			if (locals instanceof Vector) {
				Vector<Object> localsVec = (Vector<Object>)locals;
				for (int i = 0; i < localsVec.size(); i++) {
					Object local = localsVec.get(i);
					if (local instanceof Hashtable) {
						Hashtable<String, Object> localHash =
							(Hashtable<String, Object>)local;
						String name = localHash.get("name").toString();
						String value = "<missing>";
						Object valueObj = localHash.get("value");
						if (valueObj != null)
							value = valueObj.toString();
						root.add(createTreeNode(name, value));
					}
				}
			}
			updateTree();
		}
	}

	public DefaultMutableTreeNode createTreeNode(String name, String value) {
		return new DefaultMutableTreeNode(name + " = " + value);
	}

	public void updateTree() {
		tree.setModel(new DefaultTreeModel(root));
	}
}
