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
package projectviewer.ui.tree;

import java.util.*;
import javax.swing.tree.TreeNode;
import projectviewer.*;


/**
 * The root node.
 */
public class RootNode implements TreeNode
{

   private List projects;

   /**
    * Create a new <code>RootNode</code>.
    */
   public RootNode() {
      projects = new ArrayList();
   }

   /**
    * Clear any projects under root.
    */
   public void clear() {
      projects.clear();
   }
   
   /**
    * Returns a iteration of projects.
    */
   public Iterator projects() {
      return projects.iterator();
   }

   /**
    * Replace the given proxy with the actual project.
    */
   public void replaceProxy(ProjectProxyNode proxy) {
      int index = projects.indexOf(proxy);
      projects.set(index, proxy.getProxy().getProject());
   }

   /**
    * Add a proxy node.
    */
   public void addProxyNode(ProjectProxyNode proxy) {
      proxy.setRoot(this);
      projects.add(proxy);
   }

   /**
    * Add a project node.
    */
   public void addProjectNode(Project project) {
      project.setParent(this);
      projects.add(project);
   }

   /**
    * Returns <code>null</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public TreeNode getChildAt(int childIndex)
   {
      return (TreeNode) projects.get(childIndex);
   }

   /**
    * Returns <code>0</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public int getChildCount()
   {
      return projects.size();
   }

   /**
    * Returns <code>null</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public TreeNode getParent()
   {
      return null;
   }

   /**
    * Returns <code>-1</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public int getIndex(TreeNode node)
   {
      return projects.indexOf(node);
   }

   /**
    * Returns <code>true</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public boolean getAllowsChildren()
   {
      return true;
   }

   /**
    * Returns <code>false</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public boolean isLeaf()
   {
      return false;
   }

   /**
    * Returns the child projects.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public Enumeration children()
   {
      return Collections.enumeration(projects);
   }

}
