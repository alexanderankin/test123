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

import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTRoot;

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
		VPTGroup parent = VPTRoot.getInstance();
		VPTNode selected = null;
		String lookupPath = null;

		VPTProject proj = null;
		String oldName = null;
		String oldRoot = null;

		if (viewer == null) {
			viewer = ProjectViewer.getViewer(jEdit.getActiveView());
		}
		if (viewer != null) {
			VPTNode sel = viewer.getSelectedNode();
			if (sel != null && sel.isGroup()) {
				parent = (VPTGroup) sel;
			}
		}

		if (!forceNew) {
			if (viewer != null) {
				selected = viewer.getSelectedNode();
				if (selected != null)
					lookupPath = selected.getNodePath();
				if (selected == null || !selected.isProject()) {
					selected = viewer.getRoot();
					if (lookupPath == null && selected.isProject())
						lookupPath = selected.getNodePath();
				}
				proj = VPTNode.findProjectFor(selected);
			} else {
				proj = ProjectViewer.getActiveProject(jEdit.getActiveView());
			}
			if (proj != null) {
				oldName = proj.getName();
				oldRoot = proj.getRootPath();
			}
		}

		boolean add = forceNew | (proj == null);
		proj = ProjectOptions.run(proj, lookupPath);

		if (proj != null) {
			if (add) {
				ProjectManager.getInstance().addProject(proj, parent);
				RootImporter ipi = new RootImporter(proj, null, viewer, jEdit.getActiveView());
				ipi.doImport();
				if (viewer != null)
					viewer.setRootNode(proj);
				else
					ProjectViewerConfig.getInstance().setLastNode(proj);
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

				boolean notify = true;
				if (viewer == null) {
					viewer = ProjectViewer.getViewer(jEdit.getActiveView());
					notify = (viewer.getRoot().isRoot()
								|| viewer.getRoot() == proj);
				}

				if (notify)
					ProjectViewer.nodeChanged(proj);
			}
		}

	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (forceNew || (node != null && (node.isGroup() || node.isProject()))) {
			cmItem.setVisible(true);
			((JMenuItem)cmItem).setText( (forceNew || node.isGroup()) ?
				jEdit.getProperty("projectviewer.action.add_project") :
				jEdit.getProperty("projectviewer.action.edit_project"));
		} else {
			cmItem.setVisible(false);
		}
	} //}}}

}

