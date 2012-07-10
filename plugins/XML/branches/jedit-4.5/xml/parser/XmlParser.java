/*
 * XmlParser.java
 * :tabSize=4:indentSize=4:noTabs=false:
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
import org.xml.sax.helpers.NamespaceSupport;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import sidekick.*;
import xml.completion.*;
import xml.completion.ElementDecl.AttributeDecl;
import xml.*;
import xml.XmlListCellRenderer.WithLabel;

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

	//{{{ XmlParser constructor
	public XmlParser(String name)
	{
		super(name);
		String matcherName = jEdit.getProperty("xml.structure-matcher","sidekick");
		if("old".equals(matcherName)) {
			highlight = new TagHighlight();
		} else {
			highlight = new SideKickTagHighlight();
		}
		htmlHighlight = new TagHighlight();
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
		{
			StructureMatcher h;
			// revert to classic TagHighlight for HTML modes
			if(editPane.getBuffer().getMode().getName().equals("html")
					|| editPane.getBuffer().getMode().getName().equals("jsp"))
			{
				h = htmlHighlight;
			}
			else
			{
				h = highlight;
			}
			editPane.getTextArea().addStructureMatcher(h);
		}
	} //}}}

	//{{{ deactivate() method
	public void deactivate(EditPane editPane)
	{
		// don't bother to remember which one it was... 
		editPane.getTextArea().removeStructureMatcher(highlight);
		editPane.getTextArea().removeStructureMatcher(htmlHighlight);
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
		XmlParsedData data = XmlParsedData.getParsedData(editPane.getView(), false);
		if(data==null)return null;

		if(XmlPlugin.isDelegated(editPane.getTextArea()))
			return null;

		Buffer buffer = editPane.getBuffer();
		
		int lastchar = caret - 1;
		int lastcharLine = buffer.getLineOfOffset(lastchar);
		String text = buffer.getText(0,caret);
		int lineStart = buffer.getLineStartOffset(lastcharLine);
		
		// get syntax tokens for the line of character before the caret
		// ghk not sure if this duplicates jEdits syntax tokenization
		// since this is per keystroke, performance is of some importance
		DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
		buffer.markTokens(lastcharLine,tokenHandler);
		Token token = tokenHandler.getTokens();

		while(token.id != Token.END)
		{
			int next = lineStart + token.length;
			if (lineStart <= lastchar && next > lastchar)
				break;
			lineStart = next;
			token = token.next;
		}
		
		// could test for comments and return at this point
		// continuing allows some completion within comments
		// for example when comments contain lines of valid xml
		
		String modename = buffer.getMode().getName();
		
		int mode = -1;
		boolean insideQuote = false;
		int wordStart = -1;
		int attribStart = -1;
		// iterate backwards towards start of file to find a tag
		// or the & indicating the start of an entity
		// i >= 1 enables attribute completion for elements spanning multiple lines
		for(int i = lastchar; i >= 1; i--)
		{
			char ch = text.charAt(i);
			if(ch == '>') 
				return null; // completely outside a tag
			if(ch == '<')
			{
				wordStart = i;
				if (mode == -1) 
					mode = ELEMENT_COMPLETE;
				break;
			}
			if(ch == '&')
			{
				// & and ATTRIBUTE_COMPLETE is invalid because it implies whitespace in the entity
				// can occur with attributes enclosed by misinterpreted 's that contain an entity followed by a space
				if (mode == ATTRIB_COMPLETE) 
					return null;
				wordStart = i;
				if (mode == -1) 
					mode = ENTITY_COMPLETE;
				break;
			}
			// " in text area or ' delimiting attribute values break this logic
			// xslt often uses multiple levels of quotes for XPath
			else if (ch == '"') 
			{
				insideQuote = !insideQuote;
			}
			// whitespace is not allowed in element tag names or entities;
			// in xml mode attributes can only occur in Token.MARKUP or Token.END (or Token.OPERATOR when just typing the colon)
			// this solves the problem of attributes defined with ' for xml mode
			// xsl mode parses the markup more finely so the logic gets more complex but probably could be done
			else if (Character.isWhitespace(ch) && !(token.id == Token.MARKUP || token.id == Token.END || (token.id == Token.OPERATOR && text.charAt(lastchar) == ':')) && modename.equals("xml")) {
			  	return null;
			}
			// whitespace is not allowed in element tags or entities;
			// no attributes allowed in text area (Token.NULL) or comments (Token.COMMENT1) so exit
			else if (Character.isWhitespace(ch) && (token.id == Token.NULL || token.id == Token.COMMENT1)) {
			  	return null;
			}
			// no break to allow loop to iterate back to find next < or &
			// add test for not Token.NULL (text area)
			else if (Character.isWhitespace(ch) && token.id != Token.NULL && !insideQuote && mode == -1) {
				attribStart = i+1;
				mode = ATTRIB_COMPLETE;
			}
		}
		if (insideQuote) mode=-1;
		String closingTag = null;
		String word;

		List allowedCompletions = new ArrayList(20);
		Map<String, String> namespaces = data.getNamespaceBindings(caret);
		Map<String, String> namespacesToInsert = new HashMap<String, String>();
		Map<String, String> localNamespacesToInsert = new HashMap<String, String>();
		
		
		if(wordStart != -1 && mode != -1)
		{
			String tolastchar = text.substring(wordStart + 1, caret);
			// avoid ArrayIndexOutOfBoundsException for < or & followed by one or more spaces
			if (tolastchar.trim().length() == 0)
				word = "";
			else
			{
				String firstSpace = tolastchar.split("\\s")[0];
				if (firstSpace.length() > 0)
					word = firstSpace;
				else
					word = text.substring(wordStart + 1, caret);
			}

			if(mode == ELEMENT_COMPLETE)
			{
				String wordWithoutPrefix = XmlParsedData.getElementLocalName(word);
				String wordPrefix = XmlParsedData.getElementNamePrefix(word);
				List<ElementDecl> completions = data.getAllowedElements(buffer, lastchar);
				TagParser.Tag tag = TagParser.findLastOpenTag(text,lastchar - 1,data);
				if(tag != null)
					closingTag = tag.tag;

				if("!--".startsWith(word))
					allowedCompletions.add(new WithLabel(new XmlListCellRenderer.Comment()));
				if(!data.html && "![CDATA[".startsWith(word))
					allowedCompletions.add(new WithLabel(new XmlListCellRenderer.CDATA()));
				if(closingTag != null && ("/" + closingTag).startsWith(word))
				{
					if(word.length() == 0 || !jEdit.getBooleanProperty("xml.close-complete"))
						allowedCompletions.add(new WithLabel(new XmlListCellRenderer.ClosingTag(closingTag)));
					else
					{
						// just insert immediately
						XmlActions.completeClosingTag(
							editPane.getView(),
							false);
						return null;
					}
				}

				for(int i = 0; i < completions.size(); i++)
				{
					ElementDecl elementDecl = completions.get(i);
					String elementName;
					String elementNamespace = elementDecl.completionInfo.namespace;
					// elementDecl.name is the local name, now we must find a qualified name
					if(elementNamespace == null || "".equals(elementNamespace))
					{
						elementName = elementDecl.name;
					}
					else
					{
						String pre = namespaces.get(elementNamespace);
						if(pre == null)
						{
							pre = localNamespacesToInsert.get(elementNamespace);
						}
						if(pre == null)
						{
							// handle using unknown prefix
							// if users types "<mathml:" and mathml ns is undeclared use mathml:... as prefix 
							if(!"".equals(wordPrefix)
									&& elementDecl.name.startsWith(wordWithoutPrefix))
							{
								pre = wordPrefix;
								namespacesToInsert.put(elementNamespace, pre);
								localNamespacesToInsert.put(elementNamespace, pre);
								elementName = pre + ":" + elementDecl.name;
								
							}
							else
							{
								// handle elements in undeclared namespace and no prefix.
								// Generate a new prefix.
								// Store it locally, so that the declaration is not inserted when this completion is not chosen.
								// If it's chosen, a prefix (maybe different) will be generated
								pre = EditTagDialog.generatePrefix(namespaces, localNamespacesToInsert);
								localNamespacesToInsert.put(elementNamespace,pre);
								elementName = pre + ":" + elementDecl.name;
							}
						}
						else
						{
							if("".equals(pre)){
								elementName = elementDecl.name;
							}else{
								elementName = pre + ":" + elementDecl.name;
							}
						}
					}
					
					if(elementName.startsWith(word)
							|| (data.html && elementName.toLowerCase()
							.startsWith(word.toLowerCase())))
					{
						allowedCompletions.add(new XmlListCellRenderer.WithLabel<ElementDecl>(elementName,elementDecl));
					}
				}
			}
			else if (mode == ENTITY_COMPLETE)
			{
				List<EntityDecl> completions = data.entities;
				for(int i = 0; i < completions.size(); i++)
				{
					EntityDecl entity = completions.get(i);
					if(entity.name.startsWith(word))
					{
						allowedCompletions.add(new WithLabel(entity));
					}
				}
			}
			else if (mode == ATTRIB_COMPLETE) 
			{
				String prefix = text.substring(attribStart, caret);
				String wordWithoutPrefix = XmlParsedData.getElementLocalName(prefix);
				String wordPrefix = XmlParsedData.getElementNamePrefix(prefix);
				ElementDecl decl = data.getElementDecl(word,caret);
				List<AttributeDecl> completions;
				if (decl != null)
				{
					completions = decl.attributes;
					for (int i=0; i<completions.size(); ++i) 
					{
						AttributeDecl attrDecl = completions.get(i);
						String attrName;
						if(attrDecl.namespace == null || "".equals(attrDecl.namespace))
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
									// handle using unknown prefix
									// if users types "<mathml:" and mathml ns is undeclared use mathml:... as prefix 
									if(!"".equals(wordPrefix) && !"xml".equals(wordPrefix)
											&& attrName.startsWith(wordWithoutPrefix))
									{
										pre = wordPrefix;
										namespacesToInsert.put(attrDecl.namespace, pre);
										attrName = pre + ":" + attrDecl.name;
									}
									else
									{
										// handle attribute in undeclared namespace and no prefix.
										// Generate a new prefix.
										// Store it locally, so that the declaration is not inserted when this completion is not chosen.
										// If it's chosen, a prefix (maybe different) will be generated again
										pre = EditTagDialog.generatePrefix(namespaces, localNamespacesToInsert);
										localNamespacesToInsert.put(attrDecl.namespace,pre);
										attrName = pre + ":" + attrDecl.name;
										// this can get cumbersome, if one types 'a' expecting to get 'someprefix:attribute' because
										// attribute will not be proposed. On the other hand if we don't put the prefix, one cannot distinguish between
										// ns1:attr and ns2:attr...
									}
								}
							}
							else
							{
								attrName = pre + ":" + attrDecl.name;
							}
						}
						if (attrName.startsWith(prefix))
						{
							allowedCompletions.add(new XmlListCellRenderer.WithLabel<AttributeDecl>(attrName,attrDecl));
						}
					}
				}
				word = prefix;
			}
			/* else if(mode == ID_COMPLETE)
			{
				else if(obj instanceof IDDecl)
				{
					IDDecl id = (IDDecl)obj;
					if(id.id.startsWith(word))
						allowedCompletions.add(id);
				}
			} */
		}
		else
			word = "";

		if(word.endsWith("/") && allowedCompletions.size() == 0)
			return null;
		else
			return new XmlCompletion(editPane.getView(),allowedCompletions, namespaces, namespacesToInsert, word,data,closingTag);
	} //}}}

	//{{{ Package-private members
	boolean stopped;
	//}}}

	//{{{ Private members
	private StructureMatcher highlight;
	private StructureMatcher htmlHighlight;
	//}}}
}
