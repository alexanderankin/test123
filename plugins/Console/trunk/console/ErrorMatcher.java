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

// {{{ Imports
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import errorlist.DefaultErrorSource.DefaultError;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import console.utils.StringList;

// }}}

public class ErrorMatcher implements Cloneable
{
	// {{{ Instance variables
	public boolean user; // true if not one of the default matchers
	private String internalName;
	public String name;
	public String error;
	public String warning;
	public String extraPattern;
	public String fileBackref;
	public String lineBackref;
	public String messageBackref;
//	String extra, file, line, message;
	public Pattern errorRE;
	public Pattern warningRE;
	public Pattern extraRE;
    public String testText;
	public boolean isValid;
	public StringList errors = null;
	public int type = -1;
	String label;

	public String file, line, message;
	public void clear() {
		file=line=message=null;
		user=false;
		errorRE = null;
		warningRE = null;
		testText = null;
		extraRE = null;
		type = -1;
		internalName = null;
		isValid =false;
		errors = new StringList();
		type = -1;
		label = null;
	}

	// }}}

	public String matchLine(String text)
	{
		
		try {
			label = null;
			Matcher matcher = null;
			if (warningRE != null)
			{
				matcher = warningRE.matcher(text);
				if (matcher.matches())
				{
					label = jEdit.getProperty("options.console.errors.warning");
					type =  ErrorSource.WARNING;				
				}
			}
			if ((label == null) && (errorRE != null))
			{
				matcher = errorRE.matcher(text);
				if (matcher.matches())
				{
					label = jEdit.getProperty("options.console.errors.match");
					type =  ErrorSource.ERROR;
				}
			}
			
			if (label != null) 
			{
				file = matcher.replaceFirst(fileBackref);
				line = matcher.replaceFirst(lineBackref);
				message = matcher.replaceAll(messageBackref);		
				return toLongString();
			}
		}
			catch (RuntimeException  e) {
				StringList logmsg = new StringList();
//				logmsg.add(jEdit.getProperty("console.shell.bad-regex"));
				logmsg.add(label);
				logmsg.add(internalName());
				logmsg.add(e.getMessage());
//				StringList stackTrace = new StringList(e.getStackTrace());
//				logmsg.add("\n" + stackTrace.join("\n  -> "));
				Log.log(Log.WARNING, ErrorMatcher.class, logmsg.join(":"));
			}
		return null;
	}
	
	public StringList findMatches(String text)
	{
		isValid();
		String[] sl = text.split("\n");
		StringList retval = new StringList();
		int i=-1;
		while (i<sl.length-1) 
		{
			String current = sl[++i];
			String ml = matchLine(current);
			if (ml != null && extraRE != null)/* We found a matching line */
			{  /* Check the next lines */
				Matcher m = extraRE.matcher(sl[i+1]);
				while (m.matches()) { 
		  		       ml += " " + m.replaceFirst("$1");
		  		       ++i;
		  		       m = extraRE.matcher(sl[i+1]);
				}
				retval.add(ml);
			}
		}
		
		return retval;
	}

	public String toLongString()
	{
		String retval = "[" + label + "]" + file + ":" + line + ":" + message;
		return retval;
	}

	/** 
	 * @deprecated
	 * @param user
	 * @param internalName
	 * @param name
	 * @param error
	 * @param warning
	 * @param extra
	 * @param filename
	 * @param line
	 * @param message
	 */
	private ErrorMatcher(boolean user, String internalName, String name,
			String error, String warning, String extra, String filename,
			String line, String message)
	{
		this.user = user;
		this.internalName = internalName;
		this.name = name;
		this.error = error;
		this.warning = warning;
		this.extraPattern = extra;
		this.fileBackref = filename;
		this.lineBackref = line;
		this.messageBackref = message;
		isValid();
	}

	/** Copies values from one ErrorMatcher into this */
	
	public void set(ErrorMatcher other) {
		clear();
		user=other.user;
		name = other.name;
		error = other.error;
		warning = other.warning;
		extraPattern =other.extraPattern;
		fileBackref= other.fileBackref;
		lineBackref = other.lineBackref;
		messageBackref = other.messageBackref;
		testText = other.testText;
		isValid();
	}
	
	public Object clone()
	{
		ErrorMatcher retval = new ErrorMatcher();
		retval.set(this);
		return retval;
	} // }}}


    public String internalName() 
    {
    	/* Remove all non-alphanumeric characters */
		final Pattern p = Pattern.compile("\\W");
		if (name == null) return null;
		if (internalName == null)
		{
			Matcher m = p.matcher(name); 
			internalName = m.replaceAll("").toLowerCase();
		}
		return internalName;
   }
	
	public boolean isValid()
	{
		errors = new StringList();
		if (name == null)
		{
			errors.add(jEdit.getProperty("console.not-filled-out.title") +":" +
					           jEdit.getProperty("options.console.errors.name"));
			isValid = false;
			return isValid;
		}
		internalName();

		// errorRE = new RE(error,RE.REG_ICASE,RESearchMatcher.RE_SYNTAX_JEDIT);
		if ((error != null) && (error.length() > 0)) try
		{
			errorRE = Pattern.compile(error, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException pse)
		{
			errors.add(jEdit.getProperty("options.console.errors.match") + pse.getDescription());
		}

		if (warning != null && warning.length() > 0)
		{
			/*
			 * warningRE = new RE(warning,RE.REG_ICASE,
			 * RESearchMatcher.RE_SYNTAX_JEDIT);
			 */
			try
			{
				warningRE = Pattern.compile(warning, Pattern.CASE_INSENSITIVE);
			} catch (PatternSyntaxException pse)
			{
				errors.add(jEdit.getProperty("options.console.errors.warning") + pse.getDescription());
			}

		}
		
		if (extraPattern != null && extraPattern.length() != 0)
		{
			/*
			 * extraRE = new RE(extra,RE.REG_ICASE,
			 * RESearchMatcher.RE_SYNTAX_JEDIT);
			 */
			try
			{
				extraRE = Pattern.compile(extraPattern, Pattern.CASE_INSENSITIVE);
			} catch (PatternSyntaxException pse)
			{
				errors.add(jEdit.getProperty("options.console.errors.extra") + pse.getMessage());
			}
		}
		isValid = (errors.size() == 0);
		return isValid;
	} // }}}

	// {{{ ErrorMatcher constructor
	public ErrorMatcher()
	{
	} // }}}

	public String getErrors()
	{
		if (errors == null)
			return "no errors.";
		if (errors.size() == 0)
			return "no errors.";
		return "Error -  " + errors.join("\n  - ");
	}

	// {{{ match() method
	
	public DefaultErrorSource.DefaultError match(View view, String text,
			String directory, DefaultErrorSource errorSource)
	{
		String t = matchLine(text);
		if (t == null) return null;
		
		String _filename = MiscUtilities.constructPath(directory, file);
		try
		{
			return new DefaultError(errorSource, type,
					_filename, Math.max(0, Integer.parseInt(line) - 1), 0, 0,
					message);
		} 
		catch (NumberFormatException nf)
		{
			return null;
		}
	} // }}}

	
	// {{{ matchExtra() method
	public String matchExtra(String text)
	{
		if (extraRE == null)
			return null;
		Matcher matcher = extraRE.matcher(text);
		if (matcher.matches())
		{
			return matcher.replaceFirst("$1");
		}
		/*
		 * if(extraRE != null && extraRE.isMatch(text)) return
		 * extraRE.substitute(text,"$1");
		 */
		else
			return null;
	} // }}}

	// {{{ save() method
	
	public static ErrorMatcher bring(String internalName)
	{
		ErrorMatcher retval = new ErrorMatcher();
		retval.load(internalName);
		return retval;
	}
	
	/**
	 * Brings the state back from the properties
	 * 
	 * @param inout an ErrorMatcher
	 * @param user
	 * @param the name (which gets translated into an internal name)
	 * @return
	 */
	public void load(String iname) {
		this.name = iname;
		internalName();
		name = jEdit.getProperty("console.error." + internalName
				+ ".name");
		error = jEdit.getProperty("console.error." + internalName
				+ ".match");
		warning = jEdit.getProperty("console.error." + internalName
				+ ".warning");
		extraPattern = jEdit.getProperty("console.error." + internalName
				+ ".extra");
		fileBackref = jEdit.getProperty("console.error." + internalName
				+ ".filename");
		lineBackref = jEdit.getProperty("console.error." + internalName
				+ ".line");
		messageBackref = jEdit.getProperty("console.error." + internalName
				+ ".message");
		testText = jEdit.getProperty("console.error." + internalName
				+ ".testtext");
		if (!isValid() )
			Log.log(Log.ERROR, ErrorMatcher.class,
					"Invalid regexp in matcher " + internalName());
	}
	
	public void save()
	{
		jEdit.setProperty("console.error." + internalName() + ".testtext", testText);
		jEdit.setProperty("console.error." + internalName() + ".name", name);
		jEdit.setProperty("console.error." + internalName + ".match", error);
		jEdit
				.setProperty("console.error." + internalName + ".warning",
						warning);
		jEdit.setProperty("console.error." + internalName + ".extra", extraPattern);
		jEdit.setProperty("console.error." + internalName + ".filename",
				fileBackref);
		jEdit.setProperty("console.error." + internalName + ".line", lineBackref);
		jEdit.setProperty("console.error." + internalName + ".message",
						messageBackref);
	} // }}}

	// {{{ toString() method
	public String toString()
	{
		return name;
	} // }}}

	public DefaultError match0(View view, String text,
			String directory, DefaultErrorSource errorSource)
	{
		int type = 0;
		Pattern re = null;
		Matcher matcher = null;
		if (warningRE != null)
		{
			matcher = warningRE.matcher(text);
			if (matcher.matches())
			{
				re = warningRE;
				type = ErrorSource.WARNING;
			}
		}
		if (errorRE != null)
		{
			matcher = errorRE.matcher(text);
			if (matcher.matches())
			{
				re = errorRE;
				type = ErrorSource.ERROR;
			}
		}
		if (re == null)
			return null;

		String _filename;
		if (fileBackref.equals("$f"))
			_filename = view.getBuffer().getPath();
		else
		{
			_filename = matcher.replaceAll(fileBackref);
			/*
			 * _filename = MiscUtilities.constructPath(directory,
			 * re.substitute(text,filename));
			 */
			_filename = MiscUtilities.constructPath(directory, _filename);
		}
		String _line = matcher.replaceAll(lineBackref);
		String _message = matcher.replaceAll(messageBackref);
		try
		{
			return new DefaultError(errorSource, type,
					_filename, Math.max(0, Integer.parseInt(_line) - 1), 0, 0,
					_message);
		} catch (NumberFormatException nf)
		{
			return null;
		}
	} // }}}


}
