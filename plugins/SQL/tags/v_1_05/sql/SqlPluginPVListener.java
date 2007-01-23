/**
 * SqlPluginPVListener.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
 * Copyright (C) 2001, Sergey V. Udaltsov
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
import java.text.*;
import java.util.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

import projectviewer.*;
import projectviewer.event.*;
import projectviewer.config.*;
import projectviewer.vpt.*;

import sql.*;
import sql.options.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class SqlPluginPVListener
			implements ProjectViewerListener
{
	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void projectLoaded(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "Loading the project [" + evt.getProject() + "]");

		final ProjectViewer pv = evt.getProjectViewer();
		SqlPlugin.refreshToolBar(pv == null ? jEdit.getActiveView() : pv.getView(), evt.getProject());
	}

	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void nodeSelected(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "The node was selected [" + evt.getNode() + "]");
	}


	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void projectAdded(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "Removing the project [" + evt.getProject() + "]");
	}


	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void projectRemoved(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "Removing the project [" + evt.getProject() + "]");
	}


	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void nodeMoved(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "The Project has moved [" + evt.getProject() + "]");
	}

	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void groupAdded(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "The group was added [" + evt.getProject() + "]");
	}

	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void groupActivated(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "The group was activated [" + evt.getProject() + "]");
	}

	/**
	 *  Description of the Method
	 *
	 * @param  evt  Description of Parameter
	 */
	public void groupRemoved(ProjectViewerEvent evt)
	{
		Log.log(Log.DEBUG, SqlPluginPVListener.class,
		        "The group was Removed [" + evt.getProject() + "]");
	}
}

