/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package flexdock;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
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
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableLayout;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;


public class Plugin extends EditPlugin {
	public static final String NAME = "Flexdock";
	private static final String PERSPECTIVE_FILE = "jedit.xml";
	static DefaultDockingPort mainPort = null;
	private static Container mainContainer;
	private static final String MAIN_VIEW = "Main";
	public static final String JEDIT_PERSPECTIVE = "jedit";
	private static final String MAIN_PORT = "MainPort";
	public static LayoutSequence seq = null;
	
	public static void doStart(View view) {
		configureDocking(view);
		Perspective jedit = new DemoPerspectiveFactory().getPerspective("jedit");
		PerspectiveManager.getInstance().setCurrentPerspective(jedit.getPersistentId());
		PerspectiveManager.getInstance().setDefaultPerspective(jedit.getPersistentId());
		mainPort = createDockingPort(100, 100);
		mainPort.setSingleTabAllowed(false);
		mainContainer = view.getContentPane();
 		DockableWindowManager dockMan = view.getDockableWindowManager();
 		DockingManager.setSingleTabsAllowed(true);
		DockingManager.registerDockable(mainContainer, MAIN_VIEW);
		DockingManager.dock(mainContainer, (DockingPort)mainPort);
		seq.add(MAIN_VIEW);
		boolean alternateLayout = ((DockableLayout)dockMan.getLayout()).isAlternateLayout();
		if (! alternateLayout) {
			convertDockables(view, DockingConstants.NORTH_REGION, dockMan.getTopDockingArea());
			convertDockables(view, DockingConstants.SOUTH_REGION, dockMan.getBottomDockingArea());
			convertDockables(view, DockingConstants.WEST_REGION, dockMan.getLeftDockingArea());
			convertDockables(view, DockingConstants.EAST_REGION, dockMan.getRightDockingArea());
		} else {
			convertDockables(view, DockingConstants.WEST_REGION, dockMan.getLeftDockingArea());
			convertDockables(view, DockingConstants.EAST_REGION, dockMan.getRightDockingArea());
			convertDockables(view, DockingConstants.NORTH_REGION, dockMan.getTopDockingArea());
			convertDockables(view, DockingConstants.SOUTH_REGION, dockMan.getBottomDockingArea());
		}
		view.setContentPane(mainPort);
		DockingManager.setMainDockingPort(mainPort, MAIN_PORT);
		try {
			DockingManager.loadLayoutModel();
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch (PersistenceException ex) {
            ex.printStackTrace();
        }
	}
	private static void configureDocking(View view) {
		// setup the DockingManager to work with our application
		DockingManager.setDockableFactory(new ViewFactory(view));
		DockingManager.setFloatingEnabled(true);
        EffectsManager.setPreview(new GhostPreview());
        
		// configure the perspective manager
		PerspectiveManager.setFactory(new DemoPerspectiveFactory());
		PerspectiveManager.setRestoreFloatingOnLoad(true);

		File xml = new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE);
		PersistenceHandler persister = new FilePersistenceHandler(xml, XMLPersister.newDefaultInstance());
		PerspectiveManager.setPersistenceHandler(persister);
		// remember to store on shutdown
		DockingManager.setAutoPersist(true);
	}
	private static void convertDockables(View view, String region,
			PanelWindowContainer dockingArea) {
		String [] dockables = dockingArea.getDockables();
		Dockable first = null;
		for (int i = 0; i < dockables.length; i++) {
			String name = dockables[i];
			Dockable dockable = createDockableComponent(view, name);
			if (i == 0) {
				first = dockable;
				DockingManager.dock(dockable, mainPort, region);
				float split;
				if (region.equals(DockingConstants.WEST_REGION) ||
						region.equals(DockingConstants.NORTH_REGION))
					split = 0.2f;
				else
					split = 0.75f;
				DockingManager.setSplitProportion(first, split);
				seq.add(name, MAIN_VIEW, region, split);
			}
			else {
				DockingManager.dock(dockable, first);
				seq.add(dockable, first);
			}

		}
	}
	private static DefaultDockingPort createDockingPort(int w, int h) {
		DefaultDockingPort port = new DefaultDockingPort();
		port.setSingleTabAllowed(true);
		port.setPreferredSize(new Dimension(w, h));
		return port;
	}

	private static Dockable createDockableComponent(View view,
			String name) {
		DockableWindowManager dockMan = view.getDockableWindowManager();
		String title = dockMan.getDockableTitle(name);
		JComponent c = dockMan.getDockable(name);
		if (c == null) {
			dockMan.showDockableWindow(name);
			c = dockMan.getDockable(name);
		}
		dockMan.hideDockableWindow(name);
		Dockable p = DockableComponentWrapper.create(c, name, title);
		DockingManager.registerDockable(p);
		return p;
	}

	private static class ViewFactory extends DockableFactory.Stub {
		
		View view;
		
		public ViewFactory(View view) {
			this.view = view;
		}
		public Component getDockableComponent(String dockableId) {
			System.err.println("ViewFactory.getDockable: " + dockableId);
			if(MAIN_VIEW.equals(dockableId))
				return view.getContentPane();
			return view.getDockableWindowManager().getDockable(dockableId);
		}
	}
	private static class DemoPerspectiveFactory implements PerspectiveFactory {
		
		public Perspective getPerspective(String persistentId) {
			if (! persistentId.equals("jedit"))
				return null;
			Perspective perspective = new Perspective(JEDIT_PERSPECTIVE, "jEdit");
			seq = perspective.getInitialSequence(true);
			return perspective;
		}
	}
	
}
