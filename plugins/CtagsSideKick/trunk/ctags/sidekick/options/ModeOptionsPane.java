package ctags.sidekick.options;

import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;


@SuppressWarnings("serial")
public class ModeOptionsPane extends sidekick.ModeOptionsPane {

	Vector<IModeOptionPane> modePanes;
	
	public ModeOptionsPane() 
	{
		super("CtagsSideKick.mode");
	}
		
	protected void _init() {
		modePanes = new Vector<IModeOptionPane>();
		
		ModeCtagsInvocationPane invocationPane = new ModeCtagsInvocationPane();
		addComponent(invocationPane);
		modePanes.add(invocationPane);
		
		JPanel optionPanes = new JPanel(new GridLayout(1, 0));
		addComponent(optionPanes);
		ModeMapperPane mapperPane = new ModeMapperPane();
		optionPanes.add(mapperPane);
		modePanes.add(mapperPane);
		
		_load();
	}
	
	protected void _save() 
	{
		for (int i = 0; i < modePanes.size(); i++)
			modePanes.get(i).save();
		jEdit.getAction(jEdit.getProperty(GeneralOptionPane.PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
	}

	protected void _reset()
	{
		for (int i = 0; i < modePanes.size(); i++)
			modePanes.get(i).resetCurrentMode();
	}

	protected void _load()
	{
		for (int i = 0; i < modePanes.size(); i++)
			modePanes.get(i).modeSelected(getMode());
	}	

}
