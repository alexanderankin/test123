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
package projectviewer.importer;

//{{{ Imports
import java.awt.Dialog;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSFileFilter;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;

import projectviewer.ProjectViewer;
import projectviewer.PVActions;
import projectviewer.VFSHelper;
import projectviewer.gui.ImportDialog;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTDirectory;
//}}}

/**
 *	Imports files and/or directories into a project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class FileImporter extends Importer {

	protected VFSFileFilter fnf;


	/**
	 * Constructs a new file importer.
	 *
	 * @param	node	The selected node in the viewer tree.
	 * @param	viewer	The viewer instance.
	 */
	public FileImporter(VPTNode node,
						ProjectViewer viewer)
	{
		super(node, viewer);
		fnf = null;
	}


	/**
	 *	Queries the user for files to be added by showing a file chooser
	 *	dialog. If any directories are chosen, the user is asked (once)
	 *	if he wants to import them recursively.
	 *
	 *	If the files to	be added are below the project's root path, and they're
	 *	being added directly to the project or to a node that is a directory and
	 *	whose path is parent to the files being added, the importer creates the
	 *	tree to the files and appends that tree to the node.
	 */
	protected void internalDoImport() {
		VPTNode where = selected;
		ImportDialog id = getImportDialog();
		if (loadImportFilterStatus(project, id, FILTER_CONF_FILES) == null) {
			loadImportFilterStatus(project, id, FILTER_CONF_PROJECT);
		}
		id.setVisible(true);

		VFSFile[] chosen = id.getSelectedFiles();
		if (chosen == null || chosen.length == 0) {
			return;
		}

		boolean keepTree = id.getKeepTree();

		try {
			/*
			 * If the user requests, create a new named node into which
			 * the imported files will be added.
			 */
			if (!keepTree && id.getNewNodeName() != null) {
				VFS vfs = VFSManager.getVFSForPath(selected.getNodePath());
				String newDir = vfs.constructPath(selected.getNodePath(),
												  id.getNewNodeName());
				where = findChild(newDir, selected, false);
				if (where == null || where.isFile()) {
					where = new VPTDirectory(newDir);
					addNode(where, selected);
				}
			}

			fnf = id.getImportFilter();
			for (VFSFile file : chosen) {
				String parentPath = file.getVFS()
										.getParentOfPath(file.getPath());
				VPTNode parent = where;
				VPTNode node = null;

				if (keepTree &&
					file.getPath().startsWith(project.getRootPath())) {
					parent = constructPath(project, parentPath);
					if (parent == null) {
						throw new FileNotFoundException(parentPath);
					}
				}

				node = findChild(file.getPath(), parent, true);
				if (VFSHelper.pathExists(file.getPath())) {
					if (node.isDirectory() && id.getTraverseDirectories()) {
						importFiles(node, fnf, true, id.getFlattenFilePaths());
					}
				}
			}
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, "VFS error while importing", ioe);
		}

		saveImportFilterStatus(project, id, FILTER_CONF_FILES);
		postAction = new ShowNodes();
	}


	protected ImportDialog getImportDialog()
	{
		Dialog dParent = (Dialog) SwingUtilities.getAncestorOfClass(Dialog.class, viewer);
		if (dParent != null) {
			return new ImportDialog(dParent, project, selected);
		} else {
			return new ImportDialog(JOptionPane.getFrameForComponent(viewer),
									project, selected);
		}
	}


	protected void cleanup()
	{
		if (fnf instanceof ImporterFileFilter) {
			((ImporterFileFilter)fnf).done();
		}
	}

}
