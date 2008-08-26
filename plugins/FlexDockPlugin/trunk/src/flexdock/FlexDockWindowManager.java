package flexdock;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.gjt.sp.jedit.PerspectiveManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;

@SuppressWarnings("serial")
public class FlexDockWindowManager extends DockableWindowManager {

	private DefaultDockingPort leftPort, rightPort, topPort, bottomPort;
	
	public FlexDockWindowManager(View view, DockableWindowFactory instance,
			ViewConfig config)
	{
		super(view, instance, config);
		setLayout(new BorderLayout());
		leftPort = new DefaultDockingPort();
		add(leftPort, BorderLayout.WEST);
		rightPort = new DefaultDockingPort();
		add(rightPort, BorderLayout.EAST);
		topPort = new DefaultDockingPort();
		add(topPort, BorderLayout.NORTH);
		bottomPort = new DefaultDockingPort();
		add(bottomPort, BorderLayout.SOUTH);
		PerspectiveManager.setPerspectiveDirty(true);
	}

	@Override
	protected void dockingPositionChanged(String name,
		String oldPosition, String newPosition)
	{
	}

	@Override
	public void closeCurrentArea()
	{
	}

	@Override
	public JComponent floatDockableWindow(String name)
	{
		return null;
	}

	@Override
	public DockingLayout getDockingLayout(ViewConfig config)
	{
		return null;
	}

	@Override
	public void hideDockableWindow(String name)
	{
	}

	@Override
	public boolean isDockableWindowDocked(String name)
	{
		return false;
	}

	@Override
	public boolean isDockableWindowVisible(String name)
	{
		return false;
	}

	@Override
	public void applyDockingLayout(DockingLayout docking)
	{
		// 'docking' is null if jEdit was started without a perspective file
		boolean loaded = false;
		if (docking != null)
		{
		}
		if (! loaded) // No saved layout - just use the docking positions specified by jEdit properties
			super.applyDockingLayout(null);
	}

	@Override
	public void showDockableWindow(String name)
	{
		JComponent window = getDockable(name);
		String position = getDockablePosition(name); 
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return;
		Dockable d = DockingManager.registerDockable(window);
		DefaultDockingPort port;
		if (position.equals(DockableWindowManager.LEFT))
			port = leftPort;
		else if (position.equals(DockableWindowManager.RIGHT))
			port = rightPort;
		else if (position.equals(DockableWindowManager.TOP))
			port = topPort;
		else
			port = bottomPort;
		port.dock(d, DockingConstants.CENTER_REGION);
	}

	@Override
	public void setMainPanel(JPanel panel)
	{
		add(panel, BorderLayout.CENTER);
	}

	public class FlexDockDockingArea implements DockingArea {
		public void showMostRecent() {
		}
		public String getCurrent() {
			return null;
		}
		public void show(String name) {
		}
	}
	
	public DockingArea getBottomDockingArea() {
		return null;
	}

	public DockingArea getLeftDockingArea() {
		return null;
	}

	public DockingArea getRightDockingArea() {
		return null;
	}

	public DockingArea getTopDockingArea() {
		return null;
	}

	@Override
	public void disposeDockableWindow(String name) {
	}

}
