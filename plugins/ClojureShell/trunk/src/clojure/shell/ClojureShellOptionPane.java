package clojure.shell;
/**
 * @author Damien Radtke
 * class ClojureShellOptionPane
 * Option pane for configuring ClojureShell behavior.
 */
//{{{ Imports
import classpath.ClasspathPlugin;
import clojure.ClojurePlugin;
import console.Console;
import console.Output;
import java.awt.GridBagConstraints;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
//}}}
public class ClojureShellOptionPane extends AbstractOptionPane {
	private JRadioButton plainShell;
	private JRadioButton leinIfProject;
	private JRadioButton leinAlways;
	private JTextField leinRunCmd;
	private JTextField plainRunCmd;
	
	public ClojureShellOptionPane() {
	  super("clojureshell");
	}

	private boolean getBooleanProperty(String name) {
		return jEdit.getBooleanProperty(ClojureShellPlugin.OPTION_PREFIX + name);
	}

	private String getProperty(String name) {
		return jEdit.getProperty(ClojureShellPlugin.OPTION_PREFIX + name);
	}

	private void setBooleanProperty(String name, boolean value) {
		jEdit.setBooleanProperty(ClojureShellPlugin.OPTION_PREFIX + name, value);
	}

	private void setProperty(String name, String value) {
		jEdit.setProperty(ClojureShellPlugin.OPTION_PREFIX + name, value);
	}

	//{{{ _init()
	/**
	 * Initialize ClojureShell option pane.
	 */
	public void _init() {
		ButtonGroup shellTypes = new ButtonGroup();

		plainShell = new JRadioButton(
			getProperty("plainShellLbl"),
			getBooleanProperty("plainShell"));
		leinIfProject = new JRadioButton(
			getProperty("leinIfProjectLbl"),
			getBooleanProperty("leinIfProject"));
		leinAlways = new JRadioButton(
			getProperty("leinAlwaysLbl"),
			getBooleanProperty("leinAlways"));
		leinRunCmd = new JTextField(getProperty("leinRunCmd"));
		plainRunCmd = new JTextField(getProperty("plainShellRunCmd"));
		
		shellTypes.add(plainShell);
		shellTypes.add(leinIfProject);
		shellTypes.add(leinAlways);
		
		addComponent(plainShell);
		addComponent(getProperty("plainShellRunCmdLbl"), plainRunCmd,
			GridBagConstraints.HORIZONTAL);
		addComponent(leinIfProject);
		addComponent(leinAlways);
		addComponent(getProperty("leinRunCmdLbl"), leinRunCmd,
			GridBagConstraints.HORIZONTAL);
	}
	//}}}
	
	//{{{ _save()
	/**
	 * Save ClojureShell option pane properties.
	 */
	public void _save() {
		setBooleanProperty("plainShell", plainShell.isSelected());
		setBooleanProperty("leinIfProject", leinIfProject.isSelected());
		setBooleanProperty("leinAlways", leinAlways.isSelected());
		setProperty("leinRunCmd", leinRunCmd.getText());
		setProperty("plainShellRunCmd", plainRunCmd.getText());
    }
	//}}}
}

