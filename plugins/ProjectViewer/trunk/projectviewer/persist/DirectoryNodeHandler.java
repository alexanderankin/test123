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

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;
//}}}

/**
 *	Handler for directory nodes.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class DirectoryNodeHandler extends NodeHandler {

	private static final String NODE_NAME = "directory";
	private static final String PATH_ATTR = "path";

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
		return VPTDirectory.class;
	}

	/**
	 *	Returns whether the node is a child of nome other node or not.
	 */
	public boolean isChild() {
		return true;
	}

	/**
	 *	Returns whether the node(s) handled by this handler are expected to
	 *	have children or not.
	 */
	public boolean hasChildren() {
		return true;
	}

	/**
	 *	Instantiates a VPTNode based on the information given in the attribute
	 *	list.
	 */
	public VPTNode createNode(Map attrs, VPTProject project) {
		return new VPTDirectory(new File((String)attrs.get(PATH_ATTR)));
	}

	/**
	 *	Saves a VPTDirectory node.
	 */
	public void saveNode(VPTNode node, Writer out) throws IOException {
		startElement(out);
		writeAttr(PATH_ATTR, xlatePath(((VPTDirectory)node).getFile().getAbsolutePath()), out);
	}
}

