package console.options;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import console.gui.Label;

/** Console options pertaining to the System shell */
public class SystemOptionPane extends AbstractOptionPane
{
	private JComboBox prefix;
	private JCheckBox rememberCWD;
	private JCheckBox nodeselect;
	private JCheckBox mergeError;
	private JCheckBox showExitStatus;
//	private JCheckBox pvselect;
	private JCheckBox pvchange;
	private JTextField pathDirs ;
	private JCheckBox pathDirsAppend;
	

	public SystemOptionPane()
	{
		super("console.system");
	}
	protected void _init()
	{

		prefix = new JComboBox();
		prefix.setEditable(true);
		
		prefix.addItem(jEdit.getProperty("console.shell.prefix", "osdefault"));
		prefix.addItem("osdefault");
		prefix.addItem("none");
		prefix.addItem(jEdit.getProperty("console.shell.prefix.bash"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.cmd"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.tcsh"));		
		prefix.addItem(jEdit.getProperty("console.shell.prefix.command"));
		Label prefixLabel = new Label("options.console.general.shellprefix");
		addComponent(prefixLabel, prefix);
		
		Label pathLabel = new Label("options.console.general.pathdirs");
		pathDirs = new JTextField(jEdit.getProperty("console.shell.pathdirs"));
		addComponent(pathLabel, pathDirs);

		pathDirsAppend = new JCheckBox(jEdit.getProperty("options.console.general.pathdirs.append"));
		pathDirsAppend.setSelected(jEdit.getBooleanProperty("console.shell.pathdirs.append"));
		addComponent(pathDirsAppend);
		
		rememberCWD = new JCheckBox(jEdit.getProperty("options.console.general.rememberCWD"));
		rememberCWD.setSelected(jEdit.getBooleanProperty("console.rememberCWD"));
		addComponent(rememberCWD);	

		showExitStatus = new JCheckBox();
		showExitStatus.setText(jEdit.getProperty("options.console.general.showExitStatus"));
		showExitStatus.setSelected(jEdit.getBooleanProperty("console.processrunner.showExitStatus", true));
		addComponent(showExitStatus);

		
		mergeError = new JCheckBox();
		mergeError.setText(jEdit.getProperty("options.console.general.mergeError"));
		mergeError.setToolTipText(jEdit.getProperty("options.console.general.mergeError.tooltip"));
		mergeError.setSelected(jEdit.getBooleanProperty("console.processrunner.mergeError", true));
		addComponent(mergeError);
		
		addComponent(new JSeparator(SwingConstants.HORIZONTAL));
		addSeparator("options.console.general.changedir");
		nodeselect = new JCheckBox(jEdit.getProperty("options.console.general.changedir.nodeselect"));
		nodeselect.setSelected(jEdit.getBooleanProperty("console.changedir.nodeselect"));		
		pvchange = new JCheckBox(jEdit.getProperty("options.console.general.changedir.pvchange"));
		pvchange.setSelected(jEdit.getBooleanProperty("console.changedir.pvchange"));
//		pvselect = new JCheckBox(jEdit.getProperty("options.console.general.changedir.pvselect"));
//		pvselect.setSelected(jEdit.getBooleanProperty("console.changedir.pvpvselect"));		

		addComponent(nodeselect);
		addComponent(pvchange);
//		addComponent(pvselect);
		addComponent(new JSeparator(SwingConstants.HORIZONTAL));

		
	}

	public void _save() {
		jEdit.setBooleanProperty("console.changedir.pvchange", pvchange.isSelected());
//		jEdit.setBooleanProperty("console.changedir.pvselect", pvselect.isSelected());
		jEdit.setBooleanProperty("console.changedir.nodeselect", nodeselect.isSelected());
		jEdit.setBooleanProperty("console.rememberCWD", rememberCWD.isSelected());
		jEdit.setBooleanProperty("console.processrunner.mergeError", mergeError.isSelected());
		jEdit.setBooleanProperty("console.processrunner.showExitStatus", showExitStatus.isSelected());
		jEdit.setProperty("console.shell.pathdirs", pathDirs.getText());
		jEdit.setBooleanProperty("console.shell.pathdirs.append", pathDirsAppend.isSelected());
		jEdit.setProperty("console.shell.prefix", prefix.getSelectedItem().toString());
	}
}
