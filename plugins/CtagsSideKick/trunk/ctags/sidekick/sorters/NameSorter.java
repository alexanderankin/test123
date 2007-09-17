package ctags.sidekick.sorters;

import java.util.Comparator;

import ctags.sidekick.CtagsSideKickTreeNode;

public class NameSorter implements Comparator<CtagsSideKickTreeNode> {

	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		return a.getUserObject().toString().compareTo(b.getUserObject().toString());
	}

}
