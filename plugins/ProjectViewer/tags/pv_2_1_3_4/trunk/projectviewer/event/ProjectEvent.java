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

//{{{ Imports
import java.util.Iterator;
import java.util.ArrayList;
import java.util.EventObject;

import projectviewer.ProjectViewer;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
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
	private ArrayList	addedFiles;
	private ArrayList	removedFiles;
	private VPTFile		file;
	private boolean		added;
	//}}}

	//{{{ Constructor

	public ProjectEvent(VPTProject p, ArrayList added, ArrayList removed) {
		super(p);
		this.src = p;
		this.addedFiles = added;
		this.removedFiles = removed;
	}

	public ProjectEvent(VPTProject p, VPTFile file, boolean added) {
		super(p);
		this.src = p;
		this.file = file;
		this.added = added;
	}
	
	public ProjectEvent(VPTProject p) {
		super(p);
		this.src = p;
	}

	//}}}

	//{{{ getProject() method
	/** Returns the project. */
	public VPTProject getProject() {
		return src;
	} //}}}

	//{{{ getAddedFiles() method
	/** Returns the list of files added (null if single file or no file(s) added). */
	public ArrayList getAddedFiles() {
		return addedFiles;
	} //}}}

	//{{{ getAddedFile() method
	/** Returns the added file (null if multiple files or no file(s) added). */
	public VPTFile getAddedFile() {
		return (added) ? file : null;
	} //}}}

	//{{{ getRemovedFiles()
	/** Returns the list of removed files (null if no file(s) were removed). */
	public ArrayList getRemovedFiles() {
		return removedFiles;
	} //}}}

	//{{{ getRemovedFile()
	/** Returns the removed file (null if multiple files, or no file(s) were removed). */
	public VPTFile getRemovedFile() {
		return (added) ? null : file;
	} //}}}

}

