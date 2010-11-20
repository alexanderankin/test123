/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl.output;

import perl.core.CommandManager;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;

import console.Console;
import console.Output;
import debugger.jedit.Plugin;

public class ProgramShell extends BaseShell {

	static final String PREFIX = Plugin.OPTION_PREFIX;
	static final String MI_SHELL_INFO_MSG_PROP = PREFIX + "program_shell_info_msg";
	public static final String NAME = "Program";
	
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
	
	public void printPrompt(Console console, Output output)
	{
		// No prompt - this is the stdin of the debugged program
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
		CommandManager cmdMgr = getCommandManager();
		if (cmdMgr != null)
			cmdMgr.addImmediateExecution(command);
		else
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
					jEdit.getProperty(DEBUGGER_NOT_STARTED));
	}

}
