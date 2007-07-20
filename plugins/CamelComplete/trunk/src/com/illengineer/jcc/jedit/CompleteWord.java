/*
 * CompleteWord.java - Complete word dialog
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001 Slava Pestov
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

package com.illengineer.jcc.jedit;

//{{{ Imports
import com.illengineer.jcc.*;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;

import java.awt.event.KeyEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.syntax.KeywordMap;

import org.gjt.sp.jedit.textarea.*;

import org.gjt.sp.util.StandardUtilities;
//}}}

/**
 * A completion popup class.
 */
public class CompleteWord extends CompletionPopup
{
	private static int caretPos;
	private static String wordToComplete;
	
	//{{{ completeWord() method
	// MODIFIED - jpavel
	public static void completeWord(View view, List<CompletionEngine> engines)
	{
		JEditTextArea textArea = view.getTextArea();
		Buffer buffer = view.getBuffer();
		int caretLine = textArea.getCaretLine();
		int caret = textArea.getCaretPosition();

		if(!buffer.isEditable())
		{
			textArea.getToolkit().beep();
			return;
		}

		String word = getWordToComplete(buffer,caretLine, caret);
		if(word == null)
		{
			textArea.getToolkit().beep();
			return;
		}

		
		ArrayList<String> completions = new ArrayList<String>();
		for (CompletionEngine engine : engines) {
			List<String> c = engine.complete(word, true);
			if (c != null)
				completions.addAll(c);
		}
			
		if (completions.size() == 0)
			return;

		//{{{ if there is only one competion, insert in buffer
		if(completions.size() == 1)
		{
			textArea.setSelection(new Selection.Range(caret - word.length(), caret));
			textArea.setSelectedText(completions.get(0));
		} //}}}
		//{{{ show popup if > 1
		else if (completions.size() > 1)
		{
			textArea.scrollToCaret(false);
			Point location = textArea.offsetToXY(caret - word.length());
			location.y += textArea.getPainter().getFontMetrics().getHeight();

			SwingUtilities.convertPointToScreen(location,
				textArea.getPainter());
			wordToComplete = word;
			caretPos = caret;
			new CompleteWord(view,completions,location);
		} //}}}
	} //}}}

	//{{{ CompleteWord constructor
	public CompleteWord(View view, List<String> completions, Point location)
	{
		super(view, location);

		this.view = view;
		this.textArea = view.getTextArea();
		this.buffer = view.getBuffer();

		reset(new Words(completions), true);
	} //}}}

	//{{{ Private members

	//{{{ getWordToComplete() method
	// MODIFIED - jpavel
	private static String getWordToComplete(Buffer buffer, int caretLine, int caret)
	{
	    String line = buffer.getLineText(caretLine);
	    int dot = caret - buffer.getLineStartOffset(caretLine);
	    if(dot == 0)
		    return null;

	    char ch = line.charAt(dot-1);
	    if(!Character.isLetter(ch)) {
		    // I only expand letters
		    return null;
	    }

	    int wordStart = TextUtilities.findWordStart(line,dot-1,"");
	    String word = line.substring(wordStart,dot);
	    if(word.length() == 0)
		    return null;

	    return word;
	} //}}}

	//{{{ Instance variables
	private View view;
	private JEditTextArea textArea;
	private Buffer buffer;
	//}}}

	//{{{ Words class
	private class Words implements Candidates
	{
		private final DefaultListCellRenderer renderer;
		private final List<String> completions;

		public Words(List<String> completions)
		{
			this.renderer = new DefaultListCellRenderer();
			this.completions = completions;
		}

		public int getSize()
		{
			return completions.size();
		}

		public boolean isValid()
		{
			return true;
		}

		public void complete(int index)
		{
			textArea.setSelection(new Selection.Range(caretPos - wordToComplete.length(), caretPos));
			textArea.setSelectedText((String)completions.get(index));
		}
	
		public Component getCellRenderer(JList list, int index,
			boolean isSelected, boolean cellHasFocus)
		{
			renderer.getListCellRendererComponent(list,
				null, index, isSelected, cellHasFocus);

			String text = (String)completions.get(index);

			Font font = list.getFont();

			if(index < 9)
				text = (index + 1) + ": " + text;
			else if(index == 9)
				text = "0: " + text;
			else if (index < 36) {
				char ch = (char)(index - 10 + 'a');
				text = ch + ": " + text;
			}
			else if (index < 62) {
				char ch = (char)(index - 36 + 'A');
				text = ch + ": " + text;
			}

			renderer.setText(text);
			renderer.setFont(font);
			return renderer;
		}

		public String getDescription(int index)
		{
			return null;
		}
	} //}}}

	//}}}

	//{{{ keyPressed() medhod
	protected void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			textArea.backspace();
			e.consume();

			dispose();
		}
	} //}}}

	//{{{ keyTyped() medhod
	protected void keyTyped(KeyEvent e)
	{
		char ch = e.getKeyChar();
		if(Character.isDigit(ch) || (ch >= 'A' && ch <= 'z'))
		{
			int index = 0;
			
			if(Character.isDigit(ch)) {
				index = ch - '0';
				if(index == 0)
					index = 9;
				else
					index--;
			} else {
				if (ch >= 'a') 
					ch -= 0x20;
				else
					ch += 26;
				index = ch - 'A' + 10;
			}
			if(index < getCandidates().getSize())
			{
				setSelectedIndex(index);
				if(doSelectedCompletion())
				{
					dispose();
				}
				return;
			}
			else {
				e.consume();
				dispose();
				return;
			}
		}

		// \t handled above
		if(ch != '\b' && ch != '\t')
		{
			textArea.userInput(ch);
			e.consume();
			dispose();
		}
	} //}}}
}
