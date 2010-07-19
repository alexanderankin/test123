package javadoc;
//{{{ imports
import common.gui.pathbuilder.PathBuilder;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
//}}}
public class JavadocOptionPane extends AbstractOptionPane {
	private PathBuilder builder;
	public JavadocOptionPane() {
		super("javadoc");
	}
	
	protected void _init() {
		builder = new PathBuilder();
		builder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		builder.setPath(jEdit.getProperty("options.javadoc.path", ""));
		addComponent(builder);
	}
	
	protected void _save() {
		jEdit.setProperty("options.javadoc.path", builder.getPath());
	}
}
