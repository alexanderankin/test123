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
import projectviewer.ProjectViewer;
//}}}

/**
 *	The root of the tree. Aside from being the root of the project tree, it
 *	provides methods for manipulating the projects and persisting data
 *	to the disk.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTRoot extends VPTNode {

	//{{{ Constants

	private final static Icon dirClosedIcon 	= GUIUtilities.loadIcon("Folder.png");
	private final static Icon dirOpenedIcon 	= GUIUtilities.loadIcon("OpenFolder.png");

	//}}}
	
	//{{{ Static Members
	private static final VPTRoot instance = new VPTRoot();
	
	//{{{ getInstance() method
	/**	Returns an instance of the VPTRoot. */
	public static VPTRoot getInstance() {
		return instance;
	} //}}}

	//}}}
	
	//{{{ Constructors 
	
	private VPTRoot() {
		super(VPTNode.ROOT, ProjectViewer.ALL_PROJECTS);
	}
	
	//}}}

	//{{{ getIcon(boolean) method
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		return (expanded ? dirOpenedIcon : dirClosedIcon);
	} //}}}
		
}
