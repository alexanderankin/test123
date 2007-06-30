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
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableLayout;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;


public class Plugin extends EditPlugin {
	public static final String NAME = "Flexdock";
	static DefaultDockingPort mainPort = null;
	private static Container mainContainer;
	
	public static void doStart(View view) {
		mainContainer = view.getContentPane();
 		DockableWindowManager dockMan = view.getDockableWindowManager();
 		DockingManager.setSingleTabsAllowed(true);
		mainPort = createDockingPort(100, 100);
		mainPort.setSingleTabAllowed(false);
		DockingManager.registerDockable(mainContainer, "Main");
		DockingManager.dock(mainContainer, (DockingPort)mainPort);
		boolean alternateLayout = ((DockableLayout)dockMan.getLayout()).setAlternateLayout();
		if (alternateLayout) {
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
	}
	private static void convertDockables(View view, String region,
			PanelWindowContainer dockingArea) {
 		DockableWindowManager dockMan = view.getDockableWindowManager();
		String [] dockables = dockingArea.getDockables();
		JComponent first = null;
		for (int i = 0; i < dockables.length; i++) {
			String name = dockables[i];
			String title = dockMan.getDockableTitle(name);
			JComponent dockable = createDockableComponent(view, name, title);
			if (i == 0) {
				first = dockable;
				DockingManager.dock(first, mainContainer, region);
				float split;
				if (region.equals(DockingConstants.WEST_REGION) ||
						region.equals(DockingConstants.NORTH_REGION))
					split = 0.2f;
				else
					split = 0.75f;
				DockingManager.setSplitProportion(first, split);
			}
			else
				DockingManager.dock(dockable, first);
		}
	}
	private static DefaultDockingPort createDockingPort(int w, int h) {
		DefaultDockingPort port = new DefaultDockingPort();
		port.setSingleTabAllowed(true);
		port.setPreferredSize(new Dimension(w, h));
		return port;
	}

	private static JComponent createDockableComponent(View view,
			String name, String title) {
		JPanel p = new JPanel(new BorderLayout());
		DockableWindowManager dockMan = view.getDockableWindowManager();
		JComponent c = dockMan.getDockable(name);
		if (c == null) {
			dockMan.showDockableWindow(name);
			c = dockMan.getDockable(name);
		}
		p.add(c, BorderLayout.CENTER);
		dockMan.hideDockableWindow(name);
		DockingManager.registerDockable(p, title);
		return p;
	}
}
