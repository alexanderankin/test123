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

import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.*;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;

import projectviewer.event.*;
import projectviewer.config.ProjectViewerConfig;

/**
 *  A project. Provides many methods to manipulate files/directories within
 *  the project, and also to save/load the project's configuration from the
 *  user's jEdit preferences directory.
 */
public final class Project implements EBComponent {
	/** tree node collapsed */
	public final static char COLLAPSED = '0';
	/** tree node expanded */
	public final static char EXPANDED = '1';
	/** tree node is a leaf */
	public final static char LEAF = '2';

	/** Description of the Field */
	protected final static int PROJECT_KEY_UNSET = -1;

	private ProjectDirectory root;
	private int key;
	private String name;
	private Map files;
	private String urlRoot;
	private List listeners;

	private boolean isLoaded;

	private List openFiles;
	private File buildFile = null;

	// fields for tracking the expansion state of the folder tree
	private JTree folderTree = null;
	private String folderTreeState = "";

	// last open tab
	private int tabState = ProjectViewer.FOLDERS_TAB;

	// last viewed file
	private String lastFile = null;

	/** Constructor for the Project object */
	public Project() {
		this(null, PROJECT_KEY_UNSET);
	}

	/** Create a new <code>Project</code>.
	 *
	 * @param  name  Description of Parameter
	 * @since
	 */
	public Project(String name) {
		this(name, PROJECT_KEY_UNSET);
	}

	/** Create a new <code>Project</code>. <p>
	 *
	 *  Note: This is a constructor intended to be used internally.</p>
	 *
	 * @param  aName  Description of Parameter
	 * @param  aKey   Description of Parameter
	 * @since
	 */
	public Project(String aName, int aKey) {
		name = aName;
		key = aKey;

		root = null;
		isLoaded = false;

		files = new HashMap();
		listeners = new ArrayList();
		openFiles = new ArrayList();
	}

	public static int runTests() {
		Project p = new Project("test");

		int x = 0;
		p.testEscape();
		++x;

		return x;
	}

	//-- Properties
	
	/** Changes the project root, without making any modifications.
	 *
	 * @param  root  The new root value
	 */
	public void setRoot(ProjectDirectory root) {
		this.root = root;
	}

	/** Changes the project name.
	 *
	 * @param  aName  The new name.
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/** Sets the uRLRoot attribute of the Project object
	 *
	 * @param  sWebRoot  The new uRLRoot value
	 */
	public void setURLRoot(String sWebRoot) {
		urlRoot = sWebRoot;
	}

	/** Sets the "isLoaded" flag for this project.
	 *
	 * @param  flag  The new loaded value
	 */
	public void setLoaded(boolean flag) {
		isLoaded = flag;
	}

	/** Sets the buildFile attribute of the Project object
	 *
	 * @param  file  The new buildFile for the Project
	 */
	public void setBuildFile(File file) {
		buildFile = file;
		fireBuildFileSelected(buildFile);
	}

	/** Sets the lastFile attribute of the Project object
	 *
	 * @param  filename  The new lastFile value
	 */
	public void setLastFile(String filename) {
		lastFile = filename;
	}

	/** Return the last active file of the project */
	public String getLastFile() {
		return(lastFile);
	}

	
	/** Returns the project root.
	 *
	 * @return    The root value
	 * @since
	 */
	public ProjectDirectory getRoot() {
		if(root == null) {
			Log.log( Log.ERROR, this, "root is null !");
		}
		return root;
	}

	/** Returns the name of the project.
	 *
	 * @return    The name value
	 * @since
	 */
	public String getName() {
		if(name == null) {
			Log.log( Log.ERROR, this, "name is null !");
		}
		return name;
	}

	/** Returns the WebRoot for the project
	 *
	 * @return    The uRLRoot value
	 */
	public String getURLRoot() {
		return urlRoot == null ? "" : urlRoot;
	}

	/** Returns <code>true</code> if the specified file identifies a file thata is
	 *  in this project.
	 *
	 * @param  aFile  Description of Parameter
	 * @return        The projectFile value
	 * @since
	 */
	public boolean isProjectFile(ProjectFile aFile) {
		return files.containsKey(aFile.getPath());
	}

	/** Returns <code>true</code> if the specified path identifies a file in this
	 *  project.
	 *
	 * @param  aFilePath  Description of Parameter
	 * @return            The projectFile value
	 * @since
	 */
	public boolean isProjectFile(String aFilePath) {
		return files.containsKey(aFilePath);
	}

	/** Returns <code>true</code> if the given object is a project artifact ( file
	 *  or directory ).
	 *
	 * @param  obj  Description of Parameter
	 * @return      The projectArtifact value
	 * @since
	 */
	public boolean isProjectArtifact(Object obj) {
		if(obj instanceof ProjectFile && isProjectFile(((ProjectFile)obj).getPath()))
			return true;
		if(obj instanceof ProjectDirectory)
			return getRoot().getPathToFile(((ProjectDirectory)obj).toFile()) != null;
		return false;
	}

	/** Returns true if the project config has already been loaded.
	 *
	 * @return    The loaded value
	 */
	public boolean isLoaded() {
		return isLoaded;
	}

	/** Returns an iterator for the list of open files.
	 *
	 * @return    The openFiles value
	 */
	public Iterator getOpenFiles() {
		return openFiles.iterator();
	}

	/** Gets the buildFile attribute of the Project object
	 *
	 * @return    The buildFile for the Project
	 */
	public File getBuildFile() {
		return buildFile;
	}

	public void activateLastFile() {
		String lastFile=getLastFile();
		Log.log( Log.DEBUG, this, " activateLastFile() : "+lastFile);
		ProjectFile file = getFile(lastFile);
		if(file != null) fireFileOpened(file);
	}			

	
	/** Changes the project root, adjusting the current loaded configuration
	 *  to meet the new root.
	 *
	 * @param  newRoot  Description of Parameter
	 */
	public void changeRoot(ProjectDirectory newRoot) {
		// First case: newRoot is parent of oldRoot
		if(root.getPath().startsWith(newRoot.getPath()) &&
				root.getPath().length() > newRoot.getPath().length()) {
			File parent = root.toFile().getParentFile();
			File fnewRoot = newRoot.toFile();

			while(!parent.getAbsolutePath().equals(fnewRoot.getAbsolutePath())) {
				ProjectDirectory tmp = new ProjectDirectory(parent.getAbsolutePath());
				tmp.addSubDirectory(root);
				root = tmp;
				parent = parent.getParentFile();
			}

			newRoot.addSubDirectory(root);
			root = newRoot;
		}  else if(root.getPath().trim().length() == 0) {
			/* work around if current root is empty.
			   This seems to happen currently in windows
			*/
			root = newRoot; 
		
		}

	}
	/** Import the specified files.
	 *
	 * @param  files  Description of Parameter
	 * @since
	 */
	public void importFiles(List files) {
		for(Iterator i = files.iterator(); i.hasNext(); )
			importFile((ProjectFile)i.next(), false);
	}

	/** Import this given file.
	 *
	 * @param  aFile  Description of Parameter
	 * @param  save   Description of Parameter
	 * @since
	 */
	public synchronized void importFile(ProjectFile aFile, boolean save) {
		if(isProjectFile(aFile))
			return;

		ProjectDirectory dir = findDirectory(aFile);
		//-- one might have tried to import files from not below our directory
		// danson -- this should be allowed, a user may very well want to add files from
		// outside the project directory.
		if(dir != null) {
			dir.addFile(aFile);
			fireFileAdded(aFile);
			files.put(aFile.getPath(), aFile);
		}
		if(save)
			;
	}

	/** Import this given file.
	 *
	 * @param  aFile  Description of Parameter
	 * @since
	 */
	public synchronized void importFile(ProjectFile aFile) {
		importFile(aFile, true);
	}

	/** Analyze the list of objects passed and removes any ProjectFile and
	 *  ProjectDirectory instances found.
	 *
	 * @param  objects  The list of artifacts to remove.
	 * @param  delete   If ProjectFiles are to be deleted from the disk.
	 */
	public void removeArtifacts(Collection objects, boolean delete) {
		for(Iterator i = objects.iterator(); i.hasNext(); ) {
			Object o = i.next();
			if(o instanceof ProjectFile) {
				removeFile((ProjectFile)o, delete);
			}
			else if(o instanceof ProjectDirectory) {
				removeDirectory((ProjectDirectory)o, delete);
			}
		}
	}

	/** Remove the specified project file.
	 *
	 * @param  aFile  Description of Parameter
	 * @since
	 */
	public void removeFile(ProjectFile aFile) {
		removeFile(aFile, false);
	}

	/** Removes a file from the project. Optionally, deletes the file
	 *  from disk also.
	 *
	 * @param  aFile   The project file to be removed.
	 * @param  delete  If the file should be deleted from disk.
	 */
	public void removeFile(ProjectFile aFile, boolean delete) {
		//Log.log(Log.DEBUG, this, "removeFile :" + aFile.toString());
		files.remove(aFile.getPath());

		List path = getRoot().getPathToFile(aFile);
		ProjectDirectory dir = (ProjectDirectory)path.get(path.size() - 1);
		int fileIndex = dir.getIndexOfChild(aFile);
		dir.removeFile(aFile);
		fireFileRemoved(aFile, fileIndex);

		if(delete) {
			aFile.toFile().delete();
		}

		pruneDirectories(path, false);
	}

	/** Remove the specified directory.
	 *
	 * @param  aDir  Description of Parameter
	 * @since
	 */
	public void removeDirectory(ProjectDirectory aDir) {
		removeDirectory(aDir, false);
	}

	/** Removes the specified directory, and optionally delete all of its
	 *  contents from disk.
	 *
	 * @param  aDir    Description of Parameter
	 * @param  delete  Description of Parameter
	 */
	public void removeDirectory(ProjectDirectory aDir, boolean delete) {
		//Log.log(Log.DEBUG, this, "removeDirectory :" + aDir.toString());
		Iterator it;
		// remove all files from this dir
		it = aDir.safeFileIterator();
		while(it.hasNext()) {
			//removeFile((ProjectFile)it.next());
			ProjectFile aFile = (ProjectFile)it.next();
			//Log.log(Log.DEBUG, this, "removeFile :" + aFile.toString());
			files.remove(aFile.getPath());
			int fileIndex = aDir.getIndexOfChild(aFile);
			aDir.removeFile(aFile);
			fireFileRemoved(aFile, fileIndex);

			if(delete) {
				aFile.toFile().delete();
			}
		}
		// repeat it for all subdirs
		it = aDir.safeSubdirIterator();
		while(it.hasNext()) {
			removeDirectory((ProjectDirectory)it.next(), delete);
		}

		List path = getRoot().getPathToDirectory(aDir);
		ProjectDirectory parent = (ProjectDirectory)path.get(path.size() - 1);
		int childIndex = parent.getIndexOfChild(aDir);
		//Log.log( Log.DEBUG, this, "Removing Directory " + aDir.getPath() + " childIndex is " + childIndex);
		parent.removeDirectory(aDir);
		fireDirectoryRemoved(aDir, childIndex);

		if(delete) {
			aDir.toFile().delete();
		}
		//pruneDirectories(path,delete);
	}

	/** Remove all project files.
	 *
	 * @since
	 */
	public void removeAllFiles() {
		while(getRoot().getChildCount() != 0) {
			Object child = getRoot().getChild(0);

			if(child instanceof ProjectFile) {
				ProjectFile each = (ProjectFile)child;
				int childIndex = getRoot().getIndexOfChild(each);
				//Log.log( Log.DEBUG, this, "Removing File " + each.getPath() );
				getRoot().removeFile(each);
				files.remove(each.getPath());
				fireFileRemoved(each, childIndex);

			}
			else {
				ProjectDirectory each = (ProjectDirectory)child;
				int childIndex = getRoot().getIndexOfChild(each);
				//Log.log( Log.DEBUG, this, "Removing Directory " + each.getPath() );
				getRoot().removeDirectory(each);
				fireDirectoryRemoved(each, childIndex);
			}
		}
		files.clear();
	}

	/** Returns an iteration of project files.
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public Iterator projectFiles() {
		return files.values().iterator();
	}

	/** Returns the name of the project.
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public String toString() {
		return getName();
	}

	/** Add a project listener.
	 *
	 * @param  listener  The feature to be added to the ProjectListener attribute
	 * @since
	 */
	public void addProjectListener(ProjectListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	/** Remove a project listener.
	 *
	 * @param  listener  Description of Parameter
	 * @since
	 */
	public void removeProjectListener(ProjectListener listener) {
		listeners.remove(listener);
	}

	/** Handle any buffer updates or closes and notify the Project Viewer instance
	 *  that is running.
	 *
	 * @param  message  Description of Parameter
	 * @since
	 */
	public void handleMessage(EBMessage message) {
		if(message instanceof BufferUpdate) {
			BufferUpdate update = (BufferUpdate)message;
			ProjectFile file = getFile(update.getBuffer().getPath());
			if(update.getWhat().equals(BufferUpdate.LOADED)) {
				Log.log(Log.DEBUG, this, "BufferUpdate(LOADED) -> "+file );
				if(file != null) fireFileOpened(file);
			}
			if(update.getWhat().equals(BufferUpdate.CLOSED)) {
				Log.log(Log.DEBUG, this, "BufferUpdate(CLOSED) -> "+file );
				if(file != null) fireFileClosed(file);
			}
        }
		else if (message instanceof EditPaneUpdate) {
            EditPaneUpdate update = (EditPaneUpdate)message;
            //if ((update.getWhat().equals(EditPaneUpdate.BUFFER_CHANGED)) || (update.getWhat().equals(EditPaneUpdate.CREATED))) {
            if (update.getWhat().equals(EditPaneUpdate.BUFFER_CHANGED)) {
				ProjectFile file = getFile(update.getEditPane().getBuffer().getPath());
				Log.log(Log.DEBUG, this, "EditPaneUpdate(BUFFER_CHANGED) -> "+file );
				if(file != null) fireFileOpened(file);				
			}
            if (update.getWhat().equals(EditPaneUpdate.CREATED)) {
				ProjectFile file = getFile(update.getEditPane().getBuffer().getPath());
				Log.log(Log.DEBUG, this, "EditPaneUpdate(CREATED) -> "+file );
				if(file != null) fireFileOpened(file);				
			}
		}
		else if (message instanceof ViewUpdate) {
			ViewUpdate update = (ViewUpdate)message;
			if (update.getWhat().equals(ViewUpdate.EDIT_PANE_CHANGED)) {
				ProjectFile file = getFile(update.getView().getEditPane().getBuffer().getPath());
				Log.log(Log.DEBUG, this, "ViewUpdate -> "+file );
				if(file != null) fireFileOpened(file);				
			}
		}
	}

	/** Returns <code>true</code> if the given path denotes a file that falls under
	 *  this project's root directory and is not already a project file.
	 *
	 * @param  path  Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	public boolean canAddInProject(String path) {
		return !isProjectFile(path) && path.startsWith(getRoot().getPath());
	}

	/** Loads the project from the config file. */
	public void load() {
		// load everytime -- state is save on project change and restored on project
		// open, state can change more than once while jEdit is running
		//if ( isLoaded || isKeyUnset() )
		//   return;

		Properties fileProps = null;

		openFiles.clear();
		files.clear();
	
		try {
			fileProps = ProjectManager.load("projects/project" + key + ".properties");
		}
		catch(IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
			return;
		}

		Log.log( Log.DEBUG, this, "load() chk2 root=" + this.getRoot());
		/*  Bug fix for issue where 
		    users are unable to create projects when selector is not
		    on all projects 
		*/ 
		if (this.getRoot() == null) {
		// MattP should follow this case when loading from an existing project
			setRoot(new ProjectDirectory(fileProps.getProperty("root")));
		}
		else {
		// MattP: since file info does not exist on disk(yet) for new
		// project, we are getting path via
		// the state was set by the createproject wrapper
			setRoot(new ProjectDirectory(this.root.getPath()));
		}
		Log.log( Log.DEBUG, this, "load() chk3 root=" + this.getRoot());

		Enumeration pname = fileProps.propertyNames();
		while(pname.hasMoreElements()) {
			String p = (String)pname.nextElement();

			if(p.startsWith("file")) {
				ProjectFile file = new ProjectFile(fileProps.getProperty(p));
				if(!ProjectViewerConfig.getInstance().getDeleteNotFoundFiles() || file.exists()) {
					importFile(file);
				}
			}
			else if(p.startsWith("open_file")) {
				openFiles.add(fileProps.getProperty(p));
			}
			else if(p.startsWith("lastFile")) {
				lastFile = fileProps.getProperty(p);
			}
			else if(p.startsWith("webroot")) {
				setURLRoot(fileProps.getProperty(p));
			}
			else if(p.startsWith("folderTreeState")) {
				folderTreeState = fileProps.getProperty(p);
			}
			else if(p.startsWith("tabState")) {
				setTabState(Integer.parseInt(fileProps.getProperty(p)));
			}
			else if(p.startsWith("buildFile")) {
				buildFile = new File(fileProps.getProperty(p));
			}
		}
		if(lastFile != null)
			openFiles.add(lastFile);

		setLoaded(true);
	}

	/** Save the project to a file on the disk. */
	public void save() {
		Log.log(Log.DEBUG, this, "save()");
		if(!isLoaded)
			return;

		// Do we have the index of the project in ProjetManager?
		// There should be a better way to do this, but...
		if(isKeyUnset()) {
			ProjectManager.getInstance().save();
			return;
		}

		synchronized(this) {
			try {
				File f =
						new File(ProjectPlugin.getResourcePath(
						"projects/project" + key + ".properties"
						)
						);
				f.createNewFile();

				PrintWriter out = null;
				try {
					out = new PrintWriter(
							new OutputStreamWriter(
							new FileOutputStream(f),
							"ISO-8859-1"
							)
							);
				}
				catch(UnsupportedEncodingException uee) {
					// Not likely.
				}
				out.println("# Project " + getName() + " configuration");

				// Project Root
				out.println("root=" + escape(root.getPath()));

				// URL Root
				out.println("webroot=" + getURLRoot());

				// List of project files
				int counter = 1;
				for(Iterator i = projectFiles(); i.hasNext(); ) {
					out.println(
							"file." + counter + "=" +
							escape(((ProjectFile)i.next()).getPath())
							);
					counter++;
				}

				// List of open files
				if(openFiles.size() > 0) {
					for(int i = 0; i < openFiles.size(); i++) {
						out.println(
								"open_files." + (i + 1) + "=" +
								escape(openFiles.get(i).toString())
								);
					}
				}

				if(lastFile != null) {
					out.println("lastFile=" + lastFile);
				}

				// Folder tree state
				if(folderTree != null) {
					try {
						int row_count = folderTree.getRowCount();
						StringBuffer map = new StringBuffer();
						if(folderTree.isExpanded(0)) {
							for(int i = 1; i < row_count; i++) {
								if(folderTree.isCollapsed(i)) {
									map.append(COLLAPSED);
								}
								else if(folderTree.isExpanded(i)) {
									map.append(EXPANDED);
								}
								else {
									map.append(LEAF);
								}
							}
						}
						out.println("folderTreeState=" + map.toString());
					}
					catch(Exception ignored) {}
				}

				// Tab state
				out.println("tabState=" + String.valueOf(getTabState()));

				// Build file
				if(buildFile != null)
					out.println("buildFile=" + buildFile.getAbsolutePath());

				// Finishing
				out.flush();
				out.close();

			}
			catch(IOException ioe) {
				Log.log(Log.ERROR, this, ioe);
			}
		}
	}

	/** Clears the open files list. */
	public void clearOpenFiles() {
		openFiles.clear();
	}

	/** Adds a file to the open files list.
	 *
	 * @param  aPath  The feature to be added to the OpenFile attribute
	 */
	public void addOpenFile(String aPath) {
		if(isProjectFile(aPath)) {
			openFiles.add(aPath);
		}
	}

	/** Sets the folderTree attribute of the Project object
	 *
	 * @param  folderTree  The folderTree from ProjectViewer
	 */
	protected void setFolderTree(JTree folderTree) {
		this.folderTree = folderTree;
	}

	/** Sets the tabState attribute of the Project object
	 *
	 * @param  state  The new tabState value
	 */
	protected void setTabState(int state) {
		switch (state) {
			case ProjectViewer.FOLDERS_TAB:
			case ProjectViewer.FILES_TAB:
			case ProjectViewer.WORKING_FILES_TAB:
				tabState = state;
				break;
			default:
				tabState = ProjectViewer.FOLDERS_TAB;
		}
	}

	/** Gets the tabState attribute of the Project object
	 *
	 * @return    The tabState value
	 */
	protected int getTabState() {
		return tabState;
	}

	/** Restores the expanded state of the folder tree.
	 *
	 * @param  tree  the folder tree from ProjectViewer
	 */
	protected void restoreFolderTreeState(JTree tree) {
		try {
			if(tree == null)
				return;
			folderTree = tree;
			if(folderTreeState == null || folderTreeState.length() == 0)
				return;
			for(int i = 0; i < folderTreeState.length(); i++) {
				char state = folderTreeState.charAt(i);
				if(state == COLLAPSED) {
					tree.collapseRow(i + 1);
				}
				else {
					tree.expandRow(i + 1);
				}
			}
		}
		catch(Exception ignored) {}
	}

	/** Set the project's key.
	 *
	 * @param  aKey  The new key value
	 * @since
	 */
	void setKey(int aKey) {
		key = aKey;
	}

	/** Returns <code>true</code> if the project's key is unset.
	 *
	 * @return    The keyUnset value
	 * @since
	 */
	boolean isKeyUnset() {
		return key == PROJECT_KEY_UNSET;
	}

	/** Returns the project key.
	 *
	 * @return    The key value
	 * @since
	 */
	int getKey() {
		return key;
	}

	/** Activate this project. This method will cause the project to dispatch
	 *  project events based on jEdit application events.
	 *
	 * @since
	 */
	void activate() {
		EditBus.addToBus(this);
	}

	/** Deactivate this proejct. This method will cause the project to stop
	 *  dispatching project events.
	 *
	 * @since
	 */
	void deactivate() {
		save();
		EditBus.removeFromBus(this);
	}

	//-- fire events
	
	/** Fire notification that a project file has been opened.
	 *
	 * @param  aFile  Description of Parameter
	 * @since
	 */
	private void fireFileOpened(ProjectFile aFile) {
		//Log.log( Log.DEBUG, this, "fireFileOpened("+aFile.toString()+"), listeners.size()="+listeners.size() );
		if(listeners.size() > 0) {
			ProjectEvent evt = new ProjectEvent(this, aFile);
			for(int i = 0; i < listeners.size(); i++) {
				//Log.log( Log.DEBUG, this, "  "+i+" "+((ProjectListener)listeners.get(i)).toString());
				((ProjectListener)listeners.get(i)).fileOpened(evt);
			}
		}
	}

	/** Fire notification that a project file has been closed.
	 *
	 * @param  aFile  Description of Parameter
	 * @since
	 */
	private void fireFileClosed(ProjectFile aFile) {
		ProjectEvent evt = new ProjectEvent(this, aFile);
		for(int i = 0; i < listeners.size(); i++)
			((ProjectListener)listeners.get(i)).fileClosed(evt);
	}

	/** Fire notification that a project file has been removed.
	 *
	 * @param  aFile  Description of Parameter
	 * @param  index  Description of Parameter
	 * @since
	 */
	private void fireFileRemoved(ProjectFile aFile, int index) {
		ProjectEvent evt = new ProjectEvent(this, aFile, index);
		for(int i = 0; i < listeners.size(); i++)
			((ProjectListener)listeners.get(i)).fileRemoved(evt);
	}

	/** Fire notification that a project file has been added.
	 *
	 * @param  aFile  Description of Parameter
	 * @since
	 */
	private void fireFileAdded(ProjectFile aFile) {
		ProjectEvent evt = new ProjectEvent(this, aFile);
		//Log.log( Log.DEBUG, this, "Firing file added: file(" + aFile + ")" );
		for(int i = 0; i < listeners.size(); i++)
			((ProjectListener)listeners.get(i)).fileAdded(evt);
	}

	/** Fire notification that a build file has been selected
	 *
	 * @param  aFile  Description of Parameter
	 */
	private void fireBuildFileSelected(File aFile) {
		ProjectEvent evt = new ProjectEvent(this, aFile);
		for(int i = 0; i < listeners.size(); i++)
			((ProjectListener)listeners.get(i)).buildFileSelected(evt);
	}

	/** Fire notification that a project directory has been added.
	 *
	 * @param  aDirectory  Description of Parameter
	 * @since
	 */
	private void fireDirectoryAdded(ProjectDirectory aDirectory) {
		ProjectEvent evt = new ProjectEvent(this, aDirectory);
		for(int i = 0; i < listeners.size(); i++)
			((ProjectListener)listeners.get(i)).directoryAdded(evt);
	}

	/** Fire notification that a project directory has been removed.
	 *
	 * @param  aDirectory  Description of Parameter
	 * @param  index       Description of Parameter
	 * @since
	 */
	private void fireDirectoryRemoved(ProjectDirectory aDirectory, int index) {
		ProjectEvent evt = new ProjectEvent(this, aDirectory, index);
		for(int i = 0; i < listeners.size(); i++)
			((ProjectListener)listeners.get(i)).directoryRemoved(evt);
	}

	//-- helper

	/** Returns a project file identified by the specified path.
	 *
	 * @param  aFilePath  Description of Parameter
	 * @return            The file value
	 * @since
	 */
	private ProjectFile getFile(String aFilePath) {
		return (ProjectFile)files.get(aFilePath);
	}

	/** Prune the directories in the path. Optionally, deletes the empty
	 *  directories from disk.
	 *
	 * @param  path    Path to the desired directory.
	 * @param  delete  If empty directories should be removed from disk also.
	 */
	private void pruneDirectories(List path, boolean delete) {
		for(int i = path.size() - 1; i > 0; i--) {
			ProjectDirectory each = (ProjectDirectory)path.get(i);
			if(each.getChildCount() != 0)
				return;

			ProjectDirectory parent = (ProjectDirectory)path.get(i - 1);
			int dirIndex = parent.getIndexOfChild(each);
			parent.removeDirectory(each);

			if(delete)
				each.toFile().delete();

			fireDirectoryRemoved(each, dirIndex);
		}
	}

	/** Find the given file's subdirectory.
	 *
	 * @param  aFile  Description of Parameter
	 * @return        <code>aFile</code>'s sub project directory, or <code>null</code>
	 *      if the project file does not exist in a subdirectory of the project's
	 *      root.
	 * @since
	 */
	private ProjectDirectory findDirectory(ProjectFile aFile) {
		List path = getRoot().getPathToFile(aFile.toFile());
		if(path == null)
			return null;

		ProjectDirectory dir = getRoot();
		for(int i = 1; i < path.size(); i++) {
			File each = (File)path.get(i);
			if(each.isFile())
				break;
			if(dir.isSubDirectory(each)) {
				dir = dir.getSubDirectory(each);
			}
			else {
				dir = dir.addSubDirectory(each);
				fireDirectoryAdded(dir);
			}
		}
		return dir;
	}

	/** Escape the backslashes in Win32 paths.
	 *
	 * @param  str  Description of Parameter
	 * @return      Description of the Returned Value
	 */
	private String escape(String str) {
		if(str == null)
			return "";
		StringBuffer buf = new StringBuffer(str);
		for(int i = 0; i < buf.length(); i++) {
			if(buf.charAt(i) == '\\')
				buf.replace(i, ++i, "\\\\");
		}
		return buf.toString();
	}

	// a unit test for Fin
	private void testEscape() {
		// under jdk1.4/linux I (ensonic) get :
		// warning: as of release 1.4, assert is a keyword, and may not be used as an identifier
		//assert escape(null) != null;
		//assert escape(null).equals("");
		//assert escape("c:\\temp\\file.txt").equals("c:\\\\temp\\\\file.txt");
		//assert escape("/tmp/file.txt").equals("/tmp/file.txt");
	}
}

