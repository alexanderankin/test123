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

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.DefaultListCellRenderer;

import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;

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

import projectviewer.action.EditProjectAction;
//}}}

/**
 *  A Project Viewer plugin for jEdit.
 *
 *	@version    $Id$
 */
public final class ProjectViewer extends JPanel 
								 implements EBComponent {

	//{{{ Static members
	
	private static final HashMap viewers = new HashMap();
	private static final HashMap listeners = new HashMap();
	
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

	} //}}}
	
	//{{{ broadcastNodeStructureChanged(VPTNode) node
	/**
	 *	Notify all project viewer instances of a change in a node's structure.
	 */
	public static void nodeStructureChanged(VPTNode node) {
		for (Iterator it = viewers.values().iterator(); it.hasNext(); ) {
			ProjectViewer v = (ProjectViewer) it.next();
			((DefaultTreeModel)v.folderTree.getModel()).nodeStructureChanged(node);
			((DefaultTreeModel)v.fileTree.getModel()).nodeStructureChanged(node);
			((DefaultTreeModel)v.workingFileTree.getModel()).nodeStructureChanged(node);
		}
	} //}}}
	
	//}}}
			
	//{{{ Constants
	
	public final static String CREATE_NEW_PROJECT = "Create Project";
	
	//}}}
			
	//{{{ Attributes
	private View 					view;
	
	private JTree					folderTree;
	private JTree					fileTree;
	private JTree					workingFileTree;
	
	private JTabbedPane				treePane;
	private JComboBox				pList;
	
	private VPTNode 				treeRoot;
	private VPTContextMenu			vcm;
	private VPTSelectionListener	vsl;
	//}}}

	//{{{ Constructor
	/** Create a new <code>ProjectViewer</code>.
	 *
	 * @param  aView  Description of Parameter
`	 */
	public ProjectViewer(View aView) {
		view = aView;
		viewers.put(aView, this);
		vcm = new VPTContextMenu(this);
		vsl = new VPTSelectionListener(this);
		treeRoot = VPTRoot.getInstance();
		buildGUI();
		
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
		
		model.addTreeModelListener(vsl);
		tree.addTreeSelectionListener(vsl);
		return tree;
	} //}}}
	
	//{{{ buildGUI() method
	/** Builds the viewer GUI. */
	private void buildGUI() {
		setLayout(new BorderLayout());		
		
		treePane = new JTabbedPane();
		add(BorderLayout.CENTER, treePane);
		
		folderTree = createTree(new DefaultTreeModel(treeRoot, true));
		treePane.add(jEdit.getProperty("projectviewer.folderstab"), new JScrollPane(folderTree));
		
		fileTree = createTree(new VPTFileListModel(treeRoot));
		treePane.add(jEdit.getProperty("projectviewer.filestab"), new JScrollPane(fileTree));
		
		VPTWorkingFileListModel model = new VPTWorkingFileListModel(treeRoot);
		workingFileTree = createTree(model);
		EditBus.addToBus(model);
		treePane.add(jEdit.getProperty("projectviewer.workingfilestab"), new JScrollPane(workingFileTree));
		
		pList = new JComboBox();
		pList.setRenderer(new VPTListCellRenderer());
		
		pList.addItem(CREATE_NEW_PROJECT);
		pList.addItem(VPTRoot.getInstance());
		
		for (Iterator it = ProjectManager.getInstance().getProjects(); it.hasNext(); ) {
			pList.addItem(it.next());
		}
		
		pList.setSelectedItem(treeRoot);
		pList.addItemListener(new ProjectComboListener());
		add(BorderLayout.NORTH, pList);
		
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
		switch (treePane.getSelectedIndex()) {
			case 0:
				return folderTree;
				
			case 1:
				return fileTree;
				
			case 2:
				return workingFileTree;
				
			default:
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
		((DefaultTreeModel)folderTree.getModel()).setRoot(treeRoot);
		((DefaultTreeModel)fileTree.getModel()).setRoot(treeRoot);
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
			if (ie.getStateChange() != ItemEvent.SELECTED) return;
			
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
			EditProjectAction epa = new EditProjectAction(ProjectViewer.this);
			epa.actionPerformed(null);
		}

	} //}}}
		
}

