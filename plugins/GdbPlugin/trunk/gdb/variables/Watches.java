package gdb.variables;

import gdb.core.GdbState;
import gdb.core.GdbView;
import gdb.variables.GdbVar.ChangeListener;
import gdb.variables.GdbVar.UpdateListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class Watches extends GdbView implements ChangeListener {
	private JTree tree;
	private DefaultMutableTreeNode root;
	private Vector<GdbVar> vars = new Vector<GdbVar>();
	private DefaultTreeModel model = null;
	
	public Watches() {
		setLayout(new BorderLayout());
		GdbVar.addChangeListener(this);
		
		// Buttons for adding/removing watches
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String expr = JOptionPane.showInputDialog("Expression:");
				if (expr == null)
					return;
				GdbVar v = new GdbVar(expr);
				v.setChangeListener(new UpdateListener() {
					public void updated(GdbVar v) {
						model.reload(v);
					}
				});
				vars.add(v);
				root.add(v);
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
				GdbVar v = (GdbVar)(path[1]);
				v.done();
				vars.remove(v);
				updateTree();
			}
		});
		tb.add(remove);
		JButton removeAll = new JButton("Remove All");
		removeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < vars.size(); i++)
					vars.get(i).done();
				vars.clear();
				root.removeAllChildren();
				updateTree();
			}
		});
		tb.add(removeAll);
		JButton modify = new JButton("Modify");
		modify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath tp = tree.getSelectionPath();
				if (tp == null)
					return;
				Object [] path = tp.getPath();
				GdbVar v = (GdbVar)(path[1]);
				if (v != null)
					v.contextRequested();
			}
		});
		tb.add(modify);
		add(tb, BorderLayout.NORTH);
		
		tree = new JTree();
		root = new DefaultMutableTreeNode("Watches");
		model = new DefaultTreeModel(root);
		tree.setModel(model);
		tree.setRootVisible(false);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		tree.addMouseListener(new VarTreeMouseListener());
		if (GdbState.isStopped())
			update();
	}

	public void update() {
		for (int i = 0; i < vars.size(); i++) {
			vars.get(i).update();
		}
	}
	public void sessionEnded() {
		root.removeAllChildren();
		model.reload(root);
	}

	public void updateTree() {
		root.removeAllChildren();
		for (int i = 0; i < vars.size(); i++)
			root.add(vars.get(i));
		model.reload(root);
	}

	public void changed(GdbVar v) {
		update();
	}
}

