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
package projectviewer;

import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.apache.commons.digester.Digester;
import org.apache.xml.serialize.OutputFormat;
import org.mobix.xml.XmlWriteContext;
import org.xml.sax.*;


/**
 * Manages all projects.
 */
public class ProjectManager
{

   static final String PROJECTS_PROPS_FILE = "projects.properties";
   static final String FILE_PROPS_FILE = "files.properties";

   private List projectNames;
   private Map projects;


   /**
    * Create a new <code>ProjectManager</code>.
    */
   protected ProjectManager()
   {
      projectNames = new ArrayList();
      projects = new HashMap();
      loadProjectNames();
   }

   /**
    * Returns the number of projects.
    */
   public int getProjectCount()
   {
      return projectNames.size();
   }

   /**
    * Returns <code>true</code> if the named project exists.
    */
   public boolean hasProject( String name )
   {
      return projectNames.contains(name);
   }

   /**
    * Returns an array of project names.
    */
   public String[] getProjectNames()
   {
      String[] names = new String[projectNames.size()];
      return (String[]) projectNames.toArray(names);
   }

   /**
    * Returns the named project.
    */
   public Project getProject( String aName ) throws ProjectException
   {
      Project prj = (Project) projects.get(aName);
      //Log.log(Log.DEBUG, this, "Found project: " + prj);
      if (prj == null) {
         prj = loadProject(aName);
         projects.put(aName, prj);
         //Log.log(Log.DEBUG, this, "Project Loaded: " + projects.get(aName));
      }
      return prj;
   }

   /**
    * Add a project.
    */
   public void addProject( Project aProject ) throws ProjectException
   {
      save(aProject);
      projectNames.add( aProject.getName() );
      projects.put(aProject.getName(), aProject);
      Collections.sort(projectNames);
   }

   /**
    * Remove a project.
    */
   public void remove( String projectName )
   {
      projectNames.remove( projectName );
      projects.remove(projectName);
   }

   /**
    * Remove a project.
    */
   public void remove( Project aProject )
   {
      remove(aProject.getName());
   }

   /**
    * Save the given project.
    */
   public void save(Project project) throws ProjectException
   {
      OutputStream out = null;
      try {
         //Log.log(Log.DEBUG, this, "Saving project " + project.getName());
         OutputFormat format = new OutputFormat();
         format.setOmitXMLDeclaration(true);
         format.setIndenting(true);
         out = ProjectPlugin.getResourceAsOutputStream(project.getName().replace(' ', '_') + ".xml");
         project.save(new XmlWriteContext(out, format));
         //Log.log(Log.DEBUG, this, "Project saved");
      } catch (Exception e) {
         throw new ProjectException("Error saving project", e);
      } finally {
         ProjectPlugin.close(out);
      }
   }

   /**
    * Save all managed projects.
    */
   public void save()
   {
      BufferedWriter out = null;
      try {
         out = new BufferedWriter(new OutputStreamWriter(
                                     ProjectPlugin.getResourceAsOutputStream(PROJECTS_PROPS_FILE)));

         for (Iterator i = projectNames.iterator(); i.hasNext();) {
            out.write(i.next().toString());
            out.newLine();
         }
         //Log.log(Log.DEBUG, this, "Saving All projects");
         saveAllProjects();
         //Log.log(Log.DEBUG, this, "Saving All projects done");
      } catch (IOException e) {
         ProjectPlugin.error(e);
      } finally {
         ProjectPlugin.close( out );
      }
   }

   /**
    * Load a project of the given name.
    */
   public Project loadProject(String projectName) throws ProjectException
   {
      String name = projectName.replace(' ', '_') + ".xml";
      return loadProject(new File(ProjectPlugin.getResourcePath(name)));
   }

   /**
    * Load the given project.
    */
   public Project loadProject(File f) throws ProjectException
   {
      try {
         Digester digester = new Digester();
         Project prj = new SimpleProject();
         prj.initDigester(digester);
         digester.push(prj);
         digester.parse(new InputSource(f.toURL().toString()));
         return prj;
      } catch (SAXException e) {
         Exception rootCause = e.getException();
         if (rootCause == null)
            rootCause = e;
         throw new ProjectException("Error loading project", rootCause);
      }
      catch (Exception e) {
         throw new ProjectException("Error loading project", e);
      }
   }

   /**
    * Load all projects from file.
    */
   private void loadProjectNames()
   {
      BufferedReader in = null;
      try {
         in = new BufferedReader(new InputStreamReader(
                                    ProjectPlugin.getResourceAsStream(PROJECTS_PROPS_FILE)));

         String line = null;
         while ((line = in.readLine()) != null)
            projectNames.add(line.trim());

      } catch ( Throwable e ) {
         ProjectPlugin.error(e);

      } finally {
         ProjectPlugin.close(in);
      }
   }

   /**
    * Save all projects, logging any exceptions.
    */
   private void saveAllProjects()
   {
      for (Iterator i = projects.values().iterator(); i.hasNext();) {
         try {
            save((Project) i.next());
         } catch (ProjectException e) {
            ProjectPlugin.error(e);
         }
      }
   }

}
