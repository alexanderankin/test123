package gdb.core;

import gdb.core.GdbState.State;
import gdb.core.GdbState.StateListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GdbView extends JPanel implements StateListener {

	public GdbView() {
		GdbState.addStateListener(this);
	}
	
	protected CommandManager getCommandManager() {
		return CommandManager.getInstance();
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
