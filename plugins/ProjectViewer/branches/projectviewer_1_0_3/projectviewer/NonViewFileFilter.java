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

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * A file filter that filters for files that are not in the give 
 * {@link FileView}.
 */
public class NonViewFileFilter extends FileFilter {

   private FileView view;

   /**
    * Create a new <code>NonViewFileFilter</code>.
    */
   public NonViewFileFilter(FileView aView) {
      view = aView;
   }

   /**
    * Accept files that are not in the given view.
    */
   public boolean accept( File f ) {
      if (f.isDirectory()) return true;
      return !view.isProjectFile(f);
   }

   /**
    * Returns a description of this filter.
    */
   public String getDescription() {
      return "Unadded Files";
   }

}
