package console.options;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import console.ConsolePlugin;
import console.commando.CommandoCommand;
import console.commando.CommandoToolBar;
import console.utils.StringList;

/**
 * 
 * @author ezust
 * 
 */
public class ToolBarOptionPane extends AbstractOptionPane
{
	private JCheckBox enabledCheckBox;

	TreeMap  <String, JCheckBox> checkBoxes = 
		new TreeMap  <String, JCheckBox> ();

	private static final long serialVersionUID = 23562571L;

	public ToolBarOptionPane()
	{
		super("console.toolbar");
	//	mButtons = new TreeMap /* <String, JToggleButton> */();
	}

	protected void _init()
	{
		// ConsolePlugin.rescanCommands();

		addComponent(enabledCheckBox = new JCheckBox(jEdit
				.getProperty("options.console.general.commando.toolbar")));

		enabledCheckBox.getModel().setSelected(
				jEdit.getBooleanProperty("commando.toolbar.enabled"));

		/* String selectedCommands = jEdit.getProperty("commando.toolbar.list");
		ConsolePlugin.setSelectedActions(selectedCommands); */

		createButtons();
	}

	protected void createButtons()
	{

		//mButtons.clear();
		checkBoxes.clear();
		ActionSet allActions = ConsolePlugin.getAllCommands();
		GridLayout glayout = new GridLayout(0 ,3 );
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(glayout);
		
		for (EditAction ea: allActions.getActions()) {
			CommandoCommand cc = (CommandoCommand) ea;
			String label = cc.getShortLabel();
			JCheckBox cb = new JCheckBox(label);
			boolean selected = jEdit.getBooleanProperty("commando.visible." + label, true);
			cb.setSelected(selected);
			checkBoxes.put(label, cb);
			buttonPanel.add(cb);
		}
		addComponent(buttonPanel);
	}

	protected void _save()
	{
		jEdit.setBooleanProperty("commando.toolbar.enabled",
				enabledCheckBox.isSelected());
		for (JCheckBox cb: checkBoxes.values()) {
			jEdit.setBooleanProperty("commando.visible." + cb.getText(), cb.isSelected());
		}
		jEdit.saveSettings();
		CommandoToolBar.init();
	}
	
}
