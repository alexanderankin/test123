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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.microstar.xml.XmlParser;
import com.microstar.xml.HandlerBase;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.util.Log;

import projectviewer.ProjectPlugin;
import projectviewer.ProjectManager;

import projectviewer.PVActions;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
import projectviewer.config.ProjectViewerConfig;
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

	//{{{ -ProjectPersistenceManager() : <init>

	/** Private constructor. No instances! */
	private ProjectPersistenceManager() { } //}}}

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
		registerHandler(new VFSFileNodeHandler());
	}

	//}}}

	//{{{ +_loadNodeHandlers(PluginJAR)_ : void
	/**
	 *	Checks the plugin's properties to see if it declares any node
	 *	handlers, and register those node handlers within this class.
	 */
	public static void loadNodeHandlers(PluginJAR jar) {
		if (jar.getPlugin() == null) return;
		String list = jEdit.getProperty("plugin.projectviewer." +
						jar.getPlugin().getClassName() + ".node-handlers");
		Collection aList = PVActions.listToObjectCollection(list, jar, NodeHandler.class);
		if (aList != null && aList.size() > 0) {
			for (Iterator i = aList.iterator(); i.hasNext(); ) {
				NodeHandler nh = (NodeHandler) i.next();
				registerHandler(nh);
			}
		}
	} //}}}

	//{{{ +_registerHandler(NodeHandler)_ : void
	/**
	 *	Registers a node handler. The same instance will be used at all times
	 *	to process the data, so make sure this is not a problem with the
	 *	handler's implementation.
	 */
	public static void registerHandler(NodeHandler nh) {
		handlerNames.put(nh.getNodeName(), nh);
		handlerClasses.put(nh.getNodeClass(), nh);
	} //}}}

	//{{{ +_load(VPTProject, String)_ : VPTProject
	/** Loads a project from the given file name. */
	public static VPTProject load(VPTProject p, String file) {
		InputStream in = ProjectPlugin.getResourceAsStream(CONFIG_DIR + file);
		if (in == null) {
			Log.log(Log.WARNING, ProjectPersistenceManager.class.getName(), "Cannot read config file " + file);
			return null;
		}

		// OK, let's parse the config file
		try {
			XmlParser parser = new XmlParser();
			parser.setHandler(new ProjectHandler(p));
			parser.parse(null, null, new InputStreamReader(in));
		} catch (Exception e) {
			Log.log(Log.ERROR,  ProjectPersistenceManager.class.getName(), e);
			return null;
		}

		p.sortChildren();
		ProjectViewerConfig config = ProjectViewerConfig.getInstance();
		for (Iterator i = p.getOpenableNodes().iterator(); i.hasNext(); ) {
			VPTNode n = (VPTNode) i.next();
			String path = n.getNodePath();
		}
		return p;
	} //}}}

	//{{{ +_save(VPTProject, String)_ : void
	/** Saves the given project data to the disk. */
	public static void save(VPTProject p, String filename) throws IOException {
		OutputStream outs = ProjectPlugin.getResourceAsOutputStream(CONFIG_DIR + filename);
		OutputStreamWriter out = new OutputStreamWriter(outs, "UTF-8");
		ProjectManager.writeXMLHeader("UTF-8", out);

		saveNode(p, out);

		out.flush();
		out.close();
	} //}}}

	//{{{ -_saveNode(VPTNode, Writer)_ : void
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
			if (node.getAllowsChildren() && node.persistChildren()) {
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

	//{{{ +class _ProjectHandler_
	/** Handler to read project configuration files. */
	public static final class ProjectHandler extends HandlerBase {

		//{{{ Instance variables
		private HashMap attrs;
		private VPTProject proj;
		private VPTNode currNode;

		private Stack openNodes;
		//}}}

		//{{{ +ProjectHandler(VPTProject) : <init>
		public ProjectHandler(VPTProject proj) {
			this.proj = proj;
			this.currNode = proj;
			this.attrs = new HashMap();
			this.openNodes = new Stack();
		} //}}}

		//{{{ +attribute(String, String, boolean) : void
		public void attribute(String name, String value, boolean spec) {
			attrs.put(name, value);
		} //}}}

		//{{{ +startElement(String) : void
		/** takes care of identifying nodes read from the file. */
		public void startElement(String qName) {
			if (qName.equals(ProjectNodeHandler.NODE_NAME)) {
				projHandler.createNode(attrs, proj);
				attrs.clear();
			} else {
				NodeHandler nh = (NodeHandler) handlerNames.get(qName);
				if (nh == null) {
					Log.log(Log.WARNING,this, "Unknown node: " + qName);
				} else {
					VPTNode node = nh.createNode(attrs, proj);
					attrs.clear();
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

		//{{{ +endElement(String) : void
		/** Handles the closing of a directory element. */
		public void endElement(String qName) {
			if (!openNodes.isEmpty() && qName.equals(openNodes.peek())) {
				currNode.sortChildren();
				currNode = (VPTNode) currNode.getParent();
				openNodes.pop();
			}
		} //}}}

	} //}}}

}

