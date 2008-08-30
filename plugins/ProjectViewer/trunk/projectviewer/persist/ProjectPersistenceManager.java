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
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.SwingUtilities;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.util.Log;

import common.io.AtomicOutputStream;
import common.threads.WorkerThreadPool;
import common.threads.WorkRequest;

import projectviewer.ProjectPlugin;
import projectviewer.ProjectManager;

import projectviewer.PVActions;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.config.ExtensionManager;
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

	private final static String CONFIG_DIR		= "projects" + File.separator;

	/** Private constructor. No instances! */
	private ProjectPersistenceManager() { }

	/** The map of handlers based on nome names. */
	private static final Map<String,NodeHandler> handlerNames;

	/** the map of handlers based on classes. */
	private static final HashMap<Class,NodeHandler> handlerClasses;

	/** the node handler for projects (cannot be changed). */
	private static final NodeHandler projHandler = new ProjectNodeHandler();

	/** static initializer, registers the default handlers. */
	static {
		handlerNames = new HashMap<String,NodeHandler>();
		handlerClasses = new HashMap<Class,NodeHandler>();
		ExtensionManager.getInstance().register(new MService());
	}

	//}}}

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
		return p;
	} //}}}

	/**
	 * Creates a runnable task that will save the project's data to the
	 * given config file.
	 *
	 * @param p The project to save.
	 * @param filename The project config file name.
	 *
	 * @return A runnable that can be used to execute the task.
	 *
	 * @since PV 3.0.0
	 */
	public static Runnable createSaveTask(VPTProject p, String filename) {
		return new IORequest(p, filename);
	}

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
			NodeHandler handler = handlerClasses.get(node.getClass());
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

		private Stack<String> openNodes;
		//}}}

		//{{{ +ProjectHandler(VPTProject) : <init>
		public ProjectHandler(VPTProject proj) {
			this.proj = proj;
			this.currNode = proj;
			this.openNodes = new Stack<String>();
		} //}}}

		//{{{ +startElement(String) : void
		/** takes care of identifying nodes read from the file. */
		public void startElement(String uri, String localName,
								 String qName, Attributes attrs)
		{
			if (qName.equals(ProjectNodeHandler.NODE_NAME)) {
				projHandler.createNode(attrs, proj);
			} else {
				NodeHandler nh = handlerNames.get(qName);
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

	//{{{ IORequest class
	private static class IORequest implements Runnable {

		private VPTProject 	p;
		private String 		fname;
		private boolean 	notify;
		private Exception	error;

		public IORequest(VPTProject p, String fname) {
			this.p 		= p;
			this.fname 	= fname;
			this.notify = false;
		}

		public void run() {
			if (!notify) {
				doSave();
			} else {
				doNotify();
			}
		}

		private void doSave() {
			AtomicOutputStream aout = null;
			try {
				synchronized (p) {
					aout = new AtomicOutputStream(ProjectPlugin.getResourcePath(CONFIG_DIR + fname));
					Writer out = new BufferedWriter(new OutputStreamWriter(aout, "UTF-8"));
					ProjectManager.writeXMLHeader("UTF-8", out);

					saveNode(p, out);

					out.flush();
					out.close();
				}
			} catch (IOException ioe) {
				Log.log(Log.ERROR, p, ioe);
				notify = true;
				error = ioe;
				SwingUtilities.invokeLater(this);
			} finally {
				if (aout != null) {
					aout.rollback();
				}
			}
		}

		private void doNotify() {
			String msg = jEdit.getProperty("projectviewer.error.project_str")
							+ " '" +  p.getName() + "'";
			GUIUtilities.error(jEdit.getActiveView(), "projectviewer.error.save",
								new Object[] { msg, error.getMessage() });
		}

	} //}}}

	private static class MService implements ExtensionManager.ManagedService
	{
		public Class getServiceClass()
		{
			return NodeHandler.class;
		}

		public void updateExtensions(List<Object> l)
		{
			handlerClasses.clear();
			handlerNames.clear();
			registerHandler(new FileNodeHandler());
			registerHandler(new DirectoryNodeHandler());
			registerHandler(new PropertyNodeHandler());
			registerHandler(new OpenFileNodeHandler());

			/*
			 * To maintain compatibility with config files that have
			 * old VFSFile entries, we add this to the name map, but
			 * not to the class map, since they'll be translated to
			 * plain file entries when saving the project data.
			 */
			handlerNames.put("vfsfile", handlerNames.get("file"));

			if (l != null && l.size() > 0) {
				for (Object o : l) {
					registerHandler((NodeHandler)o);
				}
			}
		}
	}
}

