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
import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;
//}}}

/**
 *	Imports files and/or directories into a project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class FileImporter extends Importer {

	//{{{ Protected Members
	protected int fileCount;
	//}}}

	//{{{ +FileImporter(VPTNode, ProjectViewer) : <init>

	public FileImporter(VPTNode node, ProjectViewer viewer) {
		super(node, viewer);
	}

	//}}}

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

		CVSEntriesFilter cvsFilter = new CVSEntriesFilter();
		NonProjectFileFilter filter = new NonProjectFileFilter();
		ImportSettingsFilter settingsFilter = new ImportSettingsFilter();

		JFileChooser chooser = null;
		if (selected.isDirectory() && ((VPTDirectory)selected).getFile().exists()) {
			chooser = new JFileChooser(selected.getNodePath());
		} else {
			chooser = new JFileChooser(project.getRootPath());
		}

		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		chooser.addChoosableFileFilter(filter);
		chooser.addChoosableFileFilter(settingsFilter);
		chooser.addChoosableFileFilter(cvsFilter);
		chooser.setFileFilter(filter);

		if(chooser.showOpenDialog(this.viewer) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		if (chooser.getFileFilter() == cvsFilter) {
			selectedOp = 2;
		}

		File[] chosen = chooser.getSelectedFiles();
		if (chosen == null || chosen.length == 0) return null;

		ArrayList lst = new ArrayList();

		FilenameFilter fnf = null;
		boolean asked = false, recurse = false;
		for (int i = 0; i < chosen.length; i++) {
			VPTNode node = null;
			if (!chosen[i].exists()) {
				node = findDirectory(chosen[i], selected, true);
			} else if (chosen[i].isDirectory()) {
				node = findDirectory(chosen[i], selected, true);
				if (!asked) {
					Object[] options = {
						jEdit.getProperty("projectviewer.import.yes-settings"),
						jEdit.getProperty("projectviewer.import.yes-all"),
						jEdit.getProperty("projectviewer.import.yes-cvs"),
						jEdit.getProperty("projectviewer.import.no")
					};
					Object sel = JOptionPane.showInputDialog(this.viewer,
									jEdit.getProperty("projectviewer.import.msg_recurse"),
									jEdit.getProperty("projectviewer.import.msg_recurse.title"),
									JOptionPane.QUESTION_MESSAGE,
									null, options, options[selectedOp]);

					if (sel == null) {
						// cancel
						return null;
					}

					if (sel == options[1]) {
						recurse = true;
					} else if (sel == options[0]) {
						fnf = settingsFilter;
						recurse = true;
					} else if (sel == options[2]) {
						fnf = cvsFilter;
						recurse = true;
					} else {
						recurse = false;
					}
					asked = true;
				}
				if (recurse) {
					addTree(chosen[i], node, fnf);
				}
			} else if (findDirectory(chosen[i], selected, false) == null) {
				node = new VPTFile(chosen[i]);
				registerFile((VPTFile) node);
				fileCount++;
			}
			if (node != null && node.getParent() == null) {
				lst.add(node);
			}
		}

		showFileCount();
		return lst;
	} //}}}

	//{{{ #addTree(File, VPTNode, FilenameFilter) : void
	/**
	 *	Adds a directory tree to the given node.
	 *
	 *	@param	root	The root directory from where to look for files.
	 *	@param	where	The node to where the new files will be added.
	 *	@param	filter	The filter to use to select files.
	 */
	protected void addTree(File root, VPTNode where, FilenameFilter filter) {
		File[] children;

		if(filter != null){
			children = root.listFiles(filter);
		} else {
			children = root.listFiles();
		}

		if (children == null) return;

		for (int i = 0; i < children.length; i++) {
			if (!children[i].exists()) {
				continue;
			}

			VPTNode child;
			if (children[i].isDirectory()) {
				child = findDirectory(children[i], where, true);
				addTree(children[i], child, filter);
			} else {
				child = new VPTFile(children[i]);
				if (where.getIndex(child) != -1) {
					continue;
				}
				registerFile((VPTFile) child);
				fileCount++;
			}

			if ((!child.isDirectory() || child.getChildCount() != 0)
					&& child.getParent() == null) {
				where.add(child);
			}
		}

		where.sortChildren();
	} //}}}

	//{{{ #showFileCount() : void
	/** Shows a message in the status bar indicating how many files were imported. */
	protected void showFileCount() {
		String msg = jEdit.getProperty("projectviewer.import.msg_result",
						new Object[] { new Integer(fileCount) });
		if (viewer != null)
			viewer.setStatus(msg);
		else
			jEdit.getActiveView().getStatus().setMessageAndClear(msg);
	} //}}}

	//{{{ #class NonProjectFileFilter
	/**	A FileFilter that filters out files already added to the project. */
	protected class NonProjectFileFilter extends FileFilter {

		//{{{ +getDescription() : String
		public String getDescription() {
			return jEdit.getProperty("projectviewer.non-project-filter");
		} //}}}

		//{{{ +accept(File) : boolean
		public boolean accept(File f) {
			return (project.getChildNode(f.getAbsolutePath()) == null ||
					f.getAbsolutePath().endsWith("~") ||
					f.getAbsolutePath().endsWith(".bak"));
		} //}}}

	} //}}}

}

