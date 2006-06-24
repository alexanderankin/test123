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
import java.awt.Toolkit;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import sidekick.SideKickParsedData;
import xml.completion.ElementDecl;
import xml.parser.TagParser;
//}}}

public class XmlActions
{
	//{{{ showEditTagDialog() method
	
	
	public static void showEditTagDialog(View view)
	{
		JEditTextArea textArea = view.getTextArea();

		if(XmlPlugin.isDelegated(textArea))
		{
			view.getToolkit().beep();
			return;
		}

		Buffer buffer = view.getBuffer();
		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		XmlParsedData data = (XmlParsedData)_data;

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

		EditTagDialog dialog = new EditTagDialog(view,tag.tag,
			elementDecl,attributes,empty,
			elementDecl.completionInfo.entityHash,
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

	public static void showEditTagDialog(View view, ElementDecl elementDecl) {
		showEditTagDialog(view, elementDecl, null);
	}
	
	//{{{ showEditTagDialog() method
	
	public static void showEditTagDialog(View view, ElementDecl elementDecl, Selection insideTag)
	{
		Buffer buffer = view.getBuffer();

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		XmlParsedData data = (XmlParsedData)_data;

		String newTag;
		String closingTag;
		if(elementDecl.attributes.size() == 0)
		{
			newTag = "<" + elementDecl.name
				+ (!data.html && elementDecl.empty
				? XmlActions.getStandaloneEnd() : ">");
			if(elementDecl.empty)
				closingTag = "";
			else
				closingTag = "</" + elementDecl.name + ">";
		}
		else
		{
			EditTagDialog dialog = new EditTagDialog(view,elementDecl.name,elementDecl,
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
			JEditTextArea textArea = view.getTextArea();
			if (insideTag != null) textArea.setSelectedText(insideTag, "");
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
			{
				textArea.setSelectedText(newTag);
				int caret = textArea.getCaretPosition();
				textArea.setSelectedText(closingTag);
				textArea.setCaretPosition(caret);
			}

			textArea.selectNone();
			textArea.requestFocus();
		}
	} //}}}

	//{{{ insertClosingTag() method
	public static void insertClosingTag(View view)
	{
		JEditTextArea textArea = view.getTextArea();
		Buffer buffer = view.getBuffer();

		if(XmlPlugin.isDelegated(textArea) || !buffer.isEditable())
		{
			view.getToolkit().beep();
			return;
		}

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		XmlParsedData data = (XmlParsedData)_data;

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
		JEditTextArea textArea = view.getTextArea();
		Buffer buffer = view.getBuffer();

		if(XmlPlugin.isDelegated(textArea) || !buffer.isEditable())
		{
			view.getToolkit().beep();
			return;
		}

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		XmlParsedData data = (XmlParsedData)_data;

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

	//{{{ matchTag() method
	public static void matchTag(JEditTextArea textArea)
	{
		String text = textArea.getText();
		int caret = textArea.getCaretPosition();

		// De-Select previous selection
		// textArea.select(caret, caret);
		textArea.setSelection(new Selection.Range(caret, caret));
		
		// Move cursor inside tag, to help with matching
		try { if (text.charAt(caret) == '<')  
			textArea.goToNextCharacter(false);
		} catch (Exception e ) {} 
		
		TagParser.Tag tag = TagParser.getTagAtOffset(text,textArea.getCaretPosition());
		if (tag != null)
		{
			TagParser.Tag matchingTag = TagParser.getMatchingTag(text, tag);
			if (matchingTag != null)
			{
				textArea.setSelection(new Selection.Range(
					matchingTag.start, matchingTag.end
				));
				textArea.moveCaretPosition(matchingTag.end-1);
			}
			else
				textArea.getToolkit().beep();
		}
	} //}}}

	//{{{ insertClosingTagKeyTyped() method
	public static void insertClosingTagKeyTyped(View view)
	{
		JEditTextArea textArea = view.getTextArea();

		Macros.Recorder recorder = view.getMacroRecorder();
		if(recorder != null)
			recorder.recordInput(1,'>',false);
		textArea.userInput('>');

		Buffer buffer = view.getBuffer();

		if(XmlPlugin.isDelegated(textArea) || !buffer.isEditable()
			|| !closeCompletionOpen)
			return;

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
			return;

		XmlParsedData data = (XmlParsedData)_data;

		int caret = textArea.getCaretPosition();

		String text = buffer.getText(0,caret);

		TagParser.Tag tag = TagParser.getTagAtOffset(text,caret - 1);
		if(tag == null)
			return;

		ElementDecl decl = data.getElementDecl(tag.tag);
		if(tag.type == TagParser.T_STANDALONE_TAG
			|| (decl != null && decl.empty))
			return;

		tag = TagParser.findLastOpenTag(text,caret,data);

		if(tag != null)
		{
			String insert = "</" + tag.tag + ">";
			if(recorder != null)
				recorder.recordInput(insert,false);
			textArea.setSelectedText(insert);

			String code = "textArea.setCaretPosition("
				+ "textArea.getCaretPosition() - "
				+ insert.length() + ");";
			if(recorder != null)
				recorder.record(code);
			BeanShell.eval(view,BeanShell.getNameSpace(),code);
		}
	} //}}}

	//{{{ completeClosingTag() method
	public static void completeClosingTag(View view, boolean insertSlash)
	{
		JEditTextArea textArea = view.getTextArea();

		Macros.Recorder recorder = view.getMacroRecorder();

		if(insertSlash)
		{
			if(recorder != null)
				recorder.recordInput(1,'/',false);
			textArea.userInput('/');
		}

		JEditBuffer buffer = textArea.getBuffer();

		if(XmlPlugin.isDelegated(textArea))
			return;

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
			return;

		XmlParsedData data = (XmlParsedData)_data;

		if(!buffer.isEditable() || !closeCompletion)
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
			String insert = tag.tag + ">";
			if(recorder != null)
				recorder.recordInput(insert,false);
			textArea.setSelectedText(insert);
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
		Buffer buffer = view.getBuffer();
		JEditTextArea textArea = view.getTextArea();

		if(XmlPlugin.isDelegated(textArea) || !buffer.isEditable())
		{
			view.getToolkit().beep();
			return;
		}

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		XmlParsedData data = (XmlParsedData)_data;

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
		Buffer buffer = view.getBuffer();
		JEditTextArea textArea = view.getTextArea();

		if(XmlPlugin.isDelegated(textArea) || !buffer.isEditable())
		{
			view.getToolkit().beep();
			return;
		}

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(!(_data instanceof XmlParsedData))
		{
			GUIUtilities.error(view,"xml-no-data",null);
			return;
		}

		XmlParsedData data = (XmlParsedData)_data;

		Map entityHash = data.getNoNamespaceCompletionInfo().entityHash;

		Selection[] selection = textArea.getSelection();
		for(int i = 0; i < selection.length; i++)
		{
			textArea.setSelectedText(selection[i],
				entitiesToCharacters(textArea.getSelectedText(
				selection[i]),entityHash));
		}
	} //}}}

	//{{{ getStandaloneEnd() method
	public static String getStandaloneEnd()
	{
		return (standaloneExtraSpace ? " />" : "/>");
	} //}}}

	//{{{ propertiesChanged() method
	static void propertiesChanged()
	{
		closeCompletion = jEdit.getBooleanProperty(
			"xml.close-complete");
		closeCompletionOpen = jEdit.getBooleanProperty(
			"xml.close-complete-open");
		standaloneExtraSpace = jEdit.getBooleanProperty(
			"xml.standalone-extra-space");
	} //}}}

	//{{{ Private members

	//{{{ Static variables
	private static Segment seg = new Segment();
	private static boolean closeCompletion;
	private static boolean closeCompletionOpen;
	private static boolean standaloneExtraSpace;
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
