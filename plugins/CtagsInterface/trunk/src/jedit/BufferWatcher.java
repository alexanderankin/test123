package jedit;

import java.util.Vector;

import options.DirsOptionPane;
import options.GeneralOptionPane;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.msg.BufferUpdate;

import ctags.Runner;
import db.TagDB;

public class BufferWatcher implements EBComponent {

	private TagDB db;
	private Runner runner;
	
	public BufferWatcher(TagDB db) {
		EditBus.addToBus(this);
		this.db = db;
		runner = null;
	}
	
	public void shutdown() {
		EditBus.removeFromBus(this);
	}
	
	public void handleMessage(EBMessage message) {
		if (! (message instanceof BufferUpdate))
			return;
		BufferUpdate bu = (BufferUpdate) message;
		if ((GeneralOptionPane.getUpdateOnSave() && bu.getWhat() == BufferUpdate.SAVED) ||
			(GeneralOptionPane.getUpdateOnLoad() && bu.getWhat() == BufferUpdate.LOADED))
		{
			String file = bu.getBuffer().getPath();
			if (monitored(file))
				update(file);
		}
	}

	private void update(String file) {
		db.deleteRowsWithValue(TagDB.FILE_COL, file);
		if (runner == null)
			runner = new Runner(db);
		runner.run(file);
	}

	private boolean monitored(String file) {
		return isInMonitoredTree(file) ||
			db.containsValue(TagDB.FILE_COL, file);
	}

	private boolean isInMonitoredTree(String file) {
		Vector<String> dirs = DirsOptionPane.getDirs();
		file = MiscUtilities.resolveSymlinks(file);
		for (int i = 0; i < dirs.size(); i++)
			if (file.startsWith(dirs.get(i)))
				return true;
		return false;
	}

}
