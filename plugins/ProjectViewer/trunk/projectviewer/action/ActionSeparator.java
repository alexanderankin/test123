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
	private Action linkedAction;
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
	
	//{{{ getText() method
	/** Returns null. There's no text in this separator. */
	public String getText() {
		return null;
	} //}}}

	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Shows the separatos if the linked action is visible or null. */
	public void prepareForNode(VPTNode node) {
		cmItem.setVisible(node != null && (linkedAction == null || linkedAction.getMenuItem().isVisible()));
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

