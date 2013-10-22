package console.ssh;


import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {
	JCheckBox xforward;
	
	
	public OptionPane() {
		super("sshconsole");
	}
	
	
	protected void _init() {
		xforward = new JCheckBox(jEdit.getProperty("options.sshconsole.xforward"));
		xforward.setSelected(jEdit.getBooleanProperty("sshconsole.xforward"));
		addComponent(xforward);
	}
	
	protected void _save() {
		jEdit.setBooleanProperty("sshconsole.xforward", xforward.isSelected());
	}
	
}
