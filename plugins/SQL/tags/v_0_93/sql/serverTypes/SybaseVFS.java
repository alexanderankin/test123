/**
 * SybaseVFS.java - Sql Plugin
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
package sql.serverTypes;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.*;

import sql.*;
import sql.serverTypes.complex.*;
import sql.serverTypes.sybase.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    20 Март 2002 г.
 */
public class SybaseVFS extends ComplexVFS
{
  protected final static Map sybaseObjectTypes = new HashMap();


  /**
   *Constructor for the SybaseVFS object
   */
  public SybaseVFS()
  {
    super( sybaseObjectTypes );
  }


  /**
   *  Gets the LevelDelimiter attribute of the SybaseVFS object
   *
   * @return    The LevelDelimiter value
   */
  public String getLevelDelimiter()
  {
    return "..";
  }


  /**
   *  Gets the EntriesFromDb attribute of the SybaseVFS object
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
    final java.util.List tableGroups = getVFSObjects( rec,
        stmtPurpose,
        stmtParams );

    if ( tableGroups == null )
      return null;

    final VFS.DirectoryEntry[] retval = new VFS.DirectoryEntry[tableGroups.size()];
    int i = 0;
    for ( Iterator e = tableGroups.iterator(); e.hasNext();  )
    {
      final String r = (String) e.next();
      retval[i++] =
          _getDirectoryEntry( session, path + SqlVFS.separatorString + r, comp, level + 1 );
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
  protected java.util.List getVFSObjects( SqlServerRecord rec, String stmtName, Object args[] )
  {

    Log.log( Log.DEBUG, SqlServerRecord.class,
        "Looking for vfs objects in:" );
    if ( args != null )
      for ( int i = args.length; --i >= 0;  )
        Log.log( Log.DEBUG, SqlServerRecord.class,
            ">" + args[i] );

    final java.util.List rv = new ArrayList();
    Connection conn = null;
    try
    {
      conn = rec.allocConnection();
      final PreparedStatement pstmt = SybaseUtils.prepareSASStatement( conn, stmtName, args );
      final ResultSet rs = pstmt.executeQuery();
      while ( rs.next() )
      {
        final String tgname = rs.getString( 1 );
        rv.add( tgname );
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

  static
  {
    sybaseObjectTypes.put( "Procedures",
        new sql.serverTypes.sybase.CodeObjectType( "PROCEDURE" ) );
    sybaseObjectTypes.put( "Tables",
        new TableObjectType( "selectTablesInSchema" ) );
  }

}

