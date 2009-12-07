package projectbuilder.build
import javax.swing.JComponent
import org.gjt.sp.jedit.jEdit as JEDIT
import antfarm.AntFarmPlugin
/**
 * This class is just a workaround to a bug that causes an error to show
 * in ErrorList even with a successful build. This is caused when a build attempt fails,
 * and then a successful one is attempted. When this happens the first successful
 * build still shows an error
 */
public class BuildWatcher extends Thread {
	private JComponent console
	public BuildWatcher(JComponent console) {
		this.console = console
	}
	public void run() {
		console.getShell().waitFor(console)
		if (console.getConsolePane().getText().indexOf("BUILD SUCCESSFUL") != -1) {
			AntFarmPlugin.getErrorSource().clear()
			JEDIT.getActiveView().getDockableWindowManager().showDockableWindow("console")
		}
	}
}
