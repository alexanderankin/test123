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
 *	Imports files and/or directories from the project root. Optionally, can
 *	remove all existing files under the root before doing a fresh import.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class RootImporter extends FileImporter {

	//{{{ Private members
	private Component parent;
	private boolean clean;

	protected FilenameFilter fnf;
	protected String oldRoot;
	//}}}

	//{{{ +RootImporter(VPTNode, String, ProjectViewer, Component) : <init>

	/**
	 *	Creates an Importer that uses a component other than the ProjectViewer
	 *	as the parent of the dialogs shown to the user. If "oldRoot" is not null,
	 *	files under this directory will be removed from the root node of the
	 *	project.
	 */
	public RootImporter(VPTNode node, String oldRoot, ProjectViewer viewer, Component parent) {
		super(node, viewer);
		if (parent != null) {
			this.parent = parent;
		} else if (viewer != null) {
			this.parent = viewer;
		} else {
			this.parent = jEdit.getActiveView();
		}
		clean = (oldRoot != null);
		this.oldRoot = oldRoot;
	} //}}}

	//{{{ +RootImporter(VPTNode, ProjectViewer, boolean) : <init>
	/**
	 *	Imports files from the root of the project. If "clean" is "true", the
	 *	existing nodes that are below the root of the project will be removed
	 *	before the importing.
	 */
	public RootImporter(VPTNode node, ProjectViewer viewer, boolean clean) {
		this(node, null, viewer, null);
		this.clean = clean;
	}

	//}}}

	//{{{ #internalDoImport() : Collection
	/** Asks if the user wants to import files from the chosen project root. */
	protected Collection internalDoImport() {
		fileCount = 0;

		if (!defineFileFilter(project.getName(), (project.getChildCount() == 0))) {
			return null;
		}
		String state = null;
		if (viewer != null) {
			state = viewer.getFolderTreeState(project);
		}

		if (clean) {
			if (oldRoot == null) {
				oldRoot = project.getRootPath();
			}
			Enumeration e = project.children();
			ArrayList toRemove = new ArrayList();
			removed = new ArrayList();
			while (e.hasMoreElements()) {
				VPTNode n = (VPTNode) e.nextElement();
				if (n.getNodePath().startsWith(oldRoot)) {
					toRemove.add(n);
				}
			}
			if (toRemove.size() > 0) {
				for (Iterator i = toRemove.iterator(); i.hasNext(); ) {
					VPTNode n = (VPTNode) i.next();
					if (n.isDirectory()) {
						unregisterFiles((VPTDirectory)n);
					} else if (n.isFile()) {
						unregisterFile((VPTFile)n);
					}
					if (n.getChildCount() == 0)
						project.remove(n);
				}
			}
		}

		addTree(new File(project.getRootPath()), project, fnf);
		if (state != null) {
			postAction = new NodeStructureChange(project, state);
		}

		showFileCount();
		return null;
	} //}}}

	//{{{ #unregisterFiles(VPTDirectory) : void
	/** Unregisters all files in the directory from the project, recursively. */
	protected void unregisterFiles(VPTDirectory dir) {
		for (int i = 0; i < dir.getChildCount(); i++) {
			VPTNode n = (VPTNode) dir.getChildAt(i);
			if (n.isDirectory()) {
				unregisterFiles((VPTDirectory)n);
				if (n.getChildCount() == 0)
					dir.remove(i--);
			} else if (n.isFile()) {
				unregisterFile((VPTFile)n);
				dir.remove(i--);
			}
		}
	} //}}}

	//{{{ #defineFileFilter(String, boolean) : boolean
	/**
	 *	Shows a dialog asking the user for which file filter to use
	 *	when importing files.
	 *
	 *	@param	name	The name of the node to where files will be imported.
	 *	@param	initial	Whether this is an initial import for a project or
	 *					not.
	 *	@return	Whether to continue importing files.
	 */
	protected boolean defineFileFilter(String name, boolean initial) {
		Object[] options = {
			jEdit.getProperty("projectviewer.import.yes-settings"),
			jEdit.getProperty("projectviewer.import.yes-all"),
			jEdit.getProperty("projectviewer.import.yes-cvs"),
			jEdit.getProperty("projectviewer.import.no")
		};

		String msg = (initial) ? "projectviewer.import.msg_proj_root"
								: "projectviewer.import.msg_reimport";
		String title = (initial) ? "projectviewer.import.msg_proj_root.title"
									: "projectviewer.import.msg_reimport.title";
		Object sel = JOptionPane.showInputDialog(parent,
						jEdit.getProperty(msg, new Object[] { name } ),
						jEdit.getProperty(title),
						JOptionPane.QUESTION_MESSAGE,
						null, options, options[0]);

		fnf = null;
		if (sel == null || sel == options[3]) {
			return false;
		} else if (sel == options[0]) {
			fnf = new ImportSettingsFilter();
		} else if (sel == options[2]) {
			fnf = new CVSEntriesFilter();
		}
		return true;
	} //}}}

}

