package gdb.views;

import gdb.CommandManager;
import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class GdbVar extends DefaultMutableTreeNode {
	private String name;
	private String value = null;
	private String gdbName = null;
	private ChangeListener listener = null;
	
	public interface ChangeListener {
		void changed(GdbVar v);
	}
	
	public GdbVar(String name) {
		this.name = name;
		createGdbVar();
	}
	
	public GdbVar(String name, String gdbName) {
		this.name = name;
		this.gdbName = gdbName;
		createChildren();
		getValue();
	}
	
	public void done() {
		if (CommandManager.getInstance() == null)
			return;
		CommandManager.getInstance().add("-var-delete " + gdbName);
	}
	
	public void setChangeListener(ChangeListener l) {
		listener = l;
	}
	public void unsetChangeListener() {
		listener = null;
	}
	public String toString() {
		return name + (value != null ? " = " + value : "");
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
						listener.changed(GdbVar.this);
				}
		});
	}
	private void createChildren() {
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
						GdbVar child = new GdbVar(cexp, cname);
						add(child);
					}
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
		CommandManager.getInstance().add("-var-create - * " + name,
			new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					gdbName = res.getStringValue("name");
					createChildren();
					getValue();
				}
			}
		);
	}

}
