/*
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

//standard GUI stuff
import java.awt.Cursor;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;

import org.gjt.sp.util.Log;

/**
 * Listen to all buttons and GUI events and respond to them.
 *
 *@author     ensonic
 *@created    19. Juni 2002
 *@version    $Id$
 */
class ViewerListener
		 implements ActionListener, ItemListener {

	private ProjectViewer viewer;
	private Launcher launcher;
	private boolean paused;
	private FileFilter nonProjectFileFilter;

	/** Create a new <code>ViewerListener</code>.
	 *
	 *@param  instance  Description of Parameter
	 *@param  launcher  Description of Parameter
	 */
	public ViewerListener(ProjectViewer instance, Launcher launcher) {
		this.viewer = instance;
		this.launcher = launcher;
		paused = false;
	}

	/** Pause this listener. Any events received will be ignored. */
	public void pause() {
		paused = true;
	}

	/** Resume this listener. Any events received will not be handled. */
	public void resume() {
		paused = false;
	}

	/** Listen to specific GUI events.
	 *
	 *@param  evt  Description of Parameter
	 */
	public void actionPerformed(ActionEvent evt) {
		if (paused) {
			return;
		}

		viewer.setStatus(" ");
		Object source = evt.getSource();

		if (source == this.viewer.createProjectBtn) {
			this.createProject();
		}
		else if (source == this.viewer.deleteProjectBtn) {
			this.deleteSelectedProject();
		}
		else if (source == this.viewer.addFileBtn) {
			this.addFileToProject();
		}
		else if (source == this.viewer.removeFileBtn) {
			this.removeFilesFromProject();
			//viewer.getCurrentProject().removeFile( viewer.getSelectedFile() );
		}
		else if (source == this.viewer.removeAllFilesBtn) {
			this.removeAllFilesFromProject();
		}
		else if (source == this.viewer.importFilesBtn) {
			getImporter().doImport();
		}
		else if (source == this.viewer.openAllBtn) {
			this.openAllFilesInProject();
		}
		else if (source == this.viewer.expandBtn) {
			viewer.expandAll();
		}
		else if (source == this.viewer.contractBtn) {
			viewer.collapseAll();
		}
		else if (source == this.viewer.configBtn) {
			this.showConfig();
		}
	}


	/** Handle project combo changes.
	 *
	 *@param  evt  Description of Parameter
	 */
	public void itemStateChanged(ItemEvent evt) {
		if (paused) {
			return;
		}
		if (evt.getItem() instanceof Project) {
			viewer.setCurrentProject((Project) evt.getItem());
		}
		else {
			viewer.setCurrentProject(null);
		}
	}


	/** Show the config dialog to the user. */
	public void showConfig() {
		JDialog dialog = new JDialog();
		dialog.setTitle("Config");
		dialog.getContentPane().add(new ProjectViewerPane(viewer));
		dialog.setSize(350, 600);
		dialog.setVisible(true);
		dialog.setEnabled(true);
		dialog.toFront();
		dialog.setVisible(true);
	}

	/** Returns an instance of {@link ProjectFilesImporter}.
	 *
	 *@return    The importer value
	 */
	private ProjectFileImporter getImporter() {
		return new ProjectFileImporter(viewer);
	}

	/** Create a new Project */
	private void createProject() {
		String projectName = JOptionPane.showInputDialog(viewer,
				"Please enter a project name.  You will also be prompted for a home directory.");

		if (projectName == null) {
			return;
		}

		if (ProjectManager.getInstance().hasProject(projectName)) {
			JOptionPane.showMessageDialog(viewer,
					"There is currently a project with this name.");
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Enter your home directory for \"" + projectName + "\"");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (chooser.showOpenDialog(viewer) == JFileChooser.CANCEL_OPTION) {
			return;
		}

		File prjHome = chooser.getSelectedFile();
		Project project = new Project(projectName, new ProjectDirectory(prjHome.getAbsolutePath()));
		ProjectManager.getInstance().addProject(project);
		viewer.setCurrentProject(project);

		int confirmed = JOptionPane.showConfirmDialog(this.viewer,
				"Do you want to import files from " + prjHome + "?",
				"Import files?",
				JOptionPane.YES_NO_OPTION);
		viewer.showWaitCursor();
		if (confirmed == JOptionPane.YES_OPTION) {
			getImporter().doImport(prjHome);
		}
		viewer.refresh();
		viewer.showDefaultCursor();
	}

	/** Prompt the user to a file, get the current project, and then add the file
	 *  to the project.
	 */
	private void addFileToProject() {
		JFileChooser chooser = viewer.createFileChooser();
		if (nonProjectFileFilter == null) {
			nonProjectFileFilter =
				new FileFilter() {
					public String getDescription() {
						return "Non Project Files";
					}


					public boolean accept(File f) {
						return !viewer.getCurrentProject().isProjectFile(f.getAbsolutePath());
					}
				};
		}
		chooser.setFileFilter(nonProjectFileFilter);
		//chooser.setAcceptAllFileFilterUsed(false); #JDK1.3
		if (chooser.showOpenDialog(this.viewer) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		viewer.getCurrentProject().importFile(
				new ProjectFile(chooser.getSelectedFile().getAbsolutePath()));
	}

	/** Prompt the user if he want to remove all files below the selected entry. */
	private void removeFilesFromProject() {
		Object node = viewer.getSelectedNode();
		Log.log(Log.DEBUG, this, "node :" + node.toString());

		if (node instanceof ProjectFile) {
			Log.log(Log.DEBUG, this, " is file");
			viewer.getCurrentProject().removeFile((ProjectFile)node);
		}
		else if (node instanceof ProjectDirectory) {
			Log.log(Log.DEBUG, this, " is dir");
			int answer = JOptionPane.showConfirmDialog(viewer,
					"Are you sure you want to remove all files below the selected folder from the current project?",
					"Remove all files below the selected folder?",
					JOptionPane.YES_NO_OPTION);

			if (answer == JOptionPane.YES_OPTION) {
				viewer.getCurrentProject().removeDirectory((ProjectDirectory)node);
			}
		}
		else {
			Log.log(Log.DEBUG, this, " is ???");
			removeAllFilesFromProject();
		}
	}


	/** Prompt the user if he want to remove all file from a projects. */
	private void removeAllFilesFromProject() {
		int answer = JOptionPane.showConfirmDialog(viewer,
				"Are you sure you want to remove all files from the current project?",
				"Remove all files?",
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.YES_OPTION) {
			viewer.getCurrentProject().removeAllFiles();
		}
	}

	/** Delete all this project and select all projects. */
	private void deleteSelectedProject() {
		Project project = this.viewer.getCurrentProject();

		int confirmed =
				JOptionPane.showConfirmDialog(viewer,
				"Are you sure you want to delete the project: " + project + " ?",
				"Delete project?",
				JOptionPane.YES_NO_OPTION);

		if (confirmed != JOptionPane.YES_OPTION) {
			return;
		}

		ProjectManager.getInstance().removeProject(project);
		viewer.setCurrentProject(null);
		viewer.refresh();
	}

	/** Progmatically open all files under the current project... */
	private void openAllFilesInProject() {
		viewer.showDefaultCursor();
		for (Iterator i = viewer.getCurrentProject().projectFiles(); i.hasNext(); ) {
			launcher.launchFile((ProjectFile) i.next());
		}
		viewer.showDefaultCursor();
	}

}

