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
import projectviewer.vpt.VPTDirectory;
//}}}

/**
 *	Imports files and/or directories into a project, using the import style used
 *	by the old version of the plugin. This means that only files and directories
 *	below the project root are accepted, and that files are added with all its
 *	parents up to the project root. It uses the setting filter when importing
 *	directories recursively by default, or the CVS/Entries filter in case it
 *	was selected in the file chooser.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class OldStyleFileImporter extends FileImporter {

	//{{{ Private members
	private VPTNode toShow;
	//}}}

	//{{{ Constructor

	public OldStyleFileImporter(VPTNode node, ProjectViewer viewer) {
		super(node, viewer);
	}

	//}}}

	//{{{ internalDoImport() method
	/** Asks if the user wants to import files from the chosen project root. */
	protected Collection internalDoImport() {
		fileCount = 0;

		if (selected.isFile())
			selected = (VPTNode) selected.getParent();

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

		File[] chosen = chooser.getSelectedFiles();
		if (chosen == null || chosen.length == 0) return null;

		FileFilter fFilter = (FileFilter) chooser.getFileFilter();
		FilenameFilter fnf = settingsFilter;
		if (fFilter instanceof FilenameFilter)
			fnf = (FilenameFilter) fFilter;

		ArrayList lst = new ArrayList();

		for (int i = 0; i < chosen.length; i++) {

			if (i == 0) {
				// for the first node, do the following:
				// - check if files are under the root, so they can be imported
				// - find the node where the files are to be added, adding all the
				//   parents up to the root if necessary.
				if (!chosen[i].getAbsolutePath().startsWith(project.getRootPath()) ||
						chosen[i].getAbsolutePath().length() == project.getRootPath().length()) {
					JOptionPane.showMessageDialog(viewer,
						jEdit.getProperty("projectviewer.import.not_under_root"),
						jEdit.getProperty("projectviewer.import.not_under_root.title"),
						JOptionPane.ERROR_MESSAGE);
					return null;
				}
				selected = makePathTo(chosen[i].getParent(), lst);
			}

			VPTNode node = null;
			if (chosen[i].isDirectory()) {
				node = findDirectory(chosen[i], selected, true);
				if (node.getParent() == null) {
					if (selected == project) {
						lst.add(node);
					} else {
						selected.insert(node, selected.findIndexForChild(node));
					}
				}
				addTree(chosen[i], node, fnf);
			} else if (findDirectory(chosen[i], selected, false) == null) {
				node = new VPTFile(chosen[i]);
				project.registerFile((VPTFile)node);
				fileCount++;
				if (selected == project) {
					lst.add(node);
				} else {
					selected.insert(node, selected.findIndexForChild(node));
				}
			}

			if (i == 0) {
				if (node == null)
					node = findDirectory(chosen[i], selected, false);
				toShow = node;
			}
		}

		showFileCount();
		return lst;
	} //}}}

}

