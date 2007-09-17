package ctags.sidekick.sorters;

import java.util.Comparator;

import ctags.sidekick.CtagsSideKickTreeNode;

public interface ITreeSorter extends Comparator<CtagsSideKickTreeNode> {

	String getName();
	ITreeSorter getSorter(String params);
	String getParams();

}
