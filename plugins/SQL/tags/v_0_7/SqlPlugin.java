/**
 * SqlPlugin.java - Sql Plugin
 * Copyright (C) 26 á×ÇÕÓÔ 2001 Ç. Sergey V. Udaltsov
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

import sql.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 á×ÇÕÓÔ 2001 Ç.
 */
public class SqlPlugin extends EBPlugin
{
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

  protected static Properties props = null;
  protected static boolean configModified = false;


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

    SqlServerType.loadAll();

    final Hashtable types = SqlServerType.getAllTypes();

    for ( Enumeration e = types.elements(); e.hasMoreElements();  )
    {
      final SqlServerType t = (SqlServerType) e.nextElement();
      t.register();
    }

    EditBus.addToNamedList( DockableWindow.DOCKABLE_WINDOW_LIST, resultSetWinName );

    SqlUtils.init();

    VFSManager.registerVFS( SqlVFS.PROTOCOL, new SqlVFS() );
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
   * @since
   */
  public void createOptionPanes( OptionsDialog optionsDialog )
  {
    optionsDialog.addOptionPane( new SqlOptionPane() );
  }


  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   * @since
   */
  public void handleMessage( EBMessage message )
  {
    if ( message instanceof CreateDockableWindow )
      handleCreateDockableMessage( (CreateDockableWindow) message );
  }


  /**
   *  Description of the Method
   *
   * @param  wnd  Description of Parameter
   * @since
   */
  protected void handleCreateDockableMessage( CreateDockableWindow wnd )
  {
    if ( wnd.getDockableWindowName().equals( resultSetWinName ) )
      wnd.setDockableWindow( new ResultSetWindow( wnd.getView() ) );
  }


  /**
   *  Sets the Property attribute of the SqlPlugin class
   *
   * @param  name   The new Property value
   * @param  value  The new Property value
   * @since
   */
  public static void setProperty( String name, String value )
  {
    props.setProperty( name, value );
    configModified = true;
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
   *  Gets the Property attribute of the SqlPlugin class
   *
   * @param  name  Description of Parameter
   * @return       The Property value
   * @since
   */
  public static String getProperty( String name )
  {
    if ( props == null )
      loadProperties();

    return props.getProperty( name );
  }


  /**
   *  Gets the PropertyNames attribute of the SqlPlugin class
   *
   * @return    The PropertyNames value
   * @since
   */
  public static Enumeration getPropertyNames()
  {
    if ( props == null )
      loadProperties();

    return props.propertyNames();
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public static void loadProperties()
  {
    final String path = MiscUtilities.constructPath( jEdit.getSettingsDirectory(),
        "sql", "properties" );
    try
    {
      props = new Properties();
      final InputStream is = new BufferedInputStream( new FileInputStream( path ) );
      props.load( is );
      is.close();
      configModified = false;
    } catch ( IOException ex )
    {
      Log.log( Log.ERROR, SqlPlugin.class,
          "Error loading SqlPlugin properties" + ex );
    }
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public static void commitProperties()
  {
    if ( !configModified )
      return;

    final String path = MiscUtilities.constructPath( jEdit.getSettingsDirectory(),
        "sql", "properties" );
    try
    {
      final OutputStream os = new BufferedOutputStream( new FileOutputStream( path ) );
      props.store( os, "Sql Plugin properties" );
      os.close();
      FileVFS.setPermissions( path, 0600 );
      configModified = false;
    } catch ( IOException ex )
    {
      Log.log( Log.ERROR, SqlPlugin.class,
          "Error saving SqlPlugin properties:" );
      Log.log( Log.ERROR, SqlPlugin.class, ex );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  name  Description of Parameter
   * @since
   */
  public static void unsetProperty( String name )
  {
    props.remove( name );
    configModified = true;
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

    return (ResultSetWindow) dockableWindowManager.getDockableWindow( resultSetWinName );
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
          final SqlServerRecord rec = SqlUtils.getServerRecord( view, serverName );
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

                  try
                  {
                    buf.insertString( 0, text, null );
                  } catch ( javax.swing.text.BadLocationException ex )
                  {
                    System.err.println( ex );
                  }
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
   * @param  view  Description of Parameter
   * @since
   */
  public static void loadObjectFromServer( View view )
  {
    final String name = SqlUtils.getServerForPublishing( view );
    if ( name != null )
      loadObject( view, name );
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
   * @param  view  Description of Parameter
   * @since
   */
  public static void publishSelectionToServer( View view )
  {
    final String name = SqlUtils.getServerForPublishing( view );
    if ( name != null )
      publishSelection( view, name );
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
   *  Description of the Method
   *
   * @param  view  Description of Parameter
   * @since
   */
  public static void publishBufferToServer( View view )
  {
    final String name = SqlUtils.getServerForPublishing( view );
    if ( name != null )
      publishBuffer( view, name );
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
    final Vector rv = new Vector();
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

          rv.addElement( obj );
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

