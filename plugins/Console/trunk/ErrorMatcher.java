/*
 * ErrorMatcher.java - Error pattern matcher
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
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

public class ErrorMatcher
{
	RE regexp;
	public String name;
	public String filename;
	public String match;
	public String line;
	public String message;

	public ErrorMatcher(String name, String match, String filename,
		String line, String message)
	{
		this.name = name;
		this.match = match;
		this.filename = filename;
		this.line = line;
		this.message = message;
	}

	public int match(String text)
	{
		if(regexp == null)
		{
			try
			{
				regexp = new RE(match,RE.REG_ICASE,RESyntax.RE_SYNTAX_PERL5);
			}
			catch(REException re)
			{
				Log.log(Log.ERROR,this,"Invalid regexp: " + match);
				Log.log(Log.ERROR,this,re);

				return -1;
			}
		}

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

			ConsoleShellPluginPart.addError(type,_filename,
				Integer.parseInt(_line) - 1,_message);

			return type;
		}
		else
			return -1;
	}

	public String toString()
	{
		return name;
	}
}
