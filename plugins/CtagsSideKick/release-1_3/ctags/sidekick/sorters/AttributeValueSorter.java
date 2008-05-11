package ctags.sidekick.sorters;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ctags.sidekick.AbstractObjectEditor;
import ctags.sidekick.IObjectProcessor;

public class AttributeValueSorter extends AbstractAttributeValueSorter {

	@SuppressWarnings("serial")
	public class Editor extends AbstractObjectEditor {

		private JTextField name;
		private JTextField values;

		public Editor() {
			super(AttributeValueSorter.this);
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
			p.add(new JLabel("Values:"));
			values = new JTextField(40);
			p.add(values);
			p.setMaximumSize(p.getPreferredSize());
		}
		
		@Override
		public void save() {
			Vector<String> params = new Vector<String>();
			params.add(name.getText());
			String [] parts = values.getText().split(" ");
			for (int i = 0; i < parts.length; i++)
				params.add(parts[i]);
			setParams(params);
		}

	}

	private static final String NAME = "AttributeValue";
	private static final String DESCRIPTION =
		"Sort tags by attribute value. An optional list of values may be " +
		"specified (space-separated) to set the order in which these " +
		"values should be sorted.";
	
	private String attr;
	private HashMap<String, Integer> valueOrder;
	private boolean hasParams;
	
	protected AttributeValueSorter(String name, String description) {
		super(name, description);
		hasParams = false;
	}
	
	public AttributeValueSorter() {
		super(NAME, DESCRIPTION);
		hasParams = true;
	}

	public void parseParams(Vector<String> params) {
		attr = params.get(0);
		valueOrder = new HashMap<String, Integer>();
		for (int i = 1; i < params.size(); i++)
			valueOrder.put(params.get(i), Integer.valueOf(i));
	}
	
	@Override
	public AbstractObjectEditor getEditor() {
		return (hasParams ? new Editor() : null);
	}

	protected String getAttributeName() {
		return attr;
	}

	protected int getValueOrder(String value) {
		Integer val = valueOrder.get(value);
		if (val == null)
			return valueOrder.size() + 1;
		return val.intValue();
	}

	public IObjectProcessor getClone() {
		return new AttributeValueSorter();
	}

}
