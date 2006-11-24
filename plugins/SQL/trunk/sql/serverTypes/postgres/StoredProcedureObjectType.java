/*
 *  StoredProcedureObjectType.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *  Copyright (C) 2003 Gerke Kok
 *  gkokmdam@zonnet.nl ( for as long as Zonnet stays for free :-) )
 *
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package sql.serverTypes.postgres;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import sql.*;
import sql.serverTypes.*;

/**
 *  This class will retieve the text of a give stored procedure
 *
 * @author     gkokmdam
 * @created    23 Feb 2003
 */
public class StoredProcedureObjectType extends CodeObjectType
{

	/**
	 *Constructor for the StoredProcedureObjectType object
	 */
	public StoredProcedureObjectType()
	{
		super(null);
	}


	/**
	 *  Gets the Text attribute of the StoredPocedure
	 *
	 * @param  path      Description of Parameter
	 * @param  rec       Description of Parameter
	 * @param  schemaName  Description of Parameter
	 * @param  objName   Description of Parameter
	 * @return           The Text value
	 * @since
	 */
	public String getText(String path,
	                      SqlServerRecord rec,
	                      String schemaName,
	                      String objName)
	{
		Connection conn = null;
		try
		{
			conn = rec.allocConnection();

			PreparedStatement pstmt = null;
			try
			{
				// Some feature added by one of the PlugIns asks for
				// ".objName.marks", just ignore it
				// ( We might come up with some nice feature with this :-) )
				if (objName.toLowerCase().trim().endsWith(".marks"))
				{
					return null;
				}
				pstmt = rec.prepareStatement(
				                conn,
				                "selectStoredProcedureCode",
				                new Object[]{schemaName, objName});
				if (pstmt == null)
				{
					return null;
				}

				final ResultSet rs = SqlUtils.executeQuery(pstmt);

				final StringBuffer strProcText = new StringBuffer();
				String procLang = "pgplsql";
				int i = 0;
				while (rs.next())
				{
					strProcText.append(rs.getString("PROC_TEXT"));
					procLang = rs.getString("PROC_LANG");
					i++;
				}
				if (0 == i)
				{
					strProcText.append("/* Failed to get the procedure text\n */");
					GUIUtilities.message(jEdit.getLastView(),
					                     "sql.progress.ProcedureTextNotInDB",
					                     new Object[]{schemaName, objName});
				}
				final String sp =
				        "CREATE or REPLACE FUNCTION " + schemaName + "." + objName +
				        "\n/* This is just the text of the procedure */" +
				        "\n/* The parameters are not stored in the db :-( */" +
				        "\n/* ( the procedure consists of " + new Integer(i) + " parts in the db) */" +
				        "\nAS '\n" + strProcText +
				        "'\nLANGUAGE \'" + procLang + "\';";
				return sp;
			} finally
			{
				rec.releaseStatement(pstmt);
			}
		} catch (SQLException ex)
		{
			Log.log(Log.ERROR, StoredProcedureObjectType.class,
			        "Error loading object code");
			Log.log(Log.ERROR, StoredProcedureObjectType.class,
			        ex);
		} finally
		{
			rec.releaseConnection(conn);
		}

		return null;
	}
}

