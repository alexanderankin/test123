package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.gjt.sp.jedit.jEdit;

public class TagDB {
	private Connection conn;
	private Set<String> columns;
	private String project;
	public static final String TABLE_NAME = "TAGS";
	public static final String NAME_COL = "K_NAME";
	public static final String FILE_COL = "K_FILE";
	public static final String PATTERN_COL = "K_PATTERN";
	public static final String PROJECT_COL = "K_PROJECT";
	public static final String LINE_COL = "A_LINE";
	public static final String ATTR_PREFIX = "A_";
	
	public TagDB() {
		removeStaleLock();
        try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection("jdbc:hsqldb:file:" +
					getDBFilePath(), "sa", "");
        } catch (final Exception e) {
			e.printStackTrace();
		}
		createTables();
		getColumns();
		project = "";
	}
	
	public void setProject(String project) {
		this.project = project;
	}
	public void unsetProject() {
		project = "";
	}
	
	public synchronized void update(String expression) throws SQLException {
		Statement st = conn.createStatement();
		try {
			if (st.executeUpdate(expression) == -1)
	            System.err.println("db error : " + expression);
		} catch (SQLException e) {
			System.err.println("SQL update: " + expression);
			throw e;
		}
        st.close();
    }
	
	public synchronized ResultSet query(String expression) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(expression);
        st.close();
        return rs;
    }
	
	public void shutdown()
	{
		try {
			Statement st = conn.createStatement();
	        st.execute("SHUTDOWN");
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertTag(Hashtable<String, String> info) {
		// Find missing columns and build the inserted value string
		info.put(PROJECT_COL, project);
		StringBuffer valueStr = new StringBuffer();
		StringBuffer columnStr = new StringBuffer();
		Set<Entry<String,String>> entries = info.entrySet();
		Iterator<Entry<String, String>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String col = entry.getKey().toUpperCase();
			String val = entry.getValue();
			if (! columns.contains(col)) {
				try {
					update("ALTER TABLE " + TABLE_NAME + " ADD " +
						col + " VARCHAR;");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				columns.add(col);
			}
			if (columnStr.length() > 0)
				columnStr.append(",");
			columnStr.append(col);
			if (valueStr.length() > 0)
				valueStr.append(",");
			valueStr.append(getValueString(val));
		}
		// Insert the record
		try {
			update("INSERT INTO " + TABLE_NAME + " (" +
					columnStr.toString() + ") VALUES (" +
					valueStr.toString() + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getValueString(String string) {
		return "'" + string.replaceAll("'", "''") + "'";
	}
	
	public boolean containsValue(String column, String value) {
		try {
			ResultSet rs = query("SELECT TOP 1 " + column + " FROM " +
				TABLE_NAME + " WHERE " + column + "=" + getValueString(value));
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void deleteRowsWithValue(String column, String value) {
		try {
			query("DELETE FROM " + TABLE_NAME + " WHERE " + column +
				"=" + getValueString(value));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteRowsWithValues(Hashtable<String, String> values) {
		StringBuffer where = new StringBuffer();
		Iterator<Entry<String, String>> it = values.entrySet().iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (first)
				first = false;
			else
				where.append(" AND ");
			Entry<String, String> entry = it.next();
			where.append(entry.getKey());
			where.append("=");
			where.append(getValueString(entry.getValue()));
		}
		try {
			query("DELETE FROM " + TABLE_NAME + " WHERE " + where);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteRowsWithValuePrefix(String column, String prefix) {
		try {
			query("DELETE FROM " + TABLE_NAME + " WHERE " + column +
				" LIKE " + getValueString(prefix + "%"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static public String attr2col(String attributeName) {
		return ATTR_PREFIX + attributeName.toUpperCase();
	}
	
	private void createTables() {
		try {
			update("CREATE CACHED TABLE " + TABLE_NAME +
				"(" + NAME_COL + " VARCHAR, " +
				FILE_COL + " VARCHAR, " +
				PATTERN_COL + " VARCHAR, " +
				PROJECT_COL + " VARCHAR)");
			update("CREATE INDEX tagName ON " + TABLE_NAME + "(" + NAME_COL + ")");
			update("CREATE INDEX fileName ON " + TABLE_NAME + "(" + FILE_COL + ")");
			update("CREATE INDEX projName ON " + TABLE_NAME + "(" + PROJECT_COL + ")");
		} catch (SQLException e) {
			// Table already exists
		}
	}
	
	private void getColumns() {
		columns = new HashSet<String>();
		try {
			ResultSet rs = query("SELECT * FROM " + TABLE_NAME +
				" WHERE " + NAME_COL + "=''");
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			for (int i = 0; i < cols; i++)
				columns.add(meta.getColumnName(i + 1));
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private String getDBFilePath() {
		return jEdit.getSettingsDirectory() + "/CtagsInterface/tagdb";
	}

	private void removeStaleLock() {
		File lock = new File(getDBFilePath() + ".lck");
		if (lock.exists())
			lock.delete();
	}
}
