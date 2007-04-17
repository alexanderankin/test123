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

import java.util.Iterator;
import projectviewer.vpt.VPTFile;
import projectviewer.event.*;
import org.gjt.sp.util.Log;

/**
 *	Debugging project events.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectEventDumper implements ProjectListener {

	/**
	 *	Method called when a single file has been added to a project. The event
	 *	object will contain a reference to the file, returned by the
	 *	{@link ProjectEvent#getAddedFile() getFile()} method.
	 *
	 *	@param	pe	The project event.
	 */
	public void fileAdded(ProjectEvent pe) {
		Log.log(Log.ERROR, this, "File added: " + pe.getAddedFile().getFile().getAbsolutePath());
	}

	/**
	 *	Method called when several files have been added to a project. The event
	 *	object will contain the list of files, returned by the
	 *	{@link ProjectEvent#getAddedFiles() getFiles()} method.
	 *
	 *	@param	pe	The project event.
	 */
	public void filesAdded(ProjectEvent pe) {
		Log.log(Log.ERROR, this, "Multiple files added!");
		for (Iterator i = pe.getAddedFiles().iterator(); i.hasNext(); ) {
			Log.log(Log.ERROR, this, "File added: " + ((VPTFile)i.next()).getFile().getAbsolutePath());
		}
	}

	/**
	 *	Method called when a single file has been removed from a project.
	 *
	 *	@param	pe	The project event.
	 */
	public void fileRemoved(ProjectEvent pe) {
		Log.log(Log.ERROR, this, "File removed: " + pe.getRemovedFile().getFile().getAbsolutePath());
	}

	/**
	 *	Method called when more than one file have been removed from a project.
	 *
	 *	@param	pe	The project event.
	 */
	public void filesRemoved(ProjectEvent pe) {
		Log.log(Log.ERROR, this, "Multiple files removed!");
		for (Iterator i = pe.getRemovedFiles().iterator(); i.hasNext(); ) {
			Log.log(Log.ERROR, this, "File removed: " + ((VPTFile)i.next()).getFile().getAbsolutePath());
		}
	}

	/**
	 *	Method called when project properties (such as name and root) have
	 *	changed.
	 *
	 *	@param	pe	The project event.
	 */
	public void propertiesChanged(ProjectEvent pe) {
		Log.log(Log.ERROR, this, "Project properties changed.");
	}

	public void nodeSelected(ProjectViewerEvent evt) {
		Log.log(Log.ERROR, this, "node selected: " + evt.getNode());
	}

}

