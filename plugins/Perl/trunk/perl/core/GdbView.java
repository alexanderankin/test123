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

import perl.core.GdbState.State;
import perl.core.GdbState.StateListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GdbView extends JPanel implements StateListener {

	public GdbView() {
		GdbState.addStateListener(this);
	}
	
	protected CommandManager getCommandManager() {
		return Debugger.getInstance().getCommandManager();
	}

	public void stateChanged(State prev, State current) {
		switch (current) {
		case IDLE:
			sessionEnded();
			break;
		case PAUSED:
			update();
			break;
		case RUNNING:
			running();
			break;
		}
	}

	public void running() {
	}
	
	public void update() {
	}

	public void sessionEnded() {
	}
}
