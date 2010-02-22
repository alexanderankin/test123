package projectbuilder.command;
// imports {{{
import org.gjt.sp.jedit.gui.DockableWindowManager;
import console.Shell;
import console.Console;
// }}} imports
public class ShellRunner extends Thread {
	private Shell shell;
	private String[] commands;
	private Console console;
	private DockableWindowManager wm;
	public ShellRunner(DockableWindowManager wm, Shell shell, String[] commands) {
		this.wm = wm;
		this.shell = shell;
		this.commands = commands;
	}
	public void run() {
		wm.addDockableWindow("console");
		console = (Console) wm.getDockableWindow("console");
		// If the shell is Ant, clear it first
		if (shell.getName().equals("Ant")) console.clear();
		for (int i = 0; i<commands.length; i++) {
		   console.run(shell, commands[i]);
		   shell.waitFor(console);
	   }
	}
}
