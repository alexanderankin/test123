package ctags.sidekick;

import java.util.Vector;

public interface IObjectProcessor {
	String getName();
	String getDescription();
	IObjectProcessor getClone();
	void setParams(Vector<String> params);
	Vector<String> getParams();
	AbstractObjectEditor getEditor();
	boolean takesParameters();
}
