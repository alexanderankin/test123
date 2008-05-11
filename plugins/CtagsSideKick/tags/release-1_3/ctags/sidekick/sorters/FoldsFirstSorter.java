package ctags.sidekick.sorters;

import ctags.sidekick.AbstractObjectProcessor;
import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.IObjectProcessor;

public class FoldsFirstSorter extends AbstractObjectProcessor implements ITreeSorter {

	private static final String NAME = "FoldsFirst";
	private static final String DESCRIPTION =
		"Put leaf nodes after non-leaf nodes in the tree.";
	
	public FoldsFirstSorter() {
		super(NAME, DESCRIPTION);
	}

	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		if (a.hasChildren())
			return (b.hasChildren() ? 0 : -1);
		return (b.hasChildren() ? 1 : 0);
	}

	public IObjectProcessor getClone() {
		return new FoldsFirstSorter();
	}

}
