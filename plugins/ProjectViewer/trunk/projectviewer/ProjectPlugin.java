/* $Id$
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import projectviewer.config.ProjectViewerConfig;
import projectviewer.config.ProjectViewerOptionsPane;
import projectviewer.config.ProjectAppConfigPane;

/**
 * A Project Viewer plugin for jEdit.
 *
 *@author     <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 *@author     <A HREF="mailto:cyu77@yahoo.com">Calvin Yu</A>
 *@author     <A HREF="mailto:ensonic@sonicpulse.de">Stefan Kost</A>
 *@author     <A HREF="mailto:webmaster@sutternow.com">Matthew Payne</A>
 *@version    1.0.5
 */
public final class ProjectPlugin extends EBPlugin {

   public final static String NAME = "projectviewer";
    
    private final static ProjectViewerConfig config = ProjectViewerConfig.getInstance();

   /** Returns an input stream to the specified resource, or <code>null</code> if none is
    * found.
    *
    *@param  name  Description of Parameter
    *@return       The resourceAsStream value
    */
   public static InputStream getResourceAsStream(String name) {
      try {
         return new FileInputStream(getResourcePath(name));
      }
      catch (IOException e) {
         return null;
      }
   }

   /** Returns an output stream to the specified resource, or <code>null</node> if access
    * to that resource is denied.
    *
    *@param  name  Description of Parameter
    *@return       The resourceAsOutputStream value
    */
   public static OutputStream getResourceAsOutputStream(String name) {
      try {
         return new FileOutputStream(getResourcePath(name));
      }
      catch (IOException e) {
         return null;
      }
   }

   /** Returns the full path of the specified plugin resource.
    *
    *@param  name  Description of Parameter
    *@return       The resourcePath value
    */
   public static String getResourcePath(String name) {
      return jEdit.getSettingsDirectory()
             + File.separator + NAME
             + File.separator + name;
   }

   /** Returns the last project name.
    *
    *@return    The lastProject value
    */
   public static String getLastProject() {
      return config.getLastProject();
   }

   /** Start the plugin. */
   public void start() {
      // not needed anymore (replaced by dockables.xml)
      //EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, NAME);

      File f = new File(getResourcePath("projects/null"));
      if (!f.getParentFile().exists())
         f.getParentFile().mkdirs();
            
      checkOldProperties();
   }

   /**
    * Stop the plugin and save the project resources.
    */
   public void stop() {
      ProjectManager.getInstance().save();
	  Project current = ProjectManager.getInstance().getCurrentProject();
      config.setLastProject( (current != null) ? current.getName() : null );
      config.save();
   }

   /** Handle messages from the <code>EditBus</code>.
    *
    *@param  msg  Description of Parameter
    */
   public void handleMessage(EBMessage msg) {
      //if(msg instanceof CreateDockableWindow) {
      //  CreateDockableWindow cmsg = (CreateDockableWindow) msg;
      //  if(cmsg.getDockableWindowName().equals(NAME))
      //    cmsg.setDockableWindow( viewer = new ProjectViewer(cmsg.getView()) );
      //}
   }

   /** Create the appropriate menu items for this plugin.
    *
    *@param  menuItems  Description of Parameter
    */
   public void createMenuItems(Vector menuItems) {
      menuItems.addElement(GUIUtilities.loadMenuItem("open-viewer-menu-item"));
   }

    /**
     *  Add out option pane to jEdit's option dialog.
     */
    public void createOptionPanes(OptionsDialog optionsDialog) {
   OptionGroup optionGroup = new OptionGroup(NAME);
   optionGroup.addOptionPane(new ProjectViewerOptionsPane("ProjectViewer"));
   optionGroup.addOptionPane(new ProjectAppConfigPane("projectviewer.appconfig"));
   optionsDialog.addOptionGroup(optionGroup);
        //optionsDialog.addOptionPane();
    }

   /** Perform a check for old project properties files.
    * If they exist and new properties files doesn't, then convert the old to the new.
    */
   private void checkOldProperties() {
      File oldPrjProps = new File(jEdit.getSettingsDirectory(), "ProjectViewer.projects.properties");
      if (!oldPrjProps.exists())
         return;

      File newPrjProps = new File(getResourcePath(ProjectManager.PROJECTS_PROPS_FILE));
      if (newPrjProps.exists())
         return;

      File oldFilesProps = new File(jEdit.getSettingsDirectory(), "ProjectViewer.files.properties");
      File newFilesProps = new File(getResourcePath(ProjectManager.FILE_PROPS_FILE));

      try {
         move(oldPrjProps, newPrjProps);
         move(oldFilesProps, newFilesProps);

      }
      catch (Exception e) {
         Log.log(Log.ERROR, this, e);
         displayError("Unable to convert old projects files");
      }
   }

   /** Perform a copy from one file to another.
    *
    *@param  src              Description of Parameter
    *@param  dest             Description of Parameter
    *@exception  IOException  Description of Exception
    */
   private void move(File src, File dest) throws IOException {
      OutputStream out = null;
      InputStream in = null;
      try {
         out = new FileOutputStream(dest);
         in = new FileInputStream(src);
         Pipe.pipe(in, out);
         src.delete();

      }
      finally {
         close(in);
         close(out);
      }
   }

   /** Display an error to the user.
    *
    *@param  msg  Description of Parameter
    */
   private void displayError(String msg) {
      JOptionPane.showMessageDialog(null, msg,
            jEdit.getProperty("projectviewer.label"),
            JOptionPane.ERROR_MESSAGE);

   }

   /** Close the specified <code>InputStream</code>.
    *
    *@param  in  Description of Parameter
    */
   private void close(InputStream in) {
      try {
         if (in != null)
            in.close();
      }
      catch (IOException e) {
         Log.log(Log.WARNING, this, e);
      }
   }

   /** Close the specified <code>OutputStream</code>.
    *
    *@param  out  Description of Parameter
    */
   private void close(OutputStream out) {
      try {
         if (out != null)
            out.close();
      }
      catch (IOException e) {
         Log.log(Log.WARNING, this, e);
      }
   }

}

