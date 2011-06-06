/*
 * TagParser.java - a few very simple but fast methods used for tag highlighting
 * and such
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.parser.javacc;

//{{{ Imports
import java.util.*;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import xml.completion.ElementDecl;
import xml.XmlParsedData;
import sidekick.util.ElementUtil;
import sidekick.util.Location;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
import java.io.StringReader;
//}}}

public class TagParser
{
	public static final int T_STANDALONE_TAG = 0;
	public static final int T_START_TAG = 1;
	public static final int T_END_TAG = 2;

	//{{{ parse() method
	private static XmlDocument parse(String text){
		try{
			StringReader r = new StringReader(text);
			XmlParser parser = new XmlParser(r);
			// FIXME: do sthing
			parser.getParseErrors();
			return parser.XmlDocument();
		}catch(ParseException e){
			Log.log(Log.ERROR,"error in TagParser.parse()",e);
			return null;
		}
	}//}}}
	
	private static class LastSeenVisitor extends XmlVisitor{
		XmlDocument.XmlElement lastSeen = null;
		private Location loc;
		
		LastSeenVisitor(int line, int offset){
			//System.err.println("line="+line+","+offset);
			this.loc = new Location(line,offset);
		}
		
		public void visit(XmlDocument.Tag t) {
			//System.err.println("visit("+t.getStartLocation()+";"+t.getEndLocation());
			// loc is after startLocation
			// and is before endLocation
			if(loc.compareTo(t.getStartLocation())>0
				&& loc.compareTo(t.getEndLocation())<0){
				lastSeen = t;
			}
			// loc is before endLocation
			// if(loc.compareTo(t.endLocation)<0){
			// }
		}
		
		public void visit(XmlDocument.EndTag t) {
			// loc is after startLocation
			// and is before endLocation
			if(loc.compareTo(t.getStartLocation())>0
				&& loc.compareTo(t.getEndLocation())<0){
				lastSeen = t;
			}
		}
		
	}
	
	//{{{ getTagAtOffset() method
	public static Tag getTagAtOffset(Buffer buffer, String text, int pos)
	{
		if(pos < 0 || pos > text.length())
			return null;

		XmlDocument doc = parse(text);
		//System.err.println("---------------------------------");
		//new XmlDebugDumper(System.err).visit(doc);
		int line = buffer.getLineOfOffset(pos);
		int offset = pos - buffer.getLineStartOffset(line)+1;
		LastSeenVisitor visitor = new LastSeenVisitor(line+1, offset);
		visitor.visit(doc);
		if(visitor.lastSeen != null){
			int tagType;
			String tagName;
			XmlDocument.AttributeList attrs;
			if(visitor.lastSeen instanceof XmlDocument.Tag){
				XmlDocument.Tag t = (XmlDocument.Tag)visitor.lastSeen;
				if(t.emptyTag){
					tagType = T_STANDALONE_TAG;
				}else{
					tagType = T_START_TAG;
				}
				tagName = t.tagName;
				attrs = t.attributeList;
			}else{
				XmlDocument.EndTag t = (XmlDocument.EndTag)visitor.lastSeen;
				tagType = T_END_TAG;
				tagName = t.tagName;
				attrs = null;
			}
			ElementUtil.createStartPosition(buffer, visitor.lastSeen);
			ElementUtil.createEndPosition(buffer, visitor.lastSeen);
			
			Tag tag = new Tag(visitor.lastSeen.getStartPosition().getOffset()
					,visitor.lastSeen.getEndPosition().getOffset()-1);
			tag.tag   = tagName;
			tag.type  = tagType;
			tag.attrs = attrs;
			
			return tag;
		}else{
			return null;
		}
	} //}}}

	//{{{ getMatchingTag() method
	public static Tag getMatchingTag(Buffer buffer, String text, Tag tag)
	{
		if (tag.type == T_START_TAG)
			return findEndTag(buffer, text, tag);
		else if (tag.type == T_END_TAG)
			return findStartTag(buffer, text, tag);
		else return null;
	} //}}}

	//{{{ findLastOpenTag() method
	private static class LastOpenTagVisitor extends XmlVisitor{
		private Location loc;
		private Stack<XmlDocument.Tag> elements;
		
		LastOpenTagVisitor(int line, int offset){
			//System.err.println("line="+line+","+offset);
			elements = new Stack<XmlDocument.Tag>();
			this.loc = new Location(line,offset);
		}
		
		public void visit(XmlDocument.Tag t) {
			//System.err.println("visit("+t.getStartLocation()+";"+t.getEndLocation()+","+t);
			// loc is after endLocation
			if(loc.compareTo(t.getEndLocation())>=0){
				if(!t.emptyTag){
					elements.push(t);
				}
			}
		}
		
		public void visit(XmlDocument.EndTag t) {
			//System.err.println("visit("+t.getStartLocation()+";"+t.getEndLocation()+","+t);
			// loc is after endLocation
			if(loc.compareTo(t.getEndLocation())>0){
				for(int i=elements.size()-1;i>=0;i--){
					XmlDocument.Tag start = elements.get(i);
					if(start.tagName.equals(t.tagName)){
						// found matching open tag, pop to it !
						elements.setSize(i);
						return;
					}
				}
			}
		}
	}

	public static Tag findLastOpenTag(Buffer buffer, String text, int pos,
		XmlParsedData data)
	{
		XmlDocument doc = parse(text);
		//System.err.println("---------------------------------");
		//new XmlDebugDumper(System.err).visit(doc);
		int line = buffer.getLineOfOffset(pos);
		int offset = pos - buffer.getLineStartOffset(line)+1;
		LastOpenTagVisitor visit = new LastOpenTagVisitor(line+1,offset);
		visit.visit(doc);
		if(visit.elements.isEmpty()){
			return null;	
		}else{
			int tagType;
			String tagName;
			XmlDocument.Tag t = visit.elements.pop();
			if(t.emptyTag){
				tagType = T_STANDALONE_TAG;
			}else{
				tagType = T_START_TAG;
			}
			tagName = t.tagName;
			ElementUtil.createStartPosition(buffer, t);
			ElementUtil.createEndPosition(buffer, t);
			
			Tag tag = new Tag(t.getStartPosition().getOffset()
					,t.getEndPosition().getOffset()-1);
			tag.tag   = tagName;
			tag.type  = tagType;
			tag.attrs = t.attributeList;
			return tag;
		}
	} //}}}

	//{{{ isInsideTag() method
	// used by XmlInsert and XmlParsedData
	//it doesn't skip comments and such but it works ???
	// FIXME: should it be rewritten to use XmlParser ?
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
	private static class FindEndTagVisitor extends XmlVisitor{
		private Location loc;
		private Stack<XmlDocument.Tag> elements;
		private Tag startTag;
		private XmlDocument.EndTag lastSeen;
		
		FindEndTagVisitor(int line, int offset, Tag startTag){
			//System.err.println("line="+line+","+offset);
			elements = new Stack<XmlDocument.Tag>();
			this.loc = new Location(line,offset);
			this.startTag = startTag;
		}
		
		public void visit(XmlDocument.Tag t) {
			//System.err.println("visit("+t.getStartLocation()+";"+t.getEndLocation());
			// startTag is before this start tag
			if(loc.compareTo(t.getStartLocation())<0){
				if(!t.emptyTag){
					elements.push(t);
				}
			}
		}
		
		public void visit(XmlDocument.EndTag t) {
			// loc is before end tag
			// (passed the startTag)
			if(loc.compareTo(t.getStartLocation())<0){
				//System.err.println("considering "+t);
				if(t.tagName.equals(startTag.tag)
					&& elements.isEmpty())
				{
					// TODO: stop after that
					if(lastSeen == null){
						//System.err.println("found "+t);
						lastSeen = t;
					}
				}
				else {
				
					for(int i=elements.size()-1;i>=0;i--){
						XmlDocument.Tag start = elements.get(i);
						if(start.tagName.equals(t.tagName)){
							// found matching open tag, pop to it !
							elements.setSize(i);
							return;
						}
					}
				}
			}
		}
	}

	private static Tag findEndTag(Buffer buffer, String text, Tag startTag)
	{
		XmlDocument doc = parse(text);
		//System.err.println("---------------------------------");
		//new XmlDebugDumper(System.err).visit(doc);
		int line = buffer.getLineOfOffset(startTag.start);
		int offset = startTag.start - buffer.getLineStartOffset(line)+1;
		FindEndTagVisitor visitor = new FindEndTagVisitor(line+1, offset, startTag);
		visitor.visit(doc);
		if(visitor.lastSeen != null){
			ElementUtil.createStartPosition(buffer, visitor.lastSeen);
			ElementUtil.createEndPosition(buffer, visitor.lastSeen);
			
			Tag tag = new Tag(visitor.lastSeen.getStartPosition().getOffset()
					,visitor.lastSeen.getEndPosition().getOffset()-1);
			tag.tag   = startTag.tag;
			tag.type  = T_END_TAG;

			return tag;
		}else{
			return null;
		}
	} //}}}

	//{{{ findStartTag() method
	private static Tag findStartTag(Buffer buffer, String text, Tag endTag)
	{
		XmlDocument doc = parse(text);
		//System.err.println("---------------------------------");
		//new XmlDebugDumper(System.err).visit(doc);
		int line = buffer.getLineOfOffset(endTag.start);
		int offset = endTag.start - buffer.getLineStartOffset(line) + 1;
		LastOpenTagVisitor visitor = new LastOpenTagVisitor(line+1,offset);
		visitor.visit(doc);
		if(!visitor.elements.isEmpty()){
			for(int i=visitor.elements.size()-1;i>=0;i--){
				XmlDocument.Tag start = visitor.elements.get(i);
				if(start.tagName.equals(endTag.tag)){
					ElementUtil.createStartPosition(buffer, start);
					ElementUtil.createEndPosition(buffer, start);
					
					Tag tag = new Tag(start.getStartPosition().getOffset()
							,start.getEndPosition().getOffset()-1);
					tag.tag   = start.tagName;
					tag.type  = T_START_TAG;
					tag.attrs = start.attributeList;
					
					return tag;
				}
			}
		}
		return null;
	} //}}}

	//{{{ getAttrs() method
	// works only for XML : all attributes must be quoted
	public static List<Attr> getAttrs(Buffer buffer, Tag tag)
	{
		if (tag.type == T_START_TAG || tag.type == T_STANDALONE_TAG)
		{
			List<Attr> attrs = new ArrayList<Attr>();
			if(tag.attrs != null){
				for(XmlDocument.Attribute attr: tag.attrs.attributes){
					ElementUtil.createStartPosition(buffer, attr);
					ElementUtil.createEndPosition(buffer, attr);
					
					Attr a = new Attr(attr.getStartPosition().getOffset(),attr.getEndPosition().getOffset()-1);
					a.name = attr.name;
					a.val = attr.value;
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
	
	//}}}

	//{{{ Tag class
	public static class Tag extends StructureMatcher.Match
	{
		/** Element name */
		public String tag = null;
		public int type = -1;
		XmlDocument.AttributeList attrs;
		
		/**
		 * 
		 * @param start offset from start of buffer
		 * @param end offset from start of buffer
		 */
		public Tag(int start, int end)
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

