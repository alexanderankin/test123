/**
 * SqlServerListChanged.java - Sql Plugin
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

import org.gjt.sp.jedit.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class SqlServerListChanged extends EBMessage
{

	/**
	 *Constructor for the SqlServerListChanged object
	 *
	 * @param  source  Description of Parameter
	 */
	public SqlServerListChanged(EBComponent source)
	{
		super(source);
	}

}

