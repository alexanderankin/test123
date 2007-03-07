package gdb.views;

import gdb.CommandManager;
import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;
import gdb.views.GdbVar.ChangeListener;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

@SuppressWarnings("serial")
public class LocalVariables extends JPanel {
	private JTree tree;
	private CommandManager commandManager = null;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;
	
	public LocalVariables() {
		setLayout(new BorderLayout());
		tree = new JTree();
		root = new DefaultMutableTreeNode("Locals");
		model = new DefaultTreeModel(root);
		tree.setModel(model);
		tree.setRootVisible(false);
		add(new JScrollPane(tree));
	}

	public void setCommandManager(CommandManager cm) {
		commandManager = cm;
	}
	public void update(int frame) {
		root.removeAllChildren();
		commandManager.add("-stack-list-arguments 0 " + frame + " " + frame,
				new StackArgumentsResultHandler());
		commandManager.add("-stack-list-locals 0", new LocalsResultHandler());
	}
	public void sessionEnded() {
		root.removeAllChildren();
		model.nodeStructureChanged(root);
	}

	private class StackArgumentsResultHandler implements ResultHandler {
		@SuppressWarnings("unchecked")
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
					GdbVar v = new GdbVar(name);
					v.setChangeListener(new ChangeListener() {
						public void changed(GdbVar v) {
							model.nodeStructureChanged(v);
						}
					});
					root.add(v);
				}
			}
		}
	}
	
	private class LocalsResultHandler implements ResultHandler {
		@SuppressWarnings("unchecked")
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
						GdbVar v = new GdbVar(name);
						v.setChangeListener(new ChangeListener() {
							public void changed(GdbVar v) {
								model.nodeStructureChanged(v);
							}
						});
						root.add(v);
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
		model.nodeStructureChanged(root);
	}
}
