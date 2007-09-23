package ctags.sidekick.sorters;

import ctags.sidekick.IObjectProcessor;

public class AccessSorter extends AttributeValueSorter {

	private static final String NAME = "Access";
	private static final String DESCRIPTION =
		"Sort tags by access, from public to private.";
	
	public AccessSorter() {
		super(NAME, DESCRIPTION);
		setParams("access public package protected private");
	}

	@Override
	public IObjectProcessor getClone() {
		return new AccessSorter();
	}

}
