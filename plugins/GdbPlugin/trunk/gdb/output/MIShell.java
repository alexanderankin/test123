package gdb.output;

import gdb.core.CommandManager;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;

import console.Console;
import console.Output;
import debugger.jedit.Plugin;

public class MIShell extends BaseShell {
	static final String PREFIX = Plugin.OPTION_PREFIX;
	static final String MI_SHELL_INFO_MSG_PROP = PREFIX + "mi_shell_info_msg";
	private static final String DEBUGGER_NOT_STARTED =
		Plugin.MESSAGE_PREFIX + "debugger_not_started";
	public static String NAME = "GDB/MI";

	public MIShell() {
		super(NAME);
	}
	
	public MIShell(String arg0) {
		super(arg0);
	}

	public void printInfoMessage (Output output) {
		output.print(getConsole().getPlainColor(),
				jEdit.getProperty(MI_SHELL_INFO_MSG_PROP));
	}
	
	public void printPrompt(Console console, Output output)
	{
		// No prompt - prompt given by gdb/mi itself
	}
	
	public void append(String s) {
		if (s.endsWith("\n"))
			s = s.substring(0, s.length() - 1);
		print(s);
	}
	@Override
	public void execute(Console console, String input,
			Output output, Output error, String command) {
		CommandManager cmdMgr = getCommandManager();
		if (cmdMgr != null)
			cmdMgr.add(command);
		else
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
					jEdit.getProperty(DEBUGGER_NOT_STARTED));
	}

}
