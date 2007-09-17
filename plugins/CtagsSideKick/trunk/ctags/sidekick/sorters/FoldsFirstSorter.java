package ctags.sidekick.sorters;

import java.util.Comparator;

import ctags.sidekick.CtagsSideKickTreeNode;

public class FoldsFirstSorter implements Comparator<CtagsSideKickTreeNode> {

	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		if (a.hasChildren())
			return (b.hasChildren() ? 0 : -1);
		return (b.hasChildren() ? 1 : 0);
	}

}
