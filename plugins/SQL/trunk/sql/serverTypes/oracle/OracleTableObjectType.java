/**
 * OracleTableObjectType.java - Sql Plugin
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
package sql.serverTypes.oracle;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import sql.*;
import sql.serverTypes.OracleVFS;

public class OracleTableObjectType extends TableObjectType
{
  public OracleTableObjectType()
  {
    super( "selectTablesInGroup" );
    
    objectActions.put( "Extract to DDL", new ExtractToDMLAction() );
  }

  public static class ExtractToDMLAction extends SqlSubVFS.ObjectAction
  {
    public ExtractToDMLAction()
    { 
      super( false );
    }

    /* TODO: parametrize */
    protected MessageFormat insertStmtFormat = new MessageFormat( "INSERT INTO {0}\n  ({1})\n  VALUES ({2});\n\n" );

    protected String formatDate(java.util.Date date, SqlServerRecord rec, String fmtName)
    {
      final SqlServerType sst = rec.getServerType();
      final SimpleDateFormat sdf = (SimpleDateFormat)sst.getFormat( fmtName );
      return date == null ? "null" : "'" + sdf.format( date ) + "'";
    }
    
    public String getText( String path,
        SqlServerRecord rec,
        String userName,
        String objName )
    {
      Connection conn = null;
      try
      {
        conn = rec.allocConnection();

        PreparedStatement pstmt = null;
        try
        {
          final String fullyQualifiedTableName =
                  userName +
                  ( rec.getServerType().getSubVFS() ).getLevelDelimiter() +
                  objName;
          final String stmt = "SELECT * FROM " + fullyQualifiedTableName;
          pstmt = conn.prepareStatement( stmt );
          if ( pstmt == null )
            return null;

          final ResultSet rs = SqlUtils.executeQuery( pstmt );
          final ResultSetMetaData rsmd = rs.getMetaData();
          
          String fieldList = "";
          for (int i = rsmd.getColumnCount(), col = 1; --i >= 0; col++ )
          {
            fieldList += rsmd.getColumnName(col);
            if (i != 0) fieldList += ", ";
          }

          String rv = "";
          
          int limit = ResultSetWindow.getMaxRecordsToShow();
          while ( rs.next() )
          {
            if (limit-- == 0)
              break;

            String valList = "";
            for (int i = rsmd.getColumnCount(), col = 1; --i >= 0; col++ )
            {
              String stringifiedValue = rs.getString(col);
              java.util.Date date;
              switch (rsmd.getColumnType(col))
              {
                case Types.DATE:
                  stringifiedValue = formatDate( rs.getDate(col), rec, "date" );
                  break;
                case Types.TIME:
                  stringifiedValue = formatDate( rs.getTime(col), rec, "time" );
                  break;
                case Types.TIMESTAMP:
                  stringifiedValue = formatDate( rs.getTimestamp(col), rec, "timestamp" );
                  break;

                case Types.CHAR:
                case Types.LONGVARCHAR:
                case Types.VARCHAR:
                  stringifiedValue = stringifiedValue == null ? "null" : "'" + stringifiedValue + "'";
                  break;
                default:
                  stringifiedValue = stringifiedValue == null ? "null" : stringifiedValue;
              }
              valList += stringifiedValue;
              if (i != 0) valList += ", ";
            }
            rv += insertStmtFormat.format(new Object[] { fullyQualifiedTableName, fieldList, valList });
          }
          return rv;
        } finally
        {
          rec.releaseStatement( pstmt );
        }
      } catch ( SQLException ex )
      {
        Log.log( Log.ERROR, OracleTableObjectType.class,
            "Error extracting table data" );
        Log.log( Log.ERROR, OracleTableObjectType.class,
            ex );
      } finally
      {
        rec.releaseConnection( conn );
      }

      return null;
    }
  }
}

