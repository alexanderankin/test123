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
package projectviewer.tree;

import java.util.Iterator;
import projectviewer.*;


/**
 * A tree model that represents all files in all projects without any relationship
 * to the folders they are a child of. 
 */
public class AllProjectsFileFlatTreeModel
  extends AllProjectsTreeModel
{
  
  /**
   * Create a new <code>AllProjectsFileFlatTreeModel</code>.
   */
  public AllProjectsFileFlatTreeModel() {
    Iterator i = ProjectManager.getInstance().projects();
    while ( i.hasNext() )
      addModel( new ProjectFileFlatTreeModel( (Project) i.next() ) );
  }
  
}
