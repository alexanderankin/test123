/*
 * XmlActions.java - Action implementations
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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
import java.awt.event.*;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;
import xml.completion.*;
import xml.parser.*;
//}}}

public class XmlActions
{
	//{{{ showEditTagDialog() method
	public static void showEditTagDialog(View view)
	{
		EditPane editPane = view.getEditPane();

		// XXX
		// use TagParser here

		if(isDelegated(editPane))
		{
			view.getToolkit().beep();
			return;
		}

		Buffer buffer = editPane.getBuffer();
		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(XmlPlugin.getParserType(buffer) == null || data == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();
		String text = buffer.getText(0,buffer.getLength());

		int caret = textArea.getCaretPosition();

		TagParser.Tag tag = TagParser.getTagAtOffset(text,caret);
		if(tag == null || tag.type == TagParser.T_END_TAG)
		{
			view.getToolkit().beep();
			return;
		}

		// use a StringTokenizer to parse the tag
		HashMap attributes = new HashMap();
		String attributeName = null;
		boolean seenEquals = false;
		boolean empty = false;

		/* StringTokenizer does not support disabling or changing
		 * the escape character, so we have to work around it here. */
		char backslashSub = 127;
		StreamTokenizer st = new StreamTokenizer(new StringReader(
			text.substring(tag.start + tag.tag.length() + 1,
			tag.end - 1)
			.replace('\\',backslashSub)));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');
		st.ordinaryChar('/');
		st.ordinaryChar('=');

		Map entityHash = data.getNoNamespaceCompletionInfo().entityHash;

		//{{{ parse tag
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
							attributeName);
					}
					break loop;
				case '=':
					seenEquals = true;
					break;
				case StreamTokenizer.TT_WORD:
					if(attributeName == null)
					{
						attributeName = (data.html
							? st.sval.toLowerCase()
							: st.sval);
						break;
					}
					else
						/* fall thru */;
				case '"':
				case '\'':
					if(attributeName != null)
					{
						if(seenEquals)
						{
							attributes.put(attributeName,
								entitiesToCharacters(
								st.sval.replace(backslashSub,'\\'),
								entityHash));
							seenEquals = false;
						}
						else if(data.html)
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

		ElementDecl elementDecl = data.getElementDecl(tag.tag);
		if(elementDecl == null)
		{
			String[] pp = { tag.tag };
			GUIUtilities.error(view,"xml-edit-tag.undefined-element",pp);
			return;
		}

		EditTagDialog dialog = new EditTagDialog(view,elementDecl,
			attributes,empty,elementDecl.completionInfo.entityHash,
			data.ids,data.html);

		String newTag = dialog.getNewTag();

		if(newTag != null)
		{
			try
			{
				buffer.beginCompoundEdit();

				buffer.remove(tag.start,tag.end - tag.start);
				buffer.insert(tag.start,newTag);
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
		Buffer buffer = editPane.getBuffer();

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(XmlPlugin.getParserType(buffer) == null || data == null)
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		String newTag;
		String closingTag;
		if(elementDecl.attributes.size() == 0)
		{
			newTag = "<" + elementDecl.name
				+ (!data.html && elementDecl.empty
				? "/>" : ">");
			closingTag = "";
		}
		else
		{
			EditTagDialog dialog = new EditTagDialog(view,elementDecl,
				new HashMap(),elementDecl.empty,
				elementDecl.completionInfo.entityHash,
				data.ids,data.html);

			newTag = dialog.getNewTag();
			if(dialog.isEmpty())
				closingTag = "";
			else
				closingTag = "</" + elementDecl.name + ">";
		}

		if(newTag != null)
		{
			JEditTextArea textArea = editPane.getTextArea();

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
		EditPane editPane = view.getEditPane();
		Buffer buffer = editPane.getBuffer();

		if(isDelegated(editPane))
		{
			view.getToolkit().beep();
			return;
		}

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(!buffer.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| data == null)
		{
			view.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();

		TagParser.Tag tag = TagParser.findLastOpenTag(
			buffer.getText(0,textArea.getCaretPosition()),
			textArea.getCaretPosition(),data);

		if(tag != null)
			textArea.setSelectedText("</" + tag.tag + ">");
		else
		{
			view.getToolkit().beep();
		}
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
		EditPane editPane = view.getEditPane();
		Buffer buffer = editPane.getBuffer();

		if(isDelegated(editPane))
		{
			view.getToolkit().beep();
			return;
		}

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(!buffer.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| data == null)
		{
			view.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();

		TagParser.Tag tag = TagParser.findLastOpenTag(
			buffer.getText(0,textArea.getCaretPosition()),
			textArea.getCaretPosition(),data);

		if(tag != null)
		{
			int pos;

			Segment wsBefore = new Segment();
			pos = getPrevNonWhitespaceChar( buffer, tag.start - 1 ) + 1;
			buffer.getText( pos, tag.start-pos, wsBefore );
			//System.err.println( "wsBefore: [" + wsBefore + "]" );

			Segment wsAfter = new Segment();
			pos = getNextNonWhitespaceChar( buffer, tag.end );
			//Need to do this otherwise the ws in empty tags
			//just gets bigger and bigger and bigger...
			pos = Math.min( pos, textArea.getCaretPosition() );
			buffer.getText( tag.end, pos - tag.end, wsAfter );
			//System.err.println( "wsAfter: [" + wsAfter + "]" );

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
	public static void completeKeyTyped(final View view, char ch)
	{
		EditPane editPane = view.getEditPane();
		final JEditTextArea textArea = view.getTextArea();

		if(ch != '\0')
			textArea.userInput(ch);

		Buffer buffer = textArea.getBuffer();

		if(isDelegated(editPane))
			return;

		if(!buffer.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| !completion)
		{
			view.getToolkit().beep();
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
					complete(view);
			}
		});

		timer.setInitialDelay(delay);
		timer.setRepeats(false);
		timer.start();
	} //}}}

	//{{{ complete() method
	public static void complete(View view)
	{
		EditPane editPane = view.getEditPane();
		Buffer buffer = editPane.getBuffer();
		JEditTextArea textArea = editPane.getTextArea();

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(!buffer.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| data == null)
		{
			view.getToolkit().beep();
			return;
		}

		// first, we get the word before the caret
		int caretLine = textArea.getCaretLine();
		int caret = textArea.getCaretPosition();
		String text = buffer.getText(0,caret);
		int lineStart = textArea.getLineStartOffset(caretLine);
		int dot = caret - lineStart;
		if(dot == 0)
		{
			view.getToolkit().beep();
			return;
		}

		int mode = -1;
		int wordStart = -1;
		for(int i = caret - 1; i >= lineStart; i--)
		{
			char ch = text.charAt(i);
			if(ch == '<' || ch == '&')
			{
				wordStart = i;
				mode = (ch == '<' ? XmlComplete.ELEMENT_COMPLETE
					: XmlComplete.ENTITY_COMPLETE);
				break;
			}
		}

		if(wordStart == -1 || mode == -1)
		{
			view.getToolkit().beep();
			return;
		}

		String word = text.substring(wordStart + 1,caret);

		List completions;
		if(mode == XmlComplete.ELEMENT_COMPLETE)
		{
			// Try to only list elements that are valid at the caret
			// position
			completions = data.getAllowedElements(buffer,wordStart);
		}
		else if(mode == XmlComplete.ENTITY_COMPLETE)
			completions = data.getNoNamespaceCompletionInfo().entities;
		else
			throw new InternalError("Bad mode: " + mode);

		//if(completions.size() == 0)
		//	return;

		Point location = textArea.offsetToXY(wordStart);
		location.y += textArea.getPainter().getFontMetrics().getHeight();

		SwingUtilities.convertPointToScreen(location,
			textArea.getPainter());

		String closingTag = null;

		if(mode == XmlComplete.ELEMENT_COMPLETE)
		{
			TagParser.Tag tag = TagParser.findLastOpenTag(text,caret - 2,data);
			if(tag != null)
				closingTag = tag.tag;
			view.getStatus().setMessageAndClear(jEdit.getProperty(
				"xml-element-complete-status"));
		}
		else if(mode == XmlComplete.ID_COMPLETE)
		{
			view.getStatus().setMessageAndClear(jEdit.getProperty(
				"xml-id-complete-status"));
		}

		new XmlComplete(mode,view,word,completions,location,data.html,closingTag);
	} //}}}

	//{{{ completeClosingTag() method
	public static void completeClosingTag(View view)
	{
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();

		textArea.userInput('/');

		Buffer buffer = textArea.getBuffer();

		if(isDelegated(editPane))
			return;

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(!buffer.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| data == null || !closeCompletion)
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

		TagParser.Tag tag = TagParser.findLastOpenTag(text,caret - 2,data);
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

		if(isDelegated(editPane))
			return;

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(!buffer.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| data == null || !closeCompletionOpen)
		{
			return;
		}

		int caret = textArea.getCaretPosition();

		String text = buffer.getText(0,caret);

		TagParser.Tag tag = TagParser.getTagAtOffset(text,caret - 1);
		if(tag == null)
			return;

		ElementDecl decl = data.getElementDecl(tag.tag);
		if(tag.type == TagParser.T_STANDALONE_TAG
			|| (decl != null && decl.empty))
			return;

		tag = TagParser.findLastOpenTag(text,tag.start,data);

		if(tag != null)
		{
			textArea.setSelectedText("</" + tag.tag + ">");
			textArea.setCaretPosition(caret);
		}
	} //}}}

	//{{{ charactersToEntities() method
	public static String charactersToEntities(String s, Map hash)
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
	public static String entitiesToCharacters(String s, Map hash)
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
		Buffer buffer = editPane.getBuffer();
		JEditTextArea textArea = editPane.getTextArea();

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(!textArea.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| data == null || textArea.getSelectionCount() == 0)
		{
			view.getToolkit().beep();
			return;
		}

		Map entityHash = data.getNoNamespaceCompletionInfo().entityHash;

		Selection[] selection = textArea.getSelection();
		for(int i = 0; i < selection.length; i++)
		{
			textArea.setSelectedText(selection[i],
				charactersToEntities(textArea.getSelectedText(
				selection[i]),entityHash));
		}
	} //}}}

	//{{{ entitiesToCharacters() method
	public static void entitiesToCharacters(View view)
	{
		EditPane editPane = view.getEditPane();
		Buffer buffer = editPane.getBuffer();
		JEditTextArea textArea = editPane.getTextArea();

		XmlParsedData data = XmlParsedData.getParsedData(editPane);

		if(!textArea.isEditable() || XmlPlugin.getParserType(buffer) == null
			|| data == null || textArea.getSelectionCount() == 0)
		{
			view.getToolkit().beep();
			return;
		}

		Map entityHash = data.getNoNamespaceCompletionInfo().entityHash;

		Selection[] selection = textArea.getSelection();
		for(int i = 0; i < selection.length; i++)
		{
			textArea.setSelectedText(selection[i],
				entitiesToCharacters(textArea.getSelectedText(
				selection[i]),entityHash));
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

	//{{{ isDelegated() method
	/**
	 * The idea with this is to not show completion popups, etc
	 * when we're inside a JavaScript in an HTML file or whatever.
	 */
	public static boolean isDelegated(EditPane editPane)
	{
		Buffer buffer = editPane.getBuffer();
		ParserRuleSet rules = buffer.getRuleSetAtOffset(
			editPane.getTextArea().getCaretPosition());

		String rulesetName = rules.getName();
		String modeName = rules.getMode().getName();

		// Am I an idiot?
		if(rulesetName != null && rulesetName.startsWith("PHP"))
			return true;

		return jEdit.getProperty("mode." + modeName + "."
			+ XmlPlugin.PARSER_PROPERTY) == null;
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private static Segment seg = new Segment();
	private static boolean completion;
	private static boolean closeCompletion;
	private static boolean closeCompletionOpen;
	private static int delay;
	private static Timer timer;
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
			//System.err.println( "NNWS Testing: " + seg.first() + " at " + pos );
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

	//}}}
}
