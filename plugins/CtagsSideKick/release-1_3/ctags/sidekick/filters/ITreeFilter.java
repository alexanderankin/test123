package ctags.sidekick.filters;

import ctags.sidekick.Tag;

public interface ITreeFilter {

	boolean pass(Tag tag);
	
}
