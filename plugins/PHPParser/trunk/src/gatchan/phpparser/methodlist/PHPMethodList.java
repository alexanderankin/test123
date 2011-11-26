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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.sourceforge.phpdt.internal.compiler.ast.Type;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

/**
 * @author Matthieu Casanova
 */
public class PHPMethodList
{
	private final Map<String, Function> functions;

	public PHPMethodList()
	{
		functions = new HashMap<String, Function>();
	}

	public void load()
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(
				new InputStreamReader(PHPMethodList.class.getClassLoader().getResourceAsStream(
					"functionlist")));
			String line = reader.readLine();
			while (line != null)
			{
				if (line.charAt(0) != '#')
				{
					Function function = parseFunction(line);
					functions.put(function.getName(), function);
				}
				line = reader.readLine();
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			IOUtilities.closeQuietly(reader);
		}
	}

	private Function parseFunction(String line)
	{
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter(":");
		String t = scanner.next();
		Type type = Type.fromString(t);
		String name = scanner.next();
		List<Argument> arguments = new LinkedList<Argument>();
		boolean vararg = false;
		while (scanner.hasNext())
		{
			String arg = scanner.next();
			boolean ref = false;
			if (arg.charAt(0) == '&')
			{
				ref = true;
				arg = arg.substring(1);
			}
			if (arg.endsWith("..."))
			{
				vararg = true;
				arg = arg.substring(0, arg.length() - 3);
			}
			int i = arg.indexOf('=');
			Argument argument;
			if (i != -1)
			{
				String argType = arg.substring(0, i);
				String defaultValue = arg.substring(i+1);
				argument = new Argument(Type.fromString(argType), ref, defaultValue);
			}
			else
			{
				argument = new Argument(Type.fromString(arg), ref, null);
			}

			arguments.add(argument);
		}
		Function function = new Function(type, name, arguments.toArray(new Argument[arguments.size()]), vararg);
		return function;
	}

	public Function getFunction(String name)
	{
		return functions.get(name);
	}
}
