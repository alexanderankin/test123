/*
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

package projectviewer.config;

//{{{ Imports
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import projectviewer.ProjectPlugin;
//}}}

/**
 *  <p>Class to hold configuration information for the plugin.</p>
 *
 *  <p>Note about property changing events: currently, these events are only
 *  generated for the properties regarding the ProjectViewer GUI (that is,
 *  SHOW_TOOLBAR_OPT, SHOW_FOLDERS_OPT, SHOW_FILES_OPT and SHOW_WFILES_OPT).
 *  If the change of another property needs to be notifies to someone, please
 *  include the call to the appropriate "firePropertyChanged" method is the
 *  setter methods of the property.</p>
 *
 *  @author     Marcelo Vanzin
 */
public final class ProjectViewerConfig {

    //{{{ Static attributes

    public static final String CONFIG_FILE = "config.properties";

    public static final String CLOSE_FILES_OPT            = "projectviewer.close_files";
    public static final String REMEBER_OPEN_FILES_OPT     = "projectviewer.remeber_open";
    public static final String DELETE_NOT_FOUND_FILES_OPT = "projectviewer.delete_files";
    public static final String SAVE_ON_CHANGE_OPT         = "projectviewer.save_on_change";
    public static final String IMPORT_EXTS_OPT            = "include-extensions";
    public static final String EXCLUDE_DIRS_OPT           = "exclude-dirs";
    public static final String INCLUDE_FILES_OPT          = "include-files";
    public static final String LAST_PROJECT_OPT           = "projectviewer.last-project";
	public static final String ASK_IMPORT_OPT             = "projectviewer.ask-import";
    public static final String BROWSER_PATH_OPT           = "browser-path";
    public static final String BROWSEABLE_EXTS_OPT        = "projectviewer.browseable-extensions";

    public static final String SHOW_TOOLBAR_OPT           = "projectviewer.show_toolbar";
    public static final String SHOW_FOLDERS_OPT           = "projectviewer.show_folder_tree";
    public static final String SHOW_FILES_OPT             = "projectviewer.show_files_tree";
    public static final String SHOW_WFILES_OPT            = "projectviewer.show_working_files_tree";

	public static final int ASK_ALWAYS	= 0;
	public static final int ASK_ONCE	= 1;
	public static final int ASK_NEVER	= 2;
	
    private static ProjectViewerConfig config;

	//}}}

    //{{{ Static methods

    /** Returns the config. */
    public static synchronized ProjectViewerConfig getInstance() {
        if (config == null) {
            Properties p = null;
            try {
                p = new Properties();
                InputStream is = ProjectPlugin.getResourceAsStream("config.properties");
                p.load(is);
            } catch (Exception e) {
                // Ignores errors
                Log.log(Log.WARNING, ProjectViewerConfig.class, "Cannot read config file.");
            }

            if (p == null) {
                p = new Properties();
            }

            // Sees if the import options are set. If not, tries to load the old
            // configuration file. If it does not exists, uses the file included
            // with the plugin as defaults.
            if (p.get(IMPORT_EXTS_OPT) == null) {

                InputStream is = ProjectPlugin.getResourceAsStream("import.properties");
                if (is == null) {
                    is = ProjectViewerConfig.class.getResourceAsStream("/projectviewer/import-sample.properties");
                }
                if (is != null) {
                    Properties tmp = new Properties();
                    try {
                        tmp.load(is);
                        p.putAll(tmp);
                    } catch (IOException ioe) {
                        p.setProperty(IMPORT_EXTS_OPT, "");
                        p.setProperty(EXCLUDE_DIRS_OPT, "");
                        p.setProperty(INCLUDE_FILES_OPT, "");
                        p.setProperty(BROWSER_PATH_OPT, "mozilla");
                    } finally {
                        try { is.close(); } catch (Exception e) { }
                    }
                }
            }

            // Finally, calls the constructor
            config = new ProjectViewerConfig(p);
        }
        return config;
    }
     //}}}

	//{{{ Instance variables

    private boolean closeFiles              = true;
    private boolean rememberOpen            = true;
    private boolean deleteNotFoundFiles     = true;
    private boolean saveOnChange            = true;
	private int		askImport               = ASK_ALWAYS;

    private boolean showToolBar             = true;
    private boolean showFoldersTree         = true;
    private boolean showFilesTree           = true;
    private boolean showWorkingFilesTree    = true;

    private String importExts               = null;
    private String excludeDirs              = null;
    private String includeFiles             = null;
    private String lastProject              = null;
    private String browserPath		        = jEdit.getProperty("projectviewer.browser__path");


    private ArrayList listeners;

    //}}}

    //{{{ Constructors

    /**
     *  <p>Initializes the configuration using the properties available
     *  in the object passed.</p>
     *
     *  @param  props   An object containing the configuration of the plugin.
     */
    private ProjectViewerConfig(Properties props) {
        listeners = new ArrayList();

        if (props == null) return;
        String tmp;

        // close_files options

        tmp = props.getProperty(CLOSE_FILES_OPT);
        if (tmp != null) {
            setCloseFiles("true".equalsIgnoreCase(tmp));
        }

        // remember_open options
        tmp = props.getProperty(REMEBER_OPEN_FILES_OPT);
        if (tmp != null) {
            setRememberOpen("true".equalsIgnoreCase(tmp));
        }

        // delete_files option
        tmp = props.getProperty(DELETE_NOT_FOUND_FILES_OPT);
        if (tmp != null) {
            setDeleteNotFoundFiles("true".equalsIgnoreCase(tmp));
        }

        // save_on_change option
        tmp = props.getProperty(SAVE_ON_CHANGE_OPT);
        if (tmp != null) {
            setSaveOnChange("true".equalsIgnoreCase(tmp));
        }

         // show_toolbar
        tmp = props.getProperty(SHOW_TOOLBAR_OPT);
        if (tmp != null) {
            setShowToolBar("true".equalsIgnoreCase(tmp));
        }

        // show_folders_tree
        tmp = props.getProperty(SHOW_FOLDERS_OPT);
        if (tmp != null) {
            setShowFoldersTree("true".equalsIgnoreCase(tmp));
        }

        // show_files_tree
        tmp = props.getProperty(SHOW_FILES_OPT);
        if (tmp != null) {
            setShowFilesTree("true".equalsIgnoreCase(tmp));
        }

        // show_working_files_tree
        tmp = props.getProperty(SHOW_WFILES_OPT);
        if (tmp != null) {
            setShowWorkingFilesTree("true".equalsIgnoreCase(tmp));
        }

		// ask_inport
        tmp = props.getProperty(ASK_IMPORT_OPT);
        if (tmp != null) {
			try {
				setAskImport(Integer.parseInt(tmp));
			} catch (NumberFormatException nfe) {
				// ignore
			}
        }
		
        // Importing options
        importExts   = props.getProperty(IMPORT_EXTS_OPT);
        excludeDirs  = props.getProperty(EXCLUDE_DIRS_OPT);
        includeFiles = props.getProperty(INCLUDE_FILES_OPT);

        // Last opened project
        lastProject  = props.getProperty(LAST_PROJECT_OPT);

        // BrowserPath
        tmp = props.getProperty(BROWSER_PATH_OPT);
        if (tmp==null)
            tmp = "mozilla";
        browserPath = props.getProperty(BROWSER_PATH_OPT);

    }

	//}}}

	//{{{ Properties (Getters and Setters)

    public void setCloseFiles(boolean closeFiles) {
        this.closeFiles = closeFiles;
    }

    public void setDeleteNotFoundFiles(boolean deleteNotFoundFiles) {
        this.deleteNotFoundFiles = deleteNotFoundFiles;
    }

    public void setSaveOnChange(boolean saveOnChange) {
        this.saveOnChange = saveOnChange;
    }

    public void setRememberOpen(boolean newRememberOpen) {
        this.rememberOpen = newRememberOpen;
    }

    public void setAskImport(int newAskImport) {
        int old = this.askImport;
        if (newAskImport > ASK_NEVER || newAskImport < ASK_ALWAYS) {
			askImport = ASK_ALWAYS;
		} else {
			this.askImport = newAskImport;
		}
		this.firePropertyChanged(ASK_IMPORT_OPT, new Integer(old), 
			new Integer(askImport));
    }

    public void setImportExts(String newImportExts) {
        this.importExts = newImportExts;
    }

    public void setExcludeDirs(String newExcludeDirs) {
        this.excludeDirs = newExcludeDirs;
    }

    public void setIncludeFiles(String newIncludeFiles) {
        this.includeFiles = newIncludeFiles;
    }

    public void setLastProject(String newLastProject) {
        this.lastProject = newLastProject;
    }

    public void setBrowserpath(String newBrowserPath) {
	  this.browserPath = newBrowserPath;
    }

    public void setShowToolBar(boolean newShowToolBar) {
        boolean old = this.showToolBar;
        this.showToolBar = newShowToolBar;
        firePropertyChanged(SHOW_TOOLBAR_OPT, old, newShowToolBar);
    }

    public void setShowFoldersTree(boolean newShowFoldersTree) {
        boolean old = this.showFoldersTree;
        this.showFoldersTree = newShowFoldersTree;
        firePropertyChanged(SHOW_FOLDERS_OPT, old, newShowFoldersTree);
    }

    public void setShowFilesTree(boolean newShowFilesTree) {
        boolean old = this.showFilesTree;
        this.showFilesTree = newShowFilesTree;
        firePropertyChanged(SHOW_FILES_OPT, old, newShowFilesTree);
    }

    public void setShowWorkingFilesTree(boolean newShowWorkingFilesTree) {
        boolean old = this.showWorkingFilesTree;
        this.showWorkingFilesTree = newShowWorkingFilesTree;
        firePropertyChanged(SHOW_WFILES_OPT, old, newShowWorkingFilesTree);
    }

    public boolean getCloseFiles() {
        return closeFiles;
    }

    public boolean getDeleteNotFoundFiles() {
        return deleteNotFoundFiles;
    }

    public boolean getSaveOnChange() {
        return saveOnChange;
    }

    public boolean getRememberOpen() {
        return rememberOpen;
    }

    public int getAskImport() {
        return askImport;
    }

    public String getImportExts() {
        return importExts;
    }

    public String getExcludeDirs() {
        return excludeDirs;
    }

    public String getIncludeFiles() {
        return includeFiles;
    }

    public String getLastProject() {
        return lastProject;
    }

    public String getBrowserPath(){
        return browserPath;
    }


    public boolean getShowToolBar() {
        return showToolBar;
    }

    public boolean getShowFoldersTree() {
        return showFoldersTree;
    }

    public boolean getShowFilesTree() {
        return showFilesTree;
    }

    public boolean getShowWorkingFilesTree() {
        return showWorkingFilesTree;
    }

    //}}}

    //{{{ Public Methods

    /**
     *  Adds a new property change listener to the list.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     *  Removes a property change listener to the list.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     *  <p>Updates the properties in the properties object passed to
     *  reflect the current state of the config.</p>
     */
    public void update(Properties props) {
        props.setProperty(CLOSE_FILES_OPT, String.valueOf(closeFiles));
        props.setProperty(REMEBER_OPEN_FILES_OPT, String.valueOf(rememberOpen));
        props.setProperty(DELETE_NOT_FOUND_FILES_OPT, String.valueOf(deleteNotFoundFiles));
        props.setProperty(SAVE_ON_CHANGE_OPT, String.valueOf(saveOnChange));
		props.setProperty(ASK_IMPORT_OPT, String.valueOf(askImport));

        props.setProperty(SHOW_TOOLBAR_OPT, String.valueOf(showToolBar));
        props.setProperty(SHOW_FOLDERS_OPT, String.valueOf(showFoldersTree));
        props.setProperty(SHOW_FILES_OPT, String.valueOf(showFilesTree));
        props.setProperty(SHOW_WFILES_OPT, String.valueOf(showWorkingFilesTree));

        props.setProperty(IMPORT_EXTS_OPT, importExts);
        props.setProperty(EXCLUDE_DIRS_OPT, excludeDirs);
        props.setProperty(INCLUDE_FILES_OPT, includeFiles);

		props.setProperty(BROWSER_PATH_OPT, String.valueOf(browserPath));

        if (lastProject != null) {
            props.setProperty(LAST_PROJECT_OPT, lastProject);
        }
    }

    /** Save the configuration to the plugin's config file on disk. */
    public void save() {
        Properties p = new Properties();
        update(p);

        OutputStream out = ProjectPlugin.getResourceAsOutputStream(CONFIG_FILE);
        if (out != null) {
            try {
                p.store(out, "Project Viewer Config File");
            } catch (Exception e) {
                // Ignore errors?
                Log.log(Log.ERROR, this, "Cannot write to config file!");
            } finally {
                try { out.close(); } catch (Exception e) {}
            }
        } else {
            Log.log(Log.ERROR, this, "Cannot write to config file!");
        }

    }

	//}}}

	//{{{ Private Methods

    /** Fires and event when a boolean property is changed. */
    private void firePropertyChanged(String property, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            firePropertyChanged(property, new Boolean(oldValue), new Boolean(newValue));
        }
    }

    /** Fires and event when a property is changed. */
    private void firePropertyChanged(String property, Object oldValue, Object newValue) {
        if (!oldValue.equals(newValue) && listeners.size() > 0) {
            PropertyChangeEvent evt =
                new PropertyChangeEvent(this,property,oldValue,newValue);
            for (Iterator i = listeners.iterator(); i.hasNext(); ) {
                ((PropertyChangeListener)i.next()).propertyChange(evt);
            }
        }
    }

	//}}}
}

