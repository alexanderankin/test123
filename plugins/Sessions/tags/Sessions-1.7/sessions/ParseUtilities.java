/*
 * ParseUtilities.java - some XML string parsing utility methods
 * Copyright (c) 2001 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


/**
 * This class contains some static utility methods for dealing
 * with encoding and decoding of XML attribute values.<p>
 *
 * The methods have been ripped off from the Java XML Serialization
 * library <b>JSX</b> (see
 * <a href="http://jsx.sourceforge.net">http://jsx.sourceforge.net</a>),
 * copyright (c) 2001 by Brendan Macmillan, licensed under GNU's GPL 2.
 * The code base of the rip-off has been JSX 0.9.5.0.<p>
 *
 * In order to suit better into the Sessions plugin, I made the
 * following modifications:
 * <ul>
 * <li>slightly different exception handling; exceptions are now visible in
 *     the method signatures.
 * <li>removed references to System.err, used org.gjt.sp.util.Log instead.
 * <li>use a StringBuffer in decodeXML(String) to increase performance.
 * <li>moved from package "JSX" to "sessions".
 * <li>removed some debugging comments, added JavaDoc.
 * </ul>
 *
 * @author Dirk Moebius (<a href="mailto:dmoebius@gmx.net">dmoebius@gmx.net</a>)
 * @author Brendan Macmillan (<a href="mailto:JSX-ideas@yahoogroups.com">JSX-ideas@yahoogroups.com</a>)
 * @see http://jsx.sourceforge.net
 */


package sessions;


import org.gjt.sp.util.Log;


class ParseUtilities
{

	public static int toInt(String in, int defaultValue)
	{
		try
		{
			return Integer.parseInt(in);
		}
		catch(NumberFormatException e)
		{
			Log.log(Log.ERROR, Session.class, "not an integer: " + in + "; using defaultValue " + defaultValue + "; error was: " + e);
			return defaultValue;
		}
	}


	public static long toLong(String in, long defaultValue)
	{
		try
		{
			return Long.parseLong(in);
		}
		catch(NumberFormatException e)
		{
			Log.log(Log.ERROR, Session.class, "not a long: " + in + "; using defaultValue " + defaultValue + "; error was: " + e);
			return defaultValue;
		}
	}


	public static boolean toBoolean(String in, boolean defaultValue)
	{
		try
		{
			return parseBoolean(in);
		}
		catch(IllegalArgumentException e)
		{
			Log.log(Log.ERROR, Session.class, "not a boolean: " + in + "; using defaultValue " + defaultValue + "; error was: " + e);
			return defaultValue;
		}
	}


	public static float toFloat(String in, float defaultValue)
	{
		try
		{
			return parseFloat(in);
		}
		catch(NumberFormatException e)
		{
			Log.log(Log.ERROR, Session.class, "not a float: " + in + "; using defaultValue " + defaultValue + "; error was: " + e);
			return defaultValue;
		}
	}


	public static double toDouble(String in, double defaultValue)
	{
		try
		{
			return parseDouble(in);
		}
		catch(NumberFormatException e)
		{
			Log.log(Log.ERROR, Session.class, "not a double: " + in + "; using defaultValue " + defaultValue + "; error was: " + e);
			return defaultValue;
		}
	}


	public static boolean parseBoolean(String s) throws IllegalArgumentException
	{
		s = s.trim();
		if(s.equalsIgnoreCase("false"))
			return false;
		else if(s.equalsIgnoreCase("true"))
			return true;
		else
			throw new IllegalArgumentException("Boolean must be 'true' or 'false'; found '" + s + "'");
	}


	public static float parseFloat(String s) throws NumberFormatException
	{
		s = s.trim();
		if(s.equals("NaN"))
			return Float.NaN;
		else if(s.equals("Infinity"))
			return Float.POSITIVE_INFINITY;
		else if(s.equals("-Infinity"))
			return Float.NEGATIVE_INFINITY;
		else
			return Float.valueOf(s).floatValue();
	}


	public static double parseDouble(String s) throws NumberFormatException
	{
		s = s.trim();
		if(s.equals("NaN"))
			return Double.NaN;
		else if(s.equals("Infinity"))
			return Double.POSITIVE_INFINITY;
		else if(s.equals("-Infinity"))
			return Double.NEGATIVE_INFINITY;
		else
			return Double.valueOf(s).doubleValue();
	}


	/**
	 * Encode a String so that it can be written out to a XML document
	 * as an attribute.
	 */
	public static String encodeXML(String in)
	{
		StringBuffer out = new StringBuffer(in.length());
		for(int i = 0; i < in.length(); i++)
		{
			char c = in.charAt(i);
			switch (c)
			{
				case '&':  out.append("&amp;"); break;
				case '<':  out.append("&lt;"); break;
				case '>':  out.append("&gt;"); break;
				case '\'': out.append("&apos;"); break;
				case '"':  out.append("&quot;"); break;
				case '\\':
					// need to check for the next character, and encode them together:
					// if there is none, then output "\\" - need to escape for compiler
					// if it is '\', then output "\\\\" - need to escape for compiler
					// if it is 'u', then output "\ \ u" (spaced so compiler not upset)
					// for all other cases, simply output '\' and the character.
					if(i + 1 == in.length())
					{
						// could also write as an Exception handler
						out.append("\\\\"); // escaping: two back-slashes \ \ 
						i++;
					}
					else
					{
						c = in.charAt(i + 1);
						if(c=='\\')
						{
							out.append("\\\\\\\\"); // escaping: four back-slashes \ \ \ \ 
							i++;
						}
						else if(c=='u')
						{
							out.append("\\\\u"); //escaping: two back-slashes \ \ 
							i++;
						}
						else if(c <= 0x1F || (c >= 0x80 && c <= 0x9f))
							out.append("\\\\"); // it *will* be followed by a \u0000
						else
							out.append("\\"); // leave the next char to be encoded as usual
					}
					break; //escape the escape char...

				case '\t':
				case '\n':
				case '\r': out.append(c); break;

				default:
					if(c <= 0x1F || c >= 0x80) //*all* non-ascii
					{
						// Encode this character into something the SAX XMLReader can parse
 						out.append("&#x"+Integer.toHexString(c)+";");
					}
					else
 						out.append(c);
					break;
			}
		}

		return out.toString();
	}


	/**
	 * Decode a string that was retrieved from a XML document, which has
	 * previously been encoded using <code>encodeXML(String)</code>.
	 */
	public static String decodeXML(String in)
	{
		StringBuffer out = new StringBuffer();
		int i = 0;

		try
		{
			for(i = 0; i < in.length(); i++)
			{
				char c = in.charAt(i);
				if(c == '&')
				{
					i++;
					if(in.startsWith("amp;", i))        {out.append('&');  i += 3;}
					else if (in.startsWith("lt;", i))   {out.append('<');  i += 2;}
					else if (in.startsWith("gt;", i))   {out.append('>');  i += 2;}
					else if (in.startsWith("apos;", i)) {out.append('\''); i += 4;}
					else if (in.startsWith("quot;", i)) {out.append('"');  i += 4;}
				}
				else if(c == '\\')
				{
					i++;
					if(in.charAt(i) == 'u')
					{
						i++;
						out.append((char)Integer.parseInt(in.substring(i,i+4),16));
 						i += 3;
					}
					else if(in.charAt(i) == '\\')
						 out.append('\\');
					else
					{
						out.append('\\');
						i--; // unread the last char
					}
				}
				else
					out.append(c);
			}
		}
		catch (StringIndexOutOfBoundsException e)
		{
			Log.log(Log.ERROR, Session.class,
				"Seems to be an incomplete escaped entity or \\unnnn: " + in.substring(i));
		}
		catch (IndexOutOfBoundsException e)
		{
			Log.log(Log.ERROR, Session.class,
				"Seems to be an incomplete escaped entity: " + in.substring(in.lastIndexOf('&')));
		}
		catch (NumberFormatException e)
		{
			Log.log(Log.ERROR, Session.class,
				"Seems to be a faulty \\unnnn escaped control char: " + in.substring(i));
		}

		return out.toString();
	}

}

