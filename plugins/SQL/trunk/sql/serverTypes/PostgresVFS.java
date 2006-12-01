/**
 * ProgressVFS.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
 * Copyright (C) 2003 Gerke Kok
 * gkokmdam@zonnet.nl
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
import sql.serverTypes.postgres.*;

/**
 *  What ever a VFS class needs to do
 *
 * @author     gkokmdam
 * @created    11 February 2003
 */
public class PostgresVFS extends ComplexVFS
{
	/**
	 *  Description of the Field
	 */
	protected final static Map progressObjectTypes = new HashMap();


	/**
	 *  Constructor for the PostgresVFS object
	 */
	public PostgresVFS()
	{
		super(progressObjectTypes);
	}

	static
	{
		progressObjectTypes.put("Tables",
		                        new TableObjectType("selectTablesInGroup"));
		progressObjectTypes.put("Views",
		                        new TableObjectType("selectViewsInGroup"));
		progressObjectTypes.put("StoredProcedures",
		                        new StoredProcedureObjectType());
//    progressObjectTypes.put( "Triggers",
//        new TriggerObjectType() );
	}

}

