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
 *	wished.</p>
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
	private	 boolean	noThread;

	//}}}

	//{{{ Constructors

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
		if (node.isRoot()) {
			throw new IllegalArgumentException("Cannot add to root node.");
		} else {
			while (!node.isProject()) {
				node = (VPTNode) node.getParent();
			}
			project = (VPTProject) node;
		}
		this.viewer = viewer;
		this.noThread = noThread;
	}

	public Importer(VPTNode node, ProjectViewer viewer) {
		this(node, viewer, false);
	}

	public Importer(VPTNode node) {
		this(node, null, false);
	}

	public Importer(VPTNode node, boolean noThread) {
		this(node, null, noThread);
	}

	//}}}

	//{{{ doImport() method
	/**
	 *	Main import method. It starts a new thread to actually do the importing,
	 *	so that the UI is not blocked during the process, which can take some
	 *	time for large numbers of files.
	 */
	public void doImport() {
		if (noThread) {
			run();
		} else {
			new Thread(this).start();
		}
	} //}}}

	//{{{ importNode(VPTNode, VPTNode) method
	/** Imports a child into the given parent. */
	protected void importNode(VPTNode child, VPTNode where) {
		ProjectViewer.insertNodeInto(child, where);
	} //}}}

	//{{{ importNode(VPTNode) method
	/**
	 *	Imports the node into the selected parent. The selected parent is
	 *	defined in the constructor for the importer (and generally should
	 *	be a selected node in the tree).
	 */
	protected void importNode(VPTNode node) {
		ProjectViewer.insertNodeInto(node, selected);
	} //}}}

	//{{{ internalDoImport() method
	/**
	 *	Method to be called when importing nodes. The implementation should
	 *	eeturn a list of nodes to be added to the selected node. The method
	 *	{@link #importNode(VPTNode) importNode(VPTNode)} will be called for
	 *	each element of the collection.
	 */
	protected abstract Collection internalDoImport(); //}}}

	//{{{ findDirectory(String, VPTNode) method
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
			}
		}
		return (create) ? new VPTDirectory(dir) : null;
	} //}}}

	//{{{ makePathTo(String) method
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
			path= pf.getParent();
		}

		VPTNode where = project;
		while (!dirs.isEmpty()) {
			File curr = (File) dirs.pop();
			VPTNode n = findDirectory(curr, project, false);
			if (n == null) {
				n = new VPTDirectory(curr);
				if (where == project) {
					added.add(n);
				} else {
					where.insert(n, where.findIndexForChild(n));
				}
			}
			where = n;
		}
		return where;
	} //}}}

	//{{{ run() method
	public void run() {
		Collection c = internalDoImport();
		if (c != null && c.size() > 0) {
			for (Iterator i = c.iterator(); i.hasNext(); ) {
				importNode((VPTNode)i.next());
			}
			ProjectViewer.nodeStructureChangedFlat(project);
			if (ProjectViewerConfig.getInstance().getSaveOnChange()) {
				ProjectManager.getInstance().saveProject(project);
			}
		}
	} //}}}

}

