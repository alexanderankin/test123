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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

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

	protected int fileCount;
	protected boolean prune;
	
	//{{{ Constructor
	
	public FileImporter(VPTNode node, ProjectViewer viewer) {
		super(node, viewer);
		prune = false;
	}
	
	//}}}
	
	//{{{ internalDoImport() method
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
		File[] chosen = chooseFiles();
		if (chosen == null || chosen.length == 0) return null;

		ArrayList lst = new ArrayList();
		
		FilenameFilter fnf = null;
		boolean asked = false, recurse = false;
		long t = System.currentTimeMillis();
		for (int i = 0; i < chosen.length; i++) {
			VPTNode node;
			if (chosen[i].isDirectory()) {
				node = new VPTDirectory(chosen[i]);
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
									null, options, options[0]);
					
					if (sel == null) {
						// cancel
						return null;
					}
					
					if (sel == options[1]) {
						recurse = true;
					} else if (sel == options[2]) {
						fnf = new ImportSettingsFilter();
						recurse = true;
					} else if (sel == options[3]) {
						// TODO: CVS/Entries filter
					} else {
						recurse = false;
					}
					asked = true;
				} 
				if (recurse) {
					addTree(chosen[i], node, fnf);
				}
			} else {
				node = new VPTFile(chosen[i]);
				project.registerFile((VPTFile)node);
				fileCount++;
			}
			lst.add(node);
		}
		
		viewer.setStatus(
			jEdit.getProperty("projectviewer.import.msg_result",
				new Object[] { new Integer(fileCount) }));
		
		return lst;
	} //}}}
	
	//{{{ addTree(File, VPTNode) method
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
			VPTNode child;
			if (children[i].isDirectory()) {
				child = findDirectory(children[i], where);
			} else {
				child = new VPTFile(children[i]);
				if (where.getIndex(child) != -1) {
					continue;
				}
			}
			
			if (child.isDirectory()) {
				addTree(children[i], child, filter);
			} else {
				project.registerFile((VPTFile)child);
				fileCount++;
			}

			if (!prune || !child.isDirectory() || child.getChildCount() != 0) {
				where.add(child);
			}
		}
		
		where.sortChildren();
	} //}}}
	
	//{{{ findDirectory(String, VPTNode) method
	/** 
	 *	Looks, in the children list for the given parent, for a directory with
	 *	the given path. If it exists, return it, if not, create a new VPTDirectory
	 *	and return it.
	 *
	 *	@param	dir		The directory to look for.
	 *	@param	parent	The node where to look for the directory.
	 */
	private VPTNode findDirectory(File dir, VPTNode parent) {
		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			VPTNode n = (VPTNode) e.nextElement();
			if (n.getNodePath().equals(dir.getAbsolutePath())) {
				return n;
			}
		}
		return new VPTDirectory(dir);
	} //}}}
	
	//{{{ chooseFiles() method
	/**
	 *	Chooses what files are to be imported to the selected node.
	 */
	protected File[] chooseFiles() {
		JFileChooser chooser = null;
		if (selected.isDirectory() && ((VPTDirectory)selected).getFile().exists()) {
			chooser = new JFileChooser(selected.getNodePath());
		} else {
			chooser = new JFileChooser(project.getRootPath());
		}
		
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		NonProjectFileFilter filter = new NonProjectFileFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		
		if(chooser.showOpenDialog(this.viewer) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		return chooser.getSelectedFiles();	
	} //}}}

	
	//{{{ NonProjectFileFilter class
	/**	A FileFilter that filters out files already added to the project. */
	protected class NonProjectFileFilter extends FileFilter {
		
		public String getDescription() {
			return jEdit.getProperty("projectviewer.non-project-filter");
		}
		
		public boolean accept(File f) {
			return (project.getFile(f.getAbsolutePath()) == null);
		}
		
	} //}}}
	
}
