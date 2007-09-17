package ctags.sidekick.sorters;

import ctags.sidekick.CtagsSideKickTreeNode;

public class FoldsFirstSorter extends AbstractTreeSorter {

	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		if (a.hasChildren())
			return (b.hasChildren() ? 0 : -1);
		return (b.hasChildren() ? 1 : 0);
	}

	public String getName() {
		return "FoldsFirst";
	}

}
