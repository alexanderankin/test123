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
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import org.mobix.io.Pipe;


/**
 * A Project Viewer plugin for jEdit.
 *
 * @version $Id$
 */
public class ProjectPlugin extends EBPlugin {
    
  public static final String NAME = "projectviewer";
  
  private final static String LAST_PROJECT_PROPERTY_KEY = "plugin." + NAME + ".last-project";
  
  private ProjectViewer viewer;
  
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
   * Returns an output stream to the specified resource, or <code>null</node> if access
   * to that resource is denied.
   */
  public static OutputStream getResourceAsOutputStream( String name ) {
    try {
      return new FileOutputStream( getResourcePath( name ) );
    } catch ( IOException e ) {
      return null;
    }
  }
  
  /**
   * Returns the full path of the specified plugin resource.
   */
  public static String getResourcePath( String name ) {
    return jEdit.getSettingsDirectory()
      + System.getProperty("file.separator") + NAME
      + System.getProperty("file.separator") + name;
  }

  /**
   * Start the plugin.
   */
  public void start() {
    EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, NAME);
    
    //parse out the resources as a thread so that when the plugin is 
    //requested there is nothing to do.
    new ThreadedParser().start();
    
    File f = new File( getResourcePath( "null" ) );
    if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
    
    File importFile = new File( getResourcePath( ProjectFileImporter.PROPS_FILE ) );
    if ( !importFile.exists() ) {
      OutputStream out = null;
      InputStream in = null;
      try {
        out = new FileOutputStream( importFile );
        in = getClass().getResourceAsStream( "import-sample.properties" );
        Pipe.pipe( in, out );
        
      } catch ( IOException e ) {
        Log.log( Log.WARNING, this, e );
        
      } finally {
        close( in );
        close( out );
      }
    }
  }
  
  /**
   * Stop the plugin and save the project resources.
   */    
  public void stop() {
    ProjectManager.getInstance().save();
    if ( viewer != null ) {
      jEdit.setProperty( LAST_PROJECT_PROPERTY_KEY, 
        !viewer.isAllProjects() ? viewer.getCurrentProject().getName() : "" );
    }
  }
  
  /**
   * Returns the last project name.
   */
  public static String getLastProject() {
    return jEdit.getProperty( LAST_PROJECT_PROPERTY_KEY );
  }

  /**
   * Handle messages from the <code>EditBus</code>.
   */
  public void handleMessage(EBMessage msg) {
    if(msg instanceof CreateDockableWindow) {
      CreateDockableWindow cmsg = (CreateDockableWindow) msg;
      if(cmsg.getDockableWindowName().equals(NAME))
        cmsg.setDockableWindow( viewer = new ProjectViewer(cmsg.getView()) );
    }
  }

  public void createMenuItems(Vector menuItems) {
    menuItems.addElement(GUIUtilities.loadMenuItem( "open-viewer-menu-item" ));
  }
  
  /**
   * Close the specified <code>InputStream</code>.
   */
  private void close( InputStream in ) {
    try {
      if ( in != null ) in.close();
    } catch ( IOException e ) {
      Log.log( Log.WARNING, this, e );
    }
  }
  
  /**
   * Close the specified <code>OutputStream</code>.
   */
  private void close( OutputStream out ) {
    try {
      if ( out != null ) out.close();
    } catch ( IOException e ) {
      Log.log( Log.WARNING, this, e );
    }
  }
  
}

