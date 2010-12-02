package tags;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

public class AttributeValueCollisionResolver {

	private String name = null;
	private String attribute = null;
	private Vector<String> values;

	public AttributeValueCollisionResolver(int index) {
		name = jEdit.getProperty("options.tags.actions." + index + ".name");
		attribute = jEdit.getProperty("options.tags.actions." + index + ".attr");
		values = new Vector<String>();
		String vs = jEdit.getProperty("options.tags.actions." + index + ".values");
		if (vs == null)
			return;
		String [] parts = vs.split(" ");
		for (int i = 0; i < parts.length; i++)
			values.add(parts[i]);
	}
	
	public AttributeValueCollisionResolver(String name, String attribute,
			Vector<String> values) {
		this.name = name;
		this.attribute = attribute;
		this.values = values;
	}
	
	public Vector<TagLine> resolve(Vector<TagLine> tags) {
		if (attribute == null || tags.size() == 0)
			return tags;
		Vector<TagLine> filtered = new Vector<TagLine>();
		for (int valIndex = 0; valIndex < values.size() && filtered.isEmpty(); valIndex++) {
			String checkedValue = values.get(valIndex);
			for (int n = 0; n < tags.size(); n++) {
				TagLine l = tags.get(n);
				Vector<ExuberantInfoItem> items = l.getExuberantInfoItems();
				for (int i = 0; i < items.size(); i++) {
					ExuberantInfoItem item = (ExuberantInfoItem) items.get(i);
					String s = item.toString();
					if (s.startsWith(attribute + ":")) {
						String value = s.substring(attribute.length() + 1);
						if (value.equals(checkedValue))
							filtered.add(l);
					}
				}
			}
		}
		return filtered;
	}

	public static AttributeValueCollisionResolver getNewResolver(JDialog dialog) {
		Editor editor = new Editor(dialog);
		return editor.getResolver();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return name;
	}
	
	public void save(int index) {
		jEdit.setProperty("options.tags.actions." + index + ".name", name);
		jEdit.setProperty("options.tags.actions." + index + ".attr", attribute);
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < values.size(); i++) {
			if (i > 0)
				s.append(" ");
			s.append(values.get(i));
		}
		jEdit.setProperty("options.tags.actions." + index + ".values", s.toString());
	}

	@SuppressWarnings("serial")
	static public class Editor extends JDialog implements ActionListener {

		private JTextField name;
		private JTextField attr;
		private JTextField values;
		private JButton ok;
		private JButton cancel;
		private AttributeValueCollisionResolver resolver = null;
		
		public Editor(JDialog dialog) {
			super(dialog, "Tag Collision Resolver Action Editor", true);
			setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridx = c.gridy = 0;
			c.gridheight = c.gridwidth = 1;
			JPanel p = new JPanel();
			add(p, c);
			p.add(new JLabel(jEdit.getProperty("options.tags.actions.help")));
			
			c.gridy++;
			c.gridheight = 1;
			p = new JPanel();
			add(p, c);
			p.add(new JLabel("Action name:"));
			name = new JTextField(20);
			p.add(name);

			c.gridy++;
			p = new JPanel();
			add(p, c);
			p.add(new JLabel("Attribute name:"));
			attr = new JTextField(20);
			p.add(attr);

			c.gridy++;
			p = new JPanel();
			add(p, c);
			p.add(new JLabel("Values (in decreasing priority):"));
			values = new JTextField(40);
			p.add(values);
			
			c.gridy++;
			p = new JPanel();
			add(p, c);
			ok = new JButton("Ok");
			ok.addActionListener(this);
			cancel = new JButton("Cancel");
			cancel.addActionListener(this);
			p.add(ok);
			p.add(cancel);
			
			pack();
			setVisible(true);
		}
		
		public void save() {
			Vector<String> v = new Vector<String>();
			String [] parts = values.getText().split(" ");
			for (int i = 0; i < parts.length; i++)
				v.add(parts[i]);
			resolver = new AttributeValueCollisionResolver(name.getText(), attr.getText(), v);
		}

		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == ok) {
				save();
				dispose();
			} else if (event.getSource() == cancel) {
				dispose();
			}
		}
		
		public AttributeValueCollisionResolver getResolver() {
			return resolver;
		}

	}

}
