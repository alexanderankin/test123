package ctags.sidekick.sorters;

import java.util.Comparator;
import java.util.Vector;

import ctags.sidekick.CtagsSideKickTreeNode;

public class ListSorter implements Comparator<CtagsSideKickTreeNode> {

	Vector<Comparator<CtagsSideKickTreeNode>> sorters;
	
	public ListSorter() {
		sorters = new Vector<Comparator<CtagsSideKickTreeNode>>();
	}
	public void add(Comparator<CtagsSideKickTreeNode> sorter) {
		sorters.add(sorter);
	}
	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		for (int i = 0; i < sorters.size(); i++) {
			Comparator<CtagsSideKickTreeNode> sorter = sorters.get(i);
			int res = sorter.compare(a, b);
			if (res != 0)
				return res;
		}
		return 0;
	}
}
