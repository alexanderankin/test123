package gdb.output;

import gdb.core.CommandManager;
import gdb.core.Debugger;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import console.Console;
import console.ConsolePlugin;
import console.Output;
import console.Shell;
import debugger.jedit.Plugin;

public abstract class BaseShell extends Shell {

	protected static final String DEBUGGER_NOT_STARTED = Plugin.MESSAGE_PREFIX + "debugger_not_started";
	private Output output = null;
	
	public BaseShell(String arg0) {
		super(arg0);
		getConsole().setShell(this);	// The only way to create an Output
		output = getConsole().getOutput();
	}

	protected Console getConsole() {
		View v = jEdit.getActiveView();
		Console c = ConsolePlugin.getConsole(v);
		if (c == null) {
			v.getDockableWindowManager().showDockableWindow("console");
			c = ConsolePlugin.getConsole(v);
		}
		return c;
	}
	protected CommandManager getCommandManager() {
		return Debugger.getInstance().getCommandManager();
	}
	protected Output getOutput() {
		return output;
	}
	@Override
	public void stop(Console arg0) {
		jEdit.getAction(Debugger.KILL_ACTION).invoke(jEdit.getActiveView());
	}
	protected void print(String s) {
		getOutput().print(getConsole().getPlainColor(), s);
	}
	protected void printError(String s) {
		getOutput().print(getConsole().getErrorColor(), s);
	}
	public void clear() {
		getConsole().setShell(this);
		getConsole().clear();
	}
}
