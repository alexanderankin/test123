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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.Stack;
import java.util.HashMap;
import java.util.Enumeration;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.gjt.sp.util.Log;

import projectviewer.ProjectPlugin;
import projectviewer.ProjectManager;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	This class takes care of each projects properties. Each project has its
 *	own config file, that is loaded when the project is activated.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public final class ProjectPersistenceManager {

	//{{{ Constants

	private final static String CONFIG_DIR		= "projects" + File.separator;

	//}}}

	/** Private constructor. No instances! */
	private ProjectPersistenceManager() { }

	/** The map of handlers based on nome names. */
	private static final HashMap handlerNames = new HashMap();

	/** the map of handlers based on classes. */
	private static final HashMap handlerClasses = new HashMap();

	/** the node handler for projects (cannot be changed). */
	private static final NodeHandler projHandler = new ProjectNodeHandler();

	/** static initializer, registers the default handlers. */
	static {
		registerHandler(new FileNodeHandler());
		registerHandler(new DirectoryNodeHandler());
		registerHandler(new PropertyNodeHandler());
		registerHandler(new OpenFileNodeHandler());
	}

	//{{{ registerHandler(NodeHandler) method
	/**
	 *	Registers a node handler. The same instance will be used at all times
	 *	to process the data, so make sure this is not a problem with the
	 *	handler's implementation.
	 */
	public static void registerHandler(NodeHandler nh) {
		handlerNames.put(nh.getNodeName(), nh);
		handlerClasses.put(nh.getNodeClass(), nh);
	} //}}}

	//{{{ load(String, String) method
	/** Loads a project from the given file name. */
	public static VPTProject load(VPTProject p, String file) throws IOException {
		InputStream in = ProjectPlugin.getResourceAsStream(CONFIG_DIR + file);
		if (in == null) {
			Log.log(Log.WARNING, ProjectPersistenceManager.class.getName(), "Cannot read config file " + file);
			return null;
		}

		// OK, let's parse the config file
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			parser.parse(in, new ProjectHandler(p));
		} catch (SAXException se) {
			Log.log(Log.ERROR,  ProjectPersistenceManager.class.getName(), se);
		} catch (ParserConfigurationException pce) {
			Log.log(Log.ERROR,  ProjectPersistenceManager.class.getName(), pce);
		}

		return p;
	} //}}}

	//{{{ save(VPTProject, String) method
	/** Saves the given project data to the disk. */
	public static void save(VPTProject p, String filename) throws IOException {
		OutputStream outs = ProjectPlugin.getResourceAsOutputStream(CONFIG_DIR + filename);
		OutputStreamWriter out = new OutputStreamWriter(outs, "UTF-8");
		ProjectManager.writeXMLHeader("UTF-8", out);

		saveNode(p, out);

		out.flush();
		out.close();
	} //}}}

	//{{{ saveNode(VPTNode, Writer) method
	/** recursive method for saving nodes and their children. */
	private static void saveNode(VPTNode node, Writer out) throws IOException {
		if (node.isProject()) {
			projHandler.saveNode(node, out);

			for (Enumeration e = node.children(); e.hasMoreElements(); ) {
				saveNode((VPTNode)e.nextElement(), out);
			}

			out.write("</" + projHandler.getNodeName() + ">\n");
		} else {
			NodeHandler handler = (NodeHandler) handlerClasses.get(node.getClass());
			handler.saveNode(node, out);
			if (node.getAllowsChildren()) {
				out.write(">\n");
				for (Enumeration e = node.children(); e.hasMoreElements(); ) {
					saveNode((VPTNode)e.nextElement(), out);
				}
				out.write("</" + handler.getNodeName() + ">\n");
			} else {
				out.write(" />\n");
			}
		}
	} //}}}

	//{{{ ProjectHandler class
	/**
	 *	SAX Handler to read project configuration files.
	 */
	public static final class ProjectHandler extends DefaultHandler {

		//{{{ Instance variables
		private VPTProject proj;
		private VPTNode currNode;

		private Stack openNodes;
		//}}}

		//{{{ Constructor
		public ProjectHandler(VPTProject proj) {
			this.proj = proj;
			this.currNode = proj;
			openNodes = new Stack();
		} //}}}

		//{{{ startElement() method
		/** takes care of identifying nodes read from the file. */
		public void startElement(String uri, String localName, String qName,
									Attributes attributes) throws SAXException {
			if (qName.equals(ProjectNodeHandler.NODE_NAME)) {
				projHandler.createNode(attributes, proj);
			} else {
				NodeHandler nh = (NodeHandler) handlerNames.get(qName);
				if (nh == null) {
					Log.log(Log.WARNING,this, "Unknown node: " + qName);
				} else {
					VPTNode node = nh.createNode(attributes, proj);
					if (node != null) {
						if (nh.isChild()) {
							currNode.add(node);
						}
						if (nh.hasChildren()) {
							currNode = node;
							openNodes.push(qName);
						}
					}
				}
			}
		} //}}}

		//{{{ endElement(String,String,String) method
		/** Handles the closing of a directory element. */
		public void endElement(String uri, String localName, String qName) {
			if (!openNodes.isEmpty() && qName.equals(openNodes.peek())) {
				currNode.sortChildren();
				currNode = (VPTNode) currNode.getParent();
				openNodes.pop();
			}
		} //}}}

	} //}}}

}

