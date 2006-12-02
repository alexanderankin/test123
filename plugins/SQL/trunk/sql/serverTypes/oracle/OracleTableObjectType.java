/**
 * OracleTableObjectType.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
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
		super("selectTablesInGroup");

		objectActions.put("Extract to DDL", new ExtractToDDLAction());
	}

	public static class ExtractToDDLAction extends SqlSubVFS.ObjectAction
	{
		public ExtractToDDLAction()
		{
			super(false);
		}

		public String getText(String path,
		                      SqlServerRecord rec,
		                      String userName,
		                      String objName)
		{
			Connection conn = null;
			try
			{
				conn = rec.allocConnection();

				Log.log(Log.DEBUG, OracleTableObjectType.class,
					"Getting DDL for " + userName + "." + objName);
				return SqlUtils.loadObjectText(conn,
	                                    rec,"selectTableDDL",userName,objName,null);

			} catch (SQLException ex)
			{
				Log.log(Log.ERROR, OracleTableObjectType.class,
				        "Error extracting table data");
				Log.log(Log.ERROR, OracleTableObjectType.class,
				        ex);
			} finally
			{
				rec.releaseConnection(conn);
			}

			return null;
		}
	}
}

