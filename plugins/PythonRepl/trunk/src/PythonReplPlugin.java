/**
 * @author Damien Radtke
 * class PythonReplPlugin
 * Main class for the PythonRepl plugin
 */
//{{{ Imports
import console.Shell;
import console.PythonShell;
import org.gjt.sp.jedit.EditPlugin;
//}}}
public class PythonReplPlugin extends EditPlugin {
	public void start() {}
	public void stop() {
		PythonShell shell = (PythonShell) Shell.getShell("Python");
		shell.stop();
	}
}
