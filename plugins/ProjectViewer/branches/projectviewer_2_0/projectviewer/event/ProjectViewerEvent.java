/*
 *  $Id$
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

/** A project viewer event.
 */
public final class ProjectViewerEvent extends EventObject {

	private VPTProject project;

	/** Create a new <code>ProjectViewerEvent</code>.
	 *
	 *@param  src  Description of Parameter
	 *@param  prj  Description of Parameter
	 */
	public ProjectViewerEvent(ProjectViewer src, VPTProject prj) {
		super(src);
		project = prj;
	}

	/** Returns the {@link ProjectViewer}.
	 *
	 *@return    The projectViewer value
	 */
	public ProjectViewer getProjectViewer() {
		return (ProjectViewer) getSource();
	}

	/**
	 *	Returns the {@link VPTProject Project}.
	 *
	 *	@return    The project value
	 */
	public VPTProject getProject() {
		return project;
	}

}

