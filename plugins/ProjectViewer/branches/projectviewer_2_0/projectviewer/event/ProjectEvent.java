/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.event;

import java.util.*;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;

/** 
 *	A project event.
 *
 *	@version	$Id$
 */
public final class ProjectEvent extends EventObject {

	private VPTNode artifact;
	private int index;

	/** Create a new <code>ProjectEvent</code>.
	 *
	 *@param  project  Description of Parameter
	 */
	public ProjectEvent(VPTProject project) {
		super(project);
	}

	/** Create a new <code>ProjectEvent</code>.
	 *
	 *@param  project   Description of Parameter
	 *@param  artifact  Description of Parameter
	 */
	public ProjectEvent(VPTProject project, VPTFile artifact) {
		this(project, artifact, -1);
	}

	/** Create a new <code>ProjectEvent</code>.
	 *
	 *@param  project     Description of Parameter
	 *@param  anArtifact  Description of Parameter
	 *@param  anIndex     Description of Parameter
	 */
	public ProjectEvent(VPTProject project, VPTFile anArtifact, int anIndex) {
		super(project);
		artifact = anArtifact;
		index = anIndex;
	}

	/** 
	 *	Returns the artifact as a {@link VPTDirectory VPTDirectory}.
	 *
	 *	@return    The projectDirectory value
	 */
	public VPTDirectory getProjectDirectory() {
		return (VPTDirectory) artifact;
	}

	/** Returns the project file.
	 *
	 *@return    The projectFile value
	 */
	public VPTFile getProjectFile() {
		return (VPTFile) artifact;
	}

	/** 
	 *	Returns the {@link VPTProject Project}.
	 *
	 *@return    The project value
	 */
	public VPTProject getProject() {
		return (VPTProject) getSource();
	}

	/** Returns the project artifact.
	 *
	 *@return    The artifact value
	 */
	public Object getArtifact() {
		return artifact;
	}

	/** Returns the index of the project child involved.  This index is relative
	 * to the file's parent directory.
	 *
	 *@return    The index value
	 */
	public int getIndex() {
		return index;
	}

	/** Returns the path to the child involved.
	 *
	 *@return    The path value
	 */
	public List getPath() {
		//if (artifact.isFile())
			//return getProject().getRoot().getPathToFile(getProjectFile());
		//if (artifact.isDirectory())
			//return getProject().getRoot().getPathToDirectory(getProjectDirectory());
		return null;
	}

}

