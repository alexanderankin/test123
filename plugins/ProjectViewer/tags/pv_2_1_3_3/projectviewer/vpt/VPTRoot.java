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

import org.gjt.sp.jedit.jEdit;
//}}}

/**
 *	The root of the tree. Aside from being the root of the project tree, it
 *	provides methods for manipulating the projects and persisting data
 *	to the disk.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTRoot extends VPTGroup {

	//{{{ Static Members
	private static VPTRoot instance;

	//{{{ +_getInstance()_ : VPTRoot
	/**	Returns an instance of the VPTRoot. */
	public synchronized static VPTRoot getInstance() {
		if (instance == null)
			instance = new VPTRoot();
		return instance;
	} //}}}

	//}}}

	//{{{ -VPTRoot() : <init>
	private VPTRoot() {
		super(jEdit.getProperty("projectviewer.all_projects"));
	} //}}}

	//{{{ +getNodePath() : String
	/**	Returns File.separator. */
	public String getNodePath() {
		return File.separator;
	} //}}}

	//{{{ +compareToNode(VPTNode) : int
	/** The root is always the first node. Period. */
	public int compareToNode(VPTNode node) {
		return -1;
	} //}}}

}

