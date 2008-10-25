package sn;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;

public class SourceNavigatorPlugin extends EditPlugin {
	
	static public String OPTION_PREFIX = "option.source-navigator.";
	
	public void start()
	{
	}

	public void stop()
	{
	}

	static public String getOption(String name) {
		return jEdit.getProperty(OPTION_PREFIX + name);
	}
	static public void setOption(String name, String value) {
		jEdit.setProperty(OPTION_PREFIX + name, value);
	}
	static public void jumpTo(final View view, String file, final int line) {
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

}
