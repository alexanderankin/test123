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

import java.util.*;
import javax.swing.tree.TreeNode;
import org.apache.commons.digester.Digester;
import org.mobix.xml.XmlWriteContext;
import org.xml.sax.SAXException;


/**
 * A project proxy implementation.
 */
public class ProjectProxy implements Project
{

   private String name;
   private Project prj;
   private ProjectManager prjManager;


   /**
    * Create a new <code>ProjectProxy</code>.
    */
   public ProjectProxy( String aName, ProjectManager aProjectManager )
   {
      name = aName;
      prjManager = aProjectManager;
   }

   /**
    * Returns the project this proxy is proxying for, loading the project if
    * it hasn't been loaded already.
    */
   public Project getProject()
   {
      if (prj == null) {
         try {
            prj = prjManager.getProject(name);
         } catch (ProjectException e) {
            // TODO: Two options - throw runtime or log.  Log for now.
            // We probably want to log and then add a ProjectError child
            // which will display an error.  Another possibility - create a null
            // project.
            ProjectPlugin.error(e);
         }
      }
      return prj;
   }

   /**
    * Returns the name of the project.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the project.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void setName(String aName)
   {
      getProject().setName(aName);
   }

   /**
    * Returns the names of all available properties.
    */
   public Iterator propertyNames() {
      return getProject().propertyNames();
   }

   /**
    * Set a project property.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void setProperty(String name, String value)
   {
      getProject().setProperty(name, value);
   }

   /**
    * Returns a project property.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public String getProperty(String name)
   {
      return getProject().getProperty(name);
   }

   /**
    * Remove a project property.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void removeProperty(String name)
   {
      getProject().removeProperty(name);
   }

   /**
    * Add a view to this project.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void addView(FileView aView)
   {
      getProject().addView(aView);
   }

   /**
    * Returns the number of views.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public int getViewCount()
   {
      return getProject().getViewCount();
   }

   /**
    * Returns the view at the specified index.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public FileView getView(int index)
   {
      return getProject().getView(index);
   }

   /**
    * Remove the given view.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void remove(FileView view)
   {
      getProject().remove(view);

   }

   /**
    * Returns an iteration of views.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public Iterator views()
   {
      return getProject().views();
   }

   /**
    * Save project data.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void save(XmlWriteContext xmlWrite) throws SAXException
   {
      getProject().save(xmlWrite);
   }

   /**
    * Initialize this digester to load data into this project.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void initDigester(Digester digester)
   {
      getProject().initDigester(digester);
   }

   /**
    * Returns the parent.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public void setParent(TreeNode parent)
   {
      getProject().setParent(parent);
   }

   /**
    * Compares the names projects.
    *
    * <p>SPECIFIED IN: java.lang.Comparable</p>
    */
   public int compareTo(Object obj)
   {
      return getProject().compareTo(obj);
   }

   /**
    * Returns the name of the project.
    *
    * <p>SPECIFIED IN: java.lang.Object</p>
    */
   public String toString()
   {
      return getProject().toString();
   }

   /*
    * javax.swing.tree.TreeNode methods.
    */

   /**
    * Returns the indexed view.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public TreeNode getChildAt(int childIndex)
   {
      return getProject().getChildAt(childIndex);
   }

   /**
    * Returns the number of file views.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public int getChildCount()
   {
      return getProject().getChildCount();
   }

   /**
    * Returns <code>null</code>.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public TreeNode getParent()
   {
      return getProject().getParent();
   }

   /**
    * Returns the index of the given {@link FileView}.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public int getIndex(TreeNode node)
   {
      return getProject().getIndex(node);
   }

   /**
    * Returns <code>true</code>.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public boolean getAllowsChildren()
   {
      return getProject().getAllowsChildren();
   }

   /**
    * Returns <code>false</code>.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public boolean isLeaf()
   {
      return getProject().isLeaf();
   }

   /**
    * Returns the children of the reciever as an <code>Enumeration</code>.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public Enumeration children()
   {
      return getProject().children();
   }

}
