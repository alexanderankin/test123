/*
 * XmlActions.java - Action implementations
 * Copyright (C) 2000, 2001 Slava Pestov
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

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class XmlActions
{
	// For complete() method
	public static final int ELEMENT_COMPLETE = 0;
	public static final int ENTITY_COMPLETE = 1;

	public static void showEditTagDialog(View view)
	{
		EditPane editPane = view.getEditPane();

		CompletionInfo completionInfo = (CompletionInfo)
			editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY);

		if(completionInfo == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();
		Buffer buffer = editPane.getBuffer();

		try
		{
			buffer.getText(0,buffer.getLength(),seg);
		}
		catch(BadLocationException bl)
		{
			Log.log(Log.ERROR,XmlActions.class,bl);
		}

		int caret = textArea.getCaretPosition();

		int start = -1;
		int end = -1;

		// scan backwards looking for <
		// if we find a >, then assume we're not inside a tag
		for(int i = Math.min(seg.count,caret); i >= 0; i--)
		{
			char ch = seg.array[seg.offset + i];

			// if caret is before >, then the character at the
			// caret pos will be >
			if(i != caret && ch == '>')
				break;
			else if(ch == '<')
			{
				start = i;
				break;
			}
		}

		// scan forwards looking for >
		// if we find a <, then assume we're not inside a tag
		for(int i = caret; i < seg.count; i++)
		{
			char ch = seg.array[seg.offset + i];
			if(ch == '<')
				break;
			else if(ch == '>')
			{
				end = i;
				break;
			}
		}

		if(start == -1 || end == -1)
		{
			view.getToolkit().beep();
			return;
		}

		String tag = new String(seg.array,seg.offset + start + 1,
			seg.offset + end - start - 1);

		// use a StringTokenizer to parse the tag
		String elementName = null;
		Hashtable attributes = new Hashtable();
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

		// this is an incredibly brain-dead parser.
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
							Boolean.TRUE);
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
		}

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
			completionInfo.ids);

		String newTag = dialog.getNewTag();

		if(newTag != null)
		{
			try
			{
				buffer.beginCompoundEdit();

				buffer.remove(start,end - start + 1);
				buffer.insertString(start,newTag,null);
			}
			catch(BadLocationException ble)
			{
				Log.log(Log.ERROR,XmlPlugin.class,ble);
			}
			finally
			{
				buffer.endCompoundEdit();
			}
		}
	}

	public static void showEditTagDialog(View view, ElementDecl elementDecl)
	{
		EditPane editPane = view.getEditPane();

		CompletionInfo completionInfo = (CompletionInfo)
			editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY);

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
			new Hashtable(),elementDecl.empty,
			completionInfo.entityHash,
			completionInfo.ids);

		String newTag = dialog.getNewTag();

		if(newTag != null)
		{
			editPane.getTextArea().setSelectedText(newTag);
			editPane.getTextArea().requestFocus();
		}
	}

	public static void completeKeyTyped(final View view,
		final int mode, char ch)
	{
		EditPane editPane = view.getEditPane();
		final JEditTextArea textArea = view.getTextArea();

		if(ch != '\0')
			textArea.userInput(ch);

		Buffer buffer = textArea.getBuffer();

		if(!(buffer.isEditable()
			&& completion
			&& editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY) != null))
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
	}

	public static void complete(View view, int mode)
	{
		JEditTextArea textArea = view.getTextArea();

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

		CompletionInfo completionInfo = (CompletionInfo)
			view.getEditPane().getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY);
		if(completionInfo == null)
			return;

		Vector completions = (mode == ELEMENT_COMPLETE
			? completionInfo.elements
			: completionInfo.entities);

		if(completions.size() == 0)
			return;

		Point location = new Point(textArea.offsetToX(caretLine,wordStart),
			textArea.getPainter().getFontMetrics().getHeight()
			* (textArea.getBuffer().physicalToVirtual(caretLine)
			- textArea.getFirstLine() + 1));

		SwingUtilities.convertPointToScreen(location,
			textArea.getPainter());

		if(mode == ELEMENT_COMPLETE)
		{
			view.getStatus().setMessageAndClear(jEdit.getProperty(
				"xml-complete-status"));
		}

		new XmlComplete(view,word,completions,location);
	}

	public static void completeClosingTag(View view)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		textArea.userInput('/');

		Buffer buffer = textArea.getBuffer();

		if(!(buffer.isEditable()
			&& closeCompletion
			&& editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY) != null))
		{
			return;
		}

		int caret = textArea.getCaretPosition();
		if(caret == 1)
			return;

		try
		{
			buffer.getText(0,caret,seg);
		}
		catch(BadLocationException bl)
		{
			Log.log(Log.ERROR,XmlActions.class,bl);
		}

		if(seg.array[seg.offset + caret - 2] != '<')
			return;

		String tag = findLastOpenTag(seg);
		if(tag != null)
			textArea.setSelectedText(tag + ">");
	}

	public static void insertClosingTagKeyTyped(View view)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		textArea.userInput('>');

		Buffer buffer = view.getBuffer();

		if(!(buffer.isEditable()
			&& closeCompletionOpen
			&& editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY) != null))
		{
			return;
		}

		int caret = textArea.getCaretPosition();
		if(caret == 1)
			return;

		try
		{
			buffer.getText(caret - 1,caret,seg);
		}
		catch(BadLocationException bl)
		{
			Log.log(Log.ERROR,this,bl);
		}

		// don't insert closing tag for empty element
		if(seg.array[seg.offset] == '/')
			return;

		insertClosingTag(textArea);

		textArea.setCaretPosition(caret);
	}

	public static void insertClosingTag(JEditTextArea textArea)
	{
		Buffer buffer = textArea.getBuffer();

		if(!buffer.isEditable())
			return;

		try
		{
			buffer.getText(0,textArea.getCaretPosition(),seg);
		}
		catch(BadLocationException bl)
		{
			Log.log(Log.ERROR,XmlActions.class,bl);
		}

		String tag = findLastOpenTag(seg);
		if(tag != null)
			textArea.setSelectedText("</" + tag + ">");
	}

	public static void goToPreviousTag(JEditTextArea textArea)
	{
		Buffer buffer = textArea.getBuffer();

		try
		{
			buffer.getText(0,textArea.getCaretPosition(),seg);
		}
		catch(BadLocationException bl)
		{
			Log.log(Log.ERROR,XmlActions.class,bl);
		}

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
	}

	public static void goToNextTag(JEditTextArea textArea)
	{
		Buffer buffer = textArea.getBuffer();

		try
		{
			buffer.getText(textArea.getCaretPosition(),
				buffer.getLength(),seg);
		}
		catch(BadLocationException bl)
		{
			Log.log(Log.ERROR,XmlActions.class,bl);
		}

		int caret = textArea.getCaretPosition();

		for(int i = caret ; i < seg.count; i--)
		{
			if(seg.array[seg.offset + i] == '<')
			{
				textArea.setCaretPosition(i);
				return;
			}
		}

		textArea.getToolkit().beep();
	}

	/*
	*   finds the last tag which hasn't been closed
	*   this includes tags such as <img> or <br>
	*
	*   note: it doesn't check whether tags match
	*   eg <p><b><i></b></i> (will find <p>)
	*
	*/
	public static String findLastOpenTag(Segment s)
	{
		Stack tagStack = new Stack();
		int tagEnd = 0;
		int curOff = s.count - 1;
		int curPos = s.offset + s.count - 1;
		boolean inTag = false;
		char lastChar = '*';
		char curChar = '*';
		String curTag = "";

		while (curOff >= 0)
		{
			curChar = s.array[curPos];

			// start of tag (or end)
			if(curChar == '>')
			{
				tagEnd = curPos;
				inTag = true;
			}
			// if tag is something like '<br />', don't push onto stack, just skip
			else if (curChar == '/')
			{

				if (lastChar == '>')
				{
					inTag = false;
				}
			}
			// if in tag either push of pop a tag, depending on whether
			// last char as a wack '/' or not
			else if (curChar == '<')
			{

				if (lastChar == '?'
					|| lastChar == '!'
					|| lastChar == '-')
				{
					inTag = false;
				}

				if (inTag == true)
				{
					// if closing a tag, push it onto the stack
					if (lastChar == '/')
					{
						curTag = getTagName(s.array, curPos + 2, tagEnd - (curPos + 2));
						tagStack.push(curTag);
					}
					else
					{
						curTag = getTagName(s.array, curPos + 1, tagEnd - (curPos + 1));

						if (tagStack.empty())
							return (curTag.length() == 0 ? null : curTag);

						String lastTag = (String) tagStack.peek();
						if (lastTag.equalsIgnoreCase(curTag))
							tagStack.pop();
					}
				}
			}

			lastChar = s.array[curPos];
			curPos--;
			curOff--;
		}

		return "";
	}

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
		catch (BadLocationException ble)
		{
			Log.log(Log.ERROR, XmlActions.class, ble);
		}
		finally
		{
			buffer.endCompoundEdit();
		}
		long endTime = System.currentTimeMillis();
		Log.log(Log.DEBUG, XmlActions.class,
			"removeTags time: " + (endTime - startTime) + " ms");
	}

	static class Position
	{
		int off = -1;
		int len = -1;


		public Position(int off, int len)
		{
			this.off = off;
			this.len = len;
		}
	}

	// if markupChars is true, <, > and & will be ignored.
	public static String charactersToEntities(String s, Hashtable hash,
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
	}

	// if markupChars is true, &lt;, &gt; and &amp; will be ignored.
	public static String entitiesToCharacters(String s, Hashtable hash,
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
	}

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

		CompletionInfo completionInfo = (CompletionInfo)
			editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY);

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
	}

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

		CompletionInfo completionInfo = (CompletionInfo)
			editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY);

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
	}

	// package-private members
	static void propertiesChanged()
	{
		completion = jEdit.getBooleanProperty("xml.complete");
		closeCompletion = jEdit.getBooleanProperty(
			"xml.close-complete");
		closeCompletionOpen = jEdit.getBooleanProperty(
			"xml.close-complete-open");
		try
		{
			delay = Integer.parseInt("xml.complete-delay");
		}
		catch(NumberFormatException nf)
		{
			delay = 500;
		}
	}

	// private members
	private static Segment seg = new Segment();
	private static boolean completion;
	private static boolean closeCompletion;
	private static boolean closeCompletionOpen;
	private static int delay;
	private static Timer timer;

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
	}

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
	}
}
