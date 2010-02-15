package codebook;
// imports {{{
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collection;
import java.util.HashSet;
import java.io.File;
import java.io.FilenameFilter;
// }}} imports
public class CodeBookPlugin extends EditPlugin {
	//public static final String HOME = getPluginHome(jEdit.getPlugin("codebook.CodeBookPlugin")).getPath();
	public static final String HOME = "/home/damien/.jedit/plugins/codebook.CodeBookPlugin/";
	
	public void start() {}
	public void stop() {}
	
	/*
	public static void javadocTest() {
		final String pkgRegex = "^package\\s*.*;$"; // Used to find a buffer's package
		new Thread(new Runnable() {
			public void run() {
				try {
					if (!MiscUtilities.isToolsJarAvailable()) {
						Macros.message(jEdit.getActiveView(), "Tools.jar not found. Aborting.");
						return;
					}
					Macros.message(jEdit.getActiveView(), "Generating javadocs for the current project...");
					projectviewer.vpt.VPTProject proj = projectviewer.ProjectViewer.getActiveProject(jEdit.getActiveView());
					Collection<projectviewer.vpt.VPTNode> nodes = proj.getOpenableNodes();
					for (projectviewer.vpt.VPTNode node : nodes) {
						if (node == null || node.isDirectory()) continue;
						String path = node.getNodePath();
						if (!path.endsWith(".java")) continue;
						// Save this in a temporary directory
						new File("/tmp/javadoc").mkdir();
						String[] args = {"-d", "/tmp/javadoc", path};
						com.sun.tools.javadoc.Main.execute(args);
					}
					Macros.message(jEdit.getActiveView(), "Javadocs created in /tmp/javadoc");
				} catch (Exception e) {
					System.out.print("Caught an exception: ");
					e.printStackTrace();
				}
			}
		}).start();
	}
	*/
	
	public static void run(JEditTextArea textArea) {
		String mode = textArea.getBuffer().getMode().getName();
		try {
			// Call the appropriate runner class
			if (mode.equals("java") || mode.equals("groovy") || mode.equals("beanshell")) {
				codebook.java.JavaRunner.run(textArea);
			}
		} catch (Exception e) {
			Log.log(Log.ERROR,CodeBookPlugin.class,"Error running. "+e+" ("+e.getMessage()+")");
		}
	}
	public static void complete(JEditTextArea textArea, String complete) {
		String mode = textArea.getBuffer().getMode().getName();
		// Call the appropriate complete method
		if (mode.equals("java") || mode.equals("groovy") || mode.equals("beanshell")) {
			codebook.java.JavaRunner.complete(textArea, complete);
		}
	}
}
/* ::mode=java:: */