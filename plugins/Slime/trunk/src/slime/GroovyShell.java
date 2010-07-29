package slime;
/**
 * @author Damien Radtke
 * class GroovyShell
 * TODO: comment
 */
//{{{ Imports
import console.Console;
import java.io.IOException;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.util.Log;
//}}}
public class GroovyShell extends SlimeShell {
	
	private final String terminal = "-Djline.terminal="+
		"jline.UnsupportedTerminal";
	
	public GroovyShell() {
		super("Groovy");
	}
	
	public void init(ConsoleState state) throws IOException {
		try {
			String groovy = "groovysh";
			if (OperatingSystem.isWindows()) groovy += ".bat";
			state.p = Runtime.getRuntime().exec(groovy+" "+terminal);
		}
		catch (Exception e) {
			Log.log(Log.ERROR, this, "Starting Groovy, try 1 failed: "+e);
			String groovy = System.getenv("GROOVY_HOME");
			if (groovy == null) {
				throw new IOException("Environment variable \"GROOVY_HOME\" "+
					"is not defined.");
			}
			String path = MiscUtilities.constructPath(groovy, "bin/groovysh");
			if (OperatingSystem.isWindows()) path += ".bat";
			state.p = Runtime.getRuntime().exec("\""+path+"\" "+terminal);
		}
	}
	
	public String getPromptRegex() {
		return "groovy:\\d{3}> ";
	}
	
}
