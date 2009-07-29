package ctags.sidekick.sorters;

import ctags.sidekick.AbstractObjectProcessor;
import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.IObjectProcessor;

public class NameSorter extends AbstractObjectProcessor implements ITreeSorter {

	private static final String NAME = "Name";
	private static final String DESCRIPTION =
		"Sort tree nodes by name.";

	public NameSorter() {
		super(NAME, DESCRIPTION);
	}
	
	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		return a.getUserObject().toString().compareTo(b.getUserObject().toString());
	}


	public IObjectProcessor getClone() {
		return new NameSorter();
	}

}
