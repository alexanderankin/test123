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
import javax.swing.JOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.config.ProjectOptions;
import projectviewer.config.ProjectViewerConfig;
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
		VPTNode selected = null;
		String lookupPath = null;
		VPTProject proj = null;

		if (viewer != null) {
			selected = viewer.getSelectedNode();
			if (selected != null)
				lookupPath = selected.getNodePath();
			if (selected == null || !selected.isProject()) {
				selected = viewer.getRoot();
				if (lookupPath == null && selected.isProject())
					lookupPath = selected.getNodePath();
			}
		} else if (!forceNew) {
			proj = ProjectViewer.getActiveProject(jEdit.getActiveView());
		}

		boolean add = false;
		String oldName = null;
		String oldRoot = null;
		if (!forceNew && selected != null && !selected.isRoot()) {
			proj = VPTNode.findProjectFor(selected);
			oldName = proj.getName();
			oldRoot = proj.getRootPath();
		} else if (proj != null) {
			add = true;
		}

		proj = ProjectOptions.run(proj, lookupPath);
		if (proj != null) {
			if (add) {
				ProjectManager.getInstance().addProject(proj);
				RootImporter ipi = new RootImporter(proj, null, viewer, jEdit.getActiveView());
				ipi.doImport();
				if (viewer != null)
					viewer.setProject(proj);
				else
					ProjectViewerConfig.getInstance().setLastProject(proj.getName());
			} else {
				if (!proj.getName().equals(oldName)) {
					ProjectManager.getInstance().renameProject(oldName, proj.getName());
				}
				if (!proj.getRootPath().equals(oldRoot)) {
					RootImporter ipi;
					if (JOptionPane.showConfirmDialog(jEdit.getActiveView(),
							jEdit.getProperty("projectviewer.action.clean_old_root"),
							jEdit.getProperty("projectviewer.action.clean_old_root.title"),
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						ipi = new RootImporter(proj, oldRoot, viewer, jEdit.getActiveView());
					} else {
						ipi = new RootImporter(proj, null, viewer, jEdit.getActiveView());
					}
					ipi.doImport();
				}
				proj.firePropertiesChanged();
				viewer.repaint();
			}
		}

	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (forceNew || (node != null && (node.isRoot() || node.isProject()))) {
			cmItem.setVisible(true);
			((JMenuItem)cmItem).setText( (forceNew || node.isRoot()) ?
				jEdit.getProperty("projectviewer.action.add_project") :
				jEdit.getProperty("projectviewer.action.edit_project"));
		} else {
			cmItem.setVisible(false);
		}
	} //}}}

}

