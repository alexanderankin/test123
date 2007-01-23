/**
 * SqlServerRecord.java - Sql Plugin
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

import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.*;

import projectviewer.vpt.*;
/**
 *Description of the Class
 *
 * @author     svu
 */
public class SqlServerRecord extends Properties
{

	protected SqlServerType dbType;
	protected MessageFormat tigFmt = null;

	protected String name;
	protected String statementDelimiterRegex;

	protected Map callableStmts = null;
	protected Map preparedStmts = null;
	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String PROP_TYPE = "type";

	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String PROP_DELIMITER_REGEX = "statementDelimiterRegex";

	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String PROP_SERVER_LIST = "sql.servers.list";

	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String PROP_USER = "user";
	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String PROP_PASSWORD = "password";

	protected static Map allRecordsPerProject = new HashMap();


	/**
	 *  Constructor for the SqlServerRecord object
	 *
	 * @param  type  Description of Parameter
	 * @since
	 */
	public SqlServerRecord()
	{
	}


	/**
	 *  Sets the StatementDelimiterRegex attribute of the SqlServerRecord object
	 *
	 * @param  statementDelimiterRegex  The new StatementDelimiterRegex value
	 */
	public void setStatementDelimiterRegex(String statementDelimiterRegex)
	{
		this.statementDelimiterRegex = statementDelimiterRegex;
	}


	/**
	 *  Sets the Name attribute of the SqlServerRecord object
	 *
	 * @param  name  The new Name value
	 * @since
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 *  Gets the StatementDelimiterRegex attribute of the SqlServerRecord object
	 *
	 * @return    The StatementDelimiterRegex value
	 */
	public String getStatementDelimiterRegex()
	{
		return statementDelimiterRegex;
	}


	/**
	 *  Gets the ServerType attribute of the SqlServerRecord object
	 *
	 * @return    The ServerType value
	 * @since
	 */
	public SqlServerType getServerType()
	{
		return dbType;
	}


	public void setServerType(SqlServerType type)
	{
		dbType = type;
	}


	/**
	 *  Gets the Name attribute of the SqlServerRecord object
	 *
	 * @return    The Name value
	 * @since
	 */
	public String getName()
	{
		return name;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  conn  Description of Parameter
	 * @since
	 */
	public void releaseConnection(Connection conn)
	{
		if (conn == null)
			return;

		try
		{
			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Connection " + getConnectionString() + " released");
			conn.close();
		} catch (SQLException ex)
		{
			Log.log(Log.ERROR, SqlServerRecord.class,
			        "Error closing connection");
			Log.log(Log.ERROR, SqlServerRecord.class,
			        ex);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  c                 Description of Parameter
	 * @param  name              Description of Parameter
	 * @param  args              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	public PreparedStatement prepareStatement(Connection c, String name, Object args[])
	throws SQLException
	{
		PreparedStatement rv = (PreparedStatement) preparedStmts.get(name);
		if (rv == null)
		{
			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Creating prepared stmt " + name);
			final SqlServerType.Statement stmt = dbType.getStatement(name);
			if (stmt == null)
			{
				Log.log(Log.ERROR, SqlServerRecord.class,
				        "Could not find statment " + name + " in the server type");
				return null;
			}

			final String stmtText = stmt.getStatementText(args);
			if (stmtText == null)
			{
				Log.log(Log.ERROR, SqlServerRecord.class,
				        "Strange, null text for non-null statement " + name);
				return null;
			}

			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Prepared text is " + stmtText);
			rv = c.prepareStatement(stmtText);
			stmt.setParams(rv, args);

			if (args == null)
				preparedStmts.put(name, rv);

		}
		else
		{
			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Reusing prepared stmt " + name);
		}

		return rv;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  c                 Description of Parameter
	 * @param  name              Description of Parameter
	 * @param  args              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	public CallableStatement prepareCall(Connection c, String name, Object args[])
	throws SQLException
	{
		CallableStatement rv = (CallableStatement) callableStmts.get(name);
		if (rv == null)
		{
			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Creating callable stmt " + name);
			final SqlServerType.Statement stmt = dbType.getStatement(name);
			if (stmt == null)
			{
				Log.log(Log.ERROR, SqlServerRecord.class,
				        "Could not find statment " + name + " in the server type");
				return null;
			}

			final String stmtText = stmt.getStatementText(args);
			if (stmtText == null)
			{
				Log.log(Log.ERROR, SqlServerRecord.class,
				        "Strange, null text for non-null statement " + name);
				return null;
			}

			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Callable text is " + stmtText);
			rv = c.prepareCall(stmtText);
			stmt.setParams(rv, args);

			if (args == null)
				callableStmts.put(name, rv);

		}
		else
		{
			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Reusing callable stmt " + name);
		}

		return rv;
	}


	/**
	 *  Description of the Method
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	public Connection allocConnection()
	throws SQLException
	{
		preparedStmts = new HashMap();
		callableStmts = new HashMap();

		final String connString = getConnectionString();
		Log.log(Log.DEBUG, SqlServerRecord.class,
		        "Connection " + connString + " allocated");

		final Properties params = new Properties();

		// Set other non-used properties, including user/password
		final Map connParams = dbType.getConnectionParameters();
		final String stringPattern = dbType.getProperty("connection.string");
		for (Iterator i = keySet().iterator(); i.hasNext();)
		{
			final String propName = (String)i.next();
			final String searchPattern = "{" + propName + "}";

			if (stringPattern.indexOf(searchPattern) != -1)
				continue;

			params.setProperty(propName, getProperty(propName));
		}

		return DriverManager.getConnection(connString, params);
	}


	protected String getJEditPropertyName(String propertyName)
	{
		return "sql.server." + name + "." + propertyName;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  project  Description of Parameter
	 * @since
	 */
	public void save(VPTProject project)
	{
		final Map connParams = dbType.getConnectionParameters();
		for (Iterator e = connParams.values().iterator(); e.hasNext();)
		{
			final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.next();
			SqlPlugin.setLocalProperty(project, getJEditPropertyName(param.getName()),
			                           getProperty(param.getName()));
		}

		SqlPlugin.setLocalProperty(project, getJEditPropertyName(PROP_TYPE), dbType.getName());
		SqlPlugin.setLocalProperty(project, getJEditPropertyName(PROP_DELIMITER_REGEX), statementDelimiterRegex);

		ensureNameInProjectServerList(project, name);

		enforceProjectServerListReloading(project);
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public String toString()
	{
		return name;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public boolean hasValidProperties()
	{
		final Map connParams = dbType.getConnectionParameters();
		for (Iterator e = connParams.values().iterator(); e.hasNext();)
		{
			final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.next();
			final String value = getProperty(param.getName());
			if (value == null)
				return false;
		}
		return true;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  project  Description of Parameter
	 * @since
	 */
	public void delete(VPTProject project)
	{
		final Map connParams = dbType.getConnectionParameters();
		for (Iterator e = connParams.values().iterator(); e.hasNext();)
		{
			final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.next();
			SqlPlugin.unsetLocalProperty(project, getJEditPropertyName(param.getName()));
		}

		SqlPlugin.unsetLocalProperty(project, getJEditPropertyName(PROP_TYPE));
		SqlPlugin.unsetLocalProperty(project, getJEditPropertyName(PROP_DELIMITER_REGEX));

		deleteNameFromServerList(project, name);

		enforceProjectServerListReloading(project);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  stmt              Description of Parameter
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	public void releaseStatement(PreparedStatement stmt)
	throws SQLException
	{
		Log.log(Log.DEBUG, SqlServerRecord.class,
		        "Statement " + stmt + " released");

		if (stmt == null)
			return;

		if (preparedStmts.containsValue(stmt) ||
		                callableStmts.containsValue(stmt))
			return;

		stmt.close();
	}


	/**
	 *  Gets the ConnectionString attribute of the SqlServerRecord object
	 *
	 * @return    The ConnectionString value
	 * @since
	 */
	protected String getConnectionString()
	{
		StringBuffer stringPattern = new StringBuffer(dbType.getProperty("connection.string"));

		final Map connParams = dbType.getConnectionParameters();
		for (Iterator e = connParams.values().iterator(); e.hasNext();)
		{
			final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.next();
			final String searchPattern = "{" + param.getName() + "}";
			final String value = getProperty(param.getName());

			int idx = new String(stringPattern).indexOf(searchPattern);

			while (idx != -1)
			{
				stringPattern = stringPattern.replace(idx, idx + searchPattern.length(), value);
				idx = new String(stringPattern).indexOf(searchPattern);
			}
		}

		return new String(stringPattern);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name     Description of Parameter
	 * @param  project  Description of Parameter
	 */
	protected void deleteNameFromServerList(VPTProject project, String name)
	{
		String allServerNames = SqlPlugin.getLocalProperty(project, PROP_SERVER_LIST);
		allServerNames = allServerNames.replaceAll("[\\s]*" + name + "[\\s]*", " ");
		SqlPlugin.setLocalProperty(project, PROP_SERVER_LIST, allServerNames);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name     Description of Parameter
	 * @param  project  Description of Parameter
	 */
	protected void ensureNameInProjectServerList(VPTProject project, String name)
	{
		String allServerNames = SqlPlugin.getLocalProperty(project, PROP_SERVER_LIST);
		if (allServerNames == null)
			allServerNames = "";
		final String pattern = ".*\\s" + name + "\\s.*";

		Log.log(Log.DEBUG, SqlServerRecord.class,
		        "matching [" + allServerNames + "] to [" + pattern + "]:" +
		        allServerNames.matches(pattern));
		if (allServerNames.matches(pattern))
			return;

		allServerNames = allServerNames + " " + name + " ";
		SqlPlugin.setLocalProperty(project, PROP_SERVER_LIST, allServerNames);
	}


	/**
	 *  Gets the AllNames attribute of the SqlServerRecord class
	 *
	 * @param  project  Description of Parameter
	 * @return          The AllNames value
	 */
	public static Object[] getAllNames(VPTProject project)
	{
		final Object[] allNames = getAllRecords(project).keySet().toArray();
		Arrays.sort(allNames, Collator.getInstance());
		return allNames;
	}


	/**
	 *  Gets the AllRecords attribute of the SqlServerRecord class
	 *
	 * @param  project  Description of Parameter
	 * @return          The AllRecords value
	 * @since
	 */
	public static Map getAllRecords(VPTProject project)
	{
		if (null == allRecordsPerProject.get(project))
		{
			final HashMap projRecords = new HashMap();

			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Loading all records");
			final Map servers = new HashMap();

			final String allServerNames = SqlPlugin.getLocalProperty(project, PROP_SERVER_LIST);
			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Server list: [" + allServerNames + "]");
			for (StringTokenizer st = new StringTokenizer(allServerNames == null ? "" : allServerNames);
			                st.hasMoreTokens();)
			{
				final String name = st.nextToken();

				Log.log(Log.DEBUG, SqlServerRecord.class,
				        "Found name " + name + " for loading");
				final SqlServerRecord sr = load(project, name);
				if (sr != null)
					projRecords.put(sr.getName(), sr);
			}
			allRecordsPerProject.put(project, projRecords);
			new Thread()
			{
				public void run()
				{
					EditBus.send(new SqlServerListChanged(null));
				}
			}.start();
		}
		return (Map) allRecordsPerProject.get(project);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name     Description of Parameter
	 * @param  project  Description of Parameter
	 * @return          Description of the Returned Value
	 * @since
	 */
	public static SqlServerRecord get(VPTProject project, String name)
	{
		final Map recs = getAllRecords(project);
		if (recs == null)
			return null;
		return (SqlServerRecord) recs.get(name);
	}


	protected static void enforceProjectServerListReloading(VPTProject project)
	{
		allRecordsPerProject.remove(project);
	}


	/**
	 *Description of the Method
	 *
	 * @param  project  Description of Parameter
	 * @since
	 */
	public static void clearProperties(VPTProject project)
	{
		SqlPlugin.unsetLocalProperty(project, PROP_SERVER_LIST);

		SqlPlugin.unsetLocalProperty(project, "sql.currentServerName");

		enforceProjectServerListReloading(project);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name     Description of Parameter
	 * @param  project  Description of Parameter
	 * @return          Description of the Returned Value
	 * @since
	 */
	protected static SqlServerRecord load(VPTProject project, String name)
	{
		Log.log(Log.DEBUG, SqlServerRecord.class,
		        "Loading server record " + name);

		final SqlServerRecord rv = new SqlServerRecord();

		rv.setName(name);

		final String dbTypeName = SqlPlugin.getLocalProperty(project, rv.getJEditPropertyName(PROP_TYPE));
		final String delimiter = SqlPlugin.getLocalProperty(project, rv.getJEditPropertyName(PROP_DELIMITER_REGEX));

		if (!rv.postLoadCaching(dbTypeName, delimiter))
			return null;

		final Map connParams = rv.getServerType().getConnectionParameters();

		for (Iterator e = connParams.values().iterator(); e.hasNext();)
		{
			final SqlServerType.ConnectionParameter param = (SqlServerType.ConnectionParameter) e.next();
			final String value =
			        SqlPlugin.getLocalProperty(project, rv.getJEditPropertyName(param.getName()));
			Log.log(Log.DEBUG, SqlServerRecord.class,
			        "Looking for " + param.getName() + " in local properties -> /" + value + "/");

			rv.setProperty(param.getName(), value == null ? param.getDefaultValue() : value);
		}

		return rv;
	}


	protected boolean postLoadCaching(String dbTypeName, String delimiter)
	{
		if (dbTypeName == null)
		{
			Log.log(Log.ERROR, SqlServerRecord.class,
			        "No server type specified for the record " + name);
			return false;
		}

		final SqlServerType dbType = SqlServerType.getByName(dbTypeName);
		if (dbType == null)
		{
			Log.log(Log.ERROR, SqlServerRecord.class,
			        "Could not determine the server type for the record " + name);
			return false;
		}

		setServerType(dbType);
		setStatementDelimiterRegex(delimiter);

		Log.log(Log.DEBUG, SqlServerRecord.class,
		        "Loaded " + name + "/" + dbTypeName);

		return true;
	}


	public static boolean isValidName(String name)
	{
		if (name.indexOf(SqlVFS.separatorChar) != -1 ||
		                name.indexOf(' ') != -1 ||
		                name.indexOf('\t') != -1)
		{
			return false;
		}
		return true;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  filename Description of Parameter
	 * @since
	 */
	public void exportTo(String filename, Component comp)
	{
		final VFS vfs = VFSManager.getVFSForPath(filename);
		final Object vfss = vfs.createVFSSession(filename, comp);

		final Properties copy = (Properties)this.clone();

		copy.setProperty(PROP_TYPE, dbType.getName());
		copy.setProperty(PROP_DELIMITER_REGEX, statementDelimiterRegex);

		try
		{
			final OutputStream os = vfs._createOutputStream(vfss, filename, comp);
			copy.store(os, null);
			os.close();
			vfs._endVFSSession(vfss, comp);
		} catch (IOException ex)
		{
			Log.log(Log.ERROR, SqlServerRecord.class,
			        "Error exporting to " + filename + ": " + ex);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  filename Description of Parameter
	 * @return          Description of the Returned Value
	 * @since
	 */
	public static SqlServerRecord importFrom(String filename, Component comp)
	{
		final VFS vfs = VFSManager.getVFSForPath(filename);
		final Object vfss = vfs.createVFSSession(filename, comp);

		final SqlServerRecord rv = new SqlServerRecord();

		try
		{
			final InputStream is = vfs._createInputStream(vfss, filename, false, comp);
			rv.load(is);
			is.close();
			vfs._endVFSSession(vfss, comp);

		} catch (IOException ex)
		{
			Log.log(Log.ERROR, SqlServerRecord.class,
			        "Error importing from " + filename + ": " + ex);
			return null;
		}

		final String dbTypeName = rv.getProperty(PROP_TYPE);
		final String delimiter = rv.getProperty(PROP_DELIMITER_REGEX);

		rv.remove(PROP_TYPE);
		rv.remove(PROP_DELIMITER_REGEX);

		if (rv.postLoadCaching(dbTypeName, delimiter))
			return rv;

		return null;
	}
}

