package ctags.sidekick.sorters;

import ctags.sidekick.AbstractObjectProcessor;
import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.IObjectProcessor;

public class NameNoCaseSorter extends AbstractObjectProcessor implements ITreeSorter {

	private static final String NAME = "Name ignore case";
	private static final String DESCRIPTION =
		"Sort tree nodes by name, ignoring case.";

	public NameNoCaseSorter() {
		super(NAME, DESCRIPTION);
	}
	
	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		return a.getUserObject().toString().compareToIgnoreCase(b.getUserObject().toString());
	}


	public IObjectProcessor getClone() {
		return new NameNoCaseSorter();
	}

}
