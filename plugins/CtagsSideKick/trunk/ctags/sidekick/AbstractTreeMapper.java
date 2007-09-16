package ctags.sidekick;

import org.gjt.sp.jedit.jEdit;

public abstract class AbstractTreeMapper implements ITreeMapper {
	String name;
	
	public AbstractTreeMapper() {
		this.name = null;
	}
	public AbstractTreeMapper(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public ITreeMapper getMapper(String params) {
		return this;
	}
	public void setLang(String lang) {
	}

	public void setName(String name) {
		this.name = name;
	}
	public String toString() {
		return name;
	}
	public void save(String name) {
		// For built-in mappers to be used as part of composite mappers
		if (! name.equals(getName())) {
			jEdit.setProperty(
				MapperManager.MAPPER_OPTION + "." + name + ".base",
				getName());
		}
	}
}
