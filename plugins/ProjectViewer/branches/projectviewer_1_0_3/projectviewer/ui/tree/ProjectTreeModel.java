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

import java.util.Iterator;
import javax.swing.tree.*;
import org.gjt.sp.util.Log;
import projectviewer.*;
import projectviewer.ui.ProjectViewer;


/**
 * The project tree model.
 */
public class ProjectTreeModel extends DefaultTreeModel {

   private ProjectViewer projectViewer;

   /**
    * Create a new <code>ProjectTreeModel</code>.
    */
   public ProjectTreeModel(ProjectViewer aProjectViewer) {
      super(new RootNode());
      projectViewer = aProjectViewer;
      load();
   }

   /**
    * Create a new <code>ProjectTreeModel</code>.
    */
   public ProjectTreeModel(RootNode root) {
      super(root);
   }

   /**
    * Load the projects.
    */
   public void load() {
      RootNode root = getRootNode();
      root.clear();
      String[] prjNames = getProjectManager().getProjectNames();
      for (int i = 0; i < prjNames.length; i++) {
         ProjectProxy proxy = new ProjectProxy(prjNames[i], getProjectManager());
         //root.addProxyNode(new ProjectProxyNode(proxy));
         root.addProjectNode(proxy);
      }
      nodeStructureChanged(root);
   }

   /**
    * Returns the root node.
    */
   public RootNode getRootNode() {
      return (RootNode) root;
   }

   /**
    * Returns the path to root.
    *
    * <p>SPECIFIED IN: javax.swing.tree.DefaultTreeModel</p>
    */
   public TreeNode[] getPathToRoot(TreeNode node) {
      TreeNode[] path = super.getPathToRoot(node);
      if (path.length > 1 && !(path[1] instanceof ProjectProxy)) {
         path[1] = getProxyFor((Project) path[1]);
      }
      return path;
   }

   /**
    * Fire a tree structure changed event.
    *
    * <p>SPECIFIED IN: javax.swing.tree.DefaultTreeModel</p>
    */
   protected void fireTreeStructureChanged(Object source, Object[] path,
                                           int[] childIndicies,
                                           Object[] children)
   {
      /*if (path.length > 1 && path[1] instanceof ProjectProxyNode) {
         RootNode root = getRootNode();
         root.replaceProxy((ProjectProxyNode) path[1]);
         path[1] = ((ProjectProxyNode) path[1]).getProxy().getProject();
      }*/
      super.fireTreeStructureChanged(source, path, childIndicies, children);
   }

   /**
    * Returns the proxy that is current proxying for the given project.
    */
   private Project getProxyFor(Project project) {
      for (Iterator i = getRootNode().projects(); i.hasNext();) {
         Project proxy = (Project) i.next();
         if (proxy.getName().equals(project.getName()))
            return proxy;
      }
      return project;
   }

   /**
    * Returns the project manager.
    */
   private ProjectManager getProjectManager() {
      return projectViewer.getPlugin().getProjectManager();
   }

}
