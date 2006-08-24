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
import java.util.Map;

import java.io.Writer;
import java.io.IOException;

import org.xml.sax.Attributes;

import org.gjt.sp.jedit.io.VFSManager;

import projectviewer.vpt.VFSFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	Handler for file nodes.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VFSFileNodeHandler extends NodeHandler {

	private static final String NODE_NAME = "vfsfile";
	private static final String PATH_ATTR = "path";
	private static final String NAME_ATTR = "name";

	//{{{ +getNodeName() : String
	/**
	 *	Returns the name of the nodes that should be delegated to this handler
	 *	when loading configuration data.
	 */
	public String getNodeName() {
		return NODE_NAME;
	} //}}}

	//{{{ +getNodeClass() : Class
	/**
	 *	Returns the class of the nodes that should be delegated to this handler
	 *	when saving node data to the config file.
	 */
	public Class getNodeClass() {
		return VFSFile.class;
	} //}}}

	//{{{ +isChild() : boolean
	/**
	 *	Returns whether the node is a child of nome other node or not.
	 */
	public boolean isChild() {
		return true;
	} //}}}

	//{{{ +hasChildren() : boolean
	/**
	 *	Returns whether the node(s) handled by this handler are expected to
	 *	have children or not.
	 */
	public boolean hasChildren() {
		return false;
	} //}}}

	//{{{ +createNode(Attributes, VPTProject) : VPTNode
	/** Loads a VFSFile from the cofiguration. */
	public VPTNode createNode(Attributes attrs, VPTProject project) {
		String path = attrs.getValue(PATH_ATTR);
		VFSFile vf = new VFSFile(path);
		if (attrs.getValue(NAME_ATTR) != null) {
			vf.setName(attrs.getValue(NAME_ATTR));
		}
		return vf;
	} //}}}

	//{{{ +saveNode(VPTNode, Writer) : void
	/** Saves a VFS file node to the config file. */
	public void saveNode(VPTNode node, Writer out) throws IOException {
		startElement(out);
		VFSFile file = (VFSFile) node;
		String name = VFSManager.getVFSForPath(file.getNodePath()).getFileName(file.getNodePath());
		if (!file.getName().equals(name)) {
			writeAttr(NAME_ATTR, file.getName(), out);
		}
		writeAttr(PATH_ATTR, translatePath(file.getNodePath()), out);
	} //}}}

}

