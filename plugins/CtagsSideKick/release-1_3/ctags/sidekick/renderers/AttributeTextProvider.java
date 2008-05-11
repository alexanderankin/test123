package ctags.sidekick.renderers;

import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ctags.sidekick.AbstractObjectEditor;
import ctags.sidekick.AbstractParameterizedObjectProcessor;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;

public class AttributeTextProvider extends AbstractParameterizedObjectProcessor
	implements ITextProvider {

	public String attr;
	public String prefix = "";
	public String suffix = "";
	public String missing = "";
	
	static final String NAME = "Attribute";
	static final String DESCRIPTION = "Creates a tag string from an attribute " +
		"value, with an optional prefix and suffix and an optional default value.";
	
	protected void parseParams(Vector<String> params) {
		attr = params.get(0);
		prefix = params.get(1);
		suffix = params.get(2);
		if (params.size() > 3)
			missing = params.get(3);
	}
	
	public AttributeTextProvider() {
		super(NAME, DESCRIPTION);
	}
	
	public String toString() {
		return NAME + "(\"" + prefix + ((attr==null) ? "" : attr) + suffix +
			"\"" +
			((missing.length() > 0) ? ("|\"" + missing + "\"") : "") + ")";
	}
	
	public String getString(Tag tag) {
		if (attr == null)
			return tag.getName();
		String value = tag.getField(attr);
		if (value == null)
			return missing;
		StringBuffer buf = new StringBuffer();
		buf.append(prefix);
		buf.append(value);
		buf.append(suffix);
		return buf.toString();
	}

	public IObjectProcessor getClone() {
		return new AttributeTextProvider();
	}

	public AbstractObjectEditor getEditor() {
		return new Editor();
	}

	@SuppressWarnings("serial")
	public class Editor extends AbstractObjectEditor {

		private JTextField name;
		private JTextField prefix;
		private JTextField suffix;
		private JTextField missing;

		public Editor() {
			super(AttributeTextProvider.this);
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
			p.add(new JLabel("Prefix:"));
			prefix = new JTextField(20);
			p.add(prefix);
			p.setMaximumSize(p.getPreferredSize());

			p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("Suffix:"));
			suffix = new JTextField(20);
			p.add(suffix);
			p.setMaximumSize(p.getPreferredSize());

			p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("Default value:"));
			missing = new JTextField(20);
			p.add(missing);
			p.setMaximumSize(p.getPreferredSize());
		}
		
		@Override
		public void save() {
			Vector<String> params = new Vector<String>();  
			params.add(name.getText());
			params.add(prefix.getText());
			params.add(suffix.getText());
			params.add(missing.getText());
			setParams(params);
		}

	}

}
