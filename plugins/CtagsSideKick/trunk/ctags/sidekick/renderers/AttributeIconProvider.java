package ctags.sidekick.renderers;

import java.util.Hashtable;

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

	protected void parseParams(String params) {
		if (params == null) {
			attr = null;
			return;
		}
		// Format:
		// attribute,value icon ...,unspecified value icon,missing attribute icon  
		String [] parts = params.split(SECTION_SEPARATOR, 4);
		attr = parts[0];
		String [] values = parts[1].split(VALUE_SEPARATOR);
		for (int i = 0; i < values.length - 1; i += 2)
			icons.put(values[i], new ImageIcon(values[i+1]));
		if (parts[2] != null && parts[2].length() > 0)
			unspecified = new ImageIcon(parts[2]); 
		if (parts[3] != null && parts[3].length() > 0)
			missing = new ImageIcon(parts[3]); 
	}
	
	public String toString() {
		return NAME + "(" + attr + ")";
	}

	@Override
	public AbstractObjectEditor getEditor() {
		return new AttributeIconEditor(this);
	}

}
