/*
 * ErrorMatcher.java - Error pattern matcher
 * Copyright (C) 2000, 2001 Slava Pestov
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

import gnu.regexp.*;
import org.gjt.sp.jedit.*;

class ErrorMatcher
{
	boolean user; // true if not one of the default matchers
	String internalName;
	String name;
	String match;
	String filename;
	String line;
	String message;
	RE regexp;

	ErrorMatcher(boolean user, String internalName, String name, String match,
		String filename, String line, String message)
		throws REException
	{
		this(user,internalName,name,match,filename,line,message,
			new RE(match,RE.REG_ICASE,RESyntax.RE_SYNTAX_PERL5));
	}

	ErrorMatcher(boolean user, String internalName, String name, String match,
		String filename, String line, String message, RE regexp)
	{
		this.user = user;
		this.internalName = internalName;
		this.name = name;
		this.match = match;
		this.filename = filename;
		this.line = line;
		this.message = message;
		this.match = match;
		this.regexp = regexp;

		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < name.length(); i++)
		{
			char ch = name.charAt(i);
			if(Character.isLetterOrDigit(ch))
				buf.append(ch);
		}

		this.internalName = buf.toString();
	}

	ErrorMatcher()
	{
	}

	int match(String text, String directory, DefaultErrorSource errorSource)
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

			String _filename = MiscUtilities.constructPath(
				directory,regexp.substitute(text,filename));
			String _line = regexp.substitute(text,line);
			String _message = regexp.substitute(text,message);

			try
			{
				errorSource.addError(type,_filename,
					Integer.parseInt(_line) - 1,
					0,0,_message);
			}
			catch(NumberFormatException nf)
			{
			}

			return type;
		}

		return -1;
	}

	void save()
	{
		jEdit.setProperty("console.error." + internalName + ".name",name);
		jEdit.setProperty("console.error." + internalName + ".match",match);
		jEdit.setProperty("console.error." + internalName + ".filename",filename);
		jEdit.setProperty("console.error." + internalName + ".line",line);
		jEdit.setProperty("console.error." + internalName + ".message",message);
	}

	public String toString()
	{
		return name;
	}

	public Object clone()
	{
		return new ErrorMatcher(user,internalName,name,match,filename,
			line,message,regexp);
	}
}
