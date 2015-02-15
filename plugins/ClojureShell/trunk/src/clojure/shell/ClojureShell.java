package clojure.shell;
/**
 * @author Damien Radtke
 * class ClojureShell
 * Embeds an interactive Clojure session into the console
 */
//{{{ Imports
import classpath.ClasspathPlugin;
import clojure.ClojurePlugin;
import console.Console;
import console.Output;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.text.AttributeSet;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
import procshell.ProcessShell;
import projectviewer.ProjectPlugin;
//}}}
public class ClojureShell extends ProcessShell {

	private String prompt = "user=> ";

	// Since ConsoleState's 'console' field is package private we can't
	// get the console from the state object when we need it.
	// Use this table to keep track of them for later.
	private final Hashtable<ConsoleState, Console> findConsoleMap =
		new Hashtable<ConsoleState, Console>();

	public void openConsole(Console console) {
		super.openConsole(console);
		findConsoleMap.put(consoleStateMap.get(console), console);
	}
	
	public void closeConsole(Console console) {
		findConsoleMap.remove(consoleStateMap.get(console));
		super.closeConsole(console);
	}
	
	/*
	 * Constructor for ClojureShell
	 */
	public ClojureShell() {
		super("Clojure");
	}

	private boolean getBooleanProperty(String name) {
		return jEdit.getBooleanProperty(ClojureShellPlugin.OPTION_PREFIX + name);
	}

	private String getProperty(String name) {
		return jEdit.getProperty(ClojureShellPlugin.OPTION_PREFIX + name);
	}

	//{{{ init()
	/**
	 * Start up Clojure
	 */
	protected void init(ConsoleState state, String command) throws IOException {
		Console thisConsole = findConsoleMap.get(state);
		
		if (thisConsole == null) {
			Log.log(Log.ERROR, this, "Unable to acquire Console object.");
			return;
		}
		
		boolean hasProjectViewer = jEdit.getPlugin("projectviewer.ProjectPlugin") != null;
		boolean useLeiningen = false;
		String rootPath = null;
		String shellName = "Clojure";
		
		if (getBooleanProperty("plainShell")) {
			// Start plain shell from Clojure.jar.
		} else if (getBooleanProperty("leinIfProject")) {
			// Start Leiningen shell if using ProjectViewer, otherwise plain shell.
			// plainShell is computed from various values here.
			if (hasProjectViewer) {
				projectviewer.vpt.VPTProject project =
					projectviewer.ProjectViewer.getActiveProject(thisConsole.getView());
				
				if (project == null) {
					Log.log(Log.DEBUG, this,
						"No project selected for Leiningen REPL.");
				} else {
					// We have a project, does it have a root path?
					rootPath = project.getRootPath();
					
					if (rootPath == null || rootPath.isEmpty()) {
					    // ProjectViewer configuration should not allow this case.
						Log.log(Log.DEBUG, this,
							"Current project has no root directory for Leiningen REPL.");
						rootPath = null; // simplify later code.
					} else {
						// We have a root directory, does it have a project.clj?
						File file = new File(rootPath + File.separator + "project.clj");
						
						if (file.exists() && file.isFile()) {
							// OK, it is probably a valid Leiningen project.
							shellName = "Leiningen";
							useLeiningen = true;
						} else {
							Log.log(Log.DEBUG, this,
								"No project.clj in current project for Leiningen REPL.");
						}
					}
				}
			}
		} else if (getBooleanProperty("leinAlways")) {
			// Start Leiningen shell regardless.
			shellName = "Leiningen";
			useLeiningen = true;
		} else {
			Log.log(Log.ERROR, this,
				"Unrecognized shell choice, default to plain shell.");
		}
		
		Log.log(Log.DEBUG, this, "Attempting to start " + shellName + " REPL.");
		
		ProcessBuilder procBuilder = null;
		
		if (useLeiningen) {
			String cmd = getProperty("leinRunCmd");
			
			Log.log(Log.DEBUG, this, "Cmd: " + cmd);
			procBuilder = new ProcessBuilder(cmd.split("\\s+"));
			
			if (rootPath != null) {
				Log.log(Log.DEBUG, this,
					"Leiningen starting in directory " + rootPath);
				procBuilder.directory(new File(rootPath));
			}
		} else {
			String cmd = getProperty("plainShellRunCmd")
				.replace("$CLOJURE",
					console.ConsolePlugin.getSystemShellVariableValue(
						thisConsole.getView(), "CLOJURE"));
			
			Log.log(Log.DEBUG, this, "Cmd: " + cmd);
			procBuilder = new ProcessBuilder(cmd.split("\\s+"));
		}
		
		state.p = procBuilder.start();
		
		Log.log(Log.DEBUG, this, shellName + " REPL started.");
	}
	//}}}
	
	//{{{ eval()
	/**
	 * Evaluate text
	 */
	public void eval(Console console, String str) {
		String cmd = "(load-string \""+str.replace("\"", "\\\"")+"\")";
		send(console, cmd);
	} //}}}

	//{{{ evalBuffer()
	/**
	 * Evaluate a buffer
	 */
	public void evalBuffer(Console console, Buffer buffer) {
		String cmd = "(load-file \""+buffer.getPath().replace("\\", "\\\\")+"\")";
		send(console, cmd);
	} //}}}

	protected void onRead(ConsoleState state, String str, Output output) {
		if (str.indexOf("\n") != -1) {
			str = str.substring(str.lastIndexOf("\n")+1);
		}
		if (str.matches(prompt)) {
			state.waiting = false;
			output.commandDone();
		}
	}

	public void printInfoMessage(Output output) {
		output.print(null, jEdit.getProperty("msg.clojureshell.info-message"));
	}

}
