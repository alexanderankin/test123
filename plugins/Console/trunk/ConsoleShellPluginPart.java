/*
 * ConsoleShellPluginPart.java - Manages console shell
 * Copyright (C) 2000 Slava Pestov
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

import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

public class ConsoleShellPluginPart extends EBPlugin
{
	public static final String NAME = "Console";

	public void start()
	{
		errorSource = new DefaultErrorSource(NAME);
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
		EditBus.addToBus(errorSource);
		EditBus.addToNamedList(Shell.SHELLS_LIST,NAME);

		errorMatchers = loadMatchers();
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof CreateShell)
		{
			CreateShell createShell = (CreateShell)msg;
			if(createShell.getShellName().equals(NAME))
			{
				createShell.setShell(new ConsoleShell());
			}
		}
		else if(msg instanceof PropertiesChanged)
		{
			errorMatchers = loadMatchers();
		}
	}

	public static ErrorMatcher[] loadMatchers()
	{
		Vector errorMatchers = new Vector();
		int i = 0;
		String match;
		while((match = jEdit.getProperty("console.error." + i + ".match")) != null)
		{
			String name = jEdit.getProperty("console.error." + i + ".name");
			String filename = jEdit.getProperty("console.error." + i + ".filename");
			String line = jEdit.getProperty("console.error." + i + ".line");
			String message = jEdit.getProperty("console.error." + i + ".message");

			errorMatchers.addElement(new ErrorMatcher(name,match,
				filename,line,message));

			i++;
		}

		ErrorMatcher[] retVal = new ErrorMatcher[errorMatchers.size()];
		errorMatchers.copyInto(retVal);
		return retVal;
	}

	// package-private members
	static void addError(int type, String file, int line, String message)
	{
		errorSource.addError(type,file,line,0,0,message);
	}

	static void clearErrors()
	{
		errorSource.clear();
	}

	static int parseLine(String text)
	{
		for(int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];
			int result = m.match(text);
			if(result != -1)
				return result;
		}

		return -1;
	}

	// private members
	private static DefaultErrorSource errorSource;
	private static ErrorMatcher[] errorMatchers;
}
