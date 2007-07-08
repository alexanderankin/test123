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
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JComponent;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
//}}}

/**
 *	An action defines an action to be taken when the user presses some menu
 *	item in the tree's context menu.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public abstract class Action  implements ActionListener, Cloneable {

	//{{{ Instance variables

	protected ProjectViewer		viewer;
	protected JComponent		cmItem;
	protected String			action;

	//}}}

	//{{{ #Action() : <init>
	/** Creates a regular action. */
	protected Action() {
		this(null);
	} //}}}

	//{{{ #Action(String) : <init>
	/**
	 * 	Creates an action tied to an action name. If the action name is not
	 *	null, the default menu item created will be a jEdit EnhancedMenuItem
	 *	tied to the action name; this will allow the item to show the
	 *	currently assigned shortcut of the action, if any. The action
	 *	listeners are removed from the menu item so that only the
	 *	actionPerformed() method on this instance is called, avoiding
	 *	using the jEdit action mechanism for this instance.
	 */
	protected Action(String action) {
		this.action = action;
	} //}}}

	//{{{ +*getText()* : String
	/**
	 *	Returns a String that will be shown as the text of the menu item or
	 *	the tooltip of the toolbar button.
	 */
	public abstract String getText(); //}}}

	//{{{ +prepareForNode(VPTNode) : void
	/**
	 *	When a node is selected (for the toolbar button) or when the context
	 *	menu is shown (for the menu item), this method is called and the
	 *	selected node is passed, so the action can choose whether the action
	 *	will be available or not for that node. A common action would be
	 *	disable the button or hide the menu item.
	 *
	 *	<p>By default, does nothing.</p>
	 *
	 *	@param	node	The selected node, or "null" if multiple nodes are
	 *					selected.
	 */
	public void prepareForNode(VPTNode node) {

	}//}}}

	//{{{ +getMenuItem() : JComponent
	/**
	 *	Returns the menu item that triggers this action. This returns a
	 *	JComponent, which makes it possible to add virtually anything to
	 *	the menu. For example, it's possible to return a sub-menu instead
	 *	of a simple menu item. The default implementation returns a menu
	 *	item, which is stored in the "cmItem" variable.
	 */
	public JComponent getMenuItem() {
		if (cmItem == null) {
			if (action == null) {
				cmItem = new JMenuItem(getText());
			} else {
				cmItem = GUIUtilities.loadMenuItem(action);
				ActionListener[] l = ((AbstractButton)cmItem).getActionListeners();
				for (int i = 0; i < l.length; i++) {
					((AbstractButton)cmItem).removeActionListener(l[i]);
				}
				((AbstractButton)cmItem).setText(getText());
			}
			((JMenuItem)cmItem).addActionListener(this);
		}
		return cmItem;
	} //}}}

	//{{{ +clone() : Object
	/** Clones the current action, returning a copy of it. */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cnse) {
			// should not happen
			Log.log(Log.ERROR,this,cnse);
			return null;
		}
	} //}}}

	//{{{ +setViewer(ProjectViewer) : void
	/** Sets the viewer where this action is being used. */
	public void setViewer(ProjectViewer viewer) {
		this.viewer = viewer;
	} //}}}

}

