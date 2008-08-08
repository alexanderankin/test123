package myDoggy;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.PerspectiveManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.PersistenceDelegateCallback;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.PersistenceDelegate.MergePolicy;
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
		PerspectiveManager.setPerspectiveDirty(true);
	}

	@Override
	protected void dockingPositionChanged(String dockableName,
		String oldPosition, String newPosition)
	{
		showDockableWindow(dockableName);
	}

	@Override
	public void closeCurrentArea()
	{
		Object activeWindowId = wm.getActiveToolWindowId();
		if (activeWindowId == null)
			return;
		ToolWindow tw = wm.getToolWindow(activeWindowId);
		if (tw == null)
			return;
		tw.setVisible(false);
	}

	@Override
	public JComponent floatDockableWindow(String name)
	{
		ToolWindow tw = wm.getToolWindow(name);
		if (tw == null)
			tw = createToolWindow(name);
		if (tw == null)
			return null;
		tw.setType(ToolWindowType.FLOATING);
		tw.setActive(true);
		return (JComponent) tw.getComponent();
	}

	@Override
	public DockingLayout getDockingLayout(ViewConfig config)
	{
		MyDoggyDockingLayout layout = new MyDoggyDockingLayout();
		layout.setWindowManager(wm);
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
			if (views[i] == view)
				layout.setIndex(i);
		return layout;
	}

	private String getToolWindowID(String dockableName)
	{
		return dockableName;
	}
	
	private ToolWindow getToolWindow(String dockableName)
	{
		return wm.getToolWindow(getToolWindowID(dockableName));
	}
	
	@Override
	public void hideDockableWindow(String name)
	{
		ToolWindow tw = getToolWindow(name);
		if (tw == null)
			return;
		tw.setVisible(false);
	}

	@Override
	public boolean isDockableWindowDocked(String name)
	{
		ToolWindow tw = getToolWindow(name);
		if (tw == null)
			return false;
		return (tw.getType() == ToolWindowType.DOCKED);
	}

	@Override
	public boolean isDockableWindowVisible(String name)
	{
		ToolWindow tw = getToolWindow(name);
		if (tw == null)
			return false;
		return (tw.isVisible());
	}

	private void loadMyDoggyLayout(String filename)
	{
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(filename);
			PersistenceDelegateCallback callback = new PersistenceCallback();
			wm.getPersistenceDelegate().merge(inputStream, MergePolicy.RESET, callback);
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setDockingLayout(DockingLayout docking)
	{
		// 'docking' is null if jEdit was started without a perspective file
		if (docking != null)
		{
			MyDoggyDockingLayout layout = (MyDoggyDockingLayout) docking;
			String filename = layout.getPersistenceFilename();
			if (filename != null) {
				java.io.File f = new File(filename);
				if (f.exists()) {
					loadMyDoggyLayout(filename);
					return;
				}
			}
		}
		// No saved layout - just use the docking positions specified by jEdit properties
		super.setDockingLayout(null);
		return;
	}

	public class PersistenceCallback implements PersistenceDelegateCallback
	{
		@Override
		public Content contentNotFound(ToolWindowManager toolWindowManager,
				String contentId, PersistenceNode node) {
			return null;
		}

		@Override
		public ToolWindow toolwindowNotFound(
				ToolWindowManager toolWindowManager, String toolWindowId,
				PersistenceNode node) {
			if (! node.getBoolean("visible", true))
				return createFakeToolWindow(toolWindowId);
			return createToolWindow(toolWindowId);
		}
	}
	
	private ToolWindow createFakeToolWindow(String name)
	{
		JComponent window = new JPanel();
		if (window == null)
			return null;
		String title = getDockableTitle(name);
		String position = getDockablePosition(name); 
		String id = getToolWindowID(name);
		ToolWindowAnchor anchor = position2anchor(position);
		ToolWindow tw = wm.registerToolWindow(id, title, null, window, anchor);
		tw.setRepresentativeAnchorButtonTitle(title);
		tw.getTypeDescriptor(ToolWindowType.DOCKED).setIdVisibleOnTitleBar(false);
		PropertyChangeListener listener = new VisibilityChangeListener(tw);
		tw.addPropertyChangeListener("visible", listener);
		return tw;
	}
	
	private class VisibilityChangeListener implements PropertyChangeListener
	{
		ToolWindow tw;
		public VisibilityChangeListener(ToolWindow tw)
		{
			this.tw = tw;
		}
		private ToolWindowTab getFakeTab() {
			ToolWindowTab[] tabs = tw.getToolWindowTabs();
			for (ToolWindowTab tab: tabs)
				if (tab.getTitle().equals(tw.getTitle()))
					return tab;
			return null;
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (! tw.isVisible())
				return;
			removePropertyChangeListener("visible", this);
			ToolWindowTab fakeTab = getFakeTab();
			if (fakeTab == null)
				return;
			JComponent window = createDockable(tw.getId());
			ToolWindowTab newTab = tw.addToolWindowTab(fakeTab.getTitle(), window);
			tw.removeToolWindowTab(fakeTab);
			newTab.setSelected(true);
		}
	}
	
	private ToolWindow createToolWindow(String name)
	{
		JComponent window = getDockable(name);
		String position = getDockablePosition(name); 
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return null;
		String id = getToolWindowID(name);
		String title = getDockableTitle(name);
		ToolWindowAnchor anchor = position2anchor(position);
		ToolWindow tw = wm.registerToolWindow(id, title, null, window, anchor);
		tw.setRepresentativeAnchorButtonTitle(title);
		tw.getTypeDescriptor(ToolWindowType.DOCKED).setIdVisibleOnTitleBar(false);
		return tw;
	}
	
	@Override
	public void showDockableWindow(String name)
	{
		ToolWindow tw = getToolWindow(name);
		if (tw != null)
		{
			tw.setActive(true);
			return;
		}
		tw = createToolWindow(name);
		tw.setType(ToolWindowType.DOCKED);
		tw.setActive(true);
	}

	private ToolWindowAnchor position2anchor(String position)
	{
		if (position.equals(DockableWindowManager.LEFT))
			return ToolWindowAnchor.LEFT;
		if (position.equals(DockableWindowManager.BOTTOM))
			return ToolWindowAnchor.BOTTOM;
		if (position.equals(DockableWindowManager.RIGHT))
			return ToolWindowAnchor.RIGHT;
		return ToolWindowAnchor.TOP;
	}

	@Override
	public void setMainPanel(JPanel panel)
	{
		wm.getContentManager().addContent("main", "main", null, panel);
	}

	public class MyDoggyDockingArea implements DockingArea {
		ToolWindowAnchor anchor;
		public MyDoggyDockingArea(ToolWindowAnchor anchor) {
			this.anchor = anchor;
		}
		public void showMostRecent() {
			ToolWindow current = getCurrentToolWindow();
			if (current == null) {
				ToolWindow[] tools = wm.getToolsByAnchor(anchor);
				if (tools.length > 0)
					current = tools[0];
			}
			if ((current != null) && (! current.isActive()))
				current.setActive(true);
		}
		private ToolWindow getCurrentToolWindow() {
			ToolWindow[] tools = wm.getToolsByAnchor(anchor);
			for (ToolWindow tw: tools)
				if (tw.isActive())
					return tw;
			for (ToolWindow tw: tools)
				if (tw.isVisible())
					return tw;
			return null;
		}
		public String getCurrent() {
			ToolWindow current = getCurrentToolWindow();
			if (current == null)
				return null;
			return current.getId();
		}
		public void show(String name) {
			ToolWindow tw;
			if (name == null)
			{	// Hide the visible windows in this area
				do {
					tw = getCurrentToolWindow();
					if (tw != null)
						tw.setVisible(false);
				} while (tw != null);
			}
			else
			{	// Show the window
				showDockableWindow(name);
				tw = getToolWindow(name);
				if (tw != null)
					tw.setAnchor(anchor);
			}
		}
	}
	
	public DockingArea getBottomDockingArea() {
		return new MyDoggyDockingArea(ToolWindowAnchor.BOTTOM);
	}

	public DockingArea getLeftDockingArea() {
		return new MyDoggyDockingArea(ToolWindowAnchor.LEFT);
	}

	public DockingArea getRightDockingArea() {
		return new MyDoggyDockingArea(ToolWindowAnchor.RIGHT);
	}

	public DockingArea getTopDockingArea() {
		return new MyDoggyDockingArea(ToolWindowAnchor.TOP);
	}

	@Override
	protected void propertiesChanged() {
		super.propertiesChanged();
		wm.getToolWindowManagerDescriptor().setPushAwayMode(OptionPane.getPushAwayModeProp());
	}

}
