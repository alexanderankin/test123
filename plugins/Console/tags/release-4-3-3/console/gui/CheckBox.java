package console.gui;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.jEdit;

public class CheckBox extends JCheckBox
{
	String name;
	public CheckBox(String propertyName) {
		name = propertyName;
		String label = jEdit.getProperty(propertyName);
		boolean checked = jEdit.getBooleanProperty(name + ".checked", false);
		setSelected(checked);
		setText(label);
	}

	public void save() {
		jEdit.setBooleanProperty(name + ".checked", isSelected());
	}
}
