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
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OperatingSystem;

import projectviewer.ProjectPlugin;
//}}}

/**
 *  <p>Class to hold configuration information for the plugin.</p>
 *
 *  <p>Note about property changing events: currently, these events are only
 *  generated for the properties regarding the ProjectViewer GUI (that is,
 *  SHOW_TOOLBAR_OPT, SHOW_FOLDERS_OPT, SHOW_FILES_OPT and SHOW_WFILES_OPT).
 *  If the change of another property needs to be notified to someone, please
 *  include the call to the appropriate "firePropertyChanged" method is the
 *  setter methods of the property.</p>
 *
 *  @author     Marcelo Vanzin
 */
public final class ProjectViewerConfig {

    //{{{ Static attributes

    public static final String CONFIG_FILE = "config.properties";
	public static final String INFOVIEWER_PLUGIN = "infoviewer.InfoViewerPlugin";

    public static final String CLOSE_FILES_OPT            = "projectviewer.close_files";
    public static final String REMEBER_OPEN_FILES_OPT     = "projectviewer.remeber_open";
    public static final String DELETE_NOT_FOUND_FILES_OPT = "projectviewer.delete_files";
    public static final String IMPORT_EXTS_OPT            = "include-extensions";
    public static final String EXCLUDE_DIRS_OPT           = "exclude-dirs";
    public static final String INCLUDE_FILES_OPT          = "include-files";
    public static final String LAST_PROJECT_OPT           = "projectviewer.last-project";
	public static final String ASK_IMPORT_OPT             = "projectviewer.ask-import";
    public static final String BROWSER_PATH_OPT           = "browser-path";
	public static final String BROWSER_USE_INFOVIEWER     = "projectviewer.browser.use_infoviewer";
	public static final String USE_SYSTEM_ICONS_OPT		  = "projectviewer.use_system_icons";

    public static final String SHOW_TOOLBAR_OPT           = "projectviewer.show_toolbar";
    public static final String SHOW_FOLDERS_OPT           = "projectviewer.show_folder_tree";
    public static final String SHOW_FILES_OPT             = "projectviewer.show_files_tree";
    public static final String SHOW_WFILES_OPT            = "projectviewer.show_working_files_tree";

	public static final String USER_CONTEXT_MENU			= "projectviewer.user_context_menu";

	public static final int ASK_ALWAYS	= 0;
	public static final int ASK_ONCE	= 1;
	public static final int ASK_NEVER	= 2;

    private static final ProjectViewerConfig config = new ProjectViewerConfig();

	//}}}

    //{{{ Static methods

    /** Returns the config. */
    public static ProjectViewerConfig getInstance() {
        return config;
    }
     //}}}

	//{{{ Instance variables

    private boolean closeFiles              = true;
    private boolean rememberOpen            = true;
    private boolean deleteNotFoundFiles     = true;
	private int		askImport               = ASK_ONCE;

    private boolean showToolBar             = true;
    private boolean showFoldersTree         = true;
    private boolean showFilesTree           = true;
    private boolean showWorkingFilesTree    = true;
	private boolean useInfoViewer			= false;
	private boolean useSystemIcons			= false;

    private String importExts               = null;
    private String excludeDirs              = null;
    private String includeFiles             = null;
    private String lastProject              = null;
    private String browserPath		        = "mozilla";

	private String userContextMenu			= null;

    private ArrayList listeners;

    //}}}

    //{{{ Constructors

    /**
     *  <p>Initializes the configuration using the properties available
     *  in the object passed.</p>
     *
     *  @param  props   An object containing the configuration of the plugin.
     */
    private ProjectViewerConfig() {
		// loads the properties
		Properties props = null;
		try {
			props = new Properties();
			InputStream is = ProjectPlugin.getResourceAsStream("config.properties");
			props.load(is);
		} catch (Exception e) {
			// Ignores errors
			Log.log(Log.WARNING, ProjectViewerConfig.class, "Cannot read config file.");
		}

		if (props == null) {
			props = new Properties();
		}

		// Sees if the import options are set. If not, tries to load the old
		// configuration file. If it does not exists, uses the file included
		// with the plugin as defaults.
		if (props.get(IMPORT_EXTS_OPT) == null) {

			InputStream is = ProjectPlugin.getResourceAsStream("import.properties");
			if (is == null) {
				is = ProjectViewerConfig.class.getResourceAsStream("/projectviewer/import-sample.properties");
			}
			if (is != null) {
				Properties tmp = new Properties();
				try {
					tmp.load(is);
					props.putAll(tmp);
				} catch (IOException ioe) {
					props.setProperty(IMPORT_EXTS_OPT, "");
					props.setProperty(EXCLUDE_DIRS_OPT, "");
					props.setProperty(INCLUDE_FILES_OPT, "");
				} finally {
					try { is.close(); } catch (Exception e) { }
				}
			}
		}
		
		// instance initialization
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

		// ask_import
        tmp = props.getProperty(ASK_IMPORT_OPT);
        if (tmp != null) {
			try {
				setAskImport(Integer.parseInt(tmp));
			} catch (NumberFormatException nfe) {
				// ignore
			}
        }

        // use_system_icons
        tmp = props.getProperty(USE_SYSTEM_ICONS_OPT);
        if (tmp != null) {
            setUseSystemIcons("true".equalsIgnoreCase(tmp));
        }

        // Importing options
        importExts   = props.getProperty(IMPORT_EXTS_OPT);
        excludeDirs  = props.getProperty(EXCLUDE_DIRS_OPT);
        includeFiles = props.getProperty(INCLUDE_FILES_OPT);

        // Last opened project
        lastProject  = props.getProperty(LAST_PROJECT_OPT);

        // BrowserPath
        tmp = props.getProperty(BROWSER_PATH_OPT);
        if (tmp != null) {
			browserPath = tmp;
		}

        // browser.use_infoviewer
        tmp = props.getProperty(BROWSER_USE_INFOVIEWER);
        if (tmp != null && jEdit.getPlugin(INFOVIEWER_PLUGIN) != null) {
            setUseInfoViewer("true".equalsIgnoreCase(tmp));
        }

		// projectviewer.user_context_menu
		tmp = props.getProperty(USER_CONTEXT_MENU);
        if (tmp != null) {
            setUserContextMenu(tmp);
        }

    }

	//}}}

	//{{{ Properties (Getters and Setters)

    public void setCloseFiles(boolean closeFiles) {
        this.closeFiles = closeFiles;
    }

    public void setDeleteNotFoundFiles(boolean deleteNotFoundFiles) {
        this.deleteNotFoundFiles = deleteNotFoundFiles;
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

	// {{{ property useInfoViewer
	public void setUseInfoViewer(boolean useInfoViewer) {
		this.useInfoViewer = useInfoViewer;
	}

	public boolean getUseInfoViewer() {
		return useInfoViewer;
	}
	// }}}

	// {{{ property userContextMenu
	public void setUserContextMenu(String userContextMenu) {
		this.userContextMenu = userContextMenu;
	}

	public String getUserContextMenu() {
		return userContextMenu;
	}
	// }}}

	// {{{ property useSystemIcons
	public void setUseSystemIcons(boolean useSystemIcons) {
		this.useSystemIcons = useSystemIcons && OperatingSystem.hasJava14();
	}

	public boolean getUseSystemIcons() {
		return useSystemIcons;
	}
	// }}}

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

    //{{{ update(Properties) method
    /**
     *  <p>Updates the properties in the properties object passed to
     *  reflect the current state of the config.</p>
     */
    public void update(Properties props) {
        props.setProperty(CLOSE_FILES_OPT, String.valueOf(closeFiles));
        props.setProperty(REMEBER_OPEN_FILES_OPT, String.valueOf(rememberOpen));
        props.setProperty(DELETE_NOT_FOUND_FILES_OPT, String.valueOf(deleteNotFoundFiles));
		props.setProperty(ASK_IMPORT_OPT, String.valueOf(askImport));
		props.setProperty(USE_SYSTEM_ICONS_OPT, String.valueOf(useSystemIcons));

        props.setProperty(SHOW_TOOLBAR_OPT, String.valueOf(showToolBar));
        props.setProperty(SHOW_FOLDERS_OPT, String.valueOf(showFoldersTree));
        props.setProperty(SHOW_FILES_OPT, String.valueOf(showFilesTree));
        props.setProperty(SHOW_WFILES_OPT, String.valueOf(showWorkingFilesTree));

        props.setProperty(IMPORT_EXTS_OPT, importExts);
        props.setProperty(EXCLUDE_DIRS_OPT, excludeDirs);
        props.setProperty(INCLUDE_FILES_OPT, includeFiles);

		props.setProperty(BROWSER_PATH_OPT, String.valueOf(browserPath));
        props.setProperty(BROWSER_USE_INFOVIEWER, String.valueOf(useInfoViewer));

		if (userContextMenu != null) {
			props.setProperty(USER_CONTEXT_MENU, userContextMenu);
		}

        if (lastProject != null) {
            props.setProperty(LAST_PROJECT_OPT, lastProject);
        }
    } //}}}

    //{{{ save() method
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

    } //}}}

	//{{{ isJEdit42()
	/** Returns whether we're using jEdit 4.2 (pre5 or better). */
	public boolean isJEdit42() {
		return MiscUtilities.compareStrings(jEdit.getBuild(), "04.02.05.00", false) >= 0;
	} //}}}

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

