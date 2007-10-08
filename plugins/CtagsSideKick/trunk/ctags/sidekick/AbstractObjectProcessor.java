package ctags.sidekick;

import java.util.Vector;

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

	public Vector<String> getParams() {
		return null;
	}

	public void setParams(Vector<String> params) {
	}

	public String toString() {
		return getName();
	}
	
	public boolean takesParameters() {
		return false;
	}
}
