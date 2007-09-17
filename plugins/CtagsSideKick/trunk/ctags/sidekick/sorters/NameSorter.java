package ctags.sidekick.sorters;

import ctags.sidekick.CtagsSideKickTreeNode;

public class NameSorter extends AbstractTreeSorter {

	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		return a.getUserObject().toString().compareTo(b.getUserObject().toString());
	}

	public String getName() {
		return "Name";
	}

}
