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
package projectviewer.vpt;

//{{{ Imports
import java.io.File;

import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.Icon;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.GUIUtilities;
//}}}

/**
 *	Models a project. A project is a container for files and directories. It also
 *	provides interfaces for event notification and custom properties, so that
 *	other plugins can interact with projects.
 *
 *	<p>Note: this class is not thread safe!</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTProject extends VPTNode {

	//{{{ Constants

	public final static String DEFAULT_URL = "http://";
	
	private final static Icon projectIcon 	= GUIUtilities.loadIcon("DriveSmall.png");

	//}}}
	
	//{{{ Attributes
	
	private ArrayList	listeners;
	private ArrayList	openFiles;
	private String		rootPath;
	private String		url;
	private File		buildFile;
	private Properties	properties;
	
	protected HashMap		files;
	
	//}}}
	
	//{{{ Constructors 
	
	public VPTProject(String name) {
		super(VPTNode.PROJECT, name);
		files 		= new HashMap();
		listeners	= new ArrayList();
		openFiles	= new ArrayList();
		properties	= new Properties();
	}
	
	//}}}
	
	//{{{ Public methods
	
	//{{{ getFile() method 
	/**
	 *	Returns a VPTFile included in this project that references the given
	 *	path.
	 */
	public VPTFile getFile(String path) {
		return (VPTFile) files.get(path);
	} //}}}

	//{{{ getBuildFile() method
	/** Returns the project's build file for Ant. */
	public File getBuildFile() {
		return buildFile;
	} //}}}
	
	//{{{ getURL() method
	/** Returns the project's URL. */
	public String getURL() {
		return url;
	} //}}}
	
	//{{{ setURL(String) method
	/** Sets the project's URL. */
	public void setURL(String url) {
		this.url = url;
	} //}}}
	
	//{{{ getProperty(String) method
	/** Returns the property set for the project. */
	public String getProperty(String property) {
		return properties.getProperty(property);
	} //}}}
	
	//{{{ setProperty(String, String) method
	/**
	 *	Sets a property.
	 *
	 *	@return	The old value for the property (can be null).
	 */
	public String setProperty(String name, String value) {
		String old = properties.getProperty(name);
		properties.setProperty(name, value);
		return old;
	} //}}}
	
	//{{{ getPropertyNames() method
	/**	Returns a set containing all property names for this project. */
	public Set getPropertyNames() {
		return properties.keySet();
	} //}}}
	
	//{{{ getProperties() method.
	/** Return the project's property set. */
	public Properties getProperties() {
		return properties;
	} //}}}
	
	//{{{ getOpenFiles() method
	/**
	 *	Returns an iterator to the list of open files that this project
	 *	remembers.
	 */
	public Iterator getOpenFiles() {
		return openFiles.iterator();
	} //}}}
	
	//{{{ addOpenFile(String) method
	/**
	 *	Adds a file to the list of the project's opened files.
	 */
	public void addOpenFile(String path) {
		openFiles.add(path);
	} //}}}
	
	//{{{ clearOpenFiles() method
	/** Clears the list of open files. */
	public void clearOpenFiles() {
		openFiles.clear();
	} //}}}
	
	//{{{ isProjectFile(String) method
	/**
	 *	Returns whether the file denoted by the given path is part of this
	 *	project.
	 */
	public boolean isProjectFile(String path) {
		return files.containsKey(path);
	} //}}}
	
	//{{{ getIcon(boolean) method
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		return projectIcon;
	} //}}}

	//{{{ toString() method
	/** Returns a string representation of the current node. */
	public String toString() {
		return "Project [" + getName() + "]";
	} //}}}
	
	//{{{ getRootPath() method
	/** Returns the path to the root of the project. */
	public String getRootPath() {
		return rootPath;
	} //}}}
	
	//{{{ setRootPath(String) method
	/** Sets the path to the root of the project. */
	public void setRootPath(String path) {
		rootPath = path;
	} //}}}
	
	//{{{ registerFile(VPTFile) method
	/**
	 *	Register a file in the project, adding it to the list of files that
	 *	belong to the project. This is mainly for performance reasons when
	 *	firing project events.
	 */
	public void registerFile(VPTFile file) {
		files.put(file.getFile().getAbsolutePath(), file);
	}
	//}}}
	
	//{{{ getNodePath()
	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return getRootPath();
	} //}}}

	//}}}

}
