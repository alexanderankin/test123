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
package projectviewer.vpt;

//{{{ Imports
import java.util.ArrayList;
import java.util.List;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JTree;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.ProjectViewer;

import projectviewer.action.Action;
import projectviewer.action.SearchAction;
import projectviewer.action.ArchiveAction;
import projectviewer.action.ReimportAction;
import projectviewer.action.ActionSeparator;
import projectviewer.action.FileImportAction;
import projectviewer.action.EditGroupAction;
import projectviewer.action.EditProjectAction;
import projectviewer.action.MoveNodeAction;
import projectviewer.action.NodeRemoverAction;
import projectviewer.action.NodeRenamerAction;
import projectviewer.action.OpenWithAppAction;
import projectviewer.action.LaunchBrowserAction;
import projectviewer.action.VFSFileImportAction;
import projectviewer.action.OpenWithEncodingAction;

import projectviewer.config.ExtensionManager;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	A handler for context menu requests on the tree, providing node-sensitive
 *	functionality.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTContextMenu extends MouseAdapter
							implements ExtensionManager.ManagedService
{

	private final ProjectViewer viewer;
	private final JPopupMenu	popupMenu;
	private final List<Action>	intActions;
	private final List<Action>	extActions;
	private final List<Action>	separators;

	/**
	 *  Constructs a listener that will ask the provided viewer instance for
	 *  information about the nodes clicked.
	 */
	public VPTContextMenu(ProjectViewer viewer)
	{
		this.viewer = viewer;
		intActions = new ArrayList<Action>();
		extActions = new ArrayList<Action>();
		separators = new ArrayList<Action>();
		popupMenu = new JPopupMenu();

		addAction(new EditProjectAction(), true);
		addAction(new EditGroupAction(true), false);
		addAction(new EditGroupAction(false), true);
		addAction(new FileImportAction(), false);
		addAction(new VFSFileImportAction(), false);
		addAction(new ReimportAction(), false);
		addAction(new NodeRemoverAction(false), false);
		addAction(new NodeRemoverAction(true), false);
		addAction(new NodeRenamerAction(), true);
		addAction(new MoveNodeAction(), false);
		addAction(new LaunchBrowserAction(), false);
		addAction(new OpenWithAppAction(), false);
		addAction(new OpenWithEncodingAction(), false);
		addAction(new SearchAction(), false);
		addAction(new ArchiveAction(), false);

		/* Separator for the list of external actions added to the menu. */
		ActionSeparator sep = new ActionSeparator();
		separators.add(sep);
		sep.setLinkedActions(extActions);
		popupMenu.add(sep.getMenuItem());

		ExtensionManager.getInstance().register(this);
	}

	/** Context-menus are shown on the "pressed" event. */
	public void mousePressed(MouseEvent me)
	{
		handleMouseEvent(me);
	}

	/** Context-menus are shown on the "pressed" event. */
	public void mouseReleased(MouseEvent me)
	{
		handleMouseEvent(me);
	}

	/** Handles the mouse event internally. */
	private void handleMouseEvent(MouseEvent me)
	{
		if (!viewer.isEnabled())
			return;
		if (me.isPopupTrigger()) {
			JTree tree = viewer.getCurrentTree();
			TreePath tp = tree.getClosestPathForLocation(me.getX(),me.getY());
			if (tp != null && !tree.isPathSelected(tp)) {
				if ((me.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
					tree.addSelectionPath(tp);
				} else {
					tree.setSelectionPath(tp);
				}
			}
			if (tree.getSelectionCount() != 0) {
				VPTNode n = tree.getSelectionCount() > 1 ? null : viewer.getSelectedNode();
				for (Action a : intActions) {
					a.prepareForNode(n);
				}
				for (Action a : extActions) {
					a.prepareForNode(n);
				}
				for (Action a : separators) {
					a.prepareForNode(n);
				}
				popupMenu.show(me.getComponent(), me.getX(), me.getY());
			}
		}
	}

	/**
	 *	Called when the user context menu (or a config option related to
	 *	that menu) has changed.
	 */
	public void userMenuChanged()
	{
		updateExtensions(new ArrayList(extActions));
	}

	/**
	 *	ManagedService implementation.
	 *
	 *	@since PV 3.0.0
	 */
	public Class getServiceClass()
	{
		return Action.class;
	}

	/**
	 *	ManagedService implementation.
	 *
	 *	@since PV 3.0.0
	 */
	public void updateExtensions(List<Object> l)
	{
		ProjectViewerConfig cfg = ProjectViewerConfig.getInstance();

		extActions.clear();
		popupMenu.removeAll();

		for (Action a : intActions) {
			popupMenu.add(a.getMenuItem());
		}

		if (l != null && l.size() > 0) {
			for (Object _a : l) {
				Action a = (Action) _a;
				a = (Action) a.clone();
				a.setViewer(viewer);
				extActions.add(a);
				popupMenu.add(a.getMenuItem());
			}
		}

		/* Insert the user-defined context menu items into the popup menu. */
		String menu = ProjectViewerConfig.getInstance().getUserContextMenu();
		if (menu != null) {
			jEdit.setTemporaryProperty("projectviewer.tmp_menu", menu);
			JPopupMenu userMenu = GUIUtilities.loadPopupMenu("projectviewer.tmp_menu");
			jEdit.unsetProperty("projectviewer.tmp_menu");
			Component[] userActions = userMenu.getComponents();

			if (userActions != null && userActions.length > 0) {
				int i = 0;

				if (!cfg.getUserMenuFirst()) {
					popupMenu.addSeparator();
				}

				for (i = 0; i < userActions.length; i++) {
					if (cfg.getUserMenuFirst()) {
						popupMenu.insert(userActions[i], i);
					} else {
						popupMenu.add(userActions[i]);
					}
				}

				if (cfg.getUserMenuFirst()) {
					popupMenu.insert(new JPopupMenu.Separator(), i++);
				}
			}
		}

	}

	private void addAction(Action a, boolean addSep)
	{
		a.setViewer(viewer);
 		intActions.add(a);

		if (addSep) {
			ActionSeparator as = new ActionSeparator();
			as.setLinkedAction(a);
			as.setViewer(viewer);
			intActions.add(as);
			separators.add(as);
		}
	}

}

