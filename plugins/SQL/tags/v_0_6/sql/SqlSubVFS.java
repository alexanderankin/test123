/**
 * SqlSubVFS.java - Sql Plugin
 * Copyright (C) 2001 Sergey V. Udaltsov
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
import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.*;

import SqlPlugin;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 á×ÇÕÓÔ 2001 Ç.
 */
public class SqlSubVFS
{

  protected final static int TABLEGROUP_LEVEL = 2;
  protected final static int TABLE_LEVEL = 3;


  /**
   *  Description of the Method
   *
   * @param  session          Description of Parameter
   * @param  path             Description of Parameter
   * @param  comp             Description of Parameter
   * @param  rec              Description of Parameter
   * @param  level            Description of Parameter
   * @return                  Description of the Returned Value
   * @exception  IOException  Description of Exception
   * @since
   */
  public VFS.DirectoryEntry[] _listDirectory( Object session,
      String path,
      Component comp,
      SqlServerRecord rec,
      int level )
       throws IOException
  {
    Log.log( Log.DEBUG, SqlSubVFS.class,
        "Listing " + path );
    VFS.DirectoryEntry[] retval = null;

    int i;

    switch ( level )
    {
      case SqlVFS.DB_LEVEL:

        retval = getEntriesFromDb( session,
            path,
            comp,
            rec,
            level,
            "selectTableGroups",
            null );

        break;
      case TABLEGROUP_LEVEL:

        final String tgName = SqlVFS.getPathComponent( path, TABLEGROUP_LEVEL );

        retval = getEntriesFromDb( session,
            path,
            comp,
            rec,
            level,
            "selectTablesInGroup",
            new Object[]{tgName} );

        break;
    }
    Log.log( Log.DEBUG, SqlSubVFS.class,
        "Listed total " + ( retval == null ? -1 : retval.length ) + " items" );
    return retval;
  }


  /**
   *  Description of the Method
   *
   * @param  session          Description of Parameter
   * @param  path             Description of Parameter
   * @param  comp             Description of Parameter
   * @param  level            Description of Parameter
   * @return                  Description of the Returned Value
   * @exception  IOException  Description of Exception
   * @since
   */
  public VFS.DirectoryEntry _getDirectoryEntry( Object session, String path,
      Component comp, int level )
       throws IOException
  {
    return new VFS.DirectoryEntry( path, path, path,
        level == TABLE_LEVEL ?
        VFS.DirectoryEntry.FILE :
        VFS.DirectoryEntry.DIRECTORY,
        0L,
        false );
  }


  /**
   *  Description of the Method
   *
   * @param  vfs     Description of Parameter
   * @param  view    Description of Parameter
   * @param  buffer  Description of Parameter
   * @param  path    Description of Parameter
   * @param  level   Description of Parameter
   * @return         Description of the Returned Value
   * @since
   */
  public boolean afterLoad( final VFS vfs, final View view, final Buffer buffer, final String path, int level )
  {
    buffer.putBooleanProperty( SqlVFS.RUN_ON_LOAD_PROPERTY, true );

    return true;
  }


  /**
   *  Description of the Method
   *
   * @param  vfs              Description of Parameter
   * @param  session          Description of Parameter
   * @param  path             Description of Parameter
   * @param  ignoreErrors     Description of Parameter
   * @param  comp             Description of Parameter
   * @param  level            Description of Parameter
   * @return                  Description of the Returned Value
   * @exception  IOException  Description of Exception
   * @since
   */
  public InputStream _createInputStream( VFS vfs, Object session, String path,
      boolean ignoreErrors, Component comp, int level ) throws IOException
  {
    return new StringBufferInputStream( "SELECT * FROM " +
        vfs.getFileName( SqlVFS.normalize( vfs.getParentOfPath( path ) ) ) + "." +
        vfs.getFileName( path ) );
  }


  /**
   *  Gets the EntriesFromDb attribute of the SqlSubVFS object
   *
   * @param  session          Description of Parameter
   * @param  path             Description of Parameter
   * @param  comp             Description of Parameter
   * @param  rec              Description of Parameter
   * @param  level            Description of Parameter
   * @param  stmtPurpose      Description of Parameter
   * @param  stmtParams       Description of Parameter
   * @return                  The EntriesFromDb value
   * @exception  IOException  Description of Exception
   * @since
   */
  protected VFS.DirectoryEntry[] getEntriesFromDb( Object session,
      String path,
      Component comp,
      SqlServerRecord rec,
      int level,
      String stmtPurpose,
      Object[] stmtParams )
       throws IOException
  {
    final Vector tableGroups = getVFSObjects( rec,
        stmtPurpose,
        stmtParams );

    if ( tableGroups == null )
      return null;

    final VFS.DirectoryEntry[] retval = new VFS.DirectoryEntry[tableGroups.size()];
    int i = 0;
    for ( Enumeration e = tableGroups.elements(); e.hasMoreElements();  )
    {
      final String r = (String) e.nextElement();
      retval[i++] =
          _getDirectoryEntry( session, path + SqlVFS.separatorChar + r, comp, level + 1 );
    }
    return retval;
  }


  /**
   *  Gets the VFSObjects attribute of the SqlSubVFS object
   *
   * @param  rec       Description of Parameter
   * @param  stmtName  Description of Parameter
   * @param  args      Description of Parameter
   * @return           The VFSObjects value
   * @since
   */
  protected Vector getVFSObjects( SqlServerRecord rec, String stmtName, Object args[] )
  {
    Log.log( Log.DEBUG, SqlServerRecord.class,
        "Looking for vfs objects in:" );
    if ( args != null )
      for ( int i = args.length; --i >= 0;  )
        Log.log( Log.DEBUG, SqlServerRecord.class,
            ">" + args[i] );

    final Vector rv = new Vector();
    Connection conn = null;
    try
    {
      conn = rec.allocConnection();
      final PreparedStatement pstmt = rec.prepareStatement( conn,
          stmtName,
          args );
      final ResultSet rs = pstmt.executeQuery();
      while ( rs.next() )
      {
        final String tgname = rs.getString( 1 );
        rv.addElement( tgname );
      }
      rec.releaseStatement( pstmt );
    } catch ( SQLException ex )
    {
      Log.log( Log.ERROR, SqlServerRecord.class,
          "Error getting vfs objects in " + args );
      Log.log( Log.ERROR, SqlServerRecord.class,
          ex );
    } finally
    {
      rec.releaseConnection( conn );
    }
    return rv;
  }
}

