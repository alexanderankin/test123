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

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.config.ProjectOptions;
import projectviewer.importer.RootImporter;
//}}}

/**
 *	Action that when executed creates a new project or edits an existing one.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class EditProjectAction extends Action {

	//{{{ Private members
	private boolean forceNew;
	//}}}

	//{{{ Constructors

	/** Default constructor. */
	public EditProjectAction() {
		this(false);
	}

	/** If forceNew is true, creation of new project will be forced. */
	public EditProjectAction(boolean forceNew) {
		this.forceNew = forceNew;
	}

	//}}}

	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.a_e_project");
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
		if (selected == null || !selected.isProject())
			selected  = viewer.getRoot();

		VPTProject proj = null;
		boolean add = false;
		String oldName = null;
		String oldRoot = null;
		if (!forceNew && selected != null && !selected.isRoot()) {
			proj = VPTNode.findProjectFor(selected);
			oldName = proj.getName();
			oldRoot = proj.getRootPath();
		} else {
			add = true;
		}

		proj = ProjectOptions.run(proj);
		if (proj != null) {
			if (add) {
				ProjectManager.getInstance().addProject(proj);
				RootImporter ipi = new RootImporter(proj, viewer, jEdit.getActiveView());
				ipi.doImport();
				viewer.setProject(proj);
			} else {
				if (!proj.getName().equals(oldName)) {
					ProjectManager.getInstance().renameProject(oldName, proj.getName());
				}
				if (!proj.getRootPath().equals(oldRoot)) {
					RootImporter ipi = new RootImporter(proj, viewer, jEdit.getActiveView());
					ipi.doImport();
				}
				proj.firePropertiesChanged();
			}
		}

	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (node != null && (node.isRoot() || node.isProject())) {
			cmItem.setVisible(true);
			((JMenuItem)cmItem).setText( node.isRoot() ?
				jEdit.getProperty("projectviewer.action.add_project") :
				jEdit.getProperty("projectviewer.action.edit_project"));
		} else {
			cmItem.setVisible(false);
		}
	} //}}}

}

