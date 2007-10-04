package ctags.sidekick;

// A parameterless (and therefore editor-less) object processor

public abstract class AbstractObjectProcessor implements IObjectProcessor {

	String name;
	String description;
	
	public AbstractObjectProcessor(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public AbstractObjectEditor getEditor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public String getParams() {
		return null;
	}

	public void setParams(String params) {
	}

	public String toString() {
		return getName();
	}
	
	public boolean takesParameters() {
		return false;
	}
}
