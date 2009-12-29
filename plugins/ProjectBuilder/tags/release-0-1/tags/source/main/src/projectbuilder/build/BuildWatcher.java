package projectbuilder.build;
// imports {{{
import javax.swing.JComponent;
import org.gjt.sp.jedit.jEdit;
import antfarm.AntFarmPlugin;
import console.Console;
// }}} imports
/**
 * This class is just a workaround to a bug that causes an error to show
 * in ErrorList even with a successful build. This is caused when a build attempt fails,
 * and then a successful one is attempted. When this happens the first successful
 * build still shows an error
 */
public class BuildWatcher extends Thread {
	private Console console;
	public BuildWatcher(Console console) {
		this.console = console;
	}
	public void run() {
		console.getShell().waitFor(console);
		if (console.getConsolePane().getText().indexOf("BUILD SUCCESSFUL") != -1) {
			AntFarmPlugin.getErrorSource().clear();
			jEdit.getActiveView().getDockableWindowManager().showDockableWindow("console");
		}
	}
}
