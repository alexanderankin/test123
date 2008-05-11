package ctags.sidekick;

import ctags.sidekick.renderers.AttributeIconProvider;
import ctags.sidekick.renderers.KindIconProvider;
import ctags.sidekick.renderers.ListIconProvider;

public class IconProviderManager extends ObjectProcessorManager {

	static public final String PROVIDER_OPTION = "options.CtagsSideKick.iconProvider";
	static public final String TYPE_NAME = "icon provider";
	static private IconProviderManager instance = null;
	
	static public IconProviderManager getInstance() {
		if (instance == null)
			instance = new IconProviderManager();
		return instance;
	}
	
	@Override
	protected ListObjectProcessor getListObjectProcessor() {
		return new ListIconProvider();
	}

	@Override
	protected String getProcessorOptionPath() {
		return PROVIDER_OPTION;
	}

	@Override
	public String getProcessorTypeName() {
		return TYPE_NAME;
	}

	@Override
	protected void registerBuiltProcessors() {
		addProcessor(new AttributeIconProvider());
		addProcessor(new KindIconProvider());
	}

}
