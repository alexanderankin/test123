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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;
//}}}

/**
 *	Imports a single file into a project. This importer is designed to import
 *	single files that are below the project root, adding all paths up to the
 *	given file. Do not use it for other types of imports.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class SingleFileImporter extends Importer {

	protected String path;
	
	//{{{ Constructor
	
	public SingleFileImporter(VPTNode node, String path) {
		super(node, true);
		this.path = path;
	}
	
	//}}}
	
	//{{{ internalDoImport() method
	/**
	 *	Imports the file given in the constructor into the project. If the file
	 *	is not below the project root, do nothing.
	 *
	 *	@return	A collection of VPTNode instances.
	 */
	protected Collection internalDoImport() {
		if (!path.startsWith(project.getRootPath())) {
			return null;
		}
		
		File f = new File(path);
		String p = f.getParent();
		Stack dirs = new Stack();
		while (!p.equals(project.getRootPath())) {
			File pf = new File(p);
			dirs.push(pf);
			p = pf.getParent();
		}
		
		VPTNode where = project;
		ArrayList added = null;
		while (!dirs.isEmpty()) {
			File curr = (File) dirs.pop();
			VPTNode n = findDirectory(curr, project);
			if (n == null) {
				n = new VPTDirectory(curr);
				if (where == project) {
					added = new ArrayList();
					added.add(n);
				} else {
					where.add(n);
					where.sortChildren();
				}
			}
			where = n;
		}
		
		VPTFile vf = new VPTFile(f);
		if (where == project) {
			added = new ArrayList();
			added.add(vf);
		} else {
			where.add(vf);
			where.sortChildren();
		}
		project.registerFile(vf);
		return added;
	} //}}}
	
	//{{{ findDirectory(String, VPTNode) method
	/** 
	 *	Looks, in the children list for the given parent, for a directory with
	 *	the given path. If it exists, return it, if not, return null.
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
		return null;
	} //}}}
	
}
