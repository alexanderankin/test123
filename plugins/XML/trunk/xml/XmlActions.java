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
								completionInfo.entityHash));
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
			completionInfo.elementHash,completionInfo.html);

		if(tag != null)
			textArea.setSelectedText("</" + tag.tag + ">");
	} //}}}

	//{{{ split() method
	/**
	 * If the DTD allows this tag to be split, split at the cursor.
	 * 
	 * Note that this can be used to do a kind of 'fast-editing', eg when
	 * editing an HTML &lt;p&gt; this will insert an end tag (if necessary)
	 * and then place the cursor inside a new &lt;p&gt;.
	 *
	 * TODO: Syntax Checking
	 */
	public static void split(View view)
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
			completionInfo.elementHash,
			completionInfo.html);
		
		if(tag != null)
		{
			int pos;
			
			Segment wsBefore = new Segment();
			pos = getPrevNonWhitespaceChar( buffer, tag.start - 1 ) + 1; 
			buffer.getText( pos, tag.start-pos, wsBefore );
			System.err.println( "wsBefore: [" + wsBefore + "]" );

			Segment wsAfter = new Segment();
			pos = getNextNonWhitespaceChar( buffer, tag.end );
			//Need to do this otherwise the ws in empty tags 
			//just gets bigger and bigger and bigger... 
			pos = Math.min( pos, textArea.getCaretPosition() );
			buffer.getText( tag.end, pos - tag.end, wsAfter );
			System.err.println( "wsAfter: [" + wsAfter + "]" );
			
			int lineStart = buffer.getLineStartOffset( 
				buffer.getLineOfOffset( tag.start ) );
			String tagIndent = buffer.getText( lineStart, tag.start-lineStart );

			//Note that the number of blank lines BEFORE the end tag will
			//be the number AFTER the start tag, for symmetry's sake.
			int crBeforeEndTag = countNewLines( wsAfter );
			int crAfterEndTag = countNewLines( wsBefore );
			
			StringBuffer insert = new StringBuffer();			
			if ( crBeforeEndTag>0 ) {
				for ( int i=0; i<crBeforeEndTag; i++ ) {
					insert.append( "\n" );
				}
				insert.append(tagIndent);
			}
			insert.append("</" + tag.tag + ">");
			insert.append( wsBefore );
			insert.append("<" + tag.tag + ">");
			insert.append( wsAfter );
			
			//Move the insertion point to here
			textArea.setSelectedText(insert.toString());
		}	
	}
	//}}}

	//{{{ getPrevNonWhitespaceChar() method
	/**
	 * Find the offset of the previous non whitespace character.
	 */
	private static int getPrevNonWhitespaceChar( Buffer buf, int start ) 
	{
		//It might be more efficient if there were a getCharAt() method on the buffer?
		
		//This is trying to conserve memory by not creating strings all the time
		Segment seg = new Segment( new char[1], 0, 1 );
		int pos = start;
		while ( pos>0 ) 
		{
			buf.getText( pos, 1, seg );
			if ( ! Character.isWhitespace( seg.first() ) )
				break;
			pos--;
		}
		return pos;
	}
	//}}}
	
	//{{{ getNextNonWhitespaceChar() method
	/**
	 * Find the offset of the next non-whitespace character.
	 */
	private static int getNextNonWhitespaceChar( Buffer buf, int start ) 
	{
		//It might be more efficient if there were a getCharAt() method on the buffer?
		
		//This is trying to conserve memory by not creating strings all the time
		Segment seg = new Segment( new char[1], 0, 1 );
		int pos = start;
		while ( pos < buf.getLength() ) 
		{
			buf.getText( pos, 1, seg );
			System.err.println( "NNWS Testing: " + seg.first() + " at " + pos );
			if ( ! Character.isWhitespace( seg.first() ) )
				break;
			pos++;
		}
		
		return pos;
	}
	//}}}

	//{{{ countNewLines() method
	/**
	 * Count the number of newlines in the given segment.
	 */
	private static int countNewLines( Segment seg ) 
	{
		//It might help if there were a getCharAt() method on the buffer
		//or the buffer itself implemented CharaterIterator?
		
		//This is trying to conserve memory by not creating strings all the time
		int count = 0;
		for ( int pos = seg.getBeginIndex(); pos<seg.getEndIndex(); pos++ ) {
			if ( seg.array[pos] == '\n' ) {
				count++;
			}
		}
		return count;
	}
	//}}}

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
		long startTime = System.currentTimeMillis();
		int total = 0;
		try
		{
			buffer.beginCompoundEdit();

			String text = buffer.getText(off,len);
			for (int i = text.indexOf('<');
				i != -1; i = text.indexOf('<', ++i))
			{
				TagParser.Tag tag = TagParser.getTagAtOffset(text,i + 1);
				if (tag == null)
					continue;
				else
				{
					int length = tag.end - tag.start;
					buffer.remove(tag.start - total,length);
					total += length;
				}
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
		{
			// Try to only list elements that are valid at the caret
			// position

			Buffer buffer = editPane.getBuffer();
			int end = buffer.getLineStartOffset(caretLine) + wordStart;

			completions = completionInfo.getAllowedElements(buffer,end);
		}
		else if(mode == ENTITY_COMPLETE)
			completions = completionInfo.entities;
		else
		{
			completions = (ArrayList)editPane.getClientProperty(
				XmlPlugin.IDS_PROPERTY);
		}

		//if(completions.size() == 0)
		//	return;

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

		String text = buffer.getText(0,buffer.getLength());

		if(text.charAt(caret - 2) != '<')
			return;

		// check if caret is inside a tag
		if(TagParser.getTagAtOffset(text,caret) != null)
			return;

		TagParser.Tag tag = TagParser.findLastOpenTag(text,caret - 2,
			completionInfo.elementHash,completionInfo.html);
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
			&& completionInfo != null
			&& closeCompletionOpen
			&& XmlPlugin.getParserType(buffer) != null))
		{
			return;
		}

		int caret = textArea.getCaretPosition();

		String text = buffer.getText(0,caret);

		TagParser.Tag tag = TagParser.getTagAtOffset(text,caret - 1);
		if(tag == null)
			return;

		ElementDecl decl = (ElementDecl)completionInfo.elementHash.get(tag.tag);
		if(tag.type == TagParser.T_STANDALONE_TAG
			|| (decl != null && decl.empty))
			return;

		tag = TagParser.findLastOpenTag(text,tag.start,
			completionInfo.elementHash,completionInfo.html);

		if(tag != null)
		{
			textArea.setSelectedText("</" + tag.tag + ">");
			textArea.setCaretPosition(caret);
		}
	} //}}}

	//{{{ charactersToEntities() method
	public static String charactersToEntities(String s, HashMap hash)
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < s.length(); i++)
		{
			char ch = s.charAt(i);
			if(ch >= 0x7f
				|| ch == '<'
				|| ch == '>'
				|| ch == '&'
				|| ch == '"'
				|| ch == '\'')
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
	public static String entitiesToCharacters(String s, HashMap hash)
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
					if(c != null)
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
				selection[i]),completionInfo.entityHash));
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
				selection[i]),completionInfo.entityHash));
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
	private static Segment seg = new Segment();
	private static boolean completion;
	private static boolean closeCompletion;
	private static boolean closeCompletionOpen;
	private static int delay;
	private static Timer timer;
	//}}}
}
