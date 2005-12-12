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
package projectviewer.persist;

//{{{ Imports
import java.io.File;
import java.io.Writer;
import java.io.IOException;

import java.util.Map;
import java.util.Iterator;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	Handler for project nodes.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectNodeHandler extends NodeHandler {

	//{{{ Constants
	protected static final String NODE_NAME	= "project";
	private static final String PATH_ATTR	= "path";
	private static final String URL_ATTR	= "url";
	//}}}

	/**
	 *	Returns the name of the nodes that should be delegated to this handler
	 *	when loading configuration data.
	 */
	public String getNodeName() {
		return NODE_NAME;
	}

	/**
	 *	Returns the class of the nodes that should be delegated to this handler
	 *	when saving node data to the config file.
	 */
	public Class getNodeClass() {
		return VPTProject.class;
	}

	/**
	 *	Returns whether the node is a child of nome other node or not.
	 */
	public boolean isChild() {
		return false;
	}

	/**
	 *	Returns whether the node(s) handled by this handler are expected to
	 *	have children or not.
	 */
	public boolean hasChildren() {
		return true;
	}

	//{{{ createNode(Attributes, VPTProject) method
	/**
	 *	Instantiates a VPTNode based on the information given in the attribute
	 *	list.
	 */
	public VPTNode createNode(Map attrs, VPTProject project) {
		if (File.separatorChar == '\\') {
			project.setRootPath(((String)attrs.get(PATH_ATTR)).replace('/', '\\'));
		} else {
			project.setRootPath((String)attrs.get(PATH_ATTR));
		}
		project.setURL((String)attrs.get(URL_ATTR));
		return project;
	} //}}}

	//{{{ saveNode(VPTNode, Writer) method
	/**
	 *	Saves a project node. This behaves a little differently than other
	 *	nodes, since it not only saves the project data, but creates nodes
	 *	for the project's properties and open files.
	 */
	public void saveNode(VPTNode node, Writer out) throws IOException {
		startElement(out);
		writeAttr(PATH_ATTR, translatePath(((VPTProject)node).getRootPath()), out);
		if (((VPTProject)node).getURL() != null)
			writeAttr(URL_ATTR, ((VPTProject)node).getURL(), out);
		out.write(">\n");

		// save the properties
		VPTProject proj = (VPTProject) node;
		PropertyNodeHandler nh = new PropertyNodeHandler();
		Map props = proj.getProperties();
		for (Iterator it = props.keySet().iterator(); it.hasNext(); ) {
			String p = (String) it.next();
			nh.saveNode(p, props.get(p), out);
		}

		// save the open files
		OpenFileNodeHandler ofnh = new OpenFileNodeHandler();
		for (Iterator it = proj.getOpenFiles(); it.hasNext(); ) {
			ofnh.saveNode((String)it.next(), out);
		}

	} //}}}
}

