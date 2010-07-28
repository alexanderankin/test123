package slime.repl;
/**
 * @author Damien Radtke
 * class Python
 * Defines a REPL for running Groovy
 */
//{{{ Imports
import java.io.File;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.util.Log;
import slime.REPL;
import org.gjt.sp.jedit.MiscUtilities;
//}}}
public class Groovy extends REPL {
	
	private final String terminal = "-Djline.terminal="+
		"jline.UnsupportedTerminal";
	
	/**
	 * 1) Attempts 'groovysh' on the system path
	 * 2) Attempts to locate groovysh via the GROOVY_HOME environment variable
	 */
	public Process getProcess() {
		Process p = null;
		try {
			String groovy = "groovysh";
			if (OperatingSystem.isWindows()) groovy += ".bat";
			p = Runtime.getRuntime().exec(groovy+" "+terminal);
		}
		catch (Exception e) {
			Log.log(Log.ERROR, this, "Starting Groovy, try 1 failed: "+e);
			try {
				String groovy = System.getenv("GROOVY_HOME");
				String path = MiscUtilities.constructPath(groovy, "bin/groovysh");
				if (OperatingSystem.isWindows()) path += ".bat";
				p = Runtime.getRuntime().exec("\""+path+"\" "+terminal);
			}
			catch (Exception _e) {
				Log.log(Log.ERROR, this, "Starting Groovy, try 2 failed: "+_e);
			}
		}
		finally {
			return p;
		}
	}
	
	public String getPromptRegex() {
		return "groovy:\\d{3}> ";
	}
	
}
