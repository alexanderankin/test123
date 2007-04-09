package gdb.core;

import java.util.Vector;

public class GdbState {

	public enum State { RUNNING, PAUSED, DEAD, IDLE };
	static private State state = State.IDLE;
	static private Vector<StateListener> listeners = new Vector<StateListener>();
	
	public interface StateListener {
		void stateChanged(State prev, State current);
	}
	
	static public void addStateListener(StateListener listener) {
		listeners.add(listener);
	}
	static public void removeStateListener(StateListener listener) {
		listeners.remove(listener);
	}
	static public void setState(State newState) {
		if (state == newState)
			return;
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).stateChanged(state, newState);
		state = newState;
	}
	static public State getState() {
		return state;
	}
	
	static public boolean isRunning() {
		return (state != State.IDLE);
	}
}