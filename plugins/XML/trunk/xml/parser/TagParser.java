/*
 * TagParser.java - a few very simple but fast methods used for tag highlighting
 * and such
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000 Scott Wyatt, 2001 Andre Kaplan
 * Portions copyright (C) 2001, 2002 Slava Pestov
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
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
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
		if(endTag == -1 || endTag < pos)
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
				endTagName = i;
				tagType = T_STANDALONE_TAG;
			}
		}

		Tag tag = new Tag();
		tag.start = startTag;
		tag.end   = endTag;
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
		HashMap elementDecls)
	{
		Stack tagStack = new Stack();

loop:		for (int i = text.lastIndexOf('<', pos);
			i != -1; i = text.lastIndexOf('<', --i))
		{
			Tag tag = getTagAtOffset(text,i + 1);
			if (tag == null)
				continue;
			else
			{
				ElementDecl decl = (ElementDecl)elementDecls.get(tag.tag);
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
						if(tagStack.get(j).equals(tag.tag))
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
					tagStack.push(tag.tag);
				}
			}
		}

		return null;
	} //}}}

	//{{{ Private members

	//{{{ findEndTag() method
	private static Tag findEndTag(String text, Tag startTag)
	{
		int tagCounter = 0;

loop:		for (int i = text.indexOf('<', startTag.end);
			i != -1; i = text.indexOf('<', ++i))
		{
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
		}

		return null;
	} //}}}

	//{{{ findStartTag() method
	private static Tag findStartTag(String text, Tag endTag)
	{
		int tagCounter = 0;

loop:		for (int i = text.lastIndexOf('<', endTag.start - 1);
			i != -1; i = text.lastIndexOf('<', --i))
		{
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
						tagCounter++;
				}
				else if(tag.type == T_END_TAG)
					tagCounter++;
			}
		}

		return null;
	} //}}}

	//}}}

	//{{{ Tag class
	public static class Tag {
		public String tag = null;
		public int type = -1;
		public int start = -1;
		public int end = -1;

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

