/*
 * XmlSideKickParser.java
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

import java.util.ArrayList;
import java.util.List;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import sidekick.*;
import xml.completion.*;
import xml.parser.TagParser;
import xml.XmlListCellRenderer;
import xml.XmlParsedData;

public abstract class XmlSideKickParser extends SideKickParser
{
	public static final String COMPLETION_TRIGGERS = "<&";
	public static final int ELEMENT_COMPLETE = '<';
	public static final int ENTITY_COMPLETE = '&';

	//{{{ XmlSideKickParser constructor
	public XmlSideKickParser(String name)
	{
		super(name);
	} //}}}

	//{{{ getCompletionTriggers() method
	public String getCompletionTriggers()
	{
		return COMPLETION_TRIGGERS;
	} //}}}

	//{{{ getCompletions() method
	public SideKickCompletion complete(EditPane editPane,
		SideKickParsedData _data, int caret)
	{
		XmlParsedData data = (XmlParsedData)_data;

		Buffer buffer = editPane.getBuffer();

		// first, we get the word before the caret
		JEditTextArea textArea = editPane.getTextArea();

		int caretLine = buffer.getLineOfOffset(caret);
		String text = buffer.getText(0,caret);
		int lineStart = textArea.getLineStartOffset(caretLine);
		int dot = caret - lineStart;
		if(dot == 0)
			return null;

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
		}

		if(wordStart == -1 || mode == -1)
			return null;

		String word = text.substring(wordStart + 1,caret);

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

		//if(completions.size() == 0)
		//	return;

		List allowedCompletions = new ArrayList(completions.size());

		String closingTag = null;

		if(mode == ELEMENT_COMPLETE)
		{
			TagParser.Tag tag = TagParser.findLastOpenTag(text,caret - 2,data);
			if(tag != null)
				closingTag = tag.tag;

			if("!--".startsWith(text))
				allowedCompletions.add(new XmlListCellRenderer.Comment());
			if(!data.html && "![CDATA[".startsWith(text))
				allowedCompletions.add(new XmlListCellRenderer.CDATA());
			if(closingTag != null && ("/" + closingTag).startsWith(text))
				allowedCompletions.add(new XmlListCellRenderer.ClosingTag(closingTag));

			for(int i = 0; i < completions.size(); i++)
			{
				Object obj = completions.get(i);
				ElementDecl element = (ElementDecl)obj;
				if(element.name.startsWith(text)
					|| (data.html && element.name.toLowerCase()
					.startsWith(text.toLowerCase())))
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
				if(entity.name.startsWith(text))
					allowedCompletions.add(entity);
			}
		}
		/* else if(mode == ID_COMPLETE)
		{
			else if(obj instanceof IDDecl)
			{
				IDDecl id = (IDDecl)obj;
				if(id.id.startsWith(text))
					allowedCompletions.add(id);
			}
		} */

		return new XmlCompletion(allowedCompletions);
	} //}}}
}
