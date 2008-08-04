package myDoggy;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

@SuppressWarnings("serial")
public class MyDoggyWindowManager extends DockableWindowManager {

	private MyDoggyToolWindowManager wm = null;
	
	public MyDoggyWindowManager(View view, DockableWindowFactory instance,
			ViewConfig config)
	{
		super(view, instance, config);
		setLayout(new BorderLayout());
		wm = new MyDoggyToolWindowManager();
		add(wm, BorderLayout.CENTER);
	}

	@Override
	protected void dockableMoved(String name, String from, String to) {
		super.dockableMoved(name, from, to);
		showDockableWindow(name);
	}


	@Override
	public void adjust(View view, ViewConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeCurrentArea() {
		// TODO Auto-generated method stub

	}

	@Override
	public KeyListener closeListener(String dockableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JComponent floatDockableWindow(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingLayout getDockingLayout(ViewConfig config) {
		return new MyDoggyDockingLayout();
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
	public void setDockableTitle(String dockable, String title) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDockingLayout(DockingLayout docking) {
		// For now, just use the docking positions specified by jEdit properties
		super.setDockingLayout(null);
	}

	@Override
	public void showDockableWindow(String name) {
		ToolWindow tw = wm.getToolWindow(name);
		if (tw != null) {
			tw.setActive(true);
			return;
		}
		JComponent window = getDockable(name);
		String position = getDockablePosition(name); 
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return;
		tw = wm.registerToolWindow(
			name, getDockableTitle(name), null, window, position2anchor(position));
		tw.setActive(true);
	}

	private ToolWindowAnchor position2anchor(String position) {
		if (position.equals(DockableWindowManager.LEFT))
			return ToolWindowAnchor.LEFT;
		if (position.equals(DockableWindowManager.BOTTOM))
			return ToolWindowAnchor.BOTTOM;
		if (position.equals(DockableWindowManager.RIGHT))
			return ToolWindowAnchor.RIGHT;
		return ToolWindowAnchor.TOP;
	}

	@Override
	public void setBottomToolbars(JPanel toolbars) {
		add(toolbars, BorderLayout.SOUTH);
	}

	@Override
	public void setTopToolbars(JPanel toolbars) {
		add(toolbars, BorderLayout.NORTH);
	}

	@Override
	public void setMainPanel(JPanel panel) {
		wm.getContentManager().addContent("main", "main", null, panel);
	}

}
