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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;


public class Plugin extends EditPlugin {
	public static final String NAME = "Flexdock";
	private static View view;
	
	public static void doStart(View view) {
		Plugin.view = view;
		Container c = view.getContentPane();
		JPanel dockPanel = new JPanel(new BorderLayout());
 		DockableWindowManager dockMan = view.getDockableWindowManager();
 		DockingManager.setSingleTabsAllowed(true);
		DefaultDockingPort topPort = createDockingPort(100, dockMan.getTopDockingArea().getDimension());
		dockPanel.add(topPort, BorderLayout.NORTH);
		/*DefaultDockingPort bottomPort = createDockingPort(100, dockMan.getBottomDockingArea().getDimension());
		dockPanel.add(bottomPort, BorderLayout.SOUTH);
		*/
		DefaultDockingPort leftPort = createDockingPort(dockMan.getLeftDockingArea().getDimension(), 100);
		dockPanel.add(leftPort, BorderLayout.WEST);
		DefaultDockingPort rightPort = createDockingPort(dockMan.getRightDockingArea().getDimension(), 100);
		dockPanel.add(rightPort, BorderLayout.EAST);
		dockPanel.add(BorderLayout.CENTER, c);
		view.setContentPane(dockPanel);
		convertDockables(topPort, dockMan.getTopDockingArea());
		//convertDockables(bottomPort, dockMan.getBottomDockingArea());
		convertDockables(leftPort, dockMan.getLeftDockingArea());
		convertDockables(rightPort, dockMan.getRightDockingArea());
		dockMan.close();
	}
	private static void convertDockables(DefaultDockingPort port,
			PanelWindowContainer dockingArea) {
 		DockableWindowManager dockMan = view.getDockableWindowManager();
		String [] dockables = dockingArea.getDockables(); 
		for (int i = 0; i < dockables.length; i++) {
			String name = dockables[i];
			String title = dockMan.getDockableTitle(name);
			JComponent dockable = createDockableComponent(name, title);
			connectToDockingPort(dockable, port);
		}
	}
	private static void connectToDockingPort(JComponent component, DefaultDockingPort port) {
		port.dock(component, DockingConstants.CENTER_REGION);
	}
	private static DefaultDockingPort createDockingPort(int w, int h) {
		DefaultDockingPort port = new DefaultDockingPort();
		port.setSingleTabAllowed(true);
		port.setPreferredSize(new Dimension(w, h));
		return port;
	}

	private static JComponent createDockableComponent(String name, String title) {
		JComponent c = view.getDockableWindowManager().getDockable(name);
		if (c == null) {
			view.getDockableWindowManager().showDockableWindow(name);
			c = view.getDockableWindowManager().getDockable(name);
		}
		view.getDockableWindowManager().hideDockableWindow(name);
		DockingManager.registerDockable(c, title);
		return c;
	}
}
