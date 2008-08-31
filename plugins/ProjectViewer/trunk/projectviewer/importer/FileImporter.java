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
		loadImportFilterStatus(project, id);
		id.setVisible(true);

		VFSFile[] chosen = id.getSelectedFiles();
		if (chosen == null || chosen.length == 0) {
			return;
		}

		boolean keepTree = id.getKeepTree();

		/*
		 * If the user requests, create a new named node into which
		 * the imported files will be added.
		 */
		if (!keepTree && id.getNewNodeName() != null) {
			VFS vfs = VFSManager.getVFSForPath(selected.getNodePath());
			String newDir = vfs.constructPath(selected.getNodePath(),
											  id.getNewNodeName());
			where = findDirectory(newDir, selected, true);
			if (where.isFile()) {
				where = new VPTDirectory(newDir);
			}
			addNode(where, selected);
		}

		fnf = id.getImportFilter();
		try {
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

				if (!VFSHelper.pathExists(file.getPath())) {
					node = findDirectory(file.getPath(), parent, true);
				} else if (file.getType() == VFSFile.DIRECTORY) {
					node = findDirectory(file.getPath(), parent, true);
					if (id.getTraverseDirectories()) {
						addTree(node, fnf, id.getFlattenFilePaths());
					}
				} else if (findDirectory(file.getPath(), parent, false) == null) {
					node = new VPTFile(file.getPath());
					addNode(node, parent);
				}
			}
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, "VFS error while importing", ioe);
		}

		saveImportFilterStatus(project, id);
		postAction = new ShowNodes();
	}


	/**
	 * Adds a directory tree to the given node.
	 *
	 * @param	where	The node to where the new files will be added.
	 * @param	filter	The filter to use to select files.
	 * @param	flatten	Whether to "flat import" (add all files to top directory).
	 */
	protected void addTree(VPTNode where,
						   VFSFileFilter filter,
						   boolean flatten)
		throws IOException
	{
		Object session;
		String[] children;
		VFSFile root;
		View view;

		view = jEdit.getActiveView();
		root = VFSHelper.getFile(where.getNodePath());
		if (root == null) {
			return;
		}

		session = VFSHelper.createVFSSession(root.getVFS(), root.getPath(), view);

		try {
			children = root.getVFS()._listDirectory(session, root.getPath(),
													filter, true, view, false,
													true);
		} finally {
			VFSHelper.endVFSSession(root.getVFS(), session, view);
		}

		if (children == null || children.length == 0) {
			return;
		}

		for (String url: children) {
			VFSFile file = VFSHelper.getFile(url);
			VPTNode node;
			if (file == null) {
				continue;
			}
			node = constructPath(where, url);
		}
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
