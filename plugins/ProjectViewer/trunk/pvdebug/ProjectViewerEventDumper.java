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
package pvdebug;

import org.gjt.sp.util.Log;

import projectviewer.event.ProjectViewerEvent;
import projectviewer.event.ProjectViewerListener;

/**
 *	A listener to be used for debugging..
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectViewerEventDumper implements ProjectViewerListener {

	/** Notifies the changing of the active project. */
	public void projectLoaded(ProjectViewerEvent evt) {
		if (evt.getProject() == null) {
			Log.log(Log.ERROR, this, "Changed to all projects view.");
		} else {
			Log.log(Log.ERROR, this, "Loaded project: " + evt.getProject().getName());
			if (evt.getProject().getObjectProperty("dummy") == null) {
				evt.getProject().setProperty("dummy", new DummyProperty());
			} else {
				Object o = evt.getProject().getObjectProperty("dummy");
				Log.log(Log.ERROR, this, o);
			}
		}
	}

	/** Notifies the creation of a project. */
	public void projectAdded(ProjectViewerEvent evt) {
		Log.log(Log.ERROR, this, "Added project: " + evt.getProject().getName());
	}

	/** Notifies the removal of a project. */
	public void projectRemoved(ProjectViewerEvent evt) {
		Log.log(Log.ERROR, this, "Removed project: " + evt.getProject().getName());
	}

		/**
	 *	Notifies the addition of a group.
	 *
	 *	@since	PV 2.1.0
	 */
	 public void groupAdded(ProjectViewerEvent evt) {
		 Log.log(Log.ERROR, this, "Added group: " + evt.getSource());
	 }

	/**
	 *	Notifies the removal of a group.
	 *
	 *	@since	PV 2.1.0
	 */
	 public void groupRemoved(ProjectViewerEvent evt) {
		 Log.log(Log.ERROR, this, "Removed group: " + evt.getSource());
	 }

	/**
	 *	Notifies that a group has been activated.
	 *
	 *	@since	PV 2.1.0
	 */
	 public void groupActivated(ProjectViewerEvent evt){
		 Log.log(Log.ERROR, this, "Activated group: " + evt.getSource());
	 }

	/**
	 *	Notifies that a project or group has been moved to another group.
	 *
	 *	@since	PV 2.1.0
	 */
	public void nodeMoved(ProjectViewerEvent evt) {
		Log.log(Log.ERROR, this, "Moved node: " + evt.getSource() +
		 ", old parent: " + evt.getOldParent());
	}

	public void nodeSelected(ProjectViewerEvent evt)
	{
		Log.log(Log.ERROR, this, "Selected Node: " + evt.getNode());
	}


}

