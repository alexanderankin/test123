package ctags.sidekick;

import javax.swing.JPanel;

public abstract class AbstractObjectEditor extends JPanel {

	protected IObjectProcessor processor;
	
	public AbstractObjectEditor(IObjectProcessor processor)
	{
		this.processor = processor;
	}
	public abstract void save();
	
}
