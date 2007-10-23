package ctags;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import jedit.BufferWatcher;
import options.ActionsOptionPane;
import options.GeneralOptionPane;
import options.ProjectsOptionPane;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import ctags.Parser.TagHandler;

import projects.ProjectWatcher;
import db.TagDB;

public class CtagsInterfacePlugin extends EditPlugin {
	
	private static final String DOCKABLE = "ctags-interface-tag-list";
	static public final String OPTION = "options.CtagsInterface.";
	static public final String MESSAGE = "messages.CtagsInterface.";
	static public final String ACTION_SET = "Plugin: CtagsInterface - Actions";
	private static TagDB db;
	private static Parser parser;
	private static Runner runner;
	private static BufferWatcher watcher;
	private static ProjectWatcher pvi;
	private static ActionSet actions;
	private static TagHandler tagHandler;
	
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
		actions = new ActionSet(ACTION_SET);
		updateActions();
		jEdit.addActionSet(actions);
		tagHandler = new TagHandler() {
			public void processTag(Hashtable<String, String> info) {
				db.insertTag(info, -1);
			}
		};
	}

	public void stop()
	{
		watcher.shutdown();
		db.shutdown();
	}
	
	static public TagDB getDB() {
		return db;
	}
	
	static public void updateActions() {
		actions.removeAllActions();
		QueryAction[] queries = ActionsOptionPane.loadActions();
		for (int i = 0; i < queries.length; i++)
			actions.addAction(queries[i]);
		actions.initKeyBindings();
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
		dumpQuery("SELECT * FROM TAGS");
    }
    static void printTagsContaining(View view) {
		String s = JOptionPane.showInputDialog("Substring:");
		if (s == null || s.length() == 0)
			return;
		dumpQuery("SELECT * FROM TAGS WHERE NAME LIKE '%" + s + "%'");
    }

    static private class TagFileHandler implements TagHandler {
		private HashSet<Integer> files = new HashSet<Integer>();
		public void processTag(Hashtable<String, String> info) {
			String file = info.get(TagDB.TAGS_FILE_ID);
			int fileId = db.getSourceFileID(file);
			if (! files.contains(fileId)) {
				if (fileId < 0) {
					// Add source file to DB
					db.insertSourceFile(file);
					fileId = db.getSourceFileID(file);
				} else {
					// Delete all tags from this source file
					db.deleteTagsFromSourceFile(fileId);
				}
				files.add(fileId);
			}
			db.insertTag(info, fileId);
		}
    }
    
    // Adds a temporary tag file to the DB
    // Existing tags from source files in the tag file are removed first.  
    static private void addTempTagFile(String tagFile) {
		parser.parseTagFile(tagFile, new TagFileHandler());
    }
    
    // Action: Prompt for a temporary tag file to add to the DB
	static public void addTagFile(View view) {
		String tagFile = JOptionPane.showInputDialog("Tag file:");
		if (tagFile == null || tagFile.length() == 0)
			return;
		addTempTagFile(tagFile);
	}

	// If query results contain a single tag, jump to it, otherwise
	// present the list of tags in the Tag List dockable.
	public static void jumpToQueryResults(final View view, ResultSet rs)
	{
		Vector<Hashtable<String, String>> tags = new Vector<Hashtable<String, String>>();
		try {
			ResultSetMetaData meta;
			meta = rs.getMetaData();
			String [] cols = new String[meta.getColumnCount()];
			int [] types = new int[meta.getColumnCount()];
			for (int i = 0; i < cols.length; i++) {
				cols[i] = meta.getColumnName(i + 1);
				types[i] = meta.getColumnType(i + 1);
			}
			while (rs.next()) {
				Hashtable<String, String> values = new Hashtable<String, String>();
				for (int i = 0; i < cols.length; i++) {
					if (types[i] != Types.VARCHAR)
						continue;
					String value = rs.getString(i + 1); 
					if (value != null && value.length() > 0)
						values.put(cols[i], value);
				}
				tags.add(values);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		view.getDockableWindowManager().showDockableWindow(DOCKABLE);
		JComponent c = view.getDockableWindowManager().getDockable(DOCKABLE);
		TagList tl = (TagList) c;
		if (tags.size() == 0) {
			tl.setTags(null);
			JOptionPane.showMessageDialog(view, "No tags found");
			return;
		}
		int index = 0;
		if (tags.size() > 1) {
			tl.setTags(tags);
			return;
		}
		tl.setTags(null);
		Hashtable<String, String> info = tags.get(index);
		String file = info.get(TagDB.FILES_NAME);
		final int line = Integer.valueOf(info.get(TagDB.TAGS_LINE));
		jumpTo(view, file, line);
	}
	
	// Action: Jump to the selected tag (or tag at caret).
	public static void jumpToTag(final View view)
	{
		String tag = getDestinationTag(view);
		if (tag == null || tag.length() == 0) {
			JOptionPane.showMessageDialog(
				view, "No tag selected nor identified at caret");
			return;
		} 
		boolean projectScope = (pvi != null &&
				ProjectsOptionPane.getSearchActiveProjectOnly()); 
		ResultSet rs;
		try {
			if (projectScope) {
				String project = pvi.getActiveProject(view);
				rs = db.queryTagInProject(tag, project);
			}
			else
				rs = db.queryTag(tag);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		//System.err.println("Selected tag: " + tag);
		jumpToQueryResults(view, rs);
	}

	static public String getDestinationTag(View view) {
		String tag = view.getTextArea().getSelectedText();
		if (tag == null || tag.length() == 0)
			tag = getTagAtCaret(view);
		return tag;
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
		VFSManager.getIOThreadPool().addWorkRequest(run, inAWT);
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
				final int fileId = db.getSourceFileID(file);
				db.deleteTagsFromSourceFile(fileId);
				runner.runOnFile(file, new TagHandler() {
					public void processTag(Hashtable<String, String> info) {
						db.insertTag(info, fileId);
					}
				});
			}
		}, false);
		removeStatusMessage();
	}

	/* Source tree support */
	
	public static void tagSourceTree(final String tree) {
		setStatusMessage("Tagging source tree: " + tree);
		addWorkRequest(new Runnable() {
			public void run() {
				int originId = db.queryInteger(TagDB.ORIGINS_ID,
					"SELECT ID FROM ORIGINS WHERE TYPE='DIR' AND " +
					"NAME=" + db.quote(tree), -1);
				if (originId < 0)
					return;
				// Find file ids - required for cleanup
				Vector<Integer> fileIds = db.queryIntegerList(
					TagDB.MAP_FILE_ID,
					"SELECT FILE_ID FROM MAP WHERE ORIGIN_ID=" + originId);
				db.deleteRowsWithValue(TagDB.MAP_TABLE, TagDB.MAP_ORIGIN_ID,
						Integer.valueOf(originId));
				db.deleteRowsWithValueList(TagDB.TAGS_TABLE,
						TagDB.TAGS_FILE_ID, fileIds);
				try {
					db.query("DELETE FROM FILES WHERE NOT EXISTS (" +
							"SELECT FILE_ID FROM MAP WHERE ID=FILE_ID)");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				runner.runOnTree(tree, tagHandler);
			}
		}, false);
		removeStatusMessage();
	}
	
	/* Project support */
	
	public static ProjectWatcher getProjectWatcher() {
		return pvi;
	}
	
	private static void removeProject(String project) {
		//db.deleteRowsWithValue(TagDB.PROJECT_COL, project);
	}
	private static void removeProjectFiles(String project,
		Vector<String> files)
	{
		Hashtable<String, String> values = new Hashtable<String, String>();
		values.put(TagDB.PROJECT_COL, project);
		for (int i = 0; i < files.size(); i++) {
			values.put(TagDB.TAGS_FILE_ID, files.get(i));
			db.deleteRowsWithValues(values);
		}
	}
	private static void addProjectFiles(String project,
		Vector<String> files)
	{
		db.setProject(project);
		runner.runOnFiles(files, tagHandler);
		db.unsetProject();
	}
	public static void tagProject(final String project) {
		if (pvi == null)
			return;
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
