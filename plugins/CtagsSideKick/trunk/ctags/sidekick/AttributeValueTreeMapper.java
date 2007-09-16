package ctags.sidekick;

import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

public class AttributeValueTreeMapper extends AbstractTreeMapper {

	String attr;
	String defVal;
	static private final String BASE_NAME = "AttributeValue";
	
	public AttributeValueTreeMapper() {
		super(BASE_NAME);
	}
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
	public void save(String name) {
		jEdit.setProperty(
			MapperManager.MAPPER_OPTION + "." + name + ".base", BASE_NAME);
		String params = getParamString();
		if (params.length() > 0)
		{
			jEdit.setProperty(
				MapperManager.MAPPER_OPTION + "." + name + ".params", params);
		}
	}
	public String toString() {
		String n = getName();
		if (n == null || n.length() == 0)
			return BASE_NAME + "(" + getParamString() + ")";
		return n;
	}
	private String getParamString() {
		StringBuffer params = new StringBuffer();
		if (attr != null)
			params.append(attr);
		if (defVal != null)
			params.append(" " + defVal);
		return params.toString();
	}
}
