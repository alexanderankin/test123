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


  static
  {
    sybaseObjectTypes.put( "Defaults",
        new CodeObjectType( "D" ) );
    sybaseObjectTypes.put( "Procedures",
        new CodeObjectType( "P" ) );
    sybaseObjectTypes.put( "Extended Procedures",
        new CodeObjectType( "XP" ) );
    sybaseObjectTypes.put( "Functions",
        new CodeObjectType( "FN" ) );
    sybaseObjectTypes.put( "Triggers",
        new CodeObjectType( "TR" ) );

    sybaseObjectTypes.put( "Tables",
        new TableObjectType( "selectTablesInSchema" ) );
    sybaseObjectTypes.put( "Views",
        new TableObjectType( "selectViewsInSchema" ) );
  }

}

