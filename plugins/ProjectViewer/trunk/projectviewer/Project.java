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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.*;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

import projectviewer.event.*;
import projectviewer.config.ProjectViewerConfig;

/** 
 *  A project. Provides many methods to manipulate files/directories within
 *  the project, and also to save/load the project's configuration from the
 *  user's jEdit preferences directory.
 */
public final class Project implements EBComponent {

	protected final static int PROJECT_KEY_UNSET = -1;

	private ProjectDirectory root;
	private int key;
	private String name;
	private Map files;
	private String urlRoot;
	private List listeners;
    
    private boolean isLoaded;
    
    private List openFiles;

    public Project() {
        this(null, PROJECT_KEY_UNSET);
    }
    
	/** Create a new <code>Project</code>.
	 *
	 *@param  name  Description of Parameter
	 *@param  root  Description of Parameter
	 *@since
	 */
	public Project(String name) {
		this(name, PROJECT_KEY_UNSET);
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
	Project(String aName, int aKey) {
		name = aName;
		key = aKey;

		root = null;
        isLoaded = false;

		files = new HashMap();
		listeners = new ArrayList();
        openFiles = new ArrayList();
	}

	/** Returns the project root.
	 *
	 *@return    The root value
	 *@since
	 */
	public ProjectDirectory getRoot() {
		return root;
	}

    public void setRoot(ProjectDirectory root) {
        this.root = root;
    }
    
	/**  Returns the name of the project.
	 *
	 *@return    The name value
	 *@since
	 */
	public String getName() {
		return name;
	}
    
    /** 
     *  Changes the project name.
     *
     *  @param  aName   The new name.
     */
    public void setName(String aName) {
        this.name = aName;
    }

	/**
	 * Returns the WebRoot for the project
	 */
	public String getURLRoot() {
		  return urlRoot == null ? "" : urlRoot;  
	  }
   
   	public void setURLRoot(String sWebRoot) {
		urlRoot = sWebRoot;
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

		ProjectDirectory dir = findDirectory(aFile);
		//-- one might have tried to import files from not below our directory
		if(dir!=null) {
			dir.addFile(aFile);
			fireFileAdded(aFile);
			files.put(aFile.getPath(), aFile);
		}
	}

    /**
     *  Analyze the list of objects passed and removes any ProjectFile and
     *  ProjectDirectory instances found.
     *
     *  @param  objects     The list of artifacts to remove.
     *  @param  delete      If ProjectFiles are to be deleted from the disk.
     */
    public void removeArtifacts(Collection objects, boolean delete) {
        for (Iterator i = objects.iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o instanceof ProjectFile) {
                removeFile((ProjectFile)o, delete);
            } else if (o instanceof ProjectDirectory) {
                removeDirectory((ProjectDirectory)o,delete);
            }
        }
    }
    
	/** Remove the specified project file.
	 *
	 *@param  aFile  Description of Parameter
	 *@since
	 */
	public void removeFile(ProjectFile aFile) {
        removeFile(aFile, false);
    }
        
    /**
     *  Removes a file from the project. Optionally, deletes the file
     *  from disk also.
     *
     *  @param  aFile   The project file to be removed.
     *  @param  delete  If the file should be deleted from disk.
     */
    public void removeFile(ProjectFile aFile, boolean delete) {
		Log.log(Log.DEBUG, this, "removeFile :" + aFile.toString());
		files.remove(aFile.getPath());

		List path = getRoot().getPathToFile(aFile);
		ProjectDirectory dir = (ProjectDirectory) path.get(path.size() - 1);
		int fileIndex = dir.getIndexOfChild(aFile);
		dir.removeFile(aFile);
		fireFileRemoved(aFile, fileIndex);
        
        if (delete) {
            aFile.toFile().delete();
        }
        
		pruneDirectories(path,false);
	}

	/** Remove the specified directory.
	 *
	 *@since
	 */
	public void removeDirectory(ProjectDirectory aDir) {
        removeDirectory(aDir, false);
    }
    
    
    /**
     *  Removes the specified directory, and optionally delete all of its
     *  contents from disk.
     */
    public void removeDirectory(ProjectDirectory aDir, boolean delete) {
		Log.log(Log.DEBUG, this, "removeDirectory :" + aDir.toString());
		Iterator it;
		// remove all files from this dir
		it = aDir.safeFileIterator();
		while(it.hasNext()) {
			//removeFile((ProjectFile)it.next());
			ProjectFile aFile=(ProjectFile)it.next();
			Log.log(Log.DEBUG, this, "removeFile :" + aFile.toString());
			files.remove(aFile.getPath());
			int fileIndex = aDir.getIndexOfChild(aFile);
			aDir.removeFile(aFile);
			fireFileRemoved(aFile, fileIndex);
            
            if (delete) {
                aFile.toFile().delete();
            }
		}
		// repeat it for all subdirs
		it = aDir.safeSubdirIterator();
		while(it.hasNext()) {
			removeDirectory((ProjectDirectory)it.next(),delete);
	    }
		
		List path = getRoot().getPathToDirectory(aDir);
		ProjectDirectory parent = (ProjectDirectory) path.get(path.size() - 1);
		int childIndex = parent.getIndexOfChild(aDir);
		Log.log( Log.DEBUG, this, "Removing Directory " + aDir.getPath() + " childIndex is " + childIndex);
		parent.removeDirectory(aDir);
		fireDirectoryRemoved(aDir, childIndex);
        
        if (delete) {
            aDir.toFile().delete();
        }
		//pruneDirectories(path,delete);
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

	/**
     *  Prune the directories in the path. Optionally, deletes the empty 
     *  directories from disk.
	 *
	 *  @param  path    Path to the desired directory.
     *  @param  delete  If empty directories should be removed from disk also.
	 */
	private void pruneDirectories(List path, boolean delete) {
		for (int i = path.size() - 1; i > 0; i--) {
			ProjectDirectory each = (ProjectDirectory) path.get(i);
			if (each.getChildCount() != 0)
				return;

			ProjectDirectory parent = (ProjectDirectory) path.get(i - 1);
			int dirIndex = parent.getIndexOfChild(each);
			parent.removeDirectory(each);
            
            if (delete) each.toFile().delete();
            
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
        if (listeners.size() > 0) {
            ProjectEvent evt = new ProjectEvent(this, aFile);
            for (int i = 0; i < listeners.size(); i++)
                ((ProjectListener) listeners.get(i)).fileOpened(evt);
        }
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
    
    /** Returns true if the project config has already been loaded. */
    public boolean isLoaded() {
        return isLoaded;
    }
    
    /** Sets the "isLoaded" flag for this project. */
    public void setLoaded(boolean flag) {
        isLoaded = flag;
    }
    
    /** Loads the project from the config file. */
    public void load() {
        if (isLoaded || isKeyUnset()) return;
        
        Properties fileProps = null;
        
        openFiles.clear();
        files.clear();
        
        try {
            fileProps = ProjectManager.load("projects/project" + key + ".properties");
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
            return;
        }
        
        setRoot(new ProjectDirectory(fileProps.getProperty("root")));

        Enumeration pname = fileProps.propertyNames();
        while (pname.hasMoreElements()) {
         
            String p = (String) pname.nextElement();
            
            if (p.startsWith("file")) {
                ProjectFile file = new ProjectFile(fileProps.getProperty(p));
                if (!ProjectViewerConfig.getInstance().getDeleteNotFoundFiles() || 
                      file.exists()) {
                    importFile(file);
                }
            } else if (p.startsWith("open_file")) {
                openFiles.add(fileProps.getProperty(p));
            }
        }
        
        setLoaded(true);
    }
    
    /** Save the project to a file on the disk. */
    public void save() {
        if (!isLoaded) return;
        
        // Do we have the index of the project in ProjetManager?
        // There should be a better way to do this, but...
        if (isKeyUnset()) {
            ProjectManager.getInstance().save();
            return;
        }
        
        Properties p = new Properties();
        
        // Project Root
        p.setProperty("root", root.getPath());
        
        // List of open files
        if (openFiles.size() > 0) {
            for (int i = 0; i < openFiles.size(); i++) {
                p.setProperty("open_files." + (i+1), openFiles.get(i).toString());
            }
        }
        
        // List of project files
        int counter = 1;
        for ( Iterator i = projectFiles(); i.hasNext(); ) {
            p.setProperty(
                "file." + counter,
                ((ProjectFile)i.next()).getPath()
            );
            counter++;
        }
        
        // Saves the output to disk
        try {
            File f = 
                new File(
                    ProjectPlugin.getResourcePath(
                        "projects/project" + key + ".properties"
                    )
                );
            f.createNewFile();
            
            FileOutputStream out = new FileOutputStream(f);
            p.store(out, "Project " + getName() + " configuration");
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }
    }
    
    /** Clears the open files list. */
    public void clearOpenFiles() {
        openFiles.clear();
    }

    /** Adds a file to the open files list. */
    public void addOpenFile(String aPath) {
        if (isProjectFile(aPath)) {
            openFiles.add(aPath);
        }
    }
    
    /** Returns an iterator for the list of open files. */
    public Iterator getOpenFiles() {
        return openFiles.iterator();
    }
}

