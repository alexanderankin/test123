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

import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectViewer;
import projectviewer.PVActions;
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

	//{{{ Constants
	protected static final int FILTER_MSG_RECURSE			= 1;
	protected static final int FILTER_MSG_INITIAL_IMPORT	= 2;
	protected static final int FILTER_MSG_RE_IMPORT			= 3;
	//}}}

	//{{{ Protected Members
	protected int fileCount;
	protected FilenameFilter fnf;
	//}}}

	//{{{ +FileImporter(VPTNode, ProjectViewer) : <init>
	public FileImporter(VPTNode node, ProjectViewer viewer) {
		super(node, viewer);
		fnf = null;
	} //}}}

	//{{{ #internalDoImport() : Collection
	/**
	 *	Queries the user for files to be added by showing a file chooser
	 *	dialog. If any directories are chosen, the user is asked (once)
	 *	if he wants to import them recursively.
	 *
	 *	If the files to	be added are below the project's root path, and they're
	 *	being added directly to the project or to a node that is a directory and
	 *	whose path is parent to the files being added, the importer creates the
	 *	tree to the files and appends that tree to the node.
	 *
	 *	@return	A collection of VPTNode instances.
	 */
	protected Collection internalDoImport() {
		fileCount = 0;
		int selectedOp = 0;

		ImportDialog id = getImportDialog();
		loadImportFilterStatus(project, id);
		id.setVisible(true);

		ArrayList lst = new ArrayList();
		File[] chosen = id.getSelectedFiles();
		if (chosen == null || chosen.length == 0) return null;

		VPTNode where = null;
		VPTNode root = selected;
		if (id.getNewNodeName() != null) {
			File newDir = new File(selected.getNodePath(), id.getNewNodeName());
			where = findDirectory(newDir, selected, true);
			if (where.isFile())
				where = new VPTDirectory(newDir);
			lst.add(where);
			root = where;
		}


		FilenameFilter fnf = id.getImportFilter();
		for (int i = 0; i < chosen.length; i++) {
			VPTNode node = null;
			if (!chosen[i].exists()) {
				node = findDirectory(chosen[i], root, true);
			} else if (chosen[i].isDirectory()) {
				node = findDirectory(chosen[i], root, true);
				if (id.getTraverseDirectories()) {
					addTree(chosen[i], node, fnf, id.getFlattenFilePaths());
				}
			} else if (findDirectory(chosen[i], root, false) == null) {
				node = new VPTFile(chosen[i]);
				registerFile((VPTFile) node);
				fileCount++;
			}

			if (node != null && node.getParent() == null) {
				if (where == null) {
					lst.add(node);
				} else {
					where.add(node);
					where.sortChildren();
				}
			}
		}

		showFileCount();
		saveImportFilterStatus(project, id);
		return lst;
	} //}}}

	//{{{ #addTree(File, VPTNode, FilenameFilter) : void
	/**
	 *	Adds a directory tree to the given node.
	 *
	 *	@param	root	The root directory from where to look for files.
	 *	@param	where	The node to where the new files will be added.
	 *	@param	filter	The filter to use to select files.
	 *	@param	flatten	Whether to "flat import" (add all files to top directory).
	 */
	protected void addTree(File root, VPTNode where,
						   FilenameFilter filter, boolean flatten)
	{
		File[] children;

		if (filter != null){
			children = root.listFiles(filter);
		} else {
			children = root.listFiles();
		}

		if (children == null || children.length == 0) return;

		for (int i = 0; i < children.length; i++) {
			if (!children[i].exists()) {
				continue;
			}

			VPTNode child;
			if (children[i].isDirectory()) {
				child = (flatten) ? where : findDirectory(children[i], where, true);
				addTree(children[i], child, filter, flatten);
			} else {
				child = new VPTFile(children[i]);
				if (where.getIndex(child) != -1) {
					continue;
				}
				registerFile((VPTFile) child);
				fileCount++;
			}

			if ((!child.isDirectory() || child.getChildCount() != 0)
				&& child.getParent() == null
				&& child != where
			) {
				where.add(child);
			}
		}

		where.sortChildren();
	} //}}}

	//{{{ #showFileCount() : void
	/** Shows a message in the status bar indicating how many files were imported. */
	protected void showFileCount() {
		final String msg = jEdit.getProperty("projectviewer.import.msg_result",
							new Object[] { new Integer(fileCount) });
		PVActions.swingInvoke(
			new Runnable() {
				public void run() {
					if (viewer != null)
						viewer.setStatus(msg);
					else
						jEdit.getActiveView().getStatus().setMessageAndClear(msg);
				}
			}
		);
	} //}}}

	//{{{ #getImportDialog() : ImportDialog
	protected ImportDialog getImportDialog() {
		Dialog dParent = (Dialog) SwingUtilities.getAncestorOfClass(Dialog.class, viewer);
		if (dParent != null) {
			return new ImportDialog(dParent, project, selected);
		} else {
			return new ImportDialog(JOptionPane.getFrameForComponent(viewer),
									project, selected);
		}
	} //}}}

}

