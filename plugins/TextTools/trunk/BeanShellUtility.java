/*
 * BeanShellUtility.java - a Java class for the jEdit text editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Rudolf Widmann
 * Rudi.Widmann@web.de
 *
 * 1) Utilities useful for BeanShell applications
 * 2) converts spaces to tabs
 *	based on:
 *	- TextUtilities.spacesToTabs
 *	- MiscUtilities.createWhiteSpace
 *
 * Checked for jEdit 4.0 API
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


//{{{ Imports

import java.util.*;

import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.border.*;

//}}}

public class BeanShellUtility 
{
	
	//{{{ Protected members
	
	View		view;
	JEditTextArea	textArea;
	JEditBuffer		buffer;
	
	//}}}
	
	//{{{ BeanShellUtility constructor
	/**
	 * Constructs object
	 * sets textArea, buffer
	 * @param view
	 */
	public BeanShellUtility(View view)
	{
		this.view = view;
		textArea = view.getTextArea();
		buffer = view.getBuffer();
	} //}}}
	
	//{{{ getRow() method
	/**
	 * Find the column of the passed buffer position
	 * @param bufferPosition
	 */
	public int getRow(int bufferPosition)
	{
		int lineNbr = textArea.getLineOfOffset(bufferPosition);
		int startLineOffset = textArea.getLineStartOffset(lineNbr);
		return bufferPosition - startLineOffset;
	} //}}}
	
	//{{{ getRow() method
	public static int getRow(View view, int bufferPosition)
	{
		BeanShellUtility bsu = new BeanShellUtility(view);
		return bsu.getRow(bufferPosition);
	} //}}}
	
	//{{{ getVisibleRow() method
	/**
	 * Find the visible column of the passed buffer position
	 * tabs are expanded according to buffers tabSize
	 * @param bufferPosition
	 */
	public int getVisibleRow(int bufferPosition) 
	{
		int lineNbr = textArea.getLineOfOffset(bufferPosition);
		return getVisiblePosition(buffer.getTabSize(),
			getRow(bufferPosition),
			textArea.getLineText(lineNbr)
		);
	} //}}}
	
	//{{{ getVisibleRow() method
	public static int getVisibleRow(View view, int bufferPosition)
	{
		BeanShellUtility bsu = new BeanShellUtility(view);
		return bsu.getVisibleRow(bufferPosition);
	} //}}}
	
	//{{{ goToLineRow() method
	/**
	 * Set cursor to the given line/row position
	 * selects lines, acording to given parameter
	 * @param bufferPosition
	 * returns new cursor position
	 */
	public int goToLineRow(int line, int row, boolean select)
	{
		int caret = textArea.getCaretPosition();
		int newCaret = buffer.getLineStartOffset(line) + row;
		if (select) textArea.extendSelection(caret, newCaret);
		textArea.moveCaretPosition(newCaret,true);
		return newCaret;
	} //}}}
	
	//{{{ goToLineRow() method
	public static int goToLineRow(View view, int line, int row, boolean select) 
	{
		BeanShellUtility bsu = new BeanShellUtility(view);
		return bsu.goToLineRow(line, row, select);
	} //}}}
	
	//{{{ selectLine() method
	/**
	 * Select given linenumber
	 * @param lineSelector Line number
	 */
	 void selectLine(int lineNbr) 
	 {
		textArea.setSelection(
			new Selection.Range(
				textArea.getLineStartOffset(lineNbr),
				textArea.getLineEndOffset(lineNbr)-1
			)
		);
	 } //}}}
	 
	//{{{ getVisiblePosition() method
	 /**
	  * gets the visible column (tabs expanded) of the given String
	  * @param tabWidth tabulator width
	  * @param realPos physical column position
	  * @param line current line
	  */
	 public static int getVisiblePosition(int tabWidth, int realPos, String line) {
		 int delta = 0;  // difference between tabPos and visible Pos
		 for (int i=0;i<realPos && i<line.length(); i++) {
			 if (line.charAt(i) == '\t') { // tab found
				 int spaceNbr = (tabWidth*(1+(i+delta)/tabWidth)) - (i+delta);
				 delta += spaceNbr - 1; // tab counts for 1
			 }
		 }
		 return realPos + delta;
	 } //}}}
	 
	//{{{ getSelectionLine() method
	/**
	 * gets the selected text of the given line of the given selection
	 * @param lineNbr line Number
	 * @param currSelection current Selection
	 */
	public String getSelectionLine(int lineNbr, Selection currSelection)
	{
		int startSelectionOffset = currSelection.getStart(buffer, lineNbr);
		int endSelectionOffset = currSelection.getEnd(buffer, lineNbr);
		return textArea.getText(
			startSelectionOffset,
			currSelection.getEnd(buffer, lineNbr) - startSelectionOffset
		);
	} //}}}
	
	//{{{ getSelectionLine() method
	/**
	 * static version
	 */
	public String getSelectionLine(View view, int lineNbr, Selection currSelection)
	{
		BeanShellUtility bsu = new BeanShellUtility(view);
		return bsu.getSelectionLine(lineNbr, currSelection);
	} //}}}
	
	//{{{ getSelectionVisibleStartColumn() method
	/**
	 * gets the visible start column of the given line of the given selection
	 * @param lineNbr line Number
	 * @param currSelection current Selection
	 */
	public int getSelectionVisibleStartColumn(int lineNbr, Selection currSelection) {
		int startLineOffset = textArea.getLineStartOffset(lineNbr);
		int startSelectionOffset = currSelection.getStart(buffer, lineNbr);
		return getVisiblePosition(
			buffer.getTabSize(),
			startSelectionOffset - startLineOffset,
			textArea.getLineText(lineNbr)
		);
	} //}}}
	
	//{{{ getSelectionVisibleStartColumn() method
	/**
	 * static version
	 */
	public static int getSelectionVisibleStartColumn(View view, int lineNbr, Selection currSelection)
	{
		BeanShellUtility bsu = new BeanShellUtility(view);
		return bsu.getSelectionVisibleStartColumn(lineNbr, currSelection);
	} //}}}
 	
	//{{{ getSelectionVisibleEndColumn() method
	/**
	 * gets the visible end column of the given line of the given selection
	 * @param lineNbr line Number
	 * @param currSelection current Selection
	 */
	int getSelectionVisibleEndColumn(int lineNbr, Selection currSelection) {
		int startLineOffset = textArea.getLineStartOffset(lineNbr);
		int endSelectionOffset = currSelection.getEnd(buffer, lineNbr);
		return getVisiblePosition(
			buffer.getTabSize(),
			endSelectionOffset - startLineOffset,
			textArea.getLineText(lineNbr)
		);
	} //}}}
	
	//{{{ getSelectionVisibleEndColumn() method
	/**
	 * static version
	 */
	public static int getSelectionVisibleEndColumn(View view, int lineNbr, Selection currSelection)
	{
		BeanShellUtility bsu = new BeanShellUtility(view);
		return bsu.getSelectionVisibleEndColumn(lineNbr, currSelection);
	} //}}}

	//{{{ createWhiteSpace() method (MiscUtilities)
	/**
	 * Creates a string of white space with the specified length.
	 * Correction of MiscUtilities.createWhiteSpace
	 * @param len The length
	 * @param tabSize The tab size, or 0 if tabs are not to be used
	 * @param lineOffset The offset to the beginning of the line (used for tab calculation)
	 */
	public static  String MiscUtilities_createWhiteSpace(int len, int tabSize, int lineOffset)
	{
		StringBuilder buf = new StringBuilder();
		if(tabSize == 0 || len <= 1) // for a single blank don't create a tab
		{
			while(len-- > 0)
				buf.append(' ');
		}
		else
		{
			// offset calculation: if we don't start at a tab-position,
			// if we obtain the first tab, leading characters have to be added to len
			int leadingCharLen = lineOffset % tabSize;
			if (len + leadingCharLen >= tabSize) len += leadingCharLen;
			int count = len / tabSize;
			while(count-- > 0)
				buf.append('\t');
			count = len % tabSize;
			while(count-- > 0)
				buf.append(' ');
		}
		return buf.toString();
	} //}}}

}


