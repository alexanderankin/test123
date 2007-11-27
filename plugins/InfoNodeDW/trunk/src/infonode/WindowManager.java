package infonode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.StringViewMap;
import net.infonode.util.Direction;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableLayout;
import org.gjt.sp.jedit.gui.DockableWindowContainer;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;
import org.gjt.sp.jedit.gui.DockableWindowFactory.Window;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;

@SuppressWarnings("serial")
public class WindowManager extends DockableWindowManager {

	private static final String MAIN_VIEW_NAME = "Main";
	private org.gjt.sp.jedit.View view;
	private DockableWindowFactory factory;
	private StringViewMap viewMap;
	private RootWindow rootWindow;
	private JComponent center;
	private Vector<View> left, right, top, bottom;
	private View mainView;
	private PanelWindowContainer topPanel, bottomPanel, leftPanel, rightPanel;
	private TabWindow leftTab, rightTab, bottomTab, topTab;
	private HashMap<String, String> positions;
	
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
		EditBus.addToBus(this);
		convertView(view);
		setLayout(new BorderLayout());
		add(rootWindow, BorderLayout.CENTER);
		topPanel = new PanelWindowContainer(this,TOP,config.topPos);
		leftPanel = new PanelWindowContainer(this,LEFT,config.leftPos);
		bottomPanel = new PanelWindowContainer(this,BOTTOM,config.bottomPos);
		rightPanel = new PanelWindowContainer(this,RIGHT,config.rightPos);
	}
	private void convertView(org.gjt.sp.jedit.View view) {
		viewMap = new StringViewMap();
		positions = new HashMap<String, String>();
		DockableWindowManager dwm = view.getDockableWindowManager();
		JComponent editPane = view.getSplitPane();
		if (editPane == null)
			editPane = view.getEditPane();
		center = new JPanel(new BorderLayout());
		center.add(editPane, BorderLayout.CENTER);
		mainView = new View(MAIN_VIEW_NAME, null, center);
		mainView.getViewProperties().setAlwaysShowTitle(false);
		viewMap.addView(MAIN_VIEW_NAME, mainView);
		left = addDockables(dwm, dwm.getLeftDockingArea().getDockables());
		right = addDockables(dwm, dwm.getRightDockingArea().getDockables());
		bottom = addDockables(dwm, dwm.getBottomDockingArea().getDockables());
		top = addDockables(dwm, dwm.getTopDockingArea().getDockables());
		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getRootWindowProperties().getWindowAreaProperties().setBackgroundColor(center.getBackground());
		leftTab = createTabs(left, Direction.LEFT, Direction.UP);
		rightTab = createTabs(right, Direction.RIGHT, Direction.DOWN);
		bottomTab = createTabs(bottom, Direction.UP, Direction.RIGHT);
		topTab = createTabs(top, Direction.UP, Direction.RIGHT);
		setViewLayout();
	}
	private void setViewLayout() {
		DockingWindow sw = null;
		sw = addArea(leftTab, mainView, true, true, 0.25f);
		sw = addArea(rightTab, sw, true, false, 0.75f);
		sw = addArea(bottomTab, sw, false, false, 0.75f);
		sw = addArea(topTab, sw, false, true, 0.25f);
		rootWindow.setWindow(sw);
	}
	private DockingWindow [] convertToArray(Vector<View> views) {
		DockingWindow [] windows = new DockingWindow[views.size()];
		views.toArray(windows);
		return windows;
	}
	private TabWindow createTabs(Vector<View> views, Direction side, Direction dir) {
		DockingWindow [] w = convertToArray(views);
		TabWindow tw = new TabWindow(w);
		TabWindowProperties twp = tw.getTabWindowProperties();
		twp.getTabbedPanelProperties().setTabAreaOrientation(side);
		twp.getTabProperties().getTitledTabProperties().getNormalProperties().setDirection(dir);
		return tw;
	}
	private DockingWindow addArea(TabWindow tw, DockingWindow dw, boolean isHorizontal, boolean areaFirst, float divider) {
		if (tw.getChildWindowCount() == 0)
			return dw;
		DockingWindow w1 = (areaFirst ? tw : dw);
		DockingWindow w2 = (areaFirst ? dw : tw);
		return new SplitWindow(isHorizontal, divider, w1, w2);
	}
	public String getDockableTitle(String name) {
		String title = jEdit.getProperty(name + ".longtitle");
		if (title == null)
			return getDockableShortTitle(name);
		else
			return title;
	}
	private String getDockableShortTitle(String name) 
	{
		String title = jEdit.getProperty(name + ".title");
		if(title == null)
			return "NO TITLE PROPERTY: " + name;
		else
			return title;
	}
	private Vector<View> addDockables(DockableWindowManager dwm, String[] windows) {
		Vector<View> areaViews = new Vector<View>();
		for (int i = 0; i < windows.length; i++) {
			String name = windows[i];
			dwm.showDockableWindow(name);
			JComponent window = dwm.getDockable(name);
			View v = createDockableView(name, window);
			areaViews.add(v);
		}
		return areaViews;
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
		return bottomPanel;
	}
	@Override
	public JComponent getDockable(String name) {
		View v = viewMap.getView(name);
		if (v == null)
			return null;
		return (JComponent)v.getComponent();
	}
	@Override
	public PanelWindowContainer getLeftDockingArea() {
		return leftPanel;
	}
	@Override
	public PanelWindowContainer getRightDockingArea() {
		return rightPanel;
	}
	@Override
	public PanelWindowContainer getTopDockingArea() {
		return topPanel;
	}
	@Override
	public org.gjt.sp.jedit.View getView() {
		return view;
	}
	private void updateProperties()
	{
		if (view.isPlainView())
			return;

/*		((DockableLayout)getLayout()).setAlternateLayout(
			jEdit.getBooleanProperty("view.docking.alternateLayout"));
*/
		String[] windowList = factory.getRegisteredDockableWindows();
		for (int i = 0; i < windowList.length; i++) {
			String name = windowList[i];
			String position = getDockablePosition(name);
			if (position.equals(DockableWindowManager.FLOATING))
				continue;
			String curPosition = getCurrentDockablePosition(name);
			if (position == null || position.equals(curPosition))
				continue;
			showDockableWindow(name);
		}

		/*continuousLayout = jEdit.getBooleanProperty("appearance.continuousLayout");
		revalidate();
		repaint();
		*/
	}

	private String getCurrentDockablePosition(String name) {
		return positions.get(name);
	}
	@Override
	public void handleMessage(EBMessage msg) {
		if (msg instanceof PropertiesChanged)
			updateProperties();
/*
		if (msg instanceof DockableWindowUpdate)
		{
			if(((DockableWindowUpdate)msg).getWhat()
				== DockableWindowUpdate.PROPERTIES_CHANGED)
				propertiesChanged();
		}
		else if(msg instanceof PluginUpdate)
		{
			PluginUpdate pmsg = (PluginUpdate)msg;
			if(pmsg.getWhat() == PluginUpdate.LOADED)
			{
				Iterator<DockableWindowFactory.Window> iter = factory.getDockableWindowIterator();

				while(iter.hasNext())
				{
					DockableWindowFactory.Window w = iter.next();
					if(w.plugin == pmsg.getPluginJAR())
						addEntry(w);
				}

				propertiesChanged();
			}
			else if(pmsg.isExiting())
			{
				// we don't care
			}
			else if(pmsg.getWhat() == PluginUpdate.DEACTIVATED)
			{
				Iterator<Entry> iter = getAllPluginEntries(
					pmsg.getPluginJAR(),false);
				while(iter.hasNext())
				{
					Entry entry = iter.next();
					if(entry.container != null)
						entry.container.remove(entry);
				}
			}
			else if(pmsg.getWhat() == PluginUpdate.UNLOADED)
			{
				Iterator<Entry> iter = getAllPluginEntries(
					pmsg.getPluginJAR(),true);
				while(iter.hasNext())
				{
					Entry entry = iter.next();
					if(entry.container != null)
					{
						entry.container.unregister(entry);
						entry.win = null;
						entry.container = null;
					}
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
	private String getDockablePosition(String name) {
		return jEdit.getProperty(name + ".dock-position");
	}
	private View createDockableView(String name, JComponent c) {
		View v = new View(getDockableTitle(name), null, c);
		viewMap.addView(name, v);
		positions.put(name, getDockablePosition(name));
		return v;
	}
	@Override
	public void showDockableWindow(String name) {
		String position = getDockablePosition(name);
		if (position.equals(DockableWindowManager.FLOATING))
			return;
		View v = viewMap.getView(name);
		boolean repos = false;
		if (v == null) {
			Window w = factory.getDockableWindowFactory(name);
			JComponent c = w.createDockableWindow(view, position);
			v = createDockableView(name, c);
			repos = true;
		} else {
			String curPosition = getCurrentDockablePosition(name);
			if (! position.equals(curPosition))
				repos = true;
		}
		if (repos) {
			TabWindow tw = null;
			if (position.equals(DockableWindowManager.LEFT))
				tw = leftTab;
			else if (position.equals(DockableWindowManager.RIGHT))
				tw = rightTab;
			else if (position.equals(DockableWindowManager.BOTTOM))
				tw = bottomTab;
			if (position.equals(DockableWindowManager.TOP))
				tw = topTab;
			if (tw != null) {
				tw.addTab(v);
				setViewLayout();
			}
		}
		v.makeVisible();
		Object reason = DockableWindowUpdate.ACTIVATED;
		EditBus.send(new DockableWindowUpdate(this, reason, name));
	}
	public Component add(Component comp, int index) {
		//return mainView.add(comp, index);
		return super.add(comp, index);
	}
}
