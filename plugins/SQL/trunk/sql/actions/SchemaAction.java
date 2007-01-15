/**
 * SchemaAction.java - Sql Plugin
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

package sql.actions;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.*;

import sql.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class SchemaAction extends SqlSubVFS.ObjectAction
{
	public SchemaAction()
	{
		super(false);
	}


	public int getActionEntryType()
	{
		return VFS.DirectoryEntry.DIRECTORY;
	}


	public String getText(String path,
	                      SqlServerRecord rec,
	                      String userName,
	                      String objName)
	{
		Connection conn = null;

		final String colName = SqlVFS.getPathComponent(path, SqlSubVFS.OBJECT_ACTION_LEVEL + 1);
		if (colName == null)
			return null;

		String rv = "";
		rv += "Column: \"" + colName +"\"";

		try
		{
			conn = rec.allocConnection();
			final PreparedStatement stmt = conn.prepareStatement("SELECT " + colName + " FROM " +
			                               getFullObjectName(rec, userName, objName) +
			                               " WHERE 1 = 0");
			final ResultSet rs = stmt.executeQuery();
			final ResultSetMetaData rsmd = rs.getMetaData();

			rv += "\nLabel: \"" + rsmd.getColumnLabel(1) +"\"";
			rv += "\nType: " + rsmd.getColumnTypeName(1);
			rv += "\nPrecision: " + rsmd.getPrecision(1);
			rv += "\nScale: " + rsmd.getScale(1);
			rv += "\nAutoIncrement: " + rsmd.isAutoIncrement(1);
			rv += "\nCaseSensitive: " + rsmd.isCaseSensitive(1);
			rv += "\nCurrency: " + rsmd.isCurrency(1);
			rv += "\nNullable: " + rsmd.isNullable(1);
			rv += "\nReadOnly: " + rsmd.isReadOnly(1);
			rv += "\nSearchable: " + rsmd.isSearchable(1);
			rv += "\nSigned: " + rsmd.isSigned(1);
			rv += "\nWritable: " + rsmd.isWritable(1);

		} catch (SQLException ex)
		{
			Log.log(Log.ERROR, TableObjectType.class,
			        "Could not retrieve the properties of the column \"" + colName + "\": " + ex);
		}
		finally
		{
			rec.releaseConnection(conn);
		}

		return rv;
	}


	public VFS.DirectoryEntry[] getEntries(Object session,
	                                       String path,
	                                       SqlServerRecord rec)
	{
		Connection conn = null;
		final List cols = new ArrayList();

		final String userName = SqlVFS.getPathComponent(path, SqlSubVFS.OBJECTGROUP_LEVEL);
		if (userName == null)
			return null;
		final String objName = SqlVFS.getPathComponent(path, SqlSubVFS.OBJECT_LEVEL);
		if (objName == null)
			return null;

		try
		{
			conn = rec.allocConnection();
			final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
			                               getFullObjectName(rec, userName, objName) +
			                               " WHERE 1 = 0");
			final ResultSet rs = stmt.executeQuery();
			final ResultSetMetaData rsmd = rs.getMetaData();

			for (int i = rsmd.getColumnCount(), j = 0; --i >= 0;)
				cols.add(rsmd.getColumnName(++j));

		} catch (SQLException ex)
		{
			Log.log(Log.ERROR, TableObjectType.class,
			        "Could not retrieve the list of columns: " + ex);
		}
		finally
		{
			rec.releaseConnection(conn);
		}

		final VFS.DirectoryEntry[] retval = new VFS.DirectoryEntry[cols.size()];

		int i = 0;
		for (Iterator it = cols.iterator(); it.hasNext();)
		{
			final SqlSubVFS.VFSObjectRec r = new SqlSubVFS.VFSObjectRec((String)it.next());
			r.setDir(path);
			retval[i++] = new SqlSubVFS.SqlDirectoryEntry(r, VFS.DirectoryEntry.FILE);
		}

		return retval;

	}

}

