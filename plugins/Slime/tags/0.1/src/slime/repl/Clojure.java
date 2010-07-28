package slime.repl;
/**
 * @author Damien Radtke
 * class Clojure
 * Defines a REPL for running Clojure
 */
//{{{ Imports
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.util.Log;
import slime.REPL;
import org.gjt.sp.jedit.MiscUtilities;
//}}}
public class Clojure extends REPL {
	
	/**
	 * 1) Attempts 'clojure' on the system path
	 * 2) Attempts to locate clojure.jar via the CLOJURE_DIR env. variable
	 */
	public Process getProcess() {
		Process p = null;
		try {
			String clojure = "clojure";
			if (OperatingSystem.isWindows()) clojure += ".bat";
			p = Runtime.getRuntime().exec(clojure);
		}
		catch (Exception e) {
			try {
				Log.log(Log.ERROR,this,"Starting Clojure, try 1 failed: "+e);
				String clojureDir = System.getenv("CLOJURE_DIR");
				if (clojureDir != null) {
					String clojurePath = MiscUtilities.constructPath(
						clojureDir, "clojure.jar");
					p = Runtime.getRuntime().exec("java -cp \""+clojurePath+
						"\" clojure.main");
				}
			}
			catch (Exception _e) {
				Log.log(Log.ERROR,this,"Starting Clojure, try 2 failed: "+_e);
			}
		}
		finally {
			return p;
		}
	}
	
	public String getBufferEvalCommand(Buffer buffer) {
		return "(load-file \""+buffer.getPath().replace("\\", "/")+"\")";
	}
	
	public String getEvalCommand(String str) {
		return "(load-string \""+str.replace("\"", "\\\"")+"\")";
	}
	
	public String getPromptRegex() {
		return "user=> ";
	}
	
}
