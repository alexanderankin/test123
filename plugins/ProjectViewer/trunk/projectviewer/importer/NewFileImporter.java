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
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;

import org.gjt.sp.util.Log;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
//}}}

/**
 *	Imports a single file into a project. This importer is designed to import
 *	single files that are below the project root, adding all paths up to the
 *	given file. Do not use it for other types of imports.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class NewFileImporter extends Importer {

	protected String path;

	public NewFileImporter(VPTNode node, ProjectViewer viewer, String path)
	{
		super(node, viewer, true);
		this.path = path;
	}

	/**
	 *	Imports the file given in the constructor into the project. If the file
	 *	is not below the project root, do nothing.
	 *
	 *	@return	A collection of VPTNode instances.
	 */
	protected Collection internalDoImport()
	{
		ArrayList added;
		VPTNode node;

		if (!path.startsWith(project.getRootPath())) {
			return null;
		}

		added = new ArrayList();
		try {
			node = constructPath(project, path, added);
			postAction = new ShowNode(node);
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
		return added;
	}

}

