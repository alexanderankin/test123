package infonode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	private static final String DEFAULT_FILE =
		Plugin.getConfigDirectory() + File.separator + "perspective.sav";
	private org.gjt.sp.jedit.View view;
	private DockableWindowFactory factory;
	private JEditViewMap viewMap;
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
		if (new File(DEFAULT_FILE).exists())
			load(DEFAULT_FILE);
	}
	private void convertView(org.gjt.sp.jedit.View view) {
		viewMap = new JEditViewMap(this);
		positions = new HashMap<String, String>();
		JComponent editPane = view.getSplitPane();
		if (editPane == null)
			editPane = view.getEditPane();
		center = new JPanel(new BorderLayout());
		center.add(editPane, BorderLayout.CENTER);
		mainView = new View(MAIN_VIEW_NAME, null, center);
		mainView.setName(MAIN_VIEW_NAME);
		mainView.getViewProperties().setAlwaysShowTitle(false);
		viewMap.addView(MAIN_VIEW_NAME, mainView);
		String [] dockables = factory.getRegisteredDockableWindows();
		Vector<String> leftDockables = new Vector<String>();
		Vector<String> rightDockables = new Vector<String>();
		Vector<String> bottomDockables = new Vector<String>();
		Vector<String> topDockables = new Vector<String>();
		for (int i = 0; i < dockables.length; i++) {
			String dockable = dockables[i];
			String pos = getDockablePosition(dockable);
			if (pos.equals(DockableWindowManager.LEFT))
				leftDockables.add(dockable);
			else if (pos.equals(DockableWindowManager.RIGHT))
				rightDockables.add(dockable);
			else if (pos.equals(DockableWindowManager.BOTTOM))
				bottomDockables.add(dockable);
			else if (pos.equals(DockableWindowManager.TOP))
				topDockables.add(dockable);
		}
		left = addDockables(leftDockables);
		right = addDockables(rightDockables);
		bottom = addDockables(bottomDockables);
		top = addDockables(topDockables);
		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getRootWindowProperties().getWindowAreaProperties().setBackgroundColor(center.getBackground());
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
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
	private Vector<View> addDockables(Vector<String> dockables) {
		Vector<View> areaViews = new Vector<View>();
		for (int i = 0; i < dockables.size(); i++) {
			String name = dockables.get(i);
			showDockableWindow(name);
			areaViews.add(viewMap.getView(name));
		}
		return areaViews;
	}
	@Override
	public void close() {
		save(DEFAULT_FILE);
		for (int i = 0; i < viewMap.getViewCount(); i++) {
			View v = viewMap.getViewAtIndex(i);
			viewMap.removeView(v.getName());
		}
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
		View v = constructDockableView(name);
		leftTab.addTab(v);
		v.undock(new Point(0,0));
		return (JComponent)v.getComponent();
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
		v.setName(name);
		viewMap.addView(name, v);
		positions.put(name, getDockablePosition(name));
		return v;
	}
	public View constructDockableView(String name) {
		String position = getDockablePosition(name);
		Window w = factory.getDockableWindowFactory(name);
		JComponent c = w.createDockableWindow(view, position);
		return createDockableView(name, c);
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
	public void load(String file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			rootWindow.read(ois);
			ois.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void save(String file) {
		ObjectOutputStream ous;
		try {
			ous = new ObjectOutputStream(new FileOutputStream(file));
			rootWindow.write(ous);
			ous.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
