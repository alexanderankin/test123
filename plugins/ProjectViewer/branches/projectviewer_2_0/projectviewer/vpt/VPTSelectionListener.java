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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;

import projectviewer.ProjectViewer;
import projectviewer.ProjectManager; 
//}}}

/** 
 *	Listens to the project JTree and responds to file selections.
 *
 *	@author     <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 *	@version	$Id$
 */
public final class VPTSelectionListener implements TreeSelectionListener, MouseListener {

	//{{{ Instance Variables 
	private ProjectViewer viewer;

	private int lastClickButton;
	private long lastClickTime;
	private Object lastClickTarget;

	private boolean reportsClickCountCorrectly;
	//}}}

	//{{{ Constructor
	/** 
	 *	Create a new <code>ProjectTreeSelectionListener
	 *
	 *	@param  aViewer    Description of Parameter
	 *	@param  aLauncher  Description of Parameter
	 */
	public VPTSelectionListener(ProjectViewer aViewer) {
		viewer = aViewer;
		lastClickTime = 0L;
		reportsClickCountCorrectly = false;
	} //}}}

	//{{{ MouseListener interfaces

	/** 
	 *	Determines when the user clicks on the JTree.
	 *
	 * @param  evt  Description of Parameter
	 */
	public void mouseClicked(MouseEvent evt) {
		if(isDoubleClick(evt) && isNodeClicked(evt)) {
			VPTNode node = viewer.getSelectedNode();

			if(node.canOpen()) {
				if(node.isOpened()) {
					if (node.getNodePath().equals(viewer.getView().getBuffer().getPath())) {
						node.close();
					} else {
						// try to set the selected node's buffer as the active 
						// buffer. Since "open" does not necessarily mean "open
						// in jEdit", this falls back to "node.close()" if no
						// buffer is found.
						Buffer b = jEdit.getBuffer(node.getNodePath());
						if (b != null) {
							viewer.getView().setBuffer(b);
						} else {
							node.close();
						}
					}
				} else {
					node.open();
				}
			}
		}
		
	}

	public void mousePressed(MouseEvent evt) { 
		if (viewer.getRoot().isRoot()) {
			JTree tree = (JTree) evt.getSource();
			TreePath path = tree.getClosestPathForLocation(evt.getX(), evt.getY());
			VPTNode node = (VPTNode) path.getLastPathComponent();
			
			if (node != null && node.isProject()) {
				if (!ProjectManager.getInstance().isLoaded(node.getName())) {
					viewer.setStatus("Loading project \"" + node.getName() + "\"");
					ProjectManager.getInstance().getProject(node.getName());
					ProjectViewer.nodeStructureChanged(node);
				}
			}
		}
	}

	public void mouseReleased(MouseEvent evt) { }

	public void mouseEntered(MouseEvent evt) { }

	public void mouseExited(MouseEvent evt) { }

	//}}}
	
	//{{{ TreeSelectionListener interfaces

	/** 
	 *	Receive notification that the tree selection has changed.
	 *
	 *	@param  e  Description of Parameter
	 */
	public void valueChanged(TreeSelectionEvent e) {
		lastClickTarget = null;
		
		VPTNode node = viewer.getSelectedNode();
		if(node == null) return;

		//viewer.enableButtonsForNode(node);
		viewer.setStatus(node.toString());
	}

	//}}}
	
	//{{{ isDoubleClick(MouseEvent) method
	/** 
	 *	Because IBM's JDK doesn't support <code>getClickCount()</code> for <code>JTree</code>
	 *	properly, we have to do this.
	 *
	 *	@param  evt  Description of Parameter
	 *	@return      The doubleClick value
	 */
	private boolean isDoubleClick(MouseEvent evt) {
		if(reportsClickCountCorrectly)
			return evt.getClickCount() == 2;
		if(evt.getClickCount() == 2) {
			reportsClickCountCorrectly = true;
			return true;
		}

		TreePath path = viewer.getCurrentTree().getPathForLocation(evt.getX(), evt.getY());
		if(path == null) {
			lastClickTarget = null;
			return false;
		}

		Object target = path.getLastPathComponent();

		if(target == lastClickTarget &&
				target == viewer.getSelectedNode() &&
				lastClickButton == evt.getModifiers() &&
				(System.currentTimeMillis() - lastClickTime < 500L)) {
			lastClickTarget = null;
			return true;
		}

		lastClickButton = evt.getModifiers();
		lastClickTarget = target;
		lastClickTime = System.currentTimeMillis();
		return false;
	} //}}}

	//{{{ isNodeClicked(MouseEvent) method
	/** 
	 *	Returns <code>true</code> if a node is selected and the given
	 *	mouse event points to the specified node.
	 *
	 *	@param  evt  Description of Parameter
	 *	@return      The fileClicked value
	 */
	private boolean isNodeClicked(MouseEvent evt) {
		VPTNode selectedNode = viewer.getSelectedNode();

		TreePath path = viewer.getCurrentTree().getPathForLocation(evt.getX(), evt.getY());
		if(path == null)
			return false;
		
		Object clickedNode = path.getLastPathComponent();

		return (selectedNode == clickedNode);
	} //}}}

}

