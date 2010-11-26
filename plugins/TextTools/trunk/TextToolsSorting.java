/*
 * TextToolsSorting.java - Sorting for a number of text related functions
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2001 mike dillon
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

//{{{ Import
import java.util.*;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}

public class TextToolsSorting
{
	//{{{ sortLines() method
	public static void sortLines(JEditTextArea textArea, boolean reverse)
	{
		if (!TextToolsPlugin.isTextAreaEditable(null, textArea))
			return;
		/*
		if(!textArea.isEditable())
		{
			textArea.getToolkit().beep();
			return;
		} */
		
		// check if we have rect selection
		int[] selRows =	TextToolsSorting.getRectSelectionRows(textArea);
		if (selRows != null) 
		{
			// we have rectangular selection: assign values to 1st row of table
			JSort jsort = new JSort(false);
			jsort.addSortConstraint(selRows[0]+1, selRows[1]+1, !reverse);
			//, ignoreCase, textType, trim, delDupRange);
			TextToolsSorting.sortAndInsertData(textArea, jsort, false);
		}
		else
		{
			JEditBuffer b = textArea.getBuffer();
			b.beginCompoundEdit();
			int[] lines = textArea.getSelectedLines();
			if(lines.length > 1)
			{
				sortLines(b, lines, reverse);
			}
			else
			{
				sortLines(b, reverse);
			}
			b.endCompoundEdit();
		}
	} //}}}
	
	//{{{ sortLines() method
	public static void sortLines(JEditBuffer d, boolean reverse)
	{
		int[] lIndices = new int[d.getLineCount()];
		
		for (int i = 0; i < lIndices.length; ++i)
		{
			lIndices[i] = i;
		}
		
		sortLines(d, lIndices, reverse);
	} //}}}
	
	//{{{ sortLines() method
	public static void sortLines(JEditBuffer d, int[] lIndices, boolean reverse)
	{
		String[] lines = new String[lIndices.length];
		
		for (int i = 0; i < lines.length; i++)
		{
			lines[i] = d.getLineText(lIndices[i]);
		}
		
		java.util.Comparator<String> compare = new StandardUtilities.StringCompare<String>(false);
		if (reverse)
			compare = new ReverseCompare<String>(compare);
		
		Arrays.sort(lines, compare);
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lines.length - 1 ; ++i)
		{
			sb.append(lines[i]).append('\n');
		}
		sb.append(lines[lines.length - 1]);
		
		int start = d.getLineStartOffset(lIndices[0]);
		int length = d.getLineEndOffset(lIndices[lIndices.length - 1]) - start - 1;
		d.remove(start, length);
		d.insert(start, sb.toString());
	} //}}}
	
	//{{{ ReverseCompare class
	/**
	 * A wrapper that reverses a sort.
	 */
	static class ReverseCompare<E> implements java.util.Comparator<E>
	{
		//{{{ ReverseCompare constructor
		ReverseCompare(java.util.Comparator<E> comp)
		{
			this.comp = comp;
		} //}}}
		
		//{{{ compare() method
		public int compare(E obj1, E obj2)
		{
			return comp.compare(obj2,obj1);
		}//}}}
		
		//{{{ Private members
		private java.util.Comparator<E> comp;
		//}}}
	} //}}}
	
	//{{{ sortAdvanced() method
	public static void sortAdvanced(View view, JEditTextArea textArea)
	{
		if (!TextToolsPlugin.isTextAreaEditable(view, textArea)) 
			return;
		new TextToolsSortDialog(view, textArea);
	} //}}}
	
	//{{{ deleteDuplicates() method
	public static void deleteDuplicates(View view, JEditTextArea textArea)
	{
		if (!TextToolsPlugin.isTextAreaEditable(view, textArea))
			return;
		
		JSort jsort = new JSort(true, true);  // deldups=true, dontsort=true
		// check if we have rect selection
		int[] selRows =	TextToolsSorting.getRectSelectionRows(textArea);
		if (selRows == null)
			selRows = new int[] {-1,-1};
		
		//	addSortConstraint(int startColumn, int endColumn, boolean ascending, 
		// boolean ignoreCase, boolean numeric, boolean trimWhitespace, boolean delDupRange)
		jsort.addSortConstraint(selRows[0]+1, selRows[1]+1, true, false, false, false, true);
		TextToolsSorting.sortAndInsertData(textArea, jsort, false);
	} //}}}
	
	//{{{ shuffleLines() method
	public static void shuffleLines(View view, JEditTextArea textArea)
	{
		ArrayList recs = getData(textArea);
		if (recs == null)
		{
			// an error occurred, so ditch
			return;
		}

		JSort.shuffle(recs);
		Iterator iter = recs.iterator();
		StringBuffer sb = new StringBuffer();
		while (iter.hasNext())
		{
			sb.append(iter.next() + "\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		if (textArea.getSelectedLines().length > 1)
		{
			textArea.setSelectedText(sb.toString());
		}
		else
		{
			textArea.setText(sb.toString());
		}
	} //}}}
	
	//{{{ sortAndInsertData() method
	public static boolean sortAndInsertData(JEditTextArea textArea, JSort jsort, boolean sortOnlySelection)
	{
		List data = TextToolsSorting.getData(textArea, sortOnlySelection);
		if (data == null)
		{
			// an error occurred, so ditch
			return false;
		}
		else if (data.size() == 1)
		{
			// lets not sort a single record
			return false;
		}
		
		//display data
		if (TextToolsPlugin.debugTT) {
			Iterator di = data.iterator();
			while(di.hasNext())
			{
				Log.log(Log.DEBUG, BeanShell.class,"TextToolsSortDialog.231: di.next() = "+di.next());
			}
		}
		
 		jsort.sort(data);
		Iterator iter = data.iterator();
		StringBuffer sb = new StringBuffer();
		while (iter.hasNext())
		{
			sb.append(iter.next() + "\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		if (textArea.getSelectionCount() > 0)
		{
			textArea.setSelectedText(sb.toString());
		}
		else
		{
			textArea.setText(sb.toString());
		}
		return true;
	} //}}}
	
	//{{{ Protected members
	
	//{{{ getData() method
	static ArrayList getData(JEditTextArea textArea) {
		return getData(textArea, false);
	} //}}}
	
	//{{{ getData() method
	static ArrayList getData(JEditTextArea textArea, boolean onlyRectSelection)
	{
		JEditBuffer buffer = textArea.getBuffer();
		if (buffer.isReadOnly())
		{
			Log.log(Log.NOTICE, TextToolsSorting.class, jEdit.getProperty(
				"texttoolsplugin.error.isReadOnly.message"));
			textArea.getToolkit().beep();
			return null;
		}
		
		int[] rectSelRows=null;
		if (onlyRectSelection) 
		{
			rectSelRows = getRectSelectionRows(textArea);
			if (rectSelRows == null) 
			{
				Log.log(Log.NOTICE, TextToolsSorting.class, jEdit.getProperty(
					"texttoolsplugin.error.no-rect-selection.message"));
				textArea.getToolkit().beep();
				return null;
			}
			if (TextToolsPlugin.debugTT)
				Log.log(Log.DEBUG, BeanShell.class,"TextToolsSorting.199: rectSelRows = "+rectSelRows[0]+", "+rectSelRows[1]);
		}
				
		//   not: String data = textArea.getSelectedText();
		//  ==> get text of all selected lines !!
		ArrayList recs = new ArrayList();
		Selection[] selection = textArea.getSelection();
		if (selection == null || selection.length == 0)
		{
			// nothing selected
			if (buffer.getLength() == 0)
			{
				Log.log(Log.NOTICE, TextToolsSorting.class, jEdit.getProperty(
					"texttoolsplugin.error.empty.message"));
				textArea.getToolkit().beep();
				return null;
			}
			
			for (int i=0;i<buffer.getLineCount();i++) {
				recs.add(textArea.getLineText(i));
			}
		}
		else
		{
			if (selection.length > 1)
			{
				Log.log(Log.NOTICE, TextToolsSorting.class, "multiselection not allowed");
				textArea.getToolkit().beep();
				return null;
			}
			else
			{
				// we have a single selection: store all selected lines
				Selection sel0 = selection[0];
				if (sel0.getEnd() == sel0.getStart(buffer, sel0.getEndLine()))
				{
					// last line is not selected
					 sel0 = new Selection.Range(sel0.getStart(), sel0.getEnd()-1); // doesnt work ?
				}
				
				// sel0.getStartLine() doesnt work for range selections
				int selStartLine = buffer.getLineOfOffset(sel0.getStart());
				int selEndLine = buffer.getLineOfOffset(sel0.getEnd());
				for (int i=selStartLine;i<=selEndLine;i++)
				{
					if (onlyRectSelection) 
						recs.add(textArea.getLineText(i).substring(rectSelRows[0],rectSelRows[1]));
					else 
						recs.add(textArea.getLineText(i));
				}
			
				if (!onlyRectSelection)
				{
					// reset selection to ensure correct replacement
					sel0 = new Selection.Range(buffer.getLineStartOffset(selStartLine),
						buffer.getLineEndOffset(selEndLine)-1);
						textArea.setSelection(sel0);
				}
			}
		}
		return recs;
	} //}}}
	
	//{{{ getSubstringOrSpaces() method
	static String getSubstringOrSpaces(String s, int beginIndex, int endIndex)
	{
		int actLen = s.length();
		int reqLen = endIndex - beginIndex;
		if (actLen <= beginIndex) 
			return StandardUtilities.createWhiteSpace(reqLen, 0);
		if (actLen < endIndex) 
			return s.substring(beginIndex) + StandardUtilities.createWhiteSpace(reqLen-actLen, 0);
		return s.substring(beginIndex, endIndex); 
	} //}}}
	
	//{{{ getRectSelectionRows() method
	static int[] getRectSelectionRows(JEditTextArea textArea)
	{
		Selection[] selection = textArea.getSelection();
		if (selection.length != 0 && selection[0] instanceof Selection.Rect)
		{
			// rect selection: retrieve left and right borders
			JEditBuffer buffer = textArea.getBuffer();
			int[] selRows = new int[2];
			int selLine = selection[0].getStartLine();
			selRows[0] = selection[0].getStart(buffer, selLine) - buffer.getLineStartOffset(selLine);
			selRows[1] = selection[0].getEnd(buffer, selLine) - buffer.getLineStartOffset(selLine) - 1;
			return selRows;
		} else
			return null;
	}//}}}
	
}
