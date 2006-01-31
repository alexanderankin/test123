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

import javax.swing.Icon;

import org.gjt.sp.jedit.GUIUtilities;
//}}}

/**
 *	A VPTGroup is a container for groups and projects.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class VPTGroup extends VPTNode {

	//{{{ Constants
	private final static Icon dirClosedIcon = GUIUtilities.loadIcon("Folder.png");
	private final static Icon dirOpenedIcon = GUIUtilities.loadIcon("OpenFolder.png");
	//}}}

	//{{{ +VPTGroup(String) : <init>
	public VPTGroup(String name) {
		super(name);
	} //}}}

	//{{{ +getIcon(boolean) : Icon
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		return (expanded ? dirOpenedIcon : dirClosedIcon);
	} //}}}

	//{{{ +getNodePath() : String
	/**	Returns the path to this group in the group tree. */
	public String getNodePath() {
		if (getParent() != null) {
			return ((VPTNode)getParent()).getNodePath() + getName();
		}
		return getName() + File.separator;
	} //}}}

	//{{{ +compareToNode(VPTNode) : int
	public int compareToNode(VPTNode n) {
		if (!n.isGroup()) {
			return -1;
		} else if (n.isRoot()) {
			return 1;
		} else {
			return getName().compareTo(n.getName());
		}
	} //}}}

	//{{{ +toString() : String
	public String toString() {
		return "VPTGroup [" + getName() + "]";
	} //}}}

}

