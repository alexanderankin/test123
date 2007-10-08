package ctags.sidekick.filters;

import java.util.Vector;
import java.util.regex.Pattern;

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

public class RegExpTreeFilter extends AbstractParameterizedObjectProcessor
		implements ITreeFilter {

	static private final String NAME = "RegExp";
	static private final String DESCRIPTION =
		"Filters tags whose value for a specific attribute either " +
		"matches or mismatches a specified regular expression.";

	boolean matches;
	private String attribute = null;
	private Pattern pattern;
	
	public RegExpTreeFilter() {
		super(NAME, DESCRIPTION);
	}

	@Override
	protected void parseParams(Vector<String> params) {
		attribute = params.get(0);
		matches = params.get(1).equalsIgnoreCase("true");
		pattern = Pattern.compile(params.get(2));
	}

	public boolean pass(Tag tag) {
		if (attribute == null)
			return true;
		String value = tag.getField(attribute);
		if (value == null)
			return true;
		return (pattern.matcher(value).matches() == matches);
	}

	public String toString() {
		if (attribute == null)
			return super.toString();
		StringBuffer buf = new StringBuffer(NAME + "(" + attribute + " ");
		buf.append(matches ? "matches" : "mismatches");
		buf.append(" " + pattern.toString() + ")");
		return buf.toString();
	}

	public IObjectProcessor getClone() {
		return new RegExpTreeFilter();
	}

	public AbstractObjectEditor getEditor() {
		return new Editor();
	}

	@SuppressWarnings("serial")
	public class Editor extends AbstractObjectEditor {

		private JTextField name;
		private JTextField regexp;
		private JRadioButton matches;
		private JRadioButton mismatches;
		
		public Editor() {
			super(RegExpTreeFilter.this);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			JPanel p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("Attribute:"));
			name = new JTextField(20);
			p.add(name);
			p.setMaximumSize(p.getPreferredSize());

			matches = new JRadioButton("Matches:");
			matches.setAlignmentX(LEFT_ALIGNMENT);
			add(matches);
			mismatches = new JRadioButton("Mismatches:");
			mismatches.setAlignmentX(LEFT_ALIGNMENT);
			add(mismatches);
			ButtonGroup group = new ButtonGroup();
			group.add(matches);
			group.add(mismatches);
			matches.setSelected(true);
			
			p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			p.add(new JLabel("RegExp:"));
			regexp = new JTextField(40);
			p.add(regexp);
			p.setMaximumSize(p.getPreferredSize());
		}
		
		@Override
		public void save() {
			Vector<String> params = new Vector<String>();
			params.add(name.getText());
			params.add(matches.isSelected() ? "true" : "false"); 
			params.add(regexp.getText());
			setParams(params);
		}

	}
}
