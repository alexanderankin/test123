package ctags.sidekick.sorters;

import java.util.Comparator;
import java.util.Vector;

import ctags.sidekick.CtagsSideKickTreeNode;

public class ListSorter extends AbstractTreeSorter {

	Vector<ITreeSorter> sorters;
	
	public ListSorter() {
		sorters = new Vector<ITreeSorter>();
	}
	public void add(ITreeSorter sorter) {
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
	public String getName() {
		return "Composite";
	}
	public Vector<ITreeSorter> getComponents() {
		return sorters;
	}
}
