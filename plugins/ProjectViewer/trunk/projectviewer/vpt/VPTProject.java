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
import java.io.IOException;

import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Collection;
import java.util.Collections;

import javax.swing.Icon;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.event.ProjectEvent;
import projectviewer.event.ProjectListener;
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

	private final static Icon projectIcon 	= GUIUtilities.loadIcon("DriveSmall.png");

	//}}}

	//{{{ Attributes

	private ArrayList	openFiles;
	private ArrayList	listeners;
	private String		rootPath;
	private String		url;
	private File		buildFile;
	private Properties	properties;

	protected HashMap		files;
	protected HashMap		canonicalFiles;

	//}}}

	//{{{ Constructors

	public VPTProject(String name) {
		super(VPTNode.PROJECT, name);
		files 			= new HashMap();
		canonicalFiles	= new HashMap();
		openFiles		= new ArrayList();
		properties		= new Properties();
	}

	//}}}

	//{{{ Public methods

	//{{{ getFile() method
	/**
	 *	Returns a VPTFile included in this project that references the given
	 *	path.
	 *
	 *	<p>If in the file list returns null, returns a file from the list
	 *	where we use canonical paths to do the mapping.</p>
	 */
	public VPTFile getFile(String path) {
		Object o = files.get(path);
		if (o == null) {
			o = canonicalFiles.get(path);
		}
		return (VPTFile) o;
	} //}}}

	//{{{ getFiles() method
	/**
	 *	Returns a read-only collection of the files contained in this
	 *	project.
	 */
	public Collection getFiles() {
		return Collections.unmodifiableCollection(files.values());
	}
	//}}}

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
		if (url != null && !url.endsWith("/")) url += "/";
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

	//{{{ removeProperty(String) method
	/** Removes the given property from the project. */
	public Object removeProperty(String property) {
		return properties.remove(property);
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
	 *	firing project events. Also, if the canonical path of the file differs
	 *	from the absolute path, register it in the internal canonical paths
	 *	list.
	 */
	public void registerFile(VPTFile file) {
		files.put(file.getFile().getAbsolutePath(), file);
		try {
			String cPath = file.getFile().getCanonicalPath();
			if (!cPath.equals(file.getFile().getAbsolutePath())) {
				registerCanonicalPath(cPath, file);
			}
		} catch (IOException ioe) {
			Log.log(Log.WARNING, this, ioe);
		}
	}
	//}}}

	//{{{ registerFilePath(VPTFile) method
	/**
	 *	Register a file in the project, adding it to the list of files that
	 *	belong to the project. This is mainly for performance reasons when
	 *	firing project events.
	 */
	public void registerFilePath(VPTFile file) {
		files.put(file.getFile().getAbsolutePath(), file);
	}
	//}}}

	//{{{ registerCanonicalPath(String, VPTFile) method
	/**
	 *	Register a file whose canonical path differs from the path returned
	 *	by File.getAbsolutePath().
	 *
	 *	@param	path	Canonical path of the file.
	 */
	public void registerCanonicalPath(String path, VPTFile file) {
		canonicalFiles.put(path, file);
	} //}}}

	//{{{ removeAllChildren()
	/** Removes all children from the project, and unregisters all files. */
	public void removeAllChildren() {
		files.clear();
		canonicalFiles.clear();
		super.removeAllChildren();
	} //}}}

	//{{{ unregisterFile(VPTFile) method
	/** Unegister a file from the project. */
	public void unregisterFile(VPTFile file) {
		files.remove(file.getFile().getAbsolutePath());
		canonicalFiles.remove(file.getCanonicalPath());
	}
	//}}}

	//{{{ getNodePath()
	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return getRootPath();
	} //}}}

	//{{{ Listener Subscription and Event Dispatching
	
	//{{{ addProjectListener(ProjectListener) method
	/**
	 *	Adds a new listener to the list. The list if listeners is global to
	 *	all the projects, so listeners don't need to be registered to each
	 *	individual project.
	 */
	public void addProjectListener(ProjectListener lstnr) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(lstnr);
	} //}}}

	//{{{ removeProjectListener(ProjectListener) method
	/** Removes a listener from the list. */
	public void removeProjectListener(ProjectListener lstnr) {
		if (listeners != null) {
			listeners.remove(lstnr);
		}
	} //}}}

	//{{{ hasListeners() method
	/**
	 *	Returns whether there are any listeners registered. Mainly for use to
	 *	enhance performance by classes that would fire these events.
	 */
	public boolean hasListeners() {
		return (listeners != null && listeners.size() > 0);
	} //}}}
	
	//{{{ fireFilesChanged(ArrayList, ArrayList) method
	/**
	 *	Notifies the listeners that a group of files has been added to and/or
	 *	removed from the project.
	 */
	public void fireFilesChanged(ArrayList added, ArrayList removed) {
		if (listeners.size() > 0) {
			ProjectEvent pe = new ProjectEvent(this, added, removed);
			for (Iterator i = listeners.iterator(); i.hasNext(); ) {
				if (added != null && added.size() > 0) {
					((ProjectListener)i.next()).filesAdded(pe);
				}
				if (removed != null && removed.size() > 0) {
					((ProjectListener)i.next()).filesRemoved(pe);
				}
			}
		}
	} //}}}

	//{{{ fireFileAdded(VPTFile) method
	/**
	 *	Notifies the listeners that a single file has been added to the
	 *	project.
	 */
	public void fireFileAdded(VPTFile file) {
		if (hasListeners()) {
			ProjectEvent pe = new ProjectEvent(this, file, true);
			for (Iterator i = listeners.iterator(); i.hasNext(); ) {
				((ProjectListener)i.next()).fileAdded(pe);
			}
		}
	} //}}}

	//{{{ fireFileRemoved(VPTFile) method
	/**
	 *	Notifies the listeners that a single file has been added to the
	 *	project.
	 */
	public void fireFileRemoved(VPTFile file) {
		if (hasListeners()) {
			ProjectEvent pe = new ProjectEvent(this, file, false);
			for (Iterator i = listeners.iterator(); i.hasNext(); ) {
				((ProjectListener)i.next()).fileRemoved(pe);
			}
		}
	} //}}}
	
	//}}}
	
	//}}}

}

