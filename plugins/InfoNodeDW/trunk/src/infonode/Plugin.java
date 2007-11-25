package infonode;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class Plugin extends EditPlugin {
	public static final String NAME = "InfoNodeDW";
	public static final String OPTION_PREFIX = "options.infonode.";
	private static Vector<View> views;
	private static ViewMap viewMap;
	
	public static void doStart(org.gjt.sp.jedit.View view) {
		WindowManager wm = new WindowManager();
		wm.construct(view, DockableWindowFactory.getInstance(), view.getViewConfig());
		view.setDockableWindowManager(wm);
/*		views = new Vector<View>();
		viewMap = new ViewMap();
		DockableWindowManager dwm = view.getDockableWindowManager();
		addDockables(dwm, dwm.getLeftDockingArea().getDockables());
		addDockables(dwm, dwm.getRightDockingArea().getDockables());
		addDockables(dwm, dwm.getBottomDockingArea().getDockables());
		addDockables(dwm, dwm.getTopDockingArea().getDockables());
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		JFrame frame = new JFrame("JEdit");
		frame.add(rootWindow);
		frame.pack();
		frame.setVisible(true);
		*/
	}

	private static void addDockables(DockableWindowManager dwm, String[] windows) {
		for (int i = 0; i < windows.length; i++) {
			JComponent window = dwm.getDockable(windows[i]);
			View v = new View(dwm.getDockableTitle(windows[i]), null, window);
			viewMap.addView(views.size(), v);
			views.add(v);
		}
	}

}
