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

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.DefaultListCellRenderer;

import javax.swing.tree.TreeModel;

import org.gjt.sp.jedit.View;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
import projectviewer.vpt.VPTModel;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTContextMenu;
import projectviewer.vpt.VPTCellRenderer;
import projectviewer.vpt.VPTSelectionListener;

import projectviewer.action.EditProjectAction;
//}}}

/**
 *  A Project Viewer plugin for jEdit.
 *
 *	@version    $Id$
 */
public final class ProjectViewer extends JPanel {
			
	//{{{ Static members
	
	private static final HashMap viewers = new HashMap();
	
	/**
	 *	Returns the viewer associated with the given view, or null if none
	 *	exists.
	 */
	public static ProjectViewer getViewer(View view) {
		return (ProjectViewer) viewers.get(view);
	}
	
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

	}
	
	//}}}
			
	//{{{ Constants
	
	public final static String CREATE_NEW_PROJECT = "Create Project";
	
	//}}}
			
	//{{{ Attributes
	private View 					view;
	
	private JTree					folderTree;
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
		folderTree = createTree(new VPTModel(treeRoot));
		add(BorderLayout.CENTER, new JScrollPane(folderTree));
		
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
		if (folderTree.getSelectionPath() != null) {
			return (VPTNode) folderTree.getSelectionPath().getLastPathComponent();
		} else {
			return null;
		}
	} //}}}
	
	//{{{ getCurrentTree() method
	/** Returns the currently active tree. */
	public JTree getCurrentTree() {
		return folderTree;
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
		((VPTModel)folderTree.getModel()).setRoot(treeRoot);
	} //}}}
	
	//{{{ getRoot() method
	/**	Returns the root node of the current tree. */
	public VPTNode getRoot() {
		return treeRoot;
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
			EditProjectAction epo = new EditProjectAction(ProjectViewer.this);
			epo.actionPerformed(null);
		}

	} //}}}
	
}

