/*
 * XmlActions.java - Action implementations
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001, 2002 Slava Pestov
 *               1998, 2000 Ollie Rutherfurd
 *               2000, 2001 Andre Kaplan
 *               1999 Romain Guy
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import javax.swing.text.Segment;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import xml.completion.*;
import xml.parser.*;
//}}}

public class XmlActions
{
	// For complete() method
	public static final int ELEMENT_COMPLETE = 0;
	public static final int ENTITY_COMPLETE = 1;
	public static final int ID_COMPLETE = 2;

	//{{{ showEditTagDialog() method
	public static void showEditTagDialog(View view)
	{
		EditPane editPane = view.getEditPane();

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);

		if(completionInfo == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();
		Buffer buffer = editPane.getBuffer();
		buffer.getText(0,buffer.getLength(),seg);

		int caret = textArea.getCaretPosition();

		int start = -1;
		int end = -1;

		//{{{ scan forwards looking for >
		for(int i = Math.max(0,caret - 1); i < seg.count; i++)
		{
			char ch = seg.array[seg.offset + i];
			if(i != caret - 1
				&& i != caret
				&& ch == '<')
				break;
			else if(ch == '>')
			{
				end = i;
				break;
			}
		} //}}}

		if(end == -1)
		{
			view.getToolkit().beep();
			return;
		}

		//{{{ scan backwards looking for <
		for(int i = end; i >= 0; i--)
		{
			char ch = seg.array[seg.offset + i];
			if(ch == '<')
			{
				start = i;
				break;
			}
		} //}}}

		if(start == -1)
		{
			view.getToolkit().beep();
			return;
		}

		String tag = new String(seg.array,seg.offset + start + 1,
			seg.offset + end - start - 1);

		if(tag.indexOf('<') != -1 || tag.indexOf('>') != -1
			|| tag.startsWith("!") || tag.startsWith("?")
			|| tag.startsWith("/"))
		{
			view.getToolkit().beep();
			return;
		}

		// use a StringTokenizer to parse the tag
		String elementName = null;
		HashMap attributes = new HashMap();
		String attributeName = null;
		boolean seenEquals = false;
		boolean empty = false;

		/* StringTokenizer does not support disabling or changing
		 * the escape character, so we have to work around it here. */
		char backslashSub = 127;
		StreamTokenizer st = new StreamTokenizer(new StringReader(
			tag.replace('\\',backslashSub)));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');
		st.ordinaryChar('/');
		st.ordinaryChar('=');

		//{{{ parse tag
		try
		{
			loop: for(;;)
			{
				switch(st.nextToken())
				{
				case StreamTokenizer.TT_EOF:
					if(attributeName != null)
					{
						// in HTML, can have attributes
						// without values.
						attributes.put(attributeName,
							attributeName);
					}
					break loop;
				case '=':
					seenEquals = true;
					break;
				case StreamTokenizer.TT_WORD:
					if(elementName == null)
					{
						elementName = st.sval;
						break;
					}
					else if(attributeName == null)
					{
						attributeName = (completionInfo.html
							? st.sval.toLowerCase()
							: st.sval);
						break;
					}
				case '"':
				case '\'':
					if(attributeName != null)
					{
						if(seenEquals)
						{
							attributes.put(attributeName,
								entitiesToCharacters(
								st.sval.replace(backslashSub,'\\'),
								completionInfo.entityHash,
								true));
							seenEquals = false;
						}
						else if(completionInfo.html)
						{
							attributes.put(attributeName,
								Boolean.TRUE);
						}
						attributeName = null;
					}
					break;
				case '/':
					empty = true;
					break;
				}
			}
		}
		catch(IOException io)
		{
			// won't happen
		} //}}}

		ElementDecl elementDecl = (ElementDecl)completionInfo.elementHash
			.get((completionInfo.html ? elementName.toLowerCase()
			: elementName));
		if(elementDecl == null)
		{
			String[] pp = { elementName };
			GUIUtilities.error(view,"xml-edit-tag.undefined-element",pp);
			return;
		}

		EditTagDialog dialog = new EditTagDialog(view,elementDecl,
			attributes,empty,completionInfo.entityHash,
			(ArrayList)editPane.getClientProperty(
			XmlPlugin.IDS_PROPERTY));

		String newTag = dialog.getNewTag();

		if(newTag != null)
		{
			try
			{
				buffer.beginCompoundEdit();

				buffer.remove(start,end - start + 1);
				buffer.insert(start,newTag);
			}
			finally
			{
				buffer.endCompoundEdit();
			}
		}
	} //}}}

	//{{{ showEditTagDialog() method
	public static void showEditTagDialog(View view, ElementDecl elementDecl)
	{
		EditPane editPane = view.getEditPane();

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);

		if(completionInfo == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		if(completionInfo == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		EditTagDialog dialog = new EditTagDialog(view,elementDecl,
			new HashMap(),elementDecl.empty,
			completionInfo.entityHash,
			(ArrayList)editPane.getClientProperty(
			XmlPlugin.IDS_PROPERTY));

		String newTag = dialog.getNewTag();
		String closingTag;
		if(dialog.isEmpty())
			closingTag = "";
		else
			closingTag = "</" + elementDecl.name + ">";

		if(newTag != null)
		{
			JEditTextArea textArea = editPane.getTextArea();
			Buffer buffer = editPane.getBuffer();

			Selection[] selection = textArea.getSelection();

			if(selection.length > 0)
			{
				try
				{
					buffer.beginCompoundEdit();

					for(int i = 0; i < selection.length; i++)
					{
						buffer.insert(selection[i].getStart(),
							newTag);
						buffer.insert(selection[i].getEnd(),
							closingTag);
					}
				}
				finally
				{
					buffer.endCompoundEdit();
				}
			}
			else
				textArea.setSelectedText(newTag);

			textArea.selectNone();
			textArea.requestFocus();
		}
	} //}}}

	//{{{ insertClosingTag() method
	public static void insertClosingTag(View view)
	{
		Buffer buffer = view.getBuffer();

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(view.getEditPane());

		if(!(buffer.isEditable()
			&& completion
			&& completionInfo != null))
		{
			view.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = view.getTextArea();

		TagParser.Tag tag = TagParser.findLastOpenTag(
			buffer.getText(0,textArea.getCaretPosition()),
			textArea.getCaretPosition(),
			completionInfo.elementHash);
		if(tag != null)
			textArea.setSelectedText("</" + tag.tag + ">");
	} //}}}

	//{{{ removeTags() method
	public static void removeTags(Buffer buffer)
	{
		if(!buffer.isEditable())
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		int off = 0;
		int len = buffer.getLength();
		Segment txt = new Segment();
		long startTime = System.currentTimeMillis();
		try
		{
			buffer.beginCompoundEdit();

			buffer.getText(off, len, txt);
			Position tagPos = null;
			while ((tagPos = findNextTag(txt.array, txt.offset, txt.count)) != null)
			{
				buffer.remove(off + (tagPos.off - txt.offset), tagPos.len);
				off += (tagPos.off - txt.offset);
				len = buffer.getLength() - off;
				buffer.getText(off, len, txt);
			}
		}
		finally
		{
			buffer.endCompoundEdit();
		}
		long endTime = System.currentTimeMillis();
		Log.log(Log.DEBUG, XmlActions.class,
			"removeTags time: " + (endTime - startTime) + " ms");
	} //}}}

	//{{{ goToPreviousTag() method
	public static void goToPreviousTag(JEditTextArea textArea)
	{
		Buffer buffer = textArea.getBuffer();

		buffer.getText(0,textArea.getCaretPosition(),seg);

		int caret = textArea.getCaretPosition();

		for(int i = caret - 1; i >= 0; i--)
		{
			if(seg.array[seg.offset + i] == '<')
			{
				textArea.setCaretPosition(i);
				return;
			}
		}

		textArea.getToolkit().beep();
	} //}}}

	//{{{ goToNextTag() method
	public static void goToNextTag(JEditTextArea textArea)
	{
		Buffer buffer = textArea.getBuffer();

		int caret = textArea.getCaretPosition();

		buffer.getText(caret,buffer.getLength() - caret,seg);

		for(int i = caret; i < seg.count; i--)
		{
			if(seg.array[seg.offset + i] == '<')
			{
				textArea.setCaretPosition(i);
				return;
			}
		}

		textArea.getToolkit().beep();
	} //}}}

	//{{{ matchTag() method
	public static void matchTag(JEditTextArea textArea)
	{
		String text = textArea.getText();
		TagParser.Tag tag = TagParser.getTagAtOffset(text,textArea.getCaretPosition());
		if (tag != null)
		{
			TagParser.Tag matchingTag = TagParser.getMatchingTag(text, tag);
			if (matchingTag != null)
			{
				textArea.setSelection(new Selection.Range(
					matchingTag.start, matchingTag.end
				));
				textArea.moveCaretPosition(matchingTag.end);
			}
			else
				textArea.getToolkit().beep();
		}
	} //}}}

	//{{{ completeKeyTyped() method
	public static void completeKeyTyped(final View view,
		final int mode, char ch)
	{
		EditPane editPane = view.getEditPane();
		final JEditTextArea textArea = view.getTextArea();

		if(ch != '\0')
			textArea.userInput(ch);

		Buffer buffer = textArea.getBuffer();

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);

		if(!(buffer.isEditable()
			&& completion
			&& completionInfo != null))
		{
			return;
		}

		if(timer != null)
			timer.stop();

		final int caret = textArea.getCaretPosition();

		timer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if(caret == textArea.getCaretPosition())
					complete(view,mode);
			}
		});

		timer.setInitialDelay(delay);
		timer.setRepeats(false);
		timer.start();
	} //}}}

	//{{{ complete() method
	public static void complete(View view, int mode)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		// first, we get the word before the caret
		int caretLine = textArea.getCaretLine();
		String line = textArea.getLineText(caretLine);
		int dot = textArea.getCaretPosition()
			- textArea.getLineStartOffset(caretLine);
		if(dot == 0)
		{
			view.getToolkit().beep();
			return;
		}

		int wordStart = TextUtilities.findWordStart(line,dot-1,"<&");
		String word = line.substring(wordStart + 1,dot);

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);
		if(completionInfo == null)
			return;

		ArrayList completions;
		if(mode == ELEMENT_COMPLETE)
			completions = completionInfo.elements;
		else if(mode == ENTITY_COMPLETE)
			completions = completionInfo.entities;
		else
		{
			completions = (ArrayList)editPane.getClientProperty(
				XmlPlugin.IDS_PROPERTY);
		}

		if(completions.size() == 0)
			return;

		Point location = textArea.offsetToXY(caretLine,wordStart,new Point());
		location.y += textArea.getPainter().getFontMetrics().getHeight();

		SwingUtilities.convertPointToScreen(location,
			textArea.getPainter());

		if(mode == ELEMENT_COMPLETE)
		{
			view.getStatus().setMessageAndClear(jEdit.getProperty(
				"xml-element-complete-status"));
		}
		else if(mode == ID_COMPLETE)
		{
			view.getStatus().setMessageAndClear(jEdit.getProperty(
				"xml-id-complete-status"));
		}

		new XmlComplete(view,word,completions,location);
	} //}}}

	//{{{ completeClosingTag() method
	public static void completeClosingTag(View view)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		textArea.userInput('/');

		Buffer buffer = textArea.getBuffer();

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);

		if(!(buffer.isEditable()
			&& closeCompletion
			&& completionInfo != null
			&& XmlPlugin.getParserType(buffer) != null))
		{
			return;
		}

		int caret = textArea.getCaretPosition();
		if(caret == 1)
			return;

		String text = buffer.getText(0,caret);

		if(text.charAt(caret - 2) != '<')
			return;

		TagParser.Tag tag = TagParser.findLastOpenTag(text,caret - 2,
			completionInfo.elementHash);
		if(tag != null)
		{
			textArea.setSelectedText(tag.tag + ">");
		}
	} //}}}

	//{{{ insertClosingTagKeyTyped() method
	public static void insertClosingTagKeyTyped(View view)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		textArea.userInput('>');

		Buffer buffer = view.getBuffer();

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);

		if(!(buffer.isEditable()
			&& closeCompletionOpen
			&& XmlPlugin.getParserType(buffer) != null))
		{
			return;
		}

		int caret = textArea.getCaretPosition();

		String text = buffer.getText(0,caret);

		TagParser.Tag tag = TagParser.getTagAtOffset(text,caret - 1);
		ElementDecl decl = (ElementDecl)completionInfo.elementHash.get(tag.tag);
		if(tag.type == TagParser.T_STANDALONE_TAG
			|| (decl != null && decl.empty))
			return;

		tag = TagParser.findLastOpenTag(text,tag.start,
			completionInfo.elementHash);

		if(tag != null)
		{
			textArea.setSelectedText("</" + tag.tag + ">");
			textArea.setCaretPosition(caret);
		}
	} //}}}

	//{{{ Position class
	static class Position
	{
		int off = -1;
		int len = -1;


		public Position(int off, int len)
		{
			this.off = off;
			this.len = len;
		}
	} //}}}

	//{{{ charactersToEntities() method
	// if markupChars is true, <, > and & will be ignored.
	public static String charactersToEntities(String s, HashMap hash,
		boolean markupChars)
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < s.length(); i++)
		{
			char ch = s.charAt(i);
			if(
				(markupChars
				&& (ch >= 0x7f
				|| ch == '<'
				|| ch == '>'
				|| ch == '&'
				|| ch == '"'
				|| ch == '\''))
				|| (ch >= 0x7f))
			{
				Character c = new Character(ch);
				String entity = (String)hash.get(c);
				if(entity != null)
				{
					buf.append('&');
					buf.append(entity);
					buf.append(';');

					continue;
				}
			}

			buf.append(ch);
		}

		return buf.toString();
	} //}}}

	//{{{ entitiesToCharacters() method
	// if markupChars is true, &lt;, &gt; and &amp; will be ignored.
	public static String entitiesToCharacters(String s, HashMap hash,
		boolean markupChars)
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < s.length(); i++)
		{
			char ch = s.charAt(i);
			if(ch == '&')
			{
				int index = s.indexOf(';',i);
				if(index != -1)
				{
					String entityName = s.substring(i + 1,index);
					Character c = (Character)hash.get(entityName);
					if(markupChars || c.charValue() >= 0x7f)
					{
						buf.append(c.charValue());
						i = index;
						continue;
					}
				}
			}

			buf.append(ch);
		}

		return buf.toString();
	} //}}}

	//{{{ charactersToEntities() method
	public static void charactersToEntities(View view)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		if(!textArea.isEditable()
			|| textArea.getSelectionCount() == 0)
		{
			view.getToolkit().beep();
			return;
		}

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);

		if(completionInfo == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		Selection[] selection = textArea.getSelection();
		for(int i = 0; i < selection.length; i++)
		{
			textArea.setSelectedText(selection[i],
				charactersToEntities(textArea.getSelectedText(
				selection[i]),completionInfo.entityHash,false));
		}
	} //}}}

	//{{{ entitiesToCharacters() method
	public static void entitiesToCharacters(View view)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		if(!textArea.isEditable()
			|| textArea.getSelectionCount() == 0)
		{
			view.getToolkit().beep();
			return;
		}

		CompletionInfo completionInfo = CompletionInfo
			.getCompletionInfo(editPane);

		if(completionInfo == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		Selection[] selection = textArea.getSelection();
		for(int i = 0; i < selection.length; i++)
		{
			textArea.setSelectedText(selection[i],
				entitiesToCharacters(textArea.getSelectedText(
				selection[i]),completionInfo.entityHash,false));
		}
	} //}}}

	//{{{ propertiesChanged() method
	static void propertiesChanged()
	{
		completion = jEdit.getBooleanProperty("xml.complete");
		closeCompletion = jEdit.getBooleanProperty(
			"xml.close-complete");
		closeCompletionOpen = jEdit.getBooleanProperty(
			"xml.close-complete-open");
		delay = jEdit.getIntegerProperty("xml.complete-delay",500);
	} //}}}

	//{{{ Private members

	//{{{ Static variables
	private static Segment seg = new Segment();
	private static boolean completion;
	private static boolean closeCompletion;
	private static boolean closeCompletionOpen;
	private static int delay;
	private static Timer timer;
	//}}}

	//{{{ getTagName() method
	/*
	*   returns the name of a tag (stops at first space)
	*/
	private static String getTagName(char[] buf, int offset, int len)
	{
		int startIdx = offset;
		int endIdx   = offset + len;

		for (int i = startIdx; i < endIdx; i++)
		{
			if (!Character.isWhitespace(buf[i]))
			{
				startIdx = i;
				break;
			}
		}

		for (int i = startIdx + 1; i < endIdx; i++)
		{
			if (Character.isWhitespace(buf[i]))
			{
				endIdx = i;
				break;
			}
		}

		return new String(buf, startIdx, endIdx - startIdx);
	} //}}}

	//{{{ findNextTag() method
	private static Position findNextTag(char[] array, int off, int len)
	{
		char c;
		int startIdx = -1;
		boolean commentStart = false;
		boolean commentEnd   = false;
		char inQuote = 0x0;

loop:		for (int i = len - 1; i >= 0; i--, off++)
		{
			c = array[off];

			// Ignore <<, <>, < followed by a whitespace or ignorable identifier
			if (startIdx != -1)
			{
				if (off == (startIdx + 1))
				{
					if ((c == '<')
						||  (c == '>')
						||  Character.isISOControl(c)
						||  Character.isWhitespace(c)
					)
					{
						startIdx = -1;
						commentStart = commentEnd = false;
					}
				}
			}

			switch (c)
			{
			case '<':
				if (startIdx != -1)
					continue loop;

				if (inQuote != 0x0)
					continue loop;

				if (commentStart)
					continue loop;

				startIdx = off;

				break;

			case '"':
			case '\'':
				if (startIdx == -1)
					continue loop;

				if (commentStart)
					continue loop;

				if (inQuote == c)
					inQuote = 0x0;
				else if (inQuote == 0x0)
					inQuote = c;
				else ; // inQuote != c : do nothing

				break;

			case '>':
				if (startIdx == -1)
					continue loop;

				if (inQuote != 0x0)
					continue loop;

				if (commentStart)
				{
					if ((off - startIdx) < 6)
						continue loop;
					commentEnd = (
						   (array[off - 1] == '-')
						&& (array[off - 2] == '-')
					);
					if (commentEnd)
						return new Position(startIdx, (off + 1) - startIdx);
					else
						continue loop;
				}

				return new Position(startIdx, (off + 1) - startIdx);
				// break;

			default:
				if (!commentStart && (off == (startIdx + 3)))
				{
					commentStart = (
						   (array[startIdx + 1] == '!')
						&& (array[startIdx + 2] == '-')
						&& (array[startIdx + 3] == '-')
					);
				}
				break;
			}
		}

		if (commentStart)
			return new Position(startIdx, off - startIdx);
		else
			return null;
	} //}}}

	//}}}
}
