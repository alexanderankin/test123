package dockingFrames;

import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.PerspectiveManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

@SuppressWarnings("serial")
public class DfWindowManager extends DockableWindowManager {

	private static final String WEST = "West";
	private static final String EAST = "East";
	private static final String SOUTH = "South";
	private static final String NORTH = "North";
	private static final String CENTER = "center";
	private static final String MAIN = "main";

	private DockController controller;
	private Map<String, DockStation> stations, areas;
	private Factory factory;
	private JPanel mainPanel;
	
	public DfWindowManager(View view, DockableWindowFactory instance,
			ViewConfig config) {
		super(view, instance, config);
		setLayout(new BorderLayout());
        stations = new HashMap<String, DockStation>();
        areas = new HashMap<String, DockStation>();
		controller = new DockController();
		controller.setTheme(new EclipseTheme());
        SplitDockStation center = new SplitDockStation();
        add(center.getComponent(), BorderLayout.CENTER);
        createStackDockStation(NORTH);
        createStackDockStation(SOUTH);
        createStackDockStation(EAST);
        createStackDockStation(WEST);
        controller.add(center);
        stations.put(CENTER, center);
        factory = new Factory();
        PerspectiveManager.setPerspectiveDirty(true);
	}

	public Factory getDockFactory() {
		return factory;
	}
	
	private StackDockStation createStackDockStation(String title)
	{
		StackDockStation s = new StackDockStation();
		controller.add(s);
		s.setTitleText(title);
		areas.put(title, s);
		return s;
	}
	
	@Override
	public void applyDockingLayout(DockingLayout docking) {
		if (docking != null) {
			DfDockingLayout layout = (DfDockingLayout) docking;
			String filename = layout.getPersistenceFilename();
			if (filename != null) {
				XElement root = null;
				try {
					root = XIO.readUTF(new FileInputStream(filename));
					DockSituation situation = new DockSituation();
					situation.add(factory);
					remove(((SplitDockStation)stations.get(CENTER)).getComponent());
					stations = situation.readXML(root);
					SplitDockStation center = (SplitDockStation)stations.get(CENTER);
					add(center.getComponent(), BorderLayout.CENTER);
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		}		
		super.applyDockingLayout(docking);
		dropDockingAreas();
	}

	public Map<String, DockStation> getStationMap() {
		return stations;
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
		DfDockingLayout layout = new DfDockingLayout();
		layout.setManager(this);
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
			if (views[i] == view)
				layout.setIndex(i);
		return layout;
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
		mainPanel = panel;
		stations.get(CENTER).drop(new JEditDockable("main", "main", panel));
	}

	private void dropDockingArea(StackDockStation s,
		SplitDockPathProperty.Location location, double size)
	{
        if (s.getDockableCount() == 0)
        	return;
		SplitDockPathProperty p = new SplitDockPathProperty();
        p.add(location, size);
    	stations.get(CENTER).drop(s, p);
	}
	private void dropDockingAreas() {
        dropDockingArea((StackDockStation) areas.get(NORTH), SplitDockPathProperty.Location.TOP, 0.2);
        dropDockingArea((StackDockStation) areas.get(SOUTH), SplitDockPathProperty.Location.BOTTOM, 0.2);
        dropDockingArea((StackDockStation) areas.get(WEST), SplitDockPathProperty.Location.LEFT, 0.2);
        dropDockingArea((StackDockStation) areas.get(EAST), SplitDockPathProperty.Location.RIGHT, 0.2);
	}

	@Override
	public void showDockableWindow(String name) {
		JEditDockable d = createDefaultDockable(name);
		if (d == null)
			return;
		String s;
		String position = getDockablePosition(name); 
		if (position.equals(DockableWindowManager.TOP))
			s = NORTH;
		else if (position.equals(DockableWindowManager.BOTTOM))
			s = SOUTH;
		else if (position.equals(DockableWindowManager.RIGHT))
			s = EAST;
		else
			s = WEST;
		areas.get(s).drop(d);
	}

	private JEditDockable createDefaultDockable(String name) {
		JComponent window = getDockable(name);
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return null;
		String title = getDockableTitle(name);
		JEditDockable d = new JEditDockable(name, title, window);
		return d;
	}

	private static class JEditDockable extends DefaultDockable {
		String name;
		public JEditDockable(String name, String title, JComponent window) {
			super(window, title);
			this.name = name;
			setFactoryID("jEdit");
		}
		public String getName() {
			return name;
		}
	}
	private class Factory extends DefaultDockableFactory {

		@Override
		public String getID() {
			return "jEdit";
		}

		@Override
		public Object getLayout(DefaultDockable element,
				Map<Dockable, Integer> children)
		{
			if (element instanceof JEditDockable) {
				JEditDockable d = (JEditDockable) element;
				return d.getName();
			}
			return super.getLayout(element, children);
		}

		@Override
		public DefaultDockable layout(Object layout, Map<Integer, Dockable> children)
		{
			String name = (String) layout;
			if (name.equals(MAIN))
				return new JEditDockable(MAIN, MAIN, mainPanel);
			return createDefaultDockable(name);
		}

		@Override
		public DefaultDockable layout(Object layout) {
			return layout(layout, null);
		}

		@Override
		public Object read(XElement element) {
			XElement child = element.getElement("Dockable");
			if (child == null)
				return super.read(element);
			return child.getString("name");
		}

		@Override
		public void write(Object layout, XElement element) {
			XElement child = new XElement("Dockable");
			child.addString("name", (String)layout);
			element.addElement(child);
		}
		
	}
}
