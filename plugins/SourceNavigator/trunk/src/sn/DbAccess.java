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
	
	public interface DbRecordFilter {
		boolean accept(DbRecord record);
	}
	static private class DbKeyRecordFilter implements DbRecordFilter {
		private String [] keyStrings;
		private boolean prefix;
		private int prefixIndex;
		public DbKeyRecordFilter(String key, boolean prefix) {
			keyStrings = key.split(SN_SEP);
			this.prefix = prefix;
			prefixIndex = (prefix ? keyStrings.length - 1 : keyStrings.length);
		}
		public boolean accept(DbRecord record) {
			if (keyStrings == null)
				return true;
			for (int i = 0; i < prefixIndex; i++)
				if (! record.getColumn(i).equals(keyStrings[i]))
					return false;
			if (prefix &&
				(! record.getColumn(prefixIndex).startsWith(keyStrings[prefixIndex])))
			{
				return false;
			}
			return true;
		}
	}
	static private class DbRecordCollector implements RecordHandler {
		private DbDescriptor desc;
		private String baseDir;
		private DbRecordFilter filter;
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
			DbRecord record = new DbRecord(desc, s);
			// Check if this record is not filtered
			if (filter != null && (! filter.accept(record)))
				return false;
			record.setBaseDir(baseDir);
			records.add(record);
			return true;
		}
		private void setFilter(DbRecordFilter filter) {
			this.filter = filter;
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
	static private DatabaseEntry textToKey(String text) {
		// If 'prefix' is set, get records starting with 'text'.
		// Otherwise, get records matching 'text'.
		DatabaseEntry key = new DatabaseEntry();
		byte [] bytes = text.getBytes();
		byte [] keyBytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			keyBytes[i] = (bytes[i] == FIND_FIELD_SEP) ? 1 : bytes[i];
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
			DatabaseEntry key = textToKey(text);
			handler.setFilter(new DbKeyRecordFilter(text, prefixKey));
			dba.lookup(key, data, handler);
		}
		return handler.getCollectedRecords();
	}
}
