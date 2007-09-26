package ctags.sidekick.options;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class SideKickModeOptionsPane extends sidekick.ModeOptionsPane {

	ModeOptionsPane pane;
	
	public SideKickModeOptionsPane() 
	{
		super("CtagsSideKick.mode");
	}
		
	protected void _init() {
		pane = new ModeOptionsPane();
		addComponent(pane);
		_load();
	}
	
	protected void _save() 
	{
		pane.save();
		jEdit.getAction(jEdit.getProperty(GeneralOptionPane.PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
	}

	protected void _reset()
	{
		pane.setUseDefaults(true);
	}

	protected void _load()
	{
		pane.modeSelected(getMode());
	}	

}
