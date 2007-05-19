package gdb.variables;

import java.util.Enumeration;

@SuppressWarnings("serial")
public class ArrayRangeVar extends GdbVar {

	private GdbVar parentVar;
	int from, to;
	
	public ArrayRangeVar(String name, GdbVar parent, int from, int to) {
		super(name);
		this.parentVar = parent;
		this.from = from;
		this.to = to;
	}

	@Override
	protected void createGdbVar() {
		// Do not create a gdb var ... this is a placeholder
	}

	@SuppressWarnings("unchecked")
	@Override
	public void done() {
		Enumeration<GdbVar> c = children();
		while (c.hasMoreElements()) {
			c.nextElement().done();
		}
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public void update() {
		updateChildren();
	}

	@Override
	protected void doCreateChildren() {
		for (int i = from; i <= to; i++) {
			GdbVar child = new GdbArrayElementVar(parentVar.name, i);
			child.setChangeListener(listener);
			add(child);
		}
		notifyListener();
	}

}
