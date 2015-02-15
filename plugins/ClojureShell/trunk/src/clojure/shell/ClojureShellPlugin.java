package clojure.shell;
//{{{ Imports
import console.Shell;
import org.gjt.sp.jedit.EditPlugin;
//}}}
public class ClojureShellPlugin extends EditPlugin {
	public static final String OPTION_PREFIX = "options.clojureshell.";
	
	public void start() {}
	public void stop() {
		try {
			ClojureShell shell = (ClojureShell) Shell.getShell("Clojure");
			shell.stop();
		} catch (Exception e) {}
	}
}

