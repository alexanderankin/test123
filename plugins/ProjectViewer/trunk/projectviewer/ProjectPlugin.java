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


/**

A Project Viewer plugin for jEdit.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
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
  Stop the plugin and save the project resources.
  
  @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
  @version $Id$
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

  /**
  Start the plugin

  @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
  @version $Id$
  */
  public void start() {
    EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, NAME);
    
    //parse out the resources as a thread so that when the plugin is 
    //requested there is nothing to do.
    new ThreadedParser().start();
    
    File f = new File( getResourcePath( "null" ) );
    if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
  }
  
  public void createMenuItems(Vector menuItems) {
    menuItems.addElement(GUIUtilities.loadMenuItem( "open-viewer-menu-item" ));
  }
  
}

