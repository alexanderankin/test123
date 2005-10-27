package optional;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class OptionalOptions extends AbstractOptionPane
{
	JCheckBox useCombined;
	public OptionalOptions() 
	{
		super("optional");
	}
	protected void _init() {
		String useCombinedOptions = jEdit.getProperty("optional.combined.label");
		useCombined = new JCheckBox(useCombinedOptions);
		useCombined.setSelected(jEdit.getBooleanProperty("optional.combined"));
		addComponent(useCombined);
	}

	protected void _save() {
		boolean combined = useCombined.isSelected();
		jEdit.setBooleanProperty("optional.combined", combined);
	}
}
