package sn;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowFactory;

import utils.EditorInterface;


public class SourceNavigatorPlugin extends EditPlugin {
	
	static private final String SOURCE_NAVIGATOR_TABLES_MENU = "source-navigator-tables";
	static private final String SOURCE_NAVIGATOR_JUMP_MENU = "source-navigator-jump";
	static private final String SOURCE_NAVIGATOR_COMPLETE_MENU = "source-navigator-complete";
	static public String OPTION_PREFIX = "option.source-navigator.";
	static public String MESSAGE_PREFIX = "messages.source-navigator.";
	static public final String COMPLETION_ACTION_SET = "Plugin: Source Navigator - Completion";
	static public final String JUMPING_ACTION_SET = "Plugin: Source Navigator - Jumping";
	static private Vector<DbDescriptor> dbDescriptors;
	static private EditorInterface editorInterface;
	
	public void start()
	{
		dbDescriptors = new Vector<DbDescriptor>();
		for (int i = 1; ; i++) {
			String base = "source-navigator-table." + i + ".";
			String s = jEdit.getProperty(base + "name");
			if (s == null || s.isEmpty())
				break;
			DbDescriptor desc = new DbDescriptor(base);
			dbDescriptors.add(desc);
		}
		Collections.sort(dbDescriptors, new Comparator<DbDescriptor>() {
			public int compare(DbDescriptor d1, DbDescriptor d2) {
				return d1.label.compareTo(d2.label);
			}
		});
		createDockables();
		createActions();
	}

	public void stop()
	{
	}

	static public EditorInterface getEditorInterface() {
		if (editorInterface == null)
			editorInterface = new EditorInterface();
		return editorInterface;
	}
	
	private void createDockables() {
		jEdit.resetProperty(SOURCE_NAVIGATOR_TABLES_MENU);
		StringBuffer menu = new StringBuffer();
		for (DbDescriptor desc: dbDescriptors) {
			String dockableName = createDockable(desc);
			menu.append(dockableName + "\n\t");
		}
		jEdit.setProperty(SOURCE_NAVIGATOR_TABLES_MENU, menu.toString());
	}
	
	private void createActions() {
		StringBuffer menu = new StringBuffer();
		jEdit.resetProperty(SOURCE_NAVIGATOR_COMPLETE_MENU);
		ActionSet actions = new ActionSet(COMPLETION_ACTION_SET);
		CompleteAction completeAnyAction = new CompleteAction();
		actions.addAction(completeAnyAction);
		menu.append(completeAnyAction.getName() + "\n\t");
		for (DbDescriptor desc: dbDescriptors) {
			CompleteAction action = new CompleteAction(desc); 
			actions.addAction(action);
			menu.append(action.getName() + "\n\t");
		}
		jEdit.setProperty(SOURCE_NAVIGATOR_COMPLETE_MENU, menu.toString());
		actions.initKeyBindings();
		jEdit.addActionSet(actions);
		menu = new StringBuffer();
		jEdit.resetProperty(SOURCE_NAVIGATOR_JUMP_MENU);
		actions = new ActionSet(JUMPING_ACTION_SET);
		JumpAction jumpAnyAction = new JumpAction();
		actions.addAction(jumpAnyAction);
		menu.append(jumpAnyAction.getName() + "\n\t");
		for (DbDescriptor desc: dbDescriptors) {
			JumpAction action = new JumpAction(desc);
			actions.addAction(action);
			menu.append(action.getName() + "\n\t");
		}
		jEdit.setProperty(SOURCE_NAVIGATOR_JUMP_MENU, menu.toString());
		actions.initKeyBindings();
		jEdit.addActionSet(actions);
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
	
	private String createDockable(DbDescriptor desc) {
		String dockableName = getDockableName(desc);
		jEdit.setProperty(dockableName + ".label", desc.label);
		jEdit.setProperty(dockableName + ".title", desc.label);
		DockableWindowFactory.getInstance().registerDockableWindow(
			getPluginJAR(), dockableName,
			"new sn.DbDockable(view, \"" + desc.db + "\");",
			true, true);
		return dockableName;
	}

	static public String getDockableName(DbDescriptor desc) {
		return "source-navigator-" + desc.name + "-list";
	}
	
	static public String getOption(String name) {
		return jEdit.getProperty(OPTION_PREFIX + name);
	}
	static public void setOption(String name, String value) {
		jEdit.setProperty(OPTION_PREFIX + name, value);
	}
	static public String getMessage(String name) {
		return jEdit.getProperty(MESSAGE_PREFIX + name);
	}
}
