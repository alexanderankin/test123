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

/**
 *	Default implementation of the ProjectViewerListener. The methods do nothing
 *	when invoked.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectViewerAdapter implements ProjectViewerListener {

	/** Notifies the changing of the active project. */
	public void projectLoaded(ProjectViewerEvent evt) { }

	/** Notifies the creation of a project. */
	public void projectAdded(ProjectViewerEvent evt) { }

	/** Notifies the removal of a project. */
	public void projectRemoved(ProjectViewerEvent evt) { }

	/**
	 *	Notifies the addition of a group.
	 *
	 *	@since	PV 2.1.0
	 */
	public void groupAdded(ProjectViewerEvent evt) { }

	/**
	 *	Notifies the removal of a group.
	 *
	 *	@since	PV 2.1.0
	 */
	public void groupRemoved(ProjectViewerEvent evt) { }

	/**
	 *	Notifies that a group has been activated.
	 *
	 *	@since	PV 2.1.0
	 */
	public void groupActivated(ProjectViewerEvent evt) { }

	/**
	 *	Notifies that a project or group has been moved to another group.
	 *
	 *	@since	PV 2.1.0
	 */
	public void nodeMoved(ProjectViewerEvent evt) { }

	/**
	 * Notifies that a node has been selected (clicked on) in the ProjectViewer tree
	 * 
	 * @since PV 2.1.0.92
	 */
	public void nodeSelected(ProjectViewerEvent evt)
	{
		// TODO Auto-generated method stub
		
	}

}

