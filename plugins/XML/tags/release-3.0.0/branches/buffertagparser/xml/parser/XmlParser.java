/*
 * XmlParser.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
 * Portions copyright (C) 2001 David Walend
 * Copyright (C) 2009 Greg Knittl
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.parser;

import java.util.*;
import java.util.regex.*;
import org.xml.sax.helpers.NamespaceSupport;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import sidekick.*;
import xml.completion.*;
import xml.completion.ElementDecl.AttributeDecl;
import xml.*;

/**
 * This is the common base class for both HTML and XML Parsers.
 * It contains auto completion for closing element tags. 
 */
public abstract class XmlParser extends SideKickParser
{
	public static final String INSTANT_COMPLETION_TRIGGERS = "/";
	public static final int ELEMENT_COMPLETE = '<';
	public static final int ENTITY_COMPLETE = '&';
	public static final int ATTRIB_COMPLETE = ' ';
	public static final int COMMENT_COMPLETE = '-';
	public static final int CDATA_COMPLETE = '[';
	public static final int COMMENT_OR_CDATA_COMPLETE = '!';
	public static final int ELEMENT_CLOSE_COMPLETE = '/';
	public static final int LESS_THAN_COMPLETE = 'l';

	//{{{ XmlParser constructor
	public XmlParser(String name)
	{
		super(name);
		highlight = new TagHighlight();
	} //}}}

	//{{{ stop() method
	/**
	 * Stops the parse request currently in progress. It is up to the
	 * parser to implement this.
	 * @since SideKick 0.3
	 */
	public void stop()
	{
		stopped = true;
	} //}}}

	//{{{ activate() method
	public void activate(EditPane editPane)
	{
		super.activate(editPane);
		if(jEdit.getBooleanProperty("xml.tag-highlight"))
			editPane.getTextArea().addStructureMatcher(highlight);
	} //}}}

	//{{{ deactivate() method
	public void deactivate(EditPane editPane)
	{
		editPane.getTextArea().removeStructureMatcher(highlight);
	} //}}}

	//{{{ supportsCompletion() method
	public boolean supportsCompletion()
	{
		return true;
	} //}}}

	//{{{ getInstantCompletionTriggers() method
	public String getInstantCompletionTriggers()
	{
		return INSTANT_COMPLETION_TRIGGERS;
	} //}}}

	//{{{ complete() method
	private static final Pattern pName = Pattern.compile(".*[-:A-Za-z\\xC0-\\xD6"
					+"\\xD8-\\xF6\\xF8-\\u02FF\\u0370-\\u037D"
					+"\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F"
					+"\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF"
					+"\\uFDF0-\\uFFFD"  /*can't add \\u10000-\\uEFFFF to the pattern */
					+".0-9\\xB7\\u0300-\\u036F\\u203F-\\u2040]");
	private static final Pattern pEntityRef = Pattern.compile(".*&([-:A-Za-z\\xC0-\\xD6"
					+"\\xD8-\\xF6\\xF8-\\u02FF\\u0370-\\u037D"
					+"\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F"
					+"\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF"
					+"\\uFDF0-\\uFFFD"  /*can't add \\u10000-\\uEFFFF to the pattern */
					+".0-9\\xB7\\u0300-\\u036F\\u203F-\\u2040]*)");
	
	public SideKickCompletion complete(EditPane editPane, int caret)
	// The challenge of realtime keystroke completion for xml is that
	// the syntax may not be well formed at any given keystroke.
	// It would be ideal to start from an incremental xml parser/validator.
	// A Google search shows the nxml mode for EMACS is probably the
	// most advanced implementation of this to date (I haven't tried it).
	// complete() could check if SideKick parse on keystroke is
	// enabled and use that xml parse tree if available.
	//
	// jEdit parses syntax per keystroke and just as it usually looks reasonable
	// on the screen so it is usually a reasonable basis for suggesting completions
	//
	// This patch uses syntax information to improve the validity of
	// completion popups in a number of ways, including reducing
	// attribute completion popups in text areas.
	// This relies on XML text areas being unparsed, Token.NULL.
	// This true for the two xml modes I'm aware of: xml and xsl
	//
	// If a new xml-derived edit mode say, xhtml/css, parsed the css syntax
	// and then used this complete method, attribute popups would appear in
	// it's css text areas
	// This code could add additional logic to handle such modes
	// Better would be to have nested levels of syntax parsing
	// so that this code could handle xml level completion for all
	// xml derived syntaxes. Or process syntax in parallel to jEdit with a
	// custom rules set
	//
	// jEdit syntax Token.END represents the newline character(s) at the end of each
	// line. It is a visual marker that does not correspond to XML syntax.
	// XML allows newlines in at least text areas, attribute values, comments, CDATA.
	// I have treated Token.END as whitespace and attribute completion will pop up
	// incorrectly in some circumstances at the start of lines after the first line,
	// such as when a text area spans multiple lines
	//
	// Additional fixes:
	// enable attribute completion after attribute values that contain /
	// enable attribute completion for elements that span multiple lines
	//
	// For testing purposes, manually invoking completion from
	// Plugins > SideKick > Show Completion Popup seems to display
	// more exceptions in the code than keystroke activation
	//
	// Greg Knittl 2009-06-19
	{
		// caret can be at 0 when invoking completion from SideKick plugin menu
		// or through backspace
		// could pop up a pro forma xml declaration for caret = 0
		if (caret == 0)
			return null;
		SideKickParsedData _data = SideKickParsedData
			.getParsedData(editPane.getView());
		if(!(_data instanceof XmlParsedData))
			return null;
		if(XmlPlugin.isDelegated(editPane.getTextArea()))
			return null;

		XmlParsedData data = (XmlParsedData)_data;

		Buffer buffer = editPane.getBuffer();
		
		int lastchar = caret - 1;
		
		TwoWayTokenIterator it = new TwoWayTokenIterator(buffer,lastchar);
		
		// {{{ find out what we have to complete (attribute, element, comment, etc.)
		Token token = it.current();

		System.err.println("token at caret is "+token
			+":"+Token.tokenToString(token.id)
			+", rule is "+token.rules.getName()
			+", set is "+token.rules.getSetName());
		
		int stateBefore = 0;
		String nameToComplete = "";
		String word = "";
		if(token.id == Token.END)
		{
			System.err.println("begin line!");
		}
		else if(token.id == Token.MARKUP || token.id == Token.OPERATOR
				|| token.id == Token.LABEL)
		{
			String image = it.getCurrentText();
			int caretPosInImage = lastchar - it.getCurrentCaret();
			if(caretPosInImage>=0 && caretPosInImage < image.length())
			{
				image = image.substring(0,caretPosInImage+1);
			}
			if("<".equals(image))
			{
				System.err.println("just hit <");
				stateBefore = LESS_THAN_COMPLETE;
			}
			else if("/".equals(image))
			{
				if(it.previous() && it.current().id == Token.MARKUP
					&& it.getCurrentText().equals("<"))
				{
					stateBefore = ELEMENT_CLOSE_COMPLETE;
				}
			}
			else if("&".equals(image))
			{
				System.err.println("error: just hit & !");
			}
			else
			{
				char lastChar = image.charAt(image.length()-1);
				if(pName.matcher(image).matches())
				{
					System.err.println("element or attribute name");
					
					nameToComplete = image;
					if(it.previous())
					{
						if(it.current().id == Token.MARKUP
							&& it.current().length == 1 && "<".equals(it.getCurrentText()))
						{
							System.err.println("at element name");
							stateBefore = ELEMENT_COMPLETE;
						}
						else if(it.current().id == Token.MARKUP
							&& it.current().length == 1 && "&".equals(it.getCurrentText()))
						{
							System.err.println("error: & in markup !");
						}
						else if(it.current().id == Token.MARKUP
							&& it.current().length == 1 && "/".equals(it.getCurrentText()))
						{
							if(it.previous() && it.current().id == Token.MARKUP
								&& "<".equals(it.getCurrentText()))
							{
									stateBefore = ELEMENT_CLOSE_COMPLETE;
							}
						}
						else
						{
							if(it.current().id == Token.OPERATOR
								&& it.current().length == 1
								&& ":".equals(it.getCurrentText()))
							{
								nameToComplete = ":" + nameToComplete;
								if(!it.previous())
								{
									System.err.println("nothing before : ??");
								}
							}								
								
							if(it.current().id == Token.LABEL)
							{
								nameToComplete = it.getCurrentText() + nameToComplete;
								if(it.previous())
								{
									String image2 = it.getCurrentText();
									if(it.current().id == Token.MARKUP
										&& "<".equals(image2))
									{
											System.err.println("at qualified element name");
											stateBefore = ELEMENT_COMPLETE;
									}
									if(it.current().id == Token.MARKUP
										&& "/".equals(image2))
									{
										if(it.previous() && it.current().id == Token.MARKUP
											&& it.getCurrentText().equals("<"))
										{
											stateBefore = ELEMENT_CLOSE_COMPLETE;
										}
									}
									else if(it.current().id == Token.MARKUP
										&& "&".equals(image2))
									{
										System.err.println("error: & in markup !");
									}
									else
									{
										System.err.println("at qualified attribute name");
										stateBefore = ATTRIB_COMPLETE;
									}
								}
							}else{
									System.err.println("at attribute name");
									stateBefore = ATTRIB_COMPLETE;
							}
						}
					}
				}else if(0x20 == lastChar
					|| 0x9 == lastChar
					|| 0xd == lastChar
					|| 0xa == lastChar)
				{
					// could also be element complete but
					// we don't propose element complete until < is hit
					stateBefore=ATTRIB_COMPLETE;
				}
			}
		}
		else if(token.id == Token.LITERAL2)
		{
			nameToComplete = it.getCurrentText();
			if(";".equals(nameToComplete))
			{
				System.err.println("after entity ref");
			}
			else
			{
				// entity ref not in TAG zone
				stateBefore=ENTITY_COMPLETE;
				// just typed & => the token is &
				// when another letter is after &, the token is the letter
				if("&".equals(nameToComplete))nameToComplete = "";
				System.err.println("ENTITYComplete!");
			}
		}
		else if(token.id == Token.KEYWORD2)
		{
			// DTD or begin CDATA or begin comment
			String image = it.getCurrentText();
			if(token.length == 1 && ("-".equals(image)||"[".equals(image))
				&& it.previous()
				&& it.current().id == Token.KEYWORD2 && "<!".equals(it.getCurrentText()))
			{
				if("-".equals(image))
				{
					stateBefore = COMMENT_COMPLETE;
					word = "!-";
				}
				else
				{
					stateBefore = CDATA_COMPLETE;
					word = "![";
				}
			}
			else if(token.length == 2 && "<!".equals(image))
			{
				stateBefore = COMMENT_OR_CDATA_COMPLETE;
				word = "!";
			}
		}
		else // will accepts entity completion inside comments and CDATA 
		{
			String image = it.getCurrentText();
			int caretPosInImage = lastchar - it.getCurrentCaret();
			if(caretPosInImage>=0 && caretPosInImage < image.length())
			{
				image = image.substring(0,caretPosInImage+1);
			}
			Matcher m = pEntityRef.matcher(image);
			if(m.matches())		// FIXME: refine pattern, test with XSLT 
			{
				stateBefore=ENTITY_COMPLETE;
				System.err.println("ENTITYComplete in literal !");
				nameToComplete = m.group(1);
			}
			else if("<!--".equals(image))
			{
				System.err.println("end of comment opening");
				stateBefore = COMMENT_COMPLETE;
				word = "!--";
			}
			else if("CDATA".startsWith(image) && it.previous() && it.current().id == Token.KEYWORD2)
			{
				stateBefore = CDATA_COMPLETE;
				word = "!["+image;
			}
		}
		System.err.println("stateBefore = "+((char)stateBefore)+", nameToComplete = "+nameToComplete+", word="+word);
		
		if(word.length() == 0) // not a comment or cdata
		{
			word = nameToComplete;
		}
		// }}}
		
		// {{{ comment, entity or cdata : no parent required to complete
		if(COMMENT_COMPLETE == stateBefore)
		{
			List allowedCompletions = Collections.singletonList(new XmlListCellRenderer.Comment());
			//System.err.println("returning : "+ allowedCompletions);
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
		}
		else if(CDATA_COMPLETE == stateBefore)
		{
			List allowedCompletions = Collections.singletonList(new XmlListCellRenderer.CDATA());
			//System.err.println("returning :" + allowedCompletions);
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
		}
		else if(COMMENT_OR_CDATA_COMPLETE == stateBefore)
		{
			List allowedCompletions = new ArrayList(2);
			allowedCompletions.add(new XmlListCellRenderer.Comment());
			allowedCompletions.add(new XmlListCellRenderer.CDATA());
			//System.err.println("returning :" + allowedCompletions);
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
		}
		else if(ENTITY_COMPLETE ==  stateBefore)
		{
			List allowedCompletions = new ArrayList(20);
			List<EntityDecl> completions = data.entities;
			for(int i = 0; i < completions.size(); i++)
			{
				EntityDecl entity = completions.get(i);
				if(entity.name.startsWith(nameToComplete))
				{
					allowedCompletions.add(entity);
				}
			}
			System.err.println("returning: "+allowedCompletions);
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
		}
		else if(0 == stateBefore)
		{
			System.err.println("nothing found to complete !");
			return null;
		}
		// }}}
		
		// {{{ find if thing to complete is inside tag
		String state = null;
		boolean foundSlash = false;
		while(state == null && it.previous())
		{
			if(it.current().id == Token.MARKUP
				&& it.current().length == 1)
			{
				String currentText = it.getCurrentText();
				if("/".equals(currentText))
				{
					foundSlash = true;
				}
				if("<".equals(currentText))
				{
					if(foundSlash)
					{
						state = "</";
					}
					else
					{
						state = "<";
					}
				}
				else if(">".equals(currentText))
				{
					state = ">";
				}
			}
			foundSlash = false;
		}
		
		if(state == null)
		{
			System.err.println("at root tag");
		}
		else if(state == ">")
		{
			System.err.println("inside element");
		}
		else if(state == "<")
		{
			System.err.println("inside tag");
		}
		else if(state == "</")
		{
			System.err.println("inside closing tag");
		}
		// }}}
		
		// {{{ check coherence of context and completion wanted
		if(ELEMENT_COMPLETE == stateBefore)
		{
			if(state == ">" || state == null)
			{
				System.err.println("COMPLETE ELEMENT NAMED "+nameToComplete);
			}
			else
			{
				System.err.println("DON'T COMPLETE ELEMENT NAMED "+nameToComplete);
				return null;
			}
			
		}
		else if(ATTRIB_COMPLETE == stateBefore)
		{
			if(state == "<")
			{
				System.err.println("COMPLETE ATTRIBUTE NAMED "+nameToComplete);
			}
			else
			{
				System.err.println("DON'T COMPLETE ATTRIBUTE NAMED "+nameToComplete);
				return null;
			}
		}
		else if(LESS_THAN_COMPLETE == stateBefore)
		{
			if(state == ">" || state == null)
			{
				System.err.println("COMPLETE ANON ELEMENT");
			}
			else
			{
				System.err.println("DON'T COMPLETE ANON ELEMENT");
				return null;
			}
		}
		//}}}
		
		// {{{ find parent name
		BufferTagParser.Tag parentTag = null;

		if(state == ">")
		{
			it.next();
			parentTag = BufferTagParser.findLastOpenTag(buffer,it, it.getCurrentCaret(), data);
		}
		else if(state == "<")
		{
			// already inside parent...
			parentTag = new BufferTagParser.Tag(it.getCurrentCaret(),-1);
			parentTag.type = BufferTagParser.T_START_TAG;
			it.next();
			String parentName = it.getCurrentText();
			if(it.current().id == Token.LABEL)
			{
				// sloppy here...
				it.next();
				parentName += ":";
				it.next();
				parentName += it.getCurrentText();
			}
			parentTag.end = it.getCurrentCaret() + it.current().length;
			parentTag.tag = parentName;
		}
		
		if(parentTag == null)
		{
			System.err.println("COULDN't FIND PARENT");
		}
		else
		{
			System.err.println("PARENT IS : "+parentTag);
		}
		//}}}
		
		//{{{ find what to return 
		if(ELEMENT_COMPLETE  == stateBefore)
		{
			List<ElementDecl> allowedCompletions = data.getAllowedElements(buffer,caret,nameToComplete,parentTag);
			//System.err.println("elementName returning: "+allowedCompletions);
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
		}
		else if(LESS_THAN_COMPLETE == stateBefore)
		{
			List allowedCompletions = data.getAllowedElements(buffer,caret,"",parentTag);
			allowedCompletions.add(new XmlListCellRenderer.Comment());
			allowedCompletions.add(new XmlListCellRenderer.CDATA());
			if(parentTag != null)
			{
				allowedCompletions.add(new XmlListCellRenderer.ClosingTag(parentTag.tag));
			}
			//System.err.println("returning: "+allowedCompletions);
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
		}
		else if(ATTRIB_COMPLETE == stateBefore)
		{
			ElementDecl decl = data.getElementDecl(parentTag.tag,parentTag.start);
			System.err.println("decl="+decl);
			List<AttributeDecl> completions;
			List allowedCompletions = new ArrayList(20);
			if (decl != null)
			{
				completions = decl.attributes;
				Map<String,String> namespaces = data.getNamespaceBindings(data.getTreePathForPosition(caret));
				for (int i=0; i<completions.size(); ++i) 
				{
					AttributeDecl attrDecl = completions.get(i);
					String attrName;
					if(attrDecl.namespace == null)
					{
						attrName = attrDecl.name;
					}
					else
					{
						String pre = namespaces.get(attrDecl.namespace);
						if(pre == null)
						{
							if(attrDecl.namespace.equals(NamespaceSupport.XMLNS))
							{
								attrName = "xml:"+attrDecl.name;
							}
							else
							{
								attrName = attrDecl.name;
							}
						}
						else
						{
							attrName = pre + ":" + attrDecl.name;
						}
					}
					if (attrName.startsWith(nameToComplete))
					{
						AttributeDecl newDecl = attrDecl.copy();
						newDecl.name = attrName;
						allowedCompletions.add(newDecl);
					}
				}
			}
			//System.err.println("returning: "+allowedCompletions);
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
		}
		else if(ELEMENT_CLOSE_COMPLETE == stateBefore)
		{
			if(parentTag == null)
			{
				System.err.println("nothing to close");
				return null;
			}
			else if(parentTag.tag.startsWith(nameToComplete))
			{
				if(nameToComplete.length() == 0 
					&& jEdit.getBooleanProperty("xml.close-complete"))
				{
					XmlActions.completeClosingTag(
						editPane.getView(),
						data,
						parentTag.tag,
						false);
					return null;
				}
				else
				{
					List allowedCompletions = Collections.singletonList(new XmlListCellRenderer.ClosingTag(parentTag.tag));
					//System.err.println("returning : " + allowedCompletions);
					return new XmlCompletion(editPane.getView(),allowedCompletions,word,data);
				}
			}
			else
			{
				System.err.println("parent tag doesn't match closing prefix");
				return null;
			}
		}
		else
		{
			//System.err.println("returning null");
			return null;
		}
		//}}}
		
	} //}}}

	//{{{ Package-private members
	boolean stopped;
	//}}}

	//{{{ Private members
	private TagHighlight highlight;
	//}}}
}
