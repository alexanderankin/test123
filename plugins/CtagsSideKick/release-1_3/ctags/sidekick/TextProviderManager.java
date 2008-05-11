package ctags.sidekick;

import ctags.sidekick.renderers.AttributeTextProvider;
import ctags.sidekick.renderers.ListTextProvider;
import ctags.sidekick.renderers.NameAndSignatureTextProvider;

public class TextProviderManager extends ObjectProcessorManager {

	static public final String PROVIDER_OPTION = "options.CtagsSideKick.textProvider";
	static public final String TYPE_NAME = "text provider";
	static private TextProviderManager instance = null;
	
	static public TextProviderManager getInstance() {
		if (instance == null)
			instance = new TextProviderManager();
		return instance;
	}
	
	@Override
	protected ListObjectProcessor getListObjectProcessor() {
		return new ListTextProvider();
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
		addProcessor(new AttributeTextProvider());
		addProcessor(new NameAndSignatureTextProvider());
	}

}
