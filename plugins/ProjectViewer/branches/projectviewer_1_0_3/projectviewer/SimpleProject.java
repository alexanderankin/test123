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
import javax.swing.tree.*;
import org.apache.commons.digester.*;
import org.mobix.xml.*;
import org.xml.sax.SAXException;
import projectviewer.digester.CreateFileViewRule;


/**
 * A simple project implementation.
 */
public class SimpleProject implements Project
{

   private String name;
   private List views;
   private TreeNode parent;
   private Properties props;


   /**
    * Create a new <code>SimpleProject</code>.
    */
   public SimpleProject()
   {
      this(null);
   }

   /**
    * Create a new <code>SimpleProject</code>.
    */
   public SimpleProject( String aName )
   {
      views = new ArrayList();
      props = new Properties();
      setName(aName);
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
      name = aName;
   }

   /**
    * Set a project property.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void setProperty(String name, String value)
   {
      props.setProperty(name, value);
   }

   /**
    * Returns a project property.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public String getProperty(String name)
   {
      return props.getProperty(name);
   }

   /**
    * Remove a project property.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void removeProperty(String name)
   {
      props.remove(name);
   }

   /**
    * Add a view to this project.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void addView(FileView aView)
   {
      aView.setProject(this);
      views.add(aView);
   }

   /**
    * Returns the number of views.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public int getViewCount()
   {
      return views.size();
   }

   /**
    * Returns the view at the specified index.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public FileView getView(int index)
   {
      return (FileView) views.get(index);
   }

   /**
    * Remove the given view.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void remove(FileView view)
   {
      views.remove(view);

   }

   /**
    * Returns an iteration of views.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public Iterator views()
   {
      return views.iterator();
   }

   /**
    * Save project data.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void save(XmlWriteContext xmlWrite) throws SAXException
   {
      SimpleAttributes atts = new SimpleAttributes("name", name);
      atts.addAttribute("version", ProjectPlugin.VERSION);
      xmlWrite.startElement("project", atts);
      for (Enumeration names = props.propertyNames(); names.hasMoreElements();) {
         String propName = names.nextElement().toString();
         atts = new SimpleAttributes("name", propName);
         atts.addAttribute("value", props.getProperty(propName));
         xmlWrite.writeElement("property", atts);
      }
      for (Iterator i = views.iterator(); i.hasNext();) {
         ((FileView) i.next()).save(xmlWrite);
      }
      xmlWrite.endElement("project");
   }

   /**
    * Initialize this digester to load data into this project.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void initDigester(Digester digester)
   {
      digester.addSetProperties("project");
      digester.addCallMethod("project/property", "setProperty", 2,
                             new Class[] {String.class, String.class});
      digester.addCallParam("project/property", 0, "name");
      digester.addCallParam("project/property", 1, "value");
      digester.addRule("project/view", new CreateFileViewRule(digester));
      digester.addSetNext("project/view", "addView", FileView.class.getName());
   }

   /**
    * Set the parent node.
    *
    * <p>SPECIFIED IN: projectviewer.Project</p>
    */
   public void setParent(TreeNode node) {
      parent = node;
   }

   /**
    * Compares the names projects.
    *
    * <p>SPECIFIED IN: java.lang.Comparable</p>
    */
   public int compareTo(Object obj)
   {
      return getName().compareTo(((Project) obj).getName());
   }

   /**
    * Returns the name of the project.
    *
    * <p>SPECIFIED IN: java.lang.Object</p>
    */
   public String toString()
   {
      return getName();
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
      return getView(childIndex);
   }

   /**
    * Returns the number of file views.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public int getChildCount()
   {
      return getViewCount();
   }

   /**
    * Returns parent node.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public TreeNode getParent()
   {
      return parent;
   }

   /**
    * Returns the index of the given {@link FileView}.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public int getIndex(TreeNode node)
   {
      return views.indexOf(node);
   }

   /**
    * Returns <code>true</code>.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public boolean getAllowsChildren()
   {
      return true;
   }

   /**
    * Returns <code>false</code>.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public boolean isLeaf()
   {
      return false;
   }

   /**
    * Returns the children of the reciever as an <code>Enumeration</code>.
    *
    * <p>SPECIFIED IN: javax.swing.table.TableModel</p>
    */
   public Enumeration children()
   {
      return Collections.enumeration(views);
   }

}
