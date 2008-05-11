package ctags.sidekick.sorters;

import java.util.Vector;

import ctags.sidekick.IObjectProcessor;

public class AccessSorter extends AttributeValueSorter {

	private static final String NAME = "Access";
	private static final String DESCRIPTION =
		"Sort tags by access, from public to private.";
	
	public AccessSorter() {
		super(NAME, DESCRIPTION);
		Vector<String> params = new Vector<String>();
		params.add("access");
		params.add("public");
		params.add("package");
		params.add("protected");
		params.add("private");
		setParams(params);
	}

	@Override
	public IObjectProcessor getClone() {
		return new AccessSorter();
	}

	@Override
	public boolean takesParameters() {
		return false;
	}

}
