package console.options;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import sae.utils.StringList;

import console.ConsolePlugin;

public class ToolbarOptionPane extends AbstractOptionPane {
	private JCheckBox commandoToolBar;
	
	ActionSet mAllActions, mSelectedActions;
	
	private static final long serialVersionUID = 23562571L;

	public ToolbarOptionPane() {
		super("console.toolbar");
		mSelectedActions = ConsolePlugin.getSelectedCommands();
		mAllActions = ConsolePlugin.getAllCommands();
	} 
	
	protected void _init() {
		addComponent(commandoToolBar = new JCheckBox(jEdit
				.getProperty("options.console.general.commando.toolbar")));
		commandoToolBar.getModel().setSelected(
				jEdit.getBooleanProperty("commando.toolbar.enabled"));
		String selectedCommands = jEdit.getProperty("commando.default");
		StringList sl = StringList.split(selectedCommands, " ");
		ConsolePlugin.rescanCommands();
		mSelectedActions.removeAllActions() ;
		
		for (String name: sl) {
			EditAction ea = mAllActions.getAction(name);
			if (ea != null) {
				mSelectedActions.addAction(ea);
			}
			else {
				Log.log(Log.WARNING, this, "Unable to get action: " + name);
			}
		}
		
	}
	
	protected void _save() {
		jEdit.setBooleanProperty("commando.toolbar.enabled", commandoToolBar
				.getModel().isSelected());
	}
	
	
}

	



