package ctags.sidekick.mappers;

import ctags.sidekick.AbstractObjectProcessor;

public abstract class AbstractTreeMapper extends AbstractObjectProcessor implements ITreeMapper {
	
	public AbstractTreeMapper(String name, String description) {
		super(name, description);
	}

	public void setLang(String lang) {
	}

	public CollisionHandler getCollisionHandler() {
		return null;
	}

}
