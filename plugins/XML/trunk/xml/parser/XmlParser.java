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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.helpers.NamespaceSupport;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import sidekick.*;
import xml.completion.*;
import xml.completion.ElementDecl.AttributeDecl;
import xml.*;
import xml.XmlListCellRenderer.WithLabel;
import javax.swing.text.Segment;

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
		// I set it to true by default and will run it for some time, see if it's satisfying
		tryModesSuperSet = jEdit.getBooleanProperty("xml.try-modes-superset",true);
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
	// This relies on the actual token for supported modes (currently xml, xsl, ant, maven, html, tld)
	// and the supported mode names.
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
	// Additional fixes:
	// enable attribute completion after attribute values that contain /
	// enable attribute completion for elements that span multiple lines
	//
	// For testing purposes, manually invoking completion from
	// Plugins > SideKick > Show Completion Popup seems to display
	// more exceptions in the code than keystroke activation
	//
	// Greg Knittl 2009-06-19
	// (edited Eric Le Lay 2012-08-26)
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
		int lineStart = buffer.getLineStartOffset(lastcharLine);
		
		// get syntax tokens for the line of character before the caret
		// ghk not sure if this duplicates jEdits syntax tokenization
		// since this is per keystroke, performance is of some importance
		DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
		buffer.markTokens(lastcharLine,tokenHandler);
		Token token = tokenHandler.getTokens();
		// save last < opening a tag
		Token openTag = null;
		// save last 5 tokens : '<'  '/'  prefix  ':'  tagname
		TokenRingBuffer lastTokens = new TokenRingBuffer(5);
		while(token.id != Token.END)
		{
			int next = lineStart + token.length;
			
			lastTokens.write(token);

			if (lineStart <= lastchar && next > lastchar)
				break;
			
			
			// save last <
			if(token.length == 1
					&& (token.id == Token.MARKUP || token.id == Token.KEYWORD2)
					&& buffer.getSegment(lineStart, token.length).charAt(0) == '<')
			{
				openTag = token;
			}
			
			lineStart = next;
			token = token.next;
		}
		
		// special case for completion at start of line:
		// can't read whitespace followed by word to trigger attribute complete,
		// so use rule context from line before.
		// It's hard-coded for every supported mode.
		lineStart = buffer.getLineStartOffset(buffer.getLineOfOffset(caret));
		boolean caretIsAtLineStart = lineStart == caret;
		
		String modename = buffer.getMode().getName();
		
		lineStart = buffer.getLineStartOffset(lastcharLine);
		lastTokens.mark();
		Context ctx = new Context();
		
		if(caretIsAtLineStart){
			String ruleSet = tokenHandler.getLineContext().rules.getName();
			if("xml".equals(modename)){
				if("xml::TAGS".equals(ruleSet)){
					ctx.mode = ATTRIB_COMPLETE;
					ctx.attribStart = caret;
				}
			}else if("xsl".equals(modename)){
				if("xsl::TAGS".equals(ruleSet) || "xsl::XSLTAGS".equals(ruleSet)){
					ctx.mode = ATTRIB_COMPLETE;
					ctx.attribStart = caret;
				}
			}else if("ant".equals(modename)){
				if("ant::TAGS".equals(ruleSet)){
					ctx.mode = ATTRIB_COMPLETE;
					ctx.attribStart = caret;
				}
			}else if("maven".equals(modename)){
				if("maven::TAGS".equals(ruleSet)){
					ctx.mode = ATTRIB_COMPLETE;
					ctx.attribStart = caret;
				}
			}else if("tld".equals(modename)){
				if("tld::TAGS".equals(ruleSet)){
					ctx.mode = ATTRIB_COMPLETE;
					ctx.attribStart = caret;
				}
			}else if("html".equals(modename)){
				if("html::TAGS".equals(ruleSet)){
					ctx.mode = ATTRIB_COMPLETE;
					ctx.attribStart = caret;
				}
			}
		}else{
			
			// Taking the union of every mode is shorter.
			// Then it would work for other modes reusing xml TAGS rules.
			// see for instance javadoc in java file.
			// Yes, you have to set the parser as xml for this to work, which results immediately in an error!
			// But it is still seducing...
			if(tryModesSuperSet){
				byte[] LT_TYPE = {Token.MARKUP, Token.KEYWORD2};
				byte[] WS_TYPE = {Token.MARKUP, Token.KEYWORD2};
				byte[] WORD_TYPE = {Token.MARKUP, Token.KEYWORD1, Token.KEYWORD2, Token.KEYWORD3, Token.KEYWORD4, Token.OPERATOR, Token.FUNCTION};
				byte[] COLON_TYPE = {Token.OPERATOR, Token.LABEL, Token.MARKUP};
				byte[] PREFIX_TYPE = {Token.LABEL, Token.KEYWORD1, Token.KEYWORD2, Token.KEYWORD3, Token.KEYWORD4, Token.OPERATOR, Token.FUNCTION, Token.MARKUP};
				byte[] ENT_TYPE = {Token.LITERAL2};
				ctx = getMode(lastTokens, buffer, lineStart,
						LT_TYPE, WS_TYPE, WORD_TYPE, COLON_TYPE, PREFIX_TYPE, ENT_TYPE); 
			}else{
				if("xml".equals(modename) || "xsl".equals(modename)){
					byte[] LT_TYPE = {Token.MARKUP};
					byte[] WS_TYPE = {Token.MARKUP};
					byte[] WORD_TYPE = {Token.MARKUP};
					byte[] COLON_TYPE = {Token.OPERATOR};
					byte[] PREFIX_TYPE = {Token.LABEL};
					byte[] ENT_TYPE = {Token.LITERAL2};
					ctx = getMode(lastTokens, buffer, lineStart,
							LT_TYPE, WS_TYPE, WORD_TYPE, COLON_TYPE, PREFIX_TYPE, ENT_TYPE); 
				}
				if(ctx.mode == -1 && "xsl".equals(modename)){
					lastTokens.reset();
					byte[] LT_TYPE = {Token.KEYWORD2};
					byte[] WS_TYPE = {Token.KEYWORD2};
					byte[] WORD_TYPE = {Token.KEYWORD1,Token.KEYWORD2};
					byte[] COLON_TYPE = {Token.LABEL};
					byte[] PREFIX_TYPE = {Token.LABEL};
					byte[] ENT_TYPE = {Token.LITERAL2};
					ctx = getMode(lastTokens, buffer, lineStart,
							LT_TYPE, WS_TYPE, WORD_TYPE, COLON_TYPE, PREFIX_TYPE, ENT_TYPE); 
				}
				if(ctx.mode  == -1 && "ant".equals(modename)){
					//note: last word can contain =
					lastTokens.reset();
					byte[] LT_TYPE = {Token.MARKUP};
					byte[] WS_TYPE = {Token.MARKUP};
					byte[] WORD_TYPE = {Token.MARKUP, Token.KEYWORD1, Token.KEYWORD2, Token.KEYWORD3, Token.KEYWORD4, Token.OPERATOR, Token.FUNCTION};
					byte[] COLON_TYPE = {Token.LABEL};
					byte[] PREFIX_TYPE = {Token.LABEL, Token.KEYWORD1, Token.KEYWORD2, Token.KEYWORD3, Token.KEYWORD4, Token.OPERATOR, Token.FUNCTION};
					byte[] ENT_TYPE = {Token.LITERAL2};
					ctx = getMode(lastTokens, buffer, lineStart,
							LT_TYPE, WS_TYPE, WORD_TYPE, COLON_TYPE, PREFIX_TYPE, ENT_TYPE); 
				}
				if(ctx.mode == -1 && "html".equals(modename)){
					byte[] LT_TYPE = {Token.MARKUP};
					byte[] WS_TYPE = {Token.MARKUP};
					byte[] WORD_TYPE = {Token.MARKUP, Token.KEYWORD1};
					byte[] COLON_TYPE = {Token.MARKUP};
					byte[] PREFIX_TYPE = {Token.MARKUP, Token.KEYWORD1};
					byte[] ENT_TYPE = {Token.LITERAL2};
					lastTokens.reset();
					ctx = getMode(lastTokens, buffer, lineStart,
							LT_TYPE, WS_TYPE, WORD_TYPE, COLON_TYPE, PREFIX_TYPE, ENT_TYPE); 
				}
				if(ctx.mode == -1 && "maven".equals(modename)){
					//note: last word can contain =
					byte[] LT_TYPE = {Token.MARKUP};
					byte[] WS_TYPE = {Token.MARKUP};
					byte[] WORD_TYPE = {Token.MARKUP, Token.KEYWORD1, Token.KEYWORD2, Token.KEYWORD3};
					byte[] COLON_TYPE = {Token.LABEL};
					byte[] PREFIX_TYPE = {Token.LABEL, Token.KEYWORD1, Token.KEYWORD2, Token.KEYWORD3};
					byte[] ENT_TYPE = {Token.LITERAL2};
					lastTokens.reset();
					ctx = getMode(lastTokens, buffer, lineStart,
							LT_TYPE, WS_TYPE, WORD_TYPE, COLON_TYPE, PREFIX_TYPE, ENT_TYPE); 
				}
				if(ctx.mode == -1 && "tld".equals(modename)){
					//note: last word can contain =
					byte[] LT_TYPE = {Token.MARKUP};
					byte[] WS_TYPE = {Token.MARKUP};
					byte[] WORD_TYPE = {Token.MARKUP, Token.KEYWORD1, Token.KEYWORD2};
					byte[] COLON_TYPE = {Token.LABEL};
					byte[] PREFIX_TYPE = {Token.LABEL, Token.KEYWORD1, Token.KEYWORD2};
					byte[] ENT_TYPE = {Token.LITERAL2};
					lastTokens.reset();
					ctx = getMode(lastTokens, buffer, lineStart,
							LT_TYPE, WS_TYPE, WORD_TYPE, COLON_TYPE, PREFIX_TYPE, ENT_TYPE); 
				}
			}
		}
		
		// fix for modes not highlighting entities in attribute values (see patch #3559971)
		if(ctx.mode == -1){
			int start = Math.max(lineStart, caret - 100);
			Matcher m = entP.matcher(buffer.getSegment(start, caret - start));
			if(m.matches()){
				ctx.mode = ENTITY_COMPLETE;
				ctx.wordStart = start + m.start(1);
			}
		}

		// comments and CDATA sections are not recognised using the syntax highlighting
		// because this would be to complex to make them work inside element tag (another 5,6 cases)
		// or within themselves (that is illegal, but useful when editing). 
		if(ctx.mode == -1){
			int start = Math.max(lineStart, caret - 100);
			Matcher m = commentCDATAP.matcher(buffer.getSegment(start, caret - start));
			if(m.matches()){
				ctx.mode = ELEMENT_COMPLETE;
				ctx.wordStart = start + m.start(1);
			}
		}
		
		String closingTag = null;
		String word;

		List allowedCompletions = new ArrayList(20);
		NamespaceBindings namespaces = data.getNamespaceBindings(caret);
		NamespaceBindings namespacesToInsert = new NamespaceBindings();
		NamespaceBindings localNamespacesToInsert = new NamespaceBindings();
		
		
		if(ctx.mode != -1)
		{
			if(ctx.wordStart == -1){
				// attrib=>find element!
				if(openTag!=null){
					ctx.wordStart = lineStart+openTag.offset+openTag.length;
					String tolastchar = buffer.getSegment(ctx.wordStart, caret-ctx.wordStart).toString();
					if (tolastchar.trim().length() == 0)
						word = "";
					else
					{
						String firstSpace = tolastchar.split("\\s")[0];
						if (firstSpace.length() > 0)
							word = firstSpace;
						else
							word = buffer.getSegment(ctx.wordStart, caret-ctx.wordStart).toString();
					}
				}else{
					int start = TagParser.lastIndexOf(buffer.getSegment(0, lastchar),'<',lastchar - 1);
					if(start < 0){
						throw new IllegalStateException("can't find opening of tag??");
					}else{
						ctx.wordStart = start+1; // +1 because start is index of '<' and we only want the tag name
						int end = Math.min(start+100, buffer.getLineEndOffset(buffer.getLineOfOffset(start))); // assume tag name is less than 100 chars
						word = buffer.getText(start+1,end-start + 1).split("\\s")[0];
					}
				}
			}else{
				word = buffer.getSegment(ctx.wordStart, caret - ctx.wordStart).toString();
			}

			if(ctx.mode == ELEMENT_COMPLETE)
			{
				String wordWithoutPrefix = XmlParsedData.getElementLocalName(word);
				String wordPrefix = XmlParsedData.getElementNamePrefix(word);
				List<ElementDecl> completions = data.getAllowedElements(buffer, lastchar);
				TagParser.Tag tag = TagParser.findLastOpenTag(buffer.getSegment(0, lastchar - 1), lastchar-1,data);
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
						// this code is very similar to EditTagDialog.composeName()
						// difference is how we take into account unknown prefix and have namespacesToInsert and localNamespacesToInsert
						String pre = namespaces.getPrefix(elementNamespace);
						if(pre == null)
						{
							pre = localNamespacesToInsert.getPrefix(elementNamespace);
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
								pre = NamespaceBindings.generatePrefix(namespaces, localNamespacesToInsert);
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
			else if (ctx.mode == ENTITY_COMPLETE)
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
			else if (ctx.mode == ATTRIB_COMPLETE) 
			{
				// word contains element name
				// prefix contains what was typed of the attribute
				String prefix = buffer.getText(ctx.attribStart, caret - ctx.attribStart);
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
						
						// this code is very similar to ELEMENT_COMPLETE case
						// difference is how we take into account xml namespace (always use xml: prefix)
						if(attrDecl.namespace == null || "".equals(attrDecl.namespace))
						{
							attrName = attrDecl.name;
						}
						else
						{
							String pre = namespaces.getPrefix(attrDecl.namespace);
							// for attributes, empty prefix means no namespace, not current default namespace, so generate...
							if(pre == null || "".equals(pre))
							{
								pre = localNamespacesToInsert.getPrefix(attrDecl.namespace);
							}
							if(pre == null || "".equals(pre))
							{
								if(attrDecl.namespace.equals(NamespaceSupport.XMLNS))
								{
									attrName = "xml:"+attrDecl.name;
								}
								else
								{
									attrName = attrDecl.name;
									// handle using unknown prefix
									// if users types "xl:" and xlink ns is undeclared use xl as prefix (xl:href for instance)
									if(!"".equals(wordPrefix) && !"xml".equals(wordPrefix)
											&& attrName.startsWith(wordWithoutPrefix))
									{
										pre = wordPrefix;
										namespacesToInsert.put(attrDecl.namespace, pre);
										localNamespacesToInsert.put(attrDecl.namespace, pre);
										attrName = pre + ":" + attrDecl.name;
									}
									else
									{
										// handle attribute in undeclared namespace and no prefix.
										// Generate a new prefix.
										// Store it locally, so that the declaration is not inserted when this completion is not chosen.
										// If it's chosen, a prefix (maybe different) will be generated again
										pre = NamespaceBindings.generatePrefix(namespaces, localNamespacesToInsert);
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

	private static class Context {
		int mode = -1;
		int wordStart = -1;
		int attribStart = -1;
	}
	
	private final char lt = '<';
	private final char colon = ':';
	private final char amp = '&';
	private final char slash = '/';
	private final Pattern ws = Pattern.compile("\\s+");
	private final Pattern wordP = Pattern.compile("[\\w-_]+(=[\"']?)?");// = to match attribute names in xsl mode where = is part of the name token
	private final Pattern entP = Pattern.compile(".*&([\\w_-]*)$");
	private final Pattern commentCDATAP = Pattern.compile(".*<((!-?-?)|(!\\[(C(D(A(T(A\\[?)?)?)?)?)?))");
	private Context getMode(TokenRingBuffer lastTokens, Buffer buffer, int lineStart,
				byte[] LT_TYPE, byte[] WS_TYPE, byte[] WORD_TYPE, byte[] COLON_TYPE, byte[] PREFIX_TYPE,
				byte[] ENT_TYPE){
		Context ctx = new Context();
		if(!lastTokens.isEmpty()){
			Token last = lastTokens.popLast();
			CharSequence img = buffer.getSegment(lineStart+last.offset, last.length);
			if(matches(last,LT_TYPE, img, lt)){
				ctx.mode = ELEMENT_COMPLETE;
				ctx.wordStart = lineStart+last.offset+last.length;
			}else if(matches(last,WS_TYPE,img, ws)){
				ctx.mode = ATTRIB_COMPLETE;
				ctx.attribStart = lineStart+last.offset+last.length;
			}else if(matches(last,LT_TYPE,img, slash)){
				if(!lastTokens.isEmpty()){
					last = lastTokens.popLast();
					img = buffer.getSegment(lineStart+last.offset, last.length);
					if(matches(last,LT_TYPE,img, lt)){
						ctx.mode = ELEMENT_COMPLETE;
						ctx.wordStart = lineStart+last.offset+last.length;
					}
				}
			}else if(matches(last,WORD_TYPE, img, wordP)){
				if(!lastTokens.isEmpty()){
					last = lastTokens.popLast();
					img = buffer.getSegment(lineStart+last.offset, last.length);
					if(matches(last,LT_TYPE,img, lt)){
						ctx.mode = ELEMENT_COMPLETE;
						ctx.wordStart = lineStart+last.offset+last.length;
					}else if(matches(last,WS_TYPE,img, ws)){
						ctx.mode = ATTRIB_COMPLETE;
						ctx.attribStart = lineStart+last.offset+last.length;
					}else if(matches(last,LT_TYPE,img, slash)){
						if(!lastTokens.isEmpty()){
							last = lastTokens.popLast();
							img = buffer.getSegment(lineStart+last.offset, last.length);
							if(matches(last,LT_TYPE,img, lt)){
								ctx.mode = ELEMENT_COMPLETE;
								ctx.wordStart = lineStart+last.offset+last.length;
							}
						}
					}else if(matches(last, COLON_TYPE, img, colon)){
						if(!lastTokens.isEmpty()){
							last = lastTokens.popLast();
							img = buffer.getSegment(lineStart+last.offset, last.length);
							if(matches(last, PREFIX_TYPE, img, wordP)){
								if(!lastTokens.isEmpty()){
									last = lastTokens.popLast();
									img = buffer.getSegment(lineStart+last.offset, last.length);
									if(matches(last,LT_TYPE, img, lt)){
										ctx.mode = ELEMENT_COMPLETE;
										ctx.wordStart = lineStart+last.offset+last.length;
									}else if(matches(last,WS_TYPE,img, ws)){
										ctx.mode = ATTRIB_COMPLETE;
										ctx.attribStart = lineStart+last.offset+last.length;
									}else if(matches(last,LT_TYPE,img, slash)){
										if(!lastTokens.isEmpty()){
											last = lastTokens.popLast();
											img = buffer.getSegment(lineStart+last.offset, last.length);
											if(matches(last,LT_TYPE,img, lt)){
												ctx.mode = ELEMENT_COMPLETE;
												ctx.wordStart = lineStart+last.offset+last.length;
											}
										}
									}
								}
							}
						}
					}
				}else{
					// word at beginning of line => attribute
					ctx.mode = ATTRIB_COMPLETE;
					ctx.attribStart = lineStart;
				}
			}else if(matches(last, COLON_TYPE,img, colon)){
				if(!lastTokens.isEmpty()){
					last = lastTokens.popLast();
					img = buffer.getSegment(lineStart+last.offset, last.length);
					if(matches(last, PREFIX_TYPE, img, wordP)){
						if(!lastTokens.isEmpty()){
							last = lastTokens.popLast();
							img = buffer.getSegment(lineStart+last.offset, last.length);
							if(matches(last,LT_TYPE,img, lt)){
								ctx.mode = ELEMENT_COMPLETE;
								ctx.wordStart = lineStart+last.offset+last.length;
							}else if(matches(last,WS_TYPE,img, ws)){
								ctx.mode = ATTRIB_COMPLETE;
								ctx.attribStart = lineStart+last.offset+last.length;
							}else if(matches(last,LT_TYPE,img, slash)){
								if(!lastTokens.isEmpty()){
									last = lastTokens.popLast();
									img = buffer.getSegment(lineStart+last.offset, last.length);
									if(matches(last,LT_TYPE,img, lt)){
										ctx.mode = ELEMENT_COMPLETE;
										ctx.wordStart = lineStart+last.offset+last.length;
									}
								}
							}
						}
					}
				}
			}else if(matches(last,ENT_TYPE, img, amp)){
				ctx.mode = ENTITY_COMPLETE;
				ctx.wordStart = lineStart+last.offset+last.length;
			}else if(matches(last,ENT_TYPE, img, wordP)){
				if(!lastTokens.isEmpty()){
					last = lastTokens.popLast();
					img = buffer.getSegment(lineStart+last.offset, last.length);
					if(matches(last, ENT_TYPE, img, amp)){
						ctx.mode = ENTITY_COMPLETE;
						ctx.wordStart = lineStart+last.offset+last.length;
					}
				}
			}
		}	
		return ctx;
	}
		
	 private boolean matches(Token last, byte[] types, CharSequence img, char wanted) {
		 if(last.length != 1)return false;
		 boolean okType = false;
		 for(int i=0;i<types.length;i++){
			 if(last.id == types[i]){
				 okType = true;
				 break;
			 }
		 }
		 return okType
				 && img.charAt(0) == wanted;
	}

	 private boolean matches(Token last, byte[] types, CharSequence img, Pattern wanted) {
		 boolean okType = false;
		 for(int i=0;i<types.length;i++){
			 if(last.id == types[i]){
				 okType = true;
				 break;
			 }
		 }
		 return okType
				 && wanted.matcher(img).matches();
	}

	private boolean isWord(CharSequence s) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isWS(CharSequence s) {
		// TODO Auto-generated method stub
		return false;
	}

	static class TokenRingBuffer {
		private final Token[] tokens;
		public final int size;
		private int start = 0;
		private int end = 0;
		private int savedStart = -1;
		private int savedEnd = -1;
		
		public TokenRingBuffer(int size) {
			this.size = size;
			tokens = new Token[size+1];
		}
		
		public boolean isEmpty(){
			return start == end;
		}
		
		public void write(Token t){
			tokens[end] = t;
			end = (end + 1 ) % tokens.length;
			if(start == end){
				start = (start + 1) % tokens.length;
			}
			savedStart = -1;
		}
		
		public Token popFirst(){
			Token ret = tokens[start];
			start = (start + 1) % tokens.length;
			return ret;
		}
		
		public Token popLast(){
			end = end -1;
			if(end < 0){
				end = tokens.length - 1;
			}
			return tokens[end];
		}
		
		public void mark(){
			savedStart = start;
			savedEnd = end;
		}
		
		public boolean reset(){
			if(savedStart == -1)return false;
			else {
				start = savedStart;
				end = savedEnd;
				return true;
			}
		}
	}
	
	//{{{ Package-private members
	boolean stopped;
	//}}}

	//{{{ Private members
	private StructureMatcher highlight;
	private StructureMatcher htmlHighlight;
	private boolean tryModesSuperSet;
	//}}}
}
