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
import java.io.BufferedWriter;
import java.io.File;
import java.io.Writer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.util.Log;

import common.io.AtomicOutputStream;

import projectviewer.ProjectPlugin;
import projectviewer.ProjectManager;

import projectviewer.PVActions;
import projectviewer.vpt.VPTNode;
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
			XMLReader parser = PVActions.newXMLReader(new ProjectHandler(p));
			parser.parse(new InputSource(new InputStreamReader(in, "UTF-8")));
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
		AtomicOutputStream aout = null;
		try {
			aout = new AtomicOutputStream(ProjectPlugin.getResourcePath(CONFIG_DIR + filename));
			Writer out = new BufferedWriter(new OutputStreamWriter(aout, "UTF-8"));
			ProjectManager.writeXMLHeader("UTF-8", out);

			saveNode(p, out);

			out.flush();
			out.close();
		} finally {
			if (aout != null) {
				aout.rollback();
			}
		}
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
			if (handler != null) {
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
			} else {
				Log.log(Log.WARNING, ProjectPersistenceManager.class,
					"No handler found to save node of type: " + node.getClass().getName());
			}
		}
	} //}}}

	//{{{ +class _ProjectHandler_
	/** Handler to read project configuration files. */
	public static final class ProjectHandler extends DefaultHandler {

		//{{{ Instance variables
		private VPTProject proj;
		private VPTNode currNode;

		private Stack openNodes;
		//}}}

		//{{{ +ProjectHandler(VPTProject) : <init>
		public ProjectHandler(VPTProject proj) {
			this.proj = proj;
			this.currNode = proj;
			this.openNodes = new Stack();
		} //}}}

		//{{{ +startElement(String) : void
		/** takes care of identifying nodes read from the file. */
		public void startElement(String uri, String localName,
								 String qName, Attributes attrs)
		{
			if (qName.equals(ProjectNodeHandler.NODE_NAME)) {
				projHandler.createNode(attrs, proj);
			} else {
				NodeHandler nh = (NodeHandler) handlerNames.get(qName);
				if (nh == null) {
					Log.log(Log.WARNING,this, "Unknown node: " + qName);
				} else {
					try {
						VPTNode node = nh.createNode(attrs, proj);
						if (node != null) {
							if (nh.isChild()) {
								currNode.add(node);
							}
							if (nh.hasChildren()) {
								currNode = node;
								openNodes.push(qName);
							}
						}
					} catch (Exception e) {
						Log.log(Log.WARNING, this, "Error loading project node, error follows.");
						Log.log(Log.ERROR, this, e);
					} catch (NoClassDefFoundError ncde) {
						Log.log(Log.WARNING, this, "Error loading project node, error follows.");
						Log.log(Log.ERROR, this, ncde);
					} catch (ExceptionInInitializerError eiie) {
						Log.log(Log.WARNING, this, "Error loading project node, error follows.");
						Log.log(Log.ERROR, this, eiie);
					}
				}
			}
		} //}}}

		//{{{ +endElement(String) : void
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

