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
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Collections;

import javax.swing.Icon;

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
	private HashSet		listeners;
	private String		rootPath;
	private String		url;
	private Properties	properties;

	protected HashMap		openableNodes;

	//}}}

	//{{{ +VPTProject(String) : <init>

	public VPTProject(String name) {
		super(name);
		openableNodes	= new HashMap();
		openFiles		= new ArrayList();
		properties		= new Properties();
	}

	//}}}

	//{{{ +getFile(String) : VPTFile
	/**
	 *	Returns a VPTFile included in this project that references the given
	 *	path.
	 *
	 *	@deprecated Use {@link #getChildNode(String) getChildNode(String)}
	 *				instead.
	 */
	public VPTFile getFile(String path) {
		VPTNode o = getChildNode(path);
		if (o.isFile()) {
			return (VPTFile) o;
		}
		return null;
	} //}}}

	//{{{ +getChildNode(String) : VPTNode
	/**
	 *	Returns the node that matches the given path. Despite the name (too
	 *	late to change, don't want to break other plugins), this only applies
	 *	to "openable nodes", i.e., nodes whose canOpen() method return true
	 *	and which are correcty registered with their respective projects.
	 */
	public VPTNode getChildNode(String path) {
		return (VPTNode) openableNodes.get(path);
	} //}}}

	//{{{ +getFiles() : Collection
	/**
	 *	Returns a read-only collection of the files contained in this
	 *	project.
	 *
	 *	@deprecated Use {@link #getOpenableNodes() getOpenableNodes()} instead.
	 */
	public Collection getFiles() {
		ArrayList lst = new ArrayList();
		for (Iterator i = openableNodes.values().iterator(); i.hasNext(); ) {
			VPTNode node = (VPTNode) i.next();
			if (node.isFile()) {
				lst.add(node);
			}
		}
		return Collections.unmodifiableCollection(lst);
	}
	//}}}

	//{{{ +getOpenableNodes() : Collection
	/**
	 *	Returns a read-only collection of the nodes that can be opened contained
	 *	in this project.
	 */
	public Collection getOpenableNodes() {
		return Collections.unmodifiableCollection(openableNodes.values());
	}
	//}}}

	//{{{ +getURL() : String
	/** Returns the project's URL. */
	public String getURL() {
		return url;
	} //}}}

	//{{{ +setURL(String) : void
	/** Sets the project's URL. */
	public void setURL(String url) {
		if (url != null && !url.endsWith("/")) url += "/";
		this.url = url;
	} //}}}

	//{{{ +getProperty(String) : String
	/** Returns the property stored for the given key, as a String. */
	public String getProperty(String property) {
		Object o =  properties.get(property);
		return (o != null) ? o.toString() : null;
	} //}}}

	//{{{ +getObjectProperty(String) : Object
	/** Returns the property stored for the given key. */
	public Object getObjectProperty(String property) {
		return properties.get(property);
	} //}}}

	//{{{ +setProperty(String, String) : String
	/**
	 *	Sets a property.
	 *
	 *	@return	The old value for the property (can be null).
	 */
	public String setProperty(String name, String value) {
		Object old = properties.get(name);
		properties.put(name, value);
		return (old != null) ? old.toString() : null;
	} //}}}

	//{{{ +setProperty(String, Object) : Object
	/**
	 *	Sets a property.
	 *
	 *	@return	The old value for the property (can be null).
	 */
	public Object setProperty(String name, Object value) {
		Object old = properties.get(name);
		properties.put(name, value);
		return old;
	} //}}}

	//{{{ +getPropertyNames() : Set
	/**	Returns a set containing all property names for this project. */
	public Set getPropertyNames() {
		return properties.keySet();
	} //}}}

	//{{{ +removeProperty(String) : Object
	/** Removes the given property from the project. */
	public Object removeProperty(String property) {
		return properties.remove(property);
	} //}}}

	//{{{ +getProperties() : Properties
	/** Return the project's property set. */
	public Properties getProperties() {
		return properties;
	} //}}}

	//{{{ +getOpenFiles() : Iterator
	/**
	 *	Returns an iterator to the list of open files that this project
	 *	remembers.
	 */
	public Iterator getOpenFiles() {
		return openFiles.iterator();
	} //}}}

	//{{{ +addOpenFile(String) : void
	/**
	 *	Adds a file to the list of the project's opened files.
	 */
	public void addOpenFile(String path) {
		openFiles.add(path);
	} //}}}

	//{{{ +clearOpenFiles() : void
	/** Clears the list of open files. */
	public void clearOpenFiles() {
		openFiles.clear();
	} //}}}

	//{{{ +isProjectFile(String) : boolean
	/**
	 *	Returns whether the file denoted by the given path is part of this
	 *	project.
	 *
	 *	@deprecated Use {@link #isInProject(String) isInProject(String)} instead.
	 */
	public boolean isProjectFile(String path) {
		return isInProject(path);
	} //}}}

	//{{{ +isInProject(String) : boolean
	/**
	 *	Returns whether the project contains a node that can be opened that
	 *	matches the given path.
	 */
	public boolean isInProject(String path) {
		return openableNodes.containsKey(path);
	} //}}}

	//{{{ +getIcon(boolean) : Icon
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		return projectIcon;
	} //}}}

	//{{{ +toString() : String
	/** Returns a string representation of the current node. */
	public String toString() {
		return "Project [" + getName() + "]";
	} //}}}

	//{{{ +getRootPath() : String
	/** Returns the path to the root of the project. */
	public String getRootPath() {
		return rootPath;
	} //}}}

	//{{{ +setRootPath(String) : void
	/** Sets the path to the root of the project. */
	public void setRootPath(String path) {
		rootPath = path;
	} //}}}

	//{{{ +registerFile(VPTFile) : void
	/**
	 *	Register a file in the project, adding it to the list of files that
	 *	belong to the project
	 */
	public void registerFile(VPTFile file) {
		registerNodePath(file);
	}
	//}}}

	//{{{ +registerNodePath(VPTNode) : void
	/**
	 *	Register a node in the project, adding it to the mapping of paths to
	 *	nodes kept internally.
	 */
	public void registerNodePath(VPTNode node) {
		openableNodes.put(node.getNodePath(), node);
	}
	//}}}

	//{{{ +removeAllChildren() : void
	/** Removes all children from the project, and unregisters all files. */
	public void removeAllChildren() {
		openableNodes.clear();
		super.removeAllChildren();
	} //}}}

	//{{{ +unregisterNodePath(VPTNode) : void
	/** Unegister a node from the project. */
	public void unregisterNodePath(VPTNode node) {
		openableNodes.remove(node.getNodePath());
	} //}}}

	//{{{ +getNodePath() : String
	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return getRootPath();
	} //}}}

	//{{{ +compareToNode(VPTNode) : int
	/** Projects have precedence over everything but groups. */
	public int compareToNode(VPTNode node) {
		if (node.isGroup()){
			return 1;
		} else if (node.isProject()) {
			return getName().compareTo(node.getName());
		} else {
			return 1;
		}
	} //}}}

	//{{{ Listener Subscription and Event Dispatching

	//{{{ +addProjectListener(ProjectListener) : void
	/**
	 *	Adds a new listener to the list. The list if listeners is global to
	 *	all the projects, so listeners don't need to be registered to each
	 *	individual project.
	 */
	public void addProjectListener(ProjectListener lstnr) {
		if (listeners == null) {
			listeners = new HashSet();
		}
		listeners.add(lstnr);
	} //}}}

	//{{{ +removeProjectListener(ProjectListener) : void
	/** Removes a listener from the list. */
	public void removeProjectListener(ProjectListener lstnr) {
		if (listeners != null) {
			listeners.remove(lstnr);
		}
	} //}}}

	//{{{ +hasListeners() : boolean
	/**
	 *	Returns whether there are any listeners registered. Mainly for use to
	 *	enhance performance by classes that would fire these events.
	 */
	public boolean hasListeners() {
		return (listeners != null && listeners.size() > 0);
	} //}}}

	//{{{ +fireFilesChanged(ArrayList, ArrayList) : void
	/**
	 *	Notifies the listeners that a group of files has been added to and/or
	 *	removed from the project.
	 */
	public void fireFilesChanged(ArrayList added, ArrayList removed) {
		if (listeners.size() > 0) {
			ProjectEvent pe = new ProjectEvent(this, added, removed);
			for (Iterator i = listeners.iterator(); i.hasNext(); ) {
				ProjectListener lstnr = (ProjectListener) i.next();
				if (added != null && added.size() > 0) {
					lstnr.filesAdded(pe);
				}
				if (removed != null && removed.size() > 0) {
					lstnr.filesRemoved(pe);
				}
			}
		}
	} //}}}

	//{{{ +fireFileAdded(VPTFile) : void
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

	//{{{ +fireFileRemoved(VPTFile) : void
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

	//{{{ +firePropertiesChanged() : void
	/**
	 *	Notifies the listeners that a single file has been added to the
	 *	project.
	 */
	public void firePropertiesChanged() {
		if (hasListeners()) {
			ProjectEvent pe = new ProjectEvent(this);
			for (Iterator i = listeners.iterator(); i.hasNext(); ) {
				((ProjectListener)i.next()).propertiesChanged(pe);
			}
		}
	} //}}}

	//}}}

}

