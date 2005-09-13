package console.options;

import java.awt.Component;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import console.ConsolePlugin;
import console.commando.CommandoCommand;
import console.utils.StringList;

/**
 * 
 * @author ezust
 * 
 */
public class ToolBarOptionPane extends AbstractOptionPane
{
	private JCheckBox enabledCheckBox;

	TreeMap /* <String, JToggleButton> */mButtons;

	private static final long serialVersionUID = 23562571L;

	public ToolBarOptionPane()
	{
		super("console.toolbar");
		mButtons = new TreeMap /* <String, JToggleButton> */();
	}

	protected void _init()
	{
		// ConsolePlugin.rescanCommands();

		addComponent(enabledCheckBox = new JCheckBox(jEdit
				.getProperty("options.console.general.commando.toolbar")));

		enabledCheckBox.getModel().setSelected(
				jEdit.getBooleanProperty("commando.toolbar.enabled"));

		String selectedCommands = jEdit.getProperty("commando.toolbar.list");
		ConsolePlugin.setSelectedActions(selectedCommands);

		createButtons();
	}

	protected void createButtons()
	{

		mButtons.clear();
		ActionSet allActions = ConsolePlugin.getAllCommands();
		ActionSet selectedActions = ConsolePlugin.getSelectedCommands();
		EditAction[] list = allActions.getActions();
		for (int i = 0; i < list.length; ++i)
		{
			EditAction ea = list[i];
			CommandoCommand cc = (CommandoCommand) ea;
			String label = cc.getShortLabel();
			JToggleButton tb = new JToggleButton(label);
			boolean selected = selectedActions.contains(cc.getName());
			tb.setSelected(selected);
			mButtons.put(label, tb);
		}
		Iterator bitr = mButtons.values().iterator();
		while (bitr.hasNext())
		{
			Component c = (Component) bitr.next();
			addComponent(c);
		}
	}

	protected void _save()
	{
		jEdit.setBooleanProperty("commando.toolbar.enabled",
				enabledCheckBox.isSelected());
		StringList sl = new StringList();
		Iterator bitr = mButtons.values().iterator();
		while (bitr.hasNext())
		{
			JToggleButton tb = (JToggleButton) bitr.next();
			if (tb.isSelected())
				sl.add(tb.getText());
		}
		String actionList = sl.join(" ");
		Log.log(Log.WARNING, ToolBarOptionPane.class, "New ActionList: "
				+ actionList);
		jEdit.setProperty("commando.toolbar.list", actionList);
		jEdit.saveSettings();
		ConsolePlugin.setSelectedActions(actionList);
	}
}
