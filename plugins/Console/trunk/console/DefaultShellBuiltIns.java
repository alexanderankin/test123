/*
 * DefaultShellBuiltIns.java - A few commands built in to the console
 * Copyright (C) 2001 Slava Pestov
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

package console;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.HelpViewer;
import java.util.*;

class DefaultShellBuiltIns
{
	static void executeBuiltIn(View view, String name,
		Vector args, Console console)
	{
		if(name.equals("clear"))
		{
			if(checkArgs("clear",args,1,console))
				console.clear();
		}
		else if(name.equals("echo"))
		{
			if(checkArgs("echo",args,-2,console))
			{
				for(int i = 1; i < args.size(); i++)
				{
					console.printPlain((String)args.elementAt(i));
				}
			}
		}
		else if(name.equals("help"))
		{
			if(checkArgs("help",args,1,console))
			{
				new HelpViewer(DefaultShellBuiltIns.class
					.getResource("/console/Console.html")
					.toString());
			}
		}
		else if(name.equals("version"))
		{
			if(checkArgs("version",args,1,console))
			{
				console.printPlain(jEdit.getProperty(
					"plugin.console.ConsolePlugin.version"));
			}
		}
		else
		{
			String[] pp = { name };
			console.printError(jEdit.getProperty("console.shell.unknown-builtin",pp));
		}
	}

	// private members
	private DefaultShellBuiltIns() {}

	// if count < 0, then at least -count arguments must be specified
	// if count > 0, then exactly count arguments must be specified
	private static boolean checkArgs(String command, Vector args, int count,
		Console console)
	{
		if(count < 0)
		{
			if(args.size() >= -count)
				return true;
		}
		else if(count > 0)
		{
			if(args.size() == count)
				return true;
		}

		console.printError(jEdit.getProperty("console.shell.usage." + command));
		return false;
	}
}
