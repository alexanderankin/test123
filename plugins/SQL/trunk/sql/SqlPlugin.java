/**
 * SqlPlugin.java - Sql Plugin
 * Copyright (C) 26 ������ 2001 �. Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
 *
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
package sql;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

import projectviewer.*;
import projectviewer.event.*;
import projectviewer.config.*;
import projectviewer.vpt.*;

import sql.*;
import sql.options.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ������ 2001 �.
 */
public class SqlPlugin extends EBPlugin
     implements ProjectOptionsPlugin, ProjectViewerListener
{
  protected Hashtable sqlToolBars = new Hashtable();
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String NAME = "Sql Plugin";
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String DEFAULT_EDIT_MODE_NAME = "transact-sql";

  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String resultSetWinName = "sql.resultSet";
  /**
   *  Description of the Field
   *
   * @since
   */
  public static ImageIcon Icon;

  protected static SqlVFS sqlVFS;


  /**
   *  Description of the Method
   *
   * @since
   */
  public void start()
  {
    final File settingsDir = new File( MiscUtilities.constructPath(
        jEdit.getSettingsDirectory(), "sql" ) );
    if ( !settingsDir.exists() )
      settingsDir.mkdirs();

    VFSManager.registerVFS( SqlVFS.PROTOCOL, sqlVFS = new SqlVFS() );

    registerJdbcClassPath();

    SqlUtils.init();
  }


  /**
   *  Description of the Method
   *
   * @param  menuItems  Description of Parameter
   * @since
   */
  public void createMenuItems( Vector menuItems )
  {
    menuItems.addElement( GUIUtilities.loadMenu( "sqlMenu" ) );
  }


  /**
   *  Description of the Method
   *
   * @param  optionsDialog  Description of Parameter
   * @param  project        Description of Parameter
   */
  public void createOptionPanes( OptionsDialog optionsDialog, VPTProject project )
  {
    final OptionGroup group = new OptionGroup( "sql" );
    group.addOptionPane( new ServersOptionPane( project ) );
    optionsDialog.addOptionGroup( group );
  }


  /**
   *  Description of the Method
   *
   * @param  optionsDialog  Description of Parameter
   * @since
   */
  public void createOptionPanes( OptionsDialog optionsDialog )
  {
    final OptionGroup group = new OptionGroup( "sql" );
    group.addOptionPane( new GeneralOptionPane() );
    group.addOptionPane( new JdbcOptionPane() );

    final OptionGroup pgroup = new OptionGroup( "sql.preprocessors" );
    final java.util.Map l = SqlUtils.getPreprocessors();

    for ( Iterator i = l.values().iterator(); i.hasNext();  )
    {
      final Preprocessor pr = (Preprocessor) i.next();
      final OptionPane op = pr.getOptionPane();
      if ( op != null )
        pgroup.addOptionPane( op );
    }
    group.addOptionGroup( pgroup );
    optionsDialog.addOptionGroup( group );
  }


  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   * @since
   */
  public void handleMessage( EBMessage message )
  {
    if ( message instanceof ViewUpdate )
    {
      final ViewUpdate vu = (ViewUpdate) message;
      final View view = vu.getView();
      if ( vu.getWhat() == ViewUpdate.CREATED )
      {
        final VPTProject project = SqlUtils.getProject( view );
        Log.log( Log.DEBUG, SqlPlugin.class, "new View " + view + " got project " + project );

        removeToolBar( view );
        if ( SqlToolBar.showToolBar() )
        {
          addToolBar( view, project );
        }

        ProjectViewer.addProjectViewerListener( this, view );
      }
      else if ( vu.getWhat() == ViewUpdate.CLOSED )
      {
        sqlToolBars.remove( view );
        ProjectViewer.removeProjectViewerListener( this, view );
      }
    }
    else if ( message instanceof PropertiesChanged )
    {
      Log.log( Log.DEBUG, SqlPlugin.class, "properties changed!" );
      handlePropertiesChanged();
    }
  }


  /**
   *  Description of the Method
   *
   * @param  evt  Description of Parameter
   */
  public void projectLoaded( ProjectViewerEvent evt )
  {
    final VPTProject project = evt.getProject();
    final View view = evt.getProjectViewer().getView();
    Log.log( Log.DEBUG, SqlPlugin.class,
        "Loading the project [" + project + "]" );

    removeToolBar( view );
    if ( SqlToolBar.showToolBar() )
    {
      addToolBar( view, project );
    }
  }


  private void handlePropertiesChanged()
  {
    final boolean show = SqlToolBar.showToolBar();
    View view = jEdit.getFirstView();

    while ( view != null )
    {
      removeToolBar( view );
      if ( show )
        addToolBar( view, SqlUtils.getProject( view ) );
      view = view.getNext();
    }
  }


  private void addToolBar( View view, VPTProject project )
  {
    // create new
    final SqlToolBar toolbar = new SqlToolBar( view, project );
    sqlToolBars.put( view, toolbar );
    view.addToolBar( toolbar );
  }


  private void removeToolBar( View view )
  {
    final SqlToolBar toolbar = (SqlToolBar) sqlToolBars.get( view );
    if ( toolbar != null )
    {
      // Try to remove toolbar
      // (this does nothing if there is no toolbar)
      view.removeToolBar( toolbar );
      sqlToolBars.remove( view );
    }
  }



  /**
   *  Sets the GlobalProperty attribute of the SqlPlugin class
   *
   * @param  name   The new GlobalProperty value
   * @param  value  The new GlobalProperty value
   */
  public static void setGlobalProperty( String name, String value )
  {
    jEdit.setProperty( name, value );
  }


  /**
   *  Sets the LocalProperty attribute of the SqlPlugin class
   *
   * @param  name     The new LocalProperty value
   * @param  value    The new LocalProperty value
   * @param  project  The new LocalProperty value
   */
  public static void setLocalProperty( VPTProject project, String name, String value )
  {
    project.setProperty( name, value );
  }


  /**
   *  Sets the BufferMode attribute of the SqlPlugin class
   *
   * @param  buf   The new BufferMode value
   * @param  name  The new BufferMode value
   * @since
   */
  public static void setBufferMode( Buffer buf, String name )
  {
    final Mode mode = jEdit.getMode( name );
    if ( mode != null )
      buf.setMode( mode );
  }


  /**
   *Sets the JdbcClassPath attribute of the SqlPlugin class
   *
   * @param  jdbcClassPath  The new JdbcClassPath value
   * @since
   */
  public static void setJdbcClassPath( String[] jdbcClassPath )
  {
    final String[] oldCp = getJdbcClassPath();

    unregisterJdbcClassPath();

    for ( int i = oldCp.length; --i >= 0;  )
      unsetGlobalProperty( "sql.jdbcClassPath." + i );

    if ( jdbcClassPath != null )
    {
      for ( int i = jdbcClassPath.length; --i >= 0;  )
        setGlobalProperty( "sql.jdbcClassPath." + i, jdbcClassPath[i] );

    }
    registerJdbcClassPath();
  }


  /**
   *  Gets the LocalProperty attribute of the SqlPlugin class
   *
   * @param  name     Description of Parameter
   * @param  project  Description of Parameter
   * @return          The LocalProperty value
   */
  public static String getLocalProperty( VPTProject project, String name )
  {
    try
    {
      Log.log( Log.DEBUG, SqlPlugin.class,
          "Looking for the property [" + name + "] of " + project );
      final String val = project.getProperty( name );
      Log.log( Log.DEBUG, SqlPlugin.class,
          "Found [" + val + "]" );
      return val;
    } catch ( NullPointerException ex )
    {
      Log.log( Log.DEBUG, SqlPlugin.class,
          "Error!" );
      ex.printStackTrace();
      return null;
    }
  }


  /**
   *  Gets the GlobalProperty attribute of the SqlPlugin class
   *
   * @param  name  Description of Parameter
   * @return       The GlobalProperty value
   */
  public static String getGlobalProperty( String name )
  {
    return jEdit.getProperty( name );
  }


  /**
   *Gets the JdbcClassPath attribute of the SqlPlugin class
   *
   * @return    The JdbcClassPath value
   * @since
   */
  public static String[] getJdbcClassPath()
  {
    final java.util.List v = new ArrayList();
    int i = 0;
    while ( true )
    {
      final String s = getGlobalProperty( "sql.jdbcClassPath." + i++ );
      if ( s == null )
        break;
      v.add( s );
    }
    return (String[]) v.toArray( new String[0] );
  }


  /**
   *Description of the Method
   *
   * @param  project  Description of Parameter
   * @since
   */
  public static void clearLocalProperties( VPTProject project )
  {
    SqlServerRecord.clearProperties( project );
  }


  /**
   *  Description of the Method
   */
  public static void clearGlobalProperties()
  {
    ResultSetWindow.clearProperties();
    SqlToolBar.clearProperties();
  }



  /**
   *  Description of the Method
   *
   * @param  name  Description of Parameter
   */
  public static void unsetGlobalProperty( String name )
  {
    jEdit.unsetProperty( name );
  }


  /**
   *  Description of the Method
   *
   * @param  name     Description of Parameter
   * @param  project  Description of Parameter
   */
  public static void unsetLocalProperty( VPTProject project, String name )
  {
    project.removeProperty( name );
  }


  /**
   *  Description of the Method
   *
   * @param  view  Description of Parameter
   * @return       Description of the Returned Value
   * @since
   */
  public static ResultSetWindow showResultSetWindow( View view )
  {
    final DockableWindowManager dockableWindowManager = view.getDockableWindowManager();
    if ( !dockableWindowManager.isDockableWindowVisible( resultSetWinName ) )
      dockableWindowManager.addDockableWindow( resultSetWinName );

    dockableWindowManager.showDockableWindow( resultSetWinName );

    return (ResultSetWindow) dockableWindowManager.getDockable( resultSetWinName );
  }


  /**
   *  Description of the Method
   *
   * @param  view        Description of Parameter
   * @param  serverName  Description of Parameter
   * @since
   */
  public static void loadObject( final View view, final String serverName )
  {
    SqlUtils.getThreadGroup().runInGroup(
      new Runnable()
      {
        public void run()
        {
          final SqlServerRecord rec = SqlUtils.getServerRecord( SqlUtils.getProject( view ), serverName );
          if ( rec == null )
            return;

          Connection conn = null;
          try
          {
            conn = rec.allocConnection();

            final String user = rec.getProperty( rec.USER ).toUpperCase();
            final Object[] objs = SqlUtils.loadObjectList( view, conn, rec, user );
            if ( objs == null )
              return;

            final DbCodeObject dbobj = chooseCodeObjectInAWTThread( view, objs );
            if ( dbobj == null )
              return;

            final String text = SqlUtils.loadObjectText( conn, rec, user, dbobj.name, dbobj.type );

            if ( text == null )
            {
              Log.log( Log.NOTICE, SqlPlugin.class,
                  "Got null retrieving the object text for " + dbobj.name );
              return;
            }

            SqlUtils.runInAWTThreadNoWait(
              new Runnable()
              {
                public void run()
                {
                  final Buffer buf = jEdit.newFile( view );
                  buf.insert( 0, text );
                  setBufferMode( buf, rec.getServerType().getEditModeName() );
                }
              } );

          } catch ( SQLException ex )
          {
            SqlUtils.processSqlException( view, ex, "??", rec );
          } finally
          {
            rec.releaseConnection( conn );
          }
        }
      } );
  }



  /**
   *  Description of the Method
   *
   * @param  view        Description of Parameter
   * @param  serverName  Description of Parameter
   * @since
   */
  public static void publishSelection( View view, String serverName )
  {
    final Buffer buffer = view.getBuffer();
    final JEditTextArea tArea = view.getTextArea();
    final Selection[] sels = tArea.getSelection();
    int start;
    int end;
    if ( sels.length != 1 )
    {
      start = 0;
      end = buffer.getLength();
    }
    else
    {
      start = sels[0].getStart();
      end = sels[0].getEnd();
    }
    SqlUtils.publishText( view, buffer, start, end - start, serverName );
  }


  /**
   *  Description of the Method
   *
   * @param  view        Description of Parameter
   * @param  serverName  Description of Parameter
   * @since
   */
  public static void publishBuffer( View view, String serverName )
  {
    final Buffer buffer = view.getBuffer();
    SqlUtils.publishText( view, buffer, 0, buffer.getLength(), serverName );
  }


  /**
   *Constructor for the registerJdbcClass object
   *
   * @since
   */
  public static void registerJdbcClassPath()
  {
    final String[] jdbcClassPath = getJdbcClassPath();

    if ( jdbcClassPath != null )
      for ( int i = jdbcClassPath.length; --i >= 0;  )
      {
        final String path = jdbcClassPath[i];
        if ( !( new File( path ).exists() ) )
        {
          Log.log( Log.ERROR, SqlPlugin.class,
              "JDBC classpath component " + path + " does not exist" );
          continue;
        }
        final EditPlugin.JAR jar = jEdit.getPluginJAR( path );
        if ( jar == null )
        {// not registered yet
          try
          {
            jEdit.addPluginJAR( new EditPlugin.JAR( path,
                new JARClassLoader( path ) ) );
          } catch ( IOException ex )
          {
            Log.log( Log.ERROR, SqlPlugin.class,
                "Error loading the jdbc driver from " + path + ": " );
            Log.log( Log.ERROR, SqlPlugin.class, ex );
            continue;
          }
        }
      }

    VFSManager.sendVFSUpdate( sqlVFS, SqlVFS.PROTOCOL + ":/", false );
  }


  /**
   *Description of the Method
   *
   * @since
   */
  public static void unregisterJdbcClassPath()
  {
    SqlServerType.dropAll();

    final String[] jdbcClassPath = getJdbcClassPath();
    if ( jdbcClassPath != null )
      for ( int i = jdbcClassPath.length; --i >= 0;  )
      {
        final String path = jdbcClassPath[i];
        if ( !( new File( path ).exists() ) )
        {
          Log.log( Log.ERROR, SqlPlugin.class,
              "JDBC classpath component " + path + " does not exist" );
          continue;
        }
        final EditPlugin.JAR jar = jEdit.getPluginJAR( path );
        if ( jar == null )
        {
          Log.log( Log.ERROR, SqlPlugin.class,
              "Strange, classpath element " + path + " was not registered" );
        }
        //!! TODO
      }
  }


  /**
   *  Description of the Method
   *
   * @param  view  Description of Parameter
   * @param  objs  Description of Parameter
   * @return       Description of the Returned Value
   * @since
   */
  protected static DbCodeObject chooseCodeObjectInAWTThread( final View view,
      final Object objs[] )
  {
    final java.util.List rv = Collections.synchronizedList( new ArrayList() );

    final Runnable r =
      new Runnable()
      {
        public void run()
        {
          final JComboBox combo = new JComboBox( objs );

          final Object controls[] = new Object[2];
          controls[0] = jEdit.getProperty( "sql.objectchooser.prompt" );
          controls[1] = combo;

          final JOptionPane p = new JOptionPane(
              controls,
              JOptionPane.INFORMATION_MESSAGE,
              JOptionPane.OK_CANCEL_OPTION,
              Icon );

          final JDialog dlg = p.createDialog( view,
              jEdit.getProperty( "sql.objectchooser.title" ) );

          combo.setRenderer( new DbCodeObject.CellRenderer() );

          dlg.show();

          final Object val = p.getValue();

          if ( !new Integer( JOptionPane.OK_OPTION ).equals( val ) )
            return;

          if ( combo.getSelectedIndex() == -1 )
            return;

          final Object obj = combo.getItemAt( combo.getSelectedIndex() );

          rv.add( obj );
        }
      };

    SqlUtils.runInAWTThreadAndWait( r );

    if ( rv.size() == 0 )
      return null;

    return (DbCodeObject) rv.get( 0 );
  }

  static
  {
    Icon = new ImageIcon(
        Toolkit.getDefaultToolkit().getImage(
        SqlPlugin.class.getClassLoader().getResource( "SqlPlugin.gif" ) ) );
  }
}

