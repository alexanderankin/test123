package classpath;

import common.gui.pathbuilder.PathBuilder;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import projectviewer.vpt.VPTProject;

public class PVClasspathOptionPane extends AbstractOptionPane {

	private VPTProject project;
	private PathBuilder path;

	public PVClasspathOptionPane(VPTProject project) {
		super("project.classpath");
		this.project = project;
	}

	protected void _init() {
		path = new PathBuilder(jEdit.getProperty("options.classpath.path"));
		path.setFileFilter(new ClasspathFilter());
		String cp = project.getProperty("java.classpath");
		if (cp != null)
			path.setPath(cp);

		addComponent(path);
	}

	protected void _save() {
		project.setProperty("java.classpath", path.getPath());
	}
}
