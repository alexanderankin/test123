package slime;
/**
 * @author Damien Radtke
 * class SlimeOptionPane
 * This pane lets you enable or disable certain shells
 */
//{{{ Imports
import java.util.Hashtable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import slime.SlimePlugin;
//}}}
public class SlimeOptionPane extends AbstractOptionPane {
	private Hashtable<String, JCheckBox> shells;
	
	public SlimeOptionPane() {
		super("slime");
		shells = new Hashtable<String, JCheckBox>();
	}
	
	public void _init() {
		addComponent(new JLabel(
			jEdit.getProperty("options.slime.enable-shells")));
		for (int i = 0; i<SlimePlugin.SHELLS.length; i++) {
			String shell = SlimePlugin.SHELLS[i];
			boolean show = jEdit.getBooleanProperty(
				"options.slime.show-"+shell.toLowerCase());
			JCheckBox checkbox = new JCheckBox(shell, show);
			shells.put(shell, checkbox);
			addComponent(checkbox);
		}
		addSeparator();
		addComponent(new JLabel(
			jEdit.getProperty("options.slime.restart-required")));
	}
	
	public void _save() {
		for (int i = 0; i<SlimePlugin.SHELLS.length; i++) {
			String shell = SlimePlugin.SHELLS[i];
			boolean show = shells.get(shell).isSelected();
			jEdit.setBooleanProperty("options.slime.show-"+shell.toLowerCase(),
				show);
		}
	}
}
