package python.shell;
/**
 * @author Damien Radtke
 * class PythonReplPlugin
 * Main class for the PythonShell plugin
 */
//{{{ Imports
import console.Shell;
import org.gjt.sp.jedit.EditPlugin;
//}}}
public class PythonShellPlugin extends EditPlugin {
	public void start() {}
	public void stop() {
		try {
			PythonShell shell = (PythonShell) Shell.getShell("Python");
			shell.stop();
		} catch (Exception e) {}
	}
}
