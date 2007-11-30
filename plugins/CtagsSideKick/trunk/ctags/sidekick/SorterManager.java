package ctags.sidekick;

import ctags.sidekick.sorters.AccessSorter;
import ctags.sidekick.sorters.AttributeValueSorter;
import ctags.sidekick.sorters.FoldsFirstSorter;
import ctags.sidekick.sorters.KindSorter;
import ctags.sidekick.sorters.LineSorter;
import ctags.sidekick.sorters.ListSorter;
import ctags.sidekick.sorters.NameSorter;
import ctags.sidekick.sorters.NameNoCaseSorter;

public class SorterManager extends ObjectProcessorManager {
	
	static public final String SORTER_OPTION = "options.CtagsSideKick.sorter";
	static public final String TYPE_NAME = "sorter";
	static private SorterManager instance = null;
	
	static public SorterManager getInstance() {
		if (instance == null)
			instance = new SorterManager();
		return instance;
	}
	
	@Override
	protected ListObjectProcessor getListObjectProcessor() {
		return new ListSorter();
	}
	@Override
	protected String getProcessorOptionPath() {
		return SORTER_OPTION;
	}
	@Override
	public String getProcessorTypeName() {
		return TYPE_NAME;
	}
	@Override
	protected void registerBuiltProcessors() {
		addProcessor(new AccessSorter());
		addProcessor(new AttributeValueSorter());
		addProcessor(new FoldsFirstSorter());
		addProcessor(new KindSorter());
		addProcessor(new NameNoCaseSorter());
		addProcessor(new LineSorter());
		addProcessor(new NameSorter());
	}
	
}
