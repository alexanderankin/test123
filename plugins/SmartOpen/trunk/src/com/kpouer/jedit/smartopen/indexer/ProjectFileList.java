/*
 * jEdit - Programmer's Text Editor
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011-20163 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.smartopen.indexer;

//{{{ 
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.gjt.sp.util.Log;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
//}}}

/**
 * @author Matthieu Casanova
 */
public class ProjectFileList implements FileProvider
{
	private final VPTProject project;
	private List<String> files;
	private int index;

	//{{{ ProjectFileList constructor
	ProjectFileList(VPTProject project)
	{
		this.project = project;
	} //}}}

    @Override
    public Stream<String> stream()
    {
        if (files == null)
            constructFileList();
        return files.stream();
    }

    //{{{ constructFileList() method
	private void constructFileList()
	{
		long start = System.currentTimeMillis();
		Collection<VPTNode> nodes = project.getOpenableNodes();
		files = new ArrayList<>(nodes.size());
		for (VPTNode n : nodes)
		{
			if (n.isFile())
			{
				VPTFile vptFile = (VPTFile) n;
				String file = vptFile.getURL();
				if (file != null)
					files.add(file);
			}
		}
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this,
			"Listing files for project " + project + ' ' + files.size() + " in " + (end - start) + "ms");
	} //}}}
}
