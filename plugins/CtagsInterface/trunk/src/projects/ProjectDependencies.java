package projects;

import javax.swing.JList;

import org.gjt.sp.jedit.AbstractOptionPane;

@SuppressWarnings("serial")
public class ProjectDependencies extends AbstractOptionPane {

	JList deps;
	
	public ProjectDependencies() {
		super("CtagsInterface-ProjectDependencies");
	}

	protected void _init() {
		deps = new JList();
		addComponent(deps);
	}
}
