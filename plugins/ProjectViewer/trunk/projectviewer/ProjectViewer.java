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
import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;

import java.awt.event.KeyEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.awt.dnd.DragSource;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
import org.gjt.sp.jedit.msg.EditorExiting;
import org.gjt.sp.jedit.msg.EditorExitRequested;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;

import errorlist.ErrorSource;
import errorlist.ErrorSourceUpdate;

import projectviewer.gui.ProjectComboBox;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTContextMenu;
import projectviewer.vpt.VPTCellRenderer;
import projectviewer.vpt.VPTFileListModel;
import projectviewer.vpt.VPTSelectionListener;
import projectviewer.vpt.VPTWorkingFileListModel;
import projectviewer.vpt.VPTCompactModel;

import projectviewer.event.ProjectListener;
import projectviewer.event.ProjectViewerEvent;
import projectviewer.event.ProjectViewerListener;

import projectviewer.action.Action;
import projectviewer.action.ExpandAllAction;
import projectviewer.action.CollapseAllAction;
import projectviewer.action.EditProjectAction;
import projectviewer.action.OldStyleAddFileAction;
import projectviewer.config.ProjectViewerConfig;
import projectviewer.importer.NewFileImporter;
//}}}

/**
 *  Main GUI for the project viewer plugin.
 *
 *	@author		Marcelo Vanzin (with much code from original version)
 *	@version    $Id$
 */
public final class ProjectViewer extends JPanel
									implements HierarchyListener,
												EBComponent {

	//{{{ Static members

	private static final ProjectViewerConfig config = ProjectViewerConfig.getInstance();
	private static final HashMap viewers		= new HashMap();
	private static final HashMap listeners		= new HashMap();
	private static final ArrayList actions		= new ArrayList();

	//{{{ Static Initialization
	/**
	 *	Initializes the default actions, and gets the PV plugins from the list
	 *	of active jEdit plugins.
	 */
	static {
		// Default toolbar actions
		actions.add(new EditProjectAction());
		actions.add(new ExpandAllAction());
		actions.add(new CollapseAllAction());
		actions.add(new OldStyleAddFileAction());
	} //}}}

	//{{{ Action Handling

	//{{{ +_registerAction(Action)_ : void
	/** Adds an action to be shown on the toolbar. */
	public static void registerAction(Action action) {
		actions.add(action);
		actionsChanged();
	} //}}}

	//{{{ +_unregisterAction(Action)_ : void
	/** Removes an action from the toolbar. */
	public static void unregisterAction(Action action) {
		actions.remove(action);
		actionsChanged();
	} //}}}

	//{{{ +_removeToolbarActions(PluginJAR)_ : void
	/**
	 *	Removes the project listeners of the given plugin from the list, and
	 *	from any active project in ProjectViewer.
	 */
	public static void removeToolbarActions(PluginJAR jar) {
		boolean removed = false;
		for (Iterator i = actions.iterator(); i.hasNext(); ) {
			Object o = i.next();
			if (o.getClass().getClassLoader() == jar.getClassLoader()) {
				i.remove();
				removed = true;
			}
		}
		if (removed) {
			actionsChanged();
		}
	} //}}}

	//{{{ +_addToolbarActions(PluginJAR)_ : void
	/**
	 *	Adds to the list of listeners for the given view the listeners that
	 *	have been declared by the given plugin using properties. For global
	 *	listeners, "view" should be null.
	 */
	public static void addToolbarActions(PluginJAR jar) {
		if (jar.getPlugin() == null) return;
		String list = jEdit.getProperty("plugin.projectviewer." +
							jar.getPlugin().getClassName() + ".toolbar-actions");
		Collection aList = PVActions.listToObjectCollection(list, jar, Action.class);
		if (aList != null && aList.size() > 0) {
			actions.addAll(aList);
			actionsChanged();
		}
	} //}}}

	//{{{ -_actionsChanged()_ : void
	/** Reloads the action list for the toolbar. */
	private static void actionsChanged() {
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ViewerEntry ve = (ViewerEntry) it.next();
			ProjectViewer v = ve.dockable;
			if (v != null && v.toolBar != null)
				v.populateToolBar();
		}
	} //}}}

	//}}}

	//{{{ +_getViewer(View)_ : ProjectViewer
	/**
	 *	Returns the viewer associated with the given view, or null if none
	 *	exists.
	 */
	public static ProjectViewer getViewer(View view) {
		ViewerEntry ve = (ViewerEntry) viewers.get(view);
		if (ve != null)
			return ve.dockable;
		return null;
	} //}}}

	//{{{ Event Handling

	//{{{ +_addProjectViewerListener(ProjectViewerListener, View)_ : void
	/**
	 *	Add a listener for the instance of project viewer of the given
	 *	view. If the given view is null, the listener will be called from
	 *	all instances.
	 *
	 *	<p>Additionally, for listeners that are registered for all views, a
	 *	ProjectViewerEvent is fired when a different view is selected.</p>
	 *
	 *	@param	lstnr	The listener to add.
	 *	@param	view	The view that the lstnr is attached to, or <code>null</code>
	 *					if the listener wants to be called from all views.
	 */
	public static void addProjectViewerListener(ProjectViewerListener lstnr, View view) {
		ArrayList lst = (ArrayList) listeners.get(view);
		if (lst == null) {
			lst = new ArrayList();
			listeners.put(view, lst);
		}
		lst.add(lstnr);
	} //}}}

	//{{{ +_removeProjectViewerListener(ProjectViewerListener, View)_ : void
	/**
	 *	Remove the listener from the list of listeners for the given view. As
	 *	with the {@link #addProjectViewerListener(ProjectViewerListener, View) add}
	 *	method, <code>view</code> can be <code>null</code>.
	 */
	public static void removeProjectViewerListener(ProjectViewerListener lstnr, View view) {
		ArrayList lst = (ArrayList) listeners.get(view);
		if (lst != null) {
			lst.remove(lstnr);
		}
	} //}}}

	//{{{ +_fireProjectLoaded(Object, VPTProject, View)_ : void
	/**
	 *	Fires an event for the loading of a project. Notify all the listeners
	 *	registered for the given view and listeners registered for all
	 *	views.
	 *
	 *	<p>If the view provided is null, only the listeners registered for the
	 *	null View will receive the event.</p>
	 *
	 *	@param	src		The viewer that generated the change, or null.
	 *	@param	p		The activated project.
	 *	@param	v		The view where the change occured, or null.
	 */
	public static void fireProjectLoaded(Object src, VPTProject p, View v) {
		ProjectViewerEvent evt;
		if (src instanceof ProjectViewer) {
			evt = new ProjectViewerEvent((ProjectViewer) src, p);
		} else {
			ProjectViewer viewer = getViewer(v);
			if (viewer != null) {
				viewer.setRootNode(p);
				return;
			}
			evt = new ProjectViewerEvent(src, p);
		}

		Set listeners = getAllListeners(v);
		for (Iterator i = listeners.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).projectLoaded(evt);
		}
	} //}}}

	//{{{ +_fireGroupActivated(VPTGroup, View)_ : void
	/**
	 *	Fires an event for the loading of a group. Notify all the listeners
	 *	registered for the given view and listeners registered for all
	 *	views.
	 *
	 *	<p>If the view provided is null, only the listeners registered for the
	 *	null View will receive the event.</p>
	 *
	 *	@param	grp		The activated group.
	 *	@param	v		The view where the change occured, or null.
	 */
	public static void fireGroupActivated(VPTGroup grp, View v) {
		ProjectViewer viewer = getViewer(v);
		ProjectViewerEvent evt = new ProjectViewerEvent(grp, viewer);
		Set listeners = getAllListeners(v);
		for (Iterator i = listeners.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).groupActivated(evt);
		}
	} //}}}

	//{{{ +_fireProjectAdded(Object, VPTProject)_ : void
	/**
	 *	Fires a "project added" event. All listeners, regardless of the view, are
	 *	notified of this event.
	 */
	public static void fireProjectAdded(Object src, VPTProject p) {
		Set notify = getAllListeners(null);
		ProjectViewerEvent evt = new ProjectViewerEvent(src, p);
		for (Iterator i = notify.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).projectAdded(evt);
		}
	} //}}}

	//{{{ +_fireProjectRemoved(Object, VPTProject)_ : void
	/**
	 *	Fires a "project removed" event. All listeners, regardless of the view, are
	 *	notified of this event.
	 */
	public static void fireProjectRemoved(Object src, VPTProject p) {
		Set notify = getAllListeners(null);
		ProjectViewerEvent evt = new ProjectViewerEvent(src, p);
		for (Iterator i = notify.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).projectRemoved(evt);
		}
	} //}}}

	//{{{ +_removeProjectViewerListeners(PluginJAR)_ : void
	/**
	 *	Removes the listeners loaded by the given plugin from the listener
	 *	list. Meant to be called when said plugin is unloaded by jEdit.
	 */
	public static void removeProjectViewerListeners(PluginJAR jar) {
		for (Iterator i = listeners.values().iterator(); i.hasNext(); ) {
			if (i.next().getClass().getClassLoader() == jar.getClassLoader()) {
				i.remove();
			}
		}
	} //}}}

	//{{{ +_addProjectViewerListeners(PluginJAR, View)_ : void
	/**
	 *	Adds to the list of listeners for the given view the listeners that
	 *	have been declared by the given plugin using properties. For global
	 *	listeners, "view" should be null.
	 */
	public static void addProjectViewerListeners(PluginJAR jar, View view) {
		if (jar.getPlugin() == null) return;
		String list;
		if (view == null) {
			list = jEdit.getProperty("plugin.projectviewer." +
							jar.getPlugin().getClassName() + ".global-pv-listeners");
		} else {
			list = jEdit.getProperty("plugin.projectviewer." +
							jar.getPlugin().getClassName() + ".pv-listeners");
		}

		Collection aList = PVActions.listToObjectCollection(list, jar, ProjectViewerListener.class);
		if (aList != null && aList.size() > 0) {
			ArrayList existing = (ArrayList) listeners.get(view);
			if (existing == null) {
				listeners.put(view, aList);
			} else {
				existing.addAll(aList);
			}
		}
	} //}}}

	//{{{ +_fireNodeMovedEvent(VPTNode, VPTGroup)_ : void
	public static void fireNodeMovedEvent(VPTNode moved, VPTGroup oldParent) {
		Set notify = getAllListeners(null);
		ProjectViewerEvent pve = new ProjectViewerEvent(moved, oldParent);
		for (Iterator i = notify.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).nodeMoved(pve);
		}

	} //}}}

	//{{{ +_fireGroupAddedEvent(VPTGroup)_ : void
	public static void fireGroupAddedEvent(VPTGroup group) {
		Set notify = getAllListeners(null);
		ProjectViewerEvent pve = new ProjectViewerEvent(group);
		for (Iterator i = notify.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).groupAdded(pve);
		}
	} //}}}

	//{{{ +_fireGroupRemovedEvent(VPTGroup)_ : void
	public static void fireGroupRemovedEvent(VPTGroup group) {
		Set notify = getAllListeners(null);
		ProjectViewerEvent pve = new ProjectViewerEvent(group);
		for (Iterator i = notify.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).groupRemoved(pve);
		}
	} //}}}

	//{{{ -_getAllListeners(View)_ : Set
	/**
	 *	Returns a set of all registered ProjectViewerListeners. If a view
	 *	is provided, return only the listeners registered to that view, plus
	 *	the listeners registered globaly.
	 */
	private static Set getAllListeners(View v) {
		HashSet all = new HashSet();
		if (v == null) {
			for (Iterator i = listeners.values().iterator(); i.hasNext(); ) {
				all.addAll((ArrayList)i.next());
			}
		} else {
			Object o = listeners.get(v);
			if (o != null)
				all.addAll((ArrayList)o);
			o = listeners.get(null);
			if (o != null)
				all.addAll((ArrayList)o);
		}
		return all;
	} //}}}

	//}}}

	//{{{ Tree Changes Broadcast Methods

	//{{{ +_nodeStructureChanged(VPTNode)_ : void
	/**
	 *	Notify all project viewer instances of a change in a node's structure.
	 */
	public static void nodeStructureChanged(VPTNode node) {
		VPTProject p = VPTNode.findProjectFor(node);
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ViewerEntry ve = (ViewerEntry) it.next();
			ProjectViewer v = ve.dockable;
			if (v == null)
				continue;
			if (v.treeRoot.isNodeDescendant(node)) {
				if (v.folderTree != null) {
					((DefaultTreeModel)v.folderTree.getModel()).nodeStructureChanged(node);
				}
				if (v.fileTree != null) {
					((DefaultTreeModel)v.fileTree.getModel()).nodeStructureChanged(node);
				}

				if (v.workingFileTree != null) {
					((DefaultTreeModel)v.workingFileTree.getModel()).nodeStructureChanged(node);
				}

				if (v.compactTree != null) {
					((DefaultTreeModel)v.compactTree.getModel()).nodeStructureChanged(node);
				}
			}
		}
	} //}}}

	//{{{ +_nodeChanged(VPTNode)_ : void
	/** Notify all project viewer instances of a change in a node. */
	public static void nodeChanged(VPTNode node) {
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ViewerEntry ve = (ViewerEntry) it.next();
			ProjectViewer v = ve.dockable;
			if (v == null)
				continue;
			if (v.treeRoot.isNodeDescendant(node)) {
				if (v.folderTree != null) {
					((DefaultTreeModel)v.folderTree.getModel()).nodeChanged(node);
				}
				if (node.canOpen() || node.isProject()) {
					if (v.fileTree != null) {
						((DefaultTreeModel)v.fileTree.getModel()).nodeChanged(node);
					}

					if (v.workingFileTree != null) {
						((DefaultTreeModel)v.workingFileTree.getModel()).nodeChanged(node);
					}

					if (v.compactTree != null) {
						((DefaultTreeModel)v.compactTree.getModel()).nodeChanged(node);
					}
				}
			}
		}
	} //}}}

	//{{{ +_insertNodeInto(VPTNode, VPTNode)_ : void
	/**
	 *	Inserts a node in the given parent node (in a sorted position according
	 *	to {@link projectviewer.vpt.VPTNode#findIndexForChild(VPTNode) } and
	 *	notifies folder trees in all instances of ProjectViewer.
	 */
	public static void insertNodeInto(VPTNode child, VPTNode parent) {
		int idx = parent.findIndexForChild(child);
		parent.insert(child, idx);

		int[] ind = new int[] { idx };

		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ViewerEntry ve = (ViewerEntry) it.next();
			ProjectViewer v = ve.dockable;
			if (v == null || !v.getRoot().isNodeDescendant(parent))
				continue;
			if (v.folderTree != null) {
				((DefaultTreeModel)v.folderTree.getModel())
					.nodesWereInserted(parent, ind);
			}
			if (v.compactTree != null) {
				((DefaultTreeModel)v.compactTree.getModel())
					.nodesWereInserted(parent, ind);
			}
			if (child.isProject() || child.isGroup()) {
				if (v.fileTree != null) {
					((DefaultTreeModel)v.fileTree.getModel())
						.nodesWereInserted(parent, ind);
				}
				if (v.workingFileTree != null) {
					((DefaultTreeModel)v.workingFileTree.getModel())
						.nodesWereInserted(parent, ind);
				}
			}
		}
	} //}}}

	//{{{ +_nodeStructureChangedFlat(VPTNode)_ : void
	/**
	 *	Notify all "flat trees" in any project viewer instances of a change in
	 *	a node's structure.
	 */
	public static void nodeStructureChangedFlat(VPTNode node) {
		if (config.getShowFilesTree() || config.getShowWorkingFilesTree()) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ViewerEntry ve = (ViewerEntry) it.next();
				ProjectViewer v = ve.dockable;
				if (v == null)
					continue;
				if (v.treeRoot.isNodeDescendant(node)) {
					if (v.fileTree != null) {
						((DefaultTreeModel)v.fileTree.getModel())
							.nodeStructureChanged(node);
					}

					if (v.workingFileTree != null) {
						((DefaultTreeModel)v.workingFileTree.getModel())
							.nodeStructureChanged(node);
					}
				}
			}
		}
	} //}}}

	//{{{ +_removeNodeFromParent(VPTNode)_ : void
	/**
	 *	Removes a node from its parent, and notifies all folder trees in all
	 *	instances of ProjectViewer.
	 */
	public static void removeNodeFromParent(VPTNode child) {
		VPTNode parent = (VPTNode) child.getParent();
		int index = parent.getIndex(child);
		parent.remove(index);

		Object[] removed = new Object[] { child };
		int[] idx = new int[] { index };

		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ViewerEntry ve = (ViewerEntry) it.next();
			ProjectViewer v = ve.dockable;
			if (v == null || !v.getRoot().isNodeDescendant(parent))
				continue;
			if (v.folderTree != null) {
				((DefaultTreeModel)v.folderTree.getModel())
					.nodesWereRemoved(parent, idx, removed);
			}
			if (v.compactTree != null) {
				((DefaultTreeModel)v.compactTree.getModel())
					.nodesWereRemoved(parent, idx, removed);
			}
			if (child.isProject() || child.isGroup()) {
				if (v.fileTree != null) {
					((DefaultTreeModel)v.fileTree.getModel())
						.nodesWereRemoved(parent, idx, removed);
				}
				if (v.workingFileTree != null) {
					((DefaultTreeModel)v.workingFileTree.getModel())
						.nodesWereRemoved(parent, idx, removed);
				}
			}
		}
	} //}}}

	//{{{ +_projectRemoved(Object, VPTProject)_ : void
	/**
	 *	Notify all "flat trees" in any project viewer instances of a change in
	 *	a node's structure. Then, rebuild the project combo boxes.
	 */
	public static void projectRemoved(Object src, VPTProject p) {
		VPTNode parent = (VPTNode) p.getParent();
		int index = parent.getIndex(p);
		parent.remove(index);

		if (config.getShowFoldersTree() || config.getShowFilesTree() ||
				config.getShowWorkingFilesTree() || config.getShowCompactTree()) {

			Object[] removed = new Object[] { p };
			int[] idx = new int[] { index };

			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ViewerEntry ve = (ViewerEntry) it.next();
				ProjectViewer v = ve.dockable;
				if (v == null)
					continue;
				if (p == v.treeRoot) {
					v.setRootNode(VPTRoot.getInstance());
					continue;
				}
				if (v.treeRoot.isNodeDescendant(parent)) {
					if (v.folderTree != null) {
						((DefaultTreeModel)v.folderTree.getModel())
							.nodesWereRemoved(parent, idx, removed);
					}

					if (v.fileTree != null) {
						((DefaultTreeModel)v.fileTree.getModel())
							.nodesWereRemoved(parent, idx, removed);
					}

					if (v.workingFileTree != null) {
						((DefaultTreeModel)v.workingFileTree.getModel())
							.nodesWereRemoved(parent, idx, removed);
					}

					if (v.compactTree != null) {
						((DefaultTreeModel)v.compactTree.getModel())
							.nodesWereRemoved(parent, idx, removed);
					}
				}
			}
		}
		fireProjectRemoved(src, p);
	} //}}}

	//}}}

	//{{{ +_setActiveNode(View, VPTNode)_ : void
	/**
	 *	Sets the current active node for the view. If a viewer is
	 *	available for the given view, the root node of the viewer
	 *	is also changed.
	 *
	 *	@throws IllegalArgumentException If node is not a project or group.
	 *	@since PV 2.1.0
	 */
	public static void setActiveNode(View aView, VPTNode n) {
		if (!n.isGroup() && !n.isProject()) {
			throw new IllegalArgumentException("PV can only use Projects and Groups as root.");
		}

		ViewerEntry ve = (ViewerEntry) viewers.get(aView);
		if (ve == null) {
			ve = new ViewerEntry();
			ve.node = n;
			viewers.put(aView, ve);
		} else {
			if (n == ve.node)
				return;
			if (ve.dockable != null) {
				ve.dockable.setRootNode(n);
			} else {
				ve.node = n;
			}
		}

		if (ve.dockable == null) {
			// Fires events if the dockable is not available
			// (setRootNode() fires events when the dockable is available)
			if (n.isProject()) {
				fireProjectLoaded(ProjectViewer.class, (VPTProject) n, aView);
			} else {
				fireGroupActivated((VPTGroup)n, aView);
			}

			// Loads projects if not yet loaded
			if (n.isProject()
					&& !ProjectManager.getInstance().isLoaded(n.getName())) {
				ProjectManager.getInstance().getProject(n.getName());
			}
		}

	} //}}}

	//{{{ +_getActiveNode(View)_ : VPTNode
	/**
	 *	Return the current active node for the view. Returns null if no
	 *	active node is known for the view.
	 *
	 *	@since	PV 2.1.0
	 */
	public static VPTNode getActiveNode(View aView) {
		ViewerEntry ve = (ViewerEntry) viewers.get(aView);
		if (ve == null) {
			setActiveNode(aView, config.getLastNode());
			ve = (ViewerEntry) viewers.get(aView);
		}
		if (ve.dockable != null) {
			ve.dockable.waitForLoadLock();
		}

		return ve.node;
	} //}}}

	//{{{ +_getActiveProject(View)_ : VPTProject
	/**
	 *	Return the current active project for the view. If no project is
	 *	active, return null.
	 *
	 *	@since	PV 2.1.0
	 */
	public static VPTProject getActiveProject(View aView) {
		VPTNode n = getActiveNode(aView);
		return (n != null && n.isProject()) ? (VPTProject) n : null;
	} //}}}

	//}}}

	//{{{ Constants

	private final static String FOLDERS_TAB_TITLE 		= "projectviewer.folderstab";
	private final static String FILES_TAB_TITLE 		= "projectviewer.filestab";
	private final static String WORKING_FILES_TAB_TITLE = "projectviewer.workingfilestab";
	private final static String COMPACT_TAB_TITLE		= "projectviewer.compacttab";

	private final static String TREE_STATE_PROP = "projectviewer.folder_tree_state";
	private final static char NOT_EXPANDED		= '0';
	private final static char EXPANDED			= '1';

	//}}}

	//{{{ Attributes
	private View 					view;
	private HashSet					dontAsk;

	private JTree					folderTree;
	private JScrollPane				folderTreeScroller;
	private JTree					fileTree;
	private JScrollPane				fileTreeScroller;
	private JTree					workingFileTree;
	private JScrollPane				workingFileTreeScroller;
	private JTree					compactTree;
	private JScrollPane				compactTreeScroller;
	private JToolBar				toolBar;

	private JPanel					topPane;
	private JTabbedPane				treePane;
	private ProjectComboBox			pList;

	private VPTNode 				treeRoot;
	private VPTContextMenu			vcm;
	private VPTSelectionListener	vsl;
	private ConfigChangeListener	ccl;

	private TreeDragListener		tdl;
	private DragSource				dragSource;

	private volatile boolean		isLoadingProject;
	//}}}

	//{{{ +ProjectViewer(View) : <init>
	/**
	 *	Create a new <code>ProjectViewer</code>. Only one instance is allowed
	 *	per view.
	 *
	 *	@param  aView  The jEdit view where the viewer is to be created.
	 *	@throws	UnsupportedOperationException	If a viewer is already instantiated
	 *											for the given view.
`	 */
	public ProjectViewer(View aView) {
		ProjectViewer existant = getViewer(aView);
		if (existant != null) {
			throw new UnsupportedOperationException(
				jEdit.getProperty("projectviewer.error.multiple_views"));
		}

		setLayout(new BorderLayout());
		view = aView;
		vcm = new VPTContextMenu(this);
		vsl = new VPTSelectionListener(this);
		treeRoot = VPTRoot.getInstance();
		isLoadingProject = false;

		addHierarchyListener(this);

		// drag support
		tdl = new TreeDragListener();
		dragSource = new DragSource();

		// GUI
		buildGUI();

		ccl = new ConfigChangeListener();
		config.addPropertyChangeListener(ccl);

		// Loads the listeners from plugins that register listeners using global
		// properties instead of calling the addProjectViewerListener() method.
		EditPlugin[] plugins = jEdit.getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			addProjectViewerListeners(plugins[i].getPluginJAR(), view);
		}

		// Register the dockable window in the viewer list
		ViewerEntry ve = (ViewerEntry) viewers.get(aView);
		if (ve != null) {
			ve.dockable = this;
		} else {
			ve = new ViewerEntry();
			ve.dockable = this;
			ve.node = config.getLastNode();
			viewers.put(aView, ve);
		}
		EditBus.addToBus(this);
		setRootNode(ve.node);
	} //}}}

	//{{{ Private methods

	//{{{ -createTree(TreeModel) : JTree
	/** Creates a new tree to be added to the viewer. */
	private JTree createTree(TreeModel model) {
		JTree tree = new PVTree(model);
		tree.setCellRenderer(new VPTCellRenderer());
		//tree.setBorder(BorderFactory.createEtchedBorder());

		// don't change order!
		tree.addMouseListener(vsl);
		tree.addMouseListener(vcm);
		tree.addTreeSelectionListener(vsl);

		// drag support
		dragSource.createDefaultDragGestureRecognizer(tree,
			DnDConstants.ACTION_COPY, tdl);

		return tree;
	} //}}}

	//{{{ -populateToolBar() : void
	/** Loads the toolbar. */
	private void populateToolBar() {
		toolBar.removeAll();
		for (Iterator i = actions.iterator(); i.hasNext(); ) {
			Action a = (Action) i.next();
			a = (Action) a.clone();
			a.setViewer(this);
			toolBar.add(a.getButton());
		}
		toolBar.repaint();
	} //}}}

	//{{{ -buildGUI() : void
	/** Builds the viewer GUI. */
	private void buildGUI() {
		treePane = new JTabbedPane();

		topPane = new JPanel(new BorderLayout());

		pList = new ProjectComboBox(view);

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		box.add(pList);
		box.add(Box.createGlue());

		topPane.add(BorderLayout.CENTER, box);

		showTrees();
		showToolBar(config.getShowToolBar());
		add(BorderLayout.NORTH, topPane);

	} //}}}

	//{{{ -closeGroup(VPTGroup, VPTNode) : void
	private void closeGroup(VPTGroup group, VPTNode ignore) {
		for (int i = 0; i < group.getChildCount(); i++) {
			VPTNode child = (VPTNode) group.getChildAt(i);
			if (child != ignore) {
				if (child.isGroup()) {
					closeGroup((VPTGroup)child, ignore);
				} else if (ProjectManager.getInstance().isLoaded(child.getName())){
					closeProject((VPTProject)child);
				}
			}
		}
	} //}}}

	//{{{ -closeProject(VPTProject) : void
	/**
	 *	Closes a project: searches the open buffers for files related to the
	 *	given project and closes them (if desired) and/or saves them to the
	 *	open list (if desired).
	 */
	private void closeProject(VPTProject p) {
		p.clearOpenFiles();

		// check to see if project is active in some other viewer, so we
		// don't mess up that guy.
		if (config.getCloseFiles()) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ViewerEntry ve = (ViewerEntry) it.next();
				if (ve.dockable != this) {
					if (ve.node.isNodeDescendant(p)) {
						return;
					}
				}
			}
		}

		// close files & populate "remember" list
		if (config.getCloseFiles() || config.getRememberOpen()) {
			Buffer[] bufs = jEdit.getBuffers();

			String currFile = null;
			if (p.getChildNode(view.getBuffer().getPath()) != null) {
				currFile = view.getBuffer().getPath();
				if (config.getRememberOpen() && currFile != null) {
					p.addOpenFile(currFile);
				}
			}

			for (int i = 0; i < bufs.length; i++) {
				if (p.getChildNode(bufs[i].getPath()) != null) {
					if (config.getRememberOpen() && !bufs[i].getPath().equals(currFile)) {
						p.addOpenFile(bufs[i].getPath());
					}
					if (config.getCloseFiles()) {
						jEdit.closeBuffer(view, bufs[i]);
					}
				}
			}

		}

		// saves the folder tree state
		String state = getFolderTreeState(p);
		if (state != null) {
			p.setProperty(TREE_STATE_PROP, state);
		} else {
			p.removeProperty(TREE_STATE_PROP);
		}
	} //}}}

	//{{{ -openProject(VPTProject) : void
	/** Opens all the files that were previously opened in the project. */
	private void openProject(final VPTProject p) {
		if (config.getRememberOpen()) {
			for (Iterator i = p.getOpenFiles(); i.hasNext(); ) {
				jEdit.openFile(null, (String) i.next());
			}
		}
		p.clearOpenFiles();

		// loads tree state from the project, if saved
		final String state = p.getProperty(TREE_STATE_PROP);
		if (state != null && folderTree != null) {
			SwingUtilities.invokeLater(
				new Runnable() {
					//{{{ +run() : void
					public void run() {
						setFolderTreeState(p, state);
					} //}}}
				}
			);
		}
	} //}}}

	//{{{ -showTrees() : void
	/**
	 *	Loads the trees (folders, files, working files) into the view, deciding
	 *  what to show according to the configuration of the plugin
	 */
	private void showTrees() {
		treePane.removeAll();

		// Folders tree
		if(config.getShowFoldersTree()) {
			if(folderTree == null) {
				folderTree = createTree(new DefaultTreeModel(treeRoot, true));
				folderTreeScroller = new JScrollPane(folderTree);
			}
			treePane.addTab(jEdit.getProperty(FOLDERS_TAB_TITLE), folderTreeScroller);
		} else if (folderTree != null) {
			folderTree = null;
			folderTreeScroller = null;
		}

		// Files tree
		if(config.getShowFilesTree()) {
			if(fileTree == null) {
				fileTree = createTree(new VPTFileListModel(treeRoot));
				fileTreeScroller = new JScrollPane(fileTree);
			}
			treePane.addTab(jEdit.getProperty(FILES_TAB_TITLE), fileTreeScroller);
		} else if (fileTree != null) {
			fileTree = null;
			fileTreeScroller = null;
		}

		// Working files tree
		if(config.getShowWorkingFilesTree()) {
			if(workingFileTree == null) {
				VPTWorkingFileListModel model = new VPTWorkingFileListModel(treeRoot);
				workingFileTree = createTree(model);
				workingFileTreeScroller = new JScrollPane(workingFileTree);
			}
			treePane.addTab(jEdit.getProperty(WORKING_FILES_TAB_TITLE), workingFileTreeScroller);
		} else if (workingFileTree != null) {
			workingFileTree = null;
			workingFileTreeScroller = null;
		}

		// compact tree
		if(config.getShowCompactTree()) {
			if(compactTree == null) {
				VPTCompactModel model = new VPTCompactModel(treeRoot);
				compactTree = createTree(model);
				compactTreeScroller = new JScrollPane(compactTree);
			}
			treePane.addTab(jEdit.getProperty(COMPACT_TAB_TITLE,"Compact"), compactTreeScroller);
		} else {
			compactTree = null;
			compactTreeScroller = null;
		}


		if (treePane.getTabCount() == 0) {
			remove(treePane);
		} else if (treePane.getTabCount() == 1) {
			remove(treePane);
			add(BorderLayout.CENTER,treePane.getComponentAt(0));
		} else {
			add(BorderLayout.CENTER,treePane);
			treePane.setSelectedIndex(0);
		}
	}//}}}

	//{{{ -showToolBar(boolean) : void
	/** Shows/Hides the toolbar. */
	private void showToolBar(boolean flag) {
		if (toolBar != null) {
			topPane.remove(toolBar);
			toolBar.removeAll();
			toolBar = null;
		}

		if (flag) {
			toolBar = new JToolBar();
			toolBar.setFloatable(false);
			populateToolBar();
			topPane.add(BorderLayout.EAST, toolBar);
		}
	} //}}}

	//{{{ -unloadInactiveProjects() : void
	/** Checks if some of the projects that are loaded can be unloaded. */
	private void unloadInactiveProjects() {
		ArrayList active = null;
		for (Iterator i = viewers.values().iterator(); i.hasNext(); ) {
			ViewerEntry ve = (ViewerEntry) i.next();
			if (ve.node != null && ve.dockable != this) {
				if (active == null)
					active = new ArrayList();
				if (ve.node.isProject()) {
					active.add(ve.node.getName());
				} else if (!ve.node.isRoot()) {
					addProjectsToList(ve.node, active);
				} else {
					return;
				}
			}
		}

		ProjectManager pm = ProjectManager.getInstance();
		for (Iterator i = pm.getProjects(); i.hasNext(); ) {
			VPTProject p = (VPTProject) i.next();
			if (pm.isLoaded(p.getName())
					&& (active == null || !active.contains(p.getName()))) {
				pm.unloadProject(p);
			}
		}
	} //}}}

	//{{{ -addProjectsToList(VPTNode, List) : void
	private void addProjectsToList(VPTNode src, List l) {
		for (int i = 0; i < src.getChildCount(); i++) {
			VPTNode n = (VPTNode) src.getChildAt(i);
			if (n.isProject()) {
				l.add(n.getName());
			} else {
				addProjectsToList(n, l);
			}
		}
	} //}}}

	//{{{ -waitForLoadLock() : void
	/**
	 *	If the isLoadingProject flag is true, wait until notified
	 *	by the thread that is loading the project that loading is
	 *	done. This is more effective than the old way of using a
	 *	few synchronized blocks here and there.
	 */
	private void waitForLoadLock() {
		if (isLoadingProject) {
			synchronized (this) {
				while (isLoadingProject) {
					try {
						this.wait();
					} catch (InterruptedException ie) {
						// ignore
					}
				}
			}
		}
	} //}}}

	//}}}

	//{{{ Public Methods

	//{{{ +setStatus(String) : void
	/** Changes jEdit's status bar message for the current view. */
	public void setStatus(String message) {
		view.getStatus().setMessageAndClear(message);
	} //}}}

	//{{{ +getSelectedNode() : VPTNode
	/** Returns the currently selected node in the tree. */
	public VPTNode getSelectedNode() {
		JTree tree = getCurrentTree();
		if (tree != null && tree.getSelectionPath() != null) {
			return (VPTNode) tree.getSelectionPath().getLastPathComponent();
		} else {
			return null;
		}
	} //}}}

	//{{{ +getSelectedFilePaths() : ArrayList
    /**
     *  Returns an ArrayList of Strings containing the file paths of the selected file and folder nodes.
     *  This is mostly a utility method so other plugins/macros can peform actions on a selection of files.
     *
     */
    public ArrayList getSelectedFilePaths() {

		TreePath last = null;
        ArrayList obfp = new ArrayList();
		String sFiles="";

		JTree tree = getCurrentTree();
		if (tree == null)
			return null;

		if (tree.getSelectionPaths() != null) {
			TreePath[] paths= tree.getSelectionPaths();

		for (int i =0; i < paths.length; i++) {
			   VPTNode nd = (VPTNode)paths[i].getLastPathComponent();

			   if (nd instanceof projectviewer.vpt.VPTFile) {
			   	   sFiles += nd.getNodePath() + "\n";
			   		obfp.add(nd.getNodePath());
			   }
			}
			return obfp;
		} else {
			return null;
		}
    } //}}}

	//{{{ +getCurrentTree() : JTree
	/** Returns the currently active tree. */
	public JTree getCurrentTree() {
		if (treePane.getTabCount() > 0) {
			switch(treePane.getSelectedIndex()) {
				case 0:
					if (folderTree != null) return folderTree;
					if (fileTree != null) return fileTree;
					if (workingFileTree != null) return workingFileTree;
					if (compactTree != null) return compactTree;
				case 1:
					if (fileTree != null) return fileTree;
					if (workingFileTree != null) return workingFileTree;
					if (compactTree != null) return compactTree;
				case 2:
					if (workingFileTree != null) return workingFileTree;
					if (compactTree != null) return compactTree;
				case 3:
					if (compactTree != null) return compactTree;
				default:
					return null;
			}
		} else {
			if (folderTree != null) return folderTree;
			if (fileTree != null) return fileTree;
			if (workingFileTree != null) return workingFileTree;
			if (compactTree != null) return compactTree;
			return null;
		}
	} //}}}

	//{{{ +getView() : View
	/** Returns the View associated with this instance. */
	public View getView() {
		return view;
	} //}}}

	//{{{ +setRootNode(VPTNode) : void
	/**
	 *	Sets the root node of the trees showm by this viewer. The current root
	 *	is cleaned up before setting the new root (e.g., project files are closed,
	 *	etc.)
	 *
	 *	@throws IllegalArgumentException If node is not a project or group.
	 *	@since PV 2.1.0
	 */
	public void setRootNode(VPTNode n) {
		if (n == null)
			n = VPTRoot.getInstance();
		if (n == treeRoot)
			return;

		waitForLoadLock();

		if (!n.isGroup() && !n.isProject()) {
			throw new IllegalArgumentException("PV can only use Projects and Groups as root.");
		}

		// clean up the old root
		if (treeRoot != null && !n.isNodeDescendant(treeRoot)) {
			if (treeRoot.isProject()) {
				closeProject((VPTProject) treeRoot);
			} else {
				closeGroup((VPTGroup)treeRoot, n);
			}
			unloadInactiveProjects();
		}

		// set the new root
		if (n.isProject()) {
			VPTProject p = (VPTProject) n;
			if (!ProjectManager.getInstance().isLoaded(p.getName())) {
				setEnabled(false);
				new ProjectLoader(p.getName()).loadProject();
				return;
			}
			openProject(p);
			fireProjectLoaded(this, p, view);
		} else if (n.isGroup()){
			fireGroupActivated((VPTGroup)n, view);
		}

		treeRoot = n;
		if (folderTree != null)
			((DefaultTreeModel)folderTree.getModel()).setRoot(treeRoot);
		if (fileTree != null)
			((DefaultTreeModel)fileTree.getModel()).setRoot(treeRoot);
		if (workingFileTree != null)
			((DefaultTreeModel)workingFileTree.getModel()).setRoot(treeRoot);
		if (compactTree != null)
			((DefaultTreeModel)compactTree.getModel()).setRoot(treeRoot);

		dontAsk = null;
		config.setLastNode(n);
		((ViewerEntry)viewers.get(view)).node = n;
		ProjectManager.getInstance().fireDynamicMenuChange();
		pList.setSelectedNode(treeRoot);
	} //}}}

	//{{{ +setProject(VPTProject) : void
	/**
	 *	Sets the given project to be the root of the tree. If "p" is null,
	 *	then the root node is set to the "VPTRoot" node.
	 *
	 *	@deprecated		Use {@link #setRootNode(VPTNode) setRootNode(VPTNode)}
	 *					instead.
	 */
	public void setProject(VPTProject p) {
		setRootNode(p);
	} //}}}

	//{{{ +getRoot() : VPTNode
	/**	Returns the root node of the current tree. */
	public VPTNode getRoot() {
		waitForLoadLock();
		return treeRoot;
	} //}}}

	//{{{ +setEnabled(boolean) : void
	/** Enables or disables the viewer GUI. */
	public void setEnabled(boolean flag) {
		treePane.setEnabled(flag);
		pList.setEnabled(flag);
		if (folderTree != null) folderTree.setEnabled(flag);
		if (fileTree != null) fileTree.setEnabled(flag);
		if (workingFileTree != null) workingFileTree.setEnabled(flag);
		if (compactTree != null) compactTree.setEnabled(flag);
		if (toolBar != null) {
			Component[] buttons = toolBar.getComponents();
			for (int i = 0; i < buttons.length; i++)
				buttons[i].setEnabled(flag);
		}
		super.setEnabled(flag);
	} //}}}

	//{{{ +getFolderTreeState(VPTNode) : String
	/**
	 *	Returns a String representing the state of the folder tree.
	 *
	 *	@see	#setFolderTreeState(VPTNode, String)
	 *	@return	The state of the tree, starting at the given node, or
	 *			null if the folderTree is not visible.
	 */
	public String getFolderTreeState(VPTNode node) {
		if (folderTree != null) {
			DefaultTreeModel model = (DefaultTreeModel) folderTree.getModel();
			int start = folderTree.getRowForPath(new TreePath(model.getPathToRoot(node)));
			if (start >= 0) {
				StringBuffer state = new StringBuffer();
				if(folderTree.isExpanded(start)) {
					for(int i = start; i < folderTree.getRowCount(); i++) {
						VPTNode n = (VPTNode) folderTree.getPathForRow(i)
										.getLastPathComponent();
						if (!node.isNodeDescendant(n))
							break;
						if (folderTree.isExpanded(i)) {
							state.append(EXPANDED);
						} else {
							state.append(NOT_EXPANDED);
						}
					}
				}
				return state.toString();
			}
		}
		return null;
	} //}}}

	//{{{ +setFolderTreeState(VPTNode, String) : void
	/**
	 *	Sets the folder tree state from the given String.
	 *
	 *	@see	#getFolderTreeState(VPTNode)
	 */
	public void setFolderTreeState(VPTNode node, String state) {
		if (folderTree != null && state != null) {
			DefaultTreeModel model = (DefaultTreeModel) folderTree.getModel();
			int start = folderTree.getRowForPath(new TreePath(model.getPathToRoot(node)));
			for(int i = 0; i < state.length(); i++) {
				int row = start + i;
				if (row >= folderTree.getRowCount())
					break;

				TreePath path = folderTree.getPathForRow(row);
				if (path == null)
					return;
				VPTNode n = (VPTNode) path.getLastPathComponent();
				if (!node.isNodeDescendant(n))
					break;

				if (state.charAt(i) == EXPANDED) {
					folderTree.expandRow(row);
				}
			}
		}
	} //}}}

	//}}}

	//{{{ Message handling

	//{{{ +handleMessage(EBMessage) : void
	/** Handles an EditBus message.
	 */
	public void handleMessage(EBMessage msg) {
		if (msg instanceof ViewUpdate) {
			handleViewUpdateMessage((ViewUpdate) msg);
		} else if (msg instanceof BufferUpdate) {
			handleBufferUpdateMessage((BufferUpdate) msg, treeRoot);
		} else if (msg instanceof DynamicMenuChanged) {
			handleDynamicMenuChanged((DynamicMenuChanged)msg);
		} else if (treeRoot != null && treeRoot.isProject()) {
			if (config.isErrorListAvailable()) {
				new Helper().handleErrorListMessage(msg);
			}
		}

	} //}}}

	//{{{ -handleDynamicMenuChanged(DynamicMenuChanged) : void
	/** Handles a handleDynamicMenuChanged EditBus message. */
	private void handleDynamicMenuChanged(DynamicMenuChanged dmg) {
		if (dmg.getMenuName().equals("plugin.projectviewer.ProjectPlugin.menu")) {
			pList.updateMenu();
		}
	}//}}}

	//{{{ -handleViewUpdateMessage(ViewUpdate) : void
	/** Handles a ViewUpdate EditBus message.
	 */
	private void handleViewUpdateMessage(ViewUpdate vu) {
		// View closed? Remove from edit bus and from viewers list
		// EditPane changed? Fire a projectLoaded event for the global
		// listeners.
		if (vu.getView() == view) {
			if (vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED) {
				VPTNode active = null;
				ProjectViewer v = getViewer(vu.getView());
				if (v != null) {
					if (v.treeRoot == null)
						return;
					active = v.treeRoot;
				}
				config.setLastNode(active);
				ProjectViewerEvent evt;
				ArrayList lst = (ArrayList) listeners.get(null);
				if (lst != null) {
					if (active.isProject()) {
						evt = new ProjectViewerEvent(this, (VPTProject) active);
						for (Iterator i = lst.iterator(); i.hasNext(); ) {
							((ProjectViewerListener)i.next()).projectLoaded(evt);
						}
					} else {
						evt = new ProjectViewerEvent((VPTGroup)active);
						for (Iterator i = lst.iterator(); i.hasNext(); ) {
							((ProjectViewerListener)i.next()).groupActivated(evt);
						}
					}
				}
			}
		}
	}//}}}

	//{{{ -handleBufferUpdateMessage(BufferUpdate, VPTNode) : boolean
	/** Handles a BufferUpdate EditBus message.
	 */
	private boolean handleBufferUpdateMessage(BufferUpdate bu, VPTNode where) {
		if (bu.getView() != null && bu.getView() != view) return false;

		boolean ask = false;
		if (bu.getWhat() == BufferUpdate.SAVED) {
			if (where == null || !where.isProject())
				return false;

			VPTProject p = (VPTProject) treeRoot;
			VPTNode f = p.getChildNode(bu.getBuffer().getPath());
			if (f != null)
				return false;

			File file = new File(bu.getBuffer().getPath());
			String fileParentPath = file.getParent() + File.separator;
			String projectRootPath = p.getRootPath() + File.separator;
			ask = (config.getAskImport() != ProjectViewerConfig.ASK_NEVER &&
					(dontAsk == null ||
						config.getAskImport() == ProjectViewerConfig.ASK_ALWAYS ||
						!dontAsk.contains(bu.getBuffer().getPath())) &&
					fileParentPath.startsWith(projectRootPath));

			// Try to import newly created files to the project
			if (ask) {
				int res = JOptionPane.YES_OPTION;
				JCheckBox cBox = null;
				if (config.getAskImport() != ProjectViewerConfig.AUTO_IMPORT) {
					JPanel panel = new JPanel();
					BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
					panel.setLayout(bl);

					JLabel msg = new JLabel(
						jEdit.getProperty("projectviewer.import_new",
							new Object[] { bu.getBuffer().getName(), p.getName() }));
					cBox = new JCheckBox(jEdit.getProperty("projectviewer.import_always_cb"));
					cBox.setSelected(false);
					panel.add(msg);
					panel.add(cBox);

					res = JOptionPane.showConfirmDialog(view,
							panel,
							jEdit.getProperty("projectviewer.import_new.title"),
							JOptionPane.YES_NO_OPTION);
				}

				if (res == JOptionPane.YES_OPTION) {
					new NewFileImporter(p, this, bu.getBuffer().getPath()).doImport();

					if (cBox != null && cBox.isSelected()) {
						config.setAskImport(ProjectViewerConfig.AUTO_IMPORT);
						JPanel panel = new JPanel();
						BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
						panel.setLayout(bl);
						panel.add(new JLabel(jEdit.getProperty("projectviewer.import_always_disable.1")));
						panel.add(new JLabel(jEdit.getProperty("projectviewer.import_always_disable.2")));
						JOptionPane.showMessageDialog(view, panel,
							jEdit.getProperty("projectviewer.import_new.title"),
							JOptionPane.INFORMATION_MESSAGE);
					}
				} else if (config.getAskImport() == ProjectViewerConfig.ASK_ONCE) {
					if (dontAsk == null) {
						dontAsk = new HashSet();
					}
					dontAsk.add(bu.getBuffer().getPath());
				}
			}
		}

		// Notifies trees when a buffer is closed (so it should not be
		// underlined anymore) or opened (should underline it).
		if (where != null &&
				(bu.getWhat() == BufferUpdate.CLOSED
				|| bu.getWhat() == BufferUpdate.LOADED
				|| bu.getWhat() == BufferUpdate.DIRTY_CHANGED)) {
			if (where.isProject()) {
				VPTNode f = ((VPTProject)where).getChildNode(bu.getBuffer().getPath());
				if (f != null) {
					ProjectViewer.nodeChanged(f);
					return true;
				}
			} else {
				for (int i = 0; i < where.getChildCount(); i++) {
					if (handleBufferUpdateMessage(bu, (VPTNode)where.getChildAt(i))) {
						return true;
					}
				}
			}
		}

		return false;
 	}//}}}

	//}}}

	//{{{ +hierarchyChanged(HierarchyEvent) : void
	public void hierarchyChanged(HierarchyEvent he) {
		if (he.getChanged() == this && !isDisplayable() &&
				((he.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED)
					== HierarchyEvent.DISPLAYABILITY_CHANGED)) {
			// we're being removed from the GUI, so clean up
			EditBus.removeFromBus(this);
			if (treeRoot != null && treeRoot.isProject()) {
				closeProject((VPTProject)treeRoot);
				config.setLastNode(treeRoot);
			}
			ViewerEntry ve = (ViewerEntry) viewers.get(view);
			if (ve != null) {
				ve.dockable = null;
			}
		}
	} //}}}

	//{{{ -class ConfigChangeListener
	/** Listens for changes in the PV configuration. */
	private class ConfigChangeListener implements PropertyChangeListener, Runnable {

		private boolean willRun = false;

		//{{{ +propertyChange(PropertyChangeEvent) : void
		/** Listens for property change events in the plugin's configuration.
		 *  Shows/Hides the toolbar and the trees, according to the user's wish.
		 *
		 * @param  evt  Description of Parameter
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			// Toolbar show/hide.
			if (evt.getPropertyName().equals(ProjectViewerConfig.SHOW_TOOLBAR_OPT)) {
				showToolBar( ((Boolean)evt.getNewValue()).booleanValue() &&
					(folderTree != null || fileTree != null || workingFileTree != null || compactTree != null) );
				return;
			}

			if (evt.getPropertyName().equals(ProjectViewerConfig.ASK_IMPORT_OPT)) {
				int opt = ((Integer)evt.getNewValue()).intValue();
				if (opt == ProjectViewerConfig.ASK_NEVER ||
						opt == ProjectViewerConfig.ASK_ONCE) {
					dontAsk = null;
				}
				return;
			}

			if (evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FILES_OPT) ||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_WFILES_OPT) ||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FOLDERS_OPT)||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_COMPACT_OPT)) {
				if (!willRun) {
					SwingUtilities.invokeLater(this);
					willRun = true;
				}
				return;
			}

		}//}}}

		//{{{ +run() : void
		/** "Run" method, called by the Swing runtime after a config option for one
		 *  or more of the trees has changed.
		 */
		public void run() {
			showTrees();
			showToolBar(config.getShowToolBar());
			willRun = false;
		}//}}}

	} //}}}

	//{{{ -class ProjectLoader
	/** Loads a project in the background. */
	private class ProjectLoader implements Runnable {

		private String pName;
		private final JTree tree;
		private final DefaultTreeModel tModel;

		//{{{ +ProjectLoader(String) : <init>
		public ProjectLoader(String pName) {
			this.pName = pName;
			this.tree = getCurrentTree();
			this.tModel = (tree != null)
					? (DefaultTreeModel) tree.getModel() : null;
		} //}}}

		//{{{ +loadProject() : void
		public void loadProject() {
			// This method is called in the AWT Thread, so do some of the
			// processing here before we start the thread itself.
			treeRoot = null;
			setEnabled(false);
			if (tree != null) {
				tree.setModel(new DefaultTreeModel(
					new DefaultMutableTreeNode(
						jEdit.getProperty("projectviewer.loading_project",
							new Object[] { pName } ))));
			} else {
				setStatus(jEdit.getProperty("projectviewer.loading_project",
							new Object[] { pName } ));
			}

			isLoadingProject = true;

			//VFSManager.getIOThreadPool().addWorkRequest(this, false);
			new Thread(this).start();
		} //}}}

		//{{{ +run() : void
		public void run() {
			final VPTProject p;
			p = ProjectManager.getInstance().getProject(pName);

			synchronized (ProjectViewer.this) {
				isLoadingProject = false;
				ProjectViewer.this.notifyAll();
			}

			try {
				SwingUtilities.invokeAndWait(
					new Runnable() {
						public void run() {
							if (tree != null) {
								tModel.setRoot(p);
								tree.setModel(tModel);
							}
							setRootNode(p);
							setEnabled(true);
						}
					});
			} catch (InterruptedException ie) {
				// not gonna happen
			} catch (java.lang.reflect.InvocationTargetException ite) {
				// not gonna happen
			}
		} //}}}

	} //}}}

	//{{{ -class PVTree
	/** Listens for key events in the trees. */
	private class PVTree extends JTree {

		//{{{ +PVTree(TreeModel) : <init>
		public PVTree(TreeModel model) {
			super(model);
		} //}}}

		//{{{ +processKeyEvent(KeyEvent) : void
		public void processKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
				TreePath[] paths = getSelectionPaths();
				for (int i = 0; i < paths.length; i++) {
					VPTNode n = (VPTNode) paths[i].getLastPathComponent();
					if (n.isFile()) {
						n.open();
					}
				}
				e.consume();
			} else {
				super.processKeyEvent(e);
			}
		} //}}}

		//{{{ +expandPath(TreePath) : void
		/**
		 *	If trying to expand unloaded projects, load them before expansion
		 *	occurs.
		 */
		public void expandPath(TreePath path) {
			VPTNode n = (VPTNode) path.getLastPathComponent();
			if (n.isProject()
					&& !ProjectManager.getInstance().isLoaded(n.getName())) {

				synchronized (n) {
					if (!ProjectManager.getInstance().isLoaded(n.getName())) {
						setStatus(jEdit.getProperty("projectviewer.loading_project",
							new Object[] { n.getName() } ));
						ProjectManager.getInstance().getProject(n.getName());
					}
				}
			}
			super.expandPath(path);

			if (n.isProject() || n.isGroup()) {
				if (folderTree != null && folderTree != this)
					((PVTree)folderTree).expand(path);
				if (fileTree != null && fileTree != this)
					((PVTree)fileTree).expand(path);
				if (workingFileTree != null && workingFileTree != this)
					((PVTree)workingFileTree).expand(path);
				if (compactTree != null && compactTree != this)
					((PVTree)compactTree).expand(path);
			}

		} //}}}

		//{{{ +collapsePath(TreePath) : void
		/** Keeps trees syncd w.r.t. projects and groups. */
		public void collapsePath(TreePath path) {
			super.collapsePath(path);
			VPTNode n = (VPTNode) path.getLastPathComponent();
			if (n.isProject() || n.isGroup()) {
				if (folderTree != null && folderTree != this)
					((PVTree)folderTree).collapse(path);
				if (fileTree != null && fileTree != this)
					((PVTree)fileTree).collapse(path);
				if (workingFileTree != null && workingFileTree != this)
					((PVTree)workingFileTree).collapse(path);
				if (compactTree != null && compactTree != this)
					((PVTree)compactTree).collapse(path);
			}
		} //}}}

		//{{{ -expand(TreePath) : void
		/**
		 *	Used internally to bypass the overridden "expandPath()" method and
		 *	keep the different trees synced w.r.t. projects and groups.
		 */
		private void expand(TreePath path) {
			super.expandPath(path);
		} //}}}

		//{{{ -collapse(TreePath) : void
		/**
		 *	Used internally to bypass the overridden "expandPath()" method and
		 *	keep the different trees synced w.r.t. projects and groups.
		 */
		private void collapse(TreePath path) {
			super.collapsePath(path);
		} //}}}

	} //}}}

	//{{{ -class TreeDragListener
	/**
	 *	Implements a DragGestureListener for the trees, that will detect when
	 *	the user tries to drag a file to somewhere. Other kinds of nodes will
	 *	be ignored.
	 */
	private class TreeDragListener implements DragGestureListener {

		//{{{ +dragGestureRecognized(DragGestureEvent) : void
		public void dragGestureRecognized(DragGestureEvent dge) {
			JTree tree = getCurrentTree();
			TreePath path = tree.getPathForLocation( (int) dge.getDragOrigin().getX(),
								(int) dge.getDragOrigin().getY());

			if (path != null) {
				VPTNode n = (VPTNode) path.getLastPathComponent();
				if (n.isFile()) {
					dge.startDrag(DragSource.DefaultCopyDrop,
									new FileListTransferable((VPTFile)n));
				}
			}
		} //}}}

	} //}}}

	//{{{ -class _FileListTransferable_
	/** A transferable for a file. */
	private static class FileListTransferable extends LinkedList implements Transferable {

		//{{{ +FileListTransferable(VPTFile) : <init>
		public FileListTransferable(VPTFile file) {
			super.add(file.getFile());
		} //}}}

		//{{{ +getTransferData(DataFlavor) : Object
		public Object getTransferData(DataFlavor flavor) {
			if (flavor == DataFlavor.javaFileListFlavor) {
				return this;
			}
			return null;
		} //}}}

		//{{{ +getTransferDataFlavors() : DataFlavor[]
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.javaFileListFlavor };
		} //}}}

		//{{{ +isDataFlavorSupported(DataFlavor) : boolean
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return (flavor == DataFlavor.javaFileListFlavor);
		} //}}}

	} //}}}

	//{{{ -class _ViewerEntry_
	/**
	 *	Holds information about what's active on what view, to allow active
	 *	nodes even with no dockable open.
	 *
	 *	@since	PV 2.1.0
	 */
	private static class ViewerEntry {
		public ProjectViewer dockable;
		public VPTNode node;
	} //}}}

	//{{{ -class Helper
	/**
	 *	Class to hold methods that require classes that may not be available,
	 *	so that PV behaves well when called from a BeanShell script.
	 */
	private class Helper {

		//{{{ +handleErrorListMessage(EBMessage) : void
		public void handleErrorListMessage(EBMessage msg) {
			if (msg instanceof ErrorSourceUpdate) {
				handleErrorSourceUpdateMessage((ErrorSourceUpdate) msg);
			}
		} //}}}

		//{{{ -handleErrorSourceUpdateMessage(ErrorSourceUpdate) : void
		/** Handles a ErrorSourceUpdate EditBus message. */
		private void handleErrorSourceUpdateMessage(ErrorSourceUpdate esu) {
			//Log.log(Log.DEBUG, this, "ErrorSourceUpdate received :["+esu.getWhat()+"]["+esu.getErrorSource().getName()+"]");
			if ( esu.getWhat() == ErrorSourceUpdate.ERROR_ADDED ||
				esu.getWhat() == ErrorSourceUpdate.ERROR_REMOVED) {
				VPTProject p = (VPTProject) treeRoot;
				ErrorSource.Error error = esu.getError();
				VPTNode f = p.getChildNode(error.getFilePath());
				if ( f != null ) {
					//Log.log(Log.DEBUG, this, "ErrorSourceUpdate for :["+error.getFilePath()+"]");
					if (folderTree != null) {
						((DefaultTreeModel)folderTree.getModel()).nodeChanged(f);
					}
					if (fileTree != null) {
						((DefaultTreeModel)fileTree.getModel()).nodeChanged(f);
					}
					if (workingFileTree != null) {
						((VPTWorkingFileListModel)workingFileTree.getModel()).nodeChanged(f);
					}
					if (compactTree != null) {
						((DefaultTreeModel)compactTree.getModel()).nodeChanged(f);
					}
				}
			}
			if (esu.getWhat() == ErrorSourceUpdate.ERROR_SOURCE_ADDED
					|| esu.getWhat() == ErrorSourceUpdate.ERROR_SOURCE_REMOVED
					|| esu.getWhat() == ErrorSourceUpdate.ERRORS_CLEARED) {
				VPTProject p = (VPTProject) treeRoot;
				if (folderTree != null) {
					folderTree.repaint();
				}
				if (fileTree != null) {
					fileTree.repaint();
				}
				if (workingFileTree != null) {
					workingFileTree.repaint();
				}
				if (compactTree != null) {
					compactTree.repaint();
				}
			}
		}//}}}

	} //}}}

}

