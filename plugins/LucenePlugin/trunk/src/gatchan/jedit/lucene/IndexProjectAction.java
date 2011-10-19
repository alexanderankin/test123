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
							 true);
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
		}
	}
}
