package sql.serverTypes.sybase;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.*;

import sql.*;
import sql.serverTypes.sybase.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    20 Март 2002 г.
 */
public class SybaseUtils extends SqlUtils
{
  /**
   *  Description of the Method
   *
   * @param  sql     Description of the Parameter
   * @param  dbName  Description of the Parameter
   * @param  dbMask  Description of the Parameter
   * @return         Description of the Return Value
   */
  public static String substituteDB( String sql, String dbName, String dbMask )
  {
    String rv = "";
    int start = 0;
    int next = sql.indexOf( dbMask );
    while ( next > start )
    {
      rv += sql.substring( start, next ) + dbName;
      start = dbMask.length() + next;
      next = sql.indexOf( dbMask, start );
      if ( start > next )
      {
        rv += sql.substring( start );
      }
    }
    Log.log( Log.DEBUG, SybaseUtils.class, rv );
    return rv;
  }


  /**
   *  Description of the Method
   *
   * @param  conn              Description of the Parameter
   * @param  rec               Description of the Parameter
   * @param  userName          Description of the Parameter
   * @param  objName           Description of the Parameter
   * @param  objType           Description of the Parameter
   * @return                   Description of the Return Value
   * @exception  SQLException  Description of the Exception
   */
  public static String loadObjectText( Connection conn,
      SqlServerRecord rec,
      String userName,
      String objName,
      String objType )
       throws SQLException
  {
    Log.log( Log.DEBUG, SybaseUtils.class,
        "Loading the object " + userName + "/" +
        objName + "/" +
        objType );

    PreparedStatement pstmt = null;

    try
    {
      String stmtText = retrieveStatementText( conn, "selectCodeObjectLines" );
      String mask = retrieveStatementText( conn, "dbNameMask" );
      SqlServerType dbType = SqlServerType.getByName( "Sybase" );
      final SqlServerType.Statement stmt = dbType.getStatement( "selectCodeObjectLines" );
      String processedStmt = substituteDB( stmtText, userName, mask );

      pstmt = conn.prepareStatement( processedStmt );
      stmt.setParams( pstmt, new Object[]{objName} );
      final ResultSet rs = pstmt.executeQuery();

      final StringBuffer sb = new StringBuffer();
      while ( rs.next() )
      {
        sb.append( rs.getString( "text" ) );
      }

      return new String( sb );
    } finally
    {
      rec.releaseStatement( pstmt );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  c         Description of the Parameter
   * @param  stmtName  Description of the Parameter
   * @return           Description of the Return Value
   */
  public static String retrieveStatementText( Connection c, String stmtName )
  {
    SqlServerType dbType = SqlServerType.getByName( "Sybase" );
    final SqlServerType.Statement stmt = dbType.getStatement( stmtName );
    if ( stmt == null )
    {
      return null;
    }
    final String stmtText = stmt.getStatementText( null );
    if ( stmtText == null )
    {
      Log.log( Log.ERROR, SqlServerRecord.class,
          "Strange, null text for non-null statement " + stmtName );
      return null;
    }
    Log.log( Log.DEBUG, SqlServerRecord.class, "Prepared text is " + stmtText );
    return stmtText;
  }


  /**
   *  Description of the Method
   *
   * @param  c                 Description of the Parameter
   * @param  stmtName          Description of the Parameter
   * @param  args              Description of the Parameter
   * @return                   Description of the Return Value
   * @exception  SQLException  Description of the Exception
   */
  public static PreparedStatement prepareSASStatement( Connection c, String stmtName, Object[] args ) throws SQLException
  {

    final String stmtText = retrieveStatementText( c, stmtName );
    final String dbMaskText = retrieveStatementText( c, "dbNameMask" );

    String processedStatement = "";
    if ( stmtText.indexOf( dbMaskText ) == -1 )
    {
      processedStatement = stmtText.trim();
    }
    else
    {
      if ( args != null )
      {
        for ( int i = args.length; --i >= 0;  )
        {
          processedStatement = substituteDB( stmtText, (String) args[i], dbMaskText );
        }
      }
    }

    Log.log( Log.DEBUG, SqlServerRecord.class, "STtext: " + processedStatement );
    PreparedStatement rv = c.prepareStatement( processedStatement );

    return rv;
  }

}

