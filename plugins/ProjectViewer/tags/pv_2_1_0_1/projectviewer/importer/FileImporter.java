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
import java.util.Iterator;
import java.util.List;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPlugin;

import projectviewer.ProjectViewer;
import projectviewer.PVActions;
import projectviewer.gui.ModalJFileChooser;
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

		NonProjectFileFilter filter = new NonProjectFileFilter();

		JFileChooser chooser = null;
		if (selected.isDirectory() && ((VPTDirectory)selected).getFile().exists()) {
			chooser = new ModalJFileChooser(selected.getNodePath());
		} else {
			chooser = new ModalJFileChooser(project.getRootPath());
		}

		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		chooser.addChoosableFileFilter(filter);

		List filters = getFileFilters(false);
		for (Iterator i = filters.iterator(); i.hasNext(); )
			chooser.addChoosableFileFilter((FileFilter) i.next());
		chooser.setFileFilter(filter);

		if(chooser.showDialog(this.viewer, jEdit.getProperty("projectviewer.import.add"))
				!= JFileChooser.APPROVE_OPTION) {
			return null;
		}

		if (chooser.getFileFilter() instanceof CVSEntriesFilter) {
			fnf = (FilenameFilter) chooser.getFileFilter();
		}

		File[] chosen = chooser.getSelectedFiles();
		if (chosen == null || chosen.length == 0) return null;

		ArrayList lst = new ArrayList();

		boolean asked = false, recurse = false;
		for (int i = 0; i < chosen.length; i++) {
			VPTNode node = null;
			if (!chosen[i].exists()) {
				node = findDirectory(chosen[i], selected, true);
			} else if (chosen[i].isDirectory()) {
				node = findDirectory(chosen[i], selected, true);
				if (!asked) {
					filters.listIterator().add(new AllFilesFilter());
					recurse = defineFileFilter(filters, null, FILTER_MSG_RECURSE, viewer);
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

	//{{{ -getFileFilters(boolean) : List
	/**
	 *	Instantiate the default file filters from Project Viewer and checks
	 *	all the other plugins looking for any custom filters they provide.
	 *
	 *	@param	addAllFilter	Whether to add the "AllFilesFilter" to the list.
	 **/
	private List getFileFilters(boolean addAllFilter) {
		List filters = new ArrayList();

		if (addAllFilter)
			filters.add(new AllFilesFilter());
		filters.add(new ImportSettingsFilter());
		filters.add(new CVSEntriesFilter());

		EditPlugin[] plugins = jEdit.getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			String list = jEdit.getProperty("plugin.projectviewer." +
							plugins[i].getClassName() + ".file-filters");
			Collection aList =
				PVActions.listToObjectCollection(list, plugins[i].getPluginJAR(),
					ImporterFileFilter.class);

			if (aList != null && aList.size() > 0) {
				filters.addAll(aList);
			}
		}

		return filters;
	} //}}}

	//{{{ #defineFileFilter(List, String, int, Component) : boolean
	/**
	 *	Shows a dialog asking the user for which file filter to use
	 *	when importing files.
	 *
	 *	@param	filters	The collection of filters to choose from. If null,
	 *			the "getFileFilters()" method will be called.
	 *	@param	name	The name of the node to where files will be imported.
	 *	@param	msgType	What message to show.
	 *	@param	parent	The parent component (or where to show the dialog).
	 *	@return	Whether to continue importing files.
	 */
	protected boolean defineFileFilter(List filters, String name, int msgType,
										Component parent) {
		if (filters == null)
			filters = getFileFilters(true);
		Object[] options = filters.toArray(new Object[filters.size() + 1]);
		options[options.length - 1] = jEdit.getProperty("projectviewer.import.no");

		String msg;
		String title;
		switch (msgType) {
			case FILTER_MSG_INITIAL_IMPORT:
				msg = "projectviewer.import.msg_proj_root";
				title = "projectviewer.import.msg_proj_root.title";
				break;

			case FILTER_MSG_RE_IMPORT:
				msg = "projectviewer.import.msg_reimport";
				title = "projectviewer.import.msg_reimport.title";
				break;

			default: // recurse?
				msg = "projectviewer.import.msg_recurse";
				title = "projectviewer.import.msg_recurse.title";
		}

		Object sel = JOptionPane.showInputDialog(parent,
						jEdit.getProperty(msg, new Object[] { name } ),
						jEdit.getProperty(title),
						JOptionPane.QUESTION_MESSAGE,
						null, options, fnf);

		if (sel == null || sel == options[options.length - 1]) {
			return false;
		} else {
			fnf = (FilenameFilter) sel;
		}
		return true;
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

	//{{{ -class _AllFilesFilter_
	/** Dumb file filter that accepts everything. */
	private static class AllFilesFilter extends ImporterFileFilter {

		//{{{ +getDescription() : String
		public String getDescription() {
			return null;
		} //}}}

		//{{{ +accept(File) : boolean
		public boolean accept(File file) {
			return true;
		} //}}}

		//{{{ +accept(File, String) : boolean
		public boolean accept(File file, String fileName) {
			return true;
		} //}}}

		//{{{ +getRecurseDescription() : String
		public String getRecurseDescription() {
			return	jEdit.getProperty("projectviewer.import.yes-all");
		} //}}}

	} //}}}

}

