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

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import projectviewer.config.ProjectViewerConfig;

/** Manages all projects.
 */
public final class ProjectManager {

    private static final ProjectViewerConfig config = ProjectViewerConfig.getInstance();

	final static String PROJECTS_PROPS_FILE = "projects.properties";
	final static String FILE_PROPS_FILE = "files.properties";

	private static ProjectManager instance;

	private List projects;


	/** Create a new <code>ProjectManager</code>. */
	protected ProjectManager() {
		projects = new Vector();
		loadProjects();
	}

	/** Returns an instance of <code>ProjectManager</code>.
	 *
	 *@return    The instance value
	 */
	public static ProjectManager getInstance() {
		if (instance == null)
			instance = new ProjectManager();
		return instance;
	}

	/** Write project data to file.
	 *
	 *@param  out              Description of Parameter
	 *@param  prj              Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	private static void writeProjectData(PrintWriter out, Project prj)
			 throws IOException {
		out.print("project.");
		out.print(Integer.toString(prj.getKey()));
		out.print("=");
		out.println(prj.getName());
	}

	/** Returns a key identifying file of <code>index</code> for <code>aProject</code>.
	 *
	 *@param  index     Description of Parameter
	 *@param  aProject  Description of Parameter
	 *@return           Description of the Returned Value
	 */
	private static String buildFileKey(int index, Project aProject) {
		return "file." + index + ".project." + aProject.getKey();
	}

	/** Load the specified properties file.
	 *
	 *@param  fileName         Description of Parameter
	 *@return                  Description of the Returned Value
	 *@exception  IOException  Description of Exception
	 */
	public static Properties load(String fileName)
			 throws IOException {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = ProjectPlugin.getResourceAsStream(fileName);
			if (in != null)
				props.load(in);
			return props;
		}
		finally {
			if (in != null)
				in.close();
		}
	}

	/** Returns the index of the given project.
	 *
	 *@param  aProject  Description of Parameter
	 *@return           The indexOfProject value
	 */
	public int getIndexOfProject(Project aProject) {
		return projects.indexOf(aProject);
	}

	/** Returns the number of projects.
	 *
	 *@return    The projectCount value
	 */
	public int getProjectCount() {
		return projects.size();
	}

	/** Returns the indexed project.
	 *
	 *@param  index  Description of Parameter
	 *@return        The project value
	 */
	public Project getProject(int index) {
		return (Project) projects.get(index);
	}

	/** Returns the named project.
	 *
	 *@param  aName  Description of Parameter
	 *@return        The project value
	 */
	public Project getProject(String aName) {
		for (Iterator i = projects(); i.hasNext(); ) {
			Project each = (Project) i.next();
			if (each.getName().equals(aName))
				return each;
		}
		return null;
	}

	/** Returns an iterator of projects.
	 *
	 *@return    Description of the Returned Value
	 */
	public Iterator projects() {
		return projects.iterator();
	}

	/** Returns <code>true</code> if the named project exists.
	 *
	 *@param  name  Description of Parameter
	 *@return       Description of the Returned Value
	 */
	public boolean hasProject(String name) {
		return getProject(name) != null;
	}

	/** Add a project.
	 *
	 *@param  aProject  The feature to be added to the Project attribute
	 */
	public synchronized void addProject(Project aProject) {
		if (aProject.isKeyUnset())
			aProject.setKey(projects.size() + 1);
		projects.add(aProject);
		sortProjectList();
	}

	/** Remove a project.
	 *
	 *@param  aProject  Description of Parameter
	 */
	public synchronized void removeProject(Project aProject) {
		projects.remove(aProject);
	}

	/** Save projects. */
	public synchronized void save() {
        // Before saving, let's load all projects.
        // There should be a better way to do this!!!
        for (int i = 0; i < projects.size(); i++) {
            ((Project) projects.get(i)).load();
        }
        
        // Cleanup the "projects" directory
        File pDir = new File(ProjectPlugin.getResourcePath("projects"));
        File[] list = pDir.listFiles();
        if (list != null) for (int i = 0; i < list.length; i++) {
            list[i].delete();
        }
        
        // Saves all the data
        Log.log(Log.DEBUG, this, "Saving all project data...");
		saveAllProjectData();
        for (Iterator i = projects(); i.hasNext(); ) {
            ((Project)i.next()).save();
        }
	}

	/** Save data on all projects. */
	private synchronized void saveAllProjectData() {
        // Now, continues with saving.
		PrintWriter out = null;
		try {
			out = new PrintWriter(
                    new OutputStreamWriter(
					  ProjectPlugin.getResourceAsOutputStream(PROJECTS_PROPS_FILE),
                      "ISO-8859-1"
                    )
                  );

            out.println("# Projects configuration files");
			out.println("#DO NOT MODIFY THIS FILE:\n\n");
			out.println("#IF YOU DO WANT TO MODIFY IT... HERE IS THE SYNTAX\n");
			out.println("#project.1=projectName\n");
			out.println("#project.2=projectName\n");

			for (int i = 0; i < projects.size(); i++) {
				Project each = (Project) projects.get(i);
				each.setKey(i + 1);
				writeProjectData(out, each);
			}

		}
        catch (UnsupportedEncodingException uee) {
            // Not likely
        }
		catch (IOException e) {
			Log.log(Log.ERROR, this, e);
		}
		finally {
			close(out);
		}
	}

	/** Close output stream, catching any exceptions.
	 *
	 *@param  out  Description of Parameter
	 */
	private void close(Writer out) {
		if (out != null) {
			try {
				out.close();
			}
			catch (IOException e) {
				Log.log(Log.WARNING, this, e);
			}
		}
	}

	/** Load all projects. */
	private void loadProjects() {
		try {
			Properties projectProps = load(PROJECTS_PROPS_FILE);
			Properties fileProps = null;
            ArrayList toLoad = new ArrayList();
            
			int counter = 1;
			String prjName = projectProps.getProperty("project." + counter);
			while (prjName != null) {
				
				String root = projectProps.getProperty("project." + counter + ".root");
                    
				//Log.log( Log.DEBUG, this, "Loading project '" + prjName + "' root:" + root );
				Project project = new Project(prjName, counter);
                if (root != null) {
                    project.setRoot(new ProjectDirectory(root));
		            project.setLoaded(true);
                    toLoad.add(project);
                    if (fileProps == null) {
                        fileProps = load(FILE_PROPS_FILE);
                    }
                }
                
				addProject(project);
				prjName = projectProps.getProperty("project." + (++counter));
			}

            // Loads projects that use the old configuration scheme
			for (Iterator i = toLoad.iterator(); i.hasNext(); ) {
				Project each = (Project) i.next();

				int fileCounter = 1;
				String fileName = fileProps.getProperty(buildFileKey(fileCounter, each));
				while (fileName != null) {
					ProjectFile file = new ProjectFile(fileName);
					if (!config.getDeleteNotFoundFiles() || file.exists())
						each.importFile(file);
					fileName = fileProps.getProperty(buildFileKey(++fileCounter, each));
				}
			}
            
            // If we have any old configuration, let's save it as new config as
            // soon as possible
            if (toLoad.size() > 0) {
                Log.log(Log.NOTICE, this, "Migration to new configuration style...");
                save();
            }

		}
		catch (Throwable e) {
			Log.log(Log.ERROR, this, e);
		}
	}

	/** Object for comparing projects.
	 */
	private final class ProjectComparator implements MiscUtilities.Compare {
		/** Perform a comparison.
		 *
		 *@param  obj1  Description of Parameter
		 *@param  obj2  Description of Parameter
		 *@return       Description of the Returned Value
		 */
		public int compare(Object obj1, Object obj2) {
			Project project1 = (Project) obj1;
			Project project2 = (Project) obj2;

			return project1.getName().compareTo(project2.getName());
		}

	}

    /**
     *  Sorts the project list.
     */
    public void sortProjectList() {
        MiscUtilities.quicksort((Vector) projects, new ProjectComparator());
    }
    
}

