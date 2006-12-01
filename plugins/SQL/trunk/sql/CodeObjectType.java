/**
 * CodeObjectType.java - Sql Plugin
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

package sql;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.gjt.sp.util.*;

import sql.*;

/**
 *  Description of the Class
 *
 *  @author     svu
 */
public class CodeObjectType extends SqlSubVFS.ObjectType
{

	protected String extractionStatementPurpose;

	/**
	 *  Constructor for the CodeObjectType object
	 *
	 *  @param  typeString  Description of Parameter
	 *  @since
	 */
	public CodeObjectType(String type)
	{
		this(type, null);
	}


	/**
	 *Constructor for the CodeObjectType object
	 *
	 * @param  type             Description of Parameter
	 * @param  statementPurpose  Description of Parameter
	 */
	public CodeObjectType(String type, String statementPurpose)
	{
		this(type, statementPurpose, null);
	}


	/**
	 *Constructor for the CodeObjectType object
	 *
	 * @param  typeString             Description of Parameter
	 * @param  statementPurpose4Text  Description of Parameter
	 * @param  statementPurpose4List  Description of Parameter
	 */
	public CodeObjectType(String type, String statementPurpose4List, String statementPurpose4Text)
	{
		super(statementPurpose4List != null ? statementPurpose4List : "selectCodeObjectsInGroup", type);

		this.extractionStatementPurpose = statementPurpose4Text != null ? statementPurpose4Text : "selectCodeObjectLines";

		objectActions.put("Source Code",
		                  new SourceCodeAction(type));
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
	public String getSource(String path,
	                        SqlServerRecord rec,
	                        String userName,
	                        String objName)
	{
		Connection conn = null;
		try
		{
			conn = rec.allocConnection();
			final String text = SqlUtils.loadObjectText(conn,
			                    rec,
			                    extractionStatementPurpose,
			                    userName,
			                    objName,
			                    (String)parameter);
			return text;
		} catch (SQLException ex)
		{
			Log.log(Log.ERROR, CodeObjectType.class,
			        "Error loading object code");
			Log.log(Log.ERROR, CodeObjectType.class,
			        ex);
		} finally
		{
			rec.releaseConnection(conn);
		}
		return null;
	}

	public class SourceCodeAction extends SqlSubVFS.ObjectAction
	{
		public SourceCodeAction(String type)
		{
			super(false);
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
		public String getText(String path,
		                      SqlServerRecord rec,
		                      String userName,
		                      String objName)
		{
			return rec.getServerType().getObjectCreationPrefix() + getSource(path, rec, userName, objName);
		}
	}
}

