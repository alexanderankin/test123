/*
 * XmlParser.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
 * Portions copyright (C) 2001 David Walend
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
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;
import sidekick.*;
import xml.completion.*;
import xml.parser.TagParser;
import xml.*;

public abstract class XmlParser extends SideKickParser
{
	public static final String INSTANT_COMPLETION_TRIGGERS = "/";
	public static final int ELEMENT_COMPLETE = '<';
	public static final int ENTITY_COMPLETE = '&';

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
	public SideKickCompletion complete(EditPane editPane, int caret)
	{
		SideKickParsedData _data = SideKickParsedData
			.getParsedData(editPane.getView());
		if(!(_data instanceof XmlParsedData))
			return null;
		if(XmlPlugin.isDelegated(editPane.getTextArea()))
			return null;

		XmlParsedData data = (XmlParsedData)_data;

		Buffer buffer = editPane.getBuffer();

		// first, we get the word before the caret
		List allowedCompletions = new ArrayList(20);

		int caretLine = buffer.getLineOfOffset(caret);
		String text = buffer.getText(0,caret);
		int lineStart = buffer.getLineStartOffset(caretLine);

		int mode = -1;
		int wordStart = -1;
		for(int i = caret - 1; i >= lineStart; i--)
		{
			char ch = text.charAt(i);
			if(ch == '<' || ch == '&')
			{
				wordStart = i;
				mode = (ch == '<' ? ELEMENT_COMPLETE
					: ENTITY_COMPLETE);
				break;
			}
			else if(ch == '/' && (i == 0 || text.charAt(i - 1) != '<'))
				return null;
		}

		String closingTag = null;
		String word;

		if(wordStart != -1 && mode != -1)
		{
			word = text.substring(wordStart + 1,caret);

			List completions;
			if(mode == ELEMENT_COMPLETE)
			{
				// Try to only list elements that are valid at the caret
				// position
				completions = data.getAllowedElements(buffer,wordStart);
			}
			else if(mode == ENTITY_COMPLETE)
				completions = data.getNoNamespaceCompletionInfo().entities;
			else
				throw new InternalError("Bad mode: " + mode);

			if(mode == ELEMENT_COMPLETE)
			{
				TagParser.Tag tag = TagParser.findLastOpenTag(text,caret - 2,data);
				if(tag != null)
					closingTag = tag.tag;

				if("!--".startsWith(word))
					allowedCompletions.add(new XmlListCellRenderer.Comment());
				if(!data.html && "![CDATA[".startsWith(word))
					allowedCompletions.add(new XmlListCellRenderer.CDATA());
				if(closingTag != null && ("/" + closingTag).startsWith(word))
				{
					if(word.length() == 0 || !jEdit.getBooleanProperty("xml.close-complete"))
						allowedCompletions.add(new XmlListCellRenderer.ClosingTag(closingTag));
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
					Object obj = completions.get(i);
					ElementDecl element = (ElementDecl)obj;
					if(element.name.startsWith(word)
						|| (data.html && element.name.toLowerCase()
						.startsWith(word.toLowerCase())))
					{
						allowedCompletions.add(element);
					}
				}
			}
			else if(mode == ENTITY_COMPLETE)
			{
				for(int i = 0; i < completions.size(); i++)
				{
					Object obj = completions.get(i);
					EntityDecl entity = (EntityDecl)obj;
					if(entity.name.startsWith(word))
						allowedCompletions.add(entity);
				}
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
			return new XmlCompletion(editPane.getView(),allowedCompletions,word,data,closingTag);
	} //}}}

	//{{{ Package-private members
	boolean stopped;
	//}}}

	//{{{ Private members
	private TagHighlight highlight;
	//}}}
}
