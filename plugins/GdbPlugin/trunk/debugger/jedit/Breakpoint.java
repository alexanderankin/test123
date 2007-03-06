package debugger.jedit;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import debugger.core.DebuggerDB;
import debugger.itf.DebuggerTool;
import debugger.itf.IBreakpoint;

public class Breakpoint implements IBreakpoint {
	View view;
	DebuggerTool debugger;
	Buffer buffer;
	IBreakpoint breakpoint;
	BreakpointPainter painter;
	boolean enabled;
	Breakpoint(View view, DebuggerTool debugger, Buffer buffer, int line) {
		breakpoint = debugger.addBreakpoint(buffer.getPath(), line);
		if (breakpoint == null)
			return;
		this.view = view;
		this.debugger = debugger;
		this.buffer = buffer;
		addPainter();
		enabled = true;
		DebuggerDB.getInstance().addBreakpoint(this);
	}
	public IBreakpoint getBreakpoint() {
		return breakpoint;
	}
	public String getFile() {
		return breakpoint.getFile();
	}
	public int getLine() {
		return breakpoint.getLine();
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
		if (breakpoint.canSetEnabled())
			breakpoint.setEnabled(enabled);
		else {
			if (enabled) {
				breakpoint = debugger.addBreakpoint(getFile(), getLine());
				if (breakpoint == null) {
					// Couldn't re-enable, remove completely
					DebuggerDB.getInstance().removeBreakpoint(this);
					removePainter();
					return;
				}
			} else {
				breakpoint.remove();
				breakpoint = null;
			}
		}
		// Update the painter
		removePainter();
		addPainter();
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void remove() {
		DebuggerDB.getInstance().removeBreakpoint(this);
		if (breakpoint != null) {
			breakpoint.remove();
			breakpoint = null;
		}
		removePainter();
	}
	public boolean canSetEnabled() {
		return breakpoint.canSetEnabled();
	}
}
