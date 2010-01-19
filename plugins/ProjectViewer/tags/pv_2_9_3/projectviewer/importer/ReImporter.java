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
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;

import projectviewer.ProjectViewer;
import projectviewer.PVActions;
import projectviewer.VFSHelper;
import projectviewer.gui.ImportDialog;
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


	/**
	 *	Uses the user options from the RootImporter and re-imports the nodes
	 *	not under the root.
	 */
	protected void internalDoImport()
	{
		if (selected.isProject()) {
			super.oldRoot = ((VPTProject)selected).getRootPath();
			super.internalDoImport();

			/*
			 * Hack: if the filter is null, it means the user canceled
			 * the action. There actually should be a cleaner way of
			 * doing this.
			 */
			if (fnf == null) {
				return;
			}

			// iterates through the children
			for (int i = 0; i < project.getChildCount(); i++) {
				VPTNode node = (VPTNode) project.getChildAt(i);
				String path = node.getNodePath();

				// check whether the node is under the root (new or old)
				if (!path.startsWith(project.getRootPath())) {
					if (node.isFile()) {
						VFSFile file = ((VPTFile)node).getFile();
						if (file == null || !file.isReadable()) {
							removeFile((VPTFile)node);
						}
					} else if (node.isDirectory()) {
						reimportDirectory((VPTDirectory)node, false);
					}
				}
			}
		} else {
			String title = jEdit.getProperty("projectviewer.import.msg_reimport.title");
			ImportDialog id = showImportDialog(title, FILTER_CONF_PROJECT);

			if (id.isApproved()) {
				String state = viewer.getTreePanel().getFolderTreeState(selected);
				fnf = id.getImportFilter();
				reimportDirectory((VPTDirectory)selected, id.getFlattenFilePaths());
				postAction = new NodeStructureChange(selected, state);
				saveImportFilterStatus(project, id, FILTER_CONF_PROJECT);
			}
		}
	}

	//{{{ -reimportDirectory(VPTDirectory, boolean) : void
	private void reimportDirectory(VPTDirectory dir, boolean flatten) {
		try {
			if (VFSHelper.pathExists(dir.getURL())) {
				unregisterDir(dir, flatten);
				importFiles(dir, fnf, true, flatten);
			} else {
				for (int i = 0; i < dir.getChildCount(); i++) {
					VPTNode node = (VPTNode) dir.getChildAt(i);
					if (node.isFile()) {
						VFSFile file = ((VPTFile)node).getFile();
						if (file == null || !file.isReadable()) {
							removeFile((VPTFile)node);
						}
					} else if (node.isDirectory()) {
						reimportDirectory((VPTDirectory)node, flatten);
					}
				}
				removeDirectory(dir);
			}
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, "VFS error while importing", ioe);
		}
	} //}}}

	//{{{ #unregisterDir(VPTDirectory, boolean) : void
	/**
	 *	Unregisters all files in the directory from the project, recursively,
	 *	and removes the child nodes from the parent.
	 */
	protected void unregisterDir(VPTDirectory dir, boolean flatten) {
		for (int i = 0; i < dir.getChildCount(); i++) {
			VPTNode n = (VPTNode) dir.getChildAt(i);
			if (n.isDirectory()) {
				VPTDirectory cdir = (VPTDirectory) n;
				String parent = VFSManager.getVFSForPath(cdir.getURL())
				                          .getParentOfPath(cdir.getURL());
				if (VFSHelper.pathExists(cdir.getURL()) &&
					parent.equals(dir.getNodePath())) {
					unregisterFiles((VPTDirectory)n);
					removeDirectory((VPTDirectory)n);
				} else {
					reimportDirectory(cdir, flatten);
				}
			} else if (n.isFile()) {
				removeFile((VPTFile)n);
			}
		}
	} //}}}

}

