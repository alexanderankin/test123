/*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more detaProjectTreeSelectionListenerils.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package projectviewer.ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.util.Log;
import projectviewer.*;
import projectviewer.event.*;
import projectviewer.ui.tree.*;


/**
 * The main GUI of the Project Viewer plugin.
 *
 * @version $Revision$
 */
public class ProjectViewer extends JPanel
         implements DockableWindow, TreeSelectionListener
{

   private View view;
   private ProjectPlugin plugin;
   private Project project;
   private ProjectTreeModel treeModel;
   private JTree projectTree;

   /**
    * Create a new <code>ProjectViewer</code>.
    */
   public ProjectViewer(View aView, ProjectPlugin aPlugin)
   {
      view = aView;
      plugin = aPlugin;
      initComponents();
      Project prj = plugin.getLastProject();
      if (prj == null) {
         Iterator projects = getRoot().projects();
         if (projects.hasNext()) {
            prj = (Project) projects.next();
         } else {
            prj = new SimpleProject("Default");
            try {
               plugin.getProjectManager().addProject(prj);
            } catch (ProjectException e) {
               ProjectPlugin.error(e);
            }
         }
      }
      setProject(prj);
   }

   /**
    * Add a {@link ProjectViewerListener}.
    */
   public void addProjectViewerListener(ProjectViewerListener listener)
   {
      listenerList.add(ProjectViewerListener.class, listener);
   }

   /**
    * Remove a {@link ProjectViewerListener}.
    */
   public void removeProjectViewerListener(ProjectViewerListener listener)
   {
      listenerList.remove(ProjectViewerListener.class, listener);
   }

   /**
    * Returns the name of the dockable window.
    *
    * <p>SPECIFIED IN: org.gjt.sp.jedit.gui.DockableWindow</p>
    */
   public String getName()
   {
      return ProjectPlugin.NAME;
   }

   /**
    * Returns the actual component used for the document window.
    *
    * <p>SPECIFIED IN: org.gjt.sp.jedit.gui.DockableWindow</p>
    */
   public Component getComponent()
   {
      return this;
   }

   /**
    * Set the current project.
    */
   public void setProject(Project aProject)
   {
      plugin.setLastProject(aProject.getName());
      project = aProject;
      setTreeSelection(project);
      fireProjectLoaded(project);
   }

   /**
    * Returns the plugin.
    */
   public ProjectPlugin getPlugin()
   {
      return plugin;
   }

   /**
    * Returns the root node.
    */
   public RootNode getRoot()
   {
      return treeModel.getRootNode();
   }

   /**
    * Returns the current tree model.
    */
   public ProjectTreeModel getTreeModel()
   {
      return treeModel;
   }

   /**
    * Returns the currently selected project.
    */
   public Project getCurrentProject()
   {
      return project;
   }

   /**
    * Returns the project tree.
    */
   public JTree getProjectTree()
   {
      return projectTree;
   }

   /**
    * Returns the current selected tree node.
    */
   public Object getSelectedNode()
   {
      TreePath path = projectTree.getSelectionPath();
      if (path == null)
         return null;
      return path.getLastPathComponent();
   }

   /**
    * Set the selected node of the tree.
    */
   public void setTreeSelection(TreeNode node)
   {
      if (node == null) return;
      getProjectTree().setSelectionPath(new TreePath(getTreeModel().getPathToRoot(node)));
   }

   /**
    * Returns the view.
    */
   public View getView()
   {
      return view;
   }

   /**
    * Receive notice that a tree node has been selected.
    *
    * <p>SPECIFIED IN: javax.swing.event.TreeSelectionListener</p> 
    */
   public void valueChanged(TreeSelectionEvent evt) {}

   /**
    * Initialize the components.
    */
   private void initComponents()
   {
      setLayout(new BorderLayout());

      treeModel = new ProjectTreeModel(this);
      projectTree = new ProjectTree(treeModel);
      projectTree.addTreeSelectionListener(this);
      projectTree.addMouseListener(new ProjectTreeMouseListener(this));
      add(new JScrollPane(projectTree));
   }

   /**
    * Notify listeners that a new project has been loaded.
    */
   private void fireProjectLoaded(Project project)
   {
      ProjectViewerEvent evt = new ProjectViewerEvent(this, project);
      EventListener[] listeners = listenerList.getListeners(ProjectViewerListener.class);
      for (int i = 0; i < listeners.length; i++) {
         ((ProjectViewerListener) listeners[i]).projectLoaded(evt);
      }
   }

}
