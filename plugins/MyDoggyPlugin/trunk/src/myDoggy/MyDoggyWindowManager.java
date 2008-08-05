package myDoggy;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;
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
	protected void dockableMoved(String name, String from, String to) {
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
		MyDoggyDockingLayout layout = new MyDoggyDockingLayout();
		layout.setWindowManager(wm);
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
			if (views[i] == view)
				layout.setIndex(i);
		return layout;
	}

	private String getToolWindowID(String dockableName) {
		return dockableName;
	}
	
	private ToolWindow getToolWindow(String dockableName) {
		return wm.getToolWindow(getToolWindowID(dockableName));
	}
	
	@Override
	public void hideDockableWindow(String name) {
		ToolWindow tw = getToolWindow(name);
		if (tw == null)
			return;
		tw.setVisible(false);
	}

	@Override
	public boolean isDockableWindowDocked(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDockableWindowVisible(String name) {
		ToolWindow tw = getToolWindow(name);
		if (tw == null)
			return false;
		return (tw.isVisible());
	}

	@Override
	public void setDockableTitle(String dockable, String title) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDockingLayout(DockingLayout docking) {
		String filename = null;//((MyDoggyDockingLayout)docking).getPersistenceFilename()
		if (filename != null) {
			java.io.File f = new File(filename);
			if (f.exists()) {
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
				return;
			}
		}
		// No saved layout - just use the docking positions specified by jEdit properties
		super.setDockingLayout(null);
		return;
	}

	public class PersistenceCallback implements PersistenceDelegateCallback {
		public Content contentNotFound(ToolWindowManager toolWindowManager,
				String contentId) {
			return null;
		}
		public ToolWindow toolwindowNotFound(
				ToolWindowManager toolWindowManager, String toolWindowId) {
			return createToolWindow(toolWindowId);
		}
	}
	
	private ToolWindow createToolWindow(String name) {
		String title = getDockableTitle(name);
		JComponent window = getDockable(name);
		String position = getDockablePosition(name); 
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return null;
		String id = getToolWindowID(name);
		ToolWindow tw = wm.registerToolWindow(
			id, title, null, window, position2anchor(position));
		tw.setRepresentativeAnchorButtonTitle(title);
		return tw;
	}
	
	@Override
	public void showDockableWindow(String name) {
		ToolWindow tw = getToolWindow(name);
		if (tw != null) {
			tw.setActive(true);
			return;
		}
		tw = createToolWindow(name);
		tw.setType(ToolWindowType.DOCKED);
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
	public void setMainPanel(JPanel panel) {
		wm.getContentManager().addContent("main", "main", null, panel);
	}

}
