package gdb.breakpoints;

import gdb.core.CommandManager;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import debugger.itf.DebuggerTool;

public class Breakpoint {
	String file;
	int line;
	int number;
	View view;
	DebuggerTool debugger;
	Buffer buffer;
	BreakpointPainter painter;
	boolean enabled;
	boolean initialized = false;
	public Breakpoint(View view, DebuggerTool debugger, Buffer buffer, int line) {
		this.file = buffer.getPath();
		this.line = line;
		initialize();
		this.view = view;
		this.debugger = debugger;
		this.buffer = buffer;
		addPainter();
		enabled = true;
		BreakpointList.getInstance().add(this);
	}
	public void initialize() {
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null) {
			commandManager.add("-break-insert " + file + ":" + line,
					new BreakpointResultHandler(this));
		}
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
		if (this.enabled == enabled)
			return;
		this.enabled = enabled;
		CommandManager commandManager = CommandManager.getInstance();
		if (commandManager != null)
			if (enabled)
				commandManager.add("-break-enable " + number);
			else
				commandManager.add("-break-disable " + number);
		// Update the painter
		removePainter();
		addPainter();
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
		initialized = true;
	}
	public int getNumber() {
		return number;
	}
}
