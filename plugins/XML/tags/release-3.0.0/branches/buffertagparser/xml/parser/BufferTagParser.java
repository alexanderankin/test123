/*
 * BufferTagParser.java - reimplement TagParser using syntax highlighting as a token stream 
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
import java.util.regex.*;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import xml.completion.ElementDecl;
import xml.XmlParsedData;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
//}}}

/** reimplement TagParser using syntax highlighting as a token stream */
public class BufferTagParser
{
	public static final int T_STANDALONE_TAG = 0;
	public static final int T_START_TAG = 1;
	public static final int T_END_TAG = 2;

	//{{{ getTagAtOffset() method
	public static Tag getTagAtOffset(JEditBuffer buffer, int pos)
	{
		if(pos < 0 || pos > buffer.getLength())
			return null;
		TwoWayTokenIterator it = new TwoWayTokenIterator(buffer, pos);
		return getTagAtOffset(buffer,it);
	}

	public static Tag getTagAtOffset(JEditBuffer buffer, TwoWayTokenIterator it)
	{
		// Get the last '<' before current position.
		int startTag = -1;
		
		
		while(startTag == -1 && it.previous())
		{
			if(it.current().id == Token.MARKUP
				&& it.current().length == 1)
			{
				String currentText = it.getCurrentText();
				if("<".equals(currentText))
				{
					startTag = it.getCurrentCaret();
				}
				else if(">".equals(currentText))
				{
					break;// found end of tag !
				}
			}
		}

		
		if(startTag == -1)
			// FIXME: why ?     || startTag + 2 >= text.length()) // at least 2 chars after '<'
			return null;

			
		int tagType = T_START_TAG;
		int startTagName = startTag + 1;
		String tagName = "";
		if(it.next())
		{
			if(it.current().id == Token.MARKUP && it.current().length == 1
				&& "/".equals(it.getCurrentText()))
			{
				tagType = T_END_TAG;
				it.next();
			}
			
			tagName = it.getCurrentText();
			
			if(it.current().id == Token.LABEL)
			{
				// sloppy here...
				it.next();
				tagName += ":";
				it.next();
				tagName += it.getCurrentText();
			}

			int endTag = -1;
	
			while(endTag == -1 && it.next())
			{
				if(it.current().id == Token.MARKUP
					&& it.current().length == 1)
				{
					String currentText = it.getCurrentText();
					if(">".equals(currentText))
					{
						endTag = it.getCurrentCaret();
					}
					else if("<".equals(currentText))
					{
						break;// found start of new tag ??
					}
				}
			}
			
			if(endTag == -1)
				return null;

			it.previous();
			if(it.current().id == Token.MARKUP && it.current().length == 1
				&& it.getCurrentText().equals("/"))
			{
				tagType = T_STANDALONE_TAG;
			}
			
			Tag tag = new Tag(startTag,endTag);
			tag.tag   = tagName;
			tag.type  = tagType;
			
			return tag;
		}
		return null;
	} //}}}

	//{{{ getMatchingTag() method
	public static Tag getMatchingTag(JEditBuffer buffer, Tag tag)
	{
		if (tag.type == T_START_TAG){
			// must go after >
			TwoWayTokenIterator it = new TwoWayTokenIterator(buffer,tag.end+1);
			return findEndTag(buffer, it, tag);
		}else if (tag.type == T_END_TAG){
			// must go at <
			TwoWayTokenIterator it = new TwoWayTokenIterator(buffer,tag.start);
			return findStartTag(buffer, it, tag);
		}
		return null;
	} //}}}

	public static Tag getMatchingTag(JEditBuffer buffer, TwoWayTokenIterator it, Tag tag)
	{
		if (tag.type == T_START_TAG)
			return findEndTag(buffer, it, tag);
		else if (tag.type == T_END_TAG)
			return findStartTag(buffer, it, tag);
		return null;
	} //}}}

	//{{{ findLastOpenTag() method
	public static Tag findLastOpenTag(JEditBuffer buffer, int pos, XmlParsedData data)
	{
		if(pos < 0 || pos > buffer.getLength())
			return null;
		TwoWayTokenIterator it = new TwoWayTokenIterator(buffer, pos);
		return findLastOpenTag(buffer,it,pos,data);
	}
	
	public static Tag findLastOpenTag(JEditBuffer buffer, TwoWayTokenIterator it,
		int pos, XmlParsedData data)
	{
		Stack<String> tagStack = new Stack<String>();

		while(moveToLastGT(it))
		{
			int newPos = it.getCurrentCaret()-1;
			Tag tag = getTagAtOffset(buffer,it);
			if(tag != null)
			{
				if(tag.type == T_STANDALONE_TAG)
				{
					continue;
				}
				else
				{
					String tagName = tag.tag;
					ElementDecl decl = null;
					if(data != null && data.html)
					{
						tagName = tagName.toLowerCase();
						// only fetch the declaration in HTML mode,
						// to know if an unclosed element is allowed
						decl = data.getElementDecl(tagName, tag.start+1);
					}
					
					if(decl != null && decl.empty)
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
	public static boolean isInsideTag(JEditBuffer buffer, int pos)
	{
		TwoWayTokenIterator it = new TwoWayTokenIterator(buffer, pos);
		return isInsideTag(buffer,it);
	}
	
	// FIXME: doesn't work in xslt
	public static boolean isInsideTag(JEditBuffer buffer, TwoWayTokenIterator it)
	{
		while(it.previous())
		{
			if(it.current().id == Token.MARKUP
				&& it.current().length == 1)
			{
				String currentText = it.getCurrentText();
				if("<".equals(currentText))
				{
					return true;
				}
				else if(">".equals(currentText))
				{
					return false;
				}
			}
		}
		return false;
	} //}}}

	//{{{ Private members

	//{{{ findEndTag() method
	private static Tag findEndTag(JEditBuffer buffer, TwoWayTokenIterator it, Tag startTag)
	{
		int tagCounter = 0;

		while (moveToNextLT(it))
		{
			if(it.next())
			{
				Tag tag = getTagAtOffset(buffer, it);
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
		}
		return null;
	} //}}}

	//{{{ findStartTag() method
	private static Tag findStartTag(JEditBuffer buffer, TwoWayTokenIterator it, Tag endTag)
	{
		int tagCounter = 0;
		
		while (moveToLastGT(it))
		{
			if(it.previous())
			{
				Tag tag = getTagAtOffset(buffer, it);
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
			}
		}
		return null;
	} //}}}

	private static final Pattern pName = Pattern.compile(".*[-:A-Za-z\\xC0-\\xD6"
					+"\\xD8-\\xF6\\xF8-\\u02FF\\u0370-\\u037D"
					+"\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F"
					+"\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF"
					+"\\uFDF0-\\uFFFD"  /*can't add \\u10000-\\uEFFFF to the pattern */
					+".0-9\\xB7\\u0300-\\u036F\\u203F-\\u2040]=?"); // equal sign may be concatened
	private static Attr readQualifiedName(TwoWayTokenIterator it,int endOfTag){
		boolean found = false;
		while (!found && it.next() && it.getCurrentCaret() < endOfTag)
		{
			found = ((it.current().id == Token.MARKUP
				  && pName.matcher(it.getCurrentText()).matches())
				|| it.current().id == Token.LABEL);
		}
		
		if(!found)return null;
		
		int start =-1;
		int end = -1;
		String name = "";
		
		Token token = it.current();
		
		if(token.id == Token.LABEL)
		{
			name = it.getCurrentText();
			start = it.getCurrentCaret();
			if(it.next() && it.getCurrentCaret() < endOfTag
				     &&  it.current().length == 1
				     && ":".equals(it.getCurrentText()))
			{
				name += ':';
				if(!it.next() || it.getCurrentCaret() >= endOfTag)return null;
				else token = it.current();
			}
			else return null;
		}
		
		if(token.id == Token.MARKUP){
			String image = it.getCurrentText();
			if(pName.matcher(image).matches())
			{
				System.err.println("attribute name: "+image);
				if(start == -1)start = it.getCurrentCaret();
				end = it.getCurrentCaret()+image.length();
				name += image;
				Attr a = new Attr(start,end);
				a.name = name;
				return a;
			}
			else{
				System.err.println("invalid attribute name:"+image);
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * FIME: doesn't work for xslt
	 */
	private static void readEqualValue(Attr a, TwoWayTokenIterator it, int end)
	{
		boolean foundEqual = false;
		if(a.name.endsWith("=")){
			System.err.println("name=");
			a.name = a.name.substring(0,a.name.length()-1);
			foundEqual = true;
			it.previous();// back to name=
		}
		while (!foundEqual && it.next() && it.getCurrentCaret() < end)
		{
			Token token = it.current();
			if(token.id == Token.MARKUP
				&& token.length == 1
				&& "=".equals(it.getCurrentText()))
			{
				foundEqual = true;
			}
			else if(token.id == Token.LABEL)
			{
				System.err.println("no attribute value for "+a.name);
				it.previous();
				return;
			}
			else if(token.id == Token.MARKUP
				&& pName.matcher(it.getCurrentText()).matches())
			{
				System.err.println("no attribute value for "+a.name);
				it.previous();
				return;
			}
		}
		
		if(!foundEqual)return;
		
		boolean foundStart=false;
		String value = "";
		while (it.next() && it.getCurrentCaret() <= end)
		{
			Token token = it.current();
			if(token.id == Token.LITERAL1){
				foundStart = true;
				value+=it.getCurrentText();
				System.err.println("found "+token+":value="+value);
			}else if(foundStart){
				// multiline attribute values
				if(token.id == Token.END &&
				   !(value.length()>=2 
				   	   && value.charAt(0) == value.charAt(value.length()-1)))
				{
					continue;
				}
				System.err.println("end "+token+":value="+value);
				a.end = it.getCurrentCaret();
				a.val = value;
				it.previous();
				return;
			}
		}
	}
	
	//{{{ getAttrs() method
	// works only for XML
	public static List<Attr> getAttrs(JEditBuffer buffer,Tag tag)
	{
		if (tag.type == T_START_TAG || tag.type == T_STANDALONE_TAG)
		{
			// < tag.tag |
			int pos = tag.start+1+tag.tag.length();
			TwoWayTokenIterator it = new TwoWayTokenIterator(buffer, pos);
			List<Attr> attrs = new ArrayList<Attr>();
			Token t = it.current();
			System.err.println("tag="+tag+",tok="+t);
			while(it.getCurrentCaret()<tag.end){
				Attr a = readQualifiedName(it,tag.end);
				if(a!=null){
					readEqualValue(a,it,tag.end);
					attrs.add(a);
				}
			}
			
			return attrs;
		}
		else
		{
			return Collections.<Attr>emptyList();
		}
	} //}}}
	
	private static boolean moveToLastGT(TwoWayTokenIterator it)
	{
		while (it.previous())
		{
			if(it.current().id == Token.MARKUP
				&& it.current().length == 1 && ">".equals(it.getCurrentText()))
			{
				return true;
			}
		}
		return false;
	}
	
	private static boolean moveToNextLT(TwoWayTokenIterator it)
	{
		while (it.next())
		{
			if(it.current().id == Token.MARKUP
				&& it.current().length == 1 && "<".equals(it.getCurrentText()))
			{
				return true;
			}
		}
		return false;
	}
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

