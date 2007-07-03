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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
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
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

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
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.EBComponent;

import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
import org.gjt.sp.jedit.msg.EditorExitRequested;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;

import common.threads.WorkerThreadPool;

import errorlist.ErrorSource;
import errorlist.ErrorSourceUpdate;

import projectviewer.event.StructureUpdate;
import projectviewer.event.ViewerUpdate;

import projectviewer.gui.ProjectComboBox;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.ProjectTreePanel;

import projectviewer.action.Action;
import projectviewer.action.ExpandAllAction;
import projectviewer.action.CollapseAllAction;
import projectviewer.action.EditProjectAction;
import projectviewer.action.FileImportAction;
import projectviewer.action.NodeRemoverAction;
import projectviewer.action.NodeRenamerAction;
import projectviewer.action.OldStyleAddFileAction;
import projectviewer.config.ProjectViewerConfig;
import projectviewer.importer.NewFileImporter;
//}}}

/**
 *	Main GUI for the project viewer plugin.
 *
 *	@author		Marcelo Vanzin (with much code from original version)
 *	@version	$Id$
 */
public final class ProjectViewer extends JPanel
	implements HierarchyListener, DefaultFocusComponent, EBComponent {

	//{{{ Static members
	private static final ProjectViewerConfig config;
	private static final Map<View,ViewerEntry> viewers;
	private static final List<Action> actions;

	//{{{ Static Initialization
	/**
	 *	Initializes the default actions, and gets the PV plugins from the list
	 *	of active jEdit plugins.
	 */
	static {
		// Default toolbar actions
		config = ProjectViewerConfig.getInstance();
		viewers = new WeakHashMap<View,ViewerEntry>();
		actions = new ArrayList<Action>();
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
		Collection removed = PVActions.prune(actions, jar);
		if (removed != null) {
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
		return (ve != null) ? ve.dockable : null;
	} //}}}

	//{{{ Tree Changes Broadcast Methods

	//{{{ +_nodeStructureChanged(VPTNode)_ : void
	/**
	 *	Notify all project viewer instances of a change in a node's structure.
	 */
	public static void nodeStructureChanged(VPTNode node) {
		for (ViewerEntry ve : viewers.values()) {
			if (ve.dockable != null
				&& ve.dockable.getRoot().isNodeDescendant(node))
			{
				ve.dockable.getTreePanel().nodeStructureChanged(node);
			}
		}
	} //}}}

	//{{{ +_nodeChanged(VPTNode)_ : void
	/** Notify all project viewer instances of a change in a node. */
	public static void nodeChanged(VPTNode node) {
		for (ViewerEntry ve : viewers.values()) {
			if (ve.dockable != null
				&& ve.dockable.getRoot().isNodeDescendant(node))
			{
				ve.dockable.getTreePanel().nodeChanged(node);
				if (node == ve.dockable.getRoot()
					&& ve.dockable.pList != null)
				{
					// force a refresh of the "selected node" of the "combo"
					ve.dockable.pList.setSelectedNode(node);
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
		int[] indexes = new int[] { idx };

		parent.insert(child, idx);

		for (ViewerEntry ve : viewers.values() ) {
			if (ve.dockable != null
				&& ve.dockable.getRoot().isNodeDescendant(parent))
			{
				ve.dockable.getTreePanel().nodesWereInserted(parent, indexes);
			}
		}
	} //}}}

	//{{{ +_nodeStructureChangedFlat(VPTNode)_ : void
	/**
	 *	Notify all "flat trees" in any project viewer instances of a change in
	 *	a node's structure.
	 */
	public static void nodeStructureChangedFlat(VPTNode node) {
		for (ViewerEntry ve : viewers.values() ) {
			if (ve.dockable != null
				&& ve.dockable.getRoot().isNodeDescendant(node))
			{
				ve.dockable.getTreePanel().flatStructureChanged(node);
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
		int[] idx = new int[] { index };
		Object[] removed = new Object[] { child };

		parent.remove(index);

		for (ViewerEntry ve : viewers.values() ) {
			if (ve.dockable != null
				&& ve.dockable.getRoot().isNodeDescendant(parent))
			{
				ve.dockable.getTreePanel().nodesWereRemoved(parent, idx, removed);
			}
		}
	} //}}}

	//{{{ +_projectRemoved(Object, VPTProject)_ : void
	/**
	 *	Notify all "flat trees" in any project viewer instances of a change in
	 *	a node's structure. Then, rebuild the project combo boxes.
	 *
	 *	@since PV 3.0.0
	 */
	public static void projectRemoved(VPTProject p) {
		VPTNode parent = (VPTNode) p.getParent();
		int index = parent.getIndex(p);
		int[] idx = new int[] { index };
		Object[] removed = new Object[] { p };

		parent.remove(index);

		for (ViewerEntry ve : viewers.values() ) {
			if (ve.dockable == null) {
				continue;
			}

			if (p == ve.dockable.getRoot()) {
				ve.dockable.setRootNode(VPTRoot.getInstance());
				continue;
			}

			if (ve.dockable.getRoot().isNodeDescendant(parent)) {
				ve.dockable.getTreePanel().nodesWereRemoved(parent, idx, removed);
			}
		}
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
				modifyViewTitle(aView, n);
			}
		}

		if (ve.dockable == null) {
			// Loads projects if not yet loaded
			if (n.isProject()
					&& !ProjectManager.getInstance().isLoaded(n.getName())) {
				ProjectManager.getInstance().getProject(n.getName());
			}

			// Fires events if the dockable is not available
			// (setRootNode() fires events when the dockable is available)
			ViewerUpdate upd = new ViewerUpdate(aView,
												n,
												ViewerUpdate.Type.PROJECT_LOADED);
			EditBus.send(upd);
		} else if (!ve.dockable.isLoadingProject) {
			ve.dockable.setRootNode(n);
		}

	} //}}}

	//{{{ +_getActiveNode(View)_ : VPTNode
	/**
	 *	Return the current "active" node (opened project/group)
	 *	for the view. Returns null if no active node is known for the view.
	 *
	 *	@since	PV 2.1.0
	 */
	public static VPTNode getActiveNode(View aView) {
		if (aView == null) {
			aView = jEdit.getActiveView();
		}

		if (aView == null) {
			// cowardly bail out when jEdit is starting up.
			return null;
		}

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

	//{{{ -_modifyViewTitle(View, VPTNode)_ : void
	/**
	 *	Mofifies the title of a jEdit view, adding information about
	 *	the given node at the end of the current string.
	 */
	private static void modifyViewTitle(final View view, final VPTNode info) {
		// (info == null) might happen during jEdit startup.
		if (info != null
			&& config.getShowProjectInTitle())
		{
			view.updateTitle();
			StringBuffer title = new StringBuffer(view.getTitle());
			title.append(" [");
			if (info.isGroup()) {
				title.append("Group: ");
			} else {
				title.append("Project: ");
			}
			title.append(info.getName()).append(']');
			view.setTitle(title.toString());
		}
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

	//{{{ #_cleanViewEntry(View)_ : void
	/**
	 *	Removes the "viewer entry" related to the given view. Called
	 *	by the ProjectPlugin class when a view closed message is
	 *	received.
	 */
	protected static void cleanViewEntry(View aView) {
		viewers.remove(aView);
	} //}}}

	//}}}

	//{{{ Constants

	private final static String TREE_STATE_PROP = "projectviewer.folder_tree_state";

	//}}}

	//{{{ Attributes
	private View					view;
	private HashSet					dontAsk;

	private JToolBar				toolBar;

	private JPanel					topPane;
	private ProjectTreePanel		treePanel;
	private ProjectComboBox			pList;

	private VPTNode					treeRoot;
	private ConfigChangeListener	ccl;

	private boolean					isChangingBuffers;
	private boolean					isClosingProject;
	private volatile boolean		isLoadingProject;
	private volatile boolean		noTitleUpdate;
	//}}}

	//{{{ +ProjectViewer(View) : <init>
	/**
	 *	Create a new <code>ProjectViewer</code>. Only one instance is allowed
	 *	per view.
	 *
	 *	@param	aView  The jEdit view where the viewer is to be created.
	 *	@throws UnsupportedOperationException	If a viewer is already instantiated
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
		treeRoot = VPTRoot.getInstance();
		isLoadingProject = false;

		addHierarchyListener(this);

		buildGUI();

		ccl = new ConfigChangeListener();
		config.addPropertyChangeListener(ccl);

		// Register the dockable window in the viewer list
		ViewerEntry ve = (ViewerEntry) viewers.get(aView);
		if (ve == null) {
			ve = new ViewerEntry();
			ve.node = config.getLastNode();
			viewers.put(aView, ve);
		}
		ve.dockable = this;
		EditBus.addToBus(this);
		setRootNode(ve.node);
		isClosingProject = false;
		noTitleUpdate = false;
		setChangingBuffers(false);
	} //}}}

	//{{{ Private methods

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
		treePanel = new ProjectTreePanel(this);

		topPane = new JPanel(new BorderLayout());

		pList = new ProjectComboBox(view);

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		box.add(pList);
		box.add(Box.createGlue());

		topPane.add(BorderLayout.CENTER, box);

		treePanel.loadGUI();
		showToolBar(config.getShowToolBar());
		add(BorderLayout.CENTER, treePanel);
		add(BorderLayout.NORTH, topPane);

	} //}}}

	//{{{ -closeGroup(VPTGroup, VPTNode, boolean) : void
	private void closeGroup(VPTGroup group, VPTNode ignore, boolean ignoreClose) {
		for (int i = 0; i < group.getChildCount(); i++) {
			VPTNode child = (VPTNode) group.getChildAt(i);
			if (child != ignore) {
				if (child.isGroup()) {
					closeGroup((VPTGroup)child, ignore, ignoreClose);
				} else if (ProjectManager.getInstance().isLoaded(child.getName())){
					closeProject((VPTProject)child, ignoreClose);
				}
			}
		}
	} //}}}

	//{{{ -closeProject(VPTProject, boolean) : void
	/**
	 *	Closes a project: searches the open buffers for files related to the
	 *	given project and closes them (if desired) and/or saves them to the
	 *	open list (if desired).
	 */
	private void closeProject(VPTProject p, boolean ignoreClose) {
		setChangingBuffers(true);
		noTitleUpdate = true;
		isClosingProject = true;

		// check to see if project is active in some other viewer, so we
		// don't mess up that guy.
		if (config.getCloseFiles() && !ignoreClose) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ViewerEntry ve = (ViewerEntry) it.next();
				if (ve.dockable != this && ve.node.isNodeDescendant(p)) {
					noTitleUpdate = false;
					setChangingBuffers(false);
					isClosingProject = true;
					return;
				}
			}
		}

		// close files & populate "remember" list
		if ((config.getCloseFiles() && !ignoreClose) || config.getRememberOpen()) {
			Buffer[] bufs = jEdit.getBuffers();

			String currFile = null;
			if (p.getChildNode(view.getBuffer().getPath()) != null) {
				currFile = view.getBuffer().getPath();
			}

			for (int i = 0; i < bufs.length; i++) {
				if (p.getChildNode(bufs[i].getPath()) != null) {
					if (config.getRememberOpen() && !bufs[i].getPath().equals(currFile)) {
						p.addOpenFile(bufs[i].getPath());
					}
					if (config.getCloseFiles() && !ignoreClose) {
						jEdit.closeBuffer(view, bufs[i]);
					}
				}
			}

			if (config.getRememberOpen() && currFile != null) {
				p.addOpenFile(currFile);
			}
		}

		// saves the folder tree state
		String state = treePanel.getFolderTreeState(p);
		if (state != null) {
			p.setProperty(TREE_STATE_PROP, state);
		} else {
			p.removeProperty(TREE_STATE_PROP);
		}
		isClosingProject = true;
		noTitleUpdate = false;
		setChangingBuffers(false);
	} //}}}

	//{{{ -openProject(VPTProject) : void
	/** Opens all the files that were previously opened in the project. */
	private void openProject(final VPTProject p) {
		setChangingBuffers(true);
		if (config.getRememberOpen()) {
			for (Iterator i = p.getOpenFiles(); i.hasNext(); ) {
				String next = (String) i.next();
				if (i.hasNext())
					jEdit.openFile(null, next);
				else
					jEdit.openFile(view, next);
			}
		}
		p.clearOpenFiles();

		// loads tree state from the project, if saved
		final String state = p.getProperty(TREE_STATE_PROP);
		if (state != null && treePanel.getFolderTree() != null) {
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						treePanel.setFolderTreeState(p, state);
					}
				}
			);
		}
		setChangingBuffers(false);
	} //}}}

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

	//{{{ -unloadInactiveProjects(VPTNode) : void
	/** Checks if some of the projects that are loaded can be unloaded. */
	private void unloadInactiveProjects(VPTNode newRoot) {
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
			if (pm.isLoaded(p.getName()) && (p != newRoot)
				&& (active == null || !active.contains(p.getName())))
			{
				pm.unloadProject(p);
				getTreePanel().projectClosed(p);
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

	//{{{ -unload() : void
	/**
	 *	Cleans up the current open project, and also clean up the
	 *	loaded project list when unloading PV from the current view.
	 */
	private void unload() {
		EditBus.removeFromBus(this);
		if (treeRoot != null && treeRoot.isProject()) {
			closeProject((VPTProject)treeRoot, false);
		}
		unloadInactiveProjects(null);
		ViewerEntry ve = (ViewerEntry) viewers.get(view);
		if (ve != null) {
			ve.dockable = null;
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
	 *	Returns a list of Strings containing the file paths of the selected
	 *	file and folder nodes. This is mostly a utility method so other
	 *	plugins/macros can peform actions on a selection of files.
	 */
	public List getSelectedFilePaths() {
		List obfp = new ArrayList();
		JTree tree = getCurrentTree();
		if (tree == null)
			return null;

		if (tree.getSelectionPaths() != null) {
			TreePath[] paths= tree.getSelectionPaths();

			for (int i =0; i < paths.length; i++) {
				VPTNode nd = (VPTNode)paths[i].getLastPathComponent();
				if (nd.isFile()) {
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
		return treePanel.getCurrentTree();
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

		// close old project
		if (treeRoot != null && !n.isNodeDescendant(treeRoot)) {
			if (treeRoot.isProject()) {
				closeProject((VPTProject) treeRoot, false);
			} else {
				closeGroup((VPTGroup)treeRoot, n, false);
			}
		}

		// try to release some memory.
		if (!n.isNodeDescendant(treeRoot)) {
			unloadInactiveProjects(n);
		}

		// set the new root
		ViewerEntry ve = (ViewerEntry) viewers.get(view);
		if (n.isProject()) {
			VPTProject p = (VPTProject) n;
			if (!ProjectManager.getInstance().isLoaded(p.getName())) {
				setEnabled(false);
				new ProjectLoader(p.getName()).loadProject();
				return;
			}
			openProject(p);
			if (ve.node != p) {
				sendUpdate(p, ViewerUpdate.Type.PROJECT_LOADED);
				ve.node = p;
			}
		} else if (n.isGroup()){
			if (ve.node != n) {
				sendUpdate(n, ViewerUpdate.Type.GROUP_ACTIVATED);
				ve.node = n;
			}
		}

		treeRoot = n;
		treePanel.setRoot(treeRoot);

		dontAsk = null;
		ProjectManager.getInstance().fireDynamicMenuChange();
		pList.setSelectedNode(treeRoot);
		modifyViewTitle(view, treeRoot);
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
	/** Returns the root node of the current tree. */
	public VPTNode getRoot() {
		waitForLoadLock();
		return treeRoot;
	} //}}}

	//{{{ +setEnabled(boolean) : void
	/** Enables or disables the viewer GUI. */
	public void setEnabled(boolean flag) {
		treePanel.setEnabled(flag);
		pList.setEnabled(flag);
		if (toolBar != null) {
			Component[] buttons = toolBar.getComponents();
			for (int i = 0; i < buttons.length; i++)
				buttons[i].setEnabled(flag);
		}
		super.setEnabled(flag);
	} //}}}

	//{{{ +setChangingBuffers(boolean) : void
	/**
	 *	Method intended for use by classes that manage clicks on the
	 *	project trees to open buffers in jEdit; by setting this flag
	 *	to true, the auto-selecting of the new active buffer in jEdit
	 *	is temporarily disabled, preventing the tree from shifting
	 *	around when the user is interacting with it.
	 *
	 *	@since	PV 2.1.1
	 */
	public void setChangingBuffers(boolean flag) {
		isChangingBuffers = flag;
	} //}}}

	public ProjectTreePanel getTreePanel()
	{
		return treePanel;
	}

	/**
	 *	Sends a project viewer update message with the given data.
	 *
	 *	@since PV 3.0.0
	 */
	public void sendUpdate(VPTNode n, ViewerUpdate.Type type)
	{
		ViewerUpdate update = new ViewerUpdate(this, n, type);
		EditBus.send(update);
	}

	//}}}

	//{{{ Message handling

	//{{{ +handleMessage(EBMessage) : void
	/** Handles an EditBus message. */
	public void handleMessage(EBMessage msg) {
		if (msg instanceof ViewUpdate) {
			handleViewUpdateMessage((ViewUpdate) msg);
		} else if (msg instanceof BufferUpdate) {
			handleBufferUpdateMessage((BufferUpdate) msg, treeRoot);
		} else if (msg instanceof DynamicMenuChanged) {
			handleDynamicMenuChanged((DynamicMenuChanged)msg);
		} else if (msg instanceof EditPaneUpdate) {
			handleEditPaneUpdate((EditPaneUpdate)msg);
		} else if (treeRoot != null && msg instanceof EditorExitRequested) {
			if (jEdit.getActiveView() != view) {
				config.setLastNode(treeRoot);
			}
			if (treeRoot.isGroup()) {
				closeGroup((VPTGroup)treeRoot, null, true);
			} else {
				closeProject((VPTProject)treeRoot, true);
			}
		} else if (treeRoot != null && treeRoot.isProject()
				   && !(msg instanceof PluginUpdate))
		{
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
	/** Handles a ViewUpdate EditBus message. Checks only whether
		the EditPane was changed, and focus the file corresponding
		to the buffer on the EditPane on the PV tree. */
	private void handleViewUpdateMessage(ViewUpdate vu) {
		if (vu.getView() == view &&
			vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED)
		{
			PVActions.focusActiveBuffer(view, treeRoot);
		}
		if (vu.getWhat() == ViewUpdate.CLOSED) {
			if (vu.getView() == view) {
				config.setLastNode(treeRoot);
				unload();
				removeHierarchyListener(this);
			}
			viewers.remove(vu.getView());
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
		if ((bu.getWhat() == BufferUpdate.CLOSED
			 || bu.getWhat() == BufferUpdate.LOADED
			 || bu.getWhat() == BufferUpdate.DIRTY_CHANGED)
		) {
			if (!noTitleUpdate)
				modifyViewTitle(view, treeRoot);
			if (where != null && where.isProject()) {
				VPTNode f = ((VPTProject)where).getChildNode(bu.getBuffer().getPath());
				if (f != null) {
					if (bu.getWhat() == BufferUpdate.CLOSED) {
						getTreePanel().projectFileClosed(f);
					} else if (bu.getWhat() == BufferUpdate.LOADED) {
						getTreePanel().projectFileOpened(f);
					}

					if (!isClosingProject && bu.getWhat() == BufferUpdate.CLOSED) {
						((VPTProject)where).removeOpenFile(bu.getBuffer().getPath());
					}

					ProjectViewer.nodeChanged(f);
					return true;
				}
			} else if (where != null) {
				for (int i = 0; i < where.getChildCount(); i++) {
					if (handleBufferUpdateMessage(bu, (VPTNode)where.getChildAt(i))) {
						return true;
					}
				}
			}
		}

		return false;
	}//}}}

	//{{{ -handleEditPaneUpdate(EditPaneUpdate) : void
	private void handleEditPaneUpdate(EditPaneUpdate msg) {
		if (msg.getWhat() == EditPaneUpdate.BUFFER_CHANGED
			&& config.getFollowCurrentBuffer()
			&& msg.getEditPane().getView() == view
			&& !isChangingBuffers)
		{
			PVActions.focusActiveBuffer(view, treeRoot);
			modifyViewTitle(view, treeRoot);
		}
	} //}}}

	//}}}

	//{{{ +hierarchyChanged(HierarchyEvent) : void
	public void hierarchyChanged(HierarchyEvent he) {
		if (he.getChanged() == this && !isDisplayable() &&
				((he.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED)
					== HierarchyEvent.DISPLAYABILITY_CHANGED))
		{
			config.setLastNode(treeRoot);
			unload();
		}
	} //}}}

	//{{{ -class ConfigChangeListener
	/** Listens for changes in the PV configuration. */
	private class ConfigChangeListener implements PropertyChangeListener, Runnable {

		private boolean willRun = false;

		//{{{ +propertyChange(PropertyChangeEvent) : void
		/** Listens for property change events in the plugin's configuration.
		 *	Shows/Hides the toolbar and the trees, according to the user's wish.
		 *
		 * @param  evt	Description of Parameter
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			// Toolbar show/hide.
			if (evt.getPropertyName().equals(ProjectViewerConfig.SHOW_TOOLBAR_OPT)) {
				showToolBar(((Boolean)evt.getNewValue()).booleanValue()
							&& treePanel.getTreeCount() > 0);
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

			if (evt.getPropertyName().equals(ProjectViewerConfig.CASE_INSENSITIVE_SORT_OPT)) {
				treeRoot.sortChildren(true);
				String state = treePanel.getFolderTreeState(treeRoot);
				nodeStructureChanged(treeRoot);
				nodeStructureChangedFlat(treeRoot);
				treePanel.setFolderTreeState(treeRoot, state);
				repaint();
				return;
			}

			if (evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FILES_OPT) ||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_WFILES_OPT) ||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FOLDERS_OPT)||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_COMPACT_OPT) ||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FILTERED_OPT))
			{
				if (!willRun) {
					SwingUtilities.invokeLater(this);
					willRun = true;
				}
				return;
			}

		}//}}}

		//{{{ +run() : void
		/** "Run" method, called by the Swing runtime after a config option for one
		 *	or more of the trees has changed.
		 */
		public void run() {
			treePanel.loadGUI();
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

			WorkerThreadPool.getSharedInstance().addRequest(this);
		} //}}}

		//{{{ +run() : void
		public void run() {
			final VPTProject p;
			p = ProjectManager.getInstance().getProject(pName);

			synchronized (ProjectViewer.this) {
				isLoadingProject = false;
				ProjectViewer.this.notifyAll();
			}

			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						if (tree != null) {
							tModel.setRoot(p);
							tree.setModel(tModel);
						}
						setRootNode(p);
						setEnabled(true);
					}
				}
			);
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
			if (esu.getWhat() == ErrorSourceUpdate.ERROR_ADDED
				|| esu.getWhat() == ErrorSourceUpdate.ERROR_REMOVED)
			{
				VPTProject p = (VPTProject) treeRoot;
				ErrorSource.Error error = esu.getError();
				VPTNode f = p.getChildNode(error.getFilePath());
				if (f != null) {
					treePanel.nodeChanged(f);
				}
			}
			if (esu.getWhat() == ErrorSourceUpdate.ERROR_SOURCE_ADDED
				|| esu.getWhat() == ErrorSourceUpdate.ERROR_SOURCE_REMOVED
				|| esu.getWhat() == ErrorSourceUpdate.ERRORS_CLEARED)
			{
				treePanel.repaint();
			}
		}//}}}

	} //}}}

	public void focusOnDefaultComponent()
	{
		getCurrentTree().requestFocus();

	}

}

