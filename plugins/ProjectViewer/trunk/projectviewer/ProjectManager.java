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

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

/** Manages all projects.
 */
public final class ProjectManager {

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
	private static void writeProjectData(DataOutputStream out, Project prj)
			 throws IOException {
		out.writeBytes("project.");
		out.writeBytes(Integer.toString(prj.getKey()));
		out.writeBytes("=");
		out.writeBytes(prj.getName());
		out.writeBytes("\n");

		out.writeBytes("project.");
		out.writeBytes(Integer.toString(prj.getKey()));
		out.writeBytes(".root=");
		out.writeBytes(escape(prj.getRoot().getPath()));
		out.writeBytes("\n");
		
		out.writeBytes( "project."                        );
		out.writeBytes( Integer.toString( prj.getKey() )  );
		out.writeBytes( ".urlroot="                          );
		out.writeBytes( escape( prj.getURLRoot() ) );
		out.writeBytes( "\n"                              );   
	}

	/** Write project file data to file.
	 *
	 *@param  out              Description of Parameter
	 *@param  prj              Description of Parameter
	 *@param  file             Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	private static void writeProjectFileData(DataOutputStream out, Project prj, ProjectFile file)
			 throws IOException {
		out.writeBytes("file.");
		out.writeBytes(Integer.toString(file.getKey()));
		out.writeBytes(".project.");
		out.writeBytes(Integer.toString(prj.getKey()));
		out.writeBytes("=");
		out.writeBytes(escape(file.getPath()));
		out.writeBytes("\n");
	}

	/** Escape property characters.
	 *
	 *@param  s  Description of Parameter
	 *@return    Description of the Returned Value
	 */
	private static String escape(String s) {
		StringBuffer buf = new StringBuffer(s);
		for (int i = 0; i < buf.length(); i++) {
			if (buf.charAt(i) == '\\')
				buf.replace(i, ++i, "\\\\");
		}
		return buf.toString();
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
	private static Properties load(String fileName)
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
		MiscUtilities.quicksort((Vector) projects, new ProjectComparator());
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
		saveAllProjectData();
		saveAllProjectFileData();
	}

	/** Save data on all projects. */
	private synchronized void saveAllProjectData() {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(
					ProjectPlugin.getResourceAsOutputStream(PROJECTS_PROPS_FILE));

			out.writeBytes("#DO NOT MODIFY THIS FILE:\n\n");
			out.writeBytes("#IF YOU DO WANT TO MODIFY IT... HERE IS THE SYNTAX\n");
			out.writeBytes("#project.1=projectName\n");
			out.writeBytes("#project.2=projectName\n");

			for (int i = 0; i < projects.size(); i++) {
				Project each = (Project) projects.get(i);
				each.setKey(i + 1);
				writeProjectData(out, each);
			}

		}
		catch (IOException e) {
			Log.log(Log.ERROR, this, e);

		}
		finally {
			close(out);
		}
	}

	/** Save data on all the projects' files. */
	private synchronized void saveAllProjectFileData() {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(
					ProjectPlugin.getResourceAsOutputStream(FILE_PROPS_FILE));

			out.writeBytes("#DO NOT MODIFY THIS FILE:\n\n");
			out.writeBytes("#IF YOU DO WANT TO MODIFY IT... HERE IS THE SYNTAX\n");
			out.writeBytes("#file.1.project.1=filename\n");
			out.writeBytes("#file.2.project.1=filename\n");
			out.writeBytes("#file.1.project.2=filename\n");
			out.writeBytes("#file.2.project.2=filename\n");

			for (Iterator i = projects(); i.hasNext(); ) {
				Project eachProject = (Project) i.next();
				int count = 1;
				for (Iterator j = eachProject.projectFiles(); j.hasNext(); ) {
					ProjectFile each = (ProjectFile) j.next();
					each.setKey(count++);
					writeProjectFileData(out, eachProject, each);
				}
			}
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
	private void close(OutputStream out) {
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
			Properties fileProps = load(FILE_PROPS_FILE);

			int counter = 1;
			String prjName = projectProps.getProperty("project." + counter);
			while (prjName != null) {
				String root = projectProps.getProperty("project." + counter + ".root");
	     		        String sURLRoot = projectProps.getProperty("project." + counter + ".urlroot");
	
				//Log.log( Log.DEBUG, this, "Loading project '" + prjName + "' root:" + root );
				Project project = new Project(prjName, new ProjectDirectory(root), counter);
				project.setURLRoot(sURLRoot);
				addProject(project);
				prjName = projectProps.getProperty("project." + ++counter);
			}

			for (Iterator i = projects.iterator(); i.hasNext(); ) {
				Project each = (Project) i.next();

				int fileCounter = 1;
				String fileName = fileProps.getProperty(buildFileKey(fileCounter, each));
				while (fileName != null) {
					ProjectFile file = new ProjectFile(fileCounter, fileName);
					if (file.exists())
						each.importFile(file);
					fileName = fileProps.getProperty(buildFileKey(++fileCounter, each));
				}
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

}

