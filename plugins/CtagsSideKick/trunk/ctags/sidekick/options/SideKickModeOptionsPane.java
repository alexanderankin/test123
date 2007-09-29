package ctags.sidekick.options;

@SuppressWarnings("serial")
public class SideKickModeOptionsPane extends sidekick.ModeOptionsPane {

	ModeOptionsPane pane;
	
	public SideKickModeOptionsPane() 
	{
		super("CtagsSideKick.mode");
		pane = new ModeOptionsPane();
		addComponent(pane);
	}
		
	public void init() {
	}
	
	public void save() 
	{
		pane.save();
		//jEdit.getAction(jEdit.getProperty(GeneralOptionPane.PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
	}

	public void modeSelected(String mode) {
		pane.modeSelected(mode);
	}

	public void setUseDefaults(boolean b) {
		pane.setUseDefaults(b);
	}

	public void cancel() {
		pane.cancel();
	}

	public boolean getUseDefaults(String mode) {
		return pane.getUseDefaults(mode);
	}

}
