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
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.jedit.*;
import errorlist.*;

class ErrorMatcher implements Cloneable
{
	boolean user; // true if not one of the default matchers
	String internalName;
	String name;
	String match;
	String warning;
	String extra;
	String filename;
	String line;
	String message;
	RE matchRE;
	RE warningRE;
	RE extraRE;

	ErrorMatcher(boolean user, String internalName, String name, String match,
		String warning, String extra, String filename, String line,
		String message)
		throws REException
	{
		this.user = user;
		this.internalName = internalName;
		this.name = name;
		this.match = match;
		this.warning = warning;
		this.extra = extra;
		this.filename = filename;
		this.line = line;
		this.message = message;
		this.match = match;

		matchRE = new RE(match,RE.REG_ICASE,RESearchMatcher.RE_SYNTAX_JEDIT);

		if(warning != null && warning.length() != 0)
		{
			warningRE = new RE(warning,RE.REG_ICASE,
				RESearchMatcher.RE_SYNTAX_JEDIT);
		}

		if(extra != null && extra.length() != 0)
		{
			extraRE = new RE(extra,RE.REG_ICASE,
				RESearchMatcher.RE_SYNTAX_JEDIT);
		}
	}

	ErrorMatcher()
	{
	}

	DefaultErrorSource.DefaultError match(String text, String directory,
			DefaultErrorSource errorSource)
	{
		if(matchRE.isMatch(text))
		{
			int type;
			RE displayRE = matchRE;
			if(warningRE != null && warningRE.isMatch(text))
			{
				type = ErrorSource.WARNING;
				displayRE = warningRE;
			}
			else
				type = ErrorSource.ERROR;

			String _filename = MiscUtilities.constructPath(
				directory,displayRE.substitute(text,filename));
			String _line = displayRE.substitute(text,line);
			String _message = displayRE.substitute(text,message);

			try
			{
				return new DefaultErrorSource.DefaultError(
					errorSource,type,_filename,
					Integer.parseInt(_line) - 1,
					0,0,_message);
			}
			catch(NumberFormatException nf)
			{
			}
		}

		return null;
	}

	String matchExtra(String text, String directory,
		DefaultErrorSource errorSource)
	{
		if(extraRE != null && extraRE.isMatch(text))
			return extraRE.substitute(text,"$1");
		else
			return null;
	}

	void save()
	{
		jEdit.setProperty("console.error." + internalName + ".name",name);
		jEdit.setProperty("console.error." + internalName + ".match",match);
		jEdit.setProperty("console.error." + internalName + ".warning",warning);
		jEdit.setProperty("console.error." + internalName + ".extra",extra);
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
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			// can't happen
			throw new InternalError();
		}
	}
}
