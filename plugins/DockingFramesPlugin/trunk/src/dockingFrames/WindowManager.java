package dockingFrames;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;

@SuppressWarnings("serial")
public class WindowManager extends DockableWindowManager {

	private SplitDockStation center;
	private StackDockStation east, west, north, south;
	
	public WindowManager(View view, DockableWindowFactory instance,
			ViewConfig config) {
		super(view, instance, config);
		setLayout(new BorderLayout());
		DockController controller = new DockController();
		controller.setTheme(new EclipseTheme());
        center = new SplitDockStation();
        add(center.getComponent(), BorderLayout.CENTER);
        north = new StackDockStation();
        south = new StackDockStation();
        east = new StackDockStation();
        west = new StackDockStation();
        controller.add(center);
        controller.add(north);
        controller.add(south);
        controller.add(east);
        controller.add(west);
        north.setTitleText("North");
        south.setTitleText("South");
        east.setTitleText("East");
        west.setTitleText("West");
	}

	@Override
	public void applyDockingLayout(DockingLayout docking) {
		super.applyDockingLayout(docking);
		dropDockingAreas();
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
        center.drop(new DefaultDockable(panel, "main"));
	}

	private void dropDockingAreas() {
		SplitDockPathProperty p = new SplitDockPathProperty();
        p.add(SplitDockPathProperty.Location.TOP, 0.2);
        if (north.getDockableCount() > 0)
        	center.drop(north, p);
        p = new SplitDockPathProperty();
        p.add(SplitDockPathProperty.Location.BOTTOM, 0.2);
        if (south.getDockableCount() > 0)
        	center.drop(south, p);
        p = new SplitDockPathProperty();
        p.add(SplitDockPathProperty.Location.LEFT, 0.2);
        if (west.getDockableCount() > 0)
        	center.drop(west, p);
        p = new SplitDockPathProperty();
        p.add(SplitDockPathProperty.Location.RIGHT, 0.2);
        if (east.getDockableCount() > 0)
        	center.drop(east, p);
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
		if (position.equals(DockableWindowManager.TOP))
			s = north;
		else if (position.equals(DockableWindowManager.BOTTOM))
			s = south;
		else if (position.equals(DockableWindowManager.RIGHT))
			s = east;
		else
			s = west;
		s.drop(d);
	}

}
