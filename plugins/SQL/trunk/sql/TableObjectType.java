/**
 * TableObjectType.java - Sql Plugin
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

import java.io.*;
import java.util.*;

import sql.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class TableObjectType extends SqlSubVFS.ObjectType
{

  /**
   *Constructor for the TableObjectType object
   *
   * @param  stmtName  Description of Parameter
   * @since
   */
  public TableObjectType( String stmtName )
  {
    super( stmtName, null );
    
    objectActions.put( "Data", new DataAction() );
  }

  public static class DataAction extends SqlSubVFS.ObjectAction
  {
    public DataAction()
    { 
      super( true);
    }

    /**
     *  Gets the Text attribute of the TableObjectType object
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
      return "SELECT * FROM " +
          userName +
          ( rec.getServerType().getSubVFS() ).getLevelDelimiter() +
          objName;
    }

  }

}

