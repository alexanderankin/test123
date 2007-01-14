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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
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
import java.util.StringTokenizer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OperatingSystem;

import projectviewer.ProjectManager;
import projectviewer.ProjectPlugin;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
//}}}

/**
 *	<p>Class to hold configuration information for the plugin.</p>
 *
 *	<p>Note about property changing events: currently, these events are only
 *	generated for the properties regarding the ProjectViewer GUI (that is,
 *	SHOW_TOOLBAR_OPT, SHOW_FOLDERS_OPT, SHOW_FILES_OPT and SHOW_WFILES_OPT).
 *	If the change of another property needs to be notified to someone, please
 *	include the call to the appropriate "firePropertyChanged" method is the
 *	setter methods of the property.</p>
 *
 *	<p>Also of note is that these events are for internal ProjectViewer use
 *	and are not meant to be used by other plugins interfacing with PV.</p>
 *
 *	@author		Marcelo Vanzin
 */
public final class ProjectViewerConfig {

	//{{{ Static attributes

	public static final String CONFIG_FILE = "config.properties";
	public static final String ERRORLIST_PLUGIN = "errorlist.ErrorListPlugin";
	public static final String INFOVIEWER_PLUGIN = "infoviewer.InfoViewerPlugin";

	public static final String ASK_IMPORT_OPT			  = "projectviewer.ask-import";
	public static final String BROWSER_PATH_OPT			  = "browser-path";
	public static final String BROWSER_USE_INFOVIEWER	  = "projectviewer.browser.use_infoviewer";
	public static final String CASE_INSENSITIVE_SORT_OPT  = "projectviewer.case_insensitive_sort";
	public static final String CLOSE_FILES_OPT			  = "projectviewer.close_files";
	public static final String DELETE_NOT_FOUND_FILES_OPT = "projectviewer.delete_files";
	public static final String EXCLUDE_DIRS_OPT			  = "exclude-dirs";
	public static final String FOLLOW_BUFFER_OPT		  = "projectviewer.follow_buffer";
	public static final String IMPORT_EXTS_OPT			  = "include-extensions";
	public static final String IMPORT_GLOBS_OPT			  = "import-globs";
	public static final String INCLUDE_FILES_OPT		  = "include-files";
	public static final String LAST_NODE_OPT			  = "projectviewer.last-node.";
	public static final String LAST_PROJECT_OPT			  = "projectviewer.last-project";
	public static final String LAST_INIT_VERSION_OPT	  = "projectviewer.last-init-version";
	public static final String REMEBER_OPEN_FILES_OPT	  = "projectviewer.remeber_open";
	public static final String SHOW_PROJECT_TITLE_OPT	  = "projectviewer.show_project_in_title";
	public static final String USE_EXTERNAL_APPS_OPT	  = "projectviewer.use_external_apps";
	public static final String USE_SYSTEM_ICONS_OPT		  = "projectviewer.use_system_icons";

	public static final String SHOW_COMPACT_OPT			  = "projectviewer.show_compact_tree";
	public static final String SHOW_FILES_OPT			  = "projectviewer.show_files_tree";
	public static final String SHOW_FILTERED_OPT		  = "projectviewer.show_filtered_tree";
	public static final String SHOW_FOLDERS_OPT			  = "projectviewer.show_folder_tree";
	public static final String SHOW_TOOLBAR_OPT			  = "projectviewer.show_toolbar";
	public static final String SHOW_WFILES_OPT			  = "projectviewer.show_working_files_tree";

	public static final String USER_CONTEXT_MENU		  = "projectviewer.user_context_menu";

	public static final int ASK_ALWAYS	= 0;
	public static final int ASK_ONCE	= 1;
	public static final int ASK_NEVER	= 2;
	public static final int AUTO_IMPORT = 3;

	private static ProjectViewerConfig config = new ProjectViewerConfig();

	//}}}

	//{{{ Static methods

	/** Returns the config. */
	public static ProjectViewerConfig getInstance() {
		return config;
	}

	//}}}

	//{{{ Instance variables

	private boolean caseInsensitiveSort		= false;
	private boolean closeFiles				= true;
	private boolean rememberOpen			= true;
	private boolean deleteNotFoundFiles		= true;
	private boolean followCurrentBuffer		= true;
	private int		askImport				= ASK_ONCE;

	private boolean showToolBar				= true;
	private boolean showFoldersTree			= true;
	private boolean showFilesTree			= true;
	private boolean showWorkingFilesTree	= true;
	private boolean showCompactTree			= true;
	private boolean showFilteredTree		= true;
	private boolean showProjectInTitle		= true;
	private boolean useInfoViewer			= false;
	private boolean useExternalApps			= false;
	private boolean useSystemIcons			= false;

	private String importGlobs				= null;
	private String excludeDirs				= null;
	private String lastProject				= null;
	private String browserPath				= "mozilla";

	private String userContextMenu			= null;
	private String lastInitVersion			= null;

	private ArrayList listeners;
	private ArrayList lastNodePath;
	private VPTNode lastNode;

	//}}}

	//{{{ Constructors
	/**
	 *	<p>Initializes the configuration using the properties available
	 *	in the object passed.</p>
	 *
	 *	<p>This procedure cannot be executed in the constructor because of
	 *	some circular references due to the use of VPTNode.</p>
	 *
	 *	@param	props	An object containing the configuration of the plugin.
	 */
	private ProjectViewerConfig() {
		listeners = new ArrayList();

		// loads the properties
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = ProjectPlugin.getResourceAsStream("config.properties");
			props.load(is);
		} catch (Exception e) {
			// Ignores errors
			Log.log(Log.WARNING, ProjectViewerConfig.class, "Cannot read config file.");
		} finally {
			if (is != null) try { is.close(); } catch (Exception e) { }
		}

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

		// show_compact_tree
		tmp = props.getProperty(SHOW_COMPACT_OPT);
			setShowCompactTree("true".equalsIgnoreCase(tmp));

		// show_filtered_tree
		tmp = props.getProperty(SHOW_FILTERED_OPT);
			setShowFilteredTree("true".equalsIgnoreCase(tmp));

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

		// show_project_title
		tmp = props.getProperty(SHOW_PROJECT_TITLE_OPT);
		if (tmp != null) {
			setShowProjectInTitle("true".equalsIgnoreCase(tmp));
		}

		// case_insensitive_sort
		tmp = props.getProperty(CASE_INSENSITIVE_SORT_OPT);
		if (tmp != null) {
			setCaseInsensitiveSort("true".equalsIgnoreCase(tmp));
		}

		// Importing options
		importGlobs	 = props.getProperty(IMPORT_GLOBS_OPT);
		excludeDirs	 = props.getProperty(EXCLUDE_DIRS_OPT);

		// Last path
		int cnt = 0;
		boolean foundPath = false;
		while (props.getProperty(LAST_NODE_OPT + cnt) != null) {
			if (lastNodePath == null)
				lastNodePath = new ArrayList();
			lastNodePath.add(props.getProperty(LAST_NODE_OPT + cnt));
			cnt++;
			foundPath = true;
		}

		// Last opened project
		lastProject	 = props.getProperty(LAST_PROJECT_OPT);
		if (lastProject != null && !foundPath) {
			VPTNode c = getLastNode().getChildWithName(lastProject);
			if (c != null)
				lastNode = c;
		}

		// External apps by default
		tmp = props.getProperty(USE_EXTERNAL_APPS_OPT);
		if (tmp != null) {
			setUseExternalApps("true".equalsIgnoreCase(tmp));
		}

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

		// follow current buffer
		tmp = props.getProperty(FOLLOW_BUFFER_OPT);
		if (tmp != null) {
			setFollowCurrentBuffer("true".equalsIgnoreCase(tmp));
		}

		//{{{ Incremental updates to the config file
		// last init version
		lastInitVersion = props.getProperty(LAST_INIT_VERSION_OPT);

		// check if import settings mods were applied (version: PV 2.1.0)
		if (lastInitVersion == null ||
				MiscUtilities.compareStrings(lastInitVersion,
					"2.1.0", true) < 0) {
			updateImportSettings(props);
		}

		// only for 2.1.0.1 or below, update the AppLauncher config
		// I had to do a "2.1.0.1" thing because I messed up when
		// releasing the beta for 2.1.0 and numbering it "2.1.0"; so
		// this avoids messing up the configuration for those people
		// that were using the beta.
		if (lastInitVersion == null ||
				MiscUtilities.compareStrings(lastInitVersion,
					"2.1.0.1", true) < 0) {
			updateAppLauncherSettings(props);
		}

		// checks for incremental updates to import settings
		String thisVersion = jEdit.getProperty("plugin.projectviewer.ProjectPlugin.version");
		if (lastInitVersion == null ||
				MiscUtilities.compareStrings(lastInitVersion,
					thisVersion, true) < 0) {
			Properties importProps = loadDefaultImportProps();
			// check again, since we may have had IO problems.
			if (importProps != null) {
				// ok, last version loaded was older than the current plugins,
				// so let's update the entries if any updates exist.
				updateLists(importProps);
			}
		}

		lastInitVersion = thisVersion;
		//}}}

	} //}}}

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
		if (newAskImport > AUTO_IMPORT || newAskImport < ASK_ALWAYS) {
			askImport = ASK_ALWAYS;
		} else {
			this.askImport = newAskImport;
		}
		this.firePropertyChanged(ASK_IMPORT_OPT, new Integer(old),
			new Integer(askImport));
	}

	public void setExcludeDirs(String newExcludeDirs) {
		this.excludeDirs = newExcludeDirs;
	}

	/**
	 * @deprecated	Not used by PV anymore. Superseded by
	 *				{@link #setLastNode(VPTNode) setLastNode(VPTNode)}.
	 */
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

	public void setShowCompactTree(boolean newValue) {
		boolean old = this.showCompactTree;
		this.showCompactTree = newValue;
		firePropertyChanged(SHOW_COMPACT_OPT, old, newValue);
	}

	public void setShowFilteredTree(boolean newValue) {
		boolean old = this.showFilteredTree;
		this.showFilteredTree = newValue;
		firePropertyChanged(SHOW_FILTERED_OPT, old, newValue);
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

	public String getExcludeDirs() {
		return excludeDirs;
	}

	/**
	 * @deprecated	Not used by PV anymore. Superseded by
	 *				{@link #getLastNode() getLastNode()}.
	 */
	public String getLastProject() {
		return lastProject;
	}

	public String getBrowserPath() {
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

	public boolean getShowCompactTree() {
		return showCompactTree;
	}

	public boolean getShowFilteredTree() {
		return showFilteredTree;
	}

	//{{{ property useInfoViewer
	public void setUseInfoViewer(boolean useInfoViewer) {
		this.useInfoViewer = useInfoViewer;
	}

	public boolean getUseInfoViewer() {
		return (useInfoViewer) ? isInfoViewerAvailable() : false;
	}
	//}}}

	//{{{ property userContextMenu
	public void setUserContextMenu(String userContextMenu) {
		this.userContextMenu = userContextMenu;
	}

	public String getUserContextMenu() {
		return userContextMenu;
	}
	//}}}

	//{{{ property useSystemIcons
	public void setUseSystemIcons(boolean useSystemIcons) {
		this.useSystemIcons = useSystemIcons;
	}

	public boolean getUseSystemIcons() {
		return useSystemIcons;
	}
	//}}}

	//{{{ property lastNode
	/**
	 *	Sets the path to the given node as the "last active path" used by the
	 *	user. This makes it possible to reload the exact node that was active
	 *	before next time PV starts, be it a project or a group.
	 *
	 *	@since	PV 2.1.0
	 */
	public void setLastNode(VPTNode node) {
		this.lastNode = node;
	}

	/**
	 *	Returns the path to the last active node as a list. Each item is
	 *	the name of a node in the path, starting with the first child of
	 *	the root node at index 0. This method will never return null; at
	 *	least the root of the tree will be returned.
	 *
	 *	@since	PV 2.1.0
	 */
	public VPTNode getLastNode() {
		ProjectManager.getInstance(); // make sure config is loaded
		if (lastNode == null) {
			if (lastNodePath != null) {
				VPTNode n = VPTRoot.getInstance();
				while (!lastNodePath.isEmpty()) {
					VPTNode c = n.getChildWithName((String)lastNodePath.get(0));
					if (c != null)
						n = c;
					lastNodePath.remove(0);
				}
				lastNode = n;
				lastNodePath = null;
			} else {
				lastNode = VPTRoot.getInstance();
			}
		}
		return lastNode;
	}

	//}}}

	//{{{ property useExternalApps
	public void setUseExternalApps(boolean useExternalApps) {
		this.useExternalApps = useExternalApps;
	}

	public boolean getUseExternalApps() {
		return useExternalApps;
	}
	//}}}

	//{{{ property importGlobs
	public void setImportGlobs(String importGlobs) {
		this.importGlobs = importGlobs;
	}

	public String getImportGlobs() {
		return importGlobs;
	}
	//}}}

	//{{{ property showProjectInTitle
	public void setShowProjectInTitle(boolean flag) {
		this.showProjectInTitle = flag;
	}

	public boolean getShowProjectInTitle() {
		return showProjectInTitle;
	}
	//}}}

	//{{{ property caseInsensitiveSort
	public void setCaseInsensitiveSort(boolean flag) {
		boolean old = caseInsensitiveSort;
		this.caseInsensitiveSort = flag;
		this.firePropertyChanged(CASE_INSENSITIVE_SORT_OPT, Boolean.valueOf(old),
			Boolean.valueOf(flag));
	}

	public boolean getCaseInsensitiveSort() {
		return caseInsensitiveSort;
	}
	//}}}

	//{{{ property followCurrentBuffer
	public void setFollowCurrentBuffer(boolean flag) {
		this.followCurrentBuffer = flag;
	}

	public boolean getFollowCurrentBuffer() {
		return followCurrentBuffer;
	}
	//}}}

	//}}}

	//{{{ Public Methods

	/**
	 *	Adds a new property change listener to the list.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 *	Removes a property change listener to the list.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	//{{{ update(Properties) method
	/**
	 *	<p>Updates the properties in the properties object passed to
	 *	reflect the current state of the config.</p>
	 */
	public void update(Properties props) {
		props.setProperty(CLOSE_FILES_OPT, String.valueOf(closeFiles));
		props.setProperty(REMEBER_OPEN_FILES_OPT, String.valueOf(rememberOpen));
		props.setProperty(DELETE_NOT_FOUND_FILES_OPT, String.valueOf(deleteNotFoundFiles));
		props.setProperty(ASK_IMPORT_OPT, String.valueOf(askImport));
		props.setProperty(USE_EXTERNAL_APPS_OPT, String.valueOf(useExternalApps));
		props.setProperty(USE_SYSTEM_ICONS_OPT, String.valueOf(useSystemIcons));
		props.setProperty(SHOW_PROJECT_TITLE_OPT, String.valueOf(showProjectInTitle));
		props.setProperty(CASE_INSENSITIVE_SORT_OPT, String.valueOf(caseInsensitiveSort));
		props.setProperty(FOLLOW_BUFFER_OPT, String.valueOf(followCurrentBuffer));

		props.setProperty(SHOW_TOOLBAR_OPT, String.valueOf(showToolBar));
		props.setProperty(SHOW_FOLDERS_OPT, String.valueOf(showFoldersTree));
		props.setProperty(SHOW_FILES_OPT, String.valueOf(showFilesTree));
		props.setProperty(SHOW_WFILES_OPT, String.valueOf(showWorkingFilesTree));
		props.setProperty(SHOW_COMPACT_OPT, String.valueOf(showCompactTree));
		props.setProperty(SHOW_FILTERED_OPT, String.valueOf(showFilteredTree));

		props.setProperty(IMPORT_GLOBS_OPT, importGlobs);
		props.setProperty(EXCLUDE_DIRS_OPT, excludeDirs);
		props.setProperty(LAST_INIT_VERSION_OPT, lastInitVersion);

		props.setProperty(BROWSER_PATH_OPT, String.valueOf(browserPath));
		props.setProperty(BROWSER_USE_INFOVIEWER, String.valueOf(useInfoViewer));

		if (userContextMenu != null) {
			props.setProperty(USER_CONTEXT_MENU, userContextMenu);
		}

		// last path
		ArrayList path = null;
		if (lastNode != null && !lastNode.isRoot()) {
			VPTNode n = lastNode;
			path = new ArrayList();
			while (!n.isRoot()) {
				if (path == null)
					path = new ArrayList();
				path.add(0, n.getName());
				n = (VPTNode) n.getParent();
			}
		}
		if (path != null) {
			for (int i = 0; i < path.size(); i++)
				props.setProperty(LAST_NODE_OPT + i, path.get(i).toString());
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

	//{{{ isInfoViewerAvailable()
	public boolean isInfoViewerAvailable() {
		return (jEdit.getPlugin(INFOVIEWER_PLUGIN) != null);
	} //}}}

	//{{{ isErrorListAvailable()
	public boolean isErrorListAvailable() {
		return (jEdit.getPlugin(ERRORLIST_PLUGIN) != null);
	} //}}}

	//{{{ isJEdit43()
	public boolean isJEdit43() {
		return jEdit.getBuild().startsWith("04.03");
	} //}}}

	/**
	 *	Checks whether jEdit has the methods to do binary file check,
	 *	which was added in 4.3pre5.
	 *
	 *	@since	PV 2.3.1.6
	 */
	public boolean hasBinaryFileCheck() {
		return (MiscUtilities.compareVersions(jEdit.getBuild(), "04.03.05.00") >= 0);
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

	/**
	 *	Loads the "import.properties" (or "import-sample.properties") that
	 *	contains information about the file importing options and updates
	 *	to the import lists.
	 *
	 *	@since	PV 2.1.0
	 */
	private Properties loadDefaultImportProps() {
		InputStream is = null;
		Properties props = new Properties();
		try {
			is = ProjectViewerConfig.class.getResourceAsStream("/projectviewer/import-sample.properties");
			if (is != null) {
				props.load(is);
				try { is.close(); } catch (Exception e) { }
			}
			is = ProjectPlugin.getResourceAsStream("import.properties");
			if (is != null) {
				props.load(is);
			}
		} catch (IOException ioe) {
			props.setProperty(IMPORT_EXTS_OPT, "");
			props.setProperty(EXCLUDE_DIRS_OPT, "");
			props.setProperty(INCLUDE_FILES_OPT, "");
		} finally {
			if (is != null) try { is.close(); } catch (Exception e) { }
		}
		return props;
	}

	/**
	 *	Merges the two lists of strings, adding all the strings in "add" that
	 *	are not already in "src", returning the result.
	 *
	 *	@since	PV 2.1.0
	 */
	private String mergeList(String src, String add) {
		StringBuffer res = new StringBuffer(src);
		StringTokenizer st = new StringTokenizer(add, " ");
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (!src.startsWith(s) && !src.endsWith(s)
					&& src.indexOf(" " + s + " ") == -1) {
				res.append(" ").append(s);
			}
		}
		return res.toString();
	}

	/**
	 *	Check the import properties provided for the list of updated
	 *	properties since the "lastInitVersion" detected in the config.
	 *	If the version string is "null", all the updates are applied.
	 *
	 *	@since	PV 2.1.0
	 */
	private void updateLists(Properties importProps) {
		for (Iterator i = importProps.keySet().iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			if (key.startsWith(EXCLUDE_DIRS_OPT + "-")) {
				if (needUpdate(key.substring(EXCLUDE_DIRS_OPT.length() + 1))) {
					excludeDirs = mergeList(excludeDirs, importProps.getProperty(key));
				}
			}
			if (key.startsWith(IMPORT_GLOBS_OPT + "-")) {
				if (needUpdate(key.substring(IMPORT_GLOBS_OPT.length() + 1))) {
					importGlobs = mergeList(importGlobs, importProps.getProperty(key));
				}
			}
		}
	}

	/**
	 *	Returns whether we need to update the lists for the given version,
	 *	depending on the lastInitVersion detected from the user's config file.
	 */
	private boolean needUpdate(String updateVersion) {
		return (lastInitVersion == null
				|| MiscUtilities.compareStrings(lastInitVersion, updateVersion, true) < 0);
	}

	/**
	 *	Translates old import settings (< PV 2.1.0) into the 2.1-style globs.
	 *	This also merges the "extensions to include" and "files to include"
	 *	settings into the "include globs" list, since it now can handle both
	 *	cases. The "directories to ignore" doesn't change, since the current
	 *	syntax corresponds to valid globs; the only change is semantic (it's
	 *	now a glob, not just a string match).
	 *
	 *	@since	PV 2.1.0
	 */
	private void updateImportSettings(Properties config) {
		// translates the "extensions to include" into a list of globs.
		String includeExts = config.getProperty(IMPORT_EXTS_OPT);
		if (includeExts == null) {
			// don't have a configuration; just read the defaults and
			// return.
			Properties defaults = loadDefaultImportProps();
			importGlobs = defaults.getProperty(IMPORT_GLOBS_OPT);
			excludeDirs = defaults.getProperty(EXCLUDE_DIRS_OPT);
			return;
		}

		StringTokenizer st = new StringTokenizer(includeExts, " ");
		StringBuffer globs = new StringBuffer();
		while(st.hasMoreTokens()) {
			globs.append("*.").append(st.nextToken()).append(" ");
		}

		// appends the "files to include" list as is
		if (config.get(INCLUDE_FILES_OPT) != null)
			globs.append(config.getProperty(INCLUDE_FILES_OPT));

		setImportGlobs(globs.toString().trim());
	}

	/**
	 *	Translates the old AppLauncher cofiguration into globs.
	 *
	 *	@since	PV 2.1.0.1
	 */
	private void updateAppLauncherSettings(Properties config) {
		InputStream inprops =
			ProjectPlugin.getResourceAsStream("fileassocs.properties");
		Properties p = new Properties();

		// loads the properties from the file and translates the extensions
		// into globs
		if (inprops != null) {
			try {
				p.load(inprops);

				Properties newp = new Properties();
				for (Iterator i = p.keySet().iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					newp.put("*." + key, p.get(key));
				}
				p = newp;
			} catch (IOException ioe) {
				Log.log(Log.WARNING, this, ioe);
			} finally {
				try { inprops.close(); } catch (Exception e) { }
			}

		}

		// saves the properties back to the file
		Properties props = new Properties();
		OutputStream out = ProjectPlugin.getResourceAsOutputStream("fileassocs.properties");
		if (out != null) {
			try {
				p.store(out, "");
			} catch (IOException ioe) {
				Log.log(Log.WARNING, this, ioe);
			} finally {
				try { out.close(); } catch (Exception e) { }
			}
		}

	}

	//}}}

}

