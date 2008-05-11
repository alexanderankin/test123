package ctags.sidekick.filters;

import java.util.Vector;

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.ListObjectProcessor;
import ctags.sidekick.Tag;

public class ListTreeFilter extends ListObjectProcessor
	implements ITreeFilter {

	private static final String NAME = "Composite";
	private static final String DESCRIPTION =
		"A list of tree filters, each provides its own filtering.";
	
	public ListTreeFilter() {
		super(NAME, DESCRIPTION);
	}

	public boolean pass(Tag tag) {
		Vector<IObjectProcessor> processors = getProcessors();
		for (int i = 0; i < processors.size(); i++) {
			ITreeFilter filter = (ITreeFilter) processors.get(i);
			if (! filter.pass(tag))
				return false;
		}
		return true;
	}

	public IObjectProcessor getClone() {
		return new ListTreeFilter();
	}

}
