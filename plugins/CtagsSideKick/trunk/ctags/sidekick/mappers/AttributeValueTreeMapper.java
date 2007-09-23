package ctags.sidekick.mappers;

import java.util.Vector;

import ctags.sidekick.AbstractObjectEditor;
import ctags.sidekick.AbstractParameterizedObjectProcessor;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.StringParamEditor;
import ctags.sidekick.Tag;

public class AttributeValueTreeMapper extends AbstractParameterizedObjectProcessor 
	implements ITreeMapper {

	String attr;
	String defVal;
	static private final String NAME = "AttributeValue";
	static private final String DESCRIPTION =
		"Adds the value of a tag attribute to the tree path. " +
		"Accepts a default value if the attribute does not exist for the tag.";
	
	public AttributeValueTreeMapper() {
		super(NAME, DESCRIPTION);
	}
	
	private void parseParams(String params) {
		if (params != null) {
			String [] parts = params.split(" ", 2);
			attr = parts[0];
			if (parts.length > 1)
				defVal = parts[1];
			else
				defVal = null;
		} else
			attr = defVal = null;
	}
	
	@Override
	public void setParams(String params) {
		super.setParams(params);
		parseParams(params);
	}

	public Vector<Object> getPath(Tag tag) {
		Vector<Object> path = new Vector<Object>();
		Object val = tag.getInfo().get(attr);
		if (val == null)
			val = defVal;
		if (val != null)
			path.add(val);
		return path;
	}
	public String toString() {
		return NAME + "(" + getParams() + ")";
	}
	public String getParams() {
		StringBuffer params = new StringBuffer();
		if (attr != null)
			params.append(attr);
		if (defVal != null)
			params.append(" " + defVal);
		return params.toString();
	}

	public IObjectProcessor getClone() {
		return new AttributeValueTreeMapper();
	}

	@Override
	public AbstractObjectEditor getEditor() {
		return new StringParamEditor(this, "Attribute:");
	}

	public void setLang(String lang) {
	}
	
}
