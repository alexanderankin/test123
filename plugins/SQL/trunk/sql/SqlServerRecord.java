/**
 * SqlServerRecord.java - Sql Plugin
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

import java.sql.*;
import java.util.*;
import java.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import SqlPlugin;

/**
 *Description of the Class
 *
 * @author     svu
 * @created    29 á×ÇÕÓÔ 2001 Ç.
 */
public class SqlServerRecord extends Properties
{

  protected SqlServerType dbType;
  protected MessageFormat tigFmt = null;

  protected String name;

  protected Hashtable callableStmts = null;
  protected Hashtable preparedStmts = null;
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String TYPE = "type";

  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String USER = "user";
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String PASSWORD = "password";

  protected static Hashtable allRecords = null;


  /**
   *  Constructor for the SqlServerRecord object
   *
   * @param  type  Description of Parameter
   * @since
   */
  public SqlServerRecord( SqlServerType type )
  {
    dbType = type;
  }


  /**
   *  Sets the Name attribute of the SqlServerRecord object
   *
   * @param  name  The new Name value
   * @since
   */
  public void setName( String name )
  {
    this.name = name;
  }


  /**
   *  Gets the ServerType attribute of the SqlServerRecord object
   *
   * @return    The ServerType value
   * @since
   */
  public SqlServerType getServerType()
  {
    return dbType;
  }


  /**
   *  Gets the Name attribute of the SqlServerRecord object
   *
   * @return    The Name value
   * @since
   */
  public String getName()
  {
    return name;
  }


  /**
   *  Description of the Method
   *
   * @param  conn  Description of Parameter
   * @since
   */
  public void releaseConnection( Connection conn )
  {
    if ( conn == null )
      return;

    try
    {
      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Connection " + getConnectionString() + " released" );
      conn.close();
    } catch ( SQLException ex )
    {
      Log.log( Log.ERROR, SqlServerRecord.class,
          "Error closing connection" );
      Log.log( Log.ERROR, SqlServerRecord.class,
          ex );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  c                 Description of Parameter
   * @param  name              Description of Parameter
   * @param  args              Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public PreparedStatement prepareStatement( Connection c, String name, Object args[] )
       throws SQLException
  {
    PreparedStatement rv = (PreparedStatement) preparedStmts.get( name );
    if ( rv == null )
    {
      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Creating prepared stmt " + name );
      final SqlServerType.Statement stmt = dbType.getStatement( name );
      if ( stmt == null )
        return null;

      final String stmtText = stmt.getStatementText( args );
      if ( stmtText == null )
      {
        Log.log( Log.ERROR, SqlServerRecord.class,
            "Strange, null text for non-null statement " + name );
        return null;
      }

      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Prepared text is " + stmtText );
      rv = c.prepareStatement( stmtText );
      stmt.setParams( rv, args );

      if ( args == null )
        preparedStmts.put( name, rv );

    }
    else
    {
      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Reusing prepared stmt " + name );
    }

    return rv;
  }


  /**
   *  Description of the Method
   *
   * @param  c                 Description of Parameter
   * @param  name              Description of Parameter
   * @param  args              Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public CallableStatement prepareCall( Connection c, String name, Object args[] )
       throws SQLException
  {
    CallableStatement rv = (CallableStatement) callableStmts.get( name );
    if ( rv == null )
    {
      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Creating callable stmt " + name );
      final SqlServerType.Statement stmt = dbType.getStatement( name );
      if ( stmt == null )
        return null;

      final String stmtText = stmt.getStatementText( args );
      if ( stmtText == null )
      {
        Log.log( Log.ERROR, SqlServerRecord.class,
            "Strange, null text for non-null statement " + name );
        return null;
      }

      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Callable text is " + stmtText );
      rv = c.prepareCall( stmtText );
      stmt.setParams( rv, args );

      if ( args == null )
        callableStmts.put( name, rv );

    }
    else
    {
      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Reusing callable stmt " + name );
    }

    return rv;
  }


  /**
   *  Description of the Method
   *
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public Connection allocConnection()
       throws SQLException
  {
    preparedStmts = new Hashtable();
    callableStmts = new Hashtable();

    final String connString = getConnectionString();
    Log.log( Log.DEBUG, SqlServerRecord.class,
        "Connection " + connString + " allocated" );
    return DriverManager.getConnection(
        connString,
        getProperty( USER ),
        getProperty( PASSWORD ) );
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public void save()
  {
    allRecords = null;

    final Hashtable connParams = dbType.getConnectionParameters();
    for ( Enumeration e = connParams.elements(); e.hasMoreElements();  )
    {
      final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.nextElement();
      SqlPlugin.setProperty( "sql.server." + name + "." + param.getName(),
          getProperty( param.getName() ) );
    }

    SqlPlugin.setProperty( "sql.server." + name + "." + TYPE, dbType.getName() );
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   * @since
   */
  public String toString()
  {
    return name;
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   * @since
   */
  public boolean hasValidProperties()
  {
    final Hashtable connParams = dbType.getConnectionParameters();
    for ( Enumeration e = connParams.elements(); e.hasMoreElements();  )
    {
      final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.nextElement();
      final String value = getProperty( param.getName() );
      if ( value == null )
        return false;
    }
    return true;
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public void delete()
  {
    allRecords = null;

    final Hashtable connParams = dbType.getConnectionParameters();
    for ( Enumeration e = connParams.elements(); e.hasMoreElements();  )
    {
      final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.nextElement();
      SqlPlugin.unsetProperty( "sql.server." + name + "." + param.getName() );
    }

    SqlPlugin.unsetProperty( "sql.server." + name + "." + TYPE );
  }


  /**
   *  Description of the Method
   *
   * @param  stmt              Description of Parameter
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void releaseStatement( PreparedStatement stmt )
       throws SQLException
  {
    Log.log( Log.DEBUG, SqlServerRecord.class,
        "Statement " + stmt + " released" );

    if ( stmt == null )
      return;

    if ( preparedStmts.contains( stmt ) ||
        callableStmts.contains( stmt ) )
      return;

    stmt.close();
  }


  /**
   *  Gets the ConnectionString attribute of the SqlServerRecord object
   *
   * @return    The ConnectionString value
   * @since
   */
  protected String getConnectionString()
  {
    StringBuffer stringPattern = new StringBuffer( dbType.getProperty( "connection.string" ) );

    final Hashtable connParams = dbType.getConnectionParameters();
    for ( Enumeration e = connParams.elements(); e.hasMoreElements();  )
    {
      final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.nextElement();
      final String searchPattern = "{" + param.getName() + "}";
      final String value = getProperty( param.getName() );

      int idx = new String( stringPattern ).indexOf( searchPattern );

      while ( idx != -1 )
      {
        stringPattern = stringPattern.replace( idx, idx + searchPattern.length(), value );
        idx = new String( stringPattern ).indexOf( searchPattern );
      }
    }

    return new String( stringPattern );
  }


  /**
   *  Gets the ServerType attribute of the SqlServerRecord class
   *
   * @param  name  Description of Parameter
   * @return       The ServerType value
   * @since
   */
  public static SqlServerType getServerType( String name )
  {
    final String dbTypeName = SqlPlugin.getProperty( "sql.server." + name + "."
         + TYPE );
    if ( dbTypeName == null )
      return null;
    return SqlServerType.getByName( dbTypeName );
  }


  /**
   *  Gets the AllRecords attribute of the SqlServerRecord class
   *
   * @return    The AllRecords value
   * @since
   */
  public static Hashtable getAllRecords()
  {
    if ( null == allRecords )
    {
      allRecords = new Hashtable();

      Log.log( Log.DEBUG, SqlServerRecord.class,
          "Loading all records" );
      final Hashtable servers = new Hashtable();

      for ( Enumeration e = SqlPlugin.getPropertyNames(); e.hasMoreElements();  )
      {
        final String pname = (String) e.nextElement();

        if ( !( pname.startsWith( "sql.server." ) &&
            pname.endsWith( "." + TYPE ) ) )
          continue;
        final String name = pname.substring( 11, pname.length() - 5 );
        Log.log( Log.DEBUG, SqlServerRecord.class,
            "Found name " + name + " for loading" );
        final SqlServerRecord sr = load( name );
        if ( sr != null )
          allRecords.put( sr.getName(), sr );
      }
    }
    return allRecords;
  }


  /**
   *  Description of the Method
   *
   * @param  name  Description of Parameter
   * @return       Description of the Returned Value
   * @since
   */
  public static SqlServerRecord get( String name )
  {
    final Hashtable recs = getAllRecords();
    if ( recs == null )
      return null;
    return (SqlServerRecord) recs.get( name );
  }


  /**
   *Description of the Method
   *
   * @since
   */
  public static void clearProperties()
  {
    final Vector v = new Vector();
    for ( Enumeration e = SqlPlugin.getPropertyNames(); e.hasMoreElements();  )
    {
      final String pname = (String) e.nextElement();

      if ( pname.startsWith( "sql.server." ) )
        v.addElement( pname );
    }

    for ( Enumeration e = v.elements(); e.hasMoreElements();  )
    {
      SqlPlugin.unsetProperty( (String) e.nextElement() );
    }

    SqlPlugin.unsetProperty( "sql.currentServerName" );
  }


  /**
   *  Description of the Method
   *
   * @param  name  Description of Parameter
   * @return       Description of the Returned Value
   * @since
   */
  protected static SqlServerRecord load( String name )
  {
    Log.log( Log.DEBUG, SqlServerRecord.class,
        "Loading server record " + name );
    final SqlServerType dbType = getServerType( name );
    if ( dbType == null )
    {
      Log.log( Log.ERROR, SqlServerRecord.class,
          "Could not determine the server type for record " + name );
      return null;
    }

    final SqlServerRecord rv = new SqlServerRecord( dbType );

    final Hashtable connParams = dbType.getConnectionParameters();
    for ( Enumeration e = connParams.elements(); e.hasMoreElements();  )
    {
      final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.nextElement();
      final String value =
          SqlPlugin.getProperty( "sql.server." + name + "." + param.getName() );

      rv.setProperty( param.getName(), value );
    }

    rv.setName( name );

    Log.log( Log.DEBUG, SqlServerRecord.class,
        "Loaded " + rv.getName() + "/" + rv.dbType.getName() );
    Log.log( Log.DEBUG, SqlServerRecord.class,
        "Connection: " + rv.getConnectionString() );

    return rv;
  }
}

