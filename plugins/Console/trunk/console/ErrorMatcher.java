/*
 * ErrorMatcher.java - Error pattern matcher
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

//{{{ Imports
import gnu.regexp.*;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.jedit.*;
import errorlist.*;
//}}}

public class ErrorMatcher implements Cloneable
{
	//{{{ Instance variables
	public boolean user; // true if not one of the default matchers
	public String internalName;
	public String name;
	public String error;
	public String warning;
	public String extra;
	public String filename;
	public String line;
	public String message;
	public RE errorRE;
	public RE warningRE;
	public RE extraRE;
	//}}}

	//{{{ ErrorMatcher constructor
	public ErrorMatcher(boolean user, String internalName, String name,
		String error, String warning, String extra, String filename,
		String line, String message) throws REException
	{
		this.user = user;
		this.internalName = internalName;
		this.name = name;
		this.error = error;
		this.warning = warning;
		this.extra = extra;
		this.filename = filename;
		this.line = line;
		this.message = message;

		errorRE = new RE(error,RE.REG_ICASE,RESearchMatcher.RE_SYNTAX_JEDIT);

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
	} //}}}

	//{{{ ErrorMatcher constructor
	public ErrorMatcher()
	{
	} //}}}

	//{{{ match() method
	public DefaultErrorSource.DefaultError match(String text, String directory,
			DefaultErrorSource errorSource)
	{
		if(warningRE != null && warningRE.isMatch(text))
		{
			String _filename = MiscUtilities.constructPath(
				directory,warningRE.substitute(text,filename));
			String _line = warningRE.substitute(text,line);
			String _message = warningRE.substitute(text,message);

			try
			{
				return new DefaultErrorSource.DefaultError(
					errorSource,ErrorSource.WARNING,_filename,
					Math.max(0,Integer.parseInt(_line) - 1),
					0,0,_message);
			}
			catch(NumberFormatException nf)
			{
			}
		}
		else if(errorRE.isMatch(text))
		{
			String _filename = MiscUtilities.constructPath(
				directory,errorRE.substitute(text,filename));
			String _line = errorRE.substitute(text,line);
			String _message = errorRE.substitute(text,message);

			try
			{
				return new DefaultErrorSource.DefaultError(
					errorSource,ErrorSource.ERROR,_filename,
					Math.max(0,Integer.parseInt(_line) - 1),
					0,0,_message);
			}
			catch(NumberFormatException nf)
			{
			}
		}

		return null;
	} //}}}

	//{{{ matchExtra() method
	public String matchExtra(String text, String directory,
		DefaultErrorSource errorSource)
	{
		if(extraRE != null && extraRE.isMatch(text))
			return extraRE.substitute(text,"$1");
		else
			return null;
	} //}}}

	//{{{ save() method
	public void save()
	{
		jEdit.setProperty("console.error." + internalName + ".name",name);
		jEdit.setProperty("console.error." + internalName + ".match",error);
		jEdit.setProperty("console.error." + internalName + ".warning",warning);
		jEdit.setProperty("console.error." + internalName + ".extra",extra);
		jEdit.setProperty("console.error." + internalName + ".filename",filename);
		jEdit.setProperty("console.error." + internalName + ".line",line);
		jEdit.setProperty("console.error." + internalName + ".message",message);
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return name;
	} //}}}

	//{{{ clone() method
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
	} //}}}
}
