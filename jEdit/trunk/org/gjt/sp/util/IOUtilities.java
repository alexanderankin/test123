/*
 * IOTools.java - IO related functions
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Matthieu Casanova
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

package org.gjt.sp.util;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;

/**
 * IO tools that depends on JDK only.
 *
 * @author Matthieu Casanova
 * @version $Id$
 * @since 4.3pre5
 */
public class IOUtilities
{
	//{{{ closeQuietly() method
	/**
	 * Method that will close an {@link InputStream} ignoring it if it is null and ignoring exceptions.
	 *
	 * @param in the InputStream to close.
	 */
	public static void closeQuietly(InputStream in)
	{
		if(in != null)
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				//ignore
			}
		}
	} //}}}

	//{{{ copyStream() method
	/**
	 * Method that will close an {@link OutputStream} ignoring it if it is null and ignoring exceptions.
	 *
	 * @param out the OutputStream to close.
	 */
	public static void closeQuietly(OutputStream out)
	{
		if(out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				//ignore
			}
		}
	} //}}}

	//{{{ copyStream() method
	/**
	 * Method that will close an {@link Reader} ignoring it if it is null and ignoring exceptions.
	 *
	 * @param r the Reader to close.
	 * @since jEdit 4.3pre5
	 */
	public static void closeQuietly(Reader r)
	{
		if(r != null)
		{
			try
			{
				r.close();
			}
			catch (IOException e)
			{
				//ignore
			}
		}
	} //}}}


	private IOUtilities(){}
}
