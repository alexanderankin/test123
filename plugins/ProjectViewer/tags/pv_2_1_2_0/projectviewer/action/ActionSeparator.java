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
package projectviewer.action;

//{{{ Imports
import java.awt.event.ActionEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSeparator;

import projectviewer.vpt.VPTNode;
//}}}

/**
 *	Small hack to enable a separator to be shown when some other action is also
 *	shown.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ActionSeparator extends Action {

	//{{{ Private Members
	private Action 	linkedAction;
	private List	actions;
	//}}}

	//{{{ setLinkedAction(Action) method
	/**
	 *	Sets the linked action. The separator will be shown if and only if the
	 *	component returned by getMenuItem() is visible, i.e., the
	 *	"isVisible()" method returns true.
	 */
	public void setLinkedAction(Action linked) {
		this.linkedAction = linked;
	}
	//}}}

	/**
	 *	Set a list of actions that will define whether the separator is
	 *	shown; it will be show iff all the actions in the list are
	 *	visible.
	 */
	public void setLinkedActions(List actions) {
		this.actions = actions;
	}

	//{{{ getText() method
	/** Returns null. There's no text in this separator. */
	public String getText() {
		return null;
	} //}}}

	//{{{ actionPerformed(ActionEvent) method
	/** Does nothing. */
	public void actionPerformed(ActionEvent e) {

	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/**
	 *	Shows the separator if the linked action is visible or null and only
	 *	a single node is selected.
	 */
	public void prepareForNode(VPTNode node) {
		if (actions != null) {
			boolean visible = false;
			for (Iterator i = actions.iterator(); i.hasNext(); ) {
				if (((Action)i.next()).getMenuItem().isVisible()) {
					visible = true;
					break;
				}
			}
			cmItem.setVisible(visible);
		} else if (node != null
					&& (node.isProject()
						|| node.isFile()
						|| node.isDirectory()))
		{
			cmItem.setVisible(node != null &&
				(linkedAction == null || linkedAction.getMenuItem().isVisible()));
		} else {
			cmItem.setVisible(false);
		}
	} //}}}

	//{{{ getMenuItem() method
	/** Returns a separator. */
	public JComponent getMenuItem() {
		if (cmItem == null) {
			cmItem = new JSeparator();
		}
		return cmItem;
	} //}}}

}

