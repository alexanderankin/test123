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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

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

	private final ProjectViewer viewer; 
	
	//{{{ Constructor
	
	public FileImporter(VPTNode node, ProjectViewer viewer) {
		super(node);
		this.viewer = viewer;
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
	public Collection internalDoImport() {
		
		JFileChooser chooser = null;
		if (where.isDirectory() && ((VPTFile)where).getFile().exists()) {
			chooser = new JFileChooser(((VPTFile)where).getFile());
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

		File[] chosen = chooser.getSelectedFiles();
		if (chosen == null || chosen.length == 0) return null;

		ArrayList nodes = new ArrayList();
		
		boolean asked = false, recurse = false;
		for (int i = 0; i < chosen.length; i++) {
			VPTNode node;
			if (chosen[i].isDirectory()) {
				node = new VPTDirectory(chosen[i]);
			} else {
				node = new VPTFile(chosen[i]);
			}
			
			if (node.isDirectory()) {
				if (!asked) {
					recurse = (JOptionPane.showConfirmDialog(this.viewer,
								"Do you want to import directories recursively?",
								"Traverse directories?",
								JOptionPane.YES_NO_OPTION)  == JOptionPane.YES_OPTION);
					asked = true;
				} 
				if (recurse) {
					addTree(chosen[i], node);
				}
			}
			
			nodes.add(node);
		}

		return nodes;
	} //}}}	
	
	//{{{ addTree(File, VPTNode) method
	/**	Adds a directory tree to the give node. */
	private void addTree(File root, VPTNode node) {
		File[] children = root.listFiles();
		if (children == null) return;
		
		for (int i = 0; i < children.length; i++) {
			VPTNode child;
			if (children[i].isDirectory()) {
				child = new VPTDirectory(children[i]);
			} else {
				child = new VPTFile(children[i]);
			}
			if (child.isDirectory()) {
				addTree(children[i], child);
			}
			node.add(child);
		}
	} //}}}
	
	//{{{ NonProjectFileFilter class
	/**	A FileFilter that filters out projects already added to the project. */
	private class NonProjectFileFilter extends FileFilter {
		
		public String getDescription() {
			return "Non Project Files";
		}
		
		public boolean accept(File f) {
			return (project.getFile(f.getAbsolutePath()) == null);
		}
		
	} //}}}
	
}
