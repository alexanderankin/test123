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
 *  GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.event;

import java.util.EventObject;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

/**
 *	A project viewer event.
 *
 *	@author		Dale Anson, Marcelo Vanzin
 *	@version	$Id$
 */
public final class ProjectViewerEvent extends EventObject {

	private VPTProject project;

	/**
	 *	Create a new <code>ProjectViewerEvent</code>.
	 *
	 *	@param  src  the project viewer instance that fired the event.
	 *	@param  prj  the project loaded (null if "All Projects").
	 */
	public ProjectViewerEvent(ProjectViewer src, VPTProject prj) {
		super(src);
		project = prj;
	}

	/**
	 *	Returns the {@link ProjectViewer}.
	 *
	 *	@return    The viewer where the event occurred.
	 */
	public ProjectViewer getProjectViewer() {
		return (ProjectViewer) getSource();
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

}

