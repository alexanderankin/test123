package utils;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.msg.BufferUpdate;

public class BufferWatcher implements EBComponent {

	private Watcher watcher;
	
	public interface Watcher {
		void loaded(String file);
		void saved(String file);
	}
	
	public BufferWatcher(Watcher watcher) {
		this.watcher = watcher;
		EditBus.addToBus(this);
	}
	
	public void shutdown() {
		this.watcher = null;
		EditBus.removeFromBus(this);
	}

	public void handleMessage(EBMessage message) {
		if (! (message instanceof BufferUpdate))
			return;
		BufferUpdate bu = (BufferUpdate) message;
		String file = bu.getBuffer().getPath();
		if (bu.getWhat() == BufferUpdate.SAVED)
			watcher.saved(file);
		if (bu.getWhat() == BufferUpdate.LOADED)
			watcher.loaded(file);
	}

}
