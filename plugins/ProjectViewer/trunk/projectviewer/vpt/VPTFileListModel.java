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

import java.util.ArrayList;
import java.util.List;

/**
 *	A tree model that represents all files in a project without any relationship
 *	to the nodes they are a child of.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTFileListModel extends ProjectCustomTreeModel
{

	/**
	 *	Create a new <code>VPTFileListModel</code>.
	 *
	 *	@param rootNode	The root node of the tree.
	 */
	public VPTFileListModel(VPTNode rootNode)
	{
		super(rootNode);
	}


	/** Returns a vector with all the files of the project. */
	protected List<VPTNode> getChildren(VPTProject p)
	{
		return new ArrayList<VPTNode>(p.openableNodes.values());
	}


	protected String getName()
	{
        return "projectviewer.filestab";
	}

}

