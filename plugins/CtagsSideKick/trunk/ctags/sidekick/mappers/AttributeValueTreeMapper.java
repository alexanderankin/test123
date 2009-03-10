package ctags.sidekick.mappers;

import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ctags.sidekick.AbstractObjectEditor;
import ctags.sidekick.AbstractParameterizedObjectProcessor;
import ctags.sidekick.IObjectProcessor;
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
	
	protected void parseParams(Vector<String> params) {
		attr = params.get(0);
		if (params.size() > 1)
			defVal = params.get(1);
		else
			defVal = null;
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

	public IObjectProcessor getClone() {
		return new AttributeValueTreeMapper();
	}

	@Override
	public AbstractObjectEditor getEditor() {
		return new Editor();
	}

	public void setLang(String lang) {
	}

	public CollisionHandler getCollisionHandler() {
		return null;
	}

	@SuppressWarnings("serial")
	public class Editor extends AbstractObjectEditor {

		private JTextField name;
		private JTextField defaultValue;

		public Editor() {
			super(AttributeValueTreeMapper.this);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			JPanel p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("Attribute:"));
			name = new JTextField(20);
			p.add(name);
			p.setMaximumSize(p.getPreferredSize());

			p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("Default value:"));
			defaultValue = new JTextField(20);
			p.add(defaultValue);
			p.setMaximumSize(p.getPreferredSize());
		}
		
		@Override
		public void save() {
			Vector<String> params = new Vector<String>();
			params.add(name.getText()); 
			if (defaultValue.getText().length() > 0)
				params.add(defaultValue.getText());
			setParams(params);
		}

	}

}
