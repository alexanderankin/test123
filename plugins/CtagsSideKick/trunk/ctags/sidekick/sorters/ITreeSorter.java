package ctags.sidekick.sorters;

import java.util.Comparator;

import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.IObjectProcessor;

public interface ITreeSorter extends Comparator<CtagsSideKickTreeNode>,
	IObjectProcessor {
}
