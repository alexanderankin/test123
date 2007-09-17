package ctags.sidekick.sorters;

public class AccessSorter extends AttributeValueSorter {

	public AccessSorter() {
		super("access public package protected private");
	}

	@Override
	public String getName() {
		return "Access";
	}

}
