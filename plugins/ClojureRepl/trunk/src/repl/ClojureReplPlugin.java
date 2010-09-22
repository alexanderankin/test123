package repl;
//{{{ Imports
import console.Shell;
import console.ClojureShell;
import org.gjt.sp.jedit.EditPlugin;
//}}}
public class ClojureReplPlugin extends EditPlugin {
	public void start() {}
	public void stop() {
		try {
			ClojureShell shell = (ClojureShell) Shell.getShell("Clojure");
			shell.stop();
		} catch (Exception e) {}
	}
}
