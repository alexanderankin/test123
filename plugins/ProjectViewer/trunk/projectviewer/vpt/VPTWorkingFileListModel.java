/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.vpt;

//{{{ Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.tree.TreeNode;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;

import projectviewer.config.ProjectViewerConfig;

//}}}

/**
 *	A tree model that shows all files currently opened in jEdit in a flat list.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTWorkingFileListModel extends ProjectCustomTreeModel
{

	/**
	 *	Create a new <code>VPTFileListModel</code>.
	 *
	 *	@param rootNode	The root node of the tree.
	 */
	public VPTWorkingFileListModel(VPTNode rootNode)
	{
		super(rootNode);
	}


	/**
	 * Adds an open file to the list of open files of the projects to
	 * which it belongs.
	 */
	public void fileOpened(VPTNode child)
	{
		boolean showAllWorkingFiles =
			ProjectViewerConfig.getInstance().getShowAllWorkingFiles();
		String path = child.getNodePath();

		for (VPTProject p : getCache().keySet()) {
			if (showAllWorkingFiles || p.getChildNode(path) != null) {
				addChild(p, child);
			}
		}
	}


	/**
	 *	Removes an open file from the list of open files of the projects to
	 *	which it belongs.
	 */
	public void fileClosed(VPTNode child)
	{
		String path = child.getNodePath();

		for (VPTProject p : getCache().keySet()) {
			removeChild(p, child);
		}
	}


	/**
	 * Returns the list of files currently opened in jEdit that belong
	 * to the given project.
	 */
	protected List<VPTNode> getChildren(VPTProject proj)
	{
		boolean showAllWorkingFiles =
			ProjectViewerConfig.getInstance().getShowAllWorkingFiles();
		Buffer[] bufs = jEdit.getBuffers();
		List<VPTNode> lst = new ArrayList<VPTNode>();

		for (Buffer b : bufs) {
			String path = b.getPath();
			VPTNode n = proj.getChildNode(path);
			if (n != null) {
				lst.add(n);
			} else if (showAllWorkingFiles && !b.isUntitled()) {
				lst.add(new VPTFile(path));
			}
		}
		return lst;
	}


	protected String getName()
	{
        return "projectviewer.workingfilestab";
	}

}

