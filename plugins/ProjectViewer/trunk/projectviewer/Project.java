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

import java.io.File;
import java.util.*;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

import projectviewer.event.*;

/**
 *  A project.
 *
 */
public class Project
		 implements EBComponent {

	protected final static int PROJECT_KEY_UNSET = -1;

	private ProjectDirectory root;
	private int key;
	private String name;
	private Map files;
	private List listeners;

	/** Create a new <code>Project</code>.
	 *
	 *@param  name  Description of Parameter
	 *@param  root  Description of Parameter
	 *@since
	 */
	public Project(String name, ProjectDirectory root) {
		this(name, root, PROJECT_KEY_UNSET);
	}

	/** Create a new <code>Project</code>. <p>
	 *
	 *  Note: This is a constructor intended to be used internally.</p>
	 *
	 *@param  aName  Description of Parameter
	 *@param  aRoot  Description of Parameter
	 *@param  aKey   Description of Parameter
	 *@since
	 */
	Project(String aName, ProjectDirectory aRoot, int aKey) {
		name = aName;
		root = aRoot;
		key = aKey;
		files = new HashMap();
		listeners = new ArrayList();
	}

	/** Returns the project root.
	 *
	 *@return    The root value
	 *@since
	 */
	public ProjectDirectory getRoot() {
		return root;
	}

	/**  Returns the name of the project.
	 *
	 *@return    The name value
	 *@since
	 */
	public String getName() {
		return name;
	}

	/** Returns <code>true</code> if the specified file identifies a file thata is
	 *  in this project.
	 *
	 *@param  aFile  Description of Parameter
	 *@return        The projectFile value
	 *@since
	 */
	public boolean isProjectFile(ProjectFile aFile) {
		return files.containsKey(aFile.getPath());
	}

	/** Returns <code>true</code> if the specified path identifies a file in this
	 *  project.
	 *
	 *@param  aFilePath  Description of Parameter
	 *@return            The projectFile value
	 *@since
	 */
	public boolean isProjectFile(String aFilePath) {
		return files.containsKey(aFilePath);
	}

	/** Returns <code>true</code> if the given object is a project artifact ( file
	 *  or directory ).
	 *
	 *@param  obj  Description of Parameter
	 *@return      The projectArtifact value
	 *@since
	 */
	public boolean isProjectArtifact(Object obj) {
		if (obj instanceof ProjectFile && isProjectFile(((ProjectFile) obj).getPath()))
			return true;
		if (obj instanceof ProjectDirectory)
			return getRoot().getPathToFile(((ProjectDirectory) obj).toFile()) != null;
		return false;
	}

	/** Import the specified files.
	 *
	 *@param  files  Description of Parameter
	 *@since
	 */
	public void importFiles(List files) {
		for (Iterator i = files.iterator(); i.hasNext(); )
			importFile((ProjectFile) i.next());
	}

	/** Import this given file.
	 *
	 *@param  aFile  Description of Parameter
	 *@since
	 */
	public synchronized void importFile(ProjectFile aFile) {
		if (isProjectFile(aFile))
			return;

		if (aFile.isKeyUnset()) {
			aFile.setKey(files.size() + 1);
		}
		ProjectDirectory dir = findDirectory(aFile);
		dir.addFile(aFile);
		fireFileAdded(aFile);
		files.put(aFile.getPath(), aFile);
	}

	/** Remove the specified project file.
	 *
	 *@param  aFile  Description of Parameter
	 *@since
	 */
	public void removeFile(ProjectFile aFile) {
		Log.log(Log.DEBUG, this, "removeFile :" + aFile.toString());
		files.remove(aFile.getPath());

		List path = getRoot().getPathToFile(aFile);
		ProjectDirectory dir = (ProjectDirectory) path.get(path.size() - 1);
		int fileIndex = dir.getIndexOfChild(aFile);
		dir.removeFile(aFile);
		fireFileRemoved(aFile, fileIndex);
		pruneDirectories(path);
	}

	/** Remove the specified directory.
	 *
	 *@since
	 */
	public void removeDirectory(ProjectDirectory aDir) {
		Log.log(Log.DEBUG, this, "removeDirectory :" + aDir.toString());
		Iterator it;
		// remove all files from this dir
		it=aDir.files();
		while(it.hasNext()) {
			//removeFile((ProjectFile)it.next());
			ProjectFile aFile=(ProjectFile)it.next();
			Log.log(Log.DEBUG, this, "removeFile :" + aFile.toString());
			files.remove(aFile.getPath());
			int fileIndex = aDir.getIndexOfChild(aFile);
			aDir.removeFile(aFile);
			fireFileRemoved(aFile, fileIndex);
			it=aDir.files();
		}
		// repeat it for all subdirs
		it=aDir.subdirectories();
		while(it.hasNext()) {
			removeDirectory((ProjectDirectory)it.next());
			it=aDir.subdirectories();
		}
		
		List path = getRoot().getPathToDirectory(aDir);
		ProjectDirectory parent = (ProjectDirectory) path.get(path.size() - 1);
		int childIndex = parent.getIndexOfChild(aDir);
		Log.log( Log.DEBUG, this, "Removing Directory " + aDir.getPath() + " childIndex is " + childIndex);
		parent.removeDirectory(aDir);
		fireDirectoryRemoved(aDir, childIndex);
		//pruneDirectories(path);
	}

	/** Remove all project files.
	 *
	 *@since
	 */
	public void removeAllFiles() {
		while (getRoot().getChildCount() != 0) {
			Object child = getRoot().getChild(0);

			if (child instanceof ProjectFile) {
				ProjectFile each = (ProjectFile) child;
				int childIndex = getRoot().getIndexOfChild(each);
				//Log.log( Log.DEBUG, this, "Removing File " + each.getPath() );
				getRoot().removeFile(each);
				files.remove(each.getPath());
				fireFileRemoved(each, childIndex);

			}
			else {
				ProjectDirectory each = (ProjectDirectory) child;
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
	 *@return    Description of the Returned Value
	 *@since
	 */
	public Iterator projectFiles() {
		return files.values().iterator();
	}

	/**
	 *  Returns the name of the project.
	 *
	 *@return    Description of the Returned Value
	 *@since
	 */
	public String toString() {
		return getName();
	}

	/**
	 *  Add a project listener.
	 *
	 *@param  listener  The feature to be added to the ProjectListener attribute
	 *@since
	 */
	public void addProjectListener(ProjectListener listener) {
		listeners.add(listener);
	}

	/**
	 *  Remove a project listener.
	 *
	 *@param  listener  Description of Parameter
	 *@since
	 */
	public void removeProjectListener(ProjectListener listener) {
		listeners.remove(listener);
	}

	/**
	 *  Handle any buffer updates or closes and notify the Project Viewer instance
	 *  that is running.
	 *
	 *@param  message  Description of Parameter
	 *@since
	 */
	public void handleMessage(EBMessage message) {
		if (!(message instanceof BufferUpdate))
			return;

		BufferUpdate update = (BufferUpdate) message;

		if (update.getWhat().equals(BufferUpdate.LOADED)) {
			ProjectFile file = getFile(update.getBuffer().getPath());
			if (file != null)
				fireFileOpened(file);
		}

		if (update.getWhat().equals(BufferUpdate.CLOSED)) {
			ProjectFile file = getFile(update.getBuffer().getPath());
			if (file != null)
				fireFileClosed(file);
		}
	}

	/**
	 *  Returns <code>true</code> if the given path denotes a file that falls under
	 *  this project's root directory and is not already a project file.
	 *
	 *@param  path  Description of Parameter
	 *@return       Description of the Returned Value
	 *@since
	 */
	public boolean canAddInProject(String path) {
		return !isProjectFile(path) && path.startsWith(getRoot().getPath());
	}

	/**
	 *  Set the project's key.
	 *
	 *@param  aKey  The new key value
	 *@since
	 */
	void setKey(int aKey) {
		key = aKey;
	}

	/**
	 *  Returns <code>true</code> if the project's key is unset.
	 *
	 *@return    The keyUnset value
	 *@since
	 */
	boolean isKeyUnset() {
		return key == PROJECT_KEY_UNSET;
	}

	/**
	 *  Returns the project key.
	 *
	 *@return    The key value
	 *@since
	 */
	int getKey() {
		return key;
	}

	/**
	 *  Activate this project. This method will cause the project to dispatch
	 *  project events based on jEdit application events.
	 *
	 *@since
	 */
	void activate() {
		EditBus.addToBus(this);
	}

	/**
	 *  Deactivate this proejct. This method will cause the project to stop
	 *  dispatching project events.
	 *
	 *@since
	 */
	void deactivate() {
		EditBus.removeFromBus(this);
	}

	/**
	 *  Returns a project file identified by the specified path.
	 *
	 *@param  aFilePath  Description of Parameter
	 *@return            The file value
	 *@since
	 */
	private ProjectFile getFile(String aFilePath) {
		return (ProjectFile) files.get(aFilePath);
	}

	/** Prune the directories in the path.
	 *
	 *@param  path  Description of Parameter
	 *@since
	 */
	private void pruneDirectories(List path) {
		for (int i = path.size() - 1; i > 0; i--) {
			ProjectDirectory each = (ProjectDirectory) path.get(i);
			if (each.getChildCount() != 0)
				return;

			ProjectDirectory parent = (ProjectDirectory) path.get(i - 1);
			int dirIndex = parent.getIndexOfChild(each);
			parent.removeDirectory(each);
			fireDirectoryRemoved(each, dirIndex);
		}
	}

	/**
	 *  Fire notification that a project file has been opened.
	 *
	 *@param  aFile  Description of Parameter
	 *@since
	 */
	private void fireFileOpened(ProjectFile aFile) {
		ProjectEvent evt = new ProjectEvent(this, aFile);
		for (int i = 0; i < listeners.size(); i++)
			((ProjectListener) listeners.get(i)).fileOpened(evt);
	}

	/**
	 *  Fire notification that a project file has been closed.
	 *
	 *@param  aFile  Description of Parameter
	 *@since
	 */
	private void fireFileClosed(ProjectFile aFile) {
		ProjectEvent evt = new ProjectEvent(this, aFile);
		for (int i = 0; i < listeners.size(); i++)
			((ProjectListener) listeners.get(i)).fileClosed(evt);
	}

	/**
	 *  Fire notification that a project file has been removed.
	 *
	 *@param  aFile  Description of Parameter
	 *@param  index  Description of Parameter
	 *@since
	 */
	private void fireFileRemoved(ProjectFile aFile, int index) {
		ProjectEvent evt = new ProjectEvent(this, aFile, index);
		for (int i = 0; i < listeners.size(); i++)
			((ProjectListener) listeners.get(i)).fileRemoved(evt);
	}

	/**
	 *  Fire notification that a project file has been added.
	 *
	 *@param  aFile  Description of Parameter
	 *@since
	 */
	private void fireFileAdded(ProjectFile aFile) {
		ProjectEvent evt = new ProjectEvent(this, aFile);
		//Log.log( Log.DEBUG, this, "Firing file added: file(" + aFile + ")" );
		for (int i = 0; i < listeners.size(); i++)
			((ProjectListener) listeners.get(i)).fileAdded(evt);
	}

	/**
	 *  Fire notification that a project directory has been added.
	 *
	 *@param  aDirectory  Description of Parameter
	 *@since
	 */
	private void fireDirectoryAdded(ProjectDirectory aDirectory) {
		ProjectEvent evt = new ProjectEvent(this, aDirectory);
		for (int i = 0; i < listeners.size(); i++)
			((ProjectListener) listeners.get(i)).directoryAdded(evt);
	}

	/**
	 *  Fire notification that a project directory has been removed.
	 *
	 *@param  aDirectory  Description of Parameter
	 *@param  index       Description of Parameter
	 *@since
	 */
	private void fireDirectoryRemoved(ProjectDirectory aDirectory, int index) {
		ProjectEvent evt = new ProjectEvent(this, aDirectory, index);
		for (int i = 0; i < listeners.size(); i++)
			((ProjectListener) listeners.get(i)).directoryRemoved(evt);
	}

	/**
	 *  Find the given file's subdirectory.
	 *
	 *@param  aFile  Description of Parameter
	 *@return        <code>aFile</code>'s sub project directory, or <code>null</code>
	 *      if the project file does not exist in a subdirectory of the project's
	 *      root.
	 *@since
	 */
	private ProjectDirectory findDirectory(ProjectFile aFile) {
		List path = getRoot().getPathToFile(aFile.toFile());
		if (path == null)
			return null;

		ProjectDirectory dir = getRoot();
		for (int i = 1; i < path.size(); i++) {
			File each = (File) path.get(i);
			if (each.isFile())
				break;
			if (dir.isSubDirectory(each)) {
				dir = dir.getSubDirectory(each);
			}
			else {
				dir = dir.addSubDirectory(each);
				fireDirectoryAdded(dir);
			}
		}
		return dir;
	}

}

