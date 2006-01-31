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
package projectviewer.importer;

//{{{ Imports
import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTProject;
//}}}

/**
 *	A FileFilter that filters out files already added to the project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.1 (was a protected inner class in FileImporter before this)
 */
public class NonProjectFileFilter extends FileFilter {

	private VPTProject project;

	//{{{ +NonProjectFileFilter(VPTProject) : <init>
	public NonProjectFileFilter(VPTProject project) {
		this.project = project;
	} //}}}

	//{{{ +getDescription() : String
	public String getDescription() {
		return jEdit.getProperty("projectviewer.import.filter.non-project-filter");
	} //}}}

	//{{{ +accept(File) : boolean
	public boolean accept(File f) {
		return (project.getChildNode(f.getAbsolutePath()) == null ||
				f.getAbsolutePath().endsWith("~") ||
				f.getAbsolutePath().endsWith(".bak"));
	} //}}}

}

