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
//}}}
public class ClojureShell extends ProcessShell {

	private String prompt = "user=> ";

	/*
	 * Constructor for ClojureShell
	 */
	public ClojureShell() {
		super("Clojure");
	}

	//{{{ init()
	/**
	 * Start up Clojure
	 */
	protected void init(ConsoleState state, String command) throws IOException {
		ClojurePlugin clojure = (ClojurePlugin) jEdit.getPlugin("clojure.ClojurePlugin");
		String cp = clojure.getClojure() + File.pathSeparator + ClasspathPlugin.getClasspath();
		Log.log(Log.DEBUG,this,"Attempting to start Clojure process with classpath: "+cp);

		ProcessBuilder pb = new ProcessBuilder("java", "-cp", cp, "clojure.main");
		state.p = pb.start();
		Log.log(Log.DEBUG,this,"Clojure started.");
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
