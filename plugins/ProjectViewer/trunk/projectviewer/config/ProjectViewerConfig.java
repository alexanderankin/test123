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

// Import Java
import java.util.Properties;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

// Import jEdit
import org.gjt.sp.util.Log;
import projectviewer.ProjectPlugin;
import projectviewer.ProjectManager;

/**
 *  <p>Class to hold configuration information for the plugin.</p>
 *
 *  @author     Marcelo Vanzin
 */
public final class ProjectViewerConfig {
    
    //-------------- Static attributes
    
    public static final String CONFIG_FILE = "config.properties";
    
    private static final String CLOSE_FILES_OPT = "projectviewer.close_files";
    private static final String REMEBER_OPEN_FILES_OPT = "projectviewer.remeber_open";
    private static final String DELETE_NOT_FOUND_FILES_OPT = "projectviewer.delete_files";
    private static final String SAVE_ON_CHANGE_OPT = "projectviewer.save_on_change";
    private static final String IMPORT_EXTS_OPT = "include-extensions";
    private static final String EXCLUDE_DIRS_OPT = "exclude-dirs";
    private static final String INCLUDE_FILES_OPT = "include-files";
    private static final String LAST_PROJECT_OPT = "projectviewer.last-project";
    
    private static ProjectViewerConfig config;
    
    //-------------- Static methods
    
    /** Returns the config. */
    public static synchronized ProjectViewerConfig getInstance() {
        if (config == null) {
            Properties p = null;
            try {
                p = ProjectManager.load(CONFIG_FILE);
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
    
    //-------------- Instance variables
    
    private boolean closeFiles          = true;
    private boolean rememberOpen        = true;
    private boolean deleteNotFoundFiles = true;
    private boolean saveOnChange        = true;
    
    private String importExts           = null;
    private String excludeDirs          = null;
    private String includeFiles         = null;
    private String lastProject          = null; 
    
    
    //-------------- Constructors
    
    /**
     *  <p>Initializes the configuration using the properties available
     *  in the object passed.</p>
     *
     *  @param  props   An object containing the configuration of the plugin.
     */
    private ProjectViewerConfig(Properties props) {
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
        
        // Importing options
        importExts   = props.getProperty(IMPORT_EXTS_OPT);  
        excludeDirs  = props.getProperty(EXCLUDE_DIRS_OPT);
        includeFiles = props.getProperty(INCLUDE_FILES_OPT);
        
        // Last opened project
        lastProject  = props.getProperty(LAST_PROJECT_OPT);
    }
    
    //-------------- Properties

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
    
    //-------------- Methods

    /**
     *  <p>Updates the properties in the properties object passed to
     *  reflect the current state of the config.</p>
     */
    public void update(Properties props) {
        props.setProperty(CLOSE_FILES_OPT, String.valueOf(closeFiles));
        props.setProperty(REMEBER_OPEN_FILES_OPT, String.valueOf(rememberOpen));
        props.setProperty(DELETE_NOT_FOUND_FILES_OPT, String.valueOf(deleteNotFoundFiles));
        props.setProperty(SAVE_ON_CHANGE_OPT, String.valueOf(saveOnChange));
        
        props.setProperty(IMPORT_EXTS_OPT, importExts);
        props.setProperty(EXCLUDE_DIRS_OPT, excludeDirs); 
        props.setProperty(INCLUDE_FILES_OPT, includeFiles);
        
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
    
}
