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
 * GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

//{{{ Imports
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;
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
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.DefaultListCellRenderer;

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

import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.EditorExitRequested;

import errorlist.ErrorSource;
import errorlist.ErrorSourceUpdate;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTContextMenu;
import projectviewer.vpt.VPTCellRenderer;
import projectviewer.vpt.VPTFileListModel;
import projectviewer.vpt.VPTSelectionListener;
import projectviewer.vpt.VPTWorkingFileListModel;

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
public final class ProjectViewer extends JPanel implements EBComponent {

	//{{{ Static members

	private static final ProjectViewerConfig config = ProjectViewerConfig.getInstance();
	private static final HashMap viewers		= new HashMap();
	private static final HashMap listeners		= new HashMap();
	private static final ArrayList actions		= new ArrayList();
	private static boolean DISABLE_EVENTS;

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
		// Listeners and Actions from other plugins
		if (config.isJEdit42()) {
			EditPlugin[] plugins = jEdit.getPlugins();
			for (int i = 0; i < plugins.length; i++) {
				addProjectViewerListeners(plugins[i].getPluginJAR(), null);
				addToolbarActions(plugins[i].getPluginJAR());
				VPTContextMenu.registerActions(plugins[i].getPluginJAR());
			}
		}
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
			ProjectViewer v = (ProjectViewer) it.next();
			if (v.toolBar != null)
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
		return (ProjectViewer) viewers.get(view);
	} //}}}

	//{{{ +_updateProjectCombos()_ : void
	/**
	 *	Updates the combo box that lists the projects for all project viewers
	 *	currently instantiated.
	 */
	public static void updateProjectCombos() {
		DISABLE_EVENTS = true;

		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ProjectViewer v = (ProjectViewer) it.next();

			v.pList.removeAllItems();
			v.pList.addItem(CREATE_NEW_PROJECT);
			v.pList.addItem(VPTRoot.getInstance());

			for (Iterator it2 = ProjectManager.getInstance().getProjects(); it2.hasNext(); ) {
				v.pList.addItem(it2.next());
			}

			v.pList.setSelectedItem(v.treeRoot);
		}

		DISABLE_EVENTS = false;
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
	 *	registered for this instance's view and listeners registered for all
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
			ProjectViewer viewer = (ProjectViewer) viewers.get(v);
			if (viewer != null) {
				viewer.setProject(p);
				return;
			}
			evt = new ProjectViewerEvent(src, p);
		}

		ArrayList lst;
		if (v != null) {
			lst = (ArrayList) listeners.get(v);
			if (lst != null)
			for (Iterator i = lst.iterator(); i.hasNext(); ) {
				((ProjectViewerListener)i.next()).projectLoaded(evt);
			}
		}

		lst = (ArrayList) listeners.get(null);
		if (lst != null)
		for (Iterator i = lst.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).projectLoaded(evt);
		}

	} //}}}

	//{{{ +_fireProjectAdded(Object, VPTProject)_ : void
	/**
	 *	Fires a "project added" event. All listeners, regardless of the view, are
	 *	notified of this event.
	 */
	public static void fireProjectAdded(Object src, VPTProject p) {
		HashSet notify = new HashSet();
		for (Iterator i = listeners.values().iterator(); i.hasNext(); ) {
			notify.addAll((ArrayList)i.next());
		}

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
		HashSet notify = new HashSet();
		for (Iterator i = listeners.values().iterator(); i.hasNext(); ) {
			notify.addAll((ArrayList)i.next());
		}

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

	//}}}

	//{{{ Tree Changes Broadcast Methods

	//{{{ +_nodeStructureChanged(VPTNode)_ : void
	/**
	 *	Notify all project viewer instances of a change in a node's structure.
	 */
	public static void nodeStructureChanged(VPTNode node) {
		VPTProject p = VPTNode.findProjectFor(node);
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ProjectViewer v = (ProjectViewer) it.next();
			if (v.folderTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
				((DefaultTreeModel)v.folderTree.getModel()).nodeStructureChanged(node);
			}
			if (v.fileTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
				((DefaultTreeModel)v.fileTree.getModel()).nodeStructureChanged(node);
			}

			if (v.workingFileTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
				((DefaultTreeModel)v.workingFileTree.getModel()).nodeStructureChanged(node);
			}
		}
	} //}}}

	//{{{ +_nodeChanged(VPTNode)_ : void
	/** Notify all project viewer instances of a change in a node. */
	public static void nodeChanged(VPTNode node) {
		VPTProject p = VPTNode.findProjectFor(node);
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ProjectViewer v = (ProjectViewer) it.next();
			if (v.folderTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
				((DefaultTreeModel)v.folderTree.getModel()).nodeChanged(node);
			}
			if (node.isFile() || node.isProject()) {
				if (v.fileTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
					((DefaultTreeModel)v.fileTree.getModel()).nodeChanged(node);
				}

				if (v.workingFileTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
					((DefaultTreeModel)v.workingFileTree.getModel()).nodeChanged(node);
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
		VPTProject p = VPTNode.findProjectFor(child);

		if (config.getShowFoldersTree()) {
			int[] ind = new int[] { idx };
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.folderTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
					((DefaultTreeModel)v.folderTree.getModel())
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
		VPTProject p = VPTNode.findProjectFor(node);
		if (config.getShowFilesTree() || config.getShowWorkingFilesTree()) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.fileTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
					((DefaultTreeModel)v.fileTree.getModel()).nodeStructureChanged(node);
				}

				if (v.workingFileTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
					((DefaultTreeModel)v.workingFileTree.getModel()).nodeStructureChanged(node);
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
		VPTProject p = VPTNode.findProjectFor(child);
		VPTNode parent = (VPTNode) child.getParent();
		int index = parent.getIndex(child);
		parent.remove(index);

		if (config.getShowFoldersTree()) {
			Object[] removed = new Object[] { child };
			int[] idx = new int[] { index };
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.folderTree != null && (v.treeRoot == p || v.treeRoot.isRoot())) {
					((DefaultTreeModel)v.folderTree.getModel())
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
				config.getShowWorkingFilesTree()) {

			Object[] removed = new Object[] { p };
			int[] idx = new int[] { index };

			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.folderTree != null && v.treeRoot.isRoot()) {
					((DefaultTreeModel)v.folderTree.getModel())
						.nodesWereRemoved(parent, idx, removed);
				}

				if (v.fileTree != null && v.treeRoot.isRoot()) {
					((DefaultTreeModel)v.fileTree.getModel())
						.nodesWereRemoved(parent, idx, removed);
				}

				if (v.workingFileTree != null && v.treeRoot.isRoot()) {
					((DefaultTreeModel)v.workingFileTree.getModel())
						.nodesWereRemoved(parent, idx, removed);
				}
				if (p == v.treeRoot) {
					v.setProject(null);
				}
			}
		}
		updateProjectCombos();
		fireProjectRemoved(src, p);
	} //}}}

	//}}}

	//}}}

	//{{{ Constants

	public final static String CREATE_NEW_PROJECT = jEdit.getProperty("projectviewer.create_project");

	private final static String FOLDERS_TAB_TITLE 		= "projectviewer.folderstab";
	private final static String FILES_TAB_TITLE 		= "projectviewer.filestab";
	private final static String WORKING_FILES_TAB_TITLE = "projectviewer.workingfilestab";

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
	private JToolBar				toolBar;

	private JPanel					topPane;
	private JTabbedPane				treePane;
	private JComboBox				pList;

	private VPTNode 				treeRoot;
	private VPTContextMenu			vcm;
	private VPTSelectionListener	vsl;
	private ConfigChangeListener	ccl;

	private TreeDragListener		tdl;
	private DragSource				dragSource;
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
		if (viewers.get(aView) != null) {
			ProjectViewer existant = (ProjectViewer) viewers.get(aView);
			Window wnd = SwingUtilities.getWindowAncestor(existant);
			if (wnd != null && wnd.isShowing()) {
				throw new UnsupportedOperationException(
					jEdit.getProperty("projectviewer.error.multiple_views"));
			}
		}

		setLayout(new BorderLayout());
		view = aView;
		vcm = new VPTContextMenu(this);
		vsl = new VPTSelectionListener(this);
		treeRoot = VPTRoot.getInstance();

		// drag support
		tdl = new TreeDragListener();
		dragSource = new DragSource();

		// GUI
		buildGUI();

		ccl = new ConfigChangeListener();
		config.addPropertyChangeListener(ccl);

		viewers.put(aView, this);
		EditBus.addToBus(this);
		// Loads the listeners from plugins that register listeners using global
		// properties instead of calling the addProjectViewerListener() method.
		if (config.isJEdit42()) {
			EditPlugin[] plugins = jEdit.getPlugins();
			for (int i = 0; i < plugins.length; i++) {
				addProjectViewerListeners(plugins[i].getPluginJAR(), view);
			}
		}

		// Loads the last project into the viewer
		if (config.getLastProject() != null) {
			if (ProjectManager.getInstance().hasProject(config.getLastProject()))
				new ProjectLoader(config.getLastProject()).loadProject();
		}

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

		pList = new JComboBox();
		pList.setRenderer(new VPTListCellRenderer());

		pList.addItem(CREATE_NEW_PROJECT);
		pList.addItem(VPTRoot.getInstance());

		for (Iterator it = ProjectManager.getInstance().getProjects(); it.hasNext(); ) {
			pList.addItem(it.next());
		}

		pList.setSelectedItem(treeRoot);
		pList.addItemListener(new ProjectComboListener());

		Dimension dim = pList.getPreferredSize();
		dim.width = Integer.MAX_VALUE;
		pList.setMaximumSize(dim);

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		box.add(pList);
		box.add(Box.createGlue());

		topPane.add(BorderLayout.CENTER, box);

		showTrees();
		showToolBar(config.getShowToolBar());
		add(BorderLayout.NORTH, topPane);

	} //}}}

	//{{{ -closeProject(VPTProject, boolean, boolean) : void
	/**
	 *	Closes a project: searches the open buffers for files related to the
	 *	given project and closes them (if desired) and/or saves them to the
	 *	open list (if desired).
	 */
	private void closeProject(VPTProject p, boolean close, boolean remember) {
		p.clearOpenFiles();
		boolean usingAllProjects = false;

		// check to see if project is active in some other viewer, so we
		// don't mess up that guy.
		if (close) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer pv = (ProjectViewer) it.next();
				if (pv != this) {
					if (pv.treeRoot == p) {
						return;
					} else if (!pv.treeRoot.isProject()) {
						usingAllProjects = true;
					}
				}
			}
		}

		// close files & populate "remember" list
		if (close || remember) {
			Buffer[] bufs = jEdit.getBuffers();

			String currFile = null;
			if (p.getChildNode(view.getBuffer().getPath()) != null) {
				currFile = view.getBuffer().getPath();
			}

			for (int i = 0; i < bufs.length; i++) {
				if (p.getChildNode(bufs[i].getPath()) != null) {
					if (remember && !bufs[i].getPath().equals(currFile)) {
						p.addOpenFile(bufs[i].getPath());
					}
					if (close) {
						jEdit.closeBuffer(view, bufs[i]);
					}
				}
			}

			if (remember && currFile != null) {
				p.addOpenFile(currFile);
			}
		}

		// saves the folder tree state
		if (folderTree != null) {
			int row_count = folderTree.getRowCount();
			StringBuffer state = new StringBuffer();
			if(folderTree.isExpanded(0)) {
				for(int i = 1; i < row_count; i++) {
					if (folderTree.isExpanded(i)) {
						state.append(EXPANDED);
					} else {
						state.append(NOT_EXPANDED);
					}
				}
			}
			p.setProperty(TREE_STATE_PROP, state.toString());
		} else {
			p.removeProperty(TREE_STATE_PROP);
		}

		// unloads the project
		// needs to check if project exists, in case it has just been removed
		ProjectManager pm = ProjectManager.getInstance();
		if (!usingAllProjects && pm.hasProject(p.getName())) {
			pm.unloadProject(p);
		}
	} //}}}

	//{{{ -openProject(VPTProject) : void
	/** Opens all the files that were previously opened in the project. */
	private void openProject(VPTProject p) {
		Buffer lastBuf = null;
		if (config.getRememberOpen()) {
			for (Iterator i = p.getOpenFiles(); i.hasNext(); ) {
				lastBuf = jEdit.openFile(null, (String) i.next());
			}
		}

		if (lastBuf != null) {
			view.getEditPane().setBuffer(lastBuf);
		}

		// loads tree state from the project, if saved
		String state = p.getProperty(TREE_STATE_PROP);
		if (state != null && folderTree != null) {
			SwingUtilities.invokeLater(new TreeStateLoader(state));
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
		} else {
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
		} else {
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
		} else {
			workingFileTree = null;
			workingFileTreeScroller = null;
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
				case 1:
					if (fileTree != null) return fileTree;
					if (workingFileTree != null) return workingFileTree;
				case 2:
					if (workingFileTree != null) return workingFileTree;

				default:
					return null;
			}
		} else {
			if (folderTree != null) return folderTree;
			if (fileTree != null) return fileTree;
			if (workingFileTree != null) return workingFileTree;
			return null;
		}
	} //}}}

	//{{{ +getView() : View
	/** Returns the View associated with this instance. */
	public View getView() {
		return view;
	} //}}}

	//{{{ +setProject(VPTProject) : void
	/**
	 *	Sets the given project to be the root of the tree. If "p" is null,
	 *	then the root node is set to the "VPTRoot" node.
	 */
	public synchronized void setProject(VPTProject p) {
		if (treeRoot != null && treeRoot.isProject()) {
			closeProject((VPTProject)treeRoot, config.getCloseFiles(),
				config.getRememberOpen());
		}

		if (p != null) {
			treeRoot = p;
			config.setLastProject(p.getName());
			openProject(p);
		} else {
			treeRoot = VPTRoot.getInstance();
			config.setLastProject(null);
		}

		if (folderTree != null)
			((DefaultTreeModel)folderTree.getModel()).setRoot(treeRoot);
		if (fileTree != null)
			((DefaultTreeModel)fileTree.getModel()).setRoot(treeRoot);
		if (workingFileTree != null)
			((DefaultTreeModel)workingFileTree.getModel()).setRoot(treeRoot);

		if (p != null && pList.getSelectedItem() != p) {
			DISABLE_EVENTS = true;
			pList.setSelectedItem(p);
			DISABLE_EVENTS = false;
		}

		dontAsk = null;
		fireProjectLoaded(this, p, view);
	} //}}}

	//{{{ +getRoot() : VPTNode
	/**	Returns the root node of the current tree. */
	public synchronized VPTNode getRoot() {
		return treeRoot;
	} //}}}

	//{{{ +handleMessage(EBMessage) : void
	/** Handles an EditBus message.
	 */
	public void handleMessage(EBMessage msg) {
		if (msg instanceof ViewUpdate) {
			handleViewUpdateMessage((ViewUpdate) msg);
		} else if (treeRoot != null && treeRoot.isProject()) {
			if (msg instanceof EditorExitRequested) {
				handleEditorExitRequestedMessage((EditorExitRequested) msg);
			} else if (msg instanceof BufferUpdate) {
				handleBufferUpdateMessage((BufferUpdate) msg);
			} else if (config.isErrorListAvailable() && msg instanceof ErrorSourceUpdate) {
				handleErrorSourceUpdateMessage((ErrorSourceUpdate) msg);
			}
		}

	} //}}}

	//{{{ +handleViewUpdateMessage(ViewUpdate) : void
	/** Handles a ViewUpdate EditBus message.
	 */
	private void handleViewUpdateMessage(ViewUpdate vu) {
		// View closed? Remove from edit bus and from viewers list
		// EditPane changed? Fire a projectLoaded event for the global
		// listeners.
		if (vu.getView() == view) {
			if (vu.getWhat() == ViewUpdate.CLOSED) {
				viewers.remove(view);
				config.removePropertyChangeListener(ccl);
				listeners.remove(view);
				EditBus.removeFromBus(this);

				if (treeRoot.isProject()) {
					closeProject((VPTProject)treeRoot, config.getCloseFiles(), config.getRememberOpen());
				}

			} else if (vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED) {
				VPTProject current = null;
				if (treeRoot.isProject()) {
					current = (VPTProject) treeRoot;
					config.setLastProject(current.getName());
				} else {
					config.setLastProject(null);
				}
				ProjectViewerEvent evt = new ProjectViewerEvent(this, current);
				ArrayList lst = (ArrayList) listeners.get(null);
				if (lst != null)
				for (Iterator i = lst.iterator(); i.hasNext(); ) {
					((ProjectViewerListener)i.next()).projectLoaded(evt);
				}
			}
		}
	}//}}}

	//{{{ +handleEditorExitRequestedMessage(ViewUpdate) : void
	/** Handles a EditorExitRequested EditBus message.
	 */
	private void handleEditorExitRequestedMessage(EditorExitRequested eer) {
		// Editor is exiting, save info about current project
		ProjectViewer active = (ProjectViewer) viewers.get(eer.getView());
		if (active == this || active == null || active.treeRoot != treeRoot) {
			closeProject((VPTProject)treeRoot, false, config.getRememberOpen());
			config.setLastProject(((VPTProject)treeRoot).getName());
		}
	}//}}}

	//{{{ +handleBufferUpdateMessage(ViewUpdate) : void
	/** Handles a BufferUpdate EditBus message.
	 */
	private void handleBufferUpdateMessage(BufferUpdate bu) {
		if (bu.getView() != null && bu.getView() != view) return;

		VPTProject p = (VPTProject) treeRoot;

		VPTNode f = p.getChildNode(bu.getBuffer().getPath());

		boolean ask = false;
		if (f == null) {
			File file = new File(bu.getBuffer().getPath());
			String fileParentPath = file.getParent() + File.separator;
			String projectRootPath = p.getRootPath() + File.separator;
			ask = (config.getAskImport() != ProjectViewerConfig.ASK_NEVER &&
					bu.getWhat() == BufferUpdate.SAVED &&
					(dontAsk == null ||
						config.getAskImport() == ProjectViewerConfig.ASK_ALWAYS ||
						!dontAsk.contains(bu.getBuffer().getPath())) &&
					fileParentPath.startsWith(projectRootPath));
		}

		// Try to import newly created files to the project
		if (ask) {
			int res = JOptionPane.showConfirmDialog(view,
					jEdit.getProperty("projectviewer.import_new",
						new Object[] { bu.getBuffer().getName(), p.getName() }),
					jEdit.getProperty("projectviewer.import_new.title"),
					JOptionPane.YES_NO_OPTION);

			if(res == JOptionPane.YES_OPTION) {
				new NewFileImporter(p, this, bu.getBuffer().getPath()).doImport();
			} else if (config.getAskImport() == ProjectViewerConfig.ASK_ONCE) {
				if (dontAsk == null) {
					dontAsk = new HashSet();
				}
				dontAsk.add(bu.getBuffer().getPath());
			}
		}

		// Notifies trees when a buffer is closed (so it should not be
		// underlined anymore) or opened (should underline it).
		if (f != null) {
			if (bu.getWhat() == BufferUpdate.CLOSED) {
				if (folderTree != null) {
					((DefaultTreeModel)folderTree.getModel()).nodeChanged(f);
				}
				if (fileTree != null) {
					((DefaultTreeModel)fileTree.getModel()).nodeChanged(f);
				}
				if (workingFileTree != null) {
					((VPTWorkingFileListModel)workingFileTree.getModel())
						.removeOpenFile(f.getNodePath());
				}
			} else if (bu.getWhat() == BufferUpdate.LOADED) {
				if (folderTree != null) {
					((DefaultTreeModel)folderTree.getModel()).nodeChanged(f);
				}
				if (fileTree != null) {
					((DefaultTreeModel)fileTree.getModel()).nodeChanged(f);
				}
				if (workingFileTree != null) {
					((VPTWorkingFileListModel)workingFileTree.getModel())
						.addOpenFile(f.getNodePath());
				}
			} else if (bu.getWhat() == BufferUpdate.DIRTY_CHANGED) {
				if (folderTree != null) {
					((DefaultTreeModel)folderTree.getModel()).nodeChanged(f);
				}
				if (fileTree != null) {
					((DefaultTreeModel)fileTree.getModel()).nodeChanged(f);
				}
				if (workingFileTree != null) {
					((VPTWorkingFileListModel)workingFileTree.getModel()).nodeChanged(f);
				}
			}
		}
 	}//}}}

	//{{{ +handleErrorSourceUpdateMessage(ViewUpdate) : void
	/** Handles a ErrorSourceUpdate EditBus message.
	 */
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
			}
		}
		if ( esu.getWhat() == ErrorSourceUpdate.ERROR_SOURCE_ADDED ||
			esu.getWhat() == ErrorSourceUpdate.ERROR_SOURCE_REMOVED ||
			esu.getWhat() == ErrorSourceUpdate.ERRORS_CLEARED) {
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
		}
	}//}}}

	//{{{ +setEnabled(boolean) : void
	/** Enables or disables the viewer GUI. */
	public void setEnabled(boolean flag) {
		treePane.setEnabled(flag);
		pList.setEnabled(flag);
		if (folderTree != null) folderTree.setEnabled(flag);
		if (fileTree != null) fileTree.setEnabled(flag);
		if (workingFileTree != null) workingFileTree.setEnabled(flag);
		if (toolBar != null) {
			Component[] buttons = toolBar.getComponents();
			for (int i = 0; i < buttons.length; i++)
				buttons[i].setEnabled(flag);
		}
		super.setEnabled(flag);
	} //}}}

	//}}}

	//{{{ -class _VPTListCellRenderer_
	/** ListCellRenderer that understands VPTNodes. */
	private static class VPTListCellRenderer extends DefaultListCellRenderer {

		//{{{ +getListCellRendererComponent(JList, Object, int, boolean, boolean) : Component
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof VPTNode) {
				setText(((VPTNode)value).getName());
			}
			return this;
		} //}}}

	} //}}}

	//{{{ -class ProjectComboListener
	/** Listens for item changes in the project combo box. */
	private class ProjectComboListener implements ItemListener, Runnable {

		//{{{ +itemStateChanged(ItemEvent) : void
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() != ItemEvent.SELECTED || DISABLE_EVENTS) return;

			if(ie.getItem() instanceof VPTProject) {
				VPTProject p = (VPTProject) ie.getItem();
				new ProjectLoader(p.getName()).loadProject();
			} else {
				if(ie.getItem().toString().equals(CREATE_NEW_PROJECT)) {
					SwingUtilities.invokeLater(this);
					DISABLE_EVENTS = true;
					pList.setSelectedItem(treeRoot);
					DISABLE_EVENTS = false;
				} else {
					setProject(null);
				}
			}
		} //}}}

		//{{{ +run() : void
		/**
		 *	"Comestic" hack to let the combo box close before showing the
		 *	"new project" dialog.
		 */
		public void run() {
			EditProjectAction epa = new EditProjectAction(true);
			epa.setViewer(ProjectViewer.this);
			epa.actionPerformed(null);
		} //}}}

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
					(folderTree != null || fileTree != null || workingFileTree != null) );
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
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FOLDERS_OPT)) {
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

	//{{{ -class TreeStateLoader
	/** Loads the folder tree state from a string. */
	private class TreeStateLoader implements Runnable {

		private String state;

		//{{{ +TreeStateLoader(String) : <init>
		public TreeStateLoader(String state) {
			this.state = state;
		} //}}}

		//{{{ +run() : void
		/** "Run" method, called by the Swing runtime after a config option for one
		 *  or more of the trees has changed.
		 */
		public void run() {
			for(int i = 0; i < state.length(); i++) {
				if (state.charAt(i) == EXPANDED) {
					folderTree.expandRow(i+1);
				}
			}
		}//}}}

	} //}}}

	//{{{ -class ProjectLoader
	/** Loads a project in the background. */
	private class ProjectLoader implements Runnable {

		private String pName;

		//{{{ +ProjectLoader(String) : <init>
		public ProjectLoader(String pName) {
			this.pName = pName;
		} //}}}

		//{{{ +loadProject() : void
		public void loadProject() {
			if (ProjectManager.getInstance().isLoaded(pName)) {
				setProject(ProjectManager.getInstance().getProject(pName));
			} else {
				//VFSManager.getIOThreadPool().addWorkRequest(this, false);
				new Thread(this).start();
			}
		} //}}}

		//{{{ +run() : void
		public void run() {
			final JTree tree = getCurrentTree();
			final DefaultTreeModel tModel = (tree != null)
					? (DefaultTreeModel) tree.getModel() : null;
			final VPTNode oldRoot = treeRoot;
			treeRoot = null;

			try {
				SwingUtilities.invokeAndWait(
					new Runnable() {
						public void run() {
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
						}
					});
			} catch (InterruptedException ie) {
				// not gonna happen
			} catch (java.lang.reflect.InvocationTargetException ite) {
				// not gonna happen
			}

			final VPTProject p;
			synchronized (ProjectViewer.this) {
				p = ProjectManager.getInstance().getProject(pName);
			}

			try {
				SwingUtilities.invokeAndWait(
					new Runnable() {
						public void run() {
							synchronized (ProjectViewer.this) {
								treeRoot = oldRoot;
								if (tree != null) {
									tModel.setRoot(p);
									tree.setModel(tModel);
								}
								setProject(p);
								setEnabled(true);
							}
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

}

