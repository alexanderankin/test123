/* $Id$
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
package projectviewer;

import java.util.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;

/** Provides a neutral way to launch projects and files.
 *
 *@author     <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 *@version    $Revision$
 */
public final class Launcher {

	private View view = null;
	private ProjectViewer viewer = null;

	/** Create a new <code>Launcher</code>.
	 *
	 *@param  view    Description of Parameter
	 *@param  viewer  Description of Parameter
	 *@parameter      view         The view that Launcher should open files with.
	 *@parameter      viewer       An instance of ProjectViewer
	 *@author         <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
	 *@version        $Id$
	 */
	public Launcher(View view, ProjectViewer viewer) {
		this.view = view;
		this.viewer = viewer;
	}

	/** Takes a file and opens it up in jEdit.
	 *
	 *@param  file  Description of Parameter
	 *@author       <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
	 */
	public void launchFile(ProjectFile file) {
		if (this.view != null) {
			Buffer buffer = jEdit.openFile(this.view, null, file.getPath(), false, null);
			showFile(file);
		}
	}

	/** Takes a given file and highlights it in the current view.
	 *
	 *@param  file  Description of Parameter
	 *@author       <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
	 *@version      $Id$
	 */
	public void showFile(ProjectFile file) {
		Buffer buffer = file.getBuffer();
		if (buffer != null) {
			view.setBuffer(buffer);
		}
	}

	/** Close the specified project file.
	 *
	 *@param  file  Description of Parameter
	 */
	public void closeFile(ProjectFile file) {
		if (this.view == null)
			return;
		if (file.isOpened())
			jEdit.closeBuffer(view, file.getBuffer());
	}

	/** Close all project resources.
	 *
	 *@param  project  Description of Parameter
	 */
	public void closeProject(Project project) {
		if (project == null)
			return;
		if (viewer.getCurrentProject() == null)
			return;
		for (Iterator i = viewer.getCurrentProject().projectFiles(); i.hasNext(); )
			closeFile((ProjectFile) i.next());
	}

}

