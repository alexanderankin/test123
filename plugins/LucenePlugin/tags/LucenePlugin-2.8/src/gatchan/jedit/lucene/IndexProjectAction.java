/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
 * Copyright (C) 2009, 2011 Shlomy Reinstein
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
package gatchan.jedit.lucene;

import gatchan.jedit.lucene.Index.FileProvider;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gjt.sp.jedit.io.VFSFile;

import org.gjt.sp.util.Task;
import org.gjt.sp.util.ThreadUtilities;
import projectviewer.action.Action;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

public class IndexProjectAction extends Action
{
	@Override
	public String getText()
	{
		return "Create/update Lucene index for project";
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (viewer != null)
		{
			VPTNode node = viewer.getSelectedNode();
			if (node == null)
				node = viewer.getRoot();
			VPTProject project = VPTNode.findProjectFor(node);
			if (project == null)
				return;
			String indexName = project.getName();
			Index index = LucenePlugin.instance.getIndexForProject(indexName);
			if (index == null)
			{
				/*JOptionPane.showMessageDialog(null, jEdit.getProperty(INDEX_ERROR),
					jEdit.getProperty(INDEX_ERROR_TITLE), JOptionPane.ERROR_MESSAGE,
					null);*/
				return;
			}
			ProjectIndexer indexer = new ProjectIndexer(project, index);
			ThreadUtilities.runInBackground(indexer);
		}
	}

	private class ProjectIndexer extends Task
	{
		private final VPTProject project;
		private final Index index;

		ProjectIndexer(VPTProject project, Index index)
		{
			this.project = project;
			this.index = index;
		}

		@Override
		public void _run()
		{
			LucenePlugin.instance.addToIndex(index.getName(),
							 new ProjectFileList(project),
							 true, this);
		}

		private class ProjectFileList implements FileProvider
		{
			private final VPTProject project;
			private List<VFSFile> files;
			private int index;

			ProjectFileList(VPTProject project)
			{
				this.project = project;
			}

			@Override
			public VFSFile next()
			{
				if (files == null)
					constructFileList();
				if (index >= files.size())
					return null;
				return files.get(index++);
			}

			private void constructFileList()
			{
				Collection<VPTNode> nodes = project.getOpenableNodes();
				files = new ArrayList<VFSFile>(nodes.size());
				for (VPTNode n : nodes)
				{
					if (n.isFile())
					{
						VPTFile vptFile = (VPTFile) n;
						VFSFile file = vptFile.getFile();
						if (file != null)
							files.add(file);
					}
				}
			}

			@Override
			public int size()
			{
				if (files == null)
					constructFileList();
				return files.size();
			}
		}
	}
}
