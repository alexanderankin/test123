package ctags;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import jedit.BufferWatcher;
import options.GeneralOptionPane;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.WorkThreadPool;

import projects.ProjectWatcher;
import db.TagDB;

public class CtagsInterfacePlugin extends EditPlugin {
	
	private static final String DOCKABLE = "ctags-interface-tag-list";
	static public final String OPTION = "options.CtagsInterface.";
	static public final String MESSAGE = "messages.CtagsInterface.";
	private static TagDB db;
	private static Parser parser;
	private static Runner runner;
	private static BufferWatcher watcher;
	private static ProjectWatcher pvi;
	private static WorkThreadPool worker;
	
	public void start()
	{
		db = new TagDB();
		parser = new Parser();
		runner = new Runner(db);
		watcher = new BufferWatcher(db);
		EditPlugin p = jEdit.getPlugin("projectviewer.ProjectPlugin",false);
		if(p == null)
			pvi = null;
		else
			pvi = new ProjectWatcher();
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
		if (tag == null || tag.length() == 0) {
			tag = getTagAtCaret(view);
			if (tag == null || tag.length() == 0) {
				JOptionPane.showMessageDialog(view, "No tag selected nor identified at caret");
				return;
			}
		} 
		System.err.println("Selected tag: " + tag);
		Vector<Hashtable<String, String>> tags = new Vector<Hashtable<String, String>>();
		try {
			ResultSet rs = db.query("SELECT * FROM " + TagDB.TABLE_NAME + " WHERE " +
				TagDB.NAME_COL + "=" + db.getValueString(tag));
			ResultSetMetaData meta;
			meta = rs.getMetaData();
			String [] cols = new String[meta.getColumnCount()];
			for (int i = 0; i < cols.length; i++)
				cols[i] = meta.getColumnName(i + 1);
			while (rs.next()) {
				Hashtable<String, String> values = new Hashtable<String, String>();
				for (int i = 0; i < cols.length; i++) {
					String value = rs.getString(i + 1); 
					if (value != null && value.length() > 0)
						values.put(cols[i], value);
				}
				tags.add(values);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (tags.size() == 0) {
			JOptionPane.showMessageDialog(view, "No tags found");
			return;
		}
		int index = 0;
		if (tags.size() > 1) {
			view.getDockableWindowManager().showDockableWindow(DOCKABLE);
			JComponent c = view.getDockableWindowManager().getDockable(DOCKABLE);
			((TagList)c).setTags(tags);
			return;
		}
		Hashtable<String, String> info = tags.get(index);
		String file = info.get(TagDB.FILE_COL);
		final int line = Integer.valueOf(info.get("LINE"));
		jumpTo(view, file, line);
	}

	private static String getTagAtCaret(View view) {
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine();
		int index = ta.getCaretPosition() - ta.getLineStartOffset(line);
		String text = ta.getLineText(line);
		Pattern pat = Pattern.compile(GeneralOptionPane.getPattern());
		Matcher m = pat.matcher(text);
		int end = -1;
		int start = -1;
		String selected = "";
		while (end <= index) {
			if (! m.find())
				return null;
			end = m.end();
			start = m.start();
			selected = m.group();
		}
		if (start > index || selected.length() == 0)
			return null;
		return selected;
	}

	public static void jumpTo(final View view, String file, final int line) {
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
	
	private static void addWorkRequest(Runnable run, boolean inAWT) {
		if (! GeneralOptionPane.getUpdateInBackground()) {
			run.run();
			return;
		}
		if (worker == null) {
			worker = new WorkThreadPool("CtagsInterface", 1);
			worker.start();
		}
		worker.addWorkRequest(run, inAWT);
	}

	private static void setStatusMessage(String msg) {
		jEdit.getActiveView().getStatus().setMessage(msg);
	}
	private static void removeStatusMessage() {
		jEdit.getActiveView().getStatus().setMessage("");
	}
	
	/* Source file support */
	
	public static void tagSourceFile(final String file) {
		setStatusMessage("Tagging file: " + file);
		addWorkRequest(new Runnable() {
			public void run() {
				db.deleteRowsWithValue(TagDB.FILE_COL, file);
				runner.runOnFile(file);
			}
		}, false);
		removeStatusMessage();
		
	}

	/* Source tree support */
	
	public static void tagSourceTree(final String tree) {
		setStatusMessage("Tagging source tree: " + tree);
		addWorkRequest(new Runnable() {
			public void run() {
				db.deleteRowsWithValuePrefix(TagDB.FILE_COL, tree);
				runner.runOnTree(tree);
			}
		}, false);
		removeStatusMessage();
	}
	
	/* Project support */
	
	public static ProjectWatcher getProjectWatcher() {
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
	public static void tagProject(final String project) {
		setStatusMessage("Tagging project: " + project);
		addWorkRequest(new Runnable() {
			public void run() {
				removeProject(project);
				Vector<String> files = pvi.getFiles(project);
				addProjectFiles(project, files);
			}
		}, false);
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
