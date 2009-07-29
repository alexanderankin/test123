package ctags.sidekick.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import ctags.sidekick.AbstractObjectEditor;
import ctags.sidekick.AbstractParameterizedObjectProcessor;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;

public class SetTreeFilter extends AbstractParameterizedObjectProcessor
		implements ITreeFilter {

	static private final String NAME = "Set";
	static private final String DESCRIPTION =
		"Filters tags whose value for a specific attribute is either " +
		"included or not included in a specified set of values.";

	private boolean includes;
	private String attribute = null;
	private Set<String> values;
	
	public SetTreeFilter() {
		super(NAME, DESCRIPTION);
	}

	@Override
	protected void parseParams(Vector<String> params) {
		attribute = params.get(0);
		includes = params.get(1).equalsIgnoreCase("true");
		values = new HashSet<String>();
		for (int i = 2; i < params.size(); i++)
			values.add(params.get(i));
	}

	public boolean pass(Tag tag) {
		if (attribute == null)
			return true;
		String value = tag.getField(attribute);
		if (value == null)
			return true;
		return (values.contains(value) == includes);
	}

	public String toString() {
		if (attribute == null)
			return super.toString();
		StringBuffer buf = new StringBuffer(NAME + "(" + attribute + " ");
		buf.append(includes ? "in" : "not in");
		buf.append(" [");
		Iterator<String> it = values.iterator();
		while (it.hasNext())
			buf.append(it.next() + " ");
		buf.deleteCharAt(buf.length() - 1);
		buf.append("])");
		return buf.toString();
	}

	public IObjectProcessor getClone() {
		return new SetTreeFilter();
	}

	@Override
	public AbstractObjectEditor getEditor() {
		return new Editor();
	}

	@SuppressWarnings("serial")
	public class Editor extends AbstractObjectEditor {

		private JTextField name;
		private JTextField values;
		private JRadioButton includes;
		private JRadioButton excludes;
		
		public Editor() {
			super(SetTreeFilter.this);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			JPanel p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("Attribute:"));
			name = new JTextField(20);
			p.add(name);
			p.setMaximumSize(p.getPreferredSize());

			includes = new JRadioButton("Value in:");
			includes.setAlignmentX(LEFT_ALIGNMENT);
			add(includes);
			excludes = new JRadioButton("Value not in:");
			excludes.setAlignmentX(LEFT_ALIGNMENT);
			add(excludes);
			ButtonGroup group = new ButtonGroup();
			group.add(includes);
			group.add(excludes);
			includes.setSelected(true);
			
			p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("Values:"));
			values = new JTextField(40);
			p.add(values);
			p.setMaximumSize(p.getPreferredSize());
		}
		
		@Override
		public void save() {
			Vector<String> params = new Vector<String>();
			params.add(name.getText());
			params.add(includes.isSelected() ? "true" : "false");
			String [] parts = values.getText().split(" ");
			for (int i = 0; i < parts.length; i++)
				params.add(parts[i]);
			setParams(params);
		}

	}
	
}
