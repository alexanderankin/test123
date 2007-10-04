package ctags.sidekick;

import ctags.sidekick.mappers.AttributeValueTreeMapper;
import ctags.sidekick.mappers.FlatNamespaceTreeMapper;
import ctags.sidekick.mappers.KindTreeMapper;
import ctags.sidekick.mappers.ListTreeMapper;
import ctags.sidekick.mappers.NamespaceTreeMapper;

public class MapperManager extends ObjectProcessorManager {
	
	static public final String MAPPER_OPTION = "options.CtagsSideKick.mapper";
	static public final String TYPE_NAME = "grouper";
	static private MapperManager instance = null;
	
	static public MapperManager getInstance() {
		if (instance == null)
			instance = new MapperManager();
		return instance;
	}
	
	@Override
	protected ListObjectProcessor getListObjectProcessor() {
		return new ListTreeMapper();
	}
	@Override
	protected String getProcessorOptionPath() {
		return MAPPER_OPTION;
	}
	@Override
	public String getProcessorTypeName() {
		return TYPE_NAME;
	}
	@Override
	protected void registerBuiltProcessors() {
		addProcessor(new AttributeValueTreeMapper());
		addProcessor(new FlatNamespaceTreeMapper());
		addProcessor(new KindTreeMapper());
		addProcessor(new NamespaceTreeMapper());
	}
	
}
