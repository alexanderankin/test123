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
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.docking.theme.BlueHighlightDockingTheme;
import net.infonode.docking.theme.ClassicDockingTheme;
import net.infonode.docking.theme.DefaultDockingTheme;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.GradientDockingTheme;
import net.infonode.docking.theme.LookAndFeelDockingTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.theme.SlimFlatDockingTheme;
import net.infonode.docking.theme.SoftBlueIceDockingTheme;
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
	private ViewConfig config;
	private JEditViewMap viewMap;
	private RootWindow rootWindow;
	private JComponent center;
	private View mainView;
	private PanelWindowContainer topPanel, bottomPanel, leftPanel, rightPanel;
	private TabWindow leftTab, rightTab, bottomTab, topTab;
	private HashMap<String, String> positions;
	private DockingWindowsTheme currentTheme = null;
	private static DockingWindowsTheme [] themes = new DockingWindowsTheme[] {
			new BlueHighlightDockingTheme(),
			new ClassicDockingTheme(),
			new DefaultDockingTheme(),
			new GradientDockingTheme(),
			new LookAndFeelDockingTheme(),
			new ShapedGradientDockingTheme(),
			new SlimFlatDockingTheme(),
			new SoftBlueIceDockingTheme()
		};
	private static String [] themeNames;
	
	{
		themeNames = new String[themes.length];
		for (int i = 0; i < themes.length; i++)
			themeNames[i] = themes[i].getName();
	}

	public void setEditorComponent(Component c) {
		center.add(c, BorderLayout.CENTER);
	}
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if (DockableLayout.TOP_TOOLBARS.equals(constraints))
			center.add(comp, BorderLayout.NORTH);
		else if (DockableLayout.BOTTOM_TOOLBARS.equals(constraints))
			center.add(comp, BorderLayout.SOUTH);
		else
			super.addImpl(comp, constraints, index);
	}
	@Override
	public void construct(org.gjt.sp.jedit.View view, DockableWindowFactory factory,
			ViewConfig config) {
		this.view = view;
		this.factory = factory;
		this.config = config;
		viewMap = new JEditViewMap(this);
		positions = new HashMap<String, String>();
		center = new JPanel(new BorderLayout());
		mainView = new View(MAIN_VIEW_NAME, null, center);
		mainView.setName(MAIN_VIEW_NAME);
		mainView.getViewProperties().setAlwaysShowTitle(false);
		viewMap.addView(MAIN_VIEW_NAME, mainView);
		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getRootWindowProperties().getWindowAreaProperties().setBackgroundColor(center.getBackground());
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		rootWindow.getWindowBar(Direction.UP).setEnabled(true);
		rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
		rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);

		leftTab = createTabs(new Vector<View>(), Direction.LEFT, Direction.UP);
		rightTab = createTabs(new Vector<View>(), Direction.RIGHT, Direction.DOWN);
		bottomTab = createTabs(new Vector<View>(), Direction.UP, Direction.RIGHT);
		topTab = createTabs(new Vector<View>(), Direction.UP, Direction.RIGHT);

		setViewLayout();
		setLayout(new BorderLayout());
		add(rootWindow, BorderLayout.CENTER);
		invalidate();
		topPanel = new PanelWindowContainer(this,TOP,config.topPos);
		leftPanel = new PanelWindowContainer(this,LEFT,config.leftPos);
		bottomPanel = new PanelWindowContainer(this,BOTTOM,config.bottomPos);
		rightPanel = new PanelWindowContainer(this,RIGHT,config.rightPos);
	}
	@Override
	public DockableWindowManager getNewInstance() {
		return new WindowManager();
	}
	public void init() {
		EditBus.addToBus(this);
	}
	@Override
	public void applyViewConfig(ViewConfig config) {
		if (new File(DEFAULT_FILE).exists())
			load(DEFAULT_FILE);
		else {
			String [] dockables = factory.getRegisteredDockableWindows();
			for (int i = 0; i < dockables.length; i++) {
				String dockable = dockables[i];
				String pos = getDockablePosition(dockable);
				if (pos == null)
					continue;
				View v = createDummyView(dockable);
				TabWindow tw = null;
				if (pos.equals(DockableWindowManager.LEFT))
					tw = leftTab;
				else if (pos.equals(DockableWindowManager.RIGHT))
					tw = rightTab;
				else if (pos.equals(DockableWindowManager.BOTTOM))
					tw = bottomTab;
				else if (pos.equals(DockableWindowManager.TOP))
					tw = topTab;
				if (tw != null)
					tw.addTab(v);
			}
			super.applyViewConfig(config);
		}
	}
	private void setViewLayout() {
		DockingWindow sw = null;
		sw = addArea(leftTab, mainView, true, true, 0.25f);
		sw = addArea(rightTab, sw, true, false, 0.75f);
		sw = addArea(bottomTab, sw, false, false, 0.75f);
		sw = addArea(topTab, sw, false, true, 0.25f);
		rootWindow.setWindow(sw);
	}
	private class TabListener extends DockingWindowAdapter {

		HashSet<DockingWindow> create = new HashSet<DockingWindow>();
		HashSet<DockingWindow> added = new HashSet<DockingWindow>();
		
		@Override
		public void windowAdded(DockingWindow addedToWindow,
				DockingWindow addedWindow) {
			added.add(addedWindow);
			System.err.println("windowAdded: " + addedWindow.getName());
		}

		@Override
		public void windowShown(DockingWindow window) {
			System.err.println("windowShown: " + window.getName());
			if (added.contains(window)) {
				// Window just added
				create.add(window);
				added.remove(window);
			} else if (create.contains(window)) {
				String name = window.getName();
				TabWindow parent = (TabWindow) window.getWindowParent();
				View v = constructDockableView(name);
				parent.replaceChildWindow(window, v);
			}
		}
	}
	
	private DockingWindow [] convertToArray(Vector<View> views) {
		DockingWindow [] windows = new DockingWindow[views.size()];
		views.toArray(windows);
		return windows;
	}
	private TabWindow createTabs(Vector<View> views, Direction side, Direction dir) {
		DockingWindow [] w = convertToArray(views);
		TabWindow tw = new TabWindow(w);
		tw.addListener(new TabListener());
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
	View createDummyView(String name) {
		View v = new View(getDockableTitle(name), null, new JPanel());
		v.setName(name);
		return v;
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
		if (v == null) {
			v = constructDockableView(name);
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
			e.printStackTrace();
		} catch (IOException e) {
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
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	private void setTheme(String theme) {
		if (theme == null)
			return;
		for (int i = 0; i < themeNames.length; i++)
			if (themeNames[i].equals(theme)) {
				if (currentTheme != null)
					rootWindow.getRootWindowProperties().replaceSuperObject(
						currentTheme.getRootWindowProperties(), themes[i].getRootWindowProperties());
				else
					rootWindow.getRootWindowProperties().addSuperObject(themes[i].getRootWindowProperties());
				currentTheme = themes[i];
				return;
			}
	}
	public void selectTheme() {
		String defaultTheme = (currentTheme != null) ? currentTheme.getName() : themeNames[0];
		String theme = (String) JOptionPane.showInputDialog(view,
			"Select a theme:", "Themes", 0, null, themeNames, defaultTheme);
		setTheme(theme);
	}
}
