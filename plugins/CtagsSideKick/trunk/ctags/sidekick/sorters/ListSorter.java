package ctags.sidekick.sorters;

import java.util.Vector;

import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.ListObjectProcessor;

public class ListSorter extends ListObjectProcessor implements ITreeSorter {

	private static final String NAME = "Composite";
	private static final String DESCRIPTION =
		"Sorts tree nodes using a list of other sorters.";
	
	public ListSorter() {
		super(NAME, DESCRIPTION);
	}
	
	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		Vector<IObjectProcessor> sorters = getProcessors();
		for (int i = 0; i < sorters.size(); i++) {
			ITreeSorter sorter = (ITreeSorter) sorters.get(i);
			int res = sorter.compare(a, b);
			if (res != 0)
				return res;
		}
		return 0;
	}

	public IObjectProcessor getClone() {
		return new ListSorter();
	}
}
