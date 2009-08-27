package gatchan.jedit.lucene;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;

import projectviewer.action.Action;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

public class IndexProjectAction extends Action
{
	static public final String MESSAGE = "lucene.message.";
	static public final String INDEX_ERROR = MESSAGE + "CreateProjectIndexError";
	static public final String INDEX_ERROR_TITLE = MESSAGE + "CreateProjectIndexError.title";

	@Override
	public String getText()
	{
		return "Create/update Lucene index for project";
	}

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
			Collection<VPTNode> nodes = project.getOpenableNodes();
			Vector<VFSFile> files = new Vector<VFSFile>();
			for (VPTNode n: nodes)
			{
				if (n.isFile())
				{
					VPTFile vptFile = (VPTFile) n;
					VFSFile file = vptFile.getFile();
					if (file != null)
						files.add(file);
				}
			}
			VFSFile [] fileArray = new VFSFile[files.size()];
			files.toArray(fileArray);
			String indexName = project.getName();
			Index index = LucenePlugin.instance.getIndexForProject(indexName);
			if (index == null)
			{
				/*JOptionPane.showMessageDialog(null, jEdit.getProperty(INDEX_ERROR),
					jEdit.getProperty(INDEX_ERROR_TITLE), JOptionPane.ERROR_MESSAGE,
					null);*/
				return;
			}
			LucenePlugin.instance.addToIndex(indexName, fileArray, true);
		}
	}

}
