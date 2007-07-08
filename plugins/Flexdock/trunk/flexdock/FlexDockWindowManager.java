package flexdock;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
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
import org.flexdock.view.Viewport;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableLayout;
import org.gjt.sp.jedit.gui.DockableWindowContainer;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;

@SuppressWarnings("serial")
public class FlexDockWindowManager extends DockableWindowManager {

	View view;
	DockableWindowFactory factory;
	private static final String PERSPECTIVE_FILE = "jedit.xml";
	private static final String MAIN_VIEW = "Main";
	private static final String MAIN_PERSPECTIVE = "jEdit";
	public static Container editPane;
	private HashMap<String, JComponent> windows = new HashMap<String, JComponent>();
	private boolean alternateLayout;
	private org.flexdock.view.View mainView = null;

	PanelWindowContainer top, bottom, left, right;
	private Viewport viewport;
	private DockableWindowManager dockMan;
	private ViewFactory viewFactory;
	
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

		dockMan = view.getDockableWindowManager();
		alternateLayout = ((DockableLayout)dockMan.getLayout()).isAlternateLayout();
		top = new PanelWindowContainer(this,TOP,config.topPos);
		left = new PanelWindowContainer(this,LEFT,config.leftPos);
		bottom = new PanelWindowContainer(this,BOTTOM,config.bottomPos);
		right = new PanelWindowContainer(this,RIGHT,config.rightPos);
		editPane = view.getEditPane();
		configureDocking();
		setLayout(new BorderLayout());
		viewport = new Viewport();
		viewport.getDockingProperties().setSingleTabsAllowed(false);
		setLayout(new BorderLayout());
		add(viewport, BorderLayout.CENTER);
		mainView = new FlexDockMainView(MAIN_VIEW);
		//mainView.setTerritoryBlocked(DockingConstants.CENTER_REGION, true);
		mainView.setTitlebar(null);
		mainView.add(editPane, 0);
		final File xml = new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE);
		if (xml.exists())
			dockMan.close();
		if (! xml.exists()) {
			DockingManager.restoreLayout();
			PerspectiveManager.getInstance().loadPerspective(MAIN_PERSPECTIVE);
		} else {
			DockingManager.restoreLayout();
		}
	}
	private void configureDocking() {
		viewFactory = new ViewFactory(view);
		DockingManager.setDockableFactory(viewFactory);
		DockingManager.setFloatingEnabled(true);
        EffectsManager.setPreview(new GhostPreview());
        DockingManager.setSingleTabsAllowed(false);
		// configure the perspective manager
		PerspectiveManager.setFactory(new DemoPerspectiveFactory(view));
		PerspectiveManager.setRestoreFloatingOnLoad(true);
		// load any previously persisted layouts
		PersistenceHandler persister = new FilePersistenceHandler(new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE), XMLPersister.newDefaultInstance());
		PerspectiveManager.setPersistenceHandler(persister);
		try {
			DockingManager.loadLayoutModel();
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch (PersistenceException ex) {
            ex.printStackTrace();
        }
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
		// TODO Auto-generated method stub
	}
	@Override
	public JPopupMenu createPopupMenu(DockableWindowContainer container,
			String dockable, boolean clone) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public JComponent floatDockableWindow(String name) {
		DockableWindowFactory.Window window = factory.getDockableWindowFactory(name);
		JComponent c = window.createDockableWindow(view, DockableWindowManager.FLOATING);
		JFrame f = new JFrame(getDockableTitle(name));
		f.setContentPane(c);
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
		// TODO Auto-generated method stub
		//super.handleMessage(msg);
	}
	@Override
	public void hideDockableWindow(String name) {
		// TODO Auto-generated method stub
		//super.hideDockableWindow(name);
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
			DockingManager.restoreLayout();
			PerspectiveManager.getInstance().loadPerspective(MAIN_PERSPECTIVE);
		} else {
			DockingManager.restoreLayout();
		}
	}
	
	
	@Override
	public void showDockableWindow(String name) {
		Dockable d = DockingManager.getDockable(name);
		viewport.dock(d, DockingConstants.SOUTH_REGION);
		windows.put(name, (JComponent)d.getComponent());
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
			Perspective perspective = new Perspective(MAIN_PERSPECTIVE, "jEdit");
			sequence = perspective.getInitialSequence(true);
			DockableWindowManager dockMan = view.getDockableWindowManager();
			sequence.add(MAIN_VIEW);
			if (! alternateLayout) {
				addDockables(DockingConstants.WEST_REGION, dockMan.getLeftDockingArea());
				addDockables(DockingConstants.EAST_REGION, dockMan.getRightDockingArea());
				addDockables(DockingConstants.NORTH_REGION, dockMan.getTopDockingArea());
				addDockables(DockingConstants.SOUTH_REGION, dockMan.getBottomDockingArea());
			} else {
				addDockables(DockingConstants.NORTH_REGION, dockMan.getTopDockingArea());
				addDockables(DockingConstants.SOUTH_REGION, dockMan.getBottomDockingArea());
				addDockables(DockingConstants.WEST_REGION, dockMan.getLeftDockingArea());
				addDockables(DockingConstants.EAST_REGION, dockMan.getRightDockingArea());
			}
			return perspective;
		}
		private void addDockables(String region, PanelWindowContainer dockingArea) {
			String [] dockables = dockingArea.getDockables();
			if (dockables.length == 0)
				return;
			DockableWindowManager dockMan = dockingArea.getDockableWindowManager();
			float split;
			float dimension = (float)dockingArea.getDimension() +
				(float)dockMan.getWidth() * 0.016f;	// For the buttons
			if (region.equals(DockingConstants.WEST_REGION))
				split = dimension / dockMan.getWidth();
			else if (region.equals(DockingConstants.EAST_REGION))
				split = 1 - dimension / (dockMan.getWidth() - dockMan.getLeftDockingArea().getDimension());
			else if (region.equals(DockingConstants.NORTH_REGION))
				split = dimension / dockMan.getHeight();
			else
				split = 1 - dimension / (dockMan.getHeight() - dockMan.getTopDockingArea().getDimension());
			sequence.add(dockables[0], MAIN_VIEW, region, split);
			windows.put(dockables[0], dockMan.getDockable(dockables[0]));
			for (int i = 1; i < dockables.length; i++) {
				sequence.add(dockables[i], dockables[0]);
				windows.put(dockables[i], dockMan.getDockable(dockables[i]));
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
				c = window.createDockableWindow(view, DockableWindowManager.BOTTOM);
			}
			String title = getDockableTitle(id);
			org.flexdock.view.View d = new org.flexdock.view.View(id, title, title);
			d.addAction(DockingConstants.CLOSE_ACTION);
			d.addAction(DockingConstants.PIN_ACTION);
			d.setContentPane(c);
			//Dockable d = DockableComponentWrapper.create(c, id, title);
			//DockingManager.registerDockable(d);
			return d;
		}
	}
	private class FlexDockMainView extends org.flexdock.view.View {

		private JPanel panel;
		
		public FlexDockMainView(String persistentId) {
			super(persistentId);
			panel = new JPanel(new BorderLayout());
			setContentPane(panel);
		}

		@Override
		public Component add(Component comp, int index) {
			panel.add(comp, BorderLayout.CENTER);
			return comp;
		}
		
	}
}
