package ctags.sidekick.renderers;

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;

public interface ITextProvider extends IObjectProcessor {
	String getString(Tag tag);
}
