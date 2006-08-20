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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;

import projectviewer.gui.GroupMenu;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
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

	//{{{ +PVMenuProvider() : <init>
	/** Makes sure ProjectManager is initialized. */
	public PVMenuProvider() {
		ProjectManager.getInstance();
		this.view = jEdit.getActiveView();
	} //}}}

	//{{{ +update(JMenu) : void
	public void update(JMenu menu) {
		GroupMenu pMenu;
		if (menu.getItemCount() == 0) {
			pMenu = new GroupMenu(jEdit.getProperty("projectviewer_projects_menu.label"),
						true, true, this);

			menu.add(pMenu);
			Component[] others = GUIUtilities.loadMenu("projectviewer.menu")
									.getMenuComponents();
			for (int i = 0; i < others.length; i++) {
				menu.add(others[i]);
			}
		} else {
			pMenu = (GroupMenu) menu.getMenuComponent(0);
			pMenu.removeAll();
		}

		pMenu.populate(pMenu, VPTRoot.getInstance(), view);
	} //}}}

	//{{{ +updateEveryTime() : boolean
	/** We don't want to update every time; returns false. */
	public boolean updateEveryTime() {
		return false;
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		VPTNode n = (VPTNode) ae.getSource();

		if (n.isProject() && !ProjectManager.getInstance().isLoaded(n.getName())) {
			ProjectManager.getInstance().getProject(n.getName());
		}

		ProjectViewer pv = ProjectViewer.getViewer(view);
		if (pv != null) {
			pv.setRootNode(n);
		} else {
			ProjectViewer.setActiveNode(view, n);
		}
	} //}}}

}

