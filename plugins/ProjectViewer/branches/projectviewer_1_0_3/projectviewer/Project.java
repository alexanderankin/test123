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

import java.util.Iterator;
import javax.swing.tree.TreeNode;
import org.apache.commons.digester.Digester;
import org.mobix.xml.XmlWriteContext;
import org.xml.sax.SAXException;


/**
 * The project interface.
 */
public interface Project extends TreeNode, Comparable
{

   /**
    * Returns the name of the project.
    */
   public String getName();

   /**
    * Set the name of the project.
    */
   public void setName(String aName);

   /**
    * Returns the names of all available properties.
    */
   public Iterator propertyNames();

   /**
    * Set a project property.
    */
   public void setProperty(String name, String value);

   /**
    * Returns a project property.
    */
   public String getProperty(String name);

   /**
    * Remove a project property.
    */
   public void removeProperty(String name);

   /**
    * Add a view to this project.
    */
   public void addView(FileView aView);

   /**
    * Returns the number of views.
    */
   public int getViewCount();

   /**
    * Returns the view at the specified index.
    */
   public FileView getView(int index);

   /**
    * Returns an iteration of views.
    */
   public Iterator views();

   /**
    * Remove the given view.
    */
   public void remove(FileView view);

   /**
    * Save project data.
    */
   public void save(XmlWriteContext xmlWrite) throws SAXException;

   /**
    * Initialize this digester to load data into this project.
    */
   public void initDigester(Digester digester);

   /**
    * Set the parent node.
    */
   public void setParent(TreeNode node);

}
