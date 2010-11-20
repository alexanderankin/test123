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

package perl.breakpoints;

import javax.swing.text.Position;

import perl.core.CommandManager;
import perl.core.Debugger;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

public class Breakpoint {
	String file;
	Position pos = null;
	int line;	// Just in case 'pos' cannot be created
	int number;
	View view;
	Buffer buffer;
	BreakpointPainter painter;
	boolean enabled;
	String condition = "";
	int skipCount = 0;
	boolean initialized = false;
	// For watchpoints
	String what;
	String options;
	String when;
	
	public Breakpoint(String what, boolean read, boolean write) {
		this.file = null;
		this.view = null;
		if (read) {
			if (write) {
				this.options = "-a";
				this.when = "access";
			} else {
				this.options = "-r";
				this.when = "read";
			}
		} else {
			this.options = "";
			this.when = "write";
		}
		this.what = what;
		initialize();
		BreakpointList.getInstance().add(this);
	}
	public Breakpoint(View view, Buffer buffer, int line) {
		this.view = view;
		this.buffer = buffer;
		this.line = line;
		this.file = buffer.getPath();
		this.pos = buffer.createPosition(buffer.getLineStartOffset(line));
		initialize();
		addPainter();
		enabled = true;
		BreakpointList.getInstance().add(this);
	}
	public void reset() {
		initialized = false;
	}
	public void initialize() {
		if (initialized)
			return;
		CommandManager commandManager = getCommandManager();
		if (commandManager != null) {
			if (file != null) {
				String fileName = file;
				if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1) {
					fileName = file.replace('\\', '/');
				}
				commandManager.add("-break-insert " + fileName + ":" + getLine(),
						new BreakpointResultHandler(this));
			}
			else // Watchpoint
				commandManager.add("-break-watch " + options + " " + what,
						new BreakpointResultHandler(this));
			initialized = true;
		}
	}
	public boolean isBreakpoint() {
		return (file != null);
	}
	public String getFile() {
		return file;
	}
	public int getLine() {
		if (buffer != null && pos != null)
			return buffer.getLineOfOffset(pos.getOffset());
		return line;
	}
	public Buffer getBuffer() {
		return buffer;
	}
	private void addPainter() {
		painter = new BreakpointPainter(view.getEditPane(), buffer, this);
		view.getTextArea().getGutter().addExtension(painter);
	}
	private void removePainter() {
		if (painter == null)
			return;
		view.getTextArea().getGutter().removeExtension(painter);
	}
	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled)
			return;
		this.enabled = enabled;
		gdbSetEnabled(false);
	}
	private void gdbSetEnabled(boolean now) {
		CommandManager commandManager = getCommandManager();
		if (commandManager != null) {
			String cmd = null;
			if (enabled)
				cmd = "-break-enable " + number;
			else
				cmd = "-break-disable " + number;
			if (now)
				commandManager.addNow(cmd);
			else
				commandManager.add(cmd);
		}
		// Update the painter
		if (painter != null) {
			removePainter();
			addPainter();
		}
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void remove() {
		BreakpointList.getInstance().remove(this);
		CommandManager commandManager = getCommandManager();
		if (commandManager != null)
			commandManager.add("-break-delete " + number);
		removePainter();
	}
	public void setNumber(int num) {
		number = num;
		// In case of delayed activation (e.g. breakpoint properties set
		// prior to gdb invocation), this is the time to set the condition
		// and skip count.
		if (condition.length() > 0)
			gdbSetCondition(true);
		if (skipCount > 0)
			gdbSetSkipCount(true);
		// Breakpoint is always initially enabled
		if (! enabled) {
			gdbSetEnabled(true);
		}
	}
	public int getNumber() {
		return number;
	}
	public void setSkipCount(int count) {
		if (skipCount == count)
			return;
		skipCount = count;
		gdbSetSkipCount(false);
	}
	private void gdbSetSkipCount(boolean now) {
		CommandManager commandManager = getCommandManager();
		if (commandManager != null) {
			String cmd = "-break-after " + number + " " + skipCount;
			if (now)
				commandManager.addNow(cmd);
			else
				commandManager.add(cmd);
		}
	}
	public int getSkipCount() {
		return skipCount;
	}
	public void setCondition(String cond) {
		if (condition.equals(cond))
			return;
		condition = cond;
		gdbSetCondition(false);
	}
	private CommandManager getCommandManager() {
		return Debugger.getInstance().getCommandManager();
	}
	private void gdbSetCondition(boolean now) {
		CommandManager commandManager = getCommandManager();
		if (commandManager != null) {
			String cmd = "-break-condition " + number + " " + condition;
			if (now)
				commandManager.addNow(cmd);
			else
				commandManager.add(cmd);
		}
	}
	public String getCondition() {
		return condition;
	}
	public String getWhat() {
		return what;
	}
	public String getWhen() {
		return when;
	}
}
