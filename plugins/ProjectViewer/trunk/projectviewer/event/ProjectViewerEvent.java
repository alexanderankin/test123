/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.event;

import java.util.EventObject;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

/**
 *	A project viewer event.
 *
 *	@author		Dale Anson, Marcelo Vanzin
 *	@version	$Id$
 */
public final class ProjectViewerEvent extends EventObject {

	//{{{ Private Members
	private VPTGroup oldParent;
	private VPTProject project;

	private ProjectViewer viewer;
	//}}}


	/** @return the node selected when this event was fired. */
	public VPTNode getNode() {
		try {
			return (VPTNode) getSource();
		}
		catch (Exception e) {return null; }
	}
	
	public ProjectViewerEvent(VPTNode n, ProjectViewer v) {
		super(n);
		viewer = v;
	}

	
	/**
	 *	Create a new <code>ProjectViewerEvent</code>.
	 *
	 *	@param  src  the project viewer instance that fired the event.
	 *	@param  prj  the project loaded (null if "All Projects").
	 */
	public ProjectViewerEvent(ProjectViewer src, VPTProject prj) {
		this(src, src, prj);
	}

	/**
	 *	Create a new <code>ProjectViewerEvent</code>.
	 *
	 *	@param  src  the project viewer instance that fired the event.
	 *	@param  prj  the project loaded (null if "All Projects").
	 */
	public ProjectViewerEvent(Object src, VPTProject prj) {
		this(src, (src instanceof ProjectViewer) ? (ProjectViewer) src : null, prj);
	}

	/**
	 *	Create a new <code>ProjectViewerEvent</code>.
	 *
	 *	@param  src  the project viewer instance that fired the event.
	 *	@param  prj  the project loaded (null if "All Projects").
	 */
	public ProjectViewerEvent(Object src, ProjectViewer viewer, VPTProject prj) {
		super(src);
		this.viewer = viewer;
		project = prj;
	}

	/**
	 *	Constructs an event to notify listeners that a node was moved to
	 *	another group.
	 *
	 *	@since	PV 2.1.0
	 */
	public ProjectViewerEvent(VPTNode src, VPTGroup oldParent) {
		super(src);
		this.oldParent = oldParent;
	}

	/**
	 *	Constructs and event to notify the listeners of the addition or
	 *	removal of a group.
	 *
	 *	@since	PV 2.1.0
	 */
	public ProjectViewerEvent(VPTGroup group) {
		super(group);
	}

	/**
	 *	Constructs and event to notify the listeners of the activation of
	 *	a group in the given view.
	 *
	 *	@since	PV 2.1.0
	 */
	public ProjectViewerEvent(VPTGroup group, ProjectViewer viewer) {
		super(group);
		this.viewer = viewer;
	}

	/**
	 *	Returns the {@link ProjectViewer}.
	 *
	 *	@return    The viewer where the event occurred.
	 */
	public ProjectViewer getProjectViewer() {
		return viewer;
	}

	/**
	 *	Returns the {@link VPTProject Project}. It is important to noticed that
	 *	this value can be <code>null</code>, which means that the "All Projects"
	 *	mode has been activated.
	 *
	 *	@return    The activated project, or null if "All Projects" was chosen.
	 */
	public VPTProject getProject() {
		return project;
	}

	/**
	 *	When firing a noveMoved() event, returns the old parent of the
	 *	affected node (which can be retrieved by getSource()).
	 *
	 *	@since	PV 2.1.0
	 */
	public VPTGroup getOldParent() {
		return oldParent;
	}

}

