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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.gjt.sp.util.Log;

import projectviewer.PVActions;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
//}}}

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
	private final static String PROP_DATA_ATTR	= "data";

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
	public VPTNode createNode(Map attrs, VPTProject project) {
		String name = (String) attrs.get(PROP_NAME_ATTR);
		if (attrs.containsKey(PROP_VALUE_ATTR)) {
			project.setProperty(name, (String)attrs.get(PROP_VALUE_ATTR));
		} else {
			String data = (String) attrs.get(PROP_DATA_ATTR);
			if (data != null) {
				try {
					byte[] bytes = PVActions.decodeBase64(data);
					ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(bytes) );
					project.setProperty(name, ois.readObject());
				} catch (Exception e) {
					Log.log(Log.ERROR, this, "Error loading property of name " + name +
						" from project " + project.getName());
					Log.log(Log.ERROR, this, e);
				}
			}
		}
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
	public void saveNode(String name, Object value, Writer out) throws IOException {
		startElement(out);
		writeAttr(PROP_NAME_ATTR, name, out);

		if (value instanceof String) {
			writeAttr(PROP_VALUE_ATTR, (String) value, out);
		} else {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(bytes);
				oos.writeObject(value);
				oos.flush();
				writeAttr(PROP_DATA_ATTR, new String(PVActions.encodeBase64(bytes.toByteArray())), out);
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Error writing object to project file.");
				Log.log(Log.ERROR, this, e);
			}
		}
		out.write(" />\n");
	}

}

