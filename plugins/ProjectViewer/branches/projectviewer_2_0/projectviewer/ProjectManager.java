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
package projectviewer;

//{{{ Imports
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collections;

import java.io.File;
import java.io.Writer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.gjt.sp.util.Log;

import projectviewer.vpt.VPTRoot;
import projectviewer.vpt.VPTProject;
import projectviewer.persist.OldConfigLoader;
import projectviewer.persist.ProjectPersistenceManager;
//}}}

/**
 *	This class takes care of the global project configuration, that is, the list
 *	of configured projects. This provides functionality to load the project
 *	list in the project viewer and to map project names to configuration file
 *	names.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public final class ProjectManager {
	
	//{{{ Static members & constants
	
	private final static String CONFIG_FILE = "pv.xml";
		
	private final static String PROJECT_ROOT	= "projects";
	private final static String PROJECT_ELEMENT	= "project";
	private final static String PRJ_NAME		= "name";
	private final static String PRJ_FILE		= "file";
	
	private final static ProjectManager manager = new ProjectManager();
	
	//{{{ getInstance() method
	/** Returns the project manager instance. */
	public static ProjectManager getInstance() {
		return manager;
	} //}}}
	
	//{{{ writeXMLHeader(String, Writer) method
	/**
	 *	Writes an XML header to the given writer. If encoding is not null,
	 *	it is written in the encoding field; else, UTF-8 is used.
	 */
	public static void writeXMLHeader(String encoding, Writer out) throws IOException {
		out.write("<?xml version=\"1.0\" encoding=\"" +
			((encoding != null) ? encoding : "UTF8") +
			"\" ?>\n\n");
	} //}}}
	
	//}}}
	
	//{{{ Constructor
	
	private ProjectManager() {
		projects = new TreeMap();
		fileNames = new HashMap();
		loaded = new HashMap();
	}
	
	//}}}
	
	//{{{ Instance variables
	
	private TreeMap projects;
	private HashMap fileNames;
	private HashMap loaded;
	
	//}}}
	
	//{{{ loadConfig() method
	/**	
	 *	Reads the list of projects from disk. If the old configuration style is
	 *	found, the OldConfigLoader is called to translate to the new object
	 *	style, the data is saved, the list is cleared (so that we don't have
	 *	useless data floating around) and the new config is finally loaded
	 *	from the disk.
	 *
	 *	@throws	IOException		Shouldn't happen, but who knows...
	 */
	public void loadConfig() throws IOException {
		InputStream cfg = ProjectPlugin.getResourceAsStream(CONFIG_FILE);
		if (cfg == null) {
			Log.log(Log.NOTICE, this, "Converting old ProjectViewer configuration...");
			// Load old config style data
			OldConfigLoader.load(this);
			
			// save data in new style
			save();
			
			// clear the list
			projects.clear();
			fileNames.clear();
			loaded.clear();
			
			// re-instantiate the InputStream
			cfg = ProjectPlugin.getResourceAsStream(CONFIG_FILE);
		}
		
		// OK, let's parse the config file
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			parser.parse(cfg, new PVSAXHandler());
		} catch (SAXException se) {
			Log.log(Log.ERROR, this, se);
		} catch (ParserConfigurationException pce) {
			Log.log(Log.ERROR, this, pce);
		}

		// Projects loaded, add all of them to the root node
		VPTRoot root = VPTRoot.getInstance();
		for (Iterator it = projects.keySet().iterator(); it.hasNext(); ) {
			root.add((VPTProject)projects.get(it.next()));
		}
	} //}}}
	
	//{{{ save() method 
	/** Saves all the project data to the disk (config + each project). */
	public void save() throws IOException {
		// save each project's data, if loaded
		// if not loaded, no need to save.
		for (Iterator it = projects.keySet().iterator(); it.hasNext(); ) {
			String pName = (String) it.next();
			if (loaded.get(pName) == Boolean.TRUE) {
				String fName = (String) fileNames.get(pName);
				if (fName == null) {
					fName = createFileName(pName);
					fileNames.put(pName, fName);
				}
				ProjectPersistenceManager.save((VPTProject)projects.get(pName), fName);
			}
		}
		
		saveProjectList();
		
	} //}}}
	
	//{{{ saveProject(VPTProject) method
	/**
	 *	Save the project's data to the config file. Before calling this method,
	 *	ensure that the project is in the internal list of projects (i.e., if
	 *	it is a new project, call {@link #addProject(VPTProject) addProject(VPTProject)}
	 *	before calling this method).
	 */
	public void saveProject(VPTProject p) {
		String fName = (String) fileNames.get(p.getName());
		if (fName == null) {
			fName = createFileName(p.getName());
			fileNames.put(p.getName(), fName);
			// since we're saving the project for the first time, let's be
			// paranoid and save all configuration along with it
			try{ 
				saveProjectList();
			} catch (IOException ioe) {
				Log.log(Log.ERROR, this, ioe);
			}
		}
		try {
			ProjectPersistenceManager.save(p, fName);
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
	} //}}}
	
	//{{{ removeProject(VPTProject) method
	/**
	 *	Removes the project from the internal list of projects. Removes the 
	 *	project's config file (if it exists), and notifies the Viewer that
	 *	the project does not exist anymore.
	 */
	public void removeProject(VPTProject p) {
		String fName = (String) fileNames.get(p.getName());
		if (fName != null) {
			new File(ProjectPlugin.getResourcePath("projects/" + fName)).delete();
			fileNames.remove(p.getName());
			// project list changed, save "global" data.
			try{
				saveProjectList();
			} catch (IOException ioe) {
				Log.log(Log.ERROR, this, ioe);
			}
		}
		VPTRoot.getInstance().remove(p);
		projects.remove(p.getName());
		loaded.remove(p.getName());
		ProjectViewer.projectRemoved(p);
	} //}}}
	
	//{{{ renameProject(String, String) method
	/** Updates information about a project to reflect its name change. */
	public void renameProject(String oldName, String newName) {
		VPTProject p = (VPTProject) projects.remove(oldName); 
		projects.put(newName, p);
		loaded.put(newName, projects.remove(oldName));
		if (fileNames.get(oldName) != null) {
			String oldFile = (String) fileNames.remove(oldName);
			new File(ProjectPlugin.getResourcePath("projects/" + oldFile)).delete();
		}
		saveProject(p);
		ProjectViewer.nodeChanged(p);
	} //}}}
	
	//{{{ addProject(VPTProject) method
	/** Adds a project to the list. */
	public void addProject(VPTProject p) {
		projects.put(p.getName(), p);
		loaded.put(p.getName(), Boolean.TRUE);
		ProjectViewer.updateProjectCombos();
	} //}}}
	
	//{{{ getProject(String) method
	/**
	 *	Returns the project with the given name. If the project is not yet
	 *	loaded, load its configuration from the disk. It it does not exist,
	 *	return null.
	 */
	public VPTProject getProject(String name) {
		VPTProject p = (VPTProject) projects.get(name);
		if (loaded.get(name) == Boolean.FALSE) {
			String fName = (String) fileNames.get(name);
			if (fName != null) {
				try {
					ProjectPersistenceManager.load(p, fName);
					loaded.put(name, Boolean.TRUE);
				} catch (IOException ioe) {
					Log.log(Log.ERROR, this, ioe);
				}
			} else {
				Log.log(Log.DEBUG,this,"Shouldn't reach this statement!");
			}
		}
		return p;
	} //}}}

	//{{{ getProjects() method
	/**
	 *	Returns an iterator that points to the (ordered) list of project names
	 *	managed by this manager. The Iterator is read-only.
	 */
	public Iterator getProjects() {
		return Collections.unmodifiableCollection(projects.values()).iterator();
	} //}}}
	
	//{{{ isLoaded(String) method
	/**	
	 *	Returns whether a project is loaded or not.
	 *
	 *	@param	pName	The project's name.
	 *	@return	If the project was loaded from disk.
	 */
	public boolean isLoaded(String pName) {
		return (loaded.get(pName) == Boolean.TRUE);
	} //}}}
	
	//{{{ hasProject(String) method
	/** Returns whether a project with the given name exists. */
	public boolean hasProject(String name) {
		return projects.containsKey(name);
	} //}}}
	
	//{{{ Private Stuff
	
	//{{{ createFileName(String) method
	/**
	 *	Crates an unique file name where to save a project's configuration
	 *	based on the project's name.
	 */
	private String createFileName(String projName) {
		String illegalChars = " /:\\\"'";
		String substitutes  = "_---__";
		
		StringBuffer fName = new StringBuffer(projName);
		for (int i = 0; i < fName.length(); i++) {
			int idx = illegalChars.indexOf(fName.charAt(i)); 
			if (idx != -1) {
				fName.setCharAt(i, substitutes.charAt(idx));
			}
		}
		
		File f = new File(ProjectPlugin.getResourcePath("projects" + File.separator + fName.toString() + ".xml"));
		int cntr = 0;
		while (f.exists()) {
			cntr++;
			f = new File(ProjectPlugin.getResourcePath("projects" + File.separator + fName.toString() + "_" + cntr + ".xml"));
		}
		
		return f.getName();
	} //}}}
	
	//{{{ saveProjectList() method
	/**
	 *	Saves the "global" data for the projects: the list of projects and
	 *	the file names where each project data is stored.
	 */
	private void saveProjectList() throws IOException {
		// save the global configuration
		OutputStream outs = ProjectPlugin.getResourceAsOutputStream(CONFIG_FILE);
		OutputStreamWriter out = new OutputStreamWriter(outs, "UTF-8");
		writeXMLHeader("UTF-8", out);
		out.write("<" + PROJECT_ROOT + ">\n");
		for (Iterator it = fileNames.keySet().iterator(); it.hasNext(); ) {
			String pName = (String) it.next();
			String fName = (String) fileNames.get(pName);
			out.write("<" + PROJECT_ELEMENT + " " + 
				PRJ_NAME + "=\"" + pName + "\" " + 
				PRJ_FILE + "=\"" + fName + "\"/>\n");
		}
		out.write("</" + PROJECT_ROOT + ">\n");
		out.flush();
		out.close();
	} //}}}
	
	//{{{ PVSAXHandler class
	/**	SAX handler that takes care of reading the configuration file. */
	private class PVSAXHandler extends DefaultHandler {
		
		//{{{ startElement() method
		/** Reads "project" elements and adds them to the list. */
		public void startElement(String uri, String localName, String qName,
									Attributes attributes) throws SAXException {
			if (qName.equals(PROJECT_ELEMENT)) {
				String pName = attributes.getValue(PRJ_NAME);
				fileNames.put(pName, attributes.getValue(PRJ_FILE));
				projects.put(pName, new VPTProject(pName));
				loaded.put(pName, Boolean.FALSE);
			} else if (!qName.equals(PROJECT_ROOT)) {
				Log.log(Log.WARNING, this, "Unknown node in config file: " + qName);
			}
		} //}}}
		
	} //}}}

	//}}}

}
