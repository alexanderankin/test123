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

//{{{ Imports
import java.util.Collection;
import java.util.EventObject;

import projectviewer.ProjectViewer;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	A project event.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public final class ProjectEvent extends EventObject {

	//{{{ Private Members
	private VPTProject	src;
	private Collection	files;
	private VPTFile		file;
	//}}}

	//{{{ Constructor

	public ProjectEvent(VPTProject p, Collection files) {
		super(p);
		this.src = p;
		this.files = files;
	}

	public ProjectEvent(VPTProject p, VPTFile file) {
		super(p);
		this.src = p;
		this.file = file;
	}

	//}}}

	//{{{ getProject() method
	/** Returns the project. */
	public VPTProject getProject() {
		return src;
	} //}}}

	//{{{ getFiles() method
	/** Returns the list of files added (null if single file). */
	public Collection getFiles() {
		return files;
	} //}}}

	//{{{ getFiles() method
	/** Returns the added file (null if multiple files). */
	public VPTFile getFile() {
		return file;
	} //}}}

}

