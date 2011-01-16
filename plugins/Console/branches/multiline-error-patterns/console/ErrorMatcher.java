/*
 * ErrorMatcher.java - Error pattern matcher
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001 Slava Pestov
 * Copyright (c) 2005 Alan Ezust
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
import org.gjt.sp.util.StringList;


// }}}
// {{{ ErrorMatcher class
/**
 * ErrorMatcher - a model which represents error/warning regular
 * expressions for one particular program.
 *
 * @version $Id$
 */
public class ErrorMatcher implements Cloneable
{

	// {{{ public Instance variables
	/** may contain spaces and is used as a text label. */
	public String name;

	public String error;

	public String warning;

	public boolean user;

	public String extraPattern;

	public String fileBackref;

	public String lineBackref;

	public String startCharBackref;

	public int startOffset;

	public String endCharBackref;

	public int endOffset;

	public String messageBackref;

	public StringList errors = null;

	public Pattern errorRE;

	public Pattern warningRE;

	public Pattern extraRE;

	public String testText;

	// }}}

	// {{{ Non-public instance variables
	/* Must not contain funny characters or spaces since it is used as the property path.
	 * Also used for the map key in the ErrorListModel.
	 */
	private String internalName;

	private boolean isValid;

	private int type = -1;

	private String label;

	private String file, line, message, startChar, endChar;

	private boolean hitEnd;

	// }}}

	// {{{ clear()
	public void clear()
	{
		file = line = message = null;
		user = false;
		errorRE = null;
		warningRE = null;
		testText = null;
		extraRE = null;
		type = -1;
		internalName = null;
		isValid = false;
		errors = new StringList();
		type = -1;
		label = null;
		hitEnd = false;
	}

	// }}}

	public boolean hitEnd()
	{
		return hitEnd;
	}
	
	// {{{ matchLine()
	public String matchLine(CharSequence text)
	{
		try
		{
			label = null;
			hitEnd = false;
			Matcher matcher = null;
			if (warningRE != null)
			{
				matcher = warningRE.matcher(text);
				if (matcher.matches())
				{
					label = jEdit.getProperty("options.console.errors.warning");
					type = ErrorSource.WARNING;
					hitEnd = false;
				}
				else if(matcher.hitEnd())
				{
					hitEnd = true;
				}
			}
			if ((label == null) && (errorRE != null))
			{
				matcher = errorRE.matcher(text);
				// why is it find() for error and matches for warning ?
				// https://sourceforge.net/tracker/index.php?func=detail&aid=1764455&group_id=588&atid=300588
				// Shlomy explains that find is better than matches because it will highlight a portion
				// why is it not reproduced for warnings also ?
				// trying lookingAt() because find makes hitEnd() always true when not found
				if (matcher.lookingAt())
				{
					label = jEdit.getProperty("options.console.errors.match");
					type = ErrorSource.ERROR;
					hitEnd = false;
				}
				else if(matcher.hitEnd())
				{
					hitEnd = true;
				}
			}

			if (label != null)
			{
				matcher.reset(matcher.group());
				file = matcher.replaceFirst(fileBackref);
				line = matcher.replaceFirst(lineBackref);
				startChar = matcher.replaceFirst(startCharBackref);
				endChar = matcher.replaceFirst(endCharBackref);
				message = matcher.replaceAll(messageBackref);
				return toLongString();
			}
		}
		catch (RuntimeException e)
		{
			StringList logmsg = new StringList();
			// logmsg.add(jEdit.getProperty("console.shell.bad-regex"));
			logmsg.add(label);
			logmsg.add(internalName());
			logmsg.add(e.getMessage());
			e.printStackTrace();
			Log.log(Log.WARNING, ErrorMatcher.class, logmsg.join(":"));
		}
		return null;
	}

	// }}}


	// {{{ findMatches()
	/** used in ErrorMatchePanel only */
	public StringList findMatches(String text)
	{
		isValid();
		String[] sl = text.split("\n");
		StringList retval = new StringList();
		int i = -1;
		while (i + 1< sl.length )
		{
			String current = sl[++i];
			StringBuffer mlb = new StringBuffer();
			String ml = matchLine(current);
			if (ml != null) /* We found a match for the first line */
			{
				mlb.append(ml);
				if (  extraRE != null )
				{
					while ( i+1 < sl.length)
					{
						// CommandOutputParser first tries to match
						// the following line as a new error and
						// see if there are extra lines to match
						// otherwise
						if(matchLine(sl[i+1]) != null) break;
						Matcher m = extraRE.matcher(sl[i + 1]);
						if (!m.matches()) break;
						try {
							String extra = m.replaceFirst("$1");
							mlb.append("\n  " + extra);
						}
						catch (Exception e) {
							mlb.append("\n  " + e.getMessage());
						}
						++i;
					}
				}
				retval.add(mlb.toString());
			}
			else if(hitEnd())
			{
				for(int j=i;hitEnd() && j+1<sl.length;){
					current += "\n" + sl[++j];
					ml = matchLine(current);
					if (ml != null) /* We found a match for some lines */
					{
						mlb.append(ml);
						i = j;
						if (  extraRE != null )
						{
							while ( i+1 < sl.length)
							{
								// CommandOutputParser first tries to match
								// the following line as a new error and
								// see if there are extra lines to match
								// otherwise
								if(matchLine(sl[i+1]) != null) break;
								Matcher m = extraRE.matcher(sl[i + 1]);
								if (!m.matches()) break;
								try {
									String extra = m.replaceFirst("$1");
									mlb.append("\n  " + extra);
								}
								catch (Exception e) {
									mlb.append("\n  " + e.getMessage());
								}
								++i;
							}
						}
						retval.add(mlb.toString());
						break;
					}
				}
			}
		}
		return retval;
	}

	// }}}

	// {{{ toLongString()
	public String toLongString()
	{
		String retval = "[" + label + "]" + file + ":" + line + ":" + startChar + ":" + endChar + ":" + message;
		return retval;
	} // }}}

	// {{{ set()
	/** Copies values from one ErrorMatcher into this */
	public void set(ErrorMatcher other)
	{
		clear();
		user = other.user;
		name = other.name;
		error = other.error;
		warning = other.warning;
		extraPattern = other.extraPattern;
		fileBackref = other.fileBackref;
		lineBackref = other.lineBackref;
		startCharBackref = other.startCharBackref;
		startOffset = other.startOffset;
		endCharBackref = other.endCharBackref;
		endOffset = other.endOffset;
		messageBackref = other.messageBackref;
		testText = other.testText;
		isValid();
	}

	// }}}

	// {{{ clone()
	public Object clone()
	{
		ErrorMatcher retval = new ErrorMatcher();
		retval.set(this);
		return retval;
	}

	// }}}

	// {{{ internalName()
	public String internalName()
	{
		if ((internalName == null) && (name != null))
		{
			internalName = internalName(name);
		}
		return internalName;
	}

	// }}}

	// {{{ static internalName(String)
	public static String internalName(String name)
	{
		/* Remove all non-alphanumeric characters */
		final Pattern p = Pattern.compile("\\W");
		if (name == null)
			return null;
		String retval = null;
		Matcher m = p.matcher(name);
		retval = m.replaceAll("").toLowerCase();
		return retval;
	}

	// }}}

	// {{{ isValid()
	/**
	 * TODO: also check that any referred to group number
	 *     (from the filename/linenumber/error message), as well as
	 *     the extralines, actually exists in the  regex.
	 */
	public boolean isValid()
	{
		errors = new StringList();
		if (name == null)
		{
			errors.add(jEdit.getProperty("console.not-filled-out.title") + ":"
				+ jEdit.getProperty("options.console.errors.name"));
			isValid = false;
			return isValid;
		}
		internalName();

		if ((error != null) && (error.length() > 0))
			try
			{
				errorRE = Pattern.compile(error, Pattern.CASE_INSENSITIVE);
			}
			catch (PatternSyntaxException pse)
			{
				errors.add(jEdit.getProperty("options.console.errors.match")
					+ pse.getDescription());
			}

		if (warning != null && warning.length() > 0)
		{
			try
			{
				warningRE = Pattern.compile(warning, Pattern.CASE_INSENSITIVE);
			}
			catch (PatternSyntaxException pse)
			{
				errors.add(jEdit.getProperty("options.console.errors.warning")
					+ pse.getDescription());
			}

		}

		if (extraPattern != null && extraPattern.length() != 0)
		{
			try
			{
				extraRE = Pattern.compile(extraPattern, Pattern.CASE_INSENSITIVE);
			}
			catch (PatternSyntaxException pse)
			{
				errors.add(jEdit.getProperty("options.console.errors.extra")
					+ pse.getMessage());
			}
		}
		isValid = (errors.size() == 0);
		return isValid;
	}

	// }}}

	// {{{ ErrorMatcher constructor
	private ErrorMatcher()
	{
	}

	// }}}

	/**
	 * Loads the state of the ErrorMatcher from jEdit properties
	 * @param internalName - a name without spaces or funny chars
	 *       that corresponds to this error pattern. Used as a hash key.
	 */
	public ErrorMatcher (String internalName) {
		load(internalName);
	}
	// {{{ getErrors()
	public String getErrors()
	{
		if (errors == null)
			return "no errors.";
		if (errors.size() == 0)
			return "no errors.";
		return "Error -  " + errors.join("\n  - ");
	}

	// }}}

	// {{{ match() method
	public DefaultErrorSource.DefaultError match(View view, CharSequence text, String directory,
		ErrorSource errorSource)
	{
		String t = matchLine(text);
		if (t == null)
			return null;

		String _filename = MiscUtilities.constructPath(directory, file);

		int startc = 0, endc = 0;
		if(!"".equals(startChar))
		{
			try
			{startc = Math.max(0,Integer.parseInt(startChar)+startOffset);}
			catch(NumberFormatException nf){}
		}
		if(!"".equals(endChar))
		{
			try
			{endc = Math.max(0,Integer.parseInt(endChar)+endOffset);}
			catch(NumberFormatException nf){}
		}

		try
		{
			return new DefaultError(errorSource, type, _filename,
				Math.max(0, Integer.parseInt(line) - 1),
				startc,
				endc,
				message);
		}
		catch (NumberFormatException nf)
		{
			return null;
		}
	} // }}}

	// {{{ matchExtra() method
	public String matchExtra(CharSequence text)
	{
		if (extraRE == null)
			return null;
		Matcher matcher = extraRE.matcher(text);
		if (matcher.matches())
		{
			return matcher.replaceFirst("$1");
		}
		else
			return null;
	}

	// }}}

	// {{{ load()
	/**
	 * Brings the state back from the properties.
	 *
	 * @param iname the jedit action name (gets translated into an internal name)
	 *
	 */
	public void load(String iname)
	{
		internalName = internalName(iname);
		name = jEdit.getProperty("console.error." + internalName + ".name");
		error = jEdit.getProperty("console.error." + internalName + ".match");
		warning = jEdit.getProperty("console.error." + internalName + ".warning");
		extraPattern = jEdit.getProperty("console.error." + internalName + ".extra");
		fileBackref = jEdit.getProperty("console.error." + internalName + ".filename");
		lineBackref = jEdit.getProperty("console.error." + internalName + ".line");
		startCharBackref = jEdit.getProperty("console.error." + internalName + ".startchar","");
		startOffset = jEdit.getIntegerProperty("console.error." + internalName + ".startoffset",0);
		endCharBackref = jEdit.getProperty("console.error." + internalName + ".endchar","");
		endOffset = jEdit.getIntegerProperty("console.error." + internalName + ".endoffset",0);
		messageBackref = jEdit.getProperty("console.error." + internalName + ".message");
		testText = jEdit.getProperty("console.error." + internalName + ".testtext",
			"\n\n\n\n\n");
		if (!isValid())
			Log.log(Log.ERROR, ErrorMatcher.class, "Invalid regexp in matcher "
				+ internalName());
	}

	// }}}

	// {{{ save()
	public void save()
	{
		jEdit.setProperty("console.error." + internalName() + ".testtext", testText);
		jEdit.setProperty("console.error." + internalName() + ".name", name);
		jEdit.setProperty("console.error." + internalName + ".match", error);
		jEdit.setProperty("console.error." + internalName + ".warning", warning);
		jEdit.setProperty("console.error." + internalName + ".extra", extraPattern);
		jEdit.setProperty("console.error." + internalName + ".filename", fileBackref);
		jEdit.setProperty("console.error." + internalName + ".line", lineBackref);
		jEdit.setProperty("console.error." + internalName + ".startchar", startCharBackref);
		jEdit.setIntegerProperty("console.error." + internalName + ".startoffset", startOffset);
		jEdit.setProperty("console.error." + internalName + ".endchar", endCharBackref);
		jEdit.setIntegerProperty("console.error." + internalName + ".endoffset", endOffset);
		jEdit.setProperty("console.error." + internalName + ".message", messageBackref);
	}
	// }}}

	// {{{ toString() method
	public String toString()
	{
		return name;
	} // }}}


} // }}}
