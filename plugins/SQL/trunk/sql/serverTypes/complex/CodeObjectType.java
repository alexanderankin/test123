/**
 * CodeObjectType.java - Sql Plugin
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
package sql.serverTypes.complex;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.gjt.sp.util.*;

import sql.*;
import sql.serverTypes.ComplexVFS;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    12 Февраль 2003 г.
 */
public class CodeObjectType implements ComplexVFS.ObjectType
{
  protected String typeString;
  protected String statementPurpose4List;
  protected String statementPurpose4Text;


  /**
   *  Constructor for the CodeObjectType object
   *
   * @param  typeString  Description of Parameter
   * @since
   */
  public CodeObjectType( String typeString )
  {
    this( typeString, null );
  }


  /**
   *Constructor for the CodeObjectType object
   *
   * @param  typeString             Description of Parameter
   * @param  statementPurpose4List  Description of Parameter
   */
  public CodeObjectType( String typeString, String statementPurpose4List )
  {
    this( typeString, statementPurpose4List, null );
  }


  /**
   *Constructor for the CodeObjectType object
   *
   * @param  typeString             Description of Parameter
   * @param  statementPurpose4Text  Description of Parameter
   * @param  statementPurpose4List  Description of Parameter
   */
  public CodeObjectType( String typeString, String statementPurpose4List, String statementPurpose4Text )
  {
    this.typeString = typeString;
    this.statementPurpose4List = statementPurpose4List != null ? statementPurpose4List : "selectCodeObjectsInSchema";
    this.statementPurpose4Text = statementPurpose4Text != null ? statementPurpose4Text : "selectCodeObjectLines";
  }


  /**
   *  Gets the Text attribute of the CodeObjectType object
   *
   * @param  path      Description of Parameter
   * @param  rec       Description of Parameter
   * @param  userName  Description of Parameter
   * @param  objName   Description of Parameter
   * @return           The Text value
   */
  public String getText( String path,
      SqlServerRecord rec,
      String userName,
      String objName )
  {
    return rec.getServerType().getObjectCreationPrefix() + getSource( path, rec, userName, objName );
  }


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
  public String getSource( String path,
      SqlServerRecord rec,
      String userName,
      String objName )
  {
    Connection conn = null;
    try
    {
      conn = rec.allocConnection();
      final String text = SqlUtils.loadObjectText( conn,
          rec,
          statementPurpose4Text,
          userName,
          objName,
          typeString );
      return text;
    } catch ( SQLException ex )
    {
      Log.log( Log.ERROR, CodeObjectType.class,
          "Error loading object code" );
      Log.log( Log.ERROR, CodeObjectType.class,
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
    return statementPurpose4List;
  }


  /**
   *  Gets the Parameter attribute of the CodeObjectType object
   *
   * @return    The Parameter value
   * @since
   */
  public Object getParameter()
  {
    return typeString;
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

