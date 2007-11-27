package infonode;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.gui.DockableWindowFactory;

public class Plugin extends EditPlugin {
	public static final String NAME = "InfoNodeDW";
	public static final String OPTION_PREFIX = "options.infonode.";
	
	public static void doStart(org.gjt.sp.jedit.View view) {
		WindowManager wm = new WindowManager();
		wm.construct(view, DockableWindowFactory.getInstance(), view.getViewConfig());
		view.setDockableWindowManager(wm);
	}

}
