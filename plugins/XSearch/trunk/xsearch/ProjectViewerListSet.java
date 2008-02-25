/*
 * ProjectViewerListSet.java - Directory list matcher
 * :tabSize=4:indentSize=4:noTabs=false:
 *
 * Copyright (C) 2002 Rudolf Widmann
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

package xsearch;

//{{{ Imports
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.BufferListSet;
import org.gjt.sp.util.StringList;

import projectviewer.vpt.*;
import projectviewer.*;
//}}}

/**
 * Project Viewer search.
 * @author Rudolf Widmann
 */
public class ProjectViewerListSet extends BufferListSet
{
	//{{{ ProjectViewerListSet constructor
	public ProjectViewerListSet(final View view)
	{
		if (jEdit.getPlugin("projectviewer.ProjectPlugin",false) != null)
		{
			// we can use the projectviewer plugin. Check if a project is activ
			ProjectViewer pv =  projectviewer.ProjectViewer.getViewer(view);
			// (ProjectViewer)view.getDockableWindowManager().getDockable("projectviewer");
			if (pv == null)
				org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, BeanShell.class,"+++ openFileInProjectViewer.68: cannot get ProjectViewer");
			else
				vptNode=pv.getRoot();
		}
	} //}}}
	
	/**
	 * check is project viewer is present and a project is present
	 * ico pseudo-project, return false
	 */
	public boolean isProjectViewerPresent()
	{
		return vptNode != null && vptNode.isProject();
	}

	//{{{ getCode() method
	public String getCode()
	{
		return "new xsearch.ProjectViewerListSet()";
	} //}}}

	//{{{ _getFiles() method
	protected String[] _getFiles(final java.awt.Component comp)
	{
		if (vptNode != null && vptNode.isProject()) {
			// VPTNode vptNode = pv.getRoot();
			// org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, BeanShell.class,"+++ ProjectViewerListSet.66: vptNode = "+vptNode.getName());
			// VPTProject vptProject = vptNode.findProjectFor(vptNode);
			org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, BeanShell.class,"+++ ProjectViewerListSet.68: VPTproject = "+vptNode);
			Collection vptNodeColl = ((VPTProject)vptNode).getOpenableNodes();
			StringList al = new StringList();
			// org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, BeanShell.class,"+++ .9: fileColl = "+vptNodeColl);
			for (Iterator<VPTNode> it = vptNodeColl.iterator(); it.hasNext(); ) 
			{
				vptNode = (VPTNode)it.next();
				if (vptNode.isFile())
				{
					// org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, BeanShell.class,"+++ .15: VPTnode = "+VPTnode.getName());
					al.add(vptNode.getNodePath());
				}
			}
			return al.toArray();
		}
		return null;
	}
	//}}}

	//{{{ Private members
	private VPTNode vptNode;
	//}}}
}
