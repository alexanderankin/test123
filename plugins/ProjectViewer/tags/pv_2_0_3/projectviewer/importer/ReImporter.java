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

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;
//}}}

/**
 *	Re-imports files and/or directories from the project root and from other
 *	nodes that are not under the root. Re-importing from nodes not under the
 *	root works as following: if the directory does not exist, the file nodes
 *	below it are checked to see if they still exist, and removed if they don't;
 *	if the directory exists, the importing method chosen by the user is used
 *	to re-import the directory. These actions take place recursively.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ReImporter extends RootImporter {

	//{{{ +ReImporter(VPTNode, ProjectViewer) : <init>
	/**
	 *	Creates a ReImport object. Most of the functionality is inherited from
	 *	the RootImporter class.
	 */
	public ReImporter(VPTNode node, ProjectViewer viewer) {
		super(node, viewer, true);
	} //}}}

	//{{{ #internalDoImport() : Collection
	/**
	 *	Uses the user options from the RootImporter and re-imports the nodes
	 *	not under the root.
	 */
	protected Collection internalDoImport() {
		super.internalDoImport();

		// iterates through the children
		for (Enumeration en = project.children(); en.hasMoreElements(); ) {
			VPTNode node = (VPTNode) en.nextElement();
			String path = node.getNodePath();

			// check whether the node is under the root (new or old)
			if (!path.startsWith(project.getRootPath())) {
				if (node.isFile()) {
					if (!((VPTFile)node).getFile().exists()) {
						unregisterFile((VPTFile)node);
					}
				} else if (node.isDirectory()) {
					reimportDirectory((VPTDirectory)node);
				}
			}

		}

		return null;
	} //}}}

	//{{{ -reimportDirectory(VPTDirectory) : void
	private void reimportDirectory(VPTDirectory dir) {
		if (dir.getFile().exists()) {
			unregisterDir(dir);
			addTree(dir.getFile(), dir, fnf);
		} else {
			ArrayList toRemove = null;
			for (int i = 0; i < dir.getChildCount(); i++) {
				VPTNode node = (VPTNode) dir.getChildAt(i);
				if (node.isFile()) {
					if (!((VPTFile)node).getFile().exists()) {
						unregisterFile((VPTFile)node);
						dir.remove(i--);
					}
				} else if (node.isDirectory()) {
					reimportDirectory((VPTDirectory)node);
				}
			}
		}
	} //}}}

	//{{{ #unregisterDir(VPTDirectory) : void
	/**
	 *	Unregisters all files in the directory from the project, recursively,
	 *	and removes the child nodes from the parent.
	 */
	protected void unregisterDir(VPTDirectory dir) {
		for (int i = 0; i < dir.getChildCount(); i++) {
			VPTNode n = (VPTNode) dir.getChildAt(i);
			if (n.isDirectory()) {
				VPTDirectory cdir = (VPTDirectory) n;
				if (cdir.getFile().exists() &&
						cdir.getFile().getParent().equals(dir.getNodePath())) {
					unregisterFiles((VPTDirectory)n);
					dir.remove(i--);
				} else {
					reimportDirectory(cdir);
				}
			} else if (n.isFile()) {
				unregisterFile((VPTFile)n);
				dir.remove(i--);
			}
		}
	} //}}}


}

