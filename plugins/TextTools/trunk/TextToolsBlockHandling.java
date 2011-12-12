/*
 * TextToolsBlockHandling.java - a Java class for the jEdit text editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Rudolf Widmann
 * Rudi.Widmann@web.de
 *
 * 1) inserts or fills a given string or number into a selection
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

import org.gjt.sp.jedit.BeanShell;
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
import org.gjt.sp.util.StandardUtilities;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.border.*;
//}}}

public class TextToolsBlockHandling
{
	
	//{{{ doBlockAction() method
	public static void doBlockAction(
		View	view,
		int incrementValue,
		int insertValue,
		String insertText,
		boolean overwriteBlock,
		boolean increment,
		boolean leadingZeros)
	{
		BeanShellUtility bsu = new BeanShellUtility(view);
		JEditTextArea textArea = view.getTextArea();
		int startRange;
		int endRange;
		boolean trace=true;
		JEditBuffer buffer = view.getBuffer();
		/*********************************************************
		 * evaluate selection
		 *********************************************************/
		Selection[] sel = textArea.getSelection();
		int selCount = textArea.getSelectionCount();
		int selBegin;
		int selEnd;
		// check anything is selected
		if (selCount == 0 )
		{
			//Note: The TextToolsPlugin class now prevents 
			//this case from happening.
			
			selBegin = bsu.getVisibleRow(textArea.getCaretPosition());
			// Log.log(Log.DEBUG, BeanShell.class,"selBegin = "+selBegin);
			selEnd = selBegin + insertText.length();
			textArea.goToBufferEnd(true);
		} else {
			// get range, maybe different per line ico multiple/ rect selections
			// for several reasons, we take the width of the first selected line, and apply it to all selections
			// selBegin = bsu.getVisibleRow(sel[0].getStart());
			// selEnd = bsu.getVisibleRow(sel[0].getEnd(buffer, sel[0].getStartLine()));
			
			//Get the correct start column and end column of the selection.
			//Note: The TextToolsPlugin class ensures
			//that the selection is rectangular.
			selBegin = ((Selection.Rect)sel[0]).getStartColumn(buffer);
			selEnd = ((Selection.Rect)sel[0]).getEndColumn(buffer);
		}
		
		/*********************************************************
		 * check if only one line selected ==> select form cursor to eof
		 *********************************************************/
		if (selCount == 1 && (sel[0].getStartLine() == sel[0].getEndLine()))
		{
			//Note: The TextToolsPlugin now prevents 
			//this case from happening.
			if (trace)
				Log.log(Log.DEBUG, TextToolsBlockHandling.class,"only one selection line");
			textArea.setCaretPosition(sel[0].getStart());
			textArea.goToBufferEnd(true);
		}
		/*********************************************************
		 * check insert text matches selection
		 *********************************************************/
		int targetLen = selEnd - selBegin;
		if (!increment && overwriteBlock) // insert shall match selection exactly
		{
			if (insertText.length() < targetLen)
				insertText = insertText + StandardUtilities.createWhiteSpace(targetLen - insertText.length(), 0);
			else if (insertText.length() > targetLen)
				insertText = insertText.substring(0,targetLen);
		}
		/*********************************************************
		 * prepare increment filling string
		 *********************************************************/
		String nullsBlanksString=null;
		int currentIncValue=insertValue;
		if (increment)
		{
			if (leadingZeros)
			{
				StringBuilder nulBuf = new StringBuilder("0000000000");
				for (int s=10; s<targetLen;s+=10)
					nulBuf.append("0000000000");
				nullsBlanksString = nulBuf.toString();
			} else {
				nullsBlanksString = StandardUtilities.createWhiteSpace(targetLen, 0);
			}
		}
		/*********************************************************
		 * begin text manipulations
		 *********************************************************/
		int[] selLines = textArea.getSelectedLines();
		String sourceString = insertText;
		// Note: from now on, we don't need the selections
		textArea.selectNone();  // reset selection
		StringBuilder blankBuf = new StringBuilder("          ");
		buffer.beginCompoundEdit();
		ArrayList targetSelection = new ArrayList();  // collects Selection
		for (int i=0;i<selLines.length;i++)
		{
			/*********************************************************
			 * handle tabs: replace with spaces if tabs exist
			 *********************************************************/
			boolean isWithTabs = false;
			if (buffer.getLineText(selLines[i]).indexOf("\t") != -1)
			{
				// line contains tabs
				isWithTabs = true;
				bsu.selectLine(selLines[i]);
				textArea.tabsToSpaces();
			}
			/*********************************************************
			 * calculate insert string
			 *********************************************************/
			if (increment) 
			{
				String valString = Integer.toString(currentIncValue);
				String pfxString;
				if (targetLen - valString.length() > 0) 
				{
					pfxString = nullsBlanksString.substring(0,targetLen - valString.length());
				} else
					pfxString = "";
				if (currentIncValue < 0 && leadingZeros) 
				{
					// minus dash should be at beginning of zeros
					sourceString = "-" + pfxString + valString.substring(1);
				} else {
					sourceString = pfxString + valString;
				}
				if (sourceString.length() > targetLen)
					sourceString = sourceString.substring(sourceString.length() - targetLen);
			}
			/*********************************************************
			 * check if linelength too short: add missing blanks
			 *********************************************************/
			int lineEndOffset = textArea.getLineEndOffset(selLines[i]);
			int insertPos = textArea.getLineStartOffset(selLines[i])+selBegin;
			int missingBlanks = insertPos - lineEndOffset+1; // +1'cause of <cr>
			String prefixString = "";
			if (missingBlanks > 0) 
			{
				while (blankBuf.length() < missingBlanks) blankBuf.append("          ");
				prefixString = blankBuf.substring(0,missingBlanks);
				insertPos = insertPos - missingBlanks;
				// Log.log(Log.DEBUG, BeanShell.class,"*3 insertPos = "+insertPos);
			}
			/*********************************************************
			 * finally, do modification
			 *********************************************************/
			if (overwriteBlock && lineEndOffset > 0) 
			{
				// dont remove chars that dont exist ==> calc remove len
				int removeDecrement = insertPos +
						       prefixString.length() + 
						       sourceString.length() - 
						       (lineEndOffset-1);
				if (removeDecrement < 0) 
					removeDecrement = 0;
				buffer.remove(insertPos, prefixString.length() + sourceString.length() - removeDecrement);
			}
			buffer.insert(insertPos,prefixString+sourceString);

			if (isWithTabs) 
			{
				// change back to tabs
				bsu.selectLine(selLines[i]);
				// Note: as long as there are bugs in 
				//textArea.spacesToTabs, a local patch is used
				textArea.spacesToTabs();
			}
			/*********************************************************
			 * memorize selection
			 *********************************************************/
			int newBeginSelection = insertPos+prefixString.length();
			if (newBeginSelection > buffer.getLength())
				newBeginSelection = buffer.getLength();
			int newEndSelection = insertPos+prefixString.length() + sourceString.length();
			if (newEndSelection > buffer.getLength())
				newEndSelection = buffer.getLength();
			targetSelection.add(new Selection.Range(newBeginSelection, newEndSelection));
			// textArea.addToSelection(new Selection.Rect(insertPos, insertPos+prefixString.length()+
			// sourceString.length()));
			if (increment)
				currentIncValue += incrementValue;
		}
		/*********************************************************
		 * set selection to changed chunks
		 *********************************************************/
		 Selection[] newSel = new Selection[targetSelection.size()];
		 newSel = (Selection[])targetSelection.toArray(newSel);
		 textArea.setSelection(newSel);
		 buffer.endCompoundEdit();
	} //}}}
	
	//{{{ getTempIntProperty() method
	
	String getTempIntProperty(String prop) 
	{
		String ret = jEdit.getProperty(prop);
		if (ret == null) 
			return "";
		else
			return ret;
	} //}}}

// this single line of code is the script's main routine
// it calls the methods and exits
// displayInsertFillDialog();

/*
	Macro index data (in DocBook format)
 * - inserts or fills a given <source> into a <target box>
 * - the source is either
 *   * a string
 *   * a number to be incremented
 * - the target depends on the selection
 *   * no selection
 *   < in a selected area
 *   <
 * or all lines

<listitem>
    <para><filename>TextToolsBlockHandling.java</filename></para>
    <abstract><para>
	Inserts or replaces, fills or increments, a given string or number, into a selection block.
	Dialog driven
    </para>
    </abstract>
    <para>
    Inserts or replaces, fills or increments, a given <quote>text</quote> into a <quote>target box</quote>
    </para>
    <para>When invoked, a dialog pops-up, with the fields:
    * <quote>Text to be inserted</quote>: text you want to insert
    * <quote>Increment</quote>: int value serving as increment
    * <quote>Overwrite checkbox</quote>: checked: Overwrite selection, not checked: insert
    * <quote>Leading Zeros checkbox</quote>: checked: Fill unused digits with nulls , not checked: fill with blanks
    </para>
    <para>
    <quote>text</quote> is either:
      * a string
      * a number to be incremented
    </para>
    <para>
    the <quote>target box</quote> depends on the selection:
      * no selection: a box starting at the caret position till EOF, width of <quote>insert text</quote>
      * selection in one line: a box starting at the caret position till EOF, width as selected
      * rect selection: box as selected, width
    </para>
    <para>
    If the insertion doesnot match the target box, it is filled with blanks, or cut.
    </para>
    <para>
    Multiple selection is supported, but the width is according the the first selection
    </para>
</listitem>

*/

}