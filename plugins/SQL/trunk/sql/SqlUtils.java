/**
 * SqlUtils.java - Sql Plugin
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
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.*;

import errorlist.*;

import sql.*;
import sql.preprocessors.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ?????? 2001 ?.
 */
public class SqlUtils
{
  protected static DefaultErrorSource errorSource = null;

  protected final static DateFormat dFormat =
      new SimpleDateFormat( "yyyy-MM-dd:HH:mm:ss", Locale.US );

  protected static SqlThreadGroup sqlThreadGroup;

  protected static java.util.List preprocessors = null;

  protected static String lastRunQuery = null;
  protected static int lastStartPos = 0;


  /**
   *  Sets the SelectedServerName attribute of the SqlUtils class
   *
   * @param  name  The new SelectedServerName value
   * @since
   */
  public final static void setSelectedServerName( final String name )
  {
    if ( name != null )
      SqlPlugin.setLocalProperty( "sql.currentServerName", name );
    else
      SqlPlugin.unsetLocalProperty( "sql.currentServerName" );

    SqlPlugin.commitLocalProperties();

      new Thread()
      {
        public void run()
        {
          EditBus.send( new SqlServerChanged(
              null, name ) );
        }
      }.start();
  }


  /**
   *  Gets the SelectedServerName attribute of the SqlUtils class
   *
   * @return    The SelectedServerName value
   * @since
   */
  public final static String getSelectedServerName()
  {
    return SqlPlugin.getLocalProperty( "sql.currentServerName" );
  }


  /**
   *  Gets the ServerRecord attribute of the SqlUtils class
   *
   * @param  view        Description of Parameter
   * @param  serverName  Description of Parameter
   * @return             The ServerRecord value
   * @since
   */
  public static SqlServerRecord getServerRecord( final View view, String serverName )
  {
    if ( serverName != null )
    {
      final SqlServerRecord rec = SqlServerRecord.get( serverName );
      if ( rec != null && rec.hasValidProperties() )
        return rec;
    }

    runInAWTThreadNoWait(
      new Runnable()
      {
        public void run()
        {
          GUIUtilities.error( view, "sql.noSettings", null );
        }
      } );
    return null;
  }


  /**
   *  Gets the ThreadGroup attribute of the SqlUtils class
   *
   * @return    The ThreadGroup value
   * @since
   */
  public static SqlThreadGroup getThreadGroup()
  {
    return sqlThreadGroup;
  }


  /**
   *Gets the Preprocessors attribute of the SqlUtils class
   *
   * @return    The Preprocessors value
   * @since
   */
  public static java.util.List getPreprocessors()
  {
    if ( preprocessors == null )
      fillPreprocessors();
    return preprocessors;
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public static void init()
  {
    if ( errorSource == null )
    {
      errorSource = new DefaultErrorSource( SqlPlugin.NAME );
      ErrorSource.registerErrorSource( errorSource );
    }

    sqlThreadGroup = new SqlThreadGroup( "SQL Queries" );
  }


  /**
   *  Description of the Method
   *
   * @param  conn              Description of Parameter
   * @param  userName          Description of Parameter
   * @param  objName           Description of Parameter
   * @param  objType           Description of Parameter
   * @param  rec               Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public static String loadObjectText( Connection conn,
      SqlServerRecord rec,
      String userName,
      String objName,
      String objType )
       throws SQLException
  {
    Log.log( Log.DEBUG, SqlUtils.class,
        "Loading the object " + userName + "/" +
        objName + "/" +
        objType );

    PreparedStatement pstmt = null;
    try
    {
      pstmt = rec.prepareStatement(
          conn,
          "selectCodeObjectLines",
          new Object[]{userName, objName, objType} );
      if ( pstmt == null )
        return null;

      final ResultSet rs = pstmt.executeQuery();

      final StringBuffer sb = new StringBuffer( rec.getServerType().getObjectCreationPrefix() );
      while ( rs.next() )
        sb.append( rs.getString( "sourceCodeLine" ) );

      return new String( sb );
    } finally
    {
      rec.releaseStatement( pstmt );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  view              Description of Parameter
   * @param  conn              Description of Parameter
   * @param  rec               Description of Parameter
   * @param  userName          Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public static Object[] loadObjectList( final View view,
      Connection conn,
      final SqlServerRecord rec,
      String userName )
       throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      pstmt = rec.prepareStatement( conn,
          "selectUserObjects",
          new Object[]{userName} );
      if ( pstmt == null )
      {
        runInAWTThreadNoWait(
          new Runnable()
          {
            public void run()
            {
              GUIUtilities.message( view,
                  "sql.noStoredProcedures",
                  new Object[]{rec.getServerType().getName()} );
            }
          } );
        return null;
      }
      final ResultSet rs = pstmt.executeQuery();
      final java.util.List rv = new ArrayList();

      while ( rs.next() )
      {
        final DbCodeObject obj = new DbCodeObject( rs.getString( "name" ),
            rs.getString( "type" ),
            rs.getString( "valid" ) );
        rv.add( obj );
      }
      return rv.toArray();
    } finally
    {
      rec.releaseStatement( pstmt );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  view        Description of Parameter
   * @param  buffer      Description of Parameter
   * @param  startPos    Description of Parameter
   * @param  length      Description of Parameter
   * @param  serverName  Description of Parameter
   * @since
   */
  public static void publishText( final View view,
      final Buffer buffer,
      final int startPos,
      final int length,
      final String serverName )
  {
    sqlThreadGroup.runInGroup(
      new Runnable()
      {
        public void run()
        {
          doPublishText( view,
              startPos,
              buffer.getText( startPos, length ),
              serverName );
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
  public static void repeatLastQuery( View view,
      String serverName )
  {
    if ( lastRunQuery != null )
    {
      doPublishText( view,
          lastStartPos,
          lastRunQuery,
          serverName );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  view        Description of Parameter
   * @param  startPos    Description of Parameter
   * @param  sqlText     Description of Parameter
   * @param  serverName  Description of Parameter
   * @since
   */
  public static void doPublishText( final View view,
      int startPos,
      String sqlText,
      String serverName )
  {
    errorSource.clear();

    final SqlServerRecord rec = getServerRecord( view, serverName );
    if ( rec == null )
      return;

    Connection conn = null;
    // update vars for re-querying
    lastRunQuery = sqlText;
    lastStartPos = startPos;

    try
    {
      conn = rec.allocConnection();

      final Timestamp startTime = getSysdate( conn, rec );

      final Statement stmt = conn.createStatement();
      Log.log( Log.DEBUG, SqlUtils.class,
          "stmt created: " + stmt );

      final java.util.List v = getPreprocessors();

      for ( Iterator e = v.iterator(); e.hasNext();  )
      {
        final Preprocessor pr = (Preprocessor) e.next();
        pr.setView( view );
        sqlText = pr.process( sqlText );
      }

      final SqlParser parser = new SqlParser( sqlText, 0 );

      try
      {
        parser.findRealEndOfStatement();

        sqlText = sqlText.substring( 0, parser.getNextPos() );
      } catch ( SqlParser.SqlEotException ex )
      {
        System.err.println( ex );
      }

      Log.log( Log.DEBUG, SqlUtils.class, "After the variable substitution: [" + sqlText + "]" );

      final boolean bresult = stmt.execute( sqlText );

      if ( bresult )
        handleResultSet( view, stmt, rec, sqlText );
      else
        handleUpdateCount( view, stmt, rec, sqlText, startTime, startPos );

    } catch ( SQLException ex )
    {
      processSqlException( view, ex, sqlText, rec );
    } finally
    {
      rec.releaseConnection( conn );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  view     Description of Parameter
   * @param  ex       Description of Parameter
   * @param  sqlText  Description of Parameter
   * @param  rec      Description of Parameter
   * @since
   */
  public static void processSqlException( final View view,
      final SQLException ex,
      final String sqlText,
      final SqlServerRecord rec )
  {
    runInAWTThreadNoWait(
      new Runnable()
      {
        public void run()
        {
          GUIUtilities.message( view,
              "sql.sqlException",
              new Object[]{ex, ex.getMessage(), rec.getName()} );
        }
      } );
    Log.log( Log.ERROR, SqlUtils.class,
        "Error running SQL:" + sqlText );
    Log.log( Log.ERROR, SqlUtils.class, ex );
  }


  /**
   *  Description of the Method
   *
   * @param  r  Description of Parameter
   * @since
   */
  public static void runInAWTThreadAndWait( Runnable r )
  {
    if ( SwingUtilities.isEventDispatchThread() )
      r.run();
    else
      try
      {
        SwingUtilities.invokeAndWait( r );
      } catch ( Exception ex )
      {
        Log.log( Log.ERROR, SqlUtils.class,
            "Error running " + r + " in AWT Thread:" );
        Log.log( Log.ERROR, SqlUtils.class,
            ex );
      }
  }


  /**
   *  Description of the Method
   *
   * @param  r  Description of Parameter
   * @since
   */
  public static void runInAWTThreadNoWait( Runnable r )
  {
    if ( SwingUtilities.isEventDispatchThread() )
      r.run();
    else
      SwingUtilities.invokeLater( r );
  }


  /**
   *Description of the Method
   *
   * @since
   */
  public static void resetPreprocessors()
  {
    preprocessors = null;
  }


  /**
   *  Gets the Sysdate attribute of the SqlUtils class
   *
   * @param  conn              Description of Parameter
   * @param  rec               Description of Parameter
   * @return                   The Sysdate value
   * @exception  SQLException  Description of Exception
   * @since
   */
  protected static Timestamp getSysdate( Connection conn, SqlServerRecord rec )
       throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      cstmt = rec.prepareCall( conn, "getSysdate", null );
      if ( cstmt == null )
        return new Timestamp( new java.util.Date().getTime() );

      cstmt.registerOutParameter( 1, Types.TIMESTAMP );
      cstmt.execute();
      final Timestamp ts = cstmt.getTimestamp( 1 );
      return ts;
    } finally
    {
      rec.releaseStatement( cstmt );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  view              Description of Parameter
   * @param  stmt              Description of Parameter
   * @param  record            Description of Parameter
   * @param  text              Description of Parameter
   * @param  startTime         Description of Parameter
   * @param  startPos          Description of Parameter
   * @exception  SQLException  Description of Exception
   * @since
   */
  protected static void handleUpdateCount( final View view,
      Statement stmt,
      SqlServerRecord record,
      String text,
      Timestamp startTime,
      int startPos )
       throws SQLException
  {
    final Buffer buffer = view.getBuffer();
    final Connection conn = stmt.getConnection();
    final int updateCount = stmt.getUpdateCount();
    stmt.close();

    boolean anyObj = false;

    // still not clear whether this is correct...
    final String startTimeStr = dFormat.format( startTime );
    final String endTimeStr = dFormat.format( getSysdate( conn, record ) );

    PreparedStatement pstmt = null;
    try
    {
      pstmt =
          record.prepareStatement( conn,
          "selectLastChangedObjects",
          new Object[]{startTimeStr, endTimeStr} );

      // some SQL servers do not have objects...
      if ( pstmt != null )
      {
        final ResultSet rs = pstmt.executeQuery();

        while ( rs.next() )
        {
          anyObj = true;
          final String objectName = rs.getString( "objectName" );
          final String status = rs.getString( "status" );
          final String objectType = rs.getString( "objectType" );
          final String objectId = rs.getString( "objectId" );

          if ( text.toUpperCase().indexOf( objectName.toUpperCase() ) == -1 )
            continue;

          // some objects from other sessions...
          if ( "VALID".equals( status ) )
          {
            final Object args[] = {objectType, objectName, record.getName()};
            runInAWTThreadNoWait(
              new Runnable()
              {
                public void run()
                {
                  GUIUtilities.message( view, "sql.publishOK", args );
                }
              } );
          }
          else
          {
            final SqlParser parser = new SqlParser( text, 0 );

            int firstCodeCharOfs = parser.getNextPos();
            try
            {
              parser.skipWhiteSpace();
              firstCodeCharOfs = parser.getNextPos();
            } catch ( SqlParser.SqlEotException ex )
            {
              System.err.println( ex );
            }

            final int firstCodeLineNo = buffer.getLineOfOffset( firstCodeCharOfs + startPos );

            PreparedStatement dstmt = null;
            int cnt = 0;
            try
            {
              dstmt = record.prepareStatement(
                  conn,
                  "selectCodeObjectErrors",
                  new Object[]{objectName, objectType} );
              if ( dstmt == null )
                continue;

              final ResultSet drs = dstmt.executeQuery();
              while ( drs.next() )
              {
                int errLine = drs.getInt( "errRow" );
                int errPosition = drs.getInt( "errCol" );
                final String errText = drs.getString( "errorMessage" );

                if ( errLine == 1 )
                {
                  final int firstCodeLineDocOfs = buffer.getLineStartOffset( firstCodeLineNo );
                  errPosition += firstCodeCharOfs + startPos - firstCodeLineDocOfs;
                }
                errLine += firstCodeLineNo;

                final int finErrLine = errLine;
                final int finErrPosition = errPosition;
                runInAWTThreadNoWait(
                  new Runnable()
                  {
                    public void run()
                    {
                      errorSource.addError(
                          ErrorSource.ERROR,
                          buffer.getPath(),
                          finErrLine - 1,
                          finErrPosition - 1,
                          finErrPosition,
                          errText );
                    }
                  } );
                cnt++;
              }
            } finally
            {
              record.releaseStatement( dstmt );
            }
            final Object args[] = {objectType,
                objectName,
                record.getName(),
                new Integer( cnt )};

            runInAWTThreadNoWait(
              new Runnable()
              {
                public void run()
                {
                  final DockableWindowManager dockableWindowManager = view.getDockableWindowManager();
                  dockableWindowManager.showDockableWindow( "error-list" );
                  GUIUtilities.message( view, "sql.publishErr", args );
                }
              } );
          }
        }
      }
    } finally
    {
      record.releaseStatement( pstmt );
    }

    if ( !anyObj )
      runInAWTThreadNoWait(
        new Runnable()
        {
          final Object args[] = {new Integer( updateCount )};


          public void run()
          {
            GUIUtilities.message( view, "sql.updateOK", args );
          }
        } );
  }


  /**
   *  Description of the Method
   *
   * @param  view              Description of Parameter
   * @param  stmt              Description of Parameter
   * @param  record            Description of Parameter
   * @param  text              Description of Parameter
   * @exception  SQLException  Description of Exception
   * @since
   */
  protected static void handleResultSet( View view,
      Statement stmt,
      SqlServerRecord record,
      String text )
       throws SQLException
  {
    final ResultSet rs = stmt.getResultSet();
    final Object model = ResultSetWindow.prepareModel( rs );
    stmt.close();
    final View v = view;

    runInAWTThreadAndWait(
      new Runnable()
      {
        public void run()
        {
          ResultSetWindow wnd = SqlPlugin.showResultSetWindow( v );
          if ( wnd == null )
            return;

          wnd.updateByModel( model );
        }
      } );

  }


  /**
   *Description of the Method
   *
   * @since
   */
  protected static void fillPreprocessors()
  {
    preprocessors = new ArrayList();

    int i = 0;
    while ( true )
    {
      final String className = jEdit.getProperty( "sql.preprocessors." + i++ );
      if ( className == null || "".equals( className ) )
        break;
      try
      {
        preprocessors.add( Class.forName( className ).newInstance() );
      } catch ( Exception ex )
      {
        Log.log( Log.ERROR, SqlUtils.class, "Exception creating preprocessors" );
        Log.log( Log.ERROR, SqlUtils.class, ex );
      }
    }
  }
}

