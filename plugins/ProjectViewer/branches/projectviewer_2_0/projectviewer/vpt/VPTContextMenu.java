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
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JTree;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

// Import jEdit
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;

// Import ProjectViewer
import projectviewer.ProjectViewer;

import projectviewer.action.Action;
import projectviewer.action.FileImportAction;
import projectviewer.action.EditProjectAction;

import projectviewer.config.ProjectViewerConfig;
import projectviewer.config.AppLauncher;

//}}}

/**
 *	A handler for context menu requests on the tree, providing node-sensitive
 *	functionality.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTContextMenu extends MouseAdapter {

	//{{{ Instance Variables
	private final ProjectViewer viewer;
	private AppLauncher appList;
	private JPopupMenu popupMenu;
  
	private ArrayList	actions;
	//}}}
	
	//{{{ Constructors
	
	/**
	 *  Constructs a listener that will ask the provided viewer instance for
	 *  information about the nodes clicked.
	 */
	public VPTContextMenu(ProjectViewer viewer) {
		this.viewer = viewer;
		loadGUI();
		appList = AppLauncher.getInstance();
	}
	
	//}}}
	
	//{{{ Event Handling
	
	//{{{ mousePressed() method
	/** Context-menus are shown on the "pressed" event. */
	public void mousePressed(MouseEvent me) {
		JTree tree = (JTree) me.getSource();

		if (SwingUtilities.isRightMouseButton(me)) {
			TreePath tp = tree.getClosestPathForLocation(me.getX(),me.getY());
			if (tp != null && !tree.isPathSelected(tp)) {
				if ((me.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
					tree.addSelectionPath(tp);
				} else {
					tree.setSelectionPath(tp);
				}
			}
		}

		if (me.isPopupTrigger()) {
			handleMouseEvent(me);
		}
	} //}}}
	
	//{{{ mouseReleased() method
	/** Context-menus are shown on the "pressed" event. */
	public void mouseReleased(MouseEvent me) {
		if (me.isPopupTrigger()) {
			handleMouseEvent(me);
		}
	} //}}}
	
	//}}}
	
	//{{{ Private Methods
	
	//{{{ handleMouseEvent() method
	/** Handles the mouse event internally. */
	private void handleMouseEvent(MouseEvent me) {
		JTree tree = viewer.getCurrentTree();
		
		if (tree.getSelectionCount() == 0) {
			return;
		} else {
			prepareMenu( tree.getSelectionCount() > 1 ? null : viewer.getSelectedNode() ); 
			popupMenu.show(me.getComponent(), me.getX(), me.getY());
		}
	} //}}}
	
	//{{{ loadGUI() method
	/** Constructs the menus' GUI. */
	private void loadGUI() {
		actions = new ArrayList();
		popupMenu = new JPopupMenu();
		
		Action cpa = new EditProjectAction(viewer);
		popupMenu.add(cpa.getMenuItem());
		actions.add(cpa);

		cpa = new FileImportAction(viewer);
		popupMenu.add(cpa.getMenuItem());
		actions.add(cpa);

	} //}}}
	
	//{{{ prepareMenu(VPTNode) method
	/**
	 *	Prepares the context menu for the given node. Shows only menu items
	 *	that are allowed for the node (e.g., "Add Project" only applies for
	 *	the root node). If the node is null, the method guesses that multiple
	 *	nodes are selected, and chooses the appropriate entries.
	 */
	private void prepareMenu(VPTNode selectedNode) {
		for (Iterator it = actions.iterator(); it.hasNext(); ) {
			((Action)it.next()).prepareForNode(selectedNode);
		}
	} //}}}
	
	//}}}

}
