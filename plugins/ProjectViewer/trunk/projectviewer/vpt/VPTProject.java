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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.Icon;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.PVActions;
import projectviewer.ProjectViewer;
import projectviewer.event.ProjectUpdate;
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

	private final static Icon projectIcon = GUIUtilities.loadIcon("DriveSmall.png");

	//{{{ Attributes

	private List		openFiles;
	private List 		filterList;
	private String		rootPath;
	private String		url;
	private Properties	properties;

	protected Map<String,VPTNode> openableNodes;

	//}}}

	//{{{ +VPTProject(String) : <init>

	public VPTProject(String name) {
		super(name, true);
		openableNodes	= new HashMap<String,VPTNode>();
		openFiles		= new ArrayList();
		properties		= new Properties();
		filterList		= Collections.EMPTY_LIST;
	}

	//}}}

	//{{{ +getChildNode(String) : VPTNode
	/**
	 *	Returns the node that matches the given path. Despite the name (too
	 *	late to change, don't want to break other plugins), this only applies
	 *	to "openable nodes", i.e., nodes whose canOpen() method return true
	 *	and which are correcty registered with their respective projects.
	 */
	public VPTNode getChildNode(String path) {
		return openableNodes.get(path);
	} //}}}

	//{{{ +getOpenableNodes() : Collection
	/**
	 *	Returns a read-only collection of the nodes that can be opened contained
	 *	in this project.
	 */
	public Collection<VPTNode> getOpenableNodes() {
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
		return properties.getProperty(property);
	} //}}}

	//{{{ +setProperty(String, String) : String
	/**
	 *	Sets a property.
	 *
	 *	@return	The old value for the property (can be null).
	 */
	public String setProperty(String name, String value) {
		return (String) properties.setProperty(name, value);
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
		if (!openFiles.contains(path))
			openFiles.add(path);
	} //}}}

	/**
	 * Remove an open file from the list.
	 *
	 * @since PV 2.1.3.5
	 */
	public void removeOpenFile(String path) {
		openFiles.remove(path);
	}

	//{{{ +clearOpenFiles() : void
	/** Clears the list of open files. */
	public void clearOpenFiles() {
		openFiles.clear();
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

	//{{{ +registerNodePath(VPTNode) : void
	/**
	 *	Register a node in the project, adding it to the mapping of
	 *	paths to nodes kept internally. Only openable nodes are mapped.
	 */
	public void registerNodePath(VPTNode node) {
		if (node.canOpen()) {
			openableNodes.put(node.getNodePath(), node);
		}
	} //}}}

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

	/**
	 *	Unegister a node path from the project.
	 *
	 *	@since	PV 2.1.3.6
	 */
	public void unregisterNodePath(String path) {
		openableNodes.remove(path);
	}

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
			return compareName(node);
		} else {
			return 1;
		}
	} //}}}

	//{{{ +addFilter(VPTFilterData) : void
	/**
	 *
	 *	@since PV 2.2.2.0
	 */
	public void addFilter(VPTFilterData filterData) {
		getFilterList().add(filterData);
	} //}}}

	//{{{ +setFilterList(List) : void
	/**
	 *	Sets the list of filters particular to this project.
	 *
	 *	@since PV 2.2.2.0
	 */
	public void setFilterList(List filterList) {
		this.filterList = filterList;
		ProjectViewer.nodeStructureChanged(this);
	} //}}}

	//{{{ +getFilterList() : List
	/**
	 *	Returns the list of filters set for this project, or
	 *	Collections.EMPTY_LIST if no filters are set.
	 *
	 *	@since PV 2.2.2.0
	 */
	public List getFilterList() {
		return filterList;
	} //}}}

	//{{{ +fireFilesChanged(ArrayList, ArrayList) : void
	/**
	 *	Notifies the listeners that a group of files has been added to and/or
	 *	removed from the project.
	 */
	public void fireFilesChanged(List<VPTFile> added, List<VPTFile> removed) {
		ProjectUpdate up = new ProjectUpdate(this, added, removed);
		EditBus.send(up);
	} //}}}

	//{{{ +firePropertiesChanged() : void
	/**
	 *	Notifies the listeners that a single file has been added to the
	 *	project.
	 */
	public void firePropertiesChanged() {
		ProjectUpdate up = new ProjectUpdate(this);
		EditBus.send(up);
	} //}}}

}

