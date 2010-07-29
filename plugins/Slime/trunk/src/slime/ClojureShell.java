package slime;
/**
 * @author Damien Radtke
 * class ClojureShell
 * TODO: comment
 */
//{{{ Imports
import console.Console;
import java.io.IOException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.util.Log;
//}}}
public class ClojureShell extends SlimeShell {
	
	public ClojureShell() {
		super("Clojure");
	}
	
	public void init(ConsoleState state) throws IOException {
		try {
			String clojure = "clojure";
			if (OperatingSystem.isWindows()) clojure += ".bat";
			state.p = Runtime.getRuntime().exec(clojure);
		}
		catch (Exception e) {
			Log.log(Log.ERROR,this,"Starting Clojure, try 1 failed: "+e);
			String clojureDir = System.getenv("CLOJURE_DIR");
			if (clojureDir == null) {
				throw new IOException("Environment variable \"CLOJURE_DIR\" "+
					"is not defined.");
			}
			String clojurePath = MiscUtilities.constructPath(
				clojureDir, "clojure.jar");
			state.p = Runtime.getRuntime().exec("java -cp \""+clojurePath+
				"\" clojure.main");
		}
	}
	
	public void eval(Console console, String str) {
		send(console, "(load-string \""+str.replace("\"", "\\\"")+"\")");
	}
	
	public void evalBuffer(Console console, Buffer buffer) {
		send(console, "(load-file \""+
			buffer.getPath().replace("\\", "/")+"\")");
	}
	
	public String getPromptRegex() {
		return "user=> ";
	}
}
