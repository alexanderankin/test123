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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import projectviewer.ui.ProjectViewer;


/**
 * A Project Viewer plugin for jEdit.
 *
 * @version $Id$
 */
public class ProjectPlugin extends EBPlugin {

   public final static String VERSION = "1.0.3";
   public final static String NAME = "projectviewer";

   private final static String LAST_PROJECT_PROPERTY_KEY = "plugin." + NAME + ".last-project";

   private static ProjectPlugin instance;

   private Map actionMap;
   private ViewManager viewManager;
   private ProjectManager projectManager;
   private boolean initialized;

   /**
    * Create a new <code>ProjectPlugin</code>.
    */
   public ProjectPlugin() {
      instance = this;
      initialized = false;
   }

   /**
    * Initialize the plugin.  This method should be called before any project
    * resources are requested.
    */
   public void init() {
      projectManager = new ProjectManager(this);
      viewManager = new ViewManager();
      initialized = true;
   }

   /**
    * Returns the project manager.
    */
   public ProjectManager getProjectManager() {
      return projectManager;
   }

   /**
    * Returns the {@link ViewManager}.
    */
   public ViewManager getViewManager() {
      return viewManager;
   }

   /**
    * Start the plugin.
    *
    * <p>SPECIFIED IN: org.gjt.sp.jedit.EditPlugin</p>
    */
   public void start() {
      EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, NAME);

      File f = new File( getPluginHome() );
      if ( !f.getParentFile().exists() )
         f.getParentFile().mkdirs();

      MigrateUtils.checkAll();
   }

   /**
    * Stop the plugin and save the project resources.
    */
   public void stop() {
      if (projectManager != null) projectManager.save();
   }

   /**
    * Returns the last project.
    */
   public Project getLastProject() {
      String lastProjectName = jEdit.getProperty( LAST_PROJECT_PROPERTY_KEY );
      Project prj = null;
      if (lastProjectName != null) {
         prj = loadProject(lastProjectName);
      }
      if (prj == null) {
         setLastProject(null);
      }
      return prj;
   }

   /**
    * Set the name of the last project.
    */
   public void setLastProject(String projectName) {
      jEdit.setProperty(LAST_PROJECT_PROPERTY_KEY, projectName);
   }

   /**
    * Handle messages from the <code>EditBus</code>.
    *
    * <p>SPECIFIED IN: org.gjt.sp.jedit.EBComponent</p>
    */
   public void handleMessage(EBMessage msg) {
      if ( !(msg instanceof CreateDockableWindow) )
         return ;
      CreateDockableWindow cmsg = (CreateDockableWindow) msg;
      if (cmsg.getDockableWindowName().equals(NAME)) {
         init();
         cmsg.setDockableWindow( new ProjectViewer(cmsg.getView(), this) );
      }
   }

   /**
    * Create the appropriate menu items for this plugin.
    */
   public void createMenuItems(Vector menuItems) {
      menuItems.addElement(GUIUtilities.loadMenuItem( "open-viewer-menu-item" ));
   }

   /**
    * Log an error message.
    */
   public static void error(Object msg) {
      Log.log(Log.ERROR, instance, msg);
   }

   /**
    * Close the specified <code>InputStream</code>.
    */
   public static void close( InputStream in ) {
      try {
         if ( in != null )
            in.close();
      } catch ( IOException e ) {
         error(e);
      }
   }

   /**
    * Close the specified <code>Reader</code>.
    */
   public static void close( Reader in ) {
      try {
         if ( in != null )
            in.close();
      } catch ( IOException e ) {
         error(e);
      }
   }

   /**
    * Close the specified <code>OutputStream</code>.
    */
   public static void close( OutputStream out ) {
      try {
         if ( out != null )
            out.close();
      } catch ( IOException e ) {
         error(e);
      }
   }

   /**
    * Close the specified <code>Writer</code>.
    */
   public static void close( Writer out ) {
      try {
         if ( out != null )
            out.close();
      } catch ( IOException e ) {
         error(e);
      }
   }

   /**
    * Returns an input stream to the specified resource, or <code>null</code> if none is
    * found.
    */
   public static InputStream getResourceAsStream( String name ) {
      try {
         return new FileInputStream( getResourcePath( name ) );
      } catch ( IOException e ) {
         return null;
      }
   }

   /**
    * Returns an output stream to the specified resource.
    */
   public static OutputStream getResourceAsOutputStream( String name ) throws IOException {
      return new FileOutputStream( getResourcePath( name ) );
   }

   /**
    * Returns the full path of the specified plugin resource.
    */
   public static String getResourcePath( String name ) {
      return getPluginHome() + File.separator + name;
   }

   /**
    * Returns the full path of the plugin's resource directory.
    */
   public static String getPluginHome() {
      return jEdit.getSettingsDirectory() + File.separator + NAME;
   }

   /**
    * Load a project.  If there is an error, log it and return <code>null</code>. 
    */
   private Project loadProject(String prjName) {
      try {
         return projectManager.loadProject(prjName);
      } catch (ProjectException e) {
         error(e);
         return null;
      }
   }

}
