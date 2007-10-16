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
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class CtagsInterfacePlugin extends EditPlugin {
	
	private static Connection conn;

	public void start()
	{
        try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection("jdbc:hsqldb:file:" +
					getDBFilePath(), "sa", "");
			update("CREATE CACHED TABLE tags (name VARCHAR(256), file VARCHAR(256), pattern VARCHAR(256))");
        } catch (Exception e) {
			e.printStackTrace();
		}
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
    static public synchronized void query(String expression) throws SQLException {
    	//System.err.println("query(" + expression + ")");
        Statement st = null;
        ResultSet rs = null;
        st = conn.createStatement();
        rs = st.executeQuery(expression);
        dump(rs);
        st.close();
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
		try {
			query("SELECT * FROM tags");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		String line;
		try {
			while ((line = in.readLine()) != null)
			{
				Hashtable<String, String> info =
					new Hashtable<String, String>();
				if (line.endsWith("\n") || line.endsWith("\r"))
					line = line.substring(0, line.length() - 1);
				String fields[] = line.split("\t");
				if (fields.length < 3)
					continue;
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
				update("INSERT INTO tags(name,file) VALUES('" + info.get("name") + "','"
						+ info.get("file") + "')");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
}
