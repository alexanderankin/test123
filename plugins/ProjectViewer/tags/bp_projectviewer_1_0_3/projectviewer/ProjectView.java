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

import javax.swing.tree.TreeModel;


/**
 * Tree root.
 */
interface ProjectView {
  
  /**
   * Activate the view.
   */
  public void activate();
  
  /**
   * Deactivate the view.
   */
  public void deactivate();
  
  /**
   * Returns the current project.
   */
  public Project getCurrentProject();
  
  /**
   * Returns the tree model for the folder view.
   */
  public TreeModel getFolderViewModel();
  
  /**
   * Returns the tree model for the file view. 
   */
  public TreeModel getFileViewModel();
  
  /**
   * Returns the tree model for the working files view.
   */
  public TreeModel getWorkingFileViewModel();
  
}
