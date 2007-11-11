package ctags;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
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

import projects.ProjectWatcher;
import ctags.Parser.TagHandler;
import db.TagDB;
import dockables.TagList;

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
	private static KindIconProvider iconProvider;
	
	public void start()
	{
		db = new TagDB();
		parser = new Parser();
		runner = new Runner();
		watcher = new BufferWatcher(db);
		EditPlugin p = jEdit.getPlugin("projectviewer.ProjectPlugin",false);
		if(p == null)
			pvi = null;
		else
			pvi = new ProjectWatcher();
		actions = new ActionSet(ACTION_SET);
		updateActions();
		jEdit.addActionSet(actions);
		iconProvider = new KindIconProvider();
	}

	public void stop()
	{
		watcher.shutdown();
		db.shutdown();
	}
	
	static public TagDB getDB() {
		return db;
	}

	static public ImageIcon getIcon(Tag tag) {
		return iconProvider.getIcon(tag);
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
		dumpQuery("SELECT * FROM " + TagDB.TAGS_TABLE);
    }
    static void printTagsContaining(View view) {
		String s = JOptionPane.showInputDialog("Substring:");
		if (s == null || s.length() == 0)
			return;
		dumpQuery("SELECT * FROM " + TagDB.TAGS_TABLE +
			" WHERE " + TagDB.TAGS_NAME + " LIKE " +
			db.quote("%" + s + "%"));
    }

    static private class TagFileHandler implements TagHandler {
		private HashSet<Integer> files = new HashSet<Integer>();
		private int originId;
		public TagFileHandler(int originId) {
			this.originId = originId;
		}
		public void processTag(Tag t) {
			String file = t.getFile();
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
				db.insertSourceFileOrigin(fileId, originId);
			}
			db.insertTag(t, fileId);
		}
    }
    
    // Adds a temporary tag file to the DB
    // Existing tags from source files in the tag file are removed first.  
    static private void addTempTagFile(String tagFile) {
		parser.parseTagFile(tagFile, new TagFileHandler(TagDB.TEMP_ORIGIN_INDEX));
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
		Vector<Tag> tags = db.getResultSetTags(rs);
		jumpToTags(view, tags);
	}

	private static void jumpToTags(final View view, Vector<Tag> tags) {
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
		Tag t = tags.get(index);
		String file = t.getFile();
		final int line = t.getLine();
		jumpTo(view, file, line);
	}
	
	// Action: Add all projects to the database
	public static void tagAllProjects(View view) {
		if (pvi == null) {
			JOptionPane.showMessageDialog(view, "Project support disabled.");
			return;
		}
		Vector<String> allProjects = pvi.getProjects();
		Vector<String> dbProjects = ProjectsOptionPane.getProjects();
		for (int i = 0; i < allProjects.size(); i++) {
			String project = allProjects.get(i);
			if (! dbProjects.contains(project))
				insertOrigin(TagDB.PROJECT_ORIGIN, project);
		}
	}
	
	// Action: Search for a tag containing a substring
	public static void searchTag(final View view)
	{
		new QuickSearchTagDialog(view);
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
			(ProjectsOptionPane.getSearchActiveProjectOnly() ||
			 ProjectsOptionPane.getSearchActiveProjectFirst()));
		Vector<Tag> tags;
		try {
			String project = pvi.getActiveProject(view);
			if (projectScope && project != null) {
				ResultSet rs = db.queryTagInProject(tag, project);
				tags = db.getResultSetTags(rs);
				if (ProjectsOptionPane.getSearchActiveProjectFirst() &&
					tags.isEmpty())
				{
					rs = db.queryTag(tag);
					tags = db.getResultSetTags(rs);
				}
			} else {
				ResultSet rs = db.queryTag(tag);
				tags = db.getResultSetTags(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		//System.err.println("Selected tag: " + tag);
		jumpToTags(view, tags);
	}

	// Returns the tag to jump to: The selected tag or the one at the caret.
	static public String getDestinationTag(View view) {
		String tag = view.getTextArea().getSelectedText();
		if (tag == null || tag.length() == 0)
			tag = getTagAtCaret(view);
		return tag;
	}
	
	// Returns the tag at the caret.
	static private String getTagAtCaret(View view) {
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

	// Jumps to the specified location
	public static void jumpTo(final View view, String file, final int line) {
		Buffer buffer = jEdit.openFile(view, file);
		if (buffer == null) {
			System.err.println("Unable to open: " + file);
			return;
		}
		VFSManager.runInAWTThread(new Runnable() {
			public void run() {
				try {
					view.getTextArea().setCaretPosition(
						view.getTextArea().getLineStartOffset(line - 1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	// Jumps to the specified location
	public static void jumpToOffset(final View view, String file, final int offset) {
		Buffer buffer = jEdit.openFile(view, file);
		if (buffer == null) {
			System.err.println("Unable to open: " + file);
			return;
		}
		VFSManager.runInAWTThread(new Runnable() {
			public void run() {
				view.getTextArea().setCaretPosition(offset);
			}
		});
	}
	
	// Updates the given origins in the DB
	static public void updateOrigins(String type, Vector<String> names) {
		// Remove obsolete origins
		Vector<String> current = db.getOrigins(type);
		for (int i = 0; i < current.size(); i++) {
			String name = current.get(i);
			if (! names.contains(name))
				deleteOrigin(type, name);
		}
		// Add new origins
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			if (! current.contains(name))
				insertOrigin(type, name);
		}
	}
	
	// Refreshes the given origin in the DB
	static public void refreshOrigin(String type, String name) {
		try {
			db.deleteOriginAssociatedData(type, name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tagOrigin(type, name);
	}
	
	// Deletes an origin with all associated data from the DB
	public static void deleteOrigin(String type, String name) {
		try {
			db.deleteOrigin(type, name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (pvi != null && type.equals(TagDB.PROJECT_ORIGIN))
			pvi.updateWatchers();
	}
	// Inserts a new origin to the DB, runs Ctags on it and adds the tags
	// to the DB.
	public static void insertOrigin(String type, String name) {
		try {
			db.insertOrigin(type, name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tagOrigin(type, name);
		if (pvi != null && type.equals(TagDB.PROJECT_ORIGIN))
			pvi.updateWatchers();
	}
	// Runs Ctags on the specified origin and adds the tags to the DB.
	private static void tagOrigin(String type, String name) {
		int originId = db.getOriginID(type, name);
		if (originId < 0) {
			System.err.println("Cannot find newly inserted origin " + name + " in DB.");
			return;
		}
		TagFileHandler handler = new TagFileHandler(originId);
		if (type.equals(TagDB.PROJECT_ORIGIN))
			tagProject(name, handler);
		else if (type.equals(TagDB.DIR_ORIGIN))
			tagSourceTree(name, handler);
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
				String tagFile = runner.runOnFile(file);
				TagHandler handler = new TagHandler() {
					public void processTag(Tag t) {
						db.insertTag(t, fileId);
					}
				};
				parser.parseTagFile(tagFile, handler);
			}
		}, false);
		removeStatusMessage();
	}

	/* Source tree support */
	
	// Runs Ctags on a source tree and add the tags and associated data to the DB
	public static void tagSourceTree(final String tree, final TagHandler handler) {
		setStatusMessage("Tagging source tree: " + tree);
		addWorkRequest(new Runnable() {
			public void run() {
				String tagFile = runner.runOnTree(tree);
				parser.parseTagFile(tagFile, handler);
			}
		}, false);
		removeStatusMessage();
	}
	
	/* Project support */
	
	public static ProjectWatcher getProjectWatcher() {
		return pvi;
	}
	
	private static void removeProjectFiles(int projectId,
		Vector<String> files)
	{
		StringBuffer filesStr = new StringBuffer();
		for (int i = 0; i < files.size(); i++) {
			if (i > 0)
				filesStr.append(",");
			filesStr.append(db.quote(files.get(i)));
		}
		String query = "DELETE FROM " + TagDB.FILES_TABLE + " WHERE " +
			TagDB.FILES_NAME + " IN (" + filesStr.toString() + ")" +
			" AND NOT EXISTS " +
			"(SELECT " + TagDB.MAP_FILE_ID + " FROM " + TagDB.MAP_TABLE +
			" WHERE " + TagDB.MAP_ORIGIN_ID + "<>" + projectId +
			" AND " + TagDB.MAP_FILE_ID + "=" + TagDB.FILES_ID + ")";
		try {
			db.query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Runs Ctags on a list of files and add the tags and associated data to the DB
	private static void tagFiles(Vector<String> files, TagHandler handler)
	{
		String tagFile = runner.runOnFiles(files);
		parser.parseTagFile(tagFile, handler);
	}
	
	// Runs Ctags on a project and inserts the tags and associated data to the DB
	public static void tagProject(final String project, final TagHandler handler) {
		if (pvi == null)
			return;
		setStatusMessage("Tagging project: " + project);
		addWorkRequest(new Runnable() {
			public void run() {
				Vector<String> files = pvi.getFiles(project);
				tagFiles(files, handler);
			}
		}, false);
		removeStatusMessage();
	}
	public static void updateProject(String project, Vector<String> added,
		Vector<String> removed)
	{
		int projectId = db.getOriginID(TagDB.PROJECT_ORIGIN, project);
		if (projectId < 0)
			return;
		setStatusMessage("Updating project: " + project);
		if (removed != null)
			removeProjectFiles(projectId, removed);
		if (added != null) {
			TagHandler handler = new TagFileHandler(projectId);
			tagFiles(added, handler);
		}
		removeStatusMessage();
	}
	
	/*
	 * Interface for other plugins
	 */
	
	public static Vector<Tag> queryTag(String tag)
	{
		ResultSet rs;
		try {
			rs = db.queryTag(tag);
			return db.getResultSetTags(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Vector<Tag>();
	}
	public static Vector<Tag> query(String query)
	{
		ResultSet rs;
		try {
			rs = db.query(query);
			return db.getResultSetTags(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Vector<Tag>();
	}
}
