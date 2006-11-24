/**
 * SqlParser.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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
package sql;

import java.util.*;
import java.util.regex.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class SqlParser
{
	protected Pattern pattern;
	protected final static Pattern whiteSpace = Pattern.compile("[\\s]+");


	/**
	 *  Constructor for the SqlParser object
	 *
	 * @param  delimiterRegex  Description of Parameter
	 * @since
	 */
	public SqlParser(String delimiterRegex)
	{
		pattern = delimiterRegex == null ? null : Pattern.compile(delimiterRegex);
	}


	/**
	 *  Gets the Fragments attribute of the SqlParser object
	 *
	 * @param  sqlText  Description of Parameter
	 * @return          The Fragments value
	 */
	public List getFragments(String sqlText)
	{
		final List list = new ArrayList();
		if (pattern == null)
		{
			list.add(new SqlTextFragment(0, sqlText.length()));
			return list;
		}
		final Matcher stmtDelimMatcher = pattern.matcher(sqlText);

		int stmtStart = 0;
		int stmtEnd = -1;
		while (stmtDelimMatcher.find())
		{
			stmtEnd = stmtDelimMatcher.start();
			if (stmtEnd > stmtStart)
			{
				final SqlTextFragment text = new SqlTextFragment(stmtStart, stmtEnd);
				if (correctWhiteSpace(sqlText, text))
					list.add(text);
			}
			stmtStart = stmtDelimMatcher.end();
		}
		stmtEnd = sqlText.length();
		if (stmtEnd > stmtStart)
		{
			final SqlTextFragment text = new SqlTextFragment(stmtStart, stmtEnd);
			if (correctWhiteSpace(sqlText, text))
				list.add(text);
		}
		return list;
	}


	/**
	 *Constructor for the correctWhiteSpace object
	 *
	 * @param  sqlText  Description of Parameter
	 * @param  text     Description of Parameter
	 * @return          true if there is non-space string
	 */
	protected boolean correctWhiteSpace(String sqlText, SqlTextFragment text)
	{
		final int fragmentLength = text.getLength();
		final Matcher wsMatcherRel = whiteSpace.matcher(text.getFragment(sqlText));
		if (wsMatcherRel.find())
		{
			// if first whitespace is at start - skip it!
			if (wsMatcherRel.start() == 0)
				text.startOffset += wsMatcherRel.end();
			int lastStartRel = 0;
			int lastEndRel = 0;
			do
			{
				// skip all till last one!
				lastStartRel = wsMatcherRel.start();
				lastEndRel = wsMatcherRel.end();
			} while (wsMatcherRel.find());
			if (lastEndRel == fragmentLength && lastEndRel != text.startOffset)
				text.endOffset -= (fragmentLength - lastStartRel);
		}
		return text.endOffset > text.startOffset;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  args  Description of Parameter
	 */
	public static void main(String args[])
	{
		String pattern;
		pattern = "([\\n\\r]+;)+[\\n\\r]+";
		//final String text = "  \n\nqwe\n\n;\n\n";
		String text;

		text = "aa;bb";
		test(pattern, text);
		text = "aa\n;\nbb";
		test(pattern, text);
		text = "  aa\n;\nbb  ";
		test(pattern, text);
		text = "aa  \r\n;\n\r  bb";
		test(pattern, text);
		text = " aa  bb cc \n;\n  dd; ee; ff;\n\n ";
		test(pattern, text);
		text = "  aa\n;\nbb\n;\ncc  ";
		test(pattern, text);
		text = "\n;\r  aa\n;\nbb\n;\ncc  \r;\n ";
		test(pattern, text);
		text = "\n;\r";
		test(pattern, text);
		text = "  ";
		test(pattern, text);
		text = "\n;\r  aa\n;\nbb\n;\n;\ncc  \r;\n ";
		test(pattern, text);

		String s1 = "select sysdate from dual1\n" +
		            "/\n" +
		            "select sysdate from dual2\n" +
		            "/\n" +
		            "select sysdate from dual3/\n" +
		            "select sysdate from dual4;\n" +
		            "select sysdate from dual5\n" +
		            ";\n" +
		            "select sysdate from dual6\n";
		String s2 = "(?m)(?i)([\\s]*[/;])+[\\s]*$";
		test(s2, s1);

		s1 = "select getdate()\n" +
		     "go\n" +
		     "select getdate()\n";
		s2 = "(?i)([\\n\\r]+go)+[\\n\\r]+";
		test(s2, s1);

	}


	/**
	 *  A unit test for JUnit
	 *
	 * @param  pattern  Description of Parameter
	 * @param  text     Description of Parameter
	 */
	protected static void test(String pattern, String text)
	{
//    System.out.println( "pattern: [" + pattern + "]" );
		System.out.println("text: [" + text + "]");

		/*
		 *  final String split[] = text.split( pattern );
		 *  System.out.println( "split:" );
		 *  for ( int i = 0; i < split.length; i++ )
		 *  System.out.println( "" + i + ":[" + split[i] + "]" );
		 *  System.out.println( "split done" );
		 */
		final SqlParser p = new SqlParser(pattern);
		final List frags = p.getFragments(text);
		System.out.println("************ Result: **************");
		for (Iterator i = frags.iterator(); i.hasNext();)
		{
			final SqlTextFragment txt = (SqlTextFragment) i.next();
			System.out.println("start:" + txt.startOffset + "->end:" + txt.endOffset);
			System.out.println("s:[" + txt.getFragment(text) + "]");
		}
		System.out.println("***********************************");
	}


	public static class SqlTextFragment
	{
		public int startOffset;
		public int endOffset;


		/**
		 *Constructor for the SqlStatementText object
		 *
		 * @param  startOffset  Description of Parameter
		 * @param  endOffset    Description of Parameter
		 */
		public SqlTextFragment(int startOffset, int endOffset)
		{
			this.startOffset = startOffset;
			this.endOffset = endOffset;
		}


		public String getFragment(String text)
		{
			return text.substring(startOffset, endOffset);
		}


		public int getLength()
		{
			return endOffset - startOffset;
		}
	}
}

