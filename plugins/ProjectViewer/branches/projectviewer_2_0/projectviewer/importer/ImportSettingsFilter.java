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
import java.io.FilenameFilter;

import java.util.HashSet;
import java.util.StringTokenizer;

import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.jEdit;

import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Filter that uses the settings provided by the user (in jEdit's options)
 *	to select the files.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ImportSettingsFilter extends FileFilter implements FilenameFilter {

	//{{{ Private members 
	private HashSet includedExtensions;
	private HashSet includedFiles;
	private HashSet excludedDirectories; 
	//}}}

	//{{{ getDescription() method
	public String getDescription() {
		return jEdit.getProperty("projectviewer.importer-filter");
	} //}}}
	
	//{{{ accept(File) method
	public boolean accept(File file) {
		return accept(file.getParentFile(), file.getName());
	} //}}}
		
	//{{{ accept(File, String) method
	public boolean accept(File file, String fileName) {
		if (includedExtensions == null) {
			includedExtensions = new HashSet();
			includedFiles = new HashSet();
			excludedDirectories = new HashSet();

			ProjectViewerConfig config = ProjectViewerConfig.getInstance();
			
			copyPropertyIntoSet(config.getImportExts(),includedExtensions);
			copyPropertyIntoSet(config.getIncludeFiles(),includedFiles);
			copyPropertyIntoSet(config.getExcludeDirs(),excludedDirectories);			
		}
		
		File child = new File(file, fileName);
		if (child.isFile()) {
			if (includedFiles.contains(fileName))
				return true;
			
			// check file extension
			int dotIndex = fileName.lastIndexOf('.');
			if (dotIndex == -1 || dotIndex == fileName.length() - 1)
				return false;
			return includedExtensions.contains(fileName.substring(dotIndex + 1));
		} else {
			return !excludedDirectories.contains(fileName);
		}
	} //}}}
	
	//{{{ copyPropertyIntoSet(String, HashSet) method
	/**
	 *	Load the specified list property to the specified set.
	 *
	 *	@param  props			Description of Parameter
	 *	@param  propertyName  Description of Parameter
	 *	@param  set			Description of Parameter
	 */
	private void copyPropertyIntoSet(String property, HashSet set) {
		if (property == null)
			return;

		StringTokenizer strtok = new StringTokenizer(property);
		while (strtok.hasMoreTokens())
			set.add(strtok.nextToken());
	} //}}}
	
}
