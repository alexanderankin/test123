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

import org.xml.sax.Attributes;

import org.gjt.sp.jedit.io.VFSManager;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Handler for file nodes.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class FileNodeHandler extends NodeHandler {

	private static final String NODE_NAME = "file";
	private static final String PATH_ATTR = "path";
	private static final String NAME_ATTR = "name";

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
		return VPTFile.class;
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
		return false;
	}

	/**
	 *	Instantiates a VPTNode based on the information given in the attribute
	 *	list.
	 */
	public VPTNode createNode(Attributes attrs, VPTProject project) {
		String url = fixPath(attrs.getValue(PATH_ATTR));
		VPTFile vf = new VPTFile(url);
		if (attrs.getValue(NAME_ATTR) != null) {
			vf.setName(attrs.getValue(NAME_ATTR));
		}
		project.registerNodePath(vf);
		return vf;
	}

	/**
	 *	Saves a file node to the config file.
	 */
	public void saveNode(VPTNode node, Writer out) throws IOException {
		startElement(out);
		VPTFile file = (VPTFile) node;
		String fileName = VFSManager.getVFSForPath(file.getURL())
							.getFileName(file.getURL());
		if (!file.getName().equals(fileName)) {
			writeAttr(NAME_ATTR, file.getName(), out);
		}
		writeAttr(PATH_ATTR, file.getURL(), out);
	}
}

