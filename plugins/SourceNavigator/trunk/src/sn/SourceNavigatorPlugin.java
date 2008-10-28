package sn;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.io.VFSManager;

public class SourceNavigatorPlugin extends EditPlugin {
	
	static private final String SOURCE_NAVIGATOR_TABLES_MENU = "source-navigator-tables";
	static public String OPTION_PREFIX = "option.source-navigator.";
	static private Vector<DbDescriptor> dbDescriptors;
	
	public void start()
	{
		jEdit.resetProperty(SOURCE_NAVIGATOR_TABLES_MENU);
		dbDescriptors = new Vector<DbDescriptor>();
		for (int i = 1; ; i++) {
			String s = jEdit.getProperty("source-navigator-table." + i);
			if (s == null || s.isEmpty())
				break;
			DbDescriptor desc = createDockable(s);
			if (desc != null)
				dbDescriptors.add(desc);
		}
		Collections.sort(dbDescriptors, new Comparator<DbDescriptor>() {
			public int compare(DbDescriptor d1, DbDescriptor d2) {
				return d1.label.compareTo(d2.label);
			}
		});
	}

	public void stop()
	{
	}

	public static Vector<DbDescriptor> getDbDescriptors() {
		return dbDescriptors;
	}
	
	public static class DbDescriptor {
		public String name, label, db, columns;
		public int fileCol, lineCol;
		public DbDescriptor() {
			fileCol = lineCol = -1;
		}
		public String toString() {
			return label;
		}
	}
	
	private DbDescriptor createDockable(String s) {
		String [] parts = s.split(",");
		if (parts.length != 6)
			return null;
		DbDescriptor desc = new DbDescriptor();
		desc.name = parts[0];
		desc.label = parts[1];
		desc.db = parts[2];
		desc.columns = parts[3];
		try {
			desc.fileCol = Integer.valueOf(parts[4]);
			desc.lineCol = Integer.valueOf(parts[5]);
		} catch (Exception e) {
		}
		String dockableName = "source-navigator-" + desc.name + "-list";
		jEdit.setProperty(dockableName + ".label", desc.label);
		jEdit.setProperty(dockableName + ".title", desc.label);
		DockableWindowFactory.getInstance().registerDockableWindow(
			getPluginJAR(), dockableName,
			"new sn.DbDockable(view, \"" + desc.db + "\", \"" +
			desc.columns + "\", " + desc.fileCol + ", " + desc.lineCol + ");",
			true, true);
		String menu = jEdit.getProperty(SOURCE_NAVIGATOR_TABLES_MENU);
		if (menu == null)
			menu = dockableName;
		else
			menu = menu + "\n\t" + dockableName;
		jEdit.setProperty(SOURCE_NAVIGATOR_TABLES_MENU, menu);
		return desc;
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
