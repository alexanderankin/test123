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
package projectviewer.persist;

//{{{ Imports
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Stack;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTDirectory;

import projectviewer.ProjectPlugin;
import projectviewer.ProjectManager;
//}}}

/**
 *	Class that takes care of loading old config files into the new virtual-tree
 *	object style. Since saving is always done in the new way, there's no need
 *	to worry about saving in the old format.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public final class OldConfigLoader {

	//{{{ Constants

	private static final String OLD_CONFIG_FILE = "projects.properties";
	private static final String OLD_PRJ_CONFIG_DIR = "projects" + File.separator;
	private static final String OLD_PRJ_CONFIG_FILE = "files.properties";

	//}}}

	/** Private constructor. No instances! */
	private OldConfigLoader() { }

	//{{{ load(ProjectManager) method
	/** Loads the old configuration data into the new object style. */
	public static void load(ProjectManager manager) throws IOException {
		File oldPropsFile = new File(ProjectPlugin.getResourcePath(OLD_CONFIG_FILE));
		String oldFileList = ProjectPlugin.getResourcePath(OLD_PRJ_CONFIG_FILE);

		if (!oldPropsFile.exists()) {
			oldPropsFile = new File(jEdit.getSettingsDirectory(), "ProjectViewer.projects.properties");
			if (!oldPropsFile.exists())
				return;

			oldFileList = jEdit.getSettingsDirectory() + File.separator + "ProjectViewer.files.properties";
		}

		Properties pList = loadProperties(oldPropsFile.getAbsolutePath(), true);
		Properties oldProps = null;

		int counter = 1;
		String prjName = pList.getProperty( "project." + counter );

		while ( prjName != null ) {
			String root = pList.getProperty( "project." + counter + ".root" );

			VPTProject p = new VPTProject(prjName);

			if ( root != null ) {
				if ( oldProps == null ) {
					oldProps = loadProperties(oldFileList, true);
				}
				loadProjectFromProps(p, oldProps, counter);
			} else {
				loadProjectFromFile(p, counter);
			}

			manager.addProject(p);
			prjName = pList.getProperty( "project." + ( ++counter ) );
		}

		// TODO: cleanup old config after conversion?

	} //}}}

	//{{{ loadProjectFromFile(VPTProject, int) method
	/**
	 *	Loads the project data from the file located in the old project
	 *	file directory.
	 *
	 *	@param	p		The project where to load the data.
	 *	@param	idx		The index of the properties file to load.
	 */
	private static void loadProjectFromFile(VPTProject p, int idx) {
		Properties props = null;
		try {
			props = loadProperties(OLD_PRJ_CONFIG_DIR + "project" + idx + ".properties", false);
		} catch (IOException ioe) {
			Log.log(Log.ERROR,OldConfigLoader.class,ioe);
		}

		if (props == null) return;

		HashMap paths = new HashMap();


		// ensures that the path does not have a trailing '/'
		p.setRootPath(new File(props.getProperty("root")).getAbsolutePath());

		p.setURL(props.getProperty("webroot"));
		
		if (props.get("folderTreeState") != null) {
			p.setProperty("projectviewer.folder_tree_state", (String) props.get("folderTreeState"));
		}

		for (Iterator it = props.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			if (key.startsWith("file")) {
				File f = new File(props.getProperty(key));
				VPTNode parent = ensureDirAdded(p, f.getParent(), paths);
				VPTFile vf = new VPTFile(f);
				p.registerFile(vf);
				parent.add(vf);
			} else if (key.startsWith("open_files")) {
				p.addOpenFile(props.getProperty(key));
			} else if (key.equals("buildfile")) {
				p.setProperty("antelope.buildfile", props.getProperty(key));
			}
		}

	} //}}}

	//{{{ loadProjectFromProps(VPTProject, Properties) method
	/**
	 *	Loads the project from the given properties. Keys are expected to be
	 *	in the reeealy old format (pre-1.0.3), when all projects were stored
	 *	in the same file.
	 */
	private static void loadProjectFromProps(VPTProject p, Properties props, int idx) {
		int counter = 1;
		String prefix = p.getName() + "." + idx + ".file.";

		String fileName = props.getProperty(prefix + counter);
		HashMap paths = new HashMap();

		while ( fileName != null ) {
			File f = new File(fileName);
			VPTNode parent = ensureDirAdded(p, f.getParent(), paths);
			VPTFile vf = new VPTFile(f);
			p.registerFile(vf);
			parent.add(vf);
			fileName = props.getProperty(prefix + (++counter));
		}


	} //}}}

	//{{{ ensureDirAdded(VPTProject, HashMap)
	/**
	 *	Ensure that the given directory and all ancestors up to the root
	 *	of the project are added to the project.
	 *
	 *	@param	p		The project.
	 *	@param	path	The path to the directory to check.
	 *	@param	dirs	A HashMap containing the already added dirs (key = path).
	 *	@return	The VPTNode representing the given directory.
	 */
	private static VPTNode ensureDirAdded(VPTProject p, String path, HashMap dirs) {
		if (path.equals(p.getRootPath())) {
			return p;
		}
		if (dirs.get(path) != null) {
			return (VPTNode) dirs.get(path);
		}

		Stack toAdd = new Stack();
		VPTNode dir = new VPTDirectory(new File(path));
		dirs.put(path, dir);
		toAdd.push(dir);

		VPTNode where = null;
		String parent = new File(path).getParent();
		while (where == null) {
			if (dirs.get(parent) != null) {
				where = (VPTNode) dirs.get(parent);
			} else {
				if (parent.equals(p.getRootPath())) {
					where = p;
				} else {
					dir = new VPTDirectory(new File(parent));
					dirs.put(parent, dir);
					toAdd.push(dir);
					parent = new File(parent).getParent();
				}
			}
		}

		while (!toAdd.isEmpty()) {
			VPTNode o = (VPTNode) toAdd.pop();
			where.add(o);
			where = o;
		}

		return where;
	} //}}}

	//{{{ loadProperties(String, boolean) method
	/** Load the specified properties file. */
	private static Properties loadProperties(String fileName, boolean abs) throws IOException {
		Properties props = new Properties();
		InputStream in = null;
		try {
			if (abs) {
				in = new FileInputStream(fileName);
			} else {
				in = ProjectPlugin.getResourceAsStream(fileName);
			}
			if ( in != null )
				props.load(in);
			return props;
		} finally {
			if (in != null) in.close();
		}
	} //}}}

}

