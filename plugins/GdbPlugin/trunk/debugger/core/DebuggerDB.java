package debugger.core;

import java.util.HashSet;
import java.util.Vector;

import debugger.itf.IBreakpoint;

public class DebuggerDB {
	static private DebuggerDB instance = null;
	Vector<IBreakpoint> breakpoints = new Vector<IBreakpoint>();
	String currentFile = "";
	int currentLine = 0;
	
	private DebuggerDB() {
	}
	static public DebuggerDB getInstance() {
		if (instance == null)
			instance = new DebuggerDB();
		return instance;
	}
	public Vector<IBreakpoint> getBreakpoints() {
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
	public void addBreakpoint(IBreakpoint b) {
		breakpoints.add(b);
	}
	public void removeBreakpoint(IBreakpoint b) {
		breakpoints.remove(b);
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
