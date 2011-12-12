/*
 * TextToolsPlugin.java - Plugin for a number of text related functions
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2001 mike dillon
 * Revised for jEdit 4.0 by John Gellene
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

//{{{ Imports
import java.util.*;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JTextField;
//}}}

public class TextToolsPlugin extends EditPlugin
{
	public static boolean debugTT = false; // debug flag, can be set via Utilities->BeanShell
						// TextToolsPlugin.debugTT = true
	
	//{{{ isTextAreaEditable() method
	/**
	 * isTextAreaEditable: checks if textarea editable
	 */
	public static boolean isTextAreaEditable(View view, JEditTextArea textArea)
	{
		if (!textArea.isEditable())
		{
			textArea.getToolkit().beep();
			if (view != null)

				view.getStatus().setMessageAndClear(jEdit.getProperty(
									"view.status.textTools.buffer-not-editable"));
			return false;
		}
		else
			return true;
	}//}}}
	
	//{{{ isSelectedAndRectangular() method
	public static boolean isSelectedAndRectangular(View view, JEditTextArea textArea)
	{
		Selection[] selection = textArea.getSelection();
		if (selection.length == 0 || selection[0] instanceof Selection.Range)
		{
			textArea.getToolkit().beep();
			if (view != null)
				view.getStatus().setMessageAndClear(jEdit.getProperty(
									"view.status.textTools.rectangular-selection-required"));
			return false;
		}
		else
			return true;
	} //}}}
	
	//{{{ transposeChars() method
	public static void transposeChars(JEditTextArea textArea)
	{
		int line = textArea.getCaretLine();

		if (!textArea.isEditable() || textArea.getLineLength(line) < 2)
		{
			textArea.getToolkit().beep();
			return;
		}

		int start, caret;
		start = caret = textArea.getCaretPosition();

		if (caret == textArea.getLineStartOffset(line))
		{
			// caret is before the first character in the line,
			// so move it forward one character
			caret++;
		}
		else if (caret == textArea.getLineEndOffset(line) - 1)
		{
			// caret is after the last character in the line,
			// so move it back one character
			caret--;
		}

		JEditBuffer b = textArea.getBuffer();

		b.beginCompoundEdit();

		{
			String pair = b.getText(caret - 1, 2);
			String trans = new String(new char[] { pair.charAt(1),
				pair.charAt(0) });
			b.remove(caret - 1, 2);
			b.insert(caret - 1, trans);
		}

		// put the caret back where it was before the transposition
		textArea.setCaretPosition(start);

		b.endCompoundEdit();
	} //}}}
	
	//{{{ transposeWords() method
	public static void transposeWords(JEditTextArea textArea)
	{
		int line = textArea.getCaretLine();

		if (!textArea.isEditable() || textArea.getLineLength(line) == 0)
		{
			textArea.getToolkit().beep();
			return;
		}

		int lineStart = textArea.getLineStartOffset(line);
		int offset = textArea.getCaretPosition() - lineStart;

		JEditBuffer buffer = textArea.getBuffer();

		String lineText = textArea.getLineText(line);
		String noWordSep = (String)buffer.getProperty("noWordSep");

		if (offset == lineText.length())
			offset--;

		int wordStart = TextUtilities.findWordStart(lineText, offset,
			noWordSep);
		int wordEnd = TextUtilities.findWordEnd(lineText, offset + 1,
			noWordSep);

		// only one "word" in this line, so do nothing
		if (wordStart == 0 && wordEnd == lineText.length())
			return;

		boolean reverseBias = false;

		// figure out whether a word or a non-word was found
		boolean word = isWordChar(lineText.charAt(wordStart), noWordSep)
			|| (reverseBias = wordStart == offset);

		int word1Start, word1End, word2Start, word2End;

		word1Start = word1End = word2Start = word2End = 0;

		if (word)
		{
			if (wordStart == 0)
			{
				// search forward to find the next word
				word1Start = wordStart;
				word1End = wordEnd;

				// find the end of the non-word span after
				// word1, if it's the end of the line, return;
				// otherwise, it is the beginning of word2
				word2Start = TextUtilities.findWordEnd(
					lineText, word1End + 1, noWordSep);

				if (word2Start == lineText.length())
					return;

				word2End = TextUtilities.findWordEnd(
					lineText, word2Start + 1, noWordSep);
			}
			else if (wordEnd == lineText.length())
			{
				// search backward to find the previous word
				word2Start = wordStart;
				word2End = wordEnd;

				// find the start of the non-word span before
				// word2, if it's the start of the line, return;
				// otherwise, it is the end of word1
				word1End = TextUtilities.findWordStart(
					lineText, word2Start - 1, noWordSep);

				if (word1End == 0)
					return;

				word1Start = TextUtilities.findWordStart(
					lineText, word1End - 1, noWordSep);
			}
			else if (reverseBias)
			{
				// search backward to find the last two words
				word2Start = TextUtilities.findWordStart(
					lineText, wordStart - 1, noWordSep);

				// only one word in the line so do nothing
				if (word2Start == 0)
					return;

				word2End = wordStart;

				// find the start of the non-word span before
				// word2, if it's the start of the line, return;
				// otherwise, it is the end of word1
				word1End = TextUtilities.findWordStart(
					lineText, word2Start - 1, noWordSep);

				if (word1End == 0) return;

				word1Start = TextUtilities.findWordStart(
					lineText, word1End - 1, noWordSep);
			}
			else
			{
				word2Start = wordStart;
				word2End = wordEnd;

				// find the start of the non-word span before
				// word2, if it's the start of the line, return;
				// otherwise, it is the end of word1
				word1End = TextUtilities.findWordStart(
					lineText, word2Start - 1, noWordSep);

				if (word1End == 0)
					return;

				word1Start = TextUtilities.findWordStart(
					lineText, word1End - 1, noWordSep);
			}
		}
		else
		{
			if (wordStart == 0)
			{
				// search forward to find the first two words
				word1End = TextUtilities.findWordEnd(
					lineText, wordEnd + 1, noWordSep);

				// only one word in the line so do nothing
				if (word1End == lineText.length()) return;

				word1Start = wordEnd;

				// find the end of the non-word span after
				// word1, if it's the end of the line, return;
				// otherwise, it is the beginning of word2
				word2Start = TextUtilities.findWordEnd(
					lineText, word1End + 1, noWordSep);

				if (word2Start == lineText.length())
					return;

				word2End = TextUtilities.findWordEnd(
					lineText, word2Start + 1, noWordSep);
			}
			else if (wordEnd == lineText.length())
			{
				// search backward to find the last two words
				word2Start = TextUtilities.findWordStart(
					lineText, wordStart - 1, noWordSep);

				// only one word in the line so do nothing
				if (word2Start == 0)
					return;

				word2End = wordStart;

				// find the start of the non-word span before
				// word2, if it's the start of the line, return;
				// otherwise, it is the end of word1
				word1End = TextUtilities.findWordStart(
					lineText, word2Start - 1, noWordSep);

				if (word1End == 0)
					return;

				word1Start = TextUtilities.findWordStart(
					lineText, word1End - 1, noWordSep);
			}
			else
			{
				// find the two words on either side
				word1Start = TextUtilities.findWordStart(
					lineText, wordStart - 1, noWordSep);
				word1End = TextUtilities.findWordEnd(
					lineText, wordStart, noWordSep);

				word2Start = TextUtilities.findWordStart(
					lineText, wordEnd, noWordSep);
				word2End = TextUtilities.findWordEnd(
					lineText, wordEnd + 1, noWordSep);
			}
		}

		StringBuilder buf = new StringBuilder();

		buf.append(lineText.substring(word2Start, word2End));
		buf.append(lineText.substring(word1End, word2Start));
		buf.append(lineText.substring(word1Start, word1End));

		buffer.beginCompoundEdit();

		buffer.remove(lineStart + word1Start,
			word2End - word1Start);
		buffer.insert(lineStart + word1Start,
			buf.toString());

		textArea.setCaretPosition(lineStart + word2End);

		buffer.endCompoundEdit();
	} //}}}
	
	//{{{ transposeLines() method
	public static void transposeLines(JEditTextArea textArea)
	{
		if (!textArea.isEditable() || textArea.getLineCount() < 2)
		{
			textArea.getToolkit().beep();
			return;
		}
		
		int line = textArea.getCaretLine();
		
		if (line == 0)
		{
			// this is the first line, so move forward one line
			line++;
		}
		
		int start = textArea.getLineStartOffset(line - 1);
		int end = textArea.getLineEndOffset(line);
		
		StringBuilder buf = new StringBuilder();

		buf.append(textArea.getLineText(line)).append('\n');
		buf.append(textArea.getLineText(line - 1)).append('\n');
		
		JEditBuffer b = textArea.getBuffer();
		
		b.beginCompoundEdit();
		
		b.remove(start, end - start);
		b.insert(start, buf.toString());
		
		// put the caret at the end of the last line transposed
		textArea.setCaretPosition(end - 1);
		
		b.endCompoundEdit();
	} //}}}
	
	//{{{ doColumnInsert() method
	public static void doColumnInsert(View view)
	{
		JEditTextArea ta = view.getTextArea();
		final Selection [] sel = ta.getSelection();
		
		if (sel.length != 0 && sel[0] instanceof Selection.Rect)
		{
			final View theView = view; //final because it is used in inner class.
			
			//pop up a dialog that the user can enter text.
			//get the width and height of the text area.
			int width = ta.getWidth();
			int height = ta.getHeight();
			int taX = ta.getX();
			int taY = ta.getY();

			int xCoord = taX + width/2;
			int yCoord = taY + height/2;

			final ColumnInsertDialog dialog = new ColumnInsertDialog(new KeyListener()
			{
				public void keyTyped(KeyEvent e) {
					//check for an enter key
					if (e.getKeyChar() == '\n')
					{
						int startPos = -1;
						int endPos   = -1;
						int [] colNum;
						int cols = -1;
						Selection.Rect rSel = (Selection.Rect)sel[0];
						startPos = rSel.getStartLine();
						endPos = rSel.getEndLine();
						int rows = endPos - startPos;

						int startCol = rSel.getStart(theView.getBuffer(),startPos);
						int endCol = rSel.getEnd(theView.getBuffer(),startPos);

						cols = endCol - startCol;
						colNum = new int[rows+1];
						int j = 0;
						for(int i = startPos; i <= endPos; i++)
							colNum[j++] = rSel.getStart(theView.getBuffer(),i);

						ColumnInsertDialog d =
							(ColumnInsertDialog)((JTextField)e.getSource()).getTopLevelAncestor();

						String text = d.getText();

						d.dispose();
						//Need to do the text insert thing here.
						JEditBuffer buff = theView.getTextArea().getBuffer();
						try
						{
							buff.beginCompoundEdit();
							theView.getTextArea().setSelectedText(""); //clear out current selection
							//get an offset with Buffer.getLineStartOffset();

							int strLen = text.length();

							for(int i = 0; i < colNum.length; i++)
							{
								buff.insert(colNum[i] + i*strLen - i*cols,text);
							}
						}
						finally
						{
							buff.endCompoundEdit();
						}
					}
				}

				public void keyPressed(KeyEvent e)
				{
					//No op
				}

				public void keyReleased(KeyEvent e)
				{
					//No op
				}
			});

			//Set the location that the popup window appears at.
			dialog.setBounds(xCoord,yCoord,dialog.getWidth(),dialog.getHeight());
		}
		else //Selection was either not rectangular, or did not exist.
		{
			view.getToolkit().beep();
		}
	} //}}}
	
	//{{{ textToolsBlockHandling()
	public static void textToolsBlockHandling(View view, JEditTextArea textArea)
	{
		if (isTextAreaEditable(view, textArea) && isSelectedAndRectangular(view, textArea)) {
			new TextToolsBlockHandlingDialog(view);
		} else {
			GUIUtilities.error(view, "texttoolsplugin.error.no-rect-selection", null);
		}
		
		//TODO: Should there be some sort of message given to the user?
	} //}}}
	
	//{{{ spacesToTabsXT() method
	public static void spacesToTabsXT(JEditTextArea textArea) {
		System.out.println("Call SpacesToTabs_XT 1");
		//    TextToolsBlockHandling.spacesToTabs(textArea);
	} //}}}
	
	//{{{ Private members
	
	//{{{ isWordChar() method
	private static final boolean isWordChar(char ch, String noWordSep)
	{
		return Character.isLetterOrDigit(ch) ||
			(noWordSep != null && noWordSep.indexOf(ch) != -1);
	} //}}}
	
	//}}}
}
