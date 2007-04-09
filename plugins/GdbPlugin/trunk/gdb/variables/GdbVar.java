package gdb.variables;

import gdb.core.CommandManager;
import gdb.core.Parser.GdbResult;
import gdb.core.Parser.ResultHandler;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class GdbVar extends DefaultMutableTreeNode {
	private String name;
	private String value = null;
	private String gdbName = null;
	private UpdateListener listener = null;
	private boolean created = false;
	private boolean requested = false;
	private boolean leaf = true;
	
	private static Vector<ChangeListener> listeners =
		new Vector<ChangeListener>();
	
	public interface ChangeListener {
		void changed(GdbVar v);
	}
	public interface UpdateListener {
		void updated(GdbVar v);
	}
	
	public GdbVar(String name) {
		this.name = name;
		createGdbVar();
	}
	
	public GdbVar(String name, String gdbName, boolean leaf) {
		this.name = name;
		this.gdbName = gdbName;
		this.leaf = leaf;
		getValue();
	}
	
	public static void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	public static void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}
	
	public void done() {
		if (CommandManager.getInstance() == null)
			return;
		CommandManager.getInstance().add("-var-delete " + gdbName);
	}
	
	public void setChangeListener(UpdateListener l) {
		listener = l;
	}
	public void unsetChangeListener() {
		listener = null;
	}
	public String toString() {
		if (value == null || value.equals(""))
			return name;
		return name + " = " + value;
	}
	private void getValue() {
		if (CommandManager.getInstance() == null)
			return;
		CommandManager.getInstance().add("-var-evaluate-expression " + gdbName,
				new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					value = res.getStringValue("value");
					if (value == null)
						value = "";
					if (listener != null)
						listener.updated(GdbVar.this);
				}
		});
	}
	private void createChildren() {
		created = true;
		if (gdbName == null) {
			requested = true;
			return;
		}
		CommandManager.getInstance().add("-var-list-children " + gdbName,
			new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					int nc = Integer.parseInt(res.getStringValue("numchild"));
					for (int i = 0; i < nc; i++) {
						String base = "children/" + i + "/child/";
						String cname = res.getStringValue(base + "name");
						String cexp = res.getStringValue(base + "exp");
						String cnc = res.getStringValue(base + "numchild");
						GdbVar child = new GdbVar(cexp, cname, Integer.parseInt(cnc) == 0);
						child.setChangeListener(listener);
						add(child);
					}
					if (listener != null)
						listener.updated(GdbVar.this);
				}
			}
		);
	}
	@SuppressWarnings("unchecked")
	public void update() {
		if (gdbName == null)
			createGdbVar();
		else {
			if (CommandManager.getInstance() == null)
				return;
			CommandManager.getInstance().add("-var-update " + gdbName);
			getValue();
			Enumeration<GdbVar> c = this.children();
			while (c.hasMoreElements()) {
				GdbVar child = c.nextElement();
				child.update();
			}
		}
	}
	private void createGdbVar() {
		if (CommandManager.getInstance() == null)
			return;
		CommandManager.getInstance().add("-var-create - * \"" + name + "\"",
			new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					gdbName = res.getStringValue("name");
					String nc = res.getStringValue("numchild");
					leaf = (Integer.parseInt(nc) == 0);
					if (requested)
						createChildren();
					getValue();
				}
			}
		);
	}

	@Override
	public int getChildCount() {
		if (! created) {
			createChildren();
			return 0;
		}
		return super.getChildCount();
	}

	@Override
	public boolean isLeaf() {
		if (! created)
			return leaf ;
		return super.isLeaf();
	}

	public void contextRequested() {
		if (CommandManager.getInstance() == null)
			return;
		if (! isLeaf())
			return;
		String newValue =
			JOptionPane.showInputDialog("New value for '" + name + "':", value);
		CommandManager.getInstance().add(
				"-var-assign " + gdbName + " " + newValue);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).changed(GdbVar.this);
		getValue();	// Update the value after the change
	}
}
