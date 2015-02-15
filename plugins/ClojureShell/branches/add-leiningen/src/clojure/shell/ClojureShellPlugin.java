package clojure.shell;
//{{{ Imports
import console.Shell;
import org.gjt.sp.jedit.EditPlugin;
//}}}
public class ClojureShellPlugin extends EditPlugin {
	public void start() {}
	public void stop() {
		try {
			ClojureShell shell = (ClojureShell) Shell.getShell("Clojure");
			shell.stop();
		} catch (Exception e) {}
	}
}
