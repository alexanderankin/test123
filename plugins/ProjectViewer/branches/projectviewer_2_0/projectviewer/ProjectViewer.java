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
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ItemListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBComponent;

import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.msg.BufferUpdate;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTContextMenu;
import projectviewer.vpt.VPTCellRenderer;
import projectviewer.vpt.VPTFileListModel;
import projectviewer.vpt.VPTSelectionListener;
import projectviewer.vpt.VPTWorkingFileListModel;

import projectviewer.event.ProjectViewerEvent;
import projectviewer.event.ProjectViewerListener;

import projectviewer.action.Action;
import projectviewer.action.EditProjectAction;
import projectviewer.config.ProjectViewerConfig;
import projectviewer.importer.SingleFileImporter;
//}}}

/**
 *  A Project Viewer plugin for jEdit.
 *
 *	@version    $Id$
 */
public final class ProjectViewer extends JPanel
								 implements EBComponent {

	//{{{ Static members
	
	private static final ProjectViewerConfig config = ProjectViewerConfig.getInstance();
	private static final HashMap viewers = new HashMap();
	private static final HashMap listeners = new HashMap();
	private static final ArrayList actions = new ArrayList();
	private static boolean DISABLE_EVENTS;

	//{{{ Default toolbar actions (static initializer)
	static {
		actions.add(new EditProjectAction());
	} //}}}
	
	//{{{ registerAction(Action) method
	/** Adds an action to be shown on the context menu. */
	public static void registerAction(Action action) {
		actions.add(action);
		actionsChanged();
	} //}}}
	
	//{{{ unregisterAction(Action) method
	/** Removes an action from the context menu. */
	public static void unregisterAction(Action action) {
		actions.remove(action);
		actionsChanged();
	} //}}}
	
	//{{{ getViewer(View) method
	/**
	 *	Returns the viewer associated with the given view, or null if none
	 *	exists.
	 */
	public static ProjectViewer getViewer(View view) {
		return (ProjectViewer) viewers.get(view);
	} //}}}
	
	//{{{ addProjectViewerListener(ProjectViewerListener, View) method
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
	
	//{{{ removeProjectViewerListener(ProjectViewerListener, View) method
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
	
	//{{{ updateProjectCombos() method
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
			
			for (Iterator it2 = ProjectManager.getInstance().getProjects(); it.hasNext(); ) {
				v.pList.addItem(it2.next());
			}
			
			v.pList.setSelectedItem(v.treeRoot);
		}
		
		DISABLE_EVENTS = false;

	} //}}}
	
	//{{{ actionsChanged()
	/** Reloads the action list for the toolbar. */
	private static void actionsChanged() {
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ProjectViewer v = (ProjectViewer) it.next();
			if (v.toolBar != null)
				v.populateToolBar();
		}
	} //}}}
		
	//{{{ Tree Changes Broadcast Methods
	
	//{{{ nodeStructureChanged(VPTNode) node
	/**
	 *	Notify all project viewer instances of a change in a node's structure.
	 */
	public static void nodeStructureChanged(VPTNode node) {
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ProjectViewer v = (ProjectViewer) it.next();
			if (v.folderTree != null) {
				((DefaultTreeModel)v.folderTree.getModel()).nodeStructureChanged(node);
			}
			if (v.fileTree != null) {
				((DefaultTreeModel)v.fileTree.getModel()).nodeStructureChanged(node);
			}
			
			if (v.workingFileTree != null) {
				((DefaultTreeModel)v.workingFileTree.getModel()).nodeStructureChanged(node);
			}
		}
	} //}}}
	
	//{{{ nodeChanged(VPTNode) node
	/** Notify all project viewer instances of a change in a node. */
	public static void nodeChanged(VPTNode node) {
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ProjectViewer v = (ProjectViewer) it.next();
			if (v.folderTree != null) {
				((DefaultTreeModel)v.folderTree.getModel()).nodeChanged(node);
			}
			if (v.fileTree != null) {
				((DefaultTreeModel)v.fileTree.getModel()).nodeChanged(node);
			}
			
			if (v.workingFileTree != null) {
				((DefaultTreeModel)v.workingFileTree.getModel()).nodeChanged(node);
			}
		}
	} //}}}
	
	//{{{ insertNodeInto(VPTNode, VPTNode)
	/**
	 *	Notifies all trees in all instances of ProjectViewer that a node has
	 *	been inserted to one of its nodes.
	 */
	public static void insertNodeInto(VPTNode child, VPTNode parent) {
		int idx = parent.findIndexForChild(child);
		if (config.getShowFoldersTree()) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.folderTree != null) {
					((DefaultTreeModel)v.folderTree.getModel()).insertNodeInto(child, parent, idx);
				}
			}
		} else {
			parent.add(child);
		}
	} //}}}
	
	//{{{ nodeStructureChangedFlat(VPTNode) method
	/**
	 *	Notify all "flat trees" in any project viewer instances of a change in
	 *	a node's structure.
	 */
	public static void nodeStructureChangedFlat(VPTNode node) {
		if (config.getShowFilesTree() || config.getShowWorkingFilesTree()) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.fileTree != null) {
					((DefaultTreeModel)v.fileTree.getModel()).nodeStructureChanged(node);
				}
				
				if (v.workingFileTree != null) {
					((DefaultTreeModel)v.workingFileTree.getModel()).nodeStructureChanged(node);
				}
			}
		}
	} //}}}
	 
	//{{{ removeNodeFromParent(VPTNode)
	/**
	 *	Notifies all trees in all instances of ProjectViewer that a node has
	 *	been removed from its parent.
	 */
	public static void removeNodeFromParent(VPTNode child) {
		VPTNode parent = (VPTNode) child.getParent();
		if (config.getShowFoldersTree()) {
			boolean removed = false;
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.folderTree != null) {
					if (!removed) {
						((DefaultTreeModel)v.folderTree.getModel()).removeNodeFromParent(child);
						removed = true;
					} else {
						((DefaultTreeModel)v.folderTree.getModel()).nodeStructureChanged(parent);
					}
				}
			}
		} else {
			parent.remove(child);
		}
	} //}}}
	 
	//{{{ projectRemoved(VPTProject) method
	/**
	 *	Notify all "flat trees" in any project viewer instances of a change in
	 *	a node's structure. Then, rebuild the project combo boxes.
	 */
	public static void projectRemoved(VPTProject p) {
		if (config.getShowFilesTree() || config.getShowWorkingFilesTree()) {
			for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
				ProjectViewer v = (ProjectViewer) it.next();
				if (v.fileTree != null) {
					((VPTFileListModel)v.fileTree.getModel()).removeRef(p);
				}
				
				if (v.workingFileTree != null) {
					((VPTWorkingFileListModel)v.workingFileTree.getModel()).removeRef(p);
				}
			}
		}
		updateProjectCombos();
	} //}}}
	 
	//}}}
	
	//}}}
			
	//{{{ Constants
	
	public final static String CREATE_NEW_PROJECT = jEdit.getProperty("projectviewer.create_project");
	
	private final static String FOLDERS_TAB_TITLE = "projectviewer.folderstab";
	private final static String FILES_TAB_TITLE = "projectviewer.filestab";
	private final static String WORKING_FILES_TAB_TITLE = "projectviewer.workingfilestab";

	//}}}
			
	//{{{ Attributes
	private View 					view;
	
	private JTree					folderTree;
	private JTree					fileTree;
	private JTree					workingFileTree;
	private JToolBar				toolBar;
	
	private JPanel					topPane;
	private JTabbedPane				treePane;
	private JComboBox				pList;
	
	private VPTNode 				treeRoot;
	private VPTContextMenu			vcm;
	private VPTSelectionListener	vsl;
	private ConfigChangeListener	ccl;
	//}}}

	//{{{ Constructor
	/** Create a new <code>ProjectViewer</code>.
	 *
	 * @param  aView  Description of Parameter
`	 */
	public ProjectViewer(View aView) {
		super(new BorderLayout());
		view = aView;
		viewers.put(aView, this);
		vcm = new VPTContextMenu(this);
		vsl = new VPTSelectionListener(this);
		treeRoot = VPTRoot.getInstance();
		buildGUI();
		
		ccl = new ConfigChangeListener();
		config.addPropertyChangeListener(ccl);
		EditBus.addToBus(this);
	} //}}}

	//{{{ Private methods 
	
	//{{{ createTree(TreeModel) method
	/** Creates a new tree to be added to the viewer. */
	private JTree createTree(TreeModel model) {
		JTree tree = new JTree(model);
		tree.setCellRenderer(new VPTCellRenderer());
		tree.setBorder(BorderFactory.createEtchedBorder());
		
		// don't change order!
		tree.addMouseListener(vsl);
		tree.addMouseListener(vcm);
		
		//model.addTreeModelListener(vsl);
		tree.addTreeSelectionListener(vsl);
		return tree;
	} //}}}
	
	//{{{ populateToolBar() method
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
	
	//{{{ buildGUI() method
	/** Builds the viewer GUI. */
	private void buildGUI() {
		treePane = new JTabbedPane();
		add(BorderLayout.CENTER, treePane);
		
		showTrees();
		
		pList = new JComboBox();
		pList.setRenderer(new VPTListCellRenderer());
		
		pList.addItem(CREATE_NEW_PROJECT);
		pList.addItem(VPTRoot.getInstance());
		
		for (Iterator it = ProjectManager.getInstance().getProjects(); it.hasNext(); ) {
			pList.addItem(it.next());
		}
		
		pList.setSelectedItem(treeRoot);
		pList.addItemListener(new ProjectComboListener());
		
		topPane = new JPanel(new BorderLayout());
		
		if (config.getShowToolBar()) {
			toolBar = new JToolBar();
			populateToolBar();
			topPane.add(BorderLayout.NORTH, toolBar);
		}
		
		topPane.add(BorderLayout.SOUTH, pList);
		
		add(BorderLayout.NORTH, topPane);
		
	} //}}}

	//{{{ fireProjectLoaded(VPTProject) method
	/**
	 *	Fires an event for the loading of a project. Notify all the listeners
	 *	registered for this instance's view and listeners registered for all
	 *	views.
	 */
	private void fireProjectLoaded(VPTProject p) {
		ProjectViewerEvent evt = new ProjectViewerEvent(this, p);
		
		ArrayList lst = (ArrayList) listeners.get(view);
		if (lst != null)
		for (Iterator i = lst.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).projectLoaded(evt);
		}
		
		lst = (ArrayList) listeners.get(null);
		if (lst != null)
		for (Iterator i = lst.iterator(); i.hasNext(); ) {
			((ProjectViewerListener)i.next()).projectLoaded(evt);
		}
		
	} //}}}
	
	//{{{ showTrees() method
	/** 
	 *	Loads the trees (folders, files, working files) into the view, deciding
	 *  what to show according to the configuration of the plugin
	 */
	private void showTrees() {
		treePane.removeAll();
		int count = 0;

		// Folders tree
		if(config.getShowFoldersTree()) {
			if(folderTree == null) {
				folderTree = createTree(new DefaultTreeModel(treeRoot, true));
			}
			count++;
			treePane.addTab(jEdit.getProperty(FOLDERS_TAB_TITLE), new JScrollPane(folderTree));
		} else {
			folderTree = null;
		}

		// Files tree
		if(config.getShowFilesTree()) {
			if(fileTree == null) {
				fileTree = createTree(new VPTFileListModel(treeRoot));
			}
			count++;
			treePane.addTab(jEdit.getProperty(FILES_TAB_TITLE), new JScrollPane(fileTree));
		} else {
			fileTree = null;
		}

		// Working files tree
		if(config.getShowWorkingFilesTree()) {
			if(workingFileTree == null) {
				VPTWorkingFileListModel model = new VPTWorkingFileListModel(treeRoot);
				workingFileTree = createTree(model);
				EditBus.addToBus(model);
			}
			count++;
			treePane.addTab(jEdit.getProperty(WORKING_FILES_TAB_TITLE), new JScrollPane(workingFileTree));
		} else {
			if (workingFileTree != null) {
				EditBus.removeFromBus((VPTWorkingFileListModel)workingFileTree.getModel());
				workingFileTree = null;
			}
		}

		if (count == 0) {
			if (toolBar != null) {
				topPane.remove(toolBar);
				toolBar.removeAll();
				toolBar = null;
			}
		} else if (toolBar == null && config.getShowToolBar()) {
			toolBar = new JToolBar();
			populateToolBar();
			topPane.add(BorderLayout.NORTH, toolBar);
		}
		
		treePane.setSelectedIndex(0);
	}//}}}
	
	//}}}

	//{{{ Public Methods
	
	//{{{ setStatus(String) method
	/** Changes jEdit's status bar message for the current view. */
	public void setStatus(String message) {
		view.getStatus().setMessageAndClear(message);
	} //}}}
	
	//{{{ getSelectedNode() method
	/** Returns the currently selected node in the tree. */
	public VPTNode getSelectedNode() {
		if (getCurrentTree().getSelectionPath() != null) {
			return (VPTNode) getCurrentTree().getSelectionPath().getLastPathComponent();
		} else {
			return null;
		}
	} //}}}
	
	//{{{ getCurrentTree() method
	/** Returns the currently active tree. */
	public JTree getCurrentTree() {
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
				Log.log(Log.DEBUG, this, "invalid tabnumber :" + treePane.getSelectedIndex());
				return null;
		}
	} //}}}
	
	//{{{ getView() method
	/** Returns the View associated with this instance. */
	public View getView() {
		return view;
	} //}}}
	
	//{{{ setProject(VPTProject) method
	/**
	 *	Sets the given project to be the root of the tree. If "p" is null,
	 *	then the root node is set to the "VPTRoot" node.
	 */
	public void setProject(VPTProject p) {
		// TODO: deactivate current project, etc?
		if (p != null) {
			treeRoot = p;
		} else {
			treeRoot = VPTRoot.getInstance();
		}
		if (folderTree != null) 
			((DefaultTreeModel)folderTree.getModel()).setRoot(treeRoot);
		if (fileTree != null) 
			((DefaultTreeModel)fileTree.getModel()).setRoot(treeRoot);
		if (workingFileTree != null)
			((DefaultTreeModel)workingFileTree.getModel()).setRoot(treeRoot);
		fireProjectLoaded(p);
	} //}}}
	
	//{{{ getRoot() method
	/**	Returns the root node of the current tree. */
	public VPTNode getRoot() {
		return treeRoot;
	} //}}}
	
	//{{{ handleMessage(EBMessage) method
	/** Handles an EditBus message. */
	public void handleMessage(EBMessage msg) {
	
		// View closed? Remove from edit bus and from viewers list
		// EditPane changed? Fire a projectLoaded event for the global
		// listeners.
		if (msg instanceof ViewUpdate) {
			ViewUpdate vu = (ViewUpdate) msg;
			if (vu.getView() == view) {
				if (vu.getWhat() == ViewUpdate.CLOSED) {
					viewers.remove(view);
					config.removePropertyChangeListener(ccl);
					EditBus.removeFromBus(this);
					
					if (workingFileTree != null) {
						EditBus.removeFromBus((VPTWorkingFileListModel)workingFileTree.getModel());
					}
					
				} else if (vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED) {
					VPTProject current = null;
					if (treeRoot.isProject()) {
						current = (VPTProject) treeRoot;
					}
					ProjectViewerEvent evt = new ProjectViewerEvent(this, current);
					ArrayList lst = (ArrayList) listeners.get(null);
					if (lst != null)
					for (Iterator i = lst.iterator(); i.hasNext(); ) {
						((ProjectViewerListener)i.next()).projectLoaded(evt);
					}
				}
			}
		}
	
		// Try to import newly created files to the project
		if (treeRoot.isProject() && msg instanceof BufferUpdate) {
			BufferUpdate bu = (BufferUpdate) msg;
			VPTProject p = (VPTProject) treeRoot;
			if(bu.getView() == view && 
					bu.getWhat() == BufferUpdate.SAVED &&
					p.getFile(bu.getBuffer().getPath()) == null
					) {
				int res = JOptionPane.showConfirmDialog(view,
						jEdit.getProperty("projectviewer.import_new",
							new Object[] { bu.getBuffer().getName(), p.getName() }),
						jEdit.getProperty("projectviewer.import_new.title"),
						JOptionPane.YES_NO_OPTION);
	
				if(res == JOptionPane.YES_OPTION) {
					new SingleFileImporter(p, bu.getBuffer().getPath()).doImport();
				}
			}		
		}
	
	} //}}}
	
	//}}}
		
	//{{{ VPTListCellRenderer class
	/** ListCellRenderer that understands VPTNodes. */
	private static class VPTListCellRenderer extends DefaultListCellRenderer {
		
		public Component getListCellRendererComponent(JList list, Object value,  
			int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof VPTNode) {
				setText(((VPTNode)value).getName());
			}
			return this;
		}

	} //}}}
	
	//{{{ ProjectComboListener class
	/** Listens for item changes in the project combo box. */
	private class ProjectComboListener implements ItemListener, Runnable {
	
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() != ItemEvent.SELECTED || DISABLE_EVENTS) return;
			
			if(ie.getItem() instanceof VPTProject) {
				VPTProject p = (VPTProject) ie.getItem();
				ProjectManager.getInstance().getProject(p.getName());
				setProject(p);
			} else {
				if(ie.getItem().toString().equals(CREATE_NEW_PROJECT)) {
					SwingUtilities.invokeLater(this);
					pList.setSelectedItem(treeRoot);
				} else {
					setProject(null);
				}
			}
		}

		/**
		 *	"Comestic" hack to let the combo box close before showing the 
		 *	"new project" dialog.
		 */
		public void run() {
			EditProjectAction epa = new EditProjectAction();
			epa.prepareForNode(null);
			epa.actionPerformed(null);
		}

	} //}}}
		
	//{{{ ConfigChangeListener class
	/** Listens for changes in the PV configuration. */
	private class ConfigChangeListener implements PropertyChangeListener, Runnable {

		private boolean willRun = false;
		
		//{{{ propertyChange() method
		/** Listens for property change events in the plugin's configuration.
		 *  Shows/Hides the toolbar and the trees, according to the user's wish.
		 *
		 * @param  evt  Description of Parameter
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			// Toolbar show/hide.
			if(evt.getPropertyName().equals(ProjectViewerConfig.SHOW_TOOLBAR_OPT)) {
				if (toolBar != null) {
					topPane.remove(toolBar);
					toolBar.removeAll();
					toolBar = null;
				} else {
					toolBar = new JToolBar();
					populateToolBar();
					topPane.add(BorderLayout.NORTH, toolBar);
				}
				return;
			}

			if(evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FOLDERS_OPT) ||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_FILES_OPT) ||
					evt.getPropertyName().equals(ProjectViewerConfig.SHOW_WFILES_OPT)) {
				if(!willRun) {
					Log.log(Log.DEBUG, this, "Scheduling tree rebuild");
					SwingUtilities.invokeLater(this);
					willRun = true;
				}
				return;
			}
			
		}//}}}
		
		//{{{ run() method
		/** "Run" method, called by the Swing runtime after a config option for one
		 *  or more of the trees has changed.
		 */
		public void run() {
			showTrees();
			willRun = false;
		}//}}}		
	
	} //}}}
	
}

