package slime;
//{{{ Imports
import console.Console;
import console.ConsolePlugin;
import console.Output;
import console.Shell;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import slime.REPLShell;
//}}}
public class SlimePlugin extends EditPlugin {
	public void start() {}
	public void stop() {
		View[] views = jEdit.getViews();
		for (int i = 0; i<views.length; i++) {
			DockableWindowManager wm = views[i].getDockableWindowManager();
			Console console = (Console) wm.getDockable("console");
			if (console != null) {
				Shell.getShell("REPL").stop(console);
			}
		}
	}
	
	public static void startREPL(String name) {
		REPL repl = (REPL) ServiceManager.getService("slime.REPL", name);
		DockableWindowManager wm = 
						jEdit.getActiveView().getDockableWindowManager();
						
		Console console = (Console) wm.getDockable("console");
		if (console == null) {
			wm.addDockableWindow("console");
			console = (Console) wm.getDockable("console");
		}
		REPLShell shell = (REPLShell) Shell.getShell("REPL");
		console.setShell(shell);
		shell.startREPL(repl);
	}
	
	/**
	 * Convenience method to evaluate a string in the running REPL
	 * @param view the current view
	 * @param str the string to evaluate
	 */
	public static void eval(View view, String str) {
		REPLShell shell = getREPLShell(view);
		if (!shell.isRunning()) {
			shell.printNoREPLError();
			return;
		}
		REPL repl = shell.getRunningREPL();
		String cmd = repl.getEvalCommand(str);
		if (cmd == null) {
			shell.sendToREPL(str);
		} else {
			shell.sendToREPL(cmd);
		}
	}
	
	/**
	 * Evaluates 'buffer' in the running REPL
	 * @param view the current view
	 * @param buffer the buffer to evaluate
	 */
	public static void evalBuffer(View view, Buffer buffer) {
		REPLShell shell = getREPLShell(view);
		if (!shell.isRunning()) {
			shell.printNoREPLError();
			return;
		}
		REPL repl = shell.getRunningREPL();
		if (!buffer.isNewFile()) {
			String cmd = repl.getBufferEvalCommand(buffer);
			if (cmd != null) {
				cmd = cmd.replace("\\", "\\\\");
				shell.sendToREPL(cmd);
				return;
			}
		}
		eval(view, buffer.getText(0, buffer.getLength()));
	}
	
	/**
	 * Opens a choose file dialog and evaluates the chosen file, if any,
	 * in the running REPL
	 * @param view the current view
	 */
	public static void evalFile(View view) {
		VFSFileChooserDialog dialog = new VFSFileChooserDialog(view,
			System.getProperty("user.home")+File.separator,
			VFSBrowser.OPEN_DIALOG, false, true);
		String[] files = dialog.getSelectedFiles();
		if (files != null) {
			File f = new File(files[0]);
			Buffer b = jEdit.openTemporary(view, f.getParent(), f.getName(),
				false);
			evalBuffer(view, b);
		}
	}
	
	/**
	 * Returns the REPL shell for 'view'
	 * If it is hidden, the shell is opened; this ensures visibility
	 * @param view the current view
	 * @return the REPLShell instance of 'view'
	 */
	public static REPLShell getREPLShell(View view) {
		Console console = ConsolePlugin.getConsole(view);
		if (console == null) {
			view.getDockableWindowManager().addDockableWindow("console");
			console = ConsolePlugin.getConsole(view);
		}
		REPLShell shell = (REPLShell) console.setShell("REPL");
		return shell;
	}
}
