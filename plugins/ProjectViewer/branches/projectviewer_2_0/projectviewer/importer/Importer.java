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
import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	Base class for importers. Importers are classes that select a set of nodes
 *	(which can be of any kind) and add them to a given node.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public abstract class Importer {

	//{{{ Instance variables
	
	/** The node to where the imported nodes will be added. */
	protected VPTNode		where;
	protected VPTProject	project;
	
	private final DefaultTreeModel	treeModel;
	
	//}}}
	
	//{{{ Constructor
	
	public Importer(VPTNode node, JTree tree) {
		if (node.isFile()) {
			node = (VPTNode) node.getParent();
		}
		where = node;
		if (node.isRoot()) {
			throw new IllegalArgumentException("Cannot add to root node.");
		} else {
			while (!node.isProject()) {
				node = (VPTNode) node.getParent();
			}
			project = (VPTProject) node;
		}
		this.treeModel = (DefaultTreeModel) tree.getModel();
	}
	
	//}}}
	
	//{{{ internalDoImport() method
	/**
	 *	The "internalDoImport()" method should select the nodes and return them 
	 *	in the form of a collection. The base class takes care of adding the
	 *	selected nodes to the parent node.
	 *
	 *	@return	A collection of VPTNode instances.
	 */
	public abstract Collection internalDoImport(); //}}}	

	//{{{ registerFiles(Enumeration) method
	/**
	 *	Looks at the enumeration passed, and register any VPTFiles that point
	 *	to files into the project.
	 */
	private void registerFiles(Enumeration children) {
		while (children.hasMoreElements()) {
			VPTNode node = (VPTNode) children.nextElement();
			if (node.isFile()) {
				project.registerFile((VPTFile)node);
			} else if (node.getAllowsChildren()) {
				registerFiles(node.children());
			}
		}
	} //}}}
	
	//{{{ doImport() method
	/**	
	 *	Method to be called when importing nodes. It takes care of registering
	 *	file nodes to the corresponding project so that queries can be faster.
	 */
	public void doImport() {
		Collection c = internalDoImport();
		if (c == null) return;
		Iterator nodes = c.iterator();
		while (nodes.hasNext()) {
			VPTNode node = (VPTNode) nodes.next();
			if (node.isFile()) {
				project.registerFile((VPTFile)node);
			} else if (node.getAllowsChildren()) {
				registerFiles(node.children());
			}
			where.add(node);
		}
		treeModel.nodeStructureChanged(where);
	} //}}}

}
