/**
 * ComplexVFS.java - Sql Plugin
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

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 á×ÇÕÓÔ 2001 Ç.
 */
public abstract class ComplexVFS extends SqlSubVFS
{

  protected Map objectTypes;
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static int SCHEMA_LEVEL = 2;
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static int OBJECT_TYPE_LEVEL = 3;
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static int OBJECT_LEVEL = 4;


  /**
   *Constructor for the ComplexVFS object
   *
   * @param  objectTypes  Description of Parameter
   */
  protected ComplexVFS( Map objectTypes )
  {
    this.objectTypes = objectTypes;
  }


  /**
   *  Gets the LevelDelimiter attribute of the ComplexVFS object
   *
   * @return    The LevelDelimiter value
   */
  public String getLevelDelimiter()
  {
    return ".";
  }


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
  public VFS.DirectoryEntry[] _listDirectory( Object session, String path,
      Component comp, SqlServerRecord rec, int level )
       throws IOException
  {
    Log.log( Log.DEBUG, ComplexVFS.class,
        "Listing " + path );
    VFS.DirectoryEntry[] retval = null;

    int i;
    int idx;

    switch ( level )
    {
        case SqlVFS.DB_LEVEL:

          retval = getEntriesFromDb( session,
              path,
              comp,
              rec,
              level,
              "selectSchemas",
              null );

          break;
        case SCHEMA_LEVEL:
          retval = new VFS.DirectoryEntry[objectTypes.size()];
          i = 0;
          for ( Iterator e = objectTypes.keySet().iterator(); e.hasNext();  )
          {
            final String r = (String) e.next();
            retval[i++] =
                _getDirectoryEntry( session, path + SqlVFS.separatorString + r, comp, level + 1 );
          }
          break;
        case OBJECT_TYPE_LEVEL:
          final ObjectType oType = getObjectType( path );

          if ( oType != null )
          {
            final String schema = SqlVFS.getPathComponent( path, SCHEMA_LEVEL );
            final Object args[] = oType.getParameter() == null ?
                new Object[]{schema} :
                new Object[]{schema, oType.getParameter()};

            retval = getEntriesFromDb( session,
                path,
                comp,
                rec,
                level,
                oType.getStatementPurpose(),
                args );
          }
    }
    Log.log( Log.DEBUG, ComplexVFS.class,
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
    return new VFS.DirectoryEntry( getSqlVFS().getFileName( path ), path, path,
        level == OBJECT_LEVEL ?
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
    final ObjectType ot = getObjectType( path );
    if ( ot != null && ot.showResultSetAfterLoad() )
      buffer.setBooleanProperty( SqlVFS.RUN_ON_LOAD_PROPERTY, true );

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
    final ObjectType ot = getObjectType( path );
    if ( ot == null )
      return null;
    final String userName = SqlVFS.getPathComponent( path, ComplexVFS.SCHEMA_LEVEL );
    if ( userName == null )
      return null;
    final String objName = SqlVFS.getPathComponent( path, ComplexVFS.OBJECT_LEVEL );
    if ( objName == null )
      return null;
    final SqlServerRecord rec = SqlVFS.getServerRecord( path );
    if ( rec == null )
      return null;

    final String text = ot.getText( path, rec, userName, objName );
    if ( text == null )
      return null;

    return new StringBufferInputStream( text );
  }


  /**
   *  Gets the ObjectType attribute of the ComplexVFS object
   *
   * @param  path  Description of Parameter
   * @return       The ObjectType value
   * @since
   */
  protected ObjectType getObjectType( String path )
  {
    final String otName = SqlVFS.getPathComponent( path, OBJECT_TYPE_LEVEL );
    if ( otName == null )
      return null;
    return (ObjectType) objectTypes.get( otName );
  }


  public interface ObjectType
  {
    public String getStatementPurpose();


    public Object getParameter();


    public boolean showResultSetAfterLoad();


    public String getText( String path,
        SqlServerRecord rec,
        String userName,
        String objectName );

  }
}

