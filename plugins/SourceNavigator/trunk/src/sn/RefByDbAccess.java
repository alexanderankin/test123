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
		public String sig, refBySig;
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
			s = dataToStrings(data);
			sig = s[0];
			refBySig = s[1];
		}
		public String getIdentifier() {
			if (type.equals("#"))
				return name;
			return type + "::" + name;
		}
		public SourceElement refBySourceElement(String dir) {
			return new SourceElement(refByType, refByName, refByKind, refBySig, file, line, dir);
		}
		private String [] dataToStrings(DatabaseEntry data) {
			byte [] bytes = data.getData();
			String [] s = new String[2];
			int i = 1;
			while (bytes[i] != '}')
				i++;
			s[0] = new String(bytes, 1, i - 1);
			i += 3;
			int start = i;
			while (bytes[i] != '}')
				i++;
			s[1] = new String(bytes, start, i - start);
			return s;
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
	private DatabaseEntry identifierToKey(String identifier) {
        int index = identifier.lastIndexOf("::");
        byte[] bytes;
        if (index >= 0) {
        	String namespace = identifier.substring(0, index);
        	String name = identifier.substring(index + 2);
        	bytes = new byte[index + 1 + name.length() + 1];
        	for (int i = 0; i < namespace.length(); i++)
        		bytes[i] = (byte) namespace.charAt(i);
        	bytes[index] = 1;
        	for (int i = 0; i < name.length(); i++)
        		bytes[index + 1 + i] = (byte) name.charAt(i);
        } else {
        	bytes = new byte[3 + identifier.length()];
        	bytes[0] = '#';
        	bytes[1] = 1;
        	for (int i = 0; i < identifier.length(); i++)
        		bytes[2 + i] = (byte) identifier.charAt(i);
        }
    	bytes[bytes.length - 1] = 1;
        return new DatabaseEntry(bytes);
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
	public void lookup(String identifier, RefByRecordHandler handler)
	{
		lookup(identifierToKey(identifier), new DatabaseEntry(), handler);
	}
	public void lookup(DatabaseEntry key, DatabaseEntry data, RefByRecordHandler handler)
	{
		super.lookup(key, data, new MyRecordHandler(handler));
	}
}
