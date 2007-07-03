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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
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
import org.flexdock.view.Viewport;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableLayout;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;


public class Plugin extends EditPlugin {
	public static final String NAME = "Flexdock";
	private static final String PERSPECTIVE_FILE = "jedit.xml";
	private static final String MAIN_VIEW = "Main";
	private static final String MAIN_PERSPECTIVE = "jEdit";
	private static Viewport viewport;
	public static Container editPane;
	
	public static void doStart(final View view) {
		configureDocking(view);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				editPane = view.getContentPane();
				JPanel mv = new JPanel(new BorderLayout());
				viewport = new Viewport();
				viewport.getDockingProperties().setSingleTabsAllowed(false);
				mv.add(viewport, BorderLayout.CENTER);
				view.setContentPane(mv);
				File xml = new File(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR, PERSPECTIVE_FILE);
				if (! xml.exists()) {
					DockingManager.restoreLayout();
					PerspectiveManager.getInstance().loadPerspective(MAIN_PERSPECTIVE);
				} else {
					DockingManager.restoreLayout();
				}
			}
		});

	}

	private static void configureDocking(View view) {
		// setup the DockingManager to work with our application
		DockingManager.setDockableFactory(new ViewFactory(view));
		DockingManager.setFloatingEnabled(true);
        EffectsManager.setPreview(new GhostPreview());
        DockingManager.setSingleTabsAllowed(true);
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
		DockingManager.setAutoPersist(true);
	}
	private static class DemoPerspectiveFactory implements PerspectiveFactory {
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
			boolean alternateLayout = ((DockableLayout)dockMan.getLayout()).isAlternateLayout();
			sequence.add(MAIN_VIEW);
			if (! alternateLayout) {
				addDockables(DockingConstants.NORTH_REGION, dockMan.getTopDockingArea());
				addDockables(DockingConstants.SOUTH_REGION, dockMan.getBottomDockingArea());
				addDockables(DockingConstants.WEST_REGION, dockMan.getLeftDockingArea());
				addDockables(DockingConstants.EAST_REGION, dockMan.getRightDockingArea());
			} else {
				addDockables(DockingConstants.WEST_REGION, dockMan.getLeftDockingArea());
				addDockables(DockingConstants.EAST_REGION, dockMan.getRightDockingArea());
				addDockables(DockingConstants.NORTH_REGION, dockMan.getTopDockingArea());
				addDockables(DockingConstants.SOUTH_REGION, dockMan.getBottomDockingArea());
			}
			return perspective;
		}
		private void addDockables(String region, PanelWindowContainer dockingArea) {
			String [] dockables = dockingArea.getDockables();
			if (dockables.length == 0)
				return;
			float split = (region.equals(DockingConstants.WEST_REGION) ||
				region.equals(DockingConstants.SOUTH_REGION)) ? 0.2f : 0.75f;
			sequence.add(dockables[0], MAIN_VIEW, region, split);
			for (int i = 1; i < dockables.length; i++)
				sequence.add(dockables[i], dockables[0]);
		}
	}
	
	private static class ViewFactory extends DockableFactory.Stub {
		
		View view;
		
		public ViewFactory(View view) {
			this.view = view;
		}
		public Component getDockableComponent(String dockableId) {
			if(MAIN_VIEW.equals(dockableId))
				return createMainView();
			return createView(dockableId);
		}
		private Component createView(String id) {
			DockableWindowManager dockMan = view.getDockableWindowManager();
			JComponent c = getJEditDockable(id, dockMan);
			String title = dockMan.getDockableTitle(id);
			Dockable d = DockableComponentWrapper.create(c, id, title);
			DockingManager.registerDockable(d);
			return c;
		}
		
		private org.flexdock.view.View createMainView() {
			org.flexdock.view.View mainView =
				new org.flexdock.view.View(MAIN_VIEW, null, null);

			//mainView.setTerritoryBlocked(DockingConstants.CENTER_REGION, true);
			mainView.setTitlebar(null);
			mainView.setContentPane(editPane);
			return mainView;
		}
		private JComponent getJEditDockable(String id, DockableWindowManager dockMan) {
			JComponent c = dockMan.getDockable(id);
			if (c == null) {
				dockMan.showDockableWindow(id);
				c = dockMan.getDockable(id);
			}
			dockMan.hideDockableWindow(id);
			return c;
		}
		
	}
	
}
