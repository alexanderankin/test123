/**
 * 
 */
package gdb.breakpoints;

import gdb.core.CommandManager;
import debugger.itf.IBreakpoint;

public class GdbBreakpoint implements IBreakpoint	{
	String file;
	int line;
	int number;
	public GdbBreakpoint(String file, int line) {
		this.file = file;
		this.line = line;
	}
	public void setNumber(int num) {
		System.err.println("bp at " + file + ":" + line + " -> " + num);
		number = num;
	}
	public String getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}
	public boolean canSetEnabled() {
		return true;
	}
	public void setEnabled(boolean enabled) {
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null)
			if (enabled)
				commandManager.add("-break-enable " + number);
			else
				commandManager.add("-break-disable " + number);
	}
	public void remove() {
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null)
			commandManager.add("-break-delete " + number);
	}
}