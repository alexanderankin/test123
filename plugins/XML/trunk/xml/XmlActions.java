/*
 * XmlActions.java - Action implementations
 * Copyright (C) 2000, 2001 Slava Pestov
 *               1998, 2000 Ollie Rutherfurd
 *               2000, 2001 Andre Kaplan
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
import java.awt.Point;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
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

		Hashtable elements = (Hashtable)editPane.getClientProperty(
			XmlPlugin.ELEMENT_HASH_PROPERTY);

		if(elements == null)
		{
			GUIUtilities.error(view,"xml-edit-tag.no-tree",null);
			return;
		}

		Vector ids = (Vector)editPane.getClientProperty(
			XmlPlugin.IDS_PROPERTY);

		// XXX: to do
		boolean html = false;

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
		for(int i = caret - 1; i >= 0; i--)
		{
			char ch = seg.array[seg.offset + i];
			if(ch == '>')
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
loop:			for(;;)
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
						attributeName = (html
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
								st.sval.replace(backslashSub,'\\'));
							seenEquals = false;
						}
						else
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

		ElementDecl elementDecl = (ElementDecl)elements.get(
			(html ? elementName.toLowerCase() : elementName));
		if(elementDecl == null)
		{
			String[] pp = { elementName };
			GUIUtilities.error(view,"xml-edit-tag.undefined-element",pp);
			return;
		}

		EditTagDialog dialog = new EditTagDialog(view,elementDecl,
			attributes,empty,ids);

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

		Hashtable elements = (Hashtable)editPane.getClientProperty(
			XmlPlugin.ELEMENT_HASH_PROPERTY);

		if(elements == null)
		{
			GUIUtilities.error(view,"xml-edit-tag.no-tree",null);
			return;
		}

		Vector ids = (Vector)editPane.getClientProperty(
			XmlPlugin.IDS_PROPERTY);

		EditTagDialog dialog = new EditTagDialog(view,elementDecl,
			new Hashtable(),elementDecl.empty,ids);

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
		final JEditTextArea textArea = view.getTextArea();

		if(ch != '\0')
			textArea.userInput(ch);

		Buffer buffer = textArea.getBuffer();

		if(!(buffer.isEditable()
			&& completion
			&& buffer.getBooleanProperty("xml.parse")))
		{
			return;
		}

		XmlTree tree = (XmlTree)view.getDockableWindowManager()
			.getDockableWindow(XmlPlugin.TREE_NAME);
		if(tree == null)
			return;

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

		Vector completions;
		if(mode == ELEMENT_COMPLETE)
		{
			completions = (Vector)view.getEditPane().getClientProperty(
				XmlPlugin.ELEMENTS_PROPERTY);
		}
		else
		{
			completions = (Vector)view.getEditPane().getClientProperty(
				XmlPlugin.ENTITIES_PROPERTY);
		}

		if(completions == null || completions.size() == 0)
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
		JEditTextArea textArea = view.getTextArea();

		textArea.userInput('/');

		Buffer buffer = textArea.getBuffer();

		if(!(buffer.isEditable()
			&& closeCompletion
			&& buffer.getBooleanProperty("xml.parse")))
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
		JEditTextArea textArea = view.getTextArea();
		textArea.userInput('>');

		Buffer buffer = view.getBuffer();

		if(!(buffer.isEditable()
			&& closeCompletionOpen
			&& buffer.getBooleanProperty("xml.parse")))
			return;

		int caret = textArea.getCaretPosition();

		insertClosingTag(view);

		textArea.setCaretPosition(caret);
	}

	public static void insertClosingTag(View view)
	{
		JEditTextArea textArea = view.getTextArea();
		Buffer buffer = view.getBuffer();

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

				if (lastChar == '?')
				{
					inTag = false;
				}

				if (lastChar == '!')
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
							return curTag;

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
}
