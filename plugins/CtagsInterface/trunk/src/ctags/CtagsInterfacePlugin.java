package ctags;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import jedit.BufferWatcher;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;

import projects.ProjectViewerInterface;

import db.TagDB;

public class CtagsInterfacePlugin extends EditPlugin {
	
	static public final String OPTION = "options.CtagsInterface.";
	static public final String MESSAGE = "messages.CtagsInterface.";
	private static TagDB db;
	private static Parser parser;
	private static Runner runner;
	private static BufferWatcher watcher;
	private static ProjectViewerInterface pvi;
	
	public void start()
	{
		db = new TagDB();
		parser = new Parser();
		runner = new Runner(db);
		watcher = new BufferWatcher(db);
		EditPlugin p = jEdit.getPlugin("projectviewer.ProjectPlugin",false);
		if(p == null)
			pvi = null;
		else {
			try {
				pvi = (ProjectViewerInterface)
					Class.forName("projects.ProjectWatcher").newInstance();;
			} catch (Exception e) {
				// ok, no project support
				e.printStackTrace();
				pvi = null;
			}
		}
	}

	public void stop()
	{
		watcher.shutdown();
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
				" WHERE " + TagDB.NAME_COL + " LIKE '%" + s + "%'");
    }
	static void addTagFile(View view) {
		String tagFile = JOptionPane.showInputDialog("Tag file:");
		if (tagFile == null || tagFile.length() == 0)
			return;
		parser.parseTagFile(tagFile, db);
	}

	public static void jumpToTag(final View view)
	{
		String tag = view.getTextArea().getSelectedText();
		System.err.println("Selected tag: " + tag);
		Vector<String> files = new Vector<String>();
		Vector<String> lines = new Vector<String>();
		try {
			ResultSet rs = db.query("SELECT " + TagDB.FILE_COL + ", LINE FROM " +
				TagDB.TABLE_NAME + " WHERE " + TagDB.NAME_COL + "=" + db.getValueString(tag));
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
		jumpTo(view, file, line);
	}

	private static void jumpTo(final View view, String file, final int line) {
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
	
	private static void setStatusMessage(String msg) {
		//jEdit.getActiveView().getStatus().setMessage(msg);
	}
	private static void removeStatusMessage() {
		//jEdit.getActiveView().getStatus().setMessage("");
	}
	
	/* Source file support */
	
	public static void tagSourceFile(String file) {
		setStatusMessage("Tagging file: " + file);
		db.deleteRowsWithValue(TagDB.FILE_COL, file);
		runner.runOnFile(file);
		removeStatusMessage();
	}

	/* Source tree support */
	
	public static void tagSourceTree(String tree) {
		setStatusMessage("Tagging source tree: " + tree);
		db.deleteRowsWithValuePrefix(TagDB.FILE_COL, tree);
		runner.runOnTree(tree);
		removeStatusMessage();
	}
	
	/* Project support */
	
	public static ProjectViewerInterface getProjectViewerInterface() {
		return pvi;
	}
	
	private static void removeProject(String project) {
		db.deleteRowsWithValue(TagDB.PROJECT_COL, project);
	}
	private static void removeProjectFiles(String project,
		Vector<String> files)
	{
		Hashtable<String, String> values = new Hashtable<String, String>();
		values.put(TagDB.PROJECT_COL, project);
		for (int i = 0; i < files.size(); i++) {
			values.put(TagDB.FILE_COL, files.get(i));
			db.deleteRowsWithValues(values);
		}
	}
	private static void addProjectFiles(String project,
		Vector<String> files)
	{
		db.setProject(project);
		runner.runOnFiles(files);
		db.unsetProject();
	}
	public static void tagProject(String project) {
		setStatusMessage("Tagging project: " + project);
		removeProject(project);
		Vector<String> files = pvi.getFiles(project);
		addProjectFiles(project, files);
		removeStatusMessage();
	}
	public static void updateProject(String project,
		Vector<String> added, Vector<String> removed)
	{
		setStatusMessage("Updating project: " + project);
		if (removed != null)
			removeProjectFiles(project, removed);
		if (added != null)
			addProjectFiles(project, added);
		removeStatusMessage();
	}
}
