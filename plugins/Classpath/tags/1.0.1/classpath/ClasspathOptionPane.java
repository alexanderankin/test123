package classpath;

import common.gui.pathbuilder.PathBuilder;
import java.io.File;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;

public class ClasspathOptionPane extends AbstractOptionPane {

	private PathBuilder path;
	private JCheckBox includeWorking;
	private JCheckBox includeSystem;
	private JCheckBox includeInstalled;

	public ClasspathOptionPane() {
		super("classpath");
	}

	protected void _init() {
		// TODO: option to include system classpath
		path = new PathBuilder(jEdit.getProperty("options.classpath.path"));
		path.setPath(jEdit.getProperty("java.customClasspath"));
		path.setFileFilter(new ClasspathFilter());

		includeWorking = new JCheckBox(jEdit.getProperty("options.classpath.includeWorking"),
				jEdit.getBooleanProperty("java.classpath.includeWorking"));
		includeSystem = new JCheckBox(jEdit.getProperty("options.classpath.includeSystem"),
				jEdit.getBooleanProperty("java.classpath.includeSystem"));
		includeInstalled = new JCheckBox(jEdit.getProperty("options.classpath.includeInstalled"),
				jEdit.getBooleanProperty("java.classpath.includeInstalled"));

		includeSystem.setToolTipText(System.getProperty("java.class.path"));

		addComponent(includeWorking);
		addComponent(includeSystem);
		addComponent(includeInstalled);
		addComponent(path);
	}

	protected void _save() {
		String cp = path.getPath();
		boolean inclWorking = includeWorking.isSelected();
		boolean inclSystem = includeSystem.isSelected();
		boolean inclInstalled = includeInstalled.isSelected();

		jEdit.setProperty("java.customClasspath", cp);
		jEdit.setBooleanProperty("java.classpath.includeWorking", inclWorking);
		jEdit.setBooleanProperty("java.classpath.includeSystem", inclSystem);
		jEdit.setBooleanProperty("java.classpath.includeInstalled", inclInstalled);

		ClasspathPlugin.updateClasspath();
	}
}
