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
package projectviewer.filters;

//{{{ Imports
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.gjt.sp.util.Log;

import projectviewer.config.ProjectViewerConfig;
//}}}

/** A file filter that filters based on file patterns from a properties file.
 *
 * @version $Id$
 */
public final class PatternFilter implements FileFilter {
	private Set includedExtensions;
	private Set includedFiles;
	private Set excludedDirectories;

	//{{{ Constructors
	/** Create a new <code>FileFilter</code>. */
	public PatternFilter() {
		includedExtensions = new HashSet();
		includedFiles = new HashSet();
		excludedDirectories = new HashSet();

		ProjectViewerConfig config = ProjectViewerConfig.getInstance();
			
		copyPropertyIntoSet(config.getImportExts(),includedExtensions);
		copyPropertyIntoSet(config.getIncludeFiles(),includedFiles);
		copyPropertyIntoSet(config.getExcludeDirs(),excludedDirectories);
	}//}}}
	
	//{{{ accept(File)
	/**
	 * Accept files based of properties.
	 *
	 *@param  file  Description of Parameter
	 *@return		 Description of the Returned Value
	 */
	public boolean accept(File file) {
		if (file.isFile()) {
			if (includedFiles.contains(file.getName()))
				return true;
			if (includedExtensions.contains(getFileExtension(file)))
				return true;
		}
		else {
			if (!excludedDirectories.contains(file.getName()))
				return true;
		}
		return false;
	}//}}}

	//{{{ getFileExtension(File)
	/**
	 * Returns the file's extension.
	 *
	 *@param  file  Description of Parameter
	 *@return		 The fileExtension value
	 */
	private String getFileExtension(File file) {
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1 || dotIndex == fileName.length() - 1)
			return null;
		return fileName.substring(dotIndex + 1);
	}//}}}
	
	//{{{ copyPropertyIntoSet(String, Set)
	/**
	 * Load the specified list property to the specified set.
	 *
	 *@param  props			Description of Parameter
	 *@param  propertyName  Description of Parameter
	 *@param  set			Description of Parameter
	 */
	private void copyPropertyIntoSet(String property, Set set) {
		if (property == null)
			return;

		StringTokenizer strtok = new StringTokenizer(property);
		while (strtok.hasMoreTokens())
			set.add(strtok.nextToken());
	}//}}}
}

