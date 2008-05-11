package ctags.sidekick.renderers;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;


import ctags.sidekick.AbstractObjectEditor;
import ctags.sidekick.AbstractParameterizedObjectProcessor;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;

public class AttributeIconProvider extends AbstractParameterizedObjectProcessor
	implements IIconProvider {

	static final String NAME = "Attribute";
	static final String DESCRIPTION = "Associates values of a specified attribute " +
		"with specified icon files.";

	String attr = null;
	static Hashtable<String, ImageIcon> icons =
		new Hashtable<String, ImageIcon>();
	ImageIcon unspecified = null;
	ImageIcon missing = null;
	
	static final String SECTION_SEPARATOR = ",";
	static final String VALUE_SEPARATOR = " ";
	
	public AttributeIconProvider() {
		super(NAME, DESCRIPTION);
	}
	
	public IObjectProcessor getClone() {
		return new AttributeIconProvider();
	}

	public ImageIcon getIcon(Tag tag) {
		if (attr == null)
			return null;
		String val = tag.getField(attr);
		if (val == null)
			return missing;
		if (icons.containsKey(val))
			return icons.get(val);
		return unspecified;
	}

	protected void parseParams(Vector<String> params) {
		// attribute,value,icon,...,unspecified value icon,missing attribute icon  
		attr = params.get(0);
		int size = params.size();
		for (int i = 1; i < size - 2; i += 2)
			icons.put(params.get(i), new ImageIcon(params.get(i+1)));
		String s = params.get(size - 2);
		if (s != null && s.length() > 0)
			unspecified = new ImageIcon(s);
		s = params.get(size - 1);
		if (s != null && s.length() > 0)
			missing = new ImageIcon(s); 
	}
	
	public String toString() {
		return NAME + "(" + attr + ")";
	}

	@Override
	public AbstractObjectEditor getEditor() {
		return new AttributeIconEditor(this);
	}

}
