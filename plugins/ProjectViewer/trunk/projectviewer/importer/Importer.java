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

import java.util.Stack;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;

import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Base class for importers. Importers are classes that select a set of nodes
 *	(which can be of any kind) and add them to a given node.
 *
 *	<p>Trees are updated in the following manner: when a node is inserted, the
 *	folder tree is updates immediately. The other two trees (which are a "flat"
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

	//{{{ +Importer(VPTNode) : <init>
	public Importer(VPTNode node) {
		this(node, null, false);
	} //}}}

	//{{{ +Importer(VPTNode, boolean) : <init>
	public Importer(VPTNode node, boolean noThread) {
		this(node, null, noThread);
	} //}}}

	//{{{ +doImport() : void
	/**
	 *	Main import method. It starts a new thread to actually do the importing,
	 *	so that the UI is not blocked during the process, which can take some
	 *	time for large numbers of files.
	 */
	public void doImport() {
		if (noThread) {
			run();
		} else {
			VFSManager.getIOThreadPool().addWorkRequest(this, false);
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

	//{{{ #findDirectory(File, VPTNode, boolean) : VPTNode
	/**
	 *	Looks, in the children list for the given parent, for a directory with
	 *	the given path. If it exists, return it. If not, creates a new directory
	 *	node if <i>create</i> is true, or else return null.
	 *
	 *	@param	dir		The directory to look for.
	 *	@param	parent	The node where to look for the directory.
	 *	@param	create	Whether to create a new node if a corresponding path is
	 *					not found in the parent node.
	 */
	protected VPTNode findDirectory(File dir, VPTNode parent, boolean create) {
		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			VPTNode n = (VPTNode) e.nextElement();
			if (n.getNodePath().equals(dir.getAbsolutePath())) {
				return n;
			} else if (n.isFile()) {
				try {
					if (((VPTFile)n).getFile().getCanonicalPath().equals(dir.getAbsolutePath())) {
						return n;
					}
				} catch (java.io.IOException ioe) {
					// shouldn't happen
					Log.log(Log.WARNING, this, ioe);
				}
			}
		}
		return (create) ? new VPTDirectory(dir) : null;
	} //}}}

	//{{{ #makePathTo(String, ArrayList) : VPTNode
	/**
	 *	Inserts all nodes from the root up to and including the given path, if
	 *	the nodes are not yet inserted. Returns the node representing the given
	 *	path.
	 */
	protected VPTNode makePathTo(String path, ArrayList added) {
		Stack dirs = new Stack();
		while (!path.equals(project.getRootPath())) {
			File pf = new File(path);
			dirs.push(pf);
			path = pf.getParent();
		}

		VPTNode where = project;
		while (!dirs.isEmpty()) {
			File curr = (File) dirs.pop();
			VPTNode n = findDirectory(curr, where, false);
			if (n == null) {
				n = new VPTDirectory(curr);
				if (added.size() == 0) {
					selected = where;
					added.add(n);
				} else {
					where.insert(n, where.findIndexForChild(n));
				}
			}
			where = n;
		}
		return where;
	} //}}}

	//{{{ #registerFile(VPTFile) : void
	/**
	 *	Registers the file in the project. Also, checks if the file's absolute
	 *	path is equal to the canonical path, and registers the canonical path
	 *	in the project in case they differ.
	 */
	protected void registerFile(VPTFile file) {
		project.registerFile(file);
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

	//{{{ +run() : void
	public void run() {
		invoke(new Runnable() {
			public void run() {
				viewer.setStatus(jEdit.getProperty("projectviewer.import.wait_msg"));
				viewer.setEnabled(false);
			}
		});
		try {
			final Collection c = internalDoImport();
			if (c != null && c.size() > 0) {
				invoke(new Runnable() {
					public void run() {
						for (Iterator i = c.iterator(); i.hasNext(); ) {
							VPTNode n = (VPTNode) i.next();
							importNode(n);
						}
						ProjectViewer.nodeStructureChangedFlat(project);
						if (fireEvent && project.hasListeners()) {
							fireProjectEvent();
						}
					}
				});
				ProjectManager.getInstance().saveProject(project);
			} else if (fireEvent) {
				if ((added != null && added.size() > 0) ||
						(removed != null && removed.size() > 0)) {
					invoke(new Runnable() {
						public void run() {
							fireProjectEvent();
						}
					});
				}
			}
		} finally {
			// in case any RuntimeException occurs, let's be cautious...
			invoke(new Runnable() {
				public void run() {
					viewer.setEnabled(true);
				}
			});
		}
		if (postAction != null)
			SwingUtilities.invokeLater(postAction);
	} //}}}

	//{{{ -fireProjectEvent() : void
	/** Fires an event based on the imported file(s). */
	protected void fireProjectEvent() {
		if ((added == null || added.size() == 0)
				&& (removed == null || removed.size() == 0)) {
			return;
		}

		if (added == null || added.size() == 0) {
			if (removed != null && removed.size() > 0) {
				project.fireFilesChanged(null, removed);
			}
		} else if (added.size() == 1 && (removed == null || removed.size() == 0)) {
			project.fireFileAdded((VPTFile)added.get(0));
		} else {
			project.fireFilesChanged(added, removed);
		}
	} //}}}

	//{{{
	/**
	 *	Invokes the given runnable in the appropriate manner, according to
	 *	the "noThread" value. If "noThread" is true, just call "run()",
	 *	otherwise use "SwingUtilities.invokeAndWait()".
	 */
	private void invoke(Runnable r) {
		if (noThread) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException ie) {
				// not gonna happen
			} catch (java.lang.reflect.InvocationTargetException ite) {
				// not gonna happen
			}
		}
	}
	//}}}

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
			viewer.setFolderTreeState(node, state);
		} //}}}

	} //}}}

}

