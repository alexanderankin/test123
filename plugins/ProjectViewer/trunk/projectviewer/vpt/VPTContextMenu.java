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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JTree;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.PVActions;
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

import projectviewer.config.AppLauncher;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	A handler for context menu requests on the tree, providing node-sensitive
 *	functionality.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTContextMenu extends MouseAdapter {

	//{{{ Static Members

	private static final ArrayList actions = new ArrayList();
	private static final ArrayList intActions = new ArrayList();
	private static long lastMod = System.currentTimeMillis();

	/** Initializes the internal action list. */
	static {
		intActions.add(new EditProjectAction());
		intActions.add(new EditGroupAction(true));
		intActions.add(new EditGroupAction(false));
		intActions.add(new FileImportAction());
		intActions.add(new VFSFileImportAction());
		intActions.add(new ReimportAction());
		intActions.add(new NodeRemoverAction(false));
		intActions.add(new NodeRemoverAction(true));
		intActions.add(new NodeRenamerAction());
		intActions.add(new MoveNodeAction());
		intActions.add(new LaunchBrowserAction());
		intActions.add(new OpenWithAppAction());
		intActions.add(new OpenWithEncodingAction());
		intActions.add(new SearchAction());
		intActions.add(new ArchiveAction());
	}

	//{{{ +_registerAction(Action)_ : void
	/**
	 *	Adds an action to be shown on the context menu. Actions are shown in the
	 *	same order as they are registered.
	 */
	public static void registerAction(Action action) {
		actions.add(action);
		sortMenu();
		lastMod = System.currentTimeMillis();
	} //}}}

	//{{{ +_unregisterAction(Action)_ : void
	/** Removes an action from the context menu. */
	public static void unregisterAction(Action action) {
		actions.remove(action);
		lastMod = System.currentTimeMillis();
	} //}}}

	//{{{ +_unregisterActions(PluginJAR)_ : void
	/** Removes all actions from the given plugin. */
	public static void unregisterActions(PluginJAR jar) {
		boolean removed = false;
		for (Iterator i = actions.iterator(); i.hasNext(); ) {
			Object o = i.next();
			if (o.getClass().getClassLoader() == jar.getClassLoader()) {
				i.remove();
				removed = true;
			}
		}
		if (removed) {
			lastMod = System.currentTimeMillis();
		}
	} //}}}

	//{{{ +_registerActions(PluginJAR)_ : void
	/** Registers actions from the given plugin. */
	public static void registerActions(PluginJAR jar) {
		if (jar.getPlugin() == null) return;
		String list = jEdit.getProperty("plugin.projectviewer." +
							jar.getPlugin().getClassName() + ".context-menu-actions");
		boolean added = false;
		Collection aList = PVActions.listToObjectCollection(list, jar, Action.class);
		if (aList != null && aList.size() > 0) {
			actions.addAll(aList);
			sortMenu();
			lastMod = System.currentTimeMillis();
		}
	} //}}}

	//{{{ +_userMenuChanged()_ : void
	/** Updates "lastMod" so that the menu is rebuilt at the next invocation. */
	public static void userMenuChanged() {
		lastMod = System.currentTimeMillis();
	} //}}}

	//{{{ -_sortMenu()_ : void
	/** Sorts the menu in alphabetical order. */
	private static void sortMenu() {
		class ActionComparer implements Comparator {

			public int compare(Object o1, Object o2) {
				return ((Action)o1).getText().compareTo(
							((Action)o2).getText());
			}

		}
		Collections.sort(actions, new ActionComparer());
	} //}}}

	//}}}

	//{{{ Instance Variables
	private final ProjectViewer viewer;
	private final AppLauncher appList;
	private final JPopupMenu popupMenu;
	private final ArrayList internalActions;
	private long pmLastBuilt;
	//}}}

	//{{{ +VPTContextMenu(ProjectViewer) : <init>

	/**
	 *  Constructs a listener that will ask the provided viewer instance for
	 *  information about the nodes clicked.
	 */
	public VPTContextMenu(ProjectViewer viewer) {
		this.viewer = viewer;
		appList = AppLauncher.getInstance();
		internalActions = new ArrayList();
		popupMenu = new JPopupMenu();
		loadGUI();
	}

	//}}}

	//{{{ Event Handling

	//{{{ +mousePressed(MouseEvent) : void
	/** Context-menus are shown on the "pressed" event. */
	public void mousePressed(MouseEvent me) {
		handleMouseEvent(me);
	} //}}}

	//{{{ +mouseReleased(MouseEvent) : void
	/** Context-menus are shown on the "pressed" event. */
	public void mouseReleased(MouseEvent me) {
		handleMouseEvent(me);
	} //}}}

	//}}}

	//{{{ Private Methods

	//{{{ -handleMouseEvent(MouseEvent) : void
	/** Handles the mouse event internally. */
	private void handleMouseEvent(MouseEvent me) {
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
				prepareMenu( tree.getSelectionCount() > 1 ? null : viewer.getSelectedNode() );
				popupMenu.show(me.getComponent(), me.getX(), me.getY());
			}
		}
	} //}}}

	//{{{ -loadGUI() : void
	/** Constructs the menus' GUI. */
	private void loadGUI() {
		internalActions.clear();
		popupMenu.removeAll();

		Action a;

		for (Iterator it = intActions.iterator(); it.hasNext(); ) {
			a = (Action) it.next();
			a = (Action) a.clone();
			a.setViewer(viewer);
			internalActions.add(a);
			popupMenu.add(a.getMenuItem());

			// hacks to add some separators to the menu...
			if (a instanceof EditProjectAction) {
				ActionSeparator as = new ActionSeparator();
				as.setLinkedAction(a);
				as.setViewer(viewer);
				internalActions.add(as);
				popupMenu.add(as.getMenuItem());
			}

			if (a instanceof NodeRenamerAction) {
				ActionSeparator as = new ActionSeparator();
				as.setViewer(viewer);
				internalActions.add(as);
				popupMenu.add(as.getMenuItem());
			}

		}

		String menu = ProjectViewerConfig.getInstance().getUserContextMenu();
		if (menu != null) {
			jEdit.setTemporaryProperty("projectviewer.tmp_menu", menu);
			JPopupMenu pm = GUIUtilities.loadPopupMenu("projectviewer.tmp_menu");
			Component[] userActions = pm.getComponents();
			if (userActions != null && userActions.length > 0) {
				popupMenu.addSeparator();
				for (int i = 0; i < userActions.length; i++) {
					popupMenu.add(userActions[i]);
				}
			}
		}

		if (actions.size() > 0) {
			popupMenu.addSeparator();
			for (Iterator it = actions.iterator(); it.hasNext(); ) {
				a = (Action) it.next();
				a = (Action) a.clone();
				a.setViewer(viewer);
				internalActions.add(a);
				popupMenu.add(a.getMenuItem());
			}
		}

		pmLastBuilt = System.currentTimeMillis();
	} //}}}

	//{{{ -prepareMenu(VPTNode) : void
	/**
	 *	Prepares the context menu for the given node. Shows only menu items
	 *	that are allowed for the node (e.g., "Add Project" only applies for
	 *	the root node). If the node is null, the method guesses that multiple
	 *	nodes are selected, and chooses the appropriate entries.
	 */
	private void prepareMenu(VPTNode selectedNode) {

		if (pmLastBuilt < lastMod) {
			loadGUI();
		}

		for (Iterator it = internalActions.iterator(); it.hasNext(); ) {
			((Action)it.next()).prepareForNode(selectedNode);
		}

	} //}}}

	//}}}

}

