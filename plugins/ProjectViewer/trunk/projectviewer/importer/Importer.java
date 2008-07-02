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
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

import common.threads.WorkerThreadPool;

import projectviewer.gui.ImportDialog;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;

import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.PVActions;
import projectviewer.VFSHelper;
//}}}

/**
 *	Base class for importers. Importers are classes that select a set of nodes
 *	(which can be of any kind) and add them to a given node.
 *
 *	<p>Trees are updated in the following manner: when a node is inserted, the
 *	folder tree is updated immediately. The other two trees (which are a "flat"
 *	version of the file list, basically) are updated once at the end of the
 *	importing (to make the code simpler for those trees).</p>
 *
 *	<p>Since a lot of time may be required to import large number of files, the
 *	importing is done in its own separate thread, so that the GUI may be
 *	used normally during the process. This behaviour can be prevented if
 *	wished. If importing in a separate thread, the PV GUI will be blocked
 *	during the import phase (but the rest of jEdit will be usable).</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public abstract class Importer implements Runnable {

	//{{{ Instance variables

	/** The node to where the imported nodes will be added. */
	protected final ProjectViewer viewer;

	protected VPTNode		selected;
	protected VPTProject	project;
	private boolean			noThread;

	/** The list of added files, if any, for event firing purposes. */
	protected ArrayList		added;
	/** The list of removed files, if any, for event firing purposes. */
	protected ArrayList		removed;
	/** Whether this class should automatically fire the project event. */
	protected boolean fireEvent = true;

	/**
	 *	An action to be executed after the import occurs. It will be executed
	 *	in the AWT thread.
	 */
	protected Runnable		postAction;
	//}}}

	//{{{ +Importer(VPTNode, ProjectViewer, boolean) : <init>

	/**
	 *	If noThread is true, inhibits the use of a separate Thread to do the
	 *	importing. If you know just a few nodes will be imported, you may use
	 *	this.
	 */
	public Importer(VPTNode node, ProjectViewer viewer, boolean noThread) {
		if (node.isFile()) {
			node = (VPTNode) node.getParent();
		}
		selected = node;
		project = VPTNode.findProjectFor(node);
		if (project == null) {
			throw new IllegalArgumentException("Cannot add to root node.");
		}
		this.viewer = viewer;
		this.noThread = noThread;
		this.postAction = null;
	} //}}}

	//{{{ +Importer(VPTNode, ProjectViewer) : <init>
	public Importer(VPTNode node, ProjectViewer viewer) {
		this(node, viewer, false);
	} //}}}

	//{{{ +doImport() : void
	/**
	 *	Main import method. It starts a new thread to actually do the importing,
	 *	so that the UI is not blocked during the process, which can take some
	 *	time for large numbers of files.
	 */
	public void doImport() {
		if (noThread) {
			setViewerEnabled(false);
			run();
			setViewerEnabled(true);
		} else {
			WorkerThreadPool.getSharedInstance().addRequest(this);
		}
	} //}}}

	//{{{ #importNode(VPTNode, VPTNode) : void
	/**
	 *	Imports a child into the given parent.
	 *
	 *	<p>Important: when using a separate thread, wrap a call to this method
	 *	in a call to SwingUtilities.invokeLater() or
	 *	SwingUtilities.invokeAndWait(), to ensure thread safety.</p>
	 */
	protected void importNode(VPTNode child, VPTNode where) {
		ProjectViewer.insertNodeInto(child, where);
	} //}}}

	//{{{ #importNode(VPTNode) : void
	/**
	 *	Imports the node into the selected parent. The selected parent is
	 *	defined in the constructor for the importer (and generally should
	 *	be a selected node in the tree).
	 *
	 *	<p>Important: when using a separate thread, wrap a call to this method
	 *	in a call to SwingUtilities.invokeLater() or
	 *	SwingUtilities.invokeAndWait(), to ensure thread safety.</p>
	 */
	protected void importNode(VPTNode node) {
		ProjectViewer.insertNodeInto(node, selected);
	} //}}}

	//{{{ #*internalDoImport()* : Collection
	/**
	 *	Method to be called when importing nodes. The implementation should
	 *	eeturn a list of nodes to be added to the selected node. The method
	 *	{@link #importNode(VPTNode) importNode(VPTNode)} will be called for
	 *	each element of the collection.
	 */
	protected abstract Collection internalDoImport(); //}}}

	/**
	 *	This is called after {@link #internalDoImport()} is invoked, so
	 *	implementations can clean up any internal state. Default implementation
	 *	does nothing.
	 */
	protected void cleanup() {

	}

	//{{{ #findDirectory(String, VPTNode, boolean) : VPTNode
	/**
	 *	Looks, in the children list for the given parent, for a directory with
	 *	the given path. If it exists, return it. If not, creates a new directory
	 *	node if <i>create</i> is true, or else return null.
	 *
	 *	@param	url		The URL of the directory to look for.
	 *	@param	parent	The node where to look for the directory.
	 *	@param	create	Whether to create a new node if a corresponding path is
	 *					not found in the parent node.
	 */
	protected VPTNode findDirectory(String url, VPTNode parent, boolean create) {
		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			VPTNode n = (VPTNode) e.nextElement();
			if (n.isDirectory() && ((VPTDirectory)n).getURL().equals(url)) {
				return n;
			} else if (n.isFile() && ((VPTFile)n).getURL().equals(url)) {
				return n;
			}
		}
		return (create) ? new VPTDirectory(url) : null;
	} //}}}

	/**
	 * Creates a subtree starting at the given root, going down to the
	 * given path, updating the given list of added nodes as necessary.
	 *
	 * @param root Root node where to start constructing the path.
	 * @param path Path to insert (should be under the given root).
	 * @param flist List of added nodes to be updated (null OK).
	 *
	 * @return The newly created node.
	 * @throw IOException If an I/O error occur.
	 *
	 * @since PV 3.0.0
	 */
	protected VPTNode constructPath(VPTNode root,
									String path,
									List<VPTNode> flist)
		throws IOException
	{
		boolean isFile;
		Stack<String> dirs;
		String rootPath;
		VFS vfs;
		VFSFile file;

		assert path.startsWith(root.getNodePath()) :
			"Path not under root: " + path + " (root = " + root.getNodePath() + ")";

		if (path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - File.separator.length());
		}

		dirs = new Stack<String>();
		vfs = VFSManager.getVFSForPath(path);
		rootPath = root.getNodePath();

		file = VFSHelper.getFile(path);
		if (file == null) {
			return null;
		}
		isFile = (file.getType() == VFSFile.FILE);

		while (!path.equals(rootPath)) {
			dirs.push(path);
			path = vfs.getParentOfPath(path);
			/*
			 * VFS.getParentOfPath() returns paths with a trailing slash...
			 * BTW, it's interesting that it uses "File.separatorChar"
			 * when all this is supposed to be URL-based.
			 */
			 if (path.endsWith(File.separator)) {
				 path = path.substring(0, path.length() - File.separator.length());
			 }
		}

		while (!dirs.isEmpty()) {
			String curr = dirs.pop();
			VPTNode n = findDirectory(curr, root, false);

			if (n == null) {
				if (dirs.size() == 0 && isFile) {
					n = new VPTFile(curr);
				} else {
					n = new VPTDirectory(curr);
				}
				if (flist != null && flist.size() == 0) {
					selected = root;
					flist.add(n);
				} else {
					root.insert(n, root.findIndexForChild(n));
				}
			}
			root = n;
		}
		return root;
	}

	//{{{ #registerFile(VPTFile) : void
	/**
	 *	Registers the file in the project. Also, checks if the file's absolute
	 *	path is equal to the canonical path, and registers the canonical path
	 *	in the project in case they differ.
	 */
	protected void registerFile(VPTFile file) {
		project.registerNodePath(file);
		if (!contains(removed, file)) {
			if (added == null) added = new ArrayList();
			added.add(file);
		}
	} //}}}

	//{{{ #unregisterFile(VPTFile) : void
	/**
	 *	Unregisters a file from a project and adds the file to the list of
	 *	files that have been removed. It automatically checks that "added"
	 *	list to see if the file is already in there, so that the event does
	 *	not contain the same file being added and removed at the same time.
	 */
	protected void unregisterFile(VPTFile file) {
		project.unregisterNodePath(file);
		if (!contains(added, file)) {
			if (removed == null) removed = new ArrayList();
			removed.add(file);
		}
	} //}}}

	//{{{ -contains(ArrayList, VPTFile) : boolean
	/**
	 *	Checks whether the given list contains the given file or not. If the
	 *	file is found, it is removed from the list.
	 */
	private boolean contains(ArrayList list, VPTFile file) {
		if (list != null)
		for (int i = 0; i < list.size(); i++) {
			if (((VPTNode)list.get(i)).compareToNode(file) == 0) {
				list.remove(i);
				return true;
			}
		}
		return false;
	} //}}}

	//{{{ #setViewerEnabled(boolean) : void
	protected void setViewerEnabled(final boolean flag) {
		if (viewer != null) {
			final ProjectViewer fviewer = viewer;
			PVActions.swingInvoke(
				new Runnable() {
					//{{{ +run() : void
					public void run() {
						if (!flag)
							viewer.setStatus(jEdit.getProperty("projectviewer.import.wait_msg"));
						fviewer.setEnabled(flag);
					} //}}}
				}
			);
		}
	} //}}}

	//{{{ +run() : void
	public void run() {
		boolean error = false;
		setViewerEnabled(false);
		try {
			final Collection c = internalDoImport();
			cleanup();
			if (c != null && c.size() > 0) {
				PVActions.swingInvoke(new Runnable() {
					public void run() {
						for (Iterator i = c.iterator(); i.hasNext(); ) {
							VPTNode n = (VPTNode) i.next();
							importNode(n);
						}
						ProjectViewer.nodeStructureChangedFlat(project);
						fireProjectEvent();
					}
				});
			} else if (fireEvent) {
				if ((added != null && added.size() > 0) ||
						(removed != null && removed.size() > 0)) {
					PVActions.swingInvoke(new Runnable() {
						public void run() {
							fireProjectEvent();
						}
					});
				}
			}
			ProjectManager.getInstance().saveProject(project);
		} catch (RuntimeException e) {
			error = true;
			throw e;
		} finally {
			if (postAction == null || error)
				setViewerEnabled(true);
		}
		if (postAction != null)
			SwingUtilities.invokeLater(new PostActionWrapper());
	} //}}}

	//{{{ #fireProjectEvent() : void
	/** Fires an event based on the imported file(s). */
	protected void fireProjectEvent() {
		if ((added == null || added.size() == 0)
				&& (removed == null || removed.size() == 0)) {
			return;
		}

		if (added != null && added.size() == 0) {
			added = null;
		}

		if (removed != null && removed.size() == 0) {
			removed = null;
		}

		project.fireFilesChanged(added, removed);
	} //}}}


	protected void saveImportFilterStatus(VPTProject project,
										  ImportDialog dlg)
	{
		ImportUtils.saveFilter(project.getProperties(),
							   dlg.getImportFilter(),
							   "projectviewer.import");
	}


	protected void loadImportFilterStatus(VPTProject project,
										  ImportDialog dlg)
	{
		ImporterFileFilter filter =
			ImportUtils.loadFilter(project.getProperties(),
								   dlg.getFileFilters(),
								   "projectviewer.import");
		if (filter != null) {
			if (filter instanceof GlobFilter &&
				((GlobFilter)filter).isCustom()) {
				dlg.setCustomFilter((GlobFilter)filter);
			} else {
				dlg.setImportFilter(filter);
			}
		}
	}


	//{{{ #class ShowNode
	/** Makes sure a node is visible. */
	protected class ShowNode implements Runnable {

		private VPTNode toShow;

		//{{{ +ShowNode(VPTNode) : <init>
		public ShowNode(VPTNode toShow) {
			this.toShow = toShow;
		} //}}}

		//{{{ +run() : void
		public void run() {
			JTree tree = viewer.getCurrentTree();
			if (tree != null) {
				DefaultTreeModel tModel = (DefaultTreeModel) tree.getModel();
				TreeNode[] nodes = tModel.getPathToRoot(toShow);
				tree.makeVisible(new TreePath(nodes));
			}
		} //}}}

	} //}}}

	//{{{ #class NodeStructureChange
	protected class NodeStructureChange implements Runnable {

		private VPTNode node;
		private String state;

		//{{{ +NodeStructureChange(VPTNode, String) : <init>
		/**
		 *	Calls "nodeStructureChanged" for the given node. If "state" is
		 *	not null, also sets the tree state.
		 */
		public NodeStructureChange(VPTNode node, String state) {
			this.node = node;
			this.state = state;
		} //}}}

		//{{{ +run() : void
		public void run() {
			ProjectViewer.nodeStructureChanged(node);
			viewer.getTreePanel().setFolderTreeState(node, state);
		} //}}}

	} //}}}

	//{{{ -class PostActionWrapper
	private class PostActionWrapper implements Runnable {

		//{{{ +run() : void
		public void run() {
			setViewerEnabled(true);
			postAction.run();
		} //}}}

	} //}}}

}
