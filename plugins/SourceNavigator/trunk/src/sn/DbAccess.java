package sn;

import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;


import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.OperationStatus;

public class DbAccess {
	
	private static final char FIND_FIELD_SEP = '?';
	private static final String SN_SEP = "\\?";
	private String dir;
	private String proj;
	private String db;
	
	// C-tor that uses the default database path
	public DbAccess(String db) {
		this(SourceNavigatorPlugin.getOption(MainOptionPane.DEFAULT_DIR),
			SourceNavigatorPlugin.getOption(MainOptionPane.DEFAULT_PROJ), db);
	}
	public DbAccess(String dir, String proj, String db) {
		this.dir = dir;
		this.proj = proj;
		this.db = db;
	}
	public String getDir() {
		return dir;
	}
	
	/* Basic record lookup */
	
	public interface RecordHandler {
		// Returns true to continue record iteration, false to abort it
		boolean handle(DatabaseEntry key, DatabaseEntry data);
	}
	public void lookup(DatabaseEntry key, DatabaseEntry data, RecordHandler handler)
	{
		if (dir == null || proj == null || dir.length() == 0 || proj.length() == 0) {
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Database properties not configured - please configure them first " +
				"using the plugin option pane.");
			return;
		}
		String dbPath = dir + "/SNDB4/" + proj + "." + db;
		Database db;
		try {
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(false);
	        dbConfig.setType(DatabaseType.BTREE);
	        db = new Database(dbPath, null, dbConfig);
			Cursor crs = db.openCursor(null, null);
			OperationStatus stat = crs.getSearchKeyRange(key, data, null);
			while (stat.equals(OperationStatus.SUCCESS))
			{
				if (! handler.handle(key, data))
					break;
				stat = crs.getNext(key, data, null);
			}
			db.close();
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Database not found at '" + dbPath + "'.");
		} catch (DatabaseException e1) {
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
				"Message: " + e1.getMessage() + "Stack trace:\n" +
				e1.getStackTrace());
		}
	}
	
	static private class DbRecordCollector implements RecordHandler {
		private DbDescriptor desc;
		private String baseDir;
		private String [] keyStrings;
		private boolean prefix;
		private Vector<DbRecord> records;
		public DbRecordCollector(DbDescriptor desc) {
			this.desc = desc;
			records = new Vector<DbRecord>();
		}
		public void setBaseDir(String baseDir) {
			this.baseDir = baseDir;
		}
		public Vector<DbRecord> getCollectedRecords() {
			return records;
		}
		public boolean handle(DatabaseEntry key, DatabaseEntry data) {
			String [] s = breakToStrings(key);
			// Check that the record matches the search key
			if (keyStrings != null) {
				for (int i = 0; i < keyStrings.length; i++) {
					if (! s[i].equals(keyStrings[i])) {
						if (! prefix || i < keyStrings.length - 1)
							return false;
						if (! s[i].startsWith(keyStrings[i]))
							return false;
					}
				}
			}
			DbRecord record = new DbRecord(desc, s);
			record.setBaseDir(baseDir);
			records.add(record);
			return true;
		}
		private void setSearchKey(String key, boolean prefixKey) {
			keyStrings = key.split(SN_SEP);
			prefix = prefixKey;
		}
		private String [] breakToStrings(DatabaseEntry e) {
			byte [] bytes = e.getData();
			int nStrings = desc.getColumnCount();
			String [] strings = new String[nStrings];
			int start = 0;
			int index = 0;
			for (int i = 0; i < bytes.length && index < nStrings; i++) {
				if (bytes[i] <= 1) {
					strings[index++] = new String(bytes, start, i - start);
					start = i + 1;
				}
			}
			if (index < nStrings)
				strings[index] = new String(bytes, start, bytes.length - start - 1);
			return strings;
		}
	}
	static private DatabaseEntry textToKey(String text, boolean prefix) {
		// If 'prefix' is set, get records starting with 'text'.
		// Otherwise, get records matching 'text'.
		DatabaseEntry key = new DatabaseEntry();
		byte [] bytes = text.getBytes();
		byte [] keyBytes = new byte[bytes.length + (prefix ? 1 : 0)];
		int i;
		for (i = 0; i < bytes.length; i++)
			keyBytes[i] = (bytes[i] == FIND_FIELD_SEP) ? 1	: bytes[i];
		if (prefix)
			keyBytes[i] = 1;
		key.setData(keyBytes);
		return key;
		
	}
	static public Vector<DbRecord> lookup(DbDescriptor desc, String text,
		boolean prefixKey)
	{
		DbAccess dba = new DbAccess(desc.db);
		DatabaseEntry data = new DatabaseEntry();
		DbRecordCollector handler = new DbRecordCollector(desc);
		handler.setBaseDir(dba.getDir());
		if (text == null || text.length() == 0) {
			// Get all records in the table
			DatabaseEntry key = new DatabaseEntry();
			dba.lookup(key, data, handler);
		} else {
			// Get records (possibly starting with text as prefix)
			DatabaseEntry key = textToKey(text, prefixKey);
			handler.setSearchKey(text, prefixKey);
			dba.lookup(key, data, handler);
		}
		return handler.getCollectedRecords();
	}
}
