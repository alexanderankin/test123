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

import gnu.regexp.*;
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

		errorMatchers = new Vector();
		int i = 0;
		String match;
		while((match = jEdit.getProperty("console.error." + i + ".match")) != null)
		{
			String filename = jEdit.getProperty("console.error." + i + ".filename");
			String line = jEdit.getProperty("console.error." + i + ".line");
			String message = jEdit.getProperty("console.error." + i + ".message");

			try
			{
				errorMatchers.addElement(new ErrorMatcher(match,
					filename,line,message));
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR,ConsoleShellPluginPart.class,
					"Invalid regexp: " + match);
				Log.log(Log.ERROR,ConsoleShellPluginPart.class,e);
			}

			i++;
		}
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
	}

	// package-private members
	static void clearErrors()
	{
		errorSource.clear();
	}

	static int parseLine(String text)
	{
		for(int i = 0; i < errorMatchers.size(); i++)
		{
			ErrorMatcher m = (ErrorMatcher)errorMatchers.elementAt(i);
			int result = m.match(text);
			if(result != -1)
				return result;
		}

		return -1;
	}

	// private members
	private static DefaultErrorSource errorSource;
	private static Vector errorMatchers;

	private static class ErrorMatcher
	{
		RE regexp;
		String filename;
		String line;
		String message;

		public ErrorMatcher(String match, String filename,
			String line, String message) throws REException
		{
			regexp = new RE(match,RE.REG_ICASE,RESyntax.RE_SYNTAX_PERL5);
			this.filename = filename;
			this.line = line;
			this.message = message;
		}

		public int match(String text)
		{
			if(regexp.isMatch(text))
			{
				int type;
				String loText = text.toLowerCase();
				if(loText.indexOf("warning") != -1 ||
					loText.indexOf("caution") != -1)
					type = ErrorSource.WARNING;
				else
					type = ErrorSource.ERROR;

				String _filename = regexp.substitute(text,filename);
				String _line = regexp.substitute(text,line);
				String _message = regexp.substitute(text,message);

				errorSource.addError(type,_filename,
					Integer.parseInt(_line) - 1,0,0,
					_message);

				return type;
			}
			else
				return -1;
		}
	}
}
