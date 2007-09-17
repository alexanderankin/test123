package ctags.sidekick.sorters;

import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.Tag;

public class LineSorter extends AbstractTreeSorter {

	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		if ((a.getUserObject() instanceof Tag) &&
			(b.getUserObject() instanceof Tag))
		{
			Tag at = (Tag) a.getUserObject();
			Tag bt = (Tag) b.getUserObject();
			return Integer.valueOf(at.getLine()).compareTo(
				Integer.valueOf(bt.getLine()));
		}
		return 0;
	}

	public String getName() {
		return "Line";
	}

}
