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
package projectviewer.action;

//{{{ Imports
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.config.ProjectOptions;
//}}}

/**
 *	Action that when executed creating a new project or editing an existing
 *	one.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class EditProjectAction extends Action {

	//{{{ Constructor
	
	public EditProjectAction(ProjectViewer viewer) {
		super(viewer);
	}
	
	//}}}
	
	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return "Add Project";
	} //}}}
	
	//{{{ getIcon() method
	/**
	 *	Returns the icon to be shown on the toolbar button. The default
	 *	implementation returns "null" so that actions that will only be
	 *	used in the context menu don't need to implement this.
	 */
	public Icon getIcon() {
		return GUIUtilities.loadIcon("Drive.png");
	} //}}}
	
	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		VPTNode selected = viewer.getSelectedNode();
		VPTProject proj = null;
		boolean add = false;
		if (selected != null && selected.isProject()) {
			proj = (VPTProject) selected;
		} else {
			add = true;
		}
		proj = ProjectOptions.run(proj);
		if (proj != null && proj != selected) {
			viewer.setProject(proj);
		}
		if (proj != null && add) {
			ProjectManager.getInstance().addProject(proj);
		}
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (tbButton != null) {
			if (node != null) {
				tbButton.setToolTipText( node.isProject() ? "Edit Project" : "Add Project" );
			} else {
				tbButton.setEnabled(false);
			}
		}
		if (cmItem != null) {
			if (node != null) {
				cmItem.setVisible( node.isRoot() || node.isProject() );
				((JMenuItem)cmItem).setText( node.isRoot() ? "Add Project" : "Edit Project" );
			} else {
				cmItem.setVisible(false);
			}
		}
	} //}}}
	
}

