/*
 * TagParser.java - a few very simple but fast methods used for tag highlighting
 * and such
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000 Scott Wyatt, 2001 Andre Kaplan
 * Portions copyright (C) 2001, 2003 Slava Pestov
 * Portions copyright (C) 2010 Eric Le Lay
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
		int startTag = text.lastIndexOf('<', pos - 1);			// -1 because don't want to return something when cursor is here:  |<a>
		if(startTag == -1 || startTag + 2 >= text.length()) // at least 2 chars after '<'
			return null;

		int len = text.length();
		
		int endTag = 0;
		for(int i = startTag+1; i < len ; i++)
		{
			int ch = text.charAt(i);
			if(ch == '>'){
				endTag = i+1;
				break;
			}else if(ch == '\'' || ch == '"'){
				int nextQuote = text.indexOf(ch,i+1);
				if(nextQuote == -1)
				{
					System.err.println("quote is not closed !");
					return null;
				}
				else
				{
					i = nextQuote;
					continue;
				}
			}
		}
		
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
			else if(ch == '<')					// FIXME: what is this for ? not closed tag (see:  <a | <b> )
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
		Stack<String> tagStack = new Stack<String>();

		// some voodoo to skip inline scripts and comments and such...
		// basically we do not look for stuff that looks like a tag
		// between <X ... X> where X is a non-alphanumeric character.
		// this works fine for JSP, PHP, and maybe others.
		boolean notATag = false;

		for (int i = Math.min(text.length() - 1,pos); i >= 0; i--)
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
						notATag = false;
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

				Tag tag = getTagAtOffset(text,i + 1);           // TODO: it's i+1, but could it not work with i (inside tag)

				if (tag == null)
					continue;
				else
				{
					String tagName;
					ElementDecl decl;
					if(data == null)
					{
						tagName = tag.tag;
						decl = null;
					}
					else
					{
						if(data.html){
							tagName = tag.tag.toLowerCase();
							// only fetch the declaration in HTML mode,
							// to know if an unclosed element is allowed
							decl = data.getElementDecl(tag.tag, tag.start+1);
						}else{
							tagName = tag.tag;
							decl = null;
						}
						
					}
					
					 
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
	//it doesn't skip comments and such but it works ???
	public static boolean isInsideTag(String text, int pos)
	{
		int start = text.lastIndexOf('<',pos);

		if(start > -1 && start < pos) 
		{
			int end = -1;
			int len = text.length();
			for(int i = start+1; i < len && i < pos; i++) // +1 because start after <
			{
				int ch = text.charAt(i);
				if(ch == '>'){
					end = i;
					break;
				}else if(ch == '\'' || ch == '"'){
					int nextQuote = text.indexOf(ch,i+1);
					if(nextQuote == -1)
					{
						System.err.println("quote is not closed !");
						return false;
					}
					else
					{
						i = nextQuote;
						continue;
					}
				}
			}
			if(end == -1)
				return true;
			else if(end < start)
				return true;
			else
				return false;
		}
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

		for (int i = startTag.end; i < text.length(); i++)
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

				Tag tag = getTagAtOffset(text,i + 1);		// here we want to be inside the tag (at the pipe in <|a href="..."> )
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

				i = tag.end - 1; //skip to the end of tag
			}
			else if(ch == '>' && i != 0)
			{
				char ch2 = text.charAt(i - 1);
				if(ch2 != '"' && ch2 != '\''
					&& !Character.isWhitespace(ch2)
					&& !Character.isLetterOrDigit(ch2))
				{
					notATag= false;
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

		for (int i = endTag.start - 1; i >= 0; i--)
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
						notATag = false;
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

				i = tag.start; // go to before tag
			}
		}

		return null;
	} //}}}

	//{{{ getAttrs() method
	// works only for XML : all attributes must be quote
	public static List<Attr> getAttrs(String text, Tag tag)
	{
		if (tag.type == T_START_TAG || tag.type == T_STANDALONE_TAG)
		{
			List<Attr> attrs = new ArrayList<Attr>();
			int end = tag.end;
			if(tag.type == T_STANDALONE_TAG)
			{
				end--;
			}
			if(text.length() < end)
			{
				end = text.length();
			}
			
			int startAttrName = -1;
			int endAttrName = -1;
			boolean inName = false;
			for(int i=tag.start+tag.tag.length()+1 ; i<end ; i++)
			{
				int ch = text.charAt(i);
				
				if(Character.isWhitespace(ch))
				{
					// end of name
					if(inName){
						endAttrName = i;
						inName = false;
					}
				}
				else if(startAttrName == -1)   		// start of name
				{
					startAttrName = i;
					inName = true;
				}
				else if( ch == '=' )
				{
					if(inName)
					{
						endAttrName = i;
						inName = false;
					}
				}
				else if( ch == '\'' || ch == '"' )
				{
					int nextQuote = text.indexOf(ch,i+1);
					if(nextQuote == -1)
					{
						System.err.println("quote is not closed !");
						break;
					}
					else
					{
						Attr a = new Attr(startAttrName,nextQuote+1);
						a.name = text.substring(startAttrName,endAttrName);
						a.val = text.substring(i, nextQuote+1);
						attrs.add(a);
						
						startAttrName=-1;
						endAttrName=-1;
						inName=false;
						
						i = nextQuote;
					}
					
				}
			}
			
			
			return attrs;
		}
		else
		{
			return Collections.<Attr>emptyList();
		}
	} //}}}
	
	//}}}

	//{{{ Tag class
	public static class Tag extends StructureMatcher.Match
	{
		/** Element name */
		public String tag = null;
		public int type = -1;

		/**
		 * 
		 * @param start offset from start of buffer
		 * @param end offset from start of buffer
		 */
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
	//{{{ Attr class : a text-level attribute
	public static class Attr
	{
		/** Attribute's name (what's before the =) */
		public String name = null;
		/** Attribute's value (with quotes) */
		public String val = null;
		/** Attribute's start */ 
		public int start;
		/** Attribute's end */ 
		public int end;
		
		/**
		 * 
		 * @param start offset from start of buffer
		 * @param end offset from start of buffer
		 */
		Attr(int start, int end)
		{
			this.start = start;
			this.end = end;
		}

		public String toString()
		{
			return name+"="+val;
		}
	} //}}}
}

