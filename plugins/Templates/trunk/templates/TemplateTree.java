/*
 * TemplateTree.java
 * :tabSize=3:indentSize=3:noTabs=true:
 *
 * Copyright (c) 2002 Calvin Yu, Steve Jakob
 *
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
package templates;

import java.util.*;
import javax.swing.JTree;
import javax.swing.tree.*;
import org.gjt.sp.util.Log;

/**
 * A template tree.
 */
public class TemplateTree extends JTree
{

   private TreeNode root;

   /**
    * Create a new <code>TemplateTree</code>.
    */
   public TemplateTree()
   {
      super(TemplatesPlugin.getTemplates());
      root = TemplatesPlugin.getTemplates();
      setRootVisible(false);
   }

   /**
    * Reload this tree.
    */
   public void reload()
   {
		// Log.log(Log.DEBUG,this,"... TemplateTree.reload()");
      root = TemplatesPlugin.getTemplates();
      this.setModel(new DefaultTreeModel(root));
      // Log.log(Log.DEBUG,this,((TemplateDir)root).printDir());
   }

   /**
    * Returns <code>true</code> if the selected node is a template.
    */
   public boolean isTemplateSelected()
   {
      return isLastPathComponentATemplate(getSelectionPath());
   }

   /**
    * Collapse all templates.
    */
   public void collapseAll()
   {
      int count = getRowCount();
      for (int i=0; i<count; i++) {
         collapseRow(i);
      }
   }

   /**
    * Set the selected template.
    */
   public void setSelectedTemplate(String templatePath)
   {
      if (templatePath == null) {
         return;
      }
      StringTokenizer strtok = new StringTokenizer(templatePath, "/");
      List path = new ArrayList();
      while (strtok.hasMoreTokens()) {
         path.add(strtok.nextToken());
      }
      collapseAll();
      setSelectionPath(findTreePath(path.toArray()));
   }

   /**
    * Returns the currently selected insert path.
    */
   public String getSelectedTemplate()
   {
      TreePath path = getSelectionPath();
      TemplateFile selectedTemplate = (TemplateFile)path.getLastPathComponent();
      if (selectedTemplate.isDirectory()) {
        throw new IllegalStateException("Cannot map to a directory");
      }
      return selectedTemplate.getRelativePath();
   }

   /**
    * Returns <code>true</code> if the given path points to a template.
    */
   public boolean isLastPathComponentATemplate(TreePath path)
   {
      if (path == null) {
         return false;
      }
      TreeNode node = (TreeNode) path.getLastPathComponent();
      return node.isLeaf();
   }

   /**
    * Find the tree path given user object path.
    */
   private TreePath findTreePath(Object[] objPath)
   {
      // NOTE: the supplied object path is an array of Strings
      List path = new LinkedList();
      path.add(root);
      for (int i=0; i<objPath.length; i++) {
         if (!findChildNode(objPath, i, path)) {
            return null;
         }
      }
      return new TreePath(path.toArray());
   }

   /**
    * Find the given node with the indexed use object and add it to <code>path</code>.
    */
   private boolean findChildNode(Object[] objPath, int idx, List path)
   {
      Object target = objPath[idx];
      TreeNode parent = (TreeNode) path.get(path.size() - 1);
      Enumeration children = parent.children();
      while (children.hasMoreElements()) {
         TreeNode node = (TreeNode) children.nextElement();
         if ((((TemplateFile)node).getRelativePath()).endsWith((String)target)) {
            path.add(node);
            return true;
         }
      }
      return false;
   }

}

