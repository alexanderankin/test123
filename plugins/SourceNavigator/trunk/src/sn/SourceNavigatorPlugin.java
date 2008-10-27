package sn;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.io.VFSManager;

public class SourceNavigatorPlugin extends EditPlugin {
	
	static public String OPTION_PREFIX = "option.source-navigator.";
	
	public void start()
	{
		for (int i = 1; ; i++) {
			String s = jEdit.getProperty("source-navigator-table." + i);
			if (s == null || s.isEmpty())
				break;
			createDockable(s);
		}
	}

	public void stop()
	{
	}

	private void createDockable(String s) {
		String [] parts = s.split(",");
		if (parts.length != 6)
			return;
		String name = parts[0];
		String label = parts[1];
		String db = parts[2];
		String columns = parts[3];
		String fileCol = parts[4];
		String lineCol = parts[5];
		String dockableName = "source-navigator-" + name + "-list";
		jEdit.setProperty(dockableName + ".label", label);
		jEdit.setProperty(dockableName + ".title", label);
		DockableWindowFactory.getInstance().registerDockableWindow(
			getPluginJAR(), dockableName,
			"new sn.DbDockable(view, \"" + db + "\", \"" + columns + "\", " +
				fileCol + ", " + lineCol + ");",
			true, true);
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
