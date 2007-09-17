package ctags.sidekick.sorters;

public class KindSorter extends AttributeValueSorter {

	public KindSorter() {
		super("kind namespace typedef struct union class macro enum enumerator prototype " +
			"function member field variable local");
	}

	public String getName() {
		return "Kind";
	}
	
}
