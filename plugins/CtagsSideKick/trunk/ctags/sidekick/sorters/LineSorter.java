package ctags.sidekick.sorters;

import ctags.sidekick.AbstractObjectProcessor;
import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;

public class LineSorter extends AbstractObjectProcessor implements ITreeSorter {

	private static final String NAME = "Line";
	private static final String DESCRIPTION =
		"Sort tags by line number.";
	
	public LineSorter() {
		super(NAME, DESCRIPTION);
	}

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

	public IObjectProcessor getClone() {
		return new LineSorter();
	}

}
