/**
 * SqlPlugin.java - Sql Plugin
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
import java.sql.*;
import java.util.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;

import projectviewer.*;
import projectviewer.event.*;
import projectviewer.config.*;
import projectviewer.vpt.*;

import sql.options.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class SqlPlugin extends EBPlugin
{
	protected static Hashtable sqlToolBars = new Hashtable();
	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String NAME = "Sql Plugin";
	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String DEFAULT_EDIT_MODE_NAME = "transact-sql";

	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public final static String resultSetWinName = "sql.resultSet";
	/**
	 *  Description of the Field
	 *
	 * @since
	 */
	public static ImageIcon icon;

	protected static SqlVFS sqlVFS;


	/**
	 *  Description of the Method
	 *
	 * @since
	 */
	public void start()
	{
		final File settingsDir = new File(MiscUtilities.constructPath(
		                                          jEdit.getSettingsDirectory(), "sql"));
		if (!settingsDir.exists())
			settingsDir.mkdirs();

		registerJdbcClassPath();

		EditBus.addToBus(new ResultSetWindow.BufferListener());

		SqlUtils.init();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  message  Description of Parameter
	 * @since
	 */
	public void handleMessage(EBMessage message)
	{
		if (message instanceof ViewUpdate)
		{
			final ViewUpdate vu = (ViewUpdate) message;
			final View view = vu.getView();
			if (vu.getWhat() == ViewUpdate.CREATED)
			{
				final VPTProject project = SqlUtils.getProject(view);
				Log.log(Log.DEBUG, SqlPlugin.class, "new View " + view + " got project " + project);

				refreshToolBar(view, project);
			}
			else if (vu.getWhat() == ViewUpdate.CLOSED)
			{
				sqlToolBars.remove(view);
			}
		}
		else if (message instanceof PropertiesChanged)
		{
			Log.log(Log.DEBUG, SqlPlugin.class, "properties changed!");
			handlePropertiesChanged();
		}
		else if (message instanceof ViewerUpdate)
		{
			final ViewerUpdate vupdate = (ViewerUpdate)message;
			if (vupdate.getType() == ViewerUpdate.Type.PROJECT_LOADED)
			{
				final VPTNode node = vupdate.getNode();
				Log.log(Log.DEBUG, SqlPlugin.class,
				        "Loading the project [" + node.getNodePath() + "]");
				final ProjectViewer pv = vupdate.getViewer();
				if (node instanceof VPTProject)
					refreshToolBar(pv == null ? jEdit.getActiveView() : pv.getView(), (VPTProject)node);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view     Description of Parameter
	 * @param  project  Description of Parameter
	 */
	public static void refreshToolBar(View view, VPTProject project)
	{
		removeToolBar(view);
		if (SqlToolBar.showToolBar())
		{
			addToolBar(view, project);
		}
	}


	private void handlePropertiesChanged()
	{
		View view = jEdit.getFirstView();

		while (view != null)
		{
			refreshToolBar(view, SqlUtils.getProject(view));
			view = view.getNext();
		}
	}


	public static void addToolBar(View view, VPTProject project)
	{
		// create new
		final SqlToolBar toolbar = new SqlToolBar(view, project);
		sqlToolBars.put(view, toolbar);
		view.addToolBar(toolbar);
	}


	public static void removeToolBar(View view)
	{
		final SqlToolBar toolbar = (SqlToolBar) sqlToolBars.get(view);
		if (toolbar != null)
		{
			// Try to remove toolbar
			// (this does nothing if there is no toolbar)
			view.removeToolBar(toolbar);
			sqlToolBars.remove(view);
		}
	}



	/**
	 *  Sets the GlobalProperty attribute of the SqlPlugin class
	 *
	 * @param  name   The new GlobalProperty value
	 * @param  value  The new GlobalProperty value
	 */
	public static void setGlobalProperty(String name, String value)
	{
		jEdit.setProperty(name, value);
	}


	/**
	 *  Sets the LocalProperty attribute of the SqlPlugin class
	 *
	 * @param  name     The new LocalProperty value
	 * @param  value    The new LocalProperty value
	 * @param  project  The new LocalProperty value
	 */
	public static void setLocalProperty(VPTProject project, String name, String value)
	{
		project.setProperty(name, value);
	}


	/**
	 *  Sets the BufferMode attribute of the SqlPlugin class
	 *
	 * @param  buf   The new BufferMode value
	 * @param  name  The new BufferMode value
	 * @since
	 */
	public static void setBufferMode(Buffer buf, String name)
	{
		final Mode mode = jEdit.getMode(name);
		if (mode != null)
			buf.setMode(mode);
	}


	/**
	 *Sets the JdbcClassPath attribute of the SqlPlugin class
	 *
	 * @param  jdbcClassPath  The new JdbcClassPath value
	 * @since
	 */
	public static void setJdbcClassPath(String[] jdbcClassPath)
	{
		final String[] oldCp = getJdbcClassPath();

		unregisterJdbcClassPath();

		for (int i = oldCp.length; --i >= 0;)
			unsetGlobalProperty("sql.jdbcClassPath." + i);

		if (jdbcClassPath != null)
		{
			for (int i = jdbcClassPath.length; --i >= 0;)
				setGlobalProperty("sql.jdbcClassPath." + i, jdbcClassPath[i]);

		}
		registerJdbcClassPath();
	}


	/**
	 *  Gets the LocalProperty attribute of the SqlPlugin class
	 *
	 * @param  name     Description of Parameter
	 * @param  project  Description of Parameter
	 * @return          The LocalProperty value
	 */
	public static String getLocalProperty(VPTProject project, String name)
	{
		try
		{
			if (project == null)
				return null;
			Log.log(Log.DEBUG, SqlPlugin.class,
			        "Looking for the property [" + name + "] of " + project);
			final String val = project.getProperty(name);
			Log.log(Log.DEBUG, SqlPlugin.class,
			        "Found [" + val + "]");
			return val;
		} catch (NullPointerException ex)
		{
			Log.log(Log.DEBUG, SqlPlugin.class,
			        "Error!");
			ex.printStackTrace();
			return null;
		}
	}


	/**
	 *  Gets the GlobalProperty attribute of the SqlPlugin class
	 *
	 * @param  name  Description of Parameter
	 * @return       The GlobalProperty value
	 */
	public static String getGlobalProperty(String name)
	{
		return jEdit.getProperty(name);
	}


	/**
	 *Gets the JdbcClassPath attribute of the SqlPlugin class
	 *
	 * @return    The JdbcClassPath value
	 * @since
	 */
	public static String[] getJdbcClassPath()
	{
		final java.util.List v = new ArrayList();
		int i = 0;
		while (true)
		{
			final String s = getGlobalProperty("sql.jdbcClassPath." + i++);
			if (s == null)
				break;
			v.add(s);
		}
		return (String[]) v.toArray(new String[0]);
	}


	/**
	 *Description of the Method
	 *
	 * @param  project  Description of Parameter
	 * @since
	 */
	public static void clearLocalProperties(VPTProject project)
	{
		SqlServerRecord.clearProperties(project);
	}


	/**
	 *  Description of the Method
	 */
	public static void clearGlobalProperties()
	{
		ResultSetPanel.clearProperties();
		SqlToolBar.clearProperties();
	}



	/**
	 *  Description of the Method
	 *
	 * @param  name  Description of Parameter
	 */
	public static void unsetGlobalProperty(String name)
	{
		jEdit.unsetProperty(name);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name     Description of Parameter
	 * @param  project  Description of Parameter
	 */
	public static void unsetLocalProperty(VPTProject project, String name)
	{
		project.removeProperty(name);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view  Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	public static ResultSetWindow showResultSetWindow(View view)
	{
		final DockableWindowManager dockableWindowManager = view.getDockableWindowManager();
		if (!dockableWindowManager.isDockableWindowVisible(resultSetWinName))
			dockableWindowManager.addDockableWindow(resultSetWinName);

		dockableWindowManager.showDockableWindow(resultSetWinName);

		return (ResultSetWindow) dockableWindowManager.getDockable(resultSetWinName);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view        Description of Parameter
	 * @param  serverName  Description of Parameter
	 * @since
	 */
	public static void loadObject(final View view, final String serverName)
	{
		SqlUtils.getThreadGroup().runInGroup(
		        new Runnable()
		        {
			        public void run()
			        {
				        final SqlServerRecord rec = SqlUtils.getServerRecord(SqlUtils.getProject(view), serverName);
				        if (rec == null)
					        return;

				        Connection conn = null;
				        try
				        {
					        conn = rec.allocConnection();

					        final String user = rec.getProperty(rec.PROP_USER).toUpperCase();
					        final Object[] objs = SqlUtils.loadObjectList(view, conn, rec, user);
					        if (objs == null)
						        return;

					        final DbCodeObject dbobj = chooseCodeObjectInAWTThread(view, objs);
					        if (dbobj == null)
						        return;

					        final String text = rec.getServerType().getObjectCreationPrefix() + SqlUtils.loadObjectText(conn, rec, user, dbobj.name, dbobj.type);

					        if (text == null)
					        {
						        Log.log(Log.NOTICE, SqlPlugin.class,
						                "Got null retrieving the object text for " + dbobj.name);
						        return;
					        }

					        SqlUtils.runInAWTThreadNoWait(
					                new Runnable()
					                {
						                public void run()
						                {
							                final Buffer buf = jEdit.newFile(view);
							                buf.insert(0, text);
							                setBufferMode(buf, rec.getServerType().getEditModeName());
						                }
					                });

				        } catch (SQLException ex)
				        {
					        SqlUtils.processSqlException(view, ex, "??", rec);
				        } finally
				        {
					        rec.releaseConnection(conn);
				        }
			        }
		        });
	}


	/**
	 *Constructor for the registerJdbcClass object
	 *
	 * @since
	 */
	public static void registerJdbcClassPath()
	{
		final String[] jdbcClassPath = getJdbcClassPath();

		if (jdbcClassPath != null)
			for (int i = jdbcClassPath.length; --i >= 0;)
			{
				final String path = jdbcClassPath[i];
				if (!(new File(path).exists()))
				{
					Log.log(Log.ERROR, SqlPlugin.class,
					        "JDBC classpath component " + path + " does not exist");
					continue;
				}
				final PluginJAR jar = jEdit.getPluginJAR(path);
				if (jar == null)
				{// not registered yet
					jEdit.addPluginJAR(path);
				}
			}

		VFSManager.sendVFSUpdate(sqlVFS, SqlVFS.PROTOCOL + ":/", false);
	}


	/**
	 *Description of the Method
	 *
	 * @since
	 */
	public static void unregisterJdbcClassPath()
	{
		SqlServerType.dropAll();

		final String[] jdbcClassPath = getJdbcClassPath();
		if (jdbcClassPath != null)
			for (int i = jdbcClassPath.length; --i >= 0;)
			{
				final String path = jdbcClassPath[i];
				if (!(new File(path).exists()))
				{
					Log.log(Log.ERROR, SqlPlugin.class,
					        "JDBC classpath component " + path + " does not exist");
					continue;
				}
				final PluginJAR jar = jEdit.getPluginJAR(path);
				if (jar == null)
				{
					Log.log(Log.ERROR, SqlPlugin.class,
					        "Strange, classpath element " + path + " was not registered");
				}
				else
					jEdit.removePluginJAR(jar, false);
			}

		VFSManager.sendVFSUpdate(sqlVFS, SqlVFS.PROTOCOL + ":/", false);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view  Description of Parameter
	 * @param  objs  Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	protected static DbCodeObject chooseCodeObjectInAWTThread(final View view,
	                final Object objs[])
	{
		final java.util.List rv = Collections.synchronizedList(new ArrayList());

		final Runnable r =
		        new Runnable()
		        {
			        public void run()
			        {
				        final JComboBox combo = new JComboBox(objs);

				        final Object controls[] = new Object[2];
				        controls[0] = jEdit.getProperty("sql.objectchooser.prompt");
				        controls[1] = combo;

				        final JOptionPane p = new JOptionPane(
				                                      controls,
				                                      JOptionPane.INFORMATION_MESSAGE,
				                                      JOptionPane.OK_CANCEL_OPTION,
				                                      getIcon());

				        final JDialog dlg = p.createDialog(view,
				                                           jEdit.getProperty("sql.objectchooser.title"));

				        combo.setRenderer(new DbCodeObject.CellRenderer());

				        dlg.show();

				        final Object val = p.getValue();

				        if (!new Integer(JOptionPane.OK_OPTION).equals(val))
					        return;

				        if (combo.getSelectedIndex() == -1)
					        return;

				        final Object obj = combo.getItemAt(combo.getSelectedIndex());

				        rv.add(obj);
			        }
		        };

		SqlUtils.runInAWTThreadAndWait(r);

		if (rv.size() == 0)
			return null;

		return (DbCodeObject) rv.get(0);
	}

	public static ImageIcon getIcon()
	{
		if (icon == null)
		{
			icon = new ImageIcon(
			               Toolkit.getDefaultToolkit().getImage(
			                       SqlPlugin.class.getClassLoader().getResource("SqlPlugin.gif")));
		}
		return icon;
	}

	public static class SqlOptions implements OptionsService
	{
		/**
		*  Description of the Method
		*
		* @param  project        Description of Parameter
		*/
		public OptionPane getOptionPane(VPTProject project)
		{
			return new ServersOptionPane(project);
		}


		/**
		*  Description of the Method
		*
		* @param  project        Description of Parameter
		*/
		public OptionGroup getOptionGroup(VPTProject project)
		{
			return null;
		}
	}	
}

