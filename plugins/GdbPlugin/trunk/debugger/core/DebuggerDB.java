package debugger.core;

import gdb.breakpoints.Breakpoint;

import java.util.HashSet;
import java.util.Vector;

import debugger.itf.IBreakpoint;

public class DebuggerDB {
	static private DebuggerDB instance = null;
	Vector<Breakpoint> breakpoints = new Vector<Breakpoint>();
	String currentFile = "";
	int currentLine = 0;
	Vector<BreakpointListListener> breakpointListeners =
		new Vector<BreakpointListListener>();
	
	public interface BreakpointListListener {
		void breakpointAdded(Breakpoint bp);
		void breakpointRemoved(Breakpoint bp);
	}
	
	private DebuggerDB() {
	}
	static public DebuggerDB getInstance() {
		if (instance == null)
			instance = new DebuggerDB();
		return instance;
	}
	public Vector<Breakpoint> getBreakpoints() {
		return breakpoints;
	}
	public HashSet<Integer> getBreakpointLines(String file) {
		HashSet<Integer> lines = new HashSet<Integer>();
		for (int i = 0; i < breakpoints.size(); i++) {
			IBreakpoint b = breakpoints.get(i);
			if (b.getFile().equals(file))
				lines.add(b.getLine());
		}
		return lines;
	}
	public void addBreakpointListListener(BreakpointListListener l) {
		breakpointListeners.add(l);
	}
	public void removeBreakpointListListener(BreakpointListListener l) {
		breakpointListeners.remove(l);
	}
	public void addBreakpoint(Breakpoint b) {
		breakpoints.add(b);
		for (int i = 0; i < breakpointListeners.size(); i++)
			breakpointListeners.get(i).breakpointAdded(b);
	}
	public void removeBreakpoint(Breakpoint b) {
		breakpoints.remove(b);
		for (int i = 0; i < breakpointListeners.size(); i++)
			breakpointListeners.get(i).breakpointRemoved(b);
	}
	public Vector<IBreakpoint> getBreakpoints(String file, int line) {
		Vector<IBreakpoint> brkpts = new Vector<IBreakpoint>();
		for (int i = 0; i < breakpoints.size(); i++) {
			IBreakpoint b = breakpoints.get(i);
			if (b.getFile().equals(file) && b.getLine() == line)
				brkpts.add(b);
		}
		return brkpts;
	}
	public void setCurrentLocation(String file, int line) {
		currentFile = file;
		currentLine = line;
	}
	public String getCurrentFile() {
		return currentFile;
	}
	public int getCurrentLine() {
		return currentLine;
	}
}
