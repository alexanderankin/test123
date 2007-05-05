package gdb.breakpoints;


import gdb.core.GdbState;
import gdb.core.GdbState.State;
import gdb.core.GdbState.StateListener;

import java.util.Vector;

public class BreakpointList implements StateListener {
	static private BreakpointList instance = null;
	Vector<Breakpoint> breakpoints = new Vector<Breakpoint>();
	Vector<BreakpointListListener> listeners = new Vector<BreakpointListListener>();
	
	public interface BreakpointListListener {
		void breakpointAdded(Breakpoint bp);
		void breakpointRemoved(Breakpoint bp);
		void breakpointChanged(Breakpoint bp);
	}
	
	private BreakpointList() {
		GdbState.addStateListener(this);
	}
	static public BreakpointList getInstance() {
		if (instance == null)
			instance = new BreakpointList();
		return instance;
	}
	public Vector<Breakpoint> getBreakpoints() {
		return breakpoints;
	}
	public void addListListener(BreakpointListListener l) {
		listeners.add(l);
	}
	public void removeListListener(BreakpointListListener l) {
		listeners.remove(l);
	}
	public void add(Breakpoint b) {
		breakpoints.add(b);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).breakpointAdded(b);
	}
	public void remove(Breakpoint b) {
		breakpoints.remove(b);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).breakpointRemoved(b);
	}
	public Vector<Breakpoint> get(String file, int line) {
		Vector<Breakpoint> brkpts = new Vector<Breakpoint>();
		for (int i = 0; i < breakpoints.size(); i++) {
			Breakpoint b = breakpoints.get(i);
			if (b.getFile().equals(file) && b.getLine() == line)
				brkpts.add(b);
		}
		return brkpts;
	}
	public void stateChanged(State prev, State current) {
		if (current == State.IDLE) {
			for (int i = 0; i < breakpoints.size(); i++) {
				breakpoints.get(i).reset();
			}
		}
	}
}
