package projectbuilder.command;
// imports {{{
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;
import console.Shell;
import console.Console;
// }}} imports
public class ShellRunner extends Thread {
	private String shell_name;
	private String[] commands;
	private Console console;
	private View view;
	public ShellRunner(View view, String shell_name, String[] commands) {
		this.view = view;
		this.shell_name = shell_name;
		this.commands = commands;
	}
	public void run() {
		Shell shell = Shell.getShell(shell_name);
		if (shell == null) {
			GUIUtilities.error(view, "projectBuilder.msg.no-shell", new String[] { shell_name });
			return;
		}
		DockableWindowManager wm = view.getDockableWindowManager();
		wm.addDockableWindow("console");
		console = (Console) wm.getDockableWindow("console");
		for (int i = 0; i<commands.length; i++) {
			console.run(shell, commands[i]);
			shell.waitFor(console);
		}
	}
}
