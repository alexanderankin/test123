package sn;

import java.io.FileNotFoundException;

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
}
