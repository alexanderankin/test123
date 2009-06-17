package dockingFrames;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.layout.ThemeMap;
import bibliothek.gui.dock.layout.PredefinedDockSituation;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.ComponentWindowProvider;
import bibliothek.util.xml.XElement;

@SuppressWarnings("serial")
public class DfWindowManager extends DockableWindowManager {

	private Map<String, DockStation> stations;
	private PredefinedDockSituation situation;
	private Map<String, DefaultMultipleCDockable> created = new HashMap<String, DefaultMultipleCDockable>();
	private CControl control;
	private static JEditDockableFactory factory;
	private CWorkingArea mainArea;
	private CGrid grid;
	
	public DfWindowManager(View view, DockableWindowFactory instance,
			ViewConfig config)
	{
		super(view, instance, config);
		setLayout(new BorderLayout());
		control = new CControl(new ComponentWindowProvider(this));
		factory = new JEditDockableFactory();
		control.addMultipleDockableFactory("dockables", factory);
		add(BorderLayout.CENTER, control.getContentArea());
		mainArea = control.createWorkingArea("main");
		grid = new CGrid(control);
		grid.add(1, 1, 2, 2, mainArea);
		control.getContentArea().deploy(grid);
		setVisible(true);
	}

	@Override
	public void setMainPanel(JPanel panel) {
		DefaultSingleCDockable mainDockable = new DefaultSingleCDockable("mainPanel", panel);
		mainDockable.setTitleShown(false);
        mainDockable.setLocation(mainArea.getStationLocation());
        control.add(mainDockable);
        mainDockable.setVisible(true);
	}

	@Override
	public void showDockableWindow(String name) {
		DefaultMultipleCDockable d = created.get(name);
		if (d != null)
			return;
		d = createDefaultDockable(name);
		if (d == null)
			return;
		String position = getDockablePosition(name);
		if (position.equals(DockableWindowManager.BOTTOM))
			grid.add(1, 3, 3, 1, d);
		else if (position.equals(DockableWindowManager.TOP))
			grid.add(0, 0, 3, 1, d);
		else if (position.equals(DockableWindowManager.RIGHT))
			grid.add(3, 0, 1, 3, d);
		else if (position.equals(DockableWindowManager.LEFT))
			grid.add(0, 1, 1, 3, d);
	}

	@Override
	public void toggleDockAreas() {
		/*
		DfDockingLayout layout = new DfDockingLayout(this);
		if (! hidden) {
			layout.saveLayout(toggleDocksLayoutName, DockingLayout.NO_VIEW_INDEX);
			for (DefaultMultipleCDockable d: created.values())
				if (d.get.getDockParent() != null)
					d.getDockParent().drag(d);
		} else {
			layout.loadLayout(toggleDocksLayoutName, DockingLayout.NO_VIEW_INDEX);
			applyDockingLayout(layout);
		}
		hidden = (! hidden);
		if (view.getEditPane() != null)
			view.getEditPane().requestFocus();
			*/
	}

	private void setTheme(String name) {
		ThemeMap themes = control.getThemes();
		themes.select(name);
	}
	public CControl getControl()
	{
		return control;
	}
	public PredefinedDockSituation getDockSituation() {
		return situation;
	}
	
	@Override
	public void applyDockingLayout(DockingLayout docking) {
		if (docking != null) {
			DfDockingLayout layout = (DfDockingLayout) docking;
			String filename = layout.getPersistenceFilename();
			if (filename != null) {
				try {
					control.readXML(new File(filename));
					return;
				} catch (IOException e) {
				}
			}
		}		
		super.applyDockingLayout(docking);
		control.getContentArea().deploy(grid);
	}

	public Map<String, DockStation> getStationMap() {
		return stations;
	}
	
	@Override
	protected void dockingPositionChanged(String dockableName,
			String oldPosition, String newPosition) {
		showDockableWindow(dockableName);
	}

	@Override
	public void closeCurrentArea() {
		/*
		Dockable d = controller.getFocusedDockable();
		SplitDockProperty side = getSide(d);
		if (side != null)
			areas.get(side).show(null);
			*/
	}

	@Override
	public JComponent floatDockableWindow(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	private class DfDockingArea implements DockingArea {
		public DfDockingArea(SplitDockProperty side) {
		}
		public String getCurrent() {
			return null;
		}
		public void show(String name) {
			if (name == null) { // hide
			} else { // show
				showDockableWindow(name);
			}
		}
		public void showMostRecent() {
		}
		public String[] getDockables() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	@Override
	public DockingLayout getDockingLayout(ViewConfig config) {
		DfDockingLayout layout = new DfDockingLayout(this);
		return layout;
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
		JComponent c = getDockable(name);
		if (c == null)
			return false;
		return c.isVisible();
	}

	@Override
	protected void propertiesChanged() {
		super.propertiesChanged();
		String selectedTheme = DfOptionPane.getThemeName();
		setTheme(selectedTheme);
	}

	public void disposeDockableWindow(String name) {
		/*
		DefaultMultipleCDockable d = created.get(name);
		if (d != null) {
			if (d.getController() != null)
				d.getDockParent().drag(d);
			created.remove(name);
		}
		*/
	}

	private JEditDockable createDefaultDockable(String name) {
		JComponent window = getDockable(name);
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return null;
		String title = getDockableTitle(name);
		final JEditDockable d = new JEditDockable(factory, name, title, window);
		created.put(name, d);
		return d;
	}

	private static class MainDockable extends DefaultDockable {
		public MainDockable(JPanel panel) {
			super(panel, (String)null);
		}

		@Override
		public DockTitle getDockTitle(DockTitleVersion version) {
			return null;
		}
	}
	private class JEditDockableFactory implements MultipleCDockableFactory<JEditDockable, JEditDockableLayout>
	{
		public JEditDockableLayout create() {
			return new JEditDockableLayout();
		}

		public JEditDockable read(JEditDockableLayout layout) {
			return createDefaultDockable(layout.getName());
		}

		public JEditDockableLayout write(JEditDockable dockable) {
			return new JEditDockableLayout(dockable.getName());
		}
	}
	private static class JEditDockableLayout implements MultipleCDockableLayout {
		private String name;
		public JEditDockableLayout() {
		}
		public JEditDockableLayout(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void readStream(DataInputStream in) throws IOException {
			name = in.readUTF();
		}
		public void readXML(XElement element) {
			name = element.getString();
		}
		public void writeStream(DataOutputStream out) throws IOException {
			out.writeUTF(name);
		}
		public void writeXML(XElement element) {
			element.setString(name);
		}
	}
	private static class JEditDockable extends DefaultMultipleCDockable {
		private String name;
		public JEditDockable(JEditDockableFactory factory, String name,
			String title, JComponent window)
		{
			super(factory, title, window);
			this.name = name;
		}
		public String getName() { return name; }
	}

	@Override
	public DockingArea getBottomDockingArea() {
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}
}
