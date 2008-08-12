package dockingFrames;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.split.SplitDockProperty;

@SuppressWarnings("serial")
public class WindowManager extends DockableWindowManager {

	private SplitDockStation station;
	private StackDockStation east, west, north, south;

	public WindowManager(View view, DockableWindowFactory instance,
			ViewConfig config) {
		super(view, instance, config);
		setLayout(new BorderLayout());
		DockController controller = new DockController();
        station = new SplitDockStation();
        north = new StackDockStation();
        south = new StackDockStation();
        east = new StackDockStation();
        west = new StackDockStation();
        add(station.getComponent(), BorderLayout.CENTER);
        controller.add(station);
	}

	@Override
	public void closeCurrentArea() {
		// TODO Auto-generated method stub

	}

	@Override
	public JComponent floatDockableWindow(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingArea getBottomDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingLayout getDockingLayout(ViewConfig config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingArea getLeftDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingArea getRightDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingArea getTopDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hideDockableWindow(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDockableWindowDocked(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDockableWindowVisible(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMainPanel(JPanel panel) {
		station.drop(new DefaultDockable(panel, "main"));
		//add(panel, BorderLayout.CENTER);
	}

	@Override
	public void showDockableWindow(String name) {
		JComponent window = getDockable(name);
		String position = getDockablePosition(name); 
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return;
		String title = getDockableTitle(name);
		Dockable d = new DefaultDockable(window, title);
		StackDockStation s;
		SplitDockProperty p;
		if (position.equals(DockableWindowManager.TOP)) {
			s = north;
			p = SplitDockProperty.NORTH;
		}
		else if (position.equals(DockableWindowManager.BOTTOM)) {
			s = south;
			p = SplitDockProperty.SOUTH;
		}
		else if (position.equals(DockableWindowManager.RIGHT)) {
			s = east;
			p = SplitDockProperty.EAST;
		}
		else {
			s = west;
			p = SplitDockProperty.WEST;
		}
		s.drop(d);
		if (s.getController() == null) {
			station.drop(s, p);
		}
	}

}
