package ctags.sidekick.mappers;



public abstract class AbstractTreeMapper implements ITreeMapper {
	
	public ITreeMapper getMapper(String params) {
		return this;
	}
	public void setLang(String lang) {
	}

	public String toString() {
		return getName();
	}
	public String getParams() {
		return null;
	}
}
