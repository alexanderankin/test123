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

import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import projectviewer.ProjectProxy;


/**
 * A tree node that is the name of the project.
 */
public class ProjectProxyNode implements TreeNode
{

   private RootNode parent;
   private ProjectProxy proxy;


   /**
    * Create a new <code>ProjectProxyNode</code>.
    */
   public ProjectProxyNode(ProjectProxy aProxy)
   {
      proxy = aProxy;
   }

   /**
    * Set the root.
    */
   public void setRoot(RootNode root) {
      parent = root;
   }

   /**
    * Returns the proxy.
    */
   public ProjectProxy getProxy()
   {
      return proxy;
   }

   /**
    * Convert this node to a string.  Returns the name of the 
    * project.
    *
    * <p>SPECIFIED IN: java.lang.Object</p>
    */
   public String toString()
   {
      return proxy.getName();
   }

   /**
    * Returns <code>null</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public TreeNode getChildAt(int childIndex)
   {
      return null;
   }

   /**
    * Returns <code>0</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public int getChildCount()
   {
      return 0;
   }

   /**
    * Returns the parent <code>TreeNode</code> of the receiver.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public TreeNode getParent()
   {
      return parent;
   }

   /**
    * Returns <code>-1</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public int getIndex(TreeNode node)
   {
      return -1;
   }

   /**
    * Returns <code>true</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public boolean getAllowsChildren()
   {
      return false;
   }

   /**
    * Returns <code>true</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public boolean isLeaf()
   {
      return true;
   }

   /**
    * Returns <code>null</code>.
    *
    * <p>SPECIFIED IN: javax.swing.tree.TreeNode</p>
    */
   public Enumeration children()
   {
      return null;
   }

}
