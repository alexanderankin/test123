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
	 *	{@link ProjectEvent#getFile() getFile()} method.
	 *
	 *	@param	pe	The project event.
	 */
	public void fileAdded(ProjectEvent pe) { }

	/**
	 *	Method called when several files have been added to a project. The event
	 *	object will contain the list of files, returned by the
	 *	{@link ProjectEvent#getFiles() getFiles()} method.
	 *
	 *	@param	pe	The project event.
	 */
	public void filesAdded(ProjectEvent pe) { }

}

