/*
 * XSearchPlugin.java - Plugin for a number of text related functions
 * Copyright (C) 2002 Rudolf Widmann
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
package xsearch;

import org.gjt.sp.util.Log;

public class XSearchPlugin extends org.gjt.sp.jedit.EditPlugin
{
	public void start()
	{
		ReplaceActions.reset();
		xsearch.SearchAndReplace.load();
		Log.log(Log.DEBUG, XSearchPlugin.class,"+++ XSearchPlugin.24");
	}

	public void stop()
	{
		ReplaceActions.restore();
	}
	
}
