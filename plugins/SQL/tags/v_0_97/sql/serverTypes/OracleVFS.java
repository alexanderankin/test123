/**
 * OracleVFS.java - Sql Plugin
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
import sql.serverTypes.oracle.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ������ 2001 �.
 */
public class OracleVFS extends ComplexVFS
{
  protected final static Map oracleObjectTypes = new HashMap();

  public OracleVFS()
  { super( oracleObjectTypes ); }

  static
  {
    oracleObjectTypes.put( "Functions",
        new CodeObjectType( "FUNCTION" ) );
    oracleObjectTypes.put( "Procedures",
        new CodeObjectType( "PROCEDURE" ) );
    oracleObjectTypes.put( "Packages",
        new CodeObjectType( "PACKAGE" ) );
    oracleObjectTypes.put( "Package Bodies",
        new CodeObjectType( "PACKAGE BODY" ) );
    oracleObjectTypes.put( "Java",
        new CodeObjectType( "JAVA SOURCE" ) );

    oracleObjectTypes.put( "Tables",
        new TableObjectType( "selectTablesInSchema" ) );
    oracleObjectTypes.put( "Views",
        new TableObjectType( "selectViewsInSchema" ) );

    oracleObjectTypes.put( "Triggers",
        new TriggerObjectType() );
  }

}

