package ctags.sidekick.sorters;

import ctags.sidekick.IObjectProcessor;

public class KindSorter extends AttributeValueSorter {

	private static final String NAME = "Kind";
	private static final String DESCRIPTION =
		"Sort tags by kind: namespaces, types, functions, variables.";

	public KindSorter() {
		super(NAME, DESCRIPTION);
		setParams(
			"kind namespace typedef struct union class macro enum enumerator prototype " +
			"function member field variable local");
	}

	@Override
	public IObjectProcessor getClone() {
		return new KindSorter();
	}

}
