package sn;

import com.sleepycat.db.DatabaseEntry;

public class RefByDbAccess extends DbAccess {

	public RefByDbAccess() {
		super("by");
	}
	public RefByDbAccess(String dir, String proj) {
		super(dir, proj, "by");
	}
	static public class RefByRecord {
		public String type, name, kind, refByType, refByName, refByKind, access, file;
		public int line;
		RefByRecord(DatabaseEntry key, DatabaseEntry data) {
			String [] s = keyToStrings(key);
			type = s[0];
			name = s[1];
			kind = s[2];
			refByType = s[3];
			refByName = s[4];
			refByKind = s[5];
			access = s[6];
			file = s[8];
			line = Integer.valueOf(s[7]);
		}
		public String getIdentifier() {
			if (type.equals("#"))
				return name;
			return type + "::" + name;
		}
		public SourceElement refBySourceElement(String dir) {
			return new SourceElement(refByType, refByName, refByKind, file, line, dir);
		}
		private String [] keyToStrings(DatabaseEntry key) {
			byte [] bytes = key.getData();
			String [] strings = new String[9];
			int start = 0;
			int index = 0;
			for (int i = 0; i < bytes.length && index < 9; i++) {
				if (bytes[i] <= 1) {
					strings[index++] = new String(bytes, start, i - start);
					start = i + 1;
				}
			}
			if (index < 9)
				strings[index] = new String(bytes, start, bytes.length - start - 1);
			return strings;
		}
	}
	public interface RefByRecordHandler {
		boolean handle(RefByRecord rec);
	}
	static private class MyRecordHandler implements RecordHandler {
		RefByRecordHandler handler;
		public MyRecordHandler(RefByRecordHandler handler) {
			this.handler = handler;
		}
		public boolean handle(DatabaseEntry key, DatabaseEntry data) {
			return handler.handle(new RefByRecord(key, data));
		}
	}
	public void lookup(DatabaseEntry key, DatabaseEntry data, RefByRecordHandler handler)
	{
		super.lookup(key, data, new MyRecordHandler(handler));
	}
}
