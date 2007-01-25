package ctags.sidekick;

import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

public class ModeOptionsPane extends sidekick.ModeOptionsPane {

	static private final String CTAGS_MODE_OPTIONS_LABEL = "options.CtagsSideKick.mode.ctags_options_label";
	
	JTextField ctagsOptions;
	
	public ModeOptionsPane() 
	{
		super("CtagsSideKick.mode");
	}
		
	protected void _init() {
		ctagsOptions = new JTextField();
		addComponent(jEdit.getProperty(CTAGS_MODE_OPTIONS_LABEL), ctagsOptions);
		_load();
	}
	
	protected void _load() 
	{
		ctagsOptions.setText(getProperty(Plugin.CTAGS_MODE_OPTIONS));
	}
	
	protected void _save() 
	{
		setProperty(Plugin.CTAGS_MODE_OPTIONS, ctagsOptions.getText());
	}

	protected void _reset()
	{
		clearModeProperty(Plugin.CTAGS_MODE_OPTIONS);
	}	

}
