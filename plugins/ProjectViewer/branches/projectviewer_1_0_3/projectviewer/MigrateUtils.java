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

import java.io.*;
import org.gjt.sp.jedit.*;


/**
 * Some utilities for migrating old project resources.
 */
public class MigrateUtils {

   /**
    * Check all possible migrations strategies.
    */
   public static void checkAll() {
      check_1_1_1();
   }

   /**
    * Checks for 1.1.1 project files.
    */
   private static void check_1_1_1() {
      File oldPrjProps = new File(jEdit.getSettingsDirectory(), "ProjectViewer.projects.properties");
      if (!oldPrjProps.exists())
         return ;
      File newPrjProps = new File(ProjectPlugin.getResourcePath(ProjectManager.PROJECTS_PROPS_FILE));
      if (newPrjProps.exists())
         return ;
      File oldFilesProps = new File(jEdit.getSettingsDirectory(), "ProjectViewer.files.properties");
      File newFilesProps = new File(ProjectPlugin.getResourcePath(ProjectManager.FILE_PROPS_FILE));

      try {
         move(oldPrjProps, newPrjProps);
         move(oldFilesProps, newFilesProps);

      } catch (Exception e) {
         ProjectPlugin.error(e);
      }
   }

   /**
    * Perform a copy from one file to another.
    */
   private static void move(File src, File dest) throws IOException {
      OutputStream out = null;
      InputStream in = null;
      try {
         out = new FileOutputStream(dest);
         in = new FileInputStream(src);
         Pipe.pipe(in, out);
         src.delete();

      } finally {
         ProjectPlugin.close(in);
         ProjectPlugin.close(out);
      }
   }

}
