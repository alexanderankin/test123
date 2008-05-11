package ctags.sidekick;

import ctags.sidekick.filters.ListTreeFilter;
import ctags.sidekick.filters.RegExpTreeFilter;
import ctags.sidekick.filters.SetTreeFilter;

public class FilterManager extends ObjectProcessorManager {

	static public final String FILTER_OPTION = "options.CtagsSideKick.filter";
	static public final String TYPE_NAME = "filter";
	static private FilterManager instance = null;
	
	static public FilterManager getInstance() {
		if (instance == null)
			instance = new FilterManager();
		return instance;
	}
	
	@Override
	protected ListObjectProcessor getListObjectProcessor() {
		return new ListTreeFilter();
	}

	@Override
	protected String getProcessorOptionPath() {
		return FILTER_OPTION;
	}

	@Override
	public String getProcessorTypeName() {
		return TYPE_NAME;
	}

	@Override
	protected void registerBuiltProcessors() {
		addProcessor(new RegExpTreeFilter());
		addProcessor(new SetTreeFilter());
	}

}
