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
import java.util.Iterator;
import org.apache.commons.digester.Digester;
import org.mobix.xml.*;
import org.xml.sax.SAXException;
import projectviewer.digester.*;


/**
 * Some utilities for manipulating {@link ProjectArtifact}s.
 */
public class ProjectArtifacts {

   /**
    * Returns a directory for the given artifact.  This will return
    * <code>null</code> if it does not know how to convert the artifact
    * to a directory.
    */
   public static File getDirectory(ProjectArtifact artifact) {
      if (artifact instanceof ProjectDirectory)
         return new File(((ProjectDirectory) artifact).getPath());
      if (artifact instanceof ProjectFile) {
         ProjectDirectory prjDir = (ProjectDirectory) ((ProjectFile) artifact).getParent();
         return new File(prjDir.getPath());
      }
      return null; 
   }

   /**
    * Returns the {@link FileView} that this artifact belongs to.
    */
   public static FileView getView(ProjectArtifact artifact) {
      if (artifact instanceof FileView) return (FileView) artifact;
      return getView((ProjectArtifact) artifact.getParent());
   }

}
