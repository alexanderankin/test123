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

import java.io.Writer;
import java.io.IOException;

import org.xml.sax.Attributes;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

/**
 *	Handler for property nodes.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class PropertyNodeHandler extends NodeHandler {
	
	private final static String NODE_NAME		= "property";
	private final static String PROP_NAME_ATTR	= "name";
	private final static String PROP_VALUE_ATTR	= "value";	
	
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
		return null;
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
		return false;
	}
	
	/**
	 *	Instantiates a VPTNode based on the information given in the attribute
	 *	list.
	 */
	public VPTNode createNode(Attributes attrs, VPTProject project) {
		project.setProperty(attrs.getValue(PROP_NAME_ATTR), attrs.getValue(PROP_VALUE_ATTR));
		return null;
	}
	
	/**	
	 *	Saving property nodes is going to be handled differently by the 
	 *	persistence manager...
	 */
	public void saveNode(VPTNode node, Writer out) throws IOException {
		
	}
	
	/**
	 *	This actually saves the property to the config file...
	 */
	public void saveNode(String name, String value, Writer out) throws IOException {
		startElement(out);
		writeAttr(PROP_NAME_ATTR, name, out);
		writeAttr(PROP_VALUE_ATTR, value, out);
		out.write(" />");
	}
	
}
