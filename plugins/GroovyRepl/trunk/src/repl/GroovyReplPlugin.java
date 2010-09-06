package repl;
//{{{ Imports
import console.Shell;
import console.GroovyShell;
import org.gjt.sp.jedit.EditPlugin;
//}}}
public class GroovyReplPlugin extends EditPlugin {
	public void start() {}
	public void stop() {
		try {
			GroovyShell shell = (GroovyShell) Shell.getShell("Groovy");
			shell.stop();
		} catch (Exception e) {}
	}
}
