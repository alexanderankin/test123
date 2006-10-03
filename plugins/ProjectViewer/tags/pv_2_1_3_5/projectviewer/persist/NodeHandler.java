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

import java.io.Writer;
import java.io.IOException;

import org.xml.sax.Attributes;

import projectviewer.PVActions;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	A node handler is a class that takes care of loading an saving nodes to/from
 *	the configuration file.
 *
 *	<h3>Loading</h3>
 *
 *	When the parser finds a node that macthes the node name published by the
 *	handler, it calls the callback method with the attributes read from the
 *	file. The handler then should instantiate a node, fill any data and return
 *	the object.
 *
 *	<h3>Saving</h3>
 *
 *	The handler also publishes the class of the node (a java.lang.Class object)
 *	to register itself as the handler to save nodes of that type to the config
 *	file. When traversing the node tree, each time a node of that class is
 *	found, the appropriate handler callback method will be called.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public abstract class NodeHandler {

	/**
	 *	Returns the name of the tag representing nodes handled by this handler
	 *	when loading the project's XML file.
	 */
	public abstract String getNodeName();

	/**
	 *	Returns the class of the nodes that should be delegated to this handler
	 *	when saving node data to the config file.
	 */
	public abstract Class getNodeClass();

	/**
	 *	Returns whether the node is a child of some other node or not. For
	 *	example, property nodes are not children of any other nodes, they
	 *	simply add a property to a project.
	 *
	 *	<p>This controls whether the node will be added to the parent node
	 *	read from the XML file after instantiation.</p>
	 */
	public abstract boolean isChild();

	/**
	 *	Returns whether the node(s) handled by this handler are expected to
	 *	have children or not.
	 */
	public abstract boolean hasChildren();

	/**
	 *	Instantiates a VPTNode based on the information given in the attribute
	 *	list.
	 *
	 *	@param	attrs	The attributes read from the config file.
	 *	@param	project	The project that holds this node.
	 *	@since	PV 2.1.3.4 (older version have a different method signature)
	 */
	public abstract VPTNode createNode(Attributes attrs, VPTProject project);

	/**
	 *	Saves a node to the given Writer. The node is guaranteed to be an
	 *	instance of the class returned by the {@link #getNodeClass() getNodeClass()}
	 *	method.
	 *
	 *	<p>This method should create the tag, but it shouldn't close it, even
	 *	if it's an empty tag. Closing tags is done by the PersistenceManager
	 *	based on the value returned by {@link #hasChildren() hasChildren()}.
	 *	The config file format does not support text elements inside other
	 *	elements, so don't use them when saving nodes or you won't be able
	 *	to read them later.</p>
	 */
	public abstract void saveNode(VPTNode node, Writer out) throws IOException;

	/**	Writes the start of an element to the given writer. */
	protected void startElement(Writer out) throws IOException {
		startElement(getNodeName(), out);
	}

	/**
 	 *	Writes the start of an element to the given writer, using the given
	 *	string as the node name. This writer the opening &lt; symbol and the
	 *	node name, but does not writed the closing &gt;, since attributed
	 *	may still need to be written.
	 */
	protected void startElement(String name, Writer out) throws IOException {
		out.write("<");
		out.write(name);
	}

	/** Writes an attribute to the writer. */
	protected void writeAttr(String name, String value, Writer out) throws IOException {
		out.write(" ");
		out.write(name);
		out.write("=\"");
		PVActions.writeXML(value, out);
		out.write("\"");
	}

	/**
	 *	Translates any "\" found in the string to "/". Paths saved to the XML
	 *	file are generally expected to be in Unix format, so this method is
	 *	useful when saving the project in a Windows system.
	 */
	protected final String translatePath(String src) {
		if (src == null) return "";
		return src.replace('\\', '/');
	}

}

