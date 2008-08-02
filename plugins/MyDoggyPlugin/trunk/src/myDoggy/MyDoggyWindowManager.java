package myDoggy;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManagerBase;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

@SuppressWarnings("serial")
public class MyDoggyWindowManager extends DockableWindowManagerBase {

	private MyDoggyToolWindowManager wm = null;
	private JPanel main = null;
	
	public MyDoggyWindowManager(View view, DockableWindowFactory instance,
			ViewConfig config)
	{
		super(view, instance, config);
		setLayout(new BorderLayout());
		wm = new MyDoggyToolWindowManager();
		main = new JPanel();
		main.setLayout(new BorderLayout());
		wm.getContentManager().addContent("main", "main", null, main);
		add(wm, BorderLayout.CENTER);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void showDockableWindow(String name) {
		JComponent window = getDockable(name);
		String position = getDockablePosition(name); 
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return;
		ToolWindow tw = wm.registerToolWindow(
			name, getDockableTitle(name), null, window, position2anchor(position));
		tw.setActive(true);
	}

	private ToolWindowAnchor position2anchor(String position) {
		if (position.equals(DockableWindowManagerBase.LEFT))
			return ToolWindowAnchor.LEFT;
		if (position.equals(DockableWindowManagerBase.BOTTOM))
			return ToolWindowAnchor.BOTTOM;
		if (position.equals(DockableWindowManagerBase.RIGHT))
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
	public void setContent(java.awt.Component c) {
		main.add(c, BorderLayout.CENTER);
		main.revalidate();
	}

}
