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

import java.io.*;
import java.util.*;
import javax.swing.*;
import org.gjt.sp.util.Log;

import projectviewer.config.ProjectViewerConfig;

/** Imports project files.
 */
public final class ProjectFileImporter {

	private ProjectViewer viewer;
	private Filter filter;

	/** Import files from the current directory.
	 *
	 *@param  aViewer  Description of Parameter
	 */
	public ProjectFileImporter(ProjectViewer aViewer) {
		viewer = aViewer;
	}

	/** Import project files.
	 * This method will ask the user to specify an import
	 * directory using a file chooser.
	 */
	public void doImport() {
		JFileChooser chooser = viewer.createFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(viewer) != JFileChooser.APPROVE_OPTION)
			return;
		doImport(chooser.getSelectedFile());
	}

	/** Import project files starting from the given directory and traversing into
	 * subdirectories.
	 *
	 *@param  directory  Description of Parameter
	 */
	public void doImport(File directory) {
		List files = new ArrayList();

		String projectRoot=viewer.getCurrentProject().getRoot().getPath();
		if(!directory.toString().startsWith(projectRoot)) {
			JOptionPane.showMessageDialog(viewer,
					"The selected directory \""+directory+"\" should be below the project root\""+projectRoot+"\".",
					"Note",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		buildFileList(directory, files);

		if (files.isEmpty()) {
			JOptionPane.showMessageDialog(viewer,
					"No files were found.",
					"Note",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// TODO: Perform import in background.
		viewer.getCurrentProject().importFiles(files);

		JOptionPane.showMessageDialog(viewer,
				"Imported " + files.size() + " file(s) into your project",
				"Import Successful",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/** Returns a filter.
	 *
	 *@return    The filter value
	 */
	private Filter getFilter() {
		if (filter == null)
			filter = new Filter();
		return filter;
	}

	/** Build the file list.
	 *
	 *@param  directory  Description of Parameter
	 *@param  files      Description of Parameter
	 */
	private void buildFileList(File directory, List files) {
		File[] fileArray = directory.listFiles(getFilter());

		for (int i = 0; i < fileArray.length; ++i) {
			if (fileArray[i].isDirectory()) {
				buildFileList(fileArray[i], files);
			}
			else {
				String path = fileArray[i].getAbsolutePath();
				if (!viewer.getCurrentProject().isProjectFile(path))
					files.add(new ProjectFile(path));
			}
		}
	}

	/** A file filter that filters based off a properties file.
	 */
	private class Filter implements FileFilter {
		private Set includedExtensions;
		private Set includedFiles;
		private Set excludedDirectories;

		/** Create a new <code>FileFilter</code>. */
		public Filter() {
			includedExtensions = new HashSet();
			includedFiles = new HashSet();
			excludedDirectories = new HashSet();

            ProjectViewerConfig config = ProjectViewerConfig.getInstance();
            
            copyPropertyIntoSet(config.getImportExts(),includedExtensions);
            copyPropertyIntoSet(config.getIncludeFiles(),includedFiles);
            copyPropertyIntoSet(config.getExcludeDirs(),excludedDirectories);
		}

		/**
		 * Accept files based of properties.
		 *
		 *@param  file  Description of Parameter
		 *@return       Description of the Returned Value
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
		}

		/**
		 * Returns the file's extension.
		 *
		 *@param  file  Description of Parameter
		 *@return       The fileExtension value
		 */
		private String getFileExtension(File file) {
			String fileName = file.getName();
			int dotIndex = fileName.lastIndexOf('.');
			if (dotIndex == -1 || dotIndex == fileName.length() - 1)
				return null;
			return fileName.substring(dotIndex + 1);
		}

		/**
		 * Load the specified list property to the specified set.
		 *
		 *@param  props         Description of Parameter
		 *@param  propertyName  Description of Parameter
		 *@param  set           Description of Parameter
		 */
		private void copyPropertyIntoSet(String property, Set set) {
			if (property == null)
				return;

			StringTokenizer strtok = new StringTokenizer(property);
			while (strtok.hasMoreTokens())
				set.add(strtok.nextToken());
		}

	}

}

