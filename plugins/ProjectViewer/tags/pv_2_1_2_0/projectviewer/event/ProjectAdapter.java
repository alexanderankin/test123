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
 *	Default implementation of the listener interface that does nothing when
 *	the methods are called.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectAdapter implements ProjectListener {

	/**
	 *	Method called when a single file has been added to a project. The event
	 *	object will contain a reference to the file, returned by the
	 *	{@link ProjectEvent#getAddedFile() getFile()} method.
	 *
	 *	@param	pe	The project event.
	 */
	public void fileAdded(ProjectEvent pe) { }

	/**
	 *	Method called when several files have been added to a project. The event
	 *	object will contain the list of files, returned by the
	 *	{@link ProjectEvent#getAddedFiles() getFiles()} method.
	 *
	 *	@param	pe	The project event.
	 */
	public void filesAdded(ProjectEvent pe) { }

	/**
	 *	Method called when a single file has been removed from a project.
	 *
	 *	@param	pe	The project event.
	 */
	public void fileRemoved(ProjectEvent pe) { }

	/**
	 *	Method called when more than one file have been removed from a project.
	 *
	 *	@param	pe	The project event.
	 */
	public void filesRemoved(ProjectEvent pe) { }

	/**
	 *	Method called when project properties (such as name and root) have
	 *	changed.
	 *
	 *	@param	pe	The project event.
	 */
	public void propertiesChanged(ProjectEvent pe) { }

}

