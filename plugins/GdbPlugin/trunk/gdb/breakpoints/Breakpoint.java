package gdb.breakpoints;

import gdb.core.CommandManager;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

public class Breakpoint {
	String file;
	int line;
	int number;
	View view;
	Buffer buffer;
	BreakpointPainter painter;
	boolean enabled;
	String condition = "";
	int skipCount = 0;
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
		this.file = buffer.getPath();
		this.line = line;
		initialize();
		this.view = view;
		this.buffer = buffer;
		addPainter();
		enabled = true;
		BreakpointList.getInstance().add(this);
	}
	public void initialize() {
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null) {
			if (file != null)
				commandManager.add("-break-insert " + file + ":" + line,
						new BreakpointResultHandler(this));
			else // Watchpoint
				commandManager.add("-break-watch " + options + " " + what,
						new BreakpointResultHandler(this));
		}
	}
	public boolean isBreakpoint() {
		return (file != null);
	}
	public String getFile() {
		return file;
	}
	public int getLine() {
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
		this.enabled = enabled;
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null)
			if (enabled)
				commandManager.add("-break-enable " + number);
			else
				commandManager.add("-break-disable " + number);
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
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null)
			commandManager.add("-break-delete " + number);
		removePainter();
	}
	public void setNumber(int num) {
		number = num;
		// In case of delayed activation (e.g. breakpoint properties set
		// prior to gdb invocation), this is the time to set the condition
		// and skip count.
		if (condition.length() > 0) {
			setCondition(condition);
		}
		if (skipCount > 0) {
			setSkipCount(skipCount);
		}
		setEnabled(enabled);
	}
	public int getNumber() {
		return number;
	}
	public void setSkipCount(int count) {
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null)
			commandManager.add("-break-after " + number + " " + count);
		skipCount = count;
	}
	public int getSkipCount() {
		return skipCount;
	}
	public void setCondition(String cond) {
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null)
			commandManager.add("-break-condition " + number + " " + cond);
		condition = cond;
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
