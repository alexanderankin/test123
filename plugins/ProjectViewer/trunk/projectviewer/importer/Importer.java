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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
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
	private boolean			lockProject;

	private Map<VPTNode,VPTNode> addedNodes;

	/** The list of added files, if any, for event firing purposes. */
	private List<VPTFile>	added;
	/** The list of removed files, if any, for event firing purposes. */
	private List<VPTFile>	removed;
	/** The list of directories to be removed. */
	private List<VPTDirectory> removedDirs;

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
		this.lockProject = true;
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
	public final void doImport() {
		if (lockProject && !project.tryLock()) {
			setViewStatus("projectviewer.error.project_locked");
			return;
		}

		if (noThread) {
			setViewerEnabled(false);
			run();
			setViewerEnabled(true);
		} else {
			WorkerThreadPool.getSharedInstance().addRequest(this);
		}
	} //}}}


	/**
	 * Method called when importing nodes. The implementation should
	 * add files for import using one of the following methods:
	 *
	 * <ul>
	 *     <li>{@link #addNode(VPTNode, VPTNode)}</li>
	 *     <li>{@link #constructPath(VPTNode,String)}</li>
	 *     <li>{@link #findDirectory(String,VPTNode,boolean)}</li>
	 * </ul>
	 *
	 * @since PV 3.0.0
	 */
	protected abstract void internalDoImport();


	/**
	 * Tells the importer whether to lock the project while doing the
	 * import. If not locking, the caller is responsible for locking
	 * the project so other tasks are notified that the project is
	 * being modified.
	 *
	 * @param	lock	Whether to lock the project before importing.
	 *
	 * @since	PV 3.0.0
	 */
	public void setLockProject(boolean lock)
	{
		this.lockProject = lock;
	}


	/**
	 *	This is called after {@link #internalDoImport()} is invoked, so
	 *	implementations can clean up any internal state. Default implementation
	 *	does nothing.
	 *
	 *	@since	PV 3.0.0
	 */
	protected void cleanup()
	{

	}

	/**
	 * Inserts a new node into the private map of nodes that have been
	 * imported. The {@link #run()} method of this class will take care
	 * of processing all these nodes and properly registering files and
	 * updating the trees.
	 *
	 * @param	node	The node being inserted.
	 * @param	parent	Where the node is being inserted.
	 *
	 * @since PV 3.0.0
	 */
	protected void addNode(VPTNode node,
						   VPTNode parent)
	{
		assert (node != null) : "null node";
		assert (parent != null) : "null parent node";
		if (addedNodes == null) {
			addedNodes = new HashMap<VPTNode,VPTNode>();
		}
		addedNodes.put(node, parent);
	}


	/**
	 *	Looks, in the children list for the given parent, for a node with
	 *	the given path. If it exists, return it. If not, creates a new directory
	 *	node if <i>create</i> is true, or else return null.
	 *
	 *	If a new node is created, it's automatically registered in the
	 *	internal list of nodes to be processed.
	 *
	 *	@param	url		The URL of the directory to look for.
	 *	@param	parent	The node where to look for the directory.
	 *	@param	create	Whether to create a new node if a corresponding path is
	 *					not found in the parent node.
	 *
	 *	@return The node representing the given URL, or null if not
	 *	        found and create is false.
	 */
	protected VPTNode findDirectory(String url,
									VPTNode parent,
									boolean create)
	{
		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			VPTNode n = (VPTNode) e.nextElement();
			if (n.isDirectory() && ((VPTDirectory)n).getURL().equals(url)) {
				return n;
			} else if (n.isFile() && ((VPTFile)n).getURL().equals(url)) {
				return n;
			}
		}
		if (create) {
			VPTNode n = new VPTDirectory(url);
			addNode(n, parent);
			return n;
		}
		return null;
	}


	/**
	 * Creates a subtree starting at the given root, going down to the
	 * given path, updating the given list of added nodes as necessary.
	 *
	 * @param root Root node where to start constructing the path.
	 * @param path Path to insert (should be under the given root).
	 *
	 * @return The newly created node.
	 * @throws IOException If an I/O error occur.
	 *
	 * @since PV 3.0.0
	 */
	protected VPTNode constructPath(VPTNode root,
									String path)
		throws IOException
	{
		boolean addNode = true;
		boolean isFile;
		Stack<String> dirs;
		String rootPath;
		VFS vfs;
		VFSFile file;

		if (!path.startsWith(root.getNodePath())) {
			Log.log(Log.ERROR, this,
					"Path not under root: " + path +
					" (root = " + root.getNodePath() + ")");
			return null;
		}

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

			if (n == null && addedNodes != null) {
				/* Look in map of already added nodes. */
				for (VPTNode tmp : addedNodes.keySet()) {
					if (tmp.getNodePath().equals(curr)) {
						n = tmp;
						break;
					}
				}
			}

			if (n == null) {
				if (isFile && dirs.size() == 0) {
					n = new VPTFile(curr);
					if (contains(removed, (VPTFile) n)) {
						root = n;
						break;
					}
				} else {
					n = new VPTDirectory(curr);
				}
				if (addNode) {
					addNode(n, root);
					addNode = false;
				} else {
					root.insert(n, root.findIndexForChild(n));
				}
			} else if (n.isFile()) {
				contains(removed, (VPTFile) n);
			}
			root = n;
		}
		return root;
	}


	/**
	 * Marks a directory for removal. Directories will only be removed
	 * if, at the end of the import process, they are empty - meaning
	 * that the only children they have are other directories.
	 */
	protected void removeDirectory(VPTDirectory dir)
	{
		if (removedDirs == null) {
			removedDirs = new ArrayList<VPTDirectory>();
		}
		removedDirs.add(dir);
	}


	/**
	 * Marks a file for removal. At the end of the importing process,
	 * all removed files are unregistered from the project.
	 */
	protected void removeFile(VPTFile file)
	{
		if (!contains(added, file)) {
			if (removed == null) {
				removed = new ArrayList<VPTFile>();
			}
			removed.add(file);
		}
	}


	/**
	 *	Checks whether the given list contains the given file or not. If the
	 *	file is found, it is removed from the list.
	 */
	private boolean contains(List<VPTFile> list,
							 VPTFile file)
	{
		if (list != null)
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getURL().equals(file.getURL())) {
				list.remove(i);
				return true;
			}
		}
		return false;
	}

	//{{{ #setViewerEnabled(boolean) : void
	protected void setViewerEnabled(final boolean flag) {
		if (viewer != null) {
			PVActions.swingInvoke(
				new Runnable() {
					public void run() {
						if (!flag) {
							setViewStatus("projectviewer.import.wait_msg");
						}
						viewer.setEnabled(flag);
					}
				}
			);
		}
	} //}}}

	//{{{ +run() : void
	public final void run() {
		boolean error = false;
		setViewerEnabled(false);
		try {
			internalDoImport();
			cleanup();
			PVActions.swingInvoke(
				new Runnable() {
					public void run() {
						processAddedNodes();
					}
				}
			);
			ProjectManager.getInstance().saveProject(project);
		} catch (RuntimeException e) {
			error = true;
			throw e;
		} finally {
			if (postAction == null || error)
				setViewerEnabled(true);
			if (lockProject) {
				project.unlock();
			}
		}
		if (postAction != null)
			SwingUtilities.invokeLater(new PostActionWrapper());
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


	/**
	 * Sets a message in the view's status bar.
	 *
	 * @param	msgKey	jEdit property key, or message to be displayed.
	 *
	 * @since	PV 3.0.0
	 */
	protected void setViewStatus(final String msgKey)
	{
		PVActions.swingInvoke(
			new Runnable() {
				public void run() {
					String msg = jEdit.getProperty(msgKey, msgKey);
					View v = (viewer != null) ? viewer.getView()
											  : jEdit.getActiveView();
					v.getStatus().setMessageAndClear(msg);
				}
			}
		);
	}


	/**
	 * Processes a directory node, registering all its child files.
	 *
	 * @param	node	The directory node.
	 */
	private void processDirectory(VPTNode node)
	{
		assert (node.isDirectory());

		for (int i = 0; i < node.getChildCount(); i++) {
			VPTNode child = (VPTNode) node.getChildAt(i);
			if (child.isFile()) {
				registerFile((VPTFile)child);
			} else if (child.isDirectory()) {
				processDirectory(child);
			}
		}
	}


	/**
	 * Checks whether the given directory is "empty". It's considered
	 * empty if in the tree under the directory there are no openable
	 * nodes.
	 *
	 * @param	dir		The directory node.
	 */
	private boolean isDirectoryEmpty(VPTDirectory dir)
	{
		for (int i = dir.getChildCount() - 1; i >= 0 ; i--) {
			VPTNode child = (VPTNode) dir.getChildAt(i);
			if (child.canOpen()) {
				return false;
			} else if (child.isDirectory() &&
					   !isDirectoryEmpty((VPTDirectory)child)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Processes the internal list of added nodes, registering any new
	 * files and properly inserting the nodes in the trees. This method
	 * should be called in the AWT thread, since it updates the UI.
	 */
	private void processAddedNodes()
	{
		/* Process all the added nodes. */
		if (addedNodes != null) {
			for (VPTNode n : addedNodes.keySet()) {
				VPTNode parent = addedNodes.get(n);
				ProjectViewer.insertNodeInto(n, parent);
				if (n.isFile()) {
					registerFile((VPTFile)n);
				} else if (n.isDirectory()) {
					processDirectory(n);
				}
			}
		}

		/* Process all the files marked for removal. */
		if (removed != null) {
			for (VPTFile file : removed) {
				project.unregisterNodePath(file);
				ProjectViewer.removeNodeFromParent(file);
			}
		}

		/* Process all the directories marker for removal. */
		if (removedDirs != null) {
			for (VPTDirectory dir : removedDirs) {
				if (isDirectoryEmpty(dir)) {
					ProjectViewer.removeNodeFromParent(dir);
				}
			}
		}

		if (added != null && added.size() == 0) {
			added = null;
		}

		/* Show the count of imported files. */
		if (added != null) {
			String msg = jEdit.getProperty("projectviewer.import.msg_result",
								new Object[] { new Integer(added.size()) });
			if (viewer != null) {
				viewer.setStatus(msg);
			} else {
				jEdit.getActiveView().getStatus().setMessageAndClear(msg);
			}
		}

		/* Fire a project event notifying interested parties of any changes. */

		if (removed != null && removed.size() == 0) {
			removed = null;
		}

		if (added != null || removed != null) {
			project.fireFilesChanged(added, removed);
		}
	}


	/**
	 *	Registers the file in the project. Also, checks if the file's absolute
	 *	path is equal to the canonical path, and registers the canonical path
	 *	in the project in case they differ.
	 */
	private void registerFile(VPTFile file)
	{
		if (!contains(removed, file)) {
			project.registerNodePath(file);
			if (added == null) {
				added = new ArrayList<VPTFile>();
			}
			added.add(file);
		}
	}


	//{{{ #class ShowNodes
	/**
	 * Makes sure all newly imported nodes are visible. Added
	 * directories will be automatically expanded.
	 */
	protected class ShowNodes implements Runnable
	{

		public void run()
		{
			JTree tree = viewer.getCurrentTree();
			if (tree != null) {
				for (VPTNode n : addedNodes.keySet()) {
					DefaultTreeModel tModel = (DefaultTreeModel) tree.getModel();
					TreeNode[] nodes = tModel.getPathToRoot(n);
					tree.makeVisible(new TreePath(nodes));
					if (n.isDirectory()) {
						/* Expand directories. */
						TreePath path = new TreePath(nodes);
						tree.expandPath(path);
					}
				}
			}
		}

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
