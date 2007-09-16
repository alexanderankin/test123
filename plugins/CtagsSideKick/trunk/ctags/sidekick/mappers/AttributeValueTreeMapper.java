package ctags.sidekick.mappers;

import java.util.Vector;

import ctags.sidekick.Tag;

public class AttributeValueTreeMapper extends AbstractTreeMapper {

	String attr;
	String defVal;
	static private final String BASE_NAME = "AttributeValue";
	
	public AttributeValueTreeMapper(String params) {
		setParams(params);
	}
	
	private void setParams(String params) {
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
	
	public ITreeMapper getMapper(String params) {
		return new AttributeValueTreeMapper(params);
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
	public String getName() {
		return BASE_NAME;
	}
	public String toString() {
		return BASE_NAME + "(" + getParams() + ")";
	}
	public String getParams() {
		StringBuffer params = new StringBuffer();
		if (attr != null)
			params.append(attr);
		if (defVal != null)
			params.append(" " + defVal);
		return params.toString();
	}
}
