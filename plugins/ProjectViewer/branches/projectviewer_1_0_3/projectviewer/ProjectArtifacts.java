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

   /**
    * Load the necessary digester rules to build the file view graph.
    */
   public static void initDigester(FileView view, Digester digester) {
      digester.addRule("*/view/param", new LoadParamRule(digester)); 
      digester.addCallMethod("*/file", "addFile", 2, new Class[] {String.class, String.class});
      digester.addCallParam("*/file", 0, "name");
      digester.addCallParam("*/file", 1, "path");
      digester.addRule("*/dir", new CreateDirectoryRule(digester));
   }

   /**
    * Create the mandatory attributes for a {@link FileView} element.
    *
    * @see DefaultFileView#save(XmlWriteContext).
    */
   public static SimpleAttributes createXmlAttributes(FileView view) {
      SimpleAttributes atts = new SimpleAttributes("name", view.getName());
      atts.addAttribute("type", view.getClass().getName());
      return atts;
   }

   /**
    * Save the given project file.
    */
   public static void save(ProjectFile file, XmlWriteContext xmlWrite) throws SAXException {
      SimpleAttributes atts = new SimpleAttributes("name", file.getName());
      atts.addAttribute("path", file.getPath());
      xmlWrite.writeElement("file", atts);
   }

   /**
    * Save the given project directory.
    */
   public static void save(ProjectDirectory dir, XmlWriteContext xmlWrite) throws SAXException {
      SimpleAttributes atts = new SimpleAttributes("name", dir.getName());
      atts.addAttribute("path", dir.getPath());
      xmlWrite.startElement("dir", atts);
      saveChildren(dir, xmlWrite);
      xmlWrite.endElement("dir");
   }
   
   /**
    * Save the children of the given directory.
    */
   public static void saveChildren(ProjectDirectory dir, XmlWriteContext xmlWrite)
   throws SAXException
   {
      for (Iterator i = dir.directories(); i.hasNext();)
         save((ProjectDirectory) i.next(), xmlWrite);
      for (Iterator i = dir.files(); i.hasNext();)
         save((ProjectFile) i.next(), xmlWrite);
   }

}
