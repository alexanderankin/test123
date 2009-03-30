/*
 * SideKickCompletion.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2005 Slava Pestov
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

package ctagsinterface.jedit;

import javax.swing.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.*;

/**
 * A code completion instance.<p>
 *
 * This is a wrapper around a collection of possible completions, with callbacks
 * for inserting and displaying the completions in a popup menu.
 *
 * @author Slava Pestov
 * @version $Id: SideKickCompletion.java 10427 2007-08-22 22:43:15Z ezust $
 * @since SideKick 0.3
 */
public class EnhancedCompletion
{
	public EnhancedCompletion(View view, String text)
	{
		this.view = view;
		textArea = view.getTextArea();
		this.text = text;
	}

	/**
	 * @param items - a list of strings containing the possible completions.
	 */
	public EnhancedCompletion(View view, String text,
		List<Object> items)
	{
		this(view, text);
		this.items = items;
	}

	public EnhancedCompletion(View view, String text, Object[] items)
	{
		this(view, text);
		this.items = Arrays.asList(items);
	}

	public int size()
	{
		return items.size();
	}

	public Object get(int index)
	{
		return items.get(index);
	}

	public String getCompletionDescription(int index)
	{
		return null;
	}

	/**
	 * @return If this returns false, then we create a new completion
	 * object after user input.
	 */
	public boolean updateInPlace(EditPane editPane, int caret)
	{
		return false;
	}
	
	public ListCellRenderer getRenderer()
	{
		return new DefaultListCellRenderer();
	}

	public void insert(int index)
	{
		String selected = String.valueOf(get(index));
		int caret = textArea.getCaretPosition();
		Selection s = textArea.getSelectionAtOffset(caret);
		int start = (s == null ? caret : s.getStart());
		JEditBuffer buffer = textArea.getBuffer();
		try
		{
			buffer.beginCompoundEdit();
			buffer.remove(start - text.length(),text.length());
			buffer.insert(start - text.length(),selected);
		}
		finally
		{
			buffer.endCompoundEdit();
		}
	}

	// The length of the text being completed (for popup positioning).
	public int getTokenLength()
	{
		return text.length();
	}

	// Characters that accept the selected completion
	protected String getAcceptChars()
	{
		return " \n\t";
	}
	
	// Characters that should be inserted after the completion
	protected String getInsertChars()
	{
		return " ";
	}
	
	/**
	 * @param selectedIndex The index of the selected completion.
	 * @param keyChar the character typed by the user.
	 * @return True if completion should continue, false otherwise.
	 */
	public boolean handleKeystroke(int selectedIndex, char keyChar)
	{
		if (getAcceptChars().indexOf(keyChar) > -1)
		{
			insert(selectedIndex);
			if (getInsertChars().indexOf(keyChar) > -1)
				textArea.userInput(keyChar);
			return false;
		}
		else
		{
			textArea.userInput(keyChar);
			return true;
		}
	}

	protected String getWordAtCaret(EditPane ep, int caret)
	{
		return null;
	}
	
	/**
	 * Returns suitable completions for insertion at the specified
	 * position.
	 *
	 * Returns null by default.
	 *
	 * @param editPane The edit pane involved.
	 * @param caret The caret position.
	 */
	public EnhancedCompletion complete(EditPane editPane, int caret)
	{
		try {
			String[] keywords =
				editPane.getBuffer().getKeywordMapAtOffset(
					caret).getKeywords();
			if (keywords.length > 0) {
				String word = getWordAtCaret(editPane, caret);
				if ((word != null) && (word.length() > 0)) {
					List<String> possibles = new ArrayList<String>();
					for (int i = 0; i < keywords.length; i++) {
						String kw = keywords[i];
						if (kw.startsWith(word) && (! kw.equals(word)))
							possibles.add(keywords[i]);
					}
					Collections.sort(possibles);
					List<Object> completions = new ArrayList<Object>(
						possibles);
					return new EnhancedCompletion(editPane.getView(),
						word, completions);
				}
			}
		}
		catch (Exception e) {
		}
		return null;
	}

	protected View view;
	protected JEditTextArea textArea;
	protected String text;
	protected List<Object> items = new ArrayList<Object>();
}
