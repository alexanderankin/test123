/*
 * $Id$
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
package projectviewer;

//standard GUI stuff
import java.awt.Cursor;
import java.awt.event.*;
import java.io.File;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;

import org.gjt.sp.util.Log;
import projectviewer.config.ProjectPropertiesDlg;
import projectviewer.config.ProjectViewerConfig;

/** Listen to all buttons and GUI events and respond to them. */
public final class ViewerListener implements WindowListener, ActionListener, ItemListener {

	private ProjectViewer viewer;
	private Launcher launcher;
	private boolean paused;
	private FileFilter nonProjectFileFilter;


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
	 * @param  evt  Description of Parameter
	 */
	public void actionPerformed(ActionEvent evt) {
		if(paused) {
			return;
		}

		viewer.setStatus(" ");
		Object source = evt.getSource();

		if(source == this.viewer.createProjectBtn) {
			this.createProject();
		}
		else if(source == this.viewer.addFileBtn) {
			this.addFileToProject();
		}
		else if(source == this.viewer.removeFileBtn) {
			new RemovalManager(viewer).handleSelectionRemoval(false);
		}
		else if(source == this.viewer.removeAllFilesBtn) {
			this.removeAllFilesFromProject();
		}
		else if(source == this.viewer.importFilesBtn) {
			getImporter().doImport();
		}
		else if(source == this.viewer.openAllBtn) {
			this.openAllFilesInProject();
		}
		else if(source == this.viewer.expandBtn) {
			viewer.expandAll();
		}
		else if(source == this.viewer.contractBtn) {
			viewer.collapseAll();
		}
		else if(source == this.viewer.launchBrowserBtn) {
			// this.showConfig(); // will need to be commented out
			this.launchBrowser();
		}

	}

	/** Handle project combo changes.
	 *
	 * @param  evt  Description of Parameter
	 */
	public void itemStateChanged(ItemEvent evt) {
		if(paused) {
			return;
		}
		if(evt.getItem() instanceof Project) {
			Log.log(Log.DEBUG, this, "vsl.itemStateChanged, got a project");
			viewer.setCurrentProject((Project)evt.getItem());
		}
		else {
			Log.log(Log.DEBUG, this, "vsl.itemStateChanged, no project");
			viewer.setCurrentProject(null);
		}
	}

	/*
	 * Window Listener interface
	 */
	public void windowActivated(WindowEvent e) { }

	public void windowClosed(WindowEvent e) { }

	/** Unregister the viewer from the ProjectViewerConfig listeners.
	 *
	 * @param  e  Description of Parameter
	 */
	public void windowClosing(WindowEvent e) {
		ProjectViewerConfig.getInstance().removePropertyChangeListener(viewer);
	}

	public void windowDeactivated(WindowEvent e) { }

	public void windowDeiconified(WindowEvent e) { }

	public void windowIconified(WindowEvent e) { }

	public void windowOpened(WindowEvent e) { }

	/** Returns an instance of {@link ProjectFilesImporter}.
	 *
	 * @return    The importer value
	 */
	private ProjectFileImporter getImporter() {
		return new ProjectFileImporter(viewer);
	}


	/** Launched the selected project file in the web-browser, against the webserver.
	 * browser property must be set for jedit & urlRoot must be set for the given project
	 */
	private void launchBrowser() {
		/*
		 * need to get browser setting
		 */
		String sURLRoot = viewer.getCurrentProject().getURLRoot();
		String sURL;
		String browserExecPath = ProjectViewerConfig.getInstance().getBrowserPath();
		if(sURLRoot == "") {
			JOptionPane.showMessageDialog(viewer, "Web URL Not set for project");
			return;
		}

		if(viewer.isFileSelected()) {
			ProjectFile fileToView = viewer.getSelectedFile();

			/*
			 * Produce the url of the file based upon the projects urlRoot
			 */
			sURL = sURLRoot + fileToView.getPath().toString().substring(viewer.getCurrentProject().getRoot().getPath().length());
			//sURL = sURLRoot + fileToView.getPath();
			JOptionPane.showMessageDialog(viewer, sURL);

			Runtime rt = Runtime.getRuntime();
			String[] callAndArgs = {browserExecPath, sURL};
			try {
				Process child = rt.exec(callAndArgs);
				child.wait(4);
				System.out.println("Process exit code is: " + child.exitValue());
			}
			catch(IOException e) {
				System.err.println("IOException starting process!");
			}
			catch(InterruptedException e) {
				System.err.println("Interrupted waiting for process!");
			}
		}
		else {
			JOptionPane.showMessageDialog(viewer, "No File selected");
		}
	}

	/** Create a new Project */
	private void createProject() {
		Log.log( Log.DEBUG, this, "createProject()");

		Project project = ProjectPropertiesDlg.run(viewer, null);

		Log.log( Log.DEBUG, this, "createProject(), project="+project.toString());
		
		if(project == null)
			return;

		if(ProjectManager.getInstance().hasProject(project.getName())) {
			JOptionPane.showMessageDialog(viewer, "There is currently a project with this name.");
			return;
		}

		Log.log( Log.DEBUG, this, "createProject(), project.getRoot().1="+project.getRoot().toString());
		
		project.setLoaded(true);
		ProjectManager.getInstance().addProject(project);
		viewer.setCurrentProject(project);

		Log.log( Log.DEBUG, this, "createProject(), project="+project);

		File prjHome = project.getRoot().toFile();
		int confirmed = JOptionPane.showConfirmDialog(this.viewer,
				"Do you want to import files from " + prjHome + "?",
				"Import files?",
				JOptionPane.YES_NO_OPTION);
		viewer.showWaitCursor();
		if(confirmed == JOptionPane.YES_OPTION) {
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
		if(nonProjectFileFilter == null) {
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
		if(chooser.showOpenDialog(this.viewer) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		viewer.getCurrentProject().importFile(
				new ProjectFile(chooser.getSelectedFile().getAbsolutePath()));

		if(ProjectViewerConfig.getInstance().getSaveOnChange()) {
			viewer.getCurrentProject().save();
		}

	}

	/** Prompt the user if he want to remove all file from a projects. */
	private void removeAllFilesFromProject() {
		int answer = JOptionPane.showConfirmDialog(viewer,
				"Are you sure you want to remove all files from the current project?",
				"Remove all files?",
				JOptionPane.YES_NO_OPTION);

		if(answer == JOptionPane.YES_OPTION) {
			viewer.getCurrentProject().removeAllFiles();
		}
	}

	/** Progmatically open all files under the current project... */
	private void openAllFilesInProject() {
		viewer.showDefaultCursor();
		for(Iterator i = viewer.getCurrentProject().projectFiles(); i.hasNext(); ) {
			launcher.launchFile((ProjectFile)i.next());
		}
		viewer.showDefaultCursor();
	}
}

