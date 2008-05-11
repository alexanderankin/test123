package ctags.sidekick.sorters;

import java.util.Vector;

import ctags.sidekick.IObjectProcessor;

public class KindSorter extends AttributeValueSorter {

	private static final String NAME = "Kind";
	private static final String DESCRIPTION =
		"Sort tags by kind: namespaces, types, functions, variables.";

	public KindSorter() {
		super(NAME, DESCRIPTION);
		Vector<String> params = new Vector<String>();
		params.add("kind");
		params.add("namespace");
		params.add("typedef");
		params.add("struct");
		params.add("union");
		params.add("class");
		params.add("macro");
		params.add("enum");
		params.add("enumerator");
		params.add("prototype");
		params.add("function");
		params.add("member");
		params.add("field");
		params.add("variable");
		params.add("local");
		setParams(params);
	}

	@Override
	public IObjectProcessor getClone() {
		return new KindSorter();
	}

	@Override
	public boolean takesParameters() {
		return false;
	}

}
