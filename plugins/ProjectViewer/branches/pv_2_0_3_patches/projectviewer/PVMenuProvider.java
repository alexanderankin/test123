/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

//{{{ Imports
import java.util.Iterator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;

import projectviewer.config.ProjectViewerConfig;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	Dynamic menu implementation for jEdit 4.2. This shows a menu with all the
 *	projects in jEdit's plugin menu, allowing the user to change the active
 *	project without having to go to the PV dockable. Since jEdit doesn't allow
 *	dynamic sub-menus, this has to take care of the static entries also.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public final class PVMenuProvider implements DynamicMenuProvider,
												ActionListener {

	//{{{ Private members
	private View view;
	//}}}

	//{{{ +ProjectsMenuProvider(View) : <init>
	/** Makes sure ProjectManager is initialized. */
	public PVMenuProvider() {
		ProjectManager.getInstance();
		this.view = jEdit.getActiveView();
	} //}}}

	//{{{ +update(JMenu) : void
	public void update(JMenu menu) {
		JMenu pMenu;
		if (menu.getItemCount() == 0) {
			pMenu = new JMenu(jEdit.getProperty("projectviewer_projects_menu.label"));

			menu.add(pMenu);
			Component[] others = GUIUtilities.loadMenu("projectviewer.menu")
									.getMenuComponents();
			for (int i = 0; i < others.length; i++) {
				menu.add(others[i]);
			}
		} else {
			pMenu = (JMenu) menu.getMenuComponent(0);
			pMenu.removeAll();
		}
		for (Iterator i = ProjectManager.getInstance().getProjects(); i.hasNext(); ) {
			VPTProject p = (VPTProject) i.next();
			JMenuItem mi = new JMenuItem(p.getName());
			mi.addActionListener(this);
			pMenu.add(mi);
		}
	} //}}}

	//{{{ +updateEveryTime() : boolean
	/** We don't want to update every time; returns false. */
	public boolean updateEveryTime() {
		return false;
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		JMenuItem mi = (JMenuItem) ae.getSource();
		VPTProject p = ProjectManager.getInstance().getProject(mi.getText());
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer != null && p != null) {
			viewer.setProject(p);
		} else {
			ProjectViewerConfig.getInstance().setLastProject(p.getName());
		}
	} //}}}

}

