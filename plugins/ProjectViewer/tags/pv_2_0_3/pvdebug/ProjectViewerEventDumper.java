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
package pvdebug;

import org.gjt.sp.util.Log;

import projectviewer.event.ProjectViewerEvent;
import projectviewer.event.ProjectViewerListener;

/**
 *	A listener for {@link ProjectViewerEvent}s.
 *
 *	@author		Dale Anson, Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectViewerEventDumper implements ProjectViewerListener {

	/** Notifies the changing of the active project. */
	public void projectLoaded(ProjectViewerEvent evt) {
		Log.log(Log.ERROR, this, "Loaded project: " + evt.getProject().getName());
		if (evt.getProject().getObjectProperty("dummy") == null) {
			evt.getProject().setProperty("dummy", new DummyProperty());
		} else {
			Object o = evt.getProject().getObjectProperty("dummy");
			Log.log(Log.ERROR, this, o);
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

}

