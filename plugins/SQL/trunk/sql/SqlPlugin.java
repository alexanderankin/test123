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

import projectviewer.vpt.*;
import projectviewer.config.*;

import sql.*;
import sql.options.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ������ 2001 �.
 */
public class SqlPlugin extends EBPlugin implements ProjectOptionsPlugin
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

  /**
   * Global props
   */
  protected static Properties globalProps = null;

  /**
   * Local (per-session, per-project properties
   */
  protected static Properties localProps = null;

  protected static boolean globalConfigModified = false;

  protected static boolean localConfigModified = false;

  protected static String currentSession;

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
    final java.util.List l = SqlUtils.getPreprocessors();

    for ( Iterator i = l.iterator(); i.hasNext();  )
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
      if ( vu.getWhat() == ViewUpdate.CREATED )
      {
        if ( SqlToolBar.showToolBar() )
          addToolBar( vu.getView() );
      }
      else if ( vu.getWhat() == ViewUpdate.CLOSED )
      {
        sqlToolBars.remove( vu.getView() );
      }
    }
    else if ( message instanceof PropertiesChanged )
    {
      Log.log( Log.DEBUG, SqlPlugin.class, "properties changed!" );
      handlePropertiesChanged();
    }
  }


  private void handlePropertiesChanged()
  {
    final boolean show = SqlToolBar.showToolBar();
    View view = jEdit.getFirstView();

    while ( view != null )
    {
      if ( show )
        addToolBar( view );
      else
        removeToolBar( view );
      view = view.getNext();
    }
  }


  private synchronized void addToolBar( final View view )
  {
    // remove old
    removeToolBar( view );

    // create new
    final SqlToolBar toolbar = new SqlToolBar( view );
    sqlToolBars.put( view, toolbar );
    view.addToolBar( toolbar );
  }


  private synchronized void removeToolBar( View view )
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
    globalProps.setProperty( name, value );
    globalConfigModified = true;
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
    localProps.setProperty( name, value );
    localConfigModified = true;
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
    if ( localProps == null )
      loadLocalProperties( project );

    return localProps.getProperty( name );
  }


  /**
   *  Gets the GlobalProperty attribute of the SqlPlugin class
   *
   * @param  name  Description of Parameter
   * @return       The GlobalProperty value
   */
  public static String getGlobalProperty( String name )
  {
    if ( globalProps == null )
      loadGlobalProperties();

    return globalProps.getProperty( name );
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
   *Gets the ConfigFileName attribute of the SqlPlugin class
   *
   * @param  sessionName  Description of Parameter
   * @return              The ConfigFileName value
   * @since
   */
  public static String getConfigFileName( String sessionName )
  {
    return MiscUtilities.constructPath( jEdit.getSettingsDirectory(),
        "sql",
        ( sessionName == null || "default".equals( sessionName ) ) ?
        "properties" :
        "properties." + sessionName );
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
   * @since
   */
  public static void loadGlobalProperties()
  {
    //TODO!
    globalProps = new Properties();
    globalConfigModified = false;
  }


  /**
   *  Description of the Method
   *
   * @param  project  Description of Parameter
   */
  public static void loadLocalProperties( VPTProject project )
  {
    /*
     *  String path = getConfigFileName( getCurrentSession() );
     *  if ( !( new File( path ).exists() ) )
     *  path = getConfigFileName( null );
     *  try
     *  {
     *  localProps = new Properties();
     *  final InputStream is = new BufferedInputStream( new FileInputStream( path ) );
     *  localProps.load( is );
     *  is.close();
     *  localConfigModified = false;
     *  } catch ( IOException ex )
     *  {
     *  Log.log( Log.ERROR, SqlPlugin.class,
     *  "Error loading SqlPlugin properties" + ex );
     *  }
     */
  }


  /**
   *  Description of the Method
   */
  public static void commitGlobalProperties()
  {
    if ( !globalConfigModified )
      return;
  }


  /**
   *  Description of the Method
   *
   * @param  project  Description of Parameter
   * @since
   */
  public static void commitLocalProperties( VPTProject project )
  {
    /*
     *  if ( !localConfigModified )
     *  return;
     *  final String path = getConfigFileName( getCurrentSession() );
     *  try
     *  {
     *  final OutputStream os = new BufferedOutputStream( new FileOutputStream( path ) );
     *  localProps.store( os, "Sql Plugin properties" );
     *  os.close();
     *  FileVFS.setPermissions( path, 0600 );
     *  localConfigModified = false;
     *  } catch ( IOException ex )
     *  {
     *  Log.log( Log.ERROR, SqlPlugin.class,
     *  "Error saving SqlPlugin properties:" );
     *  Log.log( Log.ERROR, SqlPlugin.class, ex );
     *  }
     */
  }


  /**
   *  Description of the Method
   *
   * @param  name  Description of Parameter
   */
  public static void unsetGlobalProperty( String name )
  {
    globalProps.remove( name );
    globalConfigModified = true;
  }


  /**
   *  Description of the Method
   *
   * @param  name     Description of Parameter
   * @param  project  Description of Parameter
   */
  public static void unsetLocalProperty( VPTProject project, String name )
  {
    localProps.remove( name );
    localConfigModified = true;
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
   *Gets the CurrentSession attribute of the SqlPlugin class
   *
   * @param  view  Description of Parameter
   * @param  objs  Description of Parameter
   * @return       The CurrentSession value
   * @since
   */
  /*
   *  protected static String getCurrentSession()
   *  {
   *  if ( currentSession == null )
   *  currentSession = SessionManager.getInstance().getCurrentSession();
   *  return currentSession;
   *  }
   */
  /*
   *  **
   *  Description of the Method
   *
   *  @param  message  Description of Parameter
   *  @since
   *
   *  protected static void handleSessionChange( SessionChanging message )
   *  {
   *  Log.log( Log.DEBUG, SqlPlugin.class,
   *  "Changing the session from " +
   *  message.getOldSession() + " to " + message.getNewSession() );
   *  commitLocalProperties();
   *  clearLocalProperties();
   *  localProps = null;
   *  currentSession = message.getNewSession();
   *  }
   */
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

