/*
 * :folding=explicit:collapseFolds=1:
 * XSearchBeanShellUtility.java 
 * 1) Utilities useful for BeanShell applications
 * 2) converts spaces to tabs
 *	based on:
 *	- TextUtilities.spacesToTabs
 *	- MiscUtilities.createWhiteSpace

 * Copyright (C) 2002 Rudolf Widmann
 * Rudi.Widmann@web.de
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
package xsearch;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

public class XSearchBeanShellUtility {
    View 		view;
    JEditTextArea	textArea;
    Buffer		buffer;

//{{{ public XSearchBeanShellUtility(View view)
	/**
     * Constructs object
     * sets textArea, buffer
     * @param view
     */
    public XSearchBeanShellUtility(View view) {
      this.view = view;
      textArea = view.getTextArea();
      buffer = view.getBuffer();
    }
 //}}}

//{{{ int getRow(int bufferPosition)
    /**
     * Find the column of the passed buffer position
     * @param bufferPosition
     */
    public int getRow(int bufferPosition) {
      int lineNbr = textArea.getLineOfOffset(bufferPosition);
      int startLineOffset = textArea.getLineStartOffset(lineNbr);
      return bufferPosition - startLineOffset;
    }
    public static int getRow(View view, int bufferPosition) {
      XSearchBeanShellUtility bsu = new XSearchBeanShellUtility(view);
      return bsu.getRow(bufferPosition);
    }
 //}}}

//{{{ int getVisibleRow
	/**
     * Find the visible column of the passed buffer position
     * tabs are expanded according to buffers tabSize
     * @param bufferPosition
     */
    public int getVisibleRow(int bufferPosition) {
      int lineNbr = textArea.getLineOfOffset(bufferPosition);
      return getVisiblePosition(buffer.getTabSize(),
	getRow(bufferPosition),
	textArea.getLineText(lineNbr)
	);
    }
    public static int getVisibleRow(View view, int bufferPosition) {
      XSearchBeanShellUtility bsu = new XSearchBeanShellUtility(view);
      return bsu.getVisibleRow(bufferPosition);
    }
 //}}}

//{{{ int goToLineRow
	/**
     * Set cursor to the given line/row position
     * selects lines, acording to given parameter
     * @param bufferPosition
     * returns new cursor position
     */
    public int goToLineRow(int line, int row, boolean select) {
      int caret = textArea.getCaretPosition();
      int newCaret = buffer.getLineStartOffset(line) + row;
      if (select) textArea.extendSelection(caret, newCaret);
      textArea.moveCaretPosition(newCaret,true);
      return newCaret;
    }
    public static int goToLineRow(View view, int line, int row, boolean select) {
      XSearchBeanShellUtility bsu = new XSearchBeanShellUtility(view);
      return bsu.goToLineRow(line, row, select);
    }
 //}}}

//{{{ selectLine(int lineNbr)
     /**
     * Select given linenumber
     * @param lineSelector Line number
     */
    void selectLine(int lineNbr) {
      textArea.setSelection(
	  new Selection.Range(textArea.getLineStartOffset(lineNbr),
		textArea.getLineEndOffset(lineNbr)));
    }
 //}}}

//{{{ int getVisiblePosition(int tabWidth, int realPos, String line) {
	/**
	 * gets the visible column (tabs expanded) of the given String
	 * @param tabWidth tabulator width
	 * @param realPos physical column position
	 * @param line current line
	 */
	public static int getVisiblePosition(int tabWidth, int realPos, String line) {
		//Log.log(Log.DEBUG, BeanShell.class,"tabWidth = "+tabWidth+", realPos = "+realPos+", line = "+line);
	  int delta = 0;  // difference between tabPos and visible Pos
	  for (int i=0;i<realPos && i<line.length(); i++) {
	    if (line.charAt(i) == '\t') { // tab found
	      int spaceNbr = (tabWidth*(1+(i+delta)/tabWidth)) - (i+delta);
	      delta += spaceNbr - 1; // tab counts for 1
	    }
	  }
		//Log.log(Log.DEBUG, BeanShell.class,"realPos+delta = "+(realPos+delta));
	  return realPos + delta;
	}
 //}}}

//{{{ int getSelectionLine(int lineNbr, Selection currSelection)
    /**
	 * gets the selected text of the given line of the given selection
	 * @param lineNbr line Number
	 * @param currSelection current Selection
     */
   public String getSelectionLine(int lineNbr, Selection currSelection) {
      int startSelectionOffset = currSelection.getStart(buffer, lineNbr);
      int endSelectionOffset = currSelection.getEnd(buffer, lineNbr);
      return textArea.getText(startSelectionOffset,
	currSelection.getEnd(buffer, lineNbr) - startSelectionOffset);
    }

	/**
	 * static version
	 */
	public String getSelectionLine(View view, int lineNbr, Selection currSelection) {
	  XSearchBeanShellUtility bsu = new XSearchBeanShellUtility(view);
	  return bsu.getSelectionLine(lineNbr, currSelection);
	}
 //}}}

 //{{{ int getSelectionVisibleStartColumn(int lineNbr, Selection currSelection)
    /**
	 * gets the visible start column of the given line of the given selection
	 * @param lineNbr line Number
	 * @param currSelection current Selection
     */
    public int getSelectionVisibleStartColumn(int lineNbr, Selection currSelection) {
      int startLineOffset = textArea.getLineStartOffset(lineNbr);
      int startSelectionOffset = currSelection.getStart(buffer, lineNbr);
      return getVisiblePosition(buffer.getTabSize(),
	startSelectionOffset - startLineOffset,
	textArea.getLineText(lineNbr)
	);
    }
	/**
	 * static version
	 */
	public static int getSelectionVisibleStartColumn(View view, int lineNbr,
		Selection currSelection) {
	  XSearchBeanShellUtility bsu = new XSearchBeanShellUtility(view);
	  return bsu.getSelectionVisibleStartColumn(lineNbr, currSelection);
	}
 //}}}

//{{{ getSelectionVisibleEndColumn(int lineNbr, Selection currSelection)
	/**
	 * gets the visible end column of the given line of the given selection
	 * @param lineNbr line Number
	 * @param currSelection current Selection
     */
    int getSelectionVisibleEndColumn(int lineNbr, Selection currSelection) {
      int startLineOffset = textArea.getLineStartOffset(lineNbr);
      int endSelectionOffset = currSelection.getEnd(buffer, lineNbr);
      return getVisiblePosition(buffer.getTabSize(),
	endSelectionOffset - startLineOffset,
	textArea.getLineText(lineNbr)
	);
    }
	/**
	 * static version
	 */
	public static int getSelectionVisibleEndColumn(View view, int lineNbr,
		Selection currSelection) {
	  XSearchBeanShellUtility bsu = new XSearchBeanShellUtility(view);
	  return bsu.getSelectionVisibleEndColumn(lineNbr, currSelection);
	}
 //}}}

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
		StringBuffer buf = new StringBuffer();
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

	//{{{ spacesToTabs() method (TextUtilities)
	/**
	 * Converts consecutive spaces to tabs in the specified string.
	 * Correction of TextUtilities.spacesToTabs
	 * @param in The string
	 * @param tabSize The tab size
	 * @param startOfLineOffset Offset relativ to start of line
	 */
	public static String TextUtilities_spacesToTabs(String in, int tabSize, int startOfLineOffset)
	{
		StringBuffer buf = new StringBuffer();
		int width = startOfLineOffset;	// visible start of target string (selection)
		int whitespace = 0;
		for(int i = 0; i < in.length(); i++)
		{
			switch(in.charAt(i))
			{
			case ' ':
				whitespace++;
				width++;
				break;
			case '\t':
				// change all leading whitespace into tabs
				// ignore ws < tabsize
				width -= whitespace; //
				for (int j = 0;j<(whitespace/tabSize+1);j++) {
				  buf.append('\t');
				  width += tabSize;
				}
				width -= (width % tabSize);
				whitespace = 0;
				break;
			default:
				if(whitespace != 0)
				{
					buf.append(MiscUtilities_createWhiteSpace(
						whitespace, tabSize, width-whitespace));
					whitespace = 0;
				}
				buf.append(in.charAt(i));
				if (in.charAt(i) == '\n')
					width = startOfLineOffset;
				else	width++;
				break;
			}
		}

		if(whitespace != 0)
		{
			buf.append(MiscUtilities_createWhiteSpace(whitespace, tabSize, width-whitespace));
		}
		return buf.toString();
	} //}}}

	//{{{ spacesToTabs() method (JEditTextArea)
	/**
	 * Converts spaces to tabs in the selection.
	 * @since jEdit 2.7pre2
	 */
	public  void JEditTextArea_spacesToTabs() {
		Selection[] selection = textArea.getSelection();
		if(!buffer.isEditable())
		{
			view.getToolkit().beep();
			return;
		}

		buffer.beginCompoundEdit();

		if(selection.length == 0)
		{
			textArea.setText(TextUtilities_spacesToTabs(
				textArea.getText(), buffer.getTabSize(), 0));
		}
		else
		{
			for(int i = 0; i < selection.length; i++)
			{
			  Selection s = selection[i];

			  StringBuffer changedBuffer = new StringBuffer();
			  for (int selLine = s.getStartLine(); selLine <= s.getEndLine(); selLine++) {
			    int visibleStartOfSelection = getSelectionVisibleStartColumn(selLine, s);
			    changedBuffer.append(TextUtilities_spacesToTabs(
				  getSelectionLine(selLine, s), buffer.getTabSize(),
				  visibleStartOfSelection));
			    if (selLine != s.getEndLine()) changedBuffer.append("\n");
			  }
			  textArea.setSelectedText(s,changedBuffer.toString());
			}
		}

		buffer.endCompoundEdit();
	}
	public static void spacesToTabs(View view) {
	  XSearchBeanShellUtility bsu = new XSearchBeanShellUtility(view);
	  bsu.JEditTextArea_spacesToTabs();
	} //}}}
}


