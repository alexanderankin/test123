/*
 * HyperSearchPath.java - HyperSearch result
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Rudolf Widmann
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

import org.gjt.sp.jedit.*;


/**
 * class for the hyper search path
 * displays: buffer and buffer path
 */
public class HyperSearchPath extends HyperSearchResult
{

	//{{{ HyperSearchResult method
	HyperSearchPath(Buffer buffer, int line, int start, int end)
	{
		//super(buffer,  line,  start,  end);
		super(buffer,  line);
		this.addOccur(start, end);
		//Log.log(Log.DEBUG, BeanShell.class,"+++ HyperSearchPath.38: path = "+path);
		//Log.log(Log.DEBUG, BeanShell.class,"+++ HyperSearchPath.51: MiscUtilities.getFileName(path) = "+MiscUtilities.getFileName(path));
		//Log.log(Log.DEBUG, BeanShell.class,"+++ HyperSearchPath.53: MiscUtilities.getParentOfPath(path) = "+MiscUtilities.getParentOfPath(path));
		str = MiscUtilities.getFileName(path) + " ("+
			MiscUtilities.getParentOfPath(path)+")";
		//Log.log(Log.DEBUG, BeanShell.class,"+++ HyperSearchPath.53: MiscUtilities.getParentOfPath(path) = "+MiscUtilities.getParentOfPath(path));
	} //}}}

}
