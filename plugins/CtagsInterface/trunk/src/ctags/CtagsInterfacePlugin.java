package ctags;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class CtagsInterfacePlugin extends EditPlugin {
	
	private static Connection conn;
	private static Set<String> columns;
	private static final String TABLE_NAME = "tags";
	
	public void start()
	{
        try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection("jdbc:hsqldb:file:" +
					getDBFilePath(), "sa", "");
        } catch (Exception e) {
			e.printStackTrace();
		}
		try {
			update("CREATE CACHED TABLE " + TABLE_NAME +
					"(name VARCHAR, file VARCHAR, pattern VARCHAR)");
		} catch (SQLException e) {
			// Table already exists
		}
		getColumns();
	}

	public void stop()
	{
        Statement st;
		try {
			st = conn.createStatement();
	        st.execute("SHUTDOWN");
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
	static public String getDBFilePath() {
		return jEdit.getSettingsDirectory() + "/CtagsInterface/tagdb";
	}
	
    static public synchronized void update(String expression) throws SQLException {
    	//System.err.println("update(" + expression + ")");
        Statement st = null;
        st = conn.createStatement();
        int i = st.executeUpdate(expression);
        if (i == -1)
            System.out.println("db error : " + expression);
        st.close();
    }
    static public void dumpQuery(String expression) {
    	try {
			dump(query(expression));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    static public synchronized ResultSet query(String expression) throws SQLException {
    	//System.err.println("query(" + expression + ")");
        Statement st = null;
        ResultSet rs = null;
        st = conn.createStatement();
        rs = st.executeQuery(expression);
        st.close();
        return rs;
    }
    public static void dump(ResultSet rs) throws SQLException {
        ResultSetMetaData meta   = rs.getMetaData();
        int               colmax = meta.getColumnCount();
        int               i;
        Object            o = null;
        for (; rs.next(); ) {
        	StringBuffer buf = new StringBuffer();
            for (i = 0; i < colmax; ++i) {
                o = rs.getObject(i + 1);
                if (o != null)
                buf.append(o.toString() + " ");
            }
            System.err.println(buf.toString());
        }
    }
    static void printTags() {
		dumpQuery("SELECT * FROM " + TABLE_NAME);
    }
    static void printTagsContaining(View view) {
		String s = JOptionPane.showInputDialog("Substring:");
		if (s == null || s.length() == 0)
			return;
		dumpQuery("SELECT * FROM " + TABLE_NAME +
				" WHERE name LIKE '%" + s + "%'");
    }
	static void addTagFile(View view) {
		String tagFile = JOptionPane.showInputDialog("Tag file:");
		if (tagFile == null || tagFile.length() == 0)
			return;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(tagFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			String line;
			while ((line = in.readLine()) != null)
			{
				Hashtable<String, String> info = parse(line);
				if (info == null)
					continue;
				insertTag(info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void insertTag(Hashtable<String, String> info) {
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

	private static Hashtable<String, String> parse(String line) {
		Hashtable<String, String> info =
			new Hashtable<String, String>();
		if (line.endsWith("\n") || line.endsWith("\r"))
			line = line.substring(0, line.length() - 1);
		String fields[] = line.split("\t");
		if (fields.length < 3)
			return null;
		info.put("name", fields[0]);
		info.put("file", fields[1]);
		info.put("pattern", fields[2]);
		// extensions
		for (int i = 3; i < fields.length; i++)
		{
			String pair[] = fields[i].split(":", 2);
			if (pair.length != 2)
				continue;
			info.put(pair[0], pair[1]);
		}
		return info;
	}

	private static String getValueString(String string) {
		return "'" + string.replaceAll("'", "''") + "'";
	}
}
