package sn;

import java.util.Vector;

import utils.BufferWatcher.Watcher;


public class BufferUpdater implements Watcher {
	private DbDescriptor fileDescriptor;
	private int fileIndex;
	
	public BufferUpdater() {
		fileDescriptor = SourceNavigatorPlugin.getDbDescriptor("f");
		DbAccess dba = new DbAccess("f");
		fileIndex = dba.getDir().length() + 1;
		
	}
	public void loaded(String file) {
		if (monitored(file))
			update(file);
	}
	public void saved(String file) {
		if (monitored(file))
			update(file);
	}
	private void update(String file) {
		System.err.println("Updating " + file);
	}
	private boolean monitored(String file) {
		if (file.length() <= fileIndex)
			return false;
		file = file.substring(fileIndex).replace('\\', '/');
		Vector<DbRecord> records = DbAccess.lookupByKey(
			fileDescriptor, file, false);
		return (! records.isEmpty());
	}
}
