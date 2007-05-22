package gdb.output;

import org.gjt.sp.jedit.jEdit;

import console.Console;
import console.Output;
import debugger.jedit.Plugin;

public class ProgramShell extends BaseShell {

	static final String PREFIX = Plugin.OPTION_PREFIX;
	static final String MI_SHELL_INFO_MSG_PROP = PREFIX + "program_shell_info_msg";
	public static String NAME = "Program";

	public ProgramShell() {
		super(NAME);
	}
	
	public ProgramShell(String arg0) {
		super(arg0);
	}

	public void printInfoMessage (Output output) {
		output.print(getConsole().getPlainColor(),
				jEdit.getProperty(MI_SHELL_INFO_MSG_PROP));
	}
	
	public void append(String s) {
		if (s.endsWith("\n"))
			s = s.substring(0, s.length() - 1);
		print(s);
	}
	public void appendError(String s) {
		if (s.endsWith("\n"))
			s = s.substring(0, s.length() - 1);
		printError(s);
	}
	@Override
	public void execute(Console console, String input,
			Output output, Output error, String command) {
		// TO DO
	}

}
