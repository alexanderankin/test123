package db;

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
	public static final String TABLE_NAME = "tags";
	
	public TagDB() {
        try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection("jdbc:hsqldb:file:" +
					getDBFilePath(), "sa", "");
        } catch (final Exception e) {
			e.printStackTrace();
		}
		createTables();
		getColumns();
	}
	
	public synchronized void update(String expression) throws SQLException {
        Statement st = conn.createStatement();
        if (st.executeUpdate(expression) == -1)
            System.err.println("db error : " + expression);
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
		// Add missing columns, then insert the record
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
	
	private void createTables() {
		try {
			update("CREATE CACHED TABLE " + TABLE_NAME +
					"(name VARCHAR, file VARCHAR, pattern VARCHAR)");
			update("CREATE INDEX tagName ON " + TABLE_NAME + "(name)");
		} catch (SQLException e) {
			// Table already exists
		}
	}
	
	private void getColumns() {
		columns = new HashSet<String>();
		try {
			ResultSet rs = query("SELECT * FROM " + TABLE_NAME +
					" WHERE name=''");
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
	
}
