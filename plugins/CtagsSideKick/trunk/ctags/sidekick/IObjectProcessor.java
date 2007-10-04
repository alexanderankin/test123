package ctags.sidekick;

public interface IObjectProcessor {
	String getName();
	String getDescription();
	IObjectProcessor getClone();
	void setParams(String params);
	String getParams();
	AbstractObjectEditor getEditor();
	boolean takesParameters();
}
