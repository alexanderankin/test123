package gdb.output;

import gdb.core.CommandManager;
import gdb.core.Debugger;

import org.gjt.sp.jedit.jEdit;

import console.Console;
import console.ConsolePlugin;
import console.Output;
import console.Shell;
import debugger.jedit.Plugin;

public class MIShell extends Shell {
	static final String PREFIX = Plugin.OPTION_PREFIX;
	static final String MI_SHELL_INFO_MSG_PROP = PREFIX + "mi_shell_info_msg";
	public static String NAME = "GDB/MI";
	private Output output = null;

	public MIShell() {
		super(NAME);
	}
	
	public MIShell(String arg0) {
		super(arg0);
	}

	private Console getConsole() {
		return ConsolePlugin.getConsole(jEdit.getActiveView());
	}
	private CommandManager getCommandManager() {
		return Debugger.getInstance().getCommandManager();
	}
	
	public void printInfoMessage (Output output) {
		output.print(getConsole().getPlainColor(),
				jEdit.getProperty(MI_SHELL_INFO_MSG_PROP));
		this.output = output;
	}
	
	public void append(String s) {
		if (output == null)	{
			getConsole().setShell(this);	// The only way to create an Output
			output = getConsole().getOutput();
		}
		if (s.endsWith("\n"))
			s = s.substring(0, s.length() - 1);
		output.print(getConsole().getPlainColor(), s);
	}
	@Override
	public void execute(Console console, String input,
			Output output, Output error, String command) {
		getCommandManager().add(command);
	}

}
