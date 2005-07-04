/*
 * TagParser.java - a few very simple but fast methods used for tag highlighting
 * and such
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000 Scott Wyatt, 2001 Andre Kaplan
 * Portions copyright (C) 2001, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.parser;

//{{{ Imports
import java.util.*;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import xml.completion.ElementDecl;
import xml.XmlParsedData;
//}}}

public class TagParser
{
	public static final int T_STANDALONE_TAG = 0;
	public static final int T_START_TAG = 1;
	public static final int T_END_TAG = 2;

	//{{{ getTagAtOffset() method
	public static Tag getTagAtOffset(String text, int pos)
	{
		if(pos < 0 || pos > text.length())
			return null;

		// Get the last '<' before current position.
		int startTag = text.lastIndexOf('<', pos - 1);
		if(startTag == -1 || startTag + 2 >= text.length()) // at least 2 chars after '<'
			return null;

		int endTag = text.indexOf('>', startTag + 1) + 1;
		if(endTag == 0 || endTag < pos)
			return null;

		int tagType = T_START_TAG;
		int startTagName = startTag + 1;
		if(text.charAt(startTagName) == '/')
		{
			tagType = T_END_TAG;
			++startTagName;
		}
		else if(text.charAt(startTagName) == '?'
			|| text.charAt(startTagName) == '!'
			|| text.charAt(startTagName) == '%')
		{
			return null;
		}

		int endTagName = endTag - 1;
		for(int i = startTagName; i < endTag - 1; i++)
		{
			char ch = text.charAt(i);
			if(Character.isWhitespace(ch))
			{
				if(endTagName == endTag - 1)
					endTagName = i;
			}
			else if(ch == '<')
				return null;
			else if(ch == '/' && i == endTag - 2)
			{
				if(endTagName == endTag - 1)
					endTagName = i;
				tagType = T_STANDALONE_TAG;
			}
		}

		Tag tag = new Tag(startTag,endTag);
		tag.tag   = text.substring(startTagName, endTagName);
		tag.type  = tagType;

		return tag;
	} //}}}

	//{{{ getMatchingTag() method
	public static Tag getMatchingTag(String text, Tag tag)
	{
		if (tag.type == T_START_TAG)
			return findEndTag(text, tag);
		else if (tag.type == T_END_TAG)
			return findStartTag(text, tag);
		return null;
	} //}}}

	//{{{ findLastOpenTag() method
	public static Tag findLastOpenTag(String text, int pos,
		XmlParsedData data)
	{
		Stack tagStack = new Stack();

		// some voodoo to skip inline scripts and comments and such...
		// basically we do not look for stuff that looks like a tag
		// between <X ... X> where X is a non-alphanumeric character.
		// this works fine for JSP, PHP, and maybe others.
		boolean notATag = false;

loop:		for (int i = Math.min(text.length() - 1,pos); i >= 0; i--)
		{
			char ch = text.charAt(i);
			if(ch == '<')
			{
				if(i != text.length() - 1)
				{
					char ch2 = text.charAt(i + 1);
					if(ch2 != '/' && ch2 != '\'' && ch2 != '"'
						&& !Character.isWhitespace(ch2)
						&& !Character.isLetterOrDigit(ch2))
					{
						if(notATag)
							notATag = false;
						//else
						//	return null;
					}
				}
			}
			else if(ch == '>')
			{
				if(notATag)
					continue;

				if(i != 0)
				{
					char ch2 = text.charAt(i - 1);
					if(ch2 != '/' && ch2 != '\'' && ch2 != '"'
						&& !Character.isWhitespace(ch2)
						&& !Character.isLetterOrDigit(ch2))
					{
						notATag = true;
						continue;
					}
				}

				Tag tag = getTagAtOffset(text,i + 1);

				if (tag == null)
					continue;
				else
				{
					String tagName = (data.html
						? tag.tag.toLowerCase()
						: tag.tag);

					ElementDecl decl = data.getElementDecl(tag.tag);
					if(tag.type == T_STANDALONE_TAG
						|| (decl != null && decl.empty))
					{
						continue;
					}
					else if(tag.type == T_START_TAG)
					{
						if(tagStack.empty())
							return tag;

						int unwindIndex = -1;

						for(int j = tagStack.size() - 1;
							j >= 0; j--)
						{
							if(tagStack.get(j).equals(tagName))
							{
								unwindIndex = j;
								break;
							}
						}

						if(unwindIndex == -1)
							return tag;
						else
						{
							while(unwindIndex != tagStack.size())
								tagStack.remove(unwindIndex);
						}
					}
					else if(tag.type == T_END_TAG)
					{
						tagStack.push(tagName);
					}
				}
			}
		}

		return null;
	} //}}}

	//{{{ isInsideTag() method
	public static boolean isInsideTag(String text, int pos)
	{
		int start = text.lastIndexOf('<',pos);
		int end = text.lastIndexOf('>',pos);

		if(start > -1) {
			if(end == -1)
				return true;
			else if(end < start)
				return true;
			else
				return false;
		}
		else if(end > -1)
			return true;
		else
			return false;
	} //}}}

	//{{{ Private members

	//{{{ findEndTag() method
	private static Tag findEndTag(String text, Tag startTag)
	{
		int tagCounter = 0;

		// some voodoo to skip inline scripts and comments and such...
		// basically we do not look for stuff that looks like a tag
		// between <X ... X> where X is a non-alphanumeric character.
		// this works fine for JSP, PHP, and maybe others.
		boolean notATag = false;

loop:		for (int i = startTag.end; i < text.length(); i++)
		{
			char ch = text.charAt(i);
			if(ch == '<')
			{
				if(notATag)
					continue;

				if(i != text.length() - 1)
				{
					char ch2 = text.charAt(i + 1);
					if(ch2 != '/'
						&& !Character.isWhitespace(ch2)
						&& !Character.isLetterOrDigit(ch2))
					{
						notATag = true;
						continue;
					}
				}

				Tag tag = getTagAtOffset(text,i + 1);
				if (tag == null)
					continue;
				else if(tag.tag.equals(startTag.tag))
				{
					if(tag.type == T_END_TAG)
					{
						if(tagCounter == 0)
							return tag;
						else
							tagCounter--;
					}
					else if(tag.type == T_START_TAG)
					{
						tagCounter++;
					}
				}

				i = tag.end - 1;
			}
			else if(ch == '>')
			{
				if(i != 0)
				{
					char ch2 = text.charAt(i - 1);
					if(ch2 != '"' && ch2 != '\''
						&& !Character.isWhitespace(ch2)
						&& !Character.isLetterOrDigit(ch2))
					{
						if(notATag)
							notATag = false;
						//else
						//	return null;
					}
				}
			}
		}

		return null;
	} //}}}

	//{{{ findStartTag() method
	private static Tag findStartTag(String text, Tag endTag)
	{
		int tagCounter = 0;

		// some voodoo to skip inline scripts and comments and such...
		// basically we do not look for stuff that looks like a tag
		// between <X ... X> where X is a non-alphanumeric character.
		// this works fine for JSP, PHP, and maybe others.
		boolean notATag = false;

loop:		for (int i = endTag.start - 1; i >= 0; i--)
		{
			char ch = text.charAt(i);
			if(ch == '<')
			{
				if(i != text.length() - 1)
				{
					char ch2 = text.charAt(i + 1);
					if(ch2 != '/' && ch2 != '\'' && ch2 != '"'
						&& !Character.isWhitespace(ch2)
						&& !Character.isLetterOrDigit(ch2))
					{
						if(notATag)
							notATag = false;
						//else
						//	return null;
					}
				}
			}
			else if(ch == '>')
			{
				if(notATag)
					continue;

				if(i != 0)
				{
					char ch2 = text.charAt(i - 1);
					if(ch2 != '/' && ch2 != '\'' && ch2 != '"'
						&& !Character.isWhitespace(ch2)
						&& !Character.isLetterOrDigit(ch2))
					{
						notATag = true;
						continue;
					}
				}

				Tag tag = getTagAtOffset(text,i + 1);
				if (tag == null)
					continue;

				else if(tag.tag.equals(endTag.tag))
				{
					if(tag.type == T_START_TAG)
					{
						if(tagCounter == 0)
							return tag;
						else
							tagCounter--;
					}
					else if(tag.type == T_END_TAG)
						tagCounter++;
				}

				i = tag.start;
			}
		}

		return null;
	} //}}}

	//}}}

	//{{{ Tag class
	public static class Tag extends StructureMatcher.Match
	{
		public String tag = null;
		public int type = -1;

		Tag(int start, int end)
		{
			this.start = start;
			this.end = end;
		}

		public String toString()
		{
			switch(type)
			{
			case T_START_TAG:
				return "<" + tag + ">";
			case T_STANDALONE_TAG:
				return "<" + tag + "/>";
			case T_END_TAG:
				return "</" + tag + ">";
			default:
				throw new InternalError();
			}
		}
	} //}}}
}

