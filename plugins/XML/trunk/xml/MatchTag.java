/*
 * MatchTag.java
 * Copyright (C) 2000 Scott Wyatt, 2001 Andre Kaplan
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import java.util.StringTokenizer;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

public class MatchTag
{
	public static void matchTag(JEditTextArea textArea)
	{
		String text = textArea.getText();
		TagAttribute tag_attr = getSelectedTag(textArea.getCaretPosition(), text);
		if (tag_attr != null && (tag_attr.tag).charAt(0) != '/')
		{
			TagAttribute endtag_attr = null;
			if ((endtag_attr = findEndTag(text, tag_attr.tag, tag_attr.endpos + 1, 0)) != null)
			{
				textArea.setSelection(new Selection.Range(
					endtag_attr.startpos, endtag_attr.endpos
				));
				textArea.moveCaretPosition(endtag_attr.endpos);
			}
			else
				textArea.getToolkit().beep();
		}
		if (tag_attr != null && (tag_attr.tag).charAt(0) == '/')
		{
			TagAttribute starttag_attr = null;
			if ((starttag_attr = findStartTag(text, (tag_attr.tag).substring(1), (tag_attr.startpos - 1), 0)) != null)
			{
				textArea.setSelection(new Selection.Range(
					starttag_attr.startpos, starttag_attr.endpos
				));
				textArea.moveCaretPosition(starttag_attr.endpos);
			}
			else
				textArea.getToolkit().beep();
		}
	}


	public static TagAttribute getSelectedTag(int pos, String text)
	{
		int startpos = -1;
		int endpos   = -1;

		for (int i = pos; i >= 0; --i)
		{
			if (i < pos && text.charAt(i) == '<')
			{
				startpos = i;
				break;
			}
			if (i < (pos - 1) && text.charAt(i) == '>')
			{
				return null;
			}
		}

		if (startpos != -1)
		{
			for (int i = startpos; i < text.length(); ++i)
			{
				if (text.charAt(i) == '>')
				{
					endpos = i;
					break;
				}
				if (i > startpos && text.charAt(i) == '<')
					return null;
			}
		}

		if ((startpos != -1) && (endpos != -1))
		{
			StringTokenizer st1 = new StringTokenizer(
				text.substring(startpos + 1, endpos), "<>"
			);

			if (st1.hasMoreTokens())
			{
				StringTokenizer st2 = new StringTokenizer(st1.nextToken());
				if (st2.hasMoreTokens())
				{
					TagAttribute tag_attr = new TagAttribute();
					tag_attr.startpos = startpos;
					tag_attr.endpos   = endpos;
					tag_attr.tag	  = st2.nextToken();

					return tag_attr;
				}
			}

			return null;
		}

		return null;
	}


	public static TagAttribute findEndTag(String text, String endtag, int offset, int count)
	{
		for (int i = offset; i < text.length(); ++i) 
		{
			String tag = null;
			if ((text.charAt(i)) == '<')
			{
				int index = readForwardUntil(text, i + 1, '>');
				if (index != -1) {
					tag = text.substring(i + 1, index).toLowerCase();
					endtag = endtag.toLowerCase();
					if (tag.startsWith(endtag))
						return findEndTag(text, endtag, (index + 1), ++count);

					if (tag.startsWith("/" + endtag)
					) {
						if(count > 0)
							return findEndTag(text, endtag, (index + 1), --count);
						else
						{
							TagAttribute tagattr = new TagAttribute();
							tagattr.startpos = i;
							tagattr.endpos = (index + 1);
							tagattr.tag = tag;
							return tagattr;
						}
					}
				}
			}
		}
		return null;
	}


	public static TagAttribute findStartTag(String text, String starttag, int offset, int count)
	{
		for (int i = offset; i >= 0; --i)
		{
			String tag = null;
			if ((text.charAt(i)) == '>')
			{
				int index = readBackwardUntil(text, i, '<');
				if (index != -1)
				{
					tag = text.substring((index + 1), i).toLowerCase().trim();
					starttag = starttag.toLowerCase();
					if (tag.startsWith("/" + starttag))
						return findStartTag(text, starttag, (index - 1), ++count);

					if (tag.startsWith(starttag))
					{
						if(count > 0)
							return findStartTag(text, starttag, (index - 1), --count);
						else
						{
							TagAttribute tagattr = new TagAttribute();
							tagattr.startpos = index;
							tagattr.endpos = (i + 1);
							tagattr.tag = tag;
							return tagattr;
						}
					}
				}
			}
		}
		return null;
	}


	private static int readForwardUntil(String text, int offset, char delimiter)
	{
		for(int i = offset; i < text.length(); ++i)
		{
			char value = text.charAt(i);
			if(value == delimiter)
				return i;
		}
		return -1;
	}


	private static int readBackwardUntil(String text, int offset, char delimiter)
	{
		for (int i = offset; i >= 0; --i)
		{
			char value = text.charAt(i);
			if (value == delimiter)
				return i;
		}
		return -1;
	}


	public static class TagAttribute
	{
		int startpos = -1;
		int endpos = -1;
		String tag = null;
	}
}

