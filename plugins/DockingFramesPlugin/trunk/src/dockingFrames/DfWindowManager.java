package dockingFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import bibliothek.extension.gui.dock.preference.PreferenceDialog;
import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceTreeDialog;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CPreferenceModel;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.action.predefined.CMinimizeAction;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CDockable.ExtendedMode;
import bibliothek.gui.dock.common.layout.ThemeMap;
import bibliothek.gui.dock.common.location.CBaseLocation;
import bibliothek.gui.dock.common.location.CContentAreaCenterLocation;
import bibliothek.gui.dock.common.location.CFlapIndexLocation;
import bibliothek.gui.dock.common.location.CRectangleLocation;
import bibliothek.gui.dock.util.ComponentWindowProvider;
import bibliothek.util.xml.XElement;

@SuppressWarnings("serial")
public class DfWindowManager extends DockableWindowManager
{
	private Map<String, JEditDockable> created = new HashMap<String, JEditDockable>();
	private CControl control;
	private static JEditDockableFactory factory;
	private CWorkingArea mainArea;
	private DefaultSingleCDockable mainDockable;
	private DfDockingArea top, left, bottom, right;
	private JEditDockable focused;
	private CPreferenceModel prefs;

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
		CGrid grid = new CGrid();
		grid.add(0, 0, 1, 1, mainArea);
		control.getContentArea().deploy(grid);
		setVisible(true);
		prefs = new CPreferenceModel(control);
		control.setPreferenceModel(prefs);
		control.addFocusListener(new CFocusListener()
		{
			public void focusGained(CDockable dockable)
			{
				if (dockable instanceof JEditDockable)
					focused = (JEditDockable) dockable;
			}
			public void focusLost(CDockable dockable)
			{
				if (focused == dockable)
					focused = null;
			}
		});
	}

	static public void showPreferenceDialog(View view)
	{
		DfWindowManager dwm = (DfWindowManager) view.getDockableWindowManager();
		CControl control = dwm.getControl();
		Component owner = control.intern().getController().findRootWindow();
		PreferenceModel model = control.getPreferenceModel();
		if (model instanceof PreferenceTreeModel)
			PreferenceTreeDialog.openDialog((PreferenceTreeModel)model, owner);
		else
			PreferenceDialog.openDialog(model, owner);
		
	}

	@Override
	public void setMainPanel(JPanel panel)
	{
		mainDockable = new DefaultSingleCDockable("mainPanel", panel);
		mainArea.add(mainDockable);
		mainDockable.setTitleShown(false);
        mainDockable.setVisible(true);
	}

	private JEditDockable getJEditDockable(String name)
	{
		JEditDockable d = created.get(name);
		if (d == null)
		{
			d = createDefaultDockable(name);
			if (d == null)
				return null;
			control.add(d);
		}
		return d;
	}

	@Override
	public void showDockableWindow(String name)
	{
		JEditDockable d = getJEditDockable(name);
		if (d == null)
			return;
		String position = getDockablePosition(name);
		CContentAreaCenterLocation center = CLocation.base().normal();
		CRectangleLocation loc = null;
		if (position.equals(DockableWindowManager.BOTTOM))
			loc = center.rectangle(0.0, 0.8, 1.0, 0.2);
		else if (position.equals(DockableWindowManager.TOP))
			loc = center.rectangle(0.0, 0.0, 1.0, 0.2);
		else if (position.equals(DockableWindowManager.RIGHT))
			loc = center.rectangle(0.8, 0.0, 0.2, 0.8);
		else if (position.equals(DockableWindowManager.LEFT))
			loc = center.rectangle(0.0, 0.0, 0.2, 0.8);
		if (loc != null)
			d.setLocation(loc.stack());
		else
			d.setExtendedMode(ExtendedMode.EXTERNALIZED);
		d.setVisible(true);
	}

	private void setTheme(String name)
	{
		ThemeMap themes = control.getThemes();
		themes.select(name);
	}

	public CControl getControl()
	{
		return control;
	}
	
	@Override
	public void applyDockingLayout(DockingLayout docking)
	{
		if (docking != null)
		{
			DfDockingLayout layout = (DfDockingLayout) docking;
			String filename = layout.getPersistenceFilename();
			if (filename != null)
			{
				try
				{
					control.readXML(new File(filename));
					return;
				}
				catch (IOException e)
				{
				}
			}
		}		
		super.applyDockingLayout(docking);
	}

	@Override
	protected void dockingPositionChanged(String dockableName,
			String oldPosition, String newPosition)
	{
		showDockableWindow(dockableName);
	}

	@Override
	public JComponent floatDockableWindow(String name)
	{
		JEditDockable d = getJEditDockable(name);
		d.setExtendedMode(ExtendedMode.EXTERNALIZED);
		d.setVisible(true);
		return d.getWindow();
	}

	private void setDockablePosition(String name, String position)
	{
		jEdit.setProperty(name + ".dock-position", position);
	}

	private class DfDockingArea implements DockingArea
	{
		private String position;
		private JEditDockable recent;
		public DfDockingArea(String position)
		{
			this.position = position;
		}
		public String getPosition() { return position; }
		private JEditDockable getCurrentDockable()
		{
			Vector<JEditDockable> dockables = getAreaDockables();
			for (JEditDockable d: dockables)
			{
				if (d.isVisible())
					return d;
			}
			return null;
		}
		public String getCurrent()
		{
			JEditDockable d = getCurrentDockable();
			if (d == null)
				return null;
			return d.getName();
		}
		public void show(String name)
		{
			if (name == null) // hide
			{
				recent = getCurrentDockable();
				Vector<JEditDockable> dockables = getAreaDockables();
				for (JEditDockable d: dockables)
					d.setExtendedMode(ExtendedMode.MINIMIZED);
			}
			else // show
			{
				setDockablePosition(name, position);
				showDockableWindow(name);
			}
		}
		public void showMostRecent()
		{
			if (recent != null)
				recent.setExtendedMode(ExtendedMode.NORMALIZED);
		}
		private Point getLocation(Container c)
		{
			Point p = c.getLocation();
			for ( ; c != null; c = c.getParent())
			{
				p.x += c.getX();
				p.y += c.getY();
			}
			return p;
		}
		public boolean belongsToArea(JEditDockable d)
		{
			Container main = mainDockable.getContentPane();
			JComponent c = d.getWindow();
			Point mainPos = getLocation(main);
			Point cPos = getLocation(c);
			if (DockableWindowManager.BOTTOM.equals(position))
				return (cPos.getY() >= mainPos.getY() + main.getHeight());
			else if (DockableWindowManager.TOP.equals(position))
				return (cPos.getY() + c.getHeight() <= mainPos.getY());
			else if (DockableWindowManager.LEFT.equals(position))
				return (cPos.getX() + c.getWidth() <= mainPos.getX());
			else if (DockableWindowManager.RIGHT.equals(position))
				return (cPos.getX() >= mainPos.getX() + main.getWidth());
			return false;
		}
		private Vector<JEditDockable> getAreaDockables()
		{
			Vector<JEditDockable> dockables = new Vector<JEditDockable>();
			for (JEditDockable d: created.values())
			{
				if (belongsToArea(d))
					dockables.add(d);
			}
			return dockables;
		}
		public String [] getDockables()
		{
			Vector<JEditDockable> dockables = getAreaDockables();
			String [] names = new String[dockables.size()];
			for (int i = 0; i < dockables.size(); i++)
				names[i] = dockables.get(i).getName();
			return names;
		}
	}

	@Override
	public void closeCurrentArea()
	{
		if (focused == null)
			return;
		DfDockingArea area = getDockingAreaOf(focused); 
		if (area != null)
			area.show(null);
	}

	@Override
	public DockingLayout getDockingLayout(ViewConfig config)
	{
		DfDockingLayout layout = new DfDockingLayout(this);
		return layout;
	}

	@Override
	public void hideDockableWindow(String name)
	{
		JEditDockable d = created.get(name);
		if (d == null)
			return;
		if (! minimizeDockable(d))
			d.setExtendedMode(ExtendedMode.MINIMIZED);
	}

	@Override
	public boolean isDockableWindowDocked(String name)
	{
		JEditDockable d = created.get(name);
		if (d == null)
			return false;
		return (d.getExtendedMode() == ExtendedMode.NORMALIZED);
	}

	@Override
	public boolean isDockableWindowVisible(String name)
	{
		JComponent c = getDockable(name);
		if (c == null)
			return false;
		return c.isVisible();
	}

	@Override
	protected void propertiesChanged()
	{
		super.propertiesChanged();
		String selectedTheme = DfOptionPane.getThemeName();
		setTheme(selectedTheme);
	}

	public void disposeDockableWindow(String name)
	{
		JEditDockable d = created.get(name);
		if (d == null)
			return;
		d.setVisible(false);
		created.remove(name);
		control.remove(d);
	}

	private DfDockingArea getDockingAreaOf(JEditDockable dockable)
	{
		DfDockingArea area = (DfDockingArea) getTopDockingArea(); 
		if (! area.belongsToArea(dockable))
		{
			area = (DfDockingArea) getBottomDockingArea(); 
			if (! area.belongsToArea(dockable))
			{
				area = (DfDockingArea) getLeftDockingArea();
				if (! area.belongsToArea(dockable))
				{
					area = (DfDockingArea) getRightDockingArea();
					if (! area.belongsToArea(dockable))
						area = null;
				}
			}
		}
		return area;
	}

	private boolean minimizeDockable(JEditDockable d)
	{
		DfDockingArea area = getDockingAreaOf(d);
		if (area == null)
			return false;
		CBaseLocation loc = CLocation.base();
		CFlapIndexLocation mloc = null;
		if (area.getPosition().equals(DockableWindowManager.BOTTOM))
			mloc = loc.minimalSouth();
		else if (area.getPosition().equals(DockableWindowManager.TOP))
			mloc = loc.minimalNorth();
		else if (area.getPosition().equals(DockableWindowManager.LEFT))
			mloc = loc.minimalWest();
		else if (area.getPosition().equals(DockableWindowManager.RIGHT))
			mloc = loc.minimalEast();
		if (mloc == null)
			return false;
		d.setLocation(mloc.stack());
		d.setExtendedMode(ExtendedMode.MINIMIZED);
		return true;
	}

	private class MinimizeAction extends CMinimizeAction
	{
		protected MinimizeAction()
		{
			super(control);
		}
		@Override
		public void action(CDockable dockable)
		{
			if (dockable instanceof JEditDockable)
			{
				if (minimizeDockable((JEditDockable) dockable))
					return;
			}
			super.action(dockable);
		}
	}

	private JEditDockable createDefaultDockable(String name)
	{
		JComponent window = getDockable(name);
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return null;
		String title = getDockableTitle(name);
		final JEditDockable d = new JEditDockable(factory, name, title, window);
		created.put(name, d);
		MinimizeAction minimizeAction = new MinimizeAction();
		d.putAction(CDockable.ACTION_KEY_MINIMIZE, minimizeAction);
		return d;
	}

	private class JEditDockableFactory implements MultipleCDockableFactory<JEditDockable, JEditDockableLayout>
	{
		public JEditDockableLayout create()
		{
			return new JEditDockableLayout();
		}
		public JEditDockable read(JEditDockableLayout layout)
		{
			return createDefaultDockable(layout.getName());
		}
		public JEditDockableLayout write(JEditDockable dockable)
		{
			return new JEditDockableLayout(dockable.getName());
		}
	}
	private static class JEditDockableLayout implements MultipleCDockableLayout
	{
		private String name;
		public JEditDockableLayout()
		{
		}
		public JEditDockableLayout(String name)
		{
			this.name = name;
		}
		public String getName()
		{
			return name;
		}
		public void readStream(DataInputStream in) throws IOException
		{
			name = in.readUTF();
		}
		public void readXML(XElement element)
		{
			name = element.getString();
		}
		public void writeStream(DataOutputStream out) throws IOException
		{
			out.writeUTF(name);
		}
		public void writeXML(XElement element)
		{
			element.setString(name);
		}
	}
	private static class JEditDockable extends DefaultMultipleCDockable
	{
		private String name;
		private JComponent window;
		public JEditDockable(JEditDockableFactory factory, String name,
			String title, JComponent window)
		{
			super(factory, title, window);
			this.name = name;
			this.window = window;
			setCloseable(true);
		}
		public String getName() { return name; }
		public JComponent getWindow() { return window; }
	}

	@Override
	public DockingArea getBottomDockingArea()
	{
		if (bottom == null)
			bottom = new DfDockingArea(DockableWindowManager.BOTTOM);
		return bottom;
	}

	@Override
	public DockingArea getLeftDockingArea()
	{
		if (left == null)
			left = new DfDockingArea(DockableWindowManager.LEFT);
		return left;
	}

	@Override
	public DockingArea getRightDockingArea()
	{
		if (right == null)
			right = new DfDockingArea(DockableWindowManager.RIGHT);
		return right;
	}

	@Override
	public DockingArea getTopDockingArea()
	{
		if (top == null)
			top = new DfDockingArea(DockableWindowManager.TOP);
		return top;
	}
}
