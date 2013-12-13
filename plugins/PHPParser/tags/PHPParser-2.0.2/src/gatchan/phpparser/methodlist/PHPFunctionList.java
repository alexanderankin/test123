/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gatchan.phpparser.methodlist;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

/**
 * @author Matthieu Casanova
 */
public class PHPFunctionList
{
	private Map<String, Function> functions;

	public PHPFunctionList()
	{
		functions = new HashMap<String, Function>();
	}

	public void reload()
	{
		Map<String, Function> functions = new HashMap<String, Function>();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(
				new InputStreamReader(PHPFunctionList.class.getClassLoader().getResourceAsStream(
					"functionlist")));
			FunctionListParser parser = new FunctionListParser(reader);
			functions = parser.parse();
		}
		catch (ParseException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		catch (TokenMgrError e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			IOUtilities.closeQuietly(reader);
		}
		this.functions = functions;
	}

	public Function getFunction(String name)
	{
		return functions.get(name);
	}
}
