package sn;

import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;

@SuppressWarnings("serial")
public class MainOptionPane extends AbstractOptionPane {

	public static final String DEFAULT_PROJ = "defaultProj";
	public static final String DEFAULT_DIR = "defaultDir";
	private JTextField defaultDir;
	private JTextField defaultProj;
	
	public MainOptionPane() {
		super("source-navigator-options-main");
		defaultDir = new JTextField(SourceNavigatorPlugin.getOption(DEFAULT_DIR), 40);
		addComponent("Default directory:", defaultDir);
		defaultProj = new JTextField(SourceNavigatorPlugin.getOption(DEFAULT_PROJ), 40);
		addComponent("Default project:", defaultProj);
	}
	@Override
	public void _save() {
		SourceNavigatorPlugin.setOption(DEFAULT_DIR, defaultDir.getText());
		SourceNavigatorPlugin.setOption(DEFAULT_PROJ, defaultProj.getText());
	}
}
