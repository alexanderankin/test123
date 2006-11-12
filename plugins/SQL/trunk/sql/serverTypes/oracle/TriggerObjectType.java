/**
 * TriggerObjectType.java - Sql Plugin
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
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import sql.*;
import sql.serverTypes.OracleVFS;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    27 ������ 2001 �.
 */
public class TriggerObjectType implements OracleVFS.ObjectType
{

  /**
   *  Constructor for the CodeObjectType object
   *
   * @since
   */
  public TriggerObjectType() { }


  /**
   *  Gets the Text attribute of the CodeObjectType object
   *
   * @param  path      Description of Parameter
   * @param  rec       Description of Parameter
   * @param  userName  Description of Parameter
   * @param  objName   Description of Parameter
   * @return           The Text value
   * @since
   */
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
        pstmt = rec.prepareStatement(
            conn,
            "selectTriggerCode",
            new Object[]{userName, objName} );
        if ( pstmt == null )
          return null;

        final ResultSet rs = SqlUtils.executeQuery( pstmt );

        if ( rs.next() )
        {
          final String baseObjType = rs.getString( "baseObjectType" ).toLowerCase().trim();
          if ( !"table".equals( baseObjType ) )
          {
            GUIUtilities.message( jEdit.getLastView(),
                "sql.oracle.unsupportedTypeOfTrigger",
                new Object[]{userName, objName, baseObjType} );
            return null;
          }

          final String triggerType = rs.getString( "triggerType" );
          String predicate = null;
          int afterPredicate = 0;

          if ( triggerType.startsWith( "AFTER" ) )
          {
            predicate = "AFTER";
            afterPredicate = 6;
          }
          else if ( triggerType.startsWith( "BEFORE" ) )
          {
            predicate = "BEFORE";
            afterPredicate = 7;
          }
          else if ( triggerType.startsWith( "INSTEAD OF" ) )
          {
            predicate = "INSTEAD OF";
            afterPredicate = 11;
          }
          else
            return null;

          String scope = null;
          if ( afterPredicate < triggerType.length() )
            scope = triggerType.substring( afterPredicate );

          String sb =
              "CREATE OR REPLACE TRIGGER " + userName + "." + objName +
              "\n" + predicate +
              " " + rs.getString( "event" ) +
              " ON " + rs.getString( "tableOwner" ) + "." + rs.getString( "tableName" ) +
              "\n" + rs.getString( "referencingClause" ) +
              ( scope == null ? "" : ( " FOR " + scope ) ) +
              "\n" + rs.getString( "sourceCode" );
          return sb;
        }
      } finally
      {
        rec.releaseStatement( pstmt );
      }
    } catch ( SQLException ex )
    {
      Log.log( Log.ERROR, TriggerObjectType.class,
          "Error loading object code" );
      Log.log( Log.ERROR, TriggerObjectType.class,
          ex );
    } finally
    {
      rec.releaseConnection( conn );
    }

    return null;
  }


  /**
   *  Gets the StatementPurpose attribute of the CodeObjectType object
   *
   * @return    The StatementPurpose value
   * @since
   */
  public String getStatementPurpose()
  {
    return "selectCodeObjectsInGroup";
  }


  /**
   *  Gets the Parameter attribute of the CodeObjectType object
   *
   * @return    The Parameter value
   * @since
   */
  public Object getParameter()
  {
    return "TRIGGER";
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   * @since
   */
  public boolean showResultSetAfterLoad()
  {
    return false;
  }
}

