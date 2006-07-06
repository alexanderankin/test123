package csidekick;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.HistoryTextField;


public class CSideKickOptionPane extends AbstractOptionPane
{

	ProjectProperties props = null;
	JCheckBox useInProject;
	HistoryTextField textField = null;
	
	public CSideKickOptionPane()
	{
		super("CSideKick");
		
	}
	protected void _init()
	{
		props = new ProjectProperties();
		
		useInProject = new JCheckBox(jEdit.getProperty("options.csidekick.useinproject.label"));
		useInProject.setSelected(props.isCProject());
		addComponent(useInProject);
		
		textField = new HistoryTextField();
		addComponent(jEdit.getProperty("options.csidekick.includepath.label"), textField );
		textField.setText(props.getIncludePath());
	}
		

	protected void _save()
	{
		props.setIncludePath(textField.getText());
		props.setCProject(useInProject.isSelected());
	}

}
