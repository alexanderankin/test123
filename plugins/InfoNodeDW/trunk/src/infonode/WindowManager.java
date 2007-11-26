package infonode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableLayout;
import org.gjt.sp.jedit.gui.DockableWindowContainer;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;

@SuppressWarnings("serial")
public class WindowManager extends DockableWindowManager {

	private org.gjt.sp.jedit.View view;
	private DockableWindowFactory factory;
	private ViewMap viewMap;
	private RootWindow rootWindow;
	private HashMap<String, JComponent> windows = new HashMap<String, JComponent>();
	private JComponent center;
	private TabWindow left, right, top, bottom;
	
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if (constraints.equals(DockableLayout.TOP_TOOLBARS))
			center.add(comp, BorderLayout.NORTH);
		else if (constraints.equals(DockableLayout.BOTTOM_TOOLBARS))
			center.add(comp, BorderLayout.SOUTH);
		else
			super.addImpl(comp, constraints, index);
	}
	@Override
	public void construct(org.gjt.sp.jedit.View view, DockableWindowFactory factory,
			ViewConfig config) {
		this.view = view;
		this.factory = factory;
		convertView(view);
		setLayout(new BorderLayout());
		add(rootWindow, BorderLayout.CENTER);
	}
	private void convertView(org.gjt.sp.jedit.View view) {
		viewMap = new ViewMap();
		DockableWindowManager dwm = view.getDockableWindowManager();
		JComponent editPane = view.getSplitPane();
		if (editPane == null)
			editPane = view.getEditPane();
		center = new JPanel(new BorderLayout());
		center.add(editPane, BorderLayout.CENTER);
		View main = new View("Main", null, center);
		main.getViewProperties().setAlwaysShowTitle(false);
		viewMap.addView(0, main);
		left = addDockables(dwm, dwm.getLeftDockingArea().getDockables(),
				Direction.LEFT, Direction.UP);
		right = addDockables(dwm, dwm.getRightDockingArea().getDockables(),
				Direction.RIGHT, Direction.DOWN);
		bottom = addDockables(dwm, dwm.getBottomDockingArea().getDockables(),
				Direction.UP, Direction.RIGHT);
		top = addDockables(dwm, dwm.getTopDockingArea().getDockables(),
				Direction.UP, Direction.RIGHT);
		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getRootWindowProperties().getWindowAreaProperties().setBackgroundColor(center.getBackground());
		SplitWindow sw = null;
		if (left.getChildWindowCount() > 0)
			sw = new SplitWindow(true, 0.25f, left, main);
		if (right.getChildWindowCount() > 0)
			sw = new SplitWindow(true, 0.75f, (sw == null) ? main : sw, right);
		if (bottom.getChildWindowCount() > 0)
			sw = new SplitWindow(false, 0.75f, (sw == null) ? main : sw, bottom);
		if (top.getChildWindowCount() > 0)
			sw = new SplitWindow(false, 0.25f, top, (sw == null) ? main : sw);
		if (sw != null )
			rootWindow.setWindow(sw);
	}
	private TabWindow addDockables(DockableWindowManager dwm, String[] windows,
			Direction tabPosition, Direction tabDirection) {
		View [] areaViews = new View[windows.length];
		for (int i = 0; i < windows.length; i++) {
			dwm.showDockableWindow(windows[i]);
			JComponent window = dwm.getDockable(windows[i]);
			View v = new View(dwm.getDockableTitle(windows[i]), null, window);
			//viewMap.addView(viewMap.getViewCount(), v);
			areaViews[i] = v;
		}
		TabWindow tw = new TabWindow(areaViews);
		TabWindowProperties twp = tw.getTabWindowProperties();
		twp.getTabbedPanelProperties().setTabAreaOrientation(tabPosition);
		twp.getTabProperties().getTitledTabProperties().getNormalProperties().setDirection(tabDirection);
		return tw;
	}
	@Override
	public void close() {
/*
		Set<String> ids = DockingManager.getDockableIds();
		Iterator<String> i = ids.iterator();
		while (i.hasNext())
		{
			Dockable d = DockingManager.getDockable(i.next());
			DockingPort p = d.getDockingPort();
			if (p != null)
				p.undock(d.getComponent());
		}
		*/
	}
	@Override
	public void closeCurrentArea() {
		// This is never called.
	}
	@Override
	public JPopupMenu createPopupMenu(DockableWindowContainer container,
			String dockable, boolean clone) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public JComponent floatDockableWindow(String name) {
		/*
		showDockableWindow(name);
		Dockable d = DockingManager.getDockable(name);
		DockingManager.close(d);
		JComponent c = (JComponent) d.getComponent();
		JFrame f = new JFrame(getDockableTitle(name));
		DefaultDockingPort floatingPort = new DefaultDockingPort();
		f.getContentPane().add(BorderLayout.CENTER, floatingPort);
		floatingPort.dock(c, DockingConstants.CENTER_REGION);
		f.pack();
		f.setVisible(true);
		return c;
		*/
		return super.floatDockableWindow(name);
	}
	@Override
	public PanelWindowContainer getBottomDockingArea() {
		return super.getBottomDockingArea();
	}
	@Override
	public JComponent getDockable(String name) {
		return windows.get(name);
	}
	@Override
	public String getDockableTitle(String name) {
		String t = jEdit.getProperty(name + ".longtitle");
		if (t == null)
			t = jEdit.getProperty(name + ".title");
		return t;
	}
	@Override
	public PanelWindowContainer getLeftDockingArea() {
		return super.getLeftDockingArea();
	}
	@Override
	public PanelWindowContainer getRightDockingArea() {
		return super.getRightDockingArea();
	}
	@Override
	public PanelWindowContainer getTopDockingArea() {
		return super.getTopDockingArea();
	}
	@Override
	public org.gjt.sp.jedit.View getView() {
		return view;
	}
	@Override
	public void handleMessage(EBMessage msg) {
/*		if(msg instanceof PluginUpdate) {
			PluginUpdate pmsg = (PluginUpdate)msg;
			if (pmsg.isExiting())
			{
				// we don't care
			}
			else if (pmsg.getWhat() == PluginUpdate.DEACTIVATED ||
					pmsg.getWhat() == PluginUpdate.UNLOADED)
			{
				// Close all plugin dockables
				PluginJAR jar = pmsg.getPluginJAR();
				Iterator<Window> iter = factory.getDockableWindowIterator();
				while (iter.hasNext()) {
					Window w = iter.next();
					if (w.getPlugin() == jar)
						hideDockableWindow(w.getName());
				}
				
			}
		}
		*/
	}
	@Override
	public boolean isDockableWindowDocked(String name) {
		return getDockable(name) != null;
	}
	@Override
	public boolean isDockableWindowVisible(String name) {
		return isDockableWindowDocked(name);
	}
	@Override
	public void paintChildren(Graphics g) {
		// TODO Auto-generated method stub
		super.paintChildren(g);
	}
	@Override
	public void setDockableTitle(String dockableName, String newTitle) {
		String propName = dockableName + ".longtitle";
		String oldTitle = jEdit.getProperty(propName);
		jEdit.setProperty(propName, newTitle);
		firePropertyChange(propName, oldTitle, newTitle);
	}
	public void init() {
		/*
		EditBus.addToBus(this);
		File xml = new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE);
		if (! xml.exists()) {
			PerspectiveManager.getInstance().setCurrentPerspective(MAIN_PERSPECTIVE);
		} else {
			try {
				DockingManager.loadLayoutModel();
			} catch(IOException ex) {
				ex.printStackTrace();
			} catch (PersistenceException ex) {
	            ex.printStackTrace();
	        }
			DockingManager.restoreLayout();
		}
		*/
	}
	@Override
	public void hideDockableWindow(String name) {
		/*
		Object reason = DockableWindowUpdate.DEACTIVATED;
		EditBus.send(new DockableWindowUpdate(this, reason, name));
		Dockable d = DockingManager.getDockable(name);
		DockingManager.close(d);
		DockingManager.unregisterDockable(d);
		windows.remove(name);
		*/
	}
	@Override
	public void showDockableWindow(String name) {
		/*
		DockingManager.display(name);
		Object reason = DockableWindowUpdate.ACTIVATED;
		EditBus.send(new DockableWindowUpdate(this, reason, name));
		*/
	}
	public Component add(Component comp, int index) {
		//return mainView.add(comp, index);
		return super.add(comp, index);
	}

	/*
	private class DemoPerspectiveFactory implements PerspectiveFactory {
		View view;
		LayoutSequence sequence;
		public DemoPerspectiveFactory(View view) {
			this.view = view;
		}
		public Perspective getPerspective(String persistentId) {
			if(! MAIN_PERSPECTIVE.equals(persistentId))
				return null;
			Perspective perspective = new Perspective(MAIN_PERSPECTIVE, MAIN_PERSPECTIVE);
			sequence = perspective.getInitialSequence(true);
			sequence.add(MAIN_VIEW);
			String[] windowList = factory.getRegisteredDockableWindows();
			for(int i = 0; i < windowList.length; i++)
			{
				String dockable = windowList[i];
				String newPosition = jEdit.getProperty(dockable + ".dock-position",FLOATING);
			}
			if (dockables != null) {
				if (! alternateLayout) {
					addDockables(DockingConstants.WEST_REGION);
					addDockables(DockingConstants.EAST_REGION);
					addDockables(DockingConstants.NORTH_REGION);
					addDockables(DockingConstants.SOUTH_REGION);
				} else {
					addDockables(DockingConstants.NORTH_REGION);
					addDockables(DockingConstants.SOUTH_REGION);
					addDockables(DockingConstants.WEST_REGION);
					addDockables(DockingConstants.EAST_REGION);
				}
			}
			return perspective;
		}
		private void addDockables(String region) {
			Vector<String> regionDockables = dockables.get(region);
			if (regionDockables.isEmpty())
				return;
			float splitVal = split.get(region).floatValue();
			String first = regionDockables.get(0);
			sequence.add(first, MAIN_VIEW, region, splitVal);
			System.err.println(first + " " + region + " " + splitVal);
			for (int i = 1; i < regionDockables.size(); i++) {
				sequence.add(regionDockables.get(i), first);
			}
		}
	}
	@SuppressWarnings("unused")
	private class ViewFactory extends DockableFactory.Stub {
		
		View view;
		
		public ViewFactory(View view) {
			this.view = view;
		}
		public Component getDockableComponent(String dockableId) {
			if(MAIN_VIEW.equals(dockableId))
				return mainView;
			return createView(dockableId);
		}
		private Component createView(String id) {
			JComponent c = WindowManager.this.getDockable(id);
			if (c == null)
			{
				DockableWindowFactory.Window window = factory.getDockableWindowFactory(id);
				String position = jEdit.getProperty(id + DOCK_POSITION);
				c = window.createDockableWindow(view, position);
			}
			String title = getDockableTitle(id);
			Dockable d = DockableComponentWrapper.create(c, id, title);
			DockingManager.registerDockable(d);
			windows.put(id, c);
			return c;
		}
	}
	private class FlexDockMainView extends org.flexdock.view.View {

		private JPanel panel;
		
		public FlexDockMainView(String persistentId) {
			super(persistentId);
			setTitlebar(null);
			panel = new JPanel(new BorderLayout());
			setContentPane(panel);
		}

		@Override
		public Component add(Component comp, int index) {
			panel.add(comp, BorderLayout.CENTER);
			for (int i=0; i<panel.getComponentCount();i++)
				System.err.println(i + ":" + panel.getComponent(i));
			return comp;
		}
		public void add(Component comp, Object o, int index) {
			String s = (String)o;
			if (s.equals(DockableLayout.TOP_TOOLBARS))
				panel.add(comp, BorderLayout.NORTH);
			else
				panel.add(comp, BorderLayout.SOUTH);
			for (int i=0; i<panel.getComponentCount();i++)
				System.err.println(i + ":" + panel.getComponent(i));
		}
	}
	*/
}
