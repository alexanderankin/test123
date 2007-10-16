package ctags;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;

import db.TagDB;

public class CtagsInterfacePlugin extends EditPlugin {
	
	private static TagDB db;
	
	public void start()
	{
		db = new TagDB();
	}

	public void stop()
	{
		db.shutdown();
	}
	
    static public void dumpQuery(String expression) {
    	try {
			dump(db.query(expression));
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		dumpQuery("SELECT * FROM " + TagDB.TABLE_NAME);
    }
    static void printTagsContaining(View view) {
		String s = JOptionPane.showInputDialog("Substring:");
		if (s == null || s.length() == 0)
			return;
		dumpQuery("SELECT * FROM " + TagDB.TABLE_NAME +
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
				db.insertTag(info);
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

	public static void jumpToTag(final View view)
	{
		String tag = view.getTextArea().getSelectedText();
		System.err.println("Selected tag: " + tag);
		Vector<String> files = new Vector<String>();
		Vector<String> lines = new Vector<String>();
		try {
			ResultSet rs = db.query("SELECT FILE, LINE FROM " + TagDB.TABLE_NAME +
					" WHERE NAME=" + db.getValueString(tag));
			while (rs.next()) {
				files.add(rs.getString(1));
				lines.add(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (files.size() == 0) {
			JOptionPane.showMessageDialog(view, "No records found");
			return;
		}
		int index = 0;
		if (files.size() > 1) {
			String [] positions = new String[files.size()];
			for (int i = 0; i < files.size(); i++)
				positions[i] = files.get(i) + ":" + lines.get(i);
			String s = (String) JOptionPane.showInputDialog(view, "Select position:",
				"Tag collision", JOptionPane.QUESTION_MESSAGE, null,
				positions, positions[0]);
			index = Arrays.asList(positions).indexOf(s);
		}
		String file = files.get(index);
		final int line = Integer.valueOf(lines.get(index));
		Buffer buffer = jEdit.openFile(view, file);
		if (buffer == null) {
			System.err.println("Unable to open: " + file);
			return;
		}
		VFSManager.runInAWTThread(new Runnable() {
			public void run() {
				view.getTextArea().setCaretPosition(
					view.getTextArea().getLineStartOffset(line - 1));
			}
		});
	}
}
