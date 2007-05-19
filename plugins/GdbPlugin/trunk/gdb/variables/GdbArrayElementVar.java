package gdb.variables;

@SuppressWarnings("serial")
public class GdbArrayElementVar extends GdbVar {
	String displayName;
	
	public GdbArrayElementVar(String name, int index) {
		super(name + "[" + index + "]");
		displayName = String.valueOf(index);
	}
	protected String getDisplayName() {
		return displayName;
	}

}
