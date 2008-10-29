package sn;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.io.VFSManager;

public class SourceNavigatorPlugin extends EditPlugin {
	
	static private final String SOURCE_NAVIGATOR_TABLES_MENU = "source-navigator-tables";
	static public String OPTION_PREFIX = "option.source-navigator.";
	static public final String COMPLETION_ACTION_SET = "Plugin: Source Navigator - Completion";
	static public final String JUMPING_ACTION_SET = "Plugin: Source Navigator - Jumping";
	static private Vector<DbDescriptor> dbDescriptors;

	public void start()
	{
		jEdit.resetProperty(SOURCE_NAVIGATOR_TABLES_MENU);
		dbDescriptors = new Vector<DbDescriptor>();
		for (int i = 1; ; i++) {
			String base = "source-navigator-table." + i + ".";
			String s = jEdit.getProperty(base + "name");
			if (s == null || s.isEmpty())
				break;
			DbDescriptor desc = new DbDescriptor(base);
			dbDescriptors.add(desc);
			createDockable(desc);
		}
		Collections.sort(dbDescriptors, new Comparator<DbDescriptor>() {
			public int compare(DbDescriptor d1, DbDescriptor d2) {
				return d1.label.compareTo(d2.label);
			}
		});
		createActions();
	}

	static private class CompleteAction extends EditAction {
		private static final String COMPLETE_ACTION_PREFIX = "source-navigator-complete-";
		public CompleteAction(DbDescriptor desc) {
			super(COMPLETE_ACTION_PREFIX + desc.name);
			jEdit.setTemporaryProperty(
				COMPLETE_ACTION_PREFIX + desc.name + ".label", "Complete " + desc.label);
		}
		@Override
		public void invoke(View view) {
		}
	}
	static private class JumpAction extends EditAction {
		private static final String JUMP_ACTION_PREFIX = "source-navigator-jump-";
		public JumpAction(DbDescriptor desc) {
			super(JUMP_ACTION_PREFIX + desc.name);
			jEdit.setTemporaryProperty(
					JUMP_ACTION_PREFIX + desc.name + ".label", "Jump to " + desc.label);
		}
		@Override
		public void invoke(View view) {
		}
	}
	
	private void createActions() {
		ActionSet actions = new ActionSet(COMPLETION_ACTION_SET);
		for (DbDescriptor desc: dbDescriptors)
			actions.addAction(new CompleteAction(desc));
		actions.initKeyBindings();
		jEdit.addActionSet(actions);
		actions = new ActionSet(JUMPING_ACTION_SET);
		for (DbDescriptor desc: dbDescriptors)
			actions.addAction(new JumpAction(desc));
		actions.initKeyBindings();
		jEdit.addActionSet(actions);
	}

	public void stop()
	{
	}

	public static Vector<DbDescriptor> getDbDescriptors() {
		return dbDescriptors;
	}
	public static DbDescriptor getDbDescriptor(String db) {
		for (DbDescriptor desc: dbDescriptors)
			if (desc.db.equals(db))
				return desc;
		return null;
	}
	
	public static class DbDescriptor {
		static private final String SN_SEP = "\\?";
		public String name, label, db, columns;
		public String [] columnNames;
		public DbDescriptor(String base) {
			name = jEdit.getProperty(base + "name");
			label = jEdit.getProperty(base + "label");
			db = jEdit.getProperty(base + "db");
			columns = jEdit.getProperty(base + "columns");
			columnNames = columns.split(SN_SEP);
		}
		public int getColumnCount() {
			return columnNames.length;
		}
		public String getColumn(int index) {
			if (index < 0 || index >= columnNames.length)
				return null;
			return columnNames[index];
		}
		public String toString() {
			return label;
		}
	}
	
	private void createDockable(DbDescriptor desc) {
		String dockableName = "source-navigator-" + desc.name + "-list";
		jEdit.setProperty(dockableName + ".label", desc.label);
		jEdit.setProperty(dockableName + ".title", desc.label);
		DockableWindowFactory.getInstance().registerDockableWindow(
			getPluginJAR(), dockableName,
			"new sn.DbDockable(view, \"" + desc.db + "\", \"" +
				desc.columns + ");",
			true, true);
		String menu = jEdit.getProperty(SOURCE_NAVIGATOR_TABLES_MENU);
		if (menu == null)
			menu = dockableName;
		else
			menu = menu + "\n\t" + dockableName;
		jEdit.setProperty(SOURCE_NAVIGATOR_TABLES_MENU, menu);
	}
	static public String getOption(String name) {
		return jEdit.getProperty(OPTION_PREFIX + name);
	}
	static public void setOption(String name, String value) {
		jEdit.setProperty(OPTION_PREFIX + name, value);
	}
	static public void jumpTo(final View view, String file, final int line) {
		jumpTo(view, file, line, 0);
	}
	static public void jumpTo(final View view, String file, final int line,
			final int offset)
	{
		Buffer buffer = jEdit.openFile(view, file);
		if (buffer == null) {
			System.err.println("Unable to open: " + file);
			return;
		}
		if (line <= 0)
			return;
		VFSManager.runInAWTThread(new Runnable() {
			public void run() {
				try {
					view.getTextArea().setCaretPosition(
						view.getTextArea().getLineStartOffset(line - 1) + offset);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
