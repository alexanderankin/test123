
package synchroscroll;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class SynchroScrollPane extends AbstractOptionPane
{
	//{{{ Private members
	private JRadioButton radioButtonStandardMode;
	private JRadioButton radioButtonProportionalMode;
	//}}}
	
	//{{{ TextToolsCommentsOptionPane constructor
	/** Constructor for the <code>SynchroScrollPane</code> object. */
	public SynchroScrollPane()
	{
		super("SynchroScrollPluginPane");
	} //}}}
	
	/** Initialises the option pane. */
	public void _init()
	{		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		radioButtonStandardMode = new JRadioButton(
			jEdit.getProperty("options.SynchroScrollPluginPane.standardMode.title"),
			jEdit.getBooleanProperty("options.SynchroScrollPluginPane.standardMode.value"));
		
		radioButtonProportionalMode = new JRadioButton(
			jEdit.getProperty("options.SynchroScrollPluginPane.proportionalMode.title"),
			jEdit.getBooleanProperty("options.SynchroScrollPluginPane.proportionalMode.value"));
		
		JLabel labelModeInfo = new JLabel(jEdit.getProperty("options.SynchroScrollPluginPane.labelModeInfo.title"));
		
		addComponent(labelModeInfo);
		addComponent(radioButtonStandardMode);
		addComponent(radioButtonProportionalMode);

		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(radioButtonStandardMode);
		modeGroup.add(radioButtonProportionalMode);
		
	}
	
	//{{{ save() method
	/** Saves properties from the option pane. */
	public void _save()
	{
		jEdit.setBooleanProperty("options.SynchroScrollPluginPane.standardMode.value",
			radioButtonStandardMode.isSelected());
		
		jEdit.setBooleanProperty("options.SynchroScrollPluginPane.proportionalMode.value",
			radioButtonProportionalMode.isSelected());
	
	} //}}}


}

