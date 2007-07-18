package flexdock;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.defaults.DockableComponentWrapper;
import org.flexdock.docking.drag.effects.EffectsManager;
import org.flexdock.docking.drag.preview.GhostPreview;
import org.flexdock.docking.state.PersistenceException;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveFactory;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.persist.FilePersistenceHandler;
import org.flexdock.perspective.persist.PersistenceHandler;
import org.flexdock.perspective.persist.xml.XMLPersister;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableLayout;
import org.gjt.sp.jedit.gui.DockableWindowContainer;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;
import org.gjt.sp.jedit.gui.DockableWindowFactory.Window;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.msg.PluginUpdate;

@SuppressWarnings("serial")
public class FlexDockWindowManager extends DockableWindowManager {

	private static final String ALTERNATE_LAYOUT_PROPERTY = "view.docking.alternateLayout";
	public class MyDockingStrategy extends DefaultDockingStrategy {
		@Override
		public DockingPort createDockingPortImpl(DockingPort base) {
			return new MyDockingPort(FlexDockWindowManager.this);
		}
	}
	View view;
	DockableWindowFactory factory;
	private static final String PERSPECTIVE_FILE = "jedit.xml";
	private static final String MAIN_VIEW = "Main";
	private static final String MAIN_PERSPECTIVE = "jEdit";
	private static final String DOCK_POSITION = ".dock-position";
	private HashMap<String, JComponent> windows = new HashMap<String, JComponent>();
	private boolean alternateLayout;
	private FlexDockMainView mainView = null;
	private HashMap<String, Vector<String>> dockables = null;
	private HashMap<String, Float> split = null;
	
	PanelWindowContainer top, bottom, left, right;
	
	public void save() {
		try {
			DockingManager.storeLayoutModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void construct(View view, DockableWindowFactory factory,
			ViewConfig config) {
		this.view = view;
		this.factory = factory;

		top = new PanelWindowContainer(this,TOP,config.topPos);
		left = new PanelWindowContainer(this,LEFT,config.leftPos);
		bottom = new PanelWindowContainer(this,BOTTOM,config.bottomPos);
		right = new PanelWindowContainer(this,RIGHT,config.rightPos);
		configureDocking();
		DefaultDockingPort mainport = new MyDockingPort(this);
		mainport.getDockingProperties().setSingleTabsAllowed(false);
		setLayout(new BorderLayout());
		add(mainport, BorderLayout.CENTER);
		mainView = new FlexDockMainView(MAIN_VIEW);
		getDockableStates();
		//mainView.setTerritoryBlocked(DockingConstants.CENTER_REGION, true);
		//mainView.add(view.getEditPane(), 0);
	}
	public void convert(View view, DockableWindowFactory factory,
			ViewConfig config) {
		construct(view, factory, config);
		DockableWindowManager dockMan = view.getDockableWindowManager();
		alternateLayout = jEdit.getBooleanProperty(ALTERNATE_LAYOUT_PROPERTY);
		//mainView.setTerritoryBlocked(DockingConstants.CENTER_REGION, true);
		// Find the edit panes
		Component c = view.getEditPane();
		while (! (c.getParent() instanceof DockableWindowManager))
			c = c.getParent();
		mainView.add(c, 0);
		final File xml = new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE);
		if (! xml.exists()) {
			getDockableStates();
			getDockingAreaSplits(dockMan);
		}
		dockMan.close();
		init();
	}
	private void getDockableStates() {
		dockables = new HashMap<String, Vector<String>>();
		dockables.put(DockingConstants.WEST_REGION, new Vector<String>());
		dockables.put(DockingConstants.EAST_REGION, new Vector<String>());
		dockables.put(DockingConstants.NORTH_REGION, new Vector<String>());
		dockables.put(DockingConstants.SOUTH_REGION, new Vector<String>());
		String[] windowList = factory.getRegisteredDockableWindows();
		HashMap<String, String> posMap = new HashMap<String, String>();
		posMap.put(DockableWindowManager.TOP, DockingConstants.NORTH_REGION);
		posMap.put(DockableWindowManager.BOTTOM, DockingConstants.SOUTH_REGION);
		posMap.put(DockableWindowManager.LEFT, DockingConstants.WEST_REGION);
		posMap.put(DockableWindowManager.RIGHT, DockingConstants.EAST_REGION);
		for(int i = 0; i < windowList.length; i++)
		{
			String id = windowList[i];
			String position = posMap.get(jEdit.getProperty(id + DOCK_POSITION));
			if (position != null)
				dockables.get(position).add(id);
		}
	}
	private void getDockingAreaSplits(DockableWindowManager dockMan) {
		float topDim = dockMan.getTopDockingArea().getDimension();
		float bottomDim = dockMan.getBottomDockingArea().getDimension();
		float leftDim = dockMan.getLeftDockingArea().getDimension();
		float rightDim = dockMan.getRightDockingArea().getDimension();
		int w = dockMan.getWidth();
		int h = dockMan.getHeight();
		float bd = w * 0.016f; // Button panel dimension
		split = new HashMap<String, Float>();
		split.put(DockingConstants.WEST_REGION, new Float((leftDim + bd) / w));
		split.put(DockingConstants.EAST_REGION, new Float(1 - (rightDim + bd) / (w - leftDim)));
		split.put(DockingConstants.NORTH_REGION, new Float((topDim + bd) / h));
		split.put(DockingConstants.SOUTH_REGION, new Float(1 - (bottomDim + bd) / (h - topDim)));
	}
	private void configureDocking() {
		DockingManager.setDockableFactory(new ViewFactory(view));
		DockingManager.setFloatingEnabled(true);
        EffectsManager.setPreview(new GhostPreview());
        DockingManager.setSingleTabsAllowed(true);
        // configure the docking ports
        DockingManager.setDockingStrategy(MyDockingPort.class, new MyDockingStrategy());
		// configure the perspective manager
		PerspectiveManager.setFactory(new DemoPerspectiveFactory(view));
		PerspectiveManager.setRestoreFloatingOnLoad(true);
		// load any previously persisted layouts
		PersistenceHandler persister = new FilePersistenceHandler(new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE), XMLPersister.newDefaultInstance());
		PerspectiveManager.setPersistenceHandler(persister);
		// remember to store on shutdown
		//DockingManager.setAutoPersist(true);
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
	}
	@Override
	public PanelWindowContainer getBottomDockingArea() {
		return bottom;
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
		return left;
	}
	@Override
	public PanelWindowContainer getRightDockingArea() {
		return right;
	}
	@Override
	public PanelWindowContainer getTopDockingArea() {
		return top;
	}
	@Override
	public View getView() {
		return view;
	}
	@Override
	public void handleMessage(EBMessage msg) {
		if(msg instanceof PluginUpdate) {
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
		EditBus.addToBus(this);
		File xml = new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE);
		if (! xml.exists()) {
			PerspectiveManager.getInstance().loadPerspective(MAIN_PERSPECTIVE);
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
	}
	@Override
	public void hideDockableWindow(String name) {
		Object reason = DockableWindowUpdate.DEACTIVATED;
		EditBus.send(new DockableWindowUpdate(this, reason, name));
		Dockable d = DockingManager.getDockable(name);
		DockingManager.close(d);
		DockingManager.unregisterDockable(d);
		windows.remove(name);
	}
	@Override
	public void showDockableWindow(String name) {
		DockingManager.display(name);
		Object reason = DockableWindowUpdate.ACTIVATED;
		EditBus.send(new DockableWindowUpdate(this, reason, name));
	}
	public void add(Component comp, Object o, int index) {
		mainView.add(comp, o, index);
	}
	public Component add(Component comp, int index) {
		return mainView.add(comp, index);
	}

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
			JComponent c = FlexDockWindowManager.this.getDockable(id);
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
			panel.revalidate();
			return comp;
		}
		public void add(Component comp, Object o, int index) {
			String s = (String)o;
			if (s.equals(DockableLayout.TOP_TOOLBARS))
				panel.add(comp, BorderLayout.NORTH);
			else
				panel.add(comp, BorderLayout.SOUTH);
			panel.revalidate();
		}
	}
}
