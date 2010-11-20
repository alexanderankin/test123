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

package perl.core;

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
	
	static public boolean isStopped() {
		return (state == State.PAUSED || state == State.DEAD);
	}
	static public boolean isRunning() {
		return (state != State.IDLE);
	}
}