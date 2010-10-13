package groovy.shell;
//{{{ Imports
import org.gjt.sp.jedit.EditPlugin;
import console.Shell;
//}}}
public class GroovyShellPlugin extends EditPlugin {
	public void start() {}
	public void stop() {
		try {
			GroovyShell shell = (GroovyShell) Shell.getShell("Groovy");
			shell.stop();
		} catch (Exception e) {}
	}
}
