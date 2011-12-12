/*
 * TextToolsComments.java - Actions for toggling range and line comments
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Robert Fletcher
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}

/**
 * Actions for toggling range and line comments.
 *
 * @author    <a href="mailto:rfletch6@yahoo.co.uk">Robert Fletcher</a>
 * @version   $Revision$ $Date$
 */
public class TextToolsComments
{
	//{{{ toggleLineComments() method
	/**
	 * Toggles a line comment on or off at all currently selected lines or, if
	 * there are none, at the line where the caret currently sits.
	 *
	 * @param view  a jEdit view.
	 */
	public static void toggleLineComments(View view)
	{
		JEditTextArea textArea = view.getTextArea();
		JEditBuffer buffer = textArea.getBuffer();
		try
		{
			if(!buffer.isEditable())
			{
				view.getToolkit().beep();
				return;
			}

			// get an array of all selected lines, or if there are no selections,
			// just the line of the caret
			Selection[] selections = textArea.getSelection();
			
			int selectionNo;
			if (selections.length < 1)
			{
				selectionNo = 1;
			} 
			else
			{
				selectionNo = selections.length;
			}
			
			int[] lines;
			
			for (int j=0; j < selectionNo; j++)
			{
				
				//Get the lines for this selection.
				if (selections.length < 1)
				{
					lines = new int[]{textArea.getCaretLine()};
				}
				else
				{
					lines = new int[selections[j].getEndLine() - selections[j].getStartLine() + 1];
					for (int i=0; i < lines.length; i++) {
						lines[i] = i + selections[j].getStartLine();
					}
				}
				
				// If we're inserting as block find the leftmost indent
				int leftmost = Integer.MAX_VALUE;
				String line;
				if(jEdit.getBooleanProperty("options.toggle-comments.indentAsBlock"))
				{
					for(int i = 0; i < lines.length; i++)
					{
						line = buffer.getLineText(lines[i]);
						if(line.trim().length() < 1)
						{
							continue;
						}
						leftmost = Math.min(leftmost, StandardUtilities.getLeadingWhiteSpaceWidth(line, buffer.getTabSize()));
					}
				}
	
				// if block commenting we need to check for un-commented lines, if
				// any are found NO lines will be uncommented
				boolean doUncomment = true;
				if(jEdit.getBooleanProperty("options.toggle-comments.commentAsBlock"))
				{
					for(int i = 0; doUncomment && i < lines.length; i++)
					{
						line = buffer.getLineText(lines[i]).trim();
						String lineComment = buffer.getContextSensitiveProperty(buffer.getLineStartOffset(lines[i]), "lineComment");
						if(lineComment == null || lineComment.length() == 0)
						{
							continue;
						}
						if(line.length() > 0 && !line.startsWith(lineComment))
						{
							doUncomment = false;
						}
					}
				}
	
				// loop through each line
				boolean noCommentableLines = true;
				String lineComment = null;
				for(int i = 0; i < lines.length; i++)
				{
					Log.log(Log.DEBUG, TextToolsComments.class, "looping line: "+lines[i]);
					line = buffer.getLineText(lines[i]);
					
					int lineStart = buffer.getLineStartOffset(lines[i]);
					// get position after any leading whitespace
					int pos = lineStart + StandardUtilities.getLeadingWhiteSpace(line);
					if (i == 0) 
					{
						//first time through get the line comment.
						lineComment = buffer.getContextSensitiveProperty(pos + 1, "lineComment");
					}
					
					// skip over blank lines
					if(line.trim().length() < 1)
					{
						continue;
					}
					
					// re-get the lineComment property as it can vary
					if(lineComment == null || lineComment.length() == 0)
					{
						Log.log(Log.DEBUG, TextToolsComments.class, "No line comment: "+lines[i]);
						continue;
					}
					else
					{
						noCommentableLines = false;
					}
	
					lockBuffer(buffer);
					
					// if the first non-whitespace char in the line is the line
					// comment symbol and we are doing an uncomment
					// then, remove, otherwise insert
					if(line.trim().startsWith(lineComment) && doUncomment)
					{
						buffer.remove(pos, lineComment.length());
						if(Character.isWhitespace(buffer.getText(pos, 1).charAt(0)))
						{
							buffer.remove(pos, 1);
						}
					}
					else
					{
						// depending on options, add comment symbol at:
						// - start of line
						if(jEdit.getBooleanProperty("options.toggle-comments.indentAtLineStart"))
						{
							buffer.insert(lineStart, lineComment + " ");
						}
						// - leftmost indent of selected lines
						else if(jEdit.getBooleanProperty("options.toggle-comments.indentAsBlock"))
						{
							Segment seg = new Segment();
							buffer.getLineText(lines[i], seg);
							Log.log(Log.DEBUG, TextToolsComments.class, "commenting line: "+lines[i]);
							buffer.insert(lineStart + StandardUtilities.getOffsetOfVirtualColumn(seg, buffer.getTabSize(), leftmost, null), lineComment + " ");
						}
						// - or after all leading whitespace
						else
						{
							buffer.insert(pos, lineComment + " ");
						}
					}
				}
	
				// if there were no commentable lines, beep & return
				if(noCommentableLines)
				{
					view.getToolkit().beep();
					return;
				}
	
				// optionally retain selection - by default behave as jEdit
				if(!jEdit.getBooleanProperty("options.toggle-comments.keepSelected"))
				{
					Selection[] sArr = textArea.getSelection();
					if(sArr.length > 0)
					{
						textArea.setCaretPosition(sArr[sArr.length - 1].getEnd());
					}
				}
			}
		}
		finally
		{
			unlockBuffer(buffer);
		}
	} //}}}

	//{{{ toggleRangeComments() method
	/**
	 * Toggles all range comments in the specified view by applying {@link
	 * #toggleRangeComment(Buffer, int, int)} to each selection. If there are no
	 * selections {@link #toggleRangeComment(Buffer, int)} will be applied at the
	 * current caret position.
	 *
	 * @param view  a jEdit view.
	 */
	public static void toggleRangeComments(View view)
	{
		JEditTextArea textArea = view.getTextArea();
		JEditBuffer buffer = textArea.getBuffer();
		if(!buffer.isEditable() || !usesRangeComments(buffer))
		{
			view.getToolkit().beep();
			return;
		}

		try
		{
			// get an array of all selections, if there are none, use the method
			// that toggles comments around the caret
			Selection[] selections = textArea.getSelection();
			if(selections.length < 1)
			{
				toggleRangeComment(buffer, textArea.getCaretPosition());
			}

			// loop through each selection
			String selectTxt;
			for(int i = 0; i < selections.length; i++)
			{
				selectTxt = textArea.getSelectedText(selections[i]);
				try
				{
					lockBuffer(buffer);

					// get the start & end of the selection so we can retain it
					int start = selections[i].getStart();
					int end = selections[i].getEnd();

					// toggle comments in this selection
					int length = toggleRangeComment(buffer, start, end);

					// optionally retain selection - by default behave as jEdit
					if(jEdit.getBooleanProperty("options.toggle-comments.keepSelected"))
					{
						textArea.addToSelection(new Selection.Range(start, start + length));
					}
				}
				catch(IndexOutOfBoundsException e)
				{
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					Log.log(Log.ERROR, TextToolsComments.class, sw.toString());
				}
			}
		}
		finally
		{
			unlockBuffer(buffer);
		}
	} //}}}

	//{{{ isInRangeComment() method
	/**
	 * Tests whether the specified buffer index is within a range comment block.
	 *
	 * @param buffer  a jEdit buffer
	 * @param offset  an index within <code>buffer</code>
	 * @return        <code>true</code> if <code>offset</code> is within a range comment,
	 *      <code>false</code> otherwise
	 */
	private static boolean isInRangeComment(JEditBuffer buffer, int offset)
	{
		String preceding = buffer.getText(0, offset);
		String commentStart = buffer.getContextSensitiveProperty(offset, "commentStart");
		String commentEnd = buffer.getContextSensitiveProperty(offset, "commentEnd");
		int lastStart = preceding.lastIndexOf(commentStart);
		int lastEnd = preceding.lastIndexOf(commentEnd);
		return (lastStart != -1 && lastStart > lastEnd);
	} //}}}

	//{{{ lockBuffer() method
	/**
	 * If the specified <code>JEditBuffer</code> is not currently inside a compound
	 * edit, this method will start one, lock the buffer and return <code>true</code>
	 * , otherwise it will return <code>false</code>
	 *
	 * @param buffer  a jEdit buffer
	 * @return        <code>true</code> if this method locked the buffer, <code>false</code>
	 *      if it was already locked
	 */
	private static boolean lockBuffer(JEditBuffer buffer)
	{
		if(!buffer.insideCompoundEdit())
		{
			buffer.writeLock();
			buffer.beginCompoundEdit();
			return true;
		}
		return false;
	}//}}}

	//{{{ toggleRangeComment() method
	/**
	 * Toggles range comments around a specified buffer index. If the index is
	 * within a range comment, the comment block will be un-commented. If the index
	 * is not within a range comment, the method behaves as {@link
	 * #toggleRangeComment(Buffer, int, int)} with <code>start</code> and <code>end</code>
	 * values equal to the start and end indexes of non-whitespace text on the same
	 * line as <code>offset</code> .
	 *
	 * @param buffer  a jEdit buffer.
	 * @param offset  a index in <code>buffer</code> .
	 */
	private static void toggleRangeComment(JEditBuffer buffer, int offset)
	{
		String commentStart = buffer.getContextSensitiveProperty(offset, "commentStart");
		String commentEnd = buffer.getContextSensitiveProperty(offset, "commentEnd");

		if(isInRangeComment(buffer, offset))
		{
			String preceding = buffer.getText(0, offset);
			String following = buffer.getText(offset, buffer.getLength() - offset);
			int start = preceding.lastIndexOf(commentStart);
			int end = following.indexOf(commentEnd) + offset;
			try
			{
				lockBuffer(buffer);
				buffer.remove(end, commentEnd.length());
				buffer.remove(start, commentStart.length());
			}
			finally
			{
				unlockBuffer(buffer);
			}
		}
		else
		{
			int line = buffer.getLineOfOffset(offset);
			String lineTxt = buffer.getLineText(line);
			int start = buffer.getLineStartOffset(line) + StandardUtilities.getLeadingWhiteSpace(lineTxt);
			int end = buffer.getLineEndOffset(line) - (StandardUtilities.getTrailingWhiteSpace(lineTxt) + 1); // the +1 catches the \n
			toggleRangeComment(buffer, start, end);
		}
	} //}}}

	//{{{ toggleRangeComment() method
	/**
	 * Toggles range commenting in a buffer around/within the range from <code>start
	 * </code> to <code>end</code> . In the simplest case comment symbols are added /
	 * removed around the specified range. If, however, there are already range
	 * comments within the specified range, the method will reverse their
	 * commenting state.
	 *
	 * @param buffer  a jEdit buffer.
	 * @param start   the start index of a range within <code>buffer</code> .
	 * @param end     the end index of a range within <code>buffer</code> .
	 * @return        the length of the text the specified range has been replaced
	 *      with.
	 */
	private static int toggleRangeComment(JEditBuffer buffer, int start, int end)
	{
		String commentStart = buffer.getContextSensitiveProperty(start, "commentStart");
		String commentEnd = buffer.getContextSensitiveProperty(start, "commentEnd");
		StringBuilder buf = new StringBuilder(buffer.getText(start, end - start));

		// use a state machine to step through text and reverse commenting
		final byte REMOVE_COMMENT_START = 0;
		final byte REMOVE_COMMENT_END = 1;
		final byte LOOK_FOR_COMMENT_END = 2;
		final byte LOOK_FOR_COMMENT_START = 3;
		final byte INSERT_COMMENT_START = 4;
		final byte INSERT_COMMENT_END = 5;

		byte state;
		if(buf.indexOf(commentStart) == 0)
		{
			state = REMOVE_COMMENT_START;
		}
		else if(buf.indexOf(commentEnd) == 0)
		{
			state = REMOVE_COMMENT_END;
		}
		else if(isInRangeComment(buffer, start))
		{
			state = INSERT_COMMENT_END;
		}
		else
		{
			state = INSERT_COMMENT_START;
		}

		int i = 0;
		boolean atStart;
		while(i > -1 && i < buf.length())
		{
			switch(state)
			{
				case REMOVE_COMMENT_START:
					atStart = i == 0;
					buf.delete(i, i + commentStart.length());
					state = atStart ? LOOK_FOR_COMMENT_END : INSERT_COMMENT_END;
					break;
				case REMOVE_COMMENT_END:
					atStart = i == 0;
					buf.delete(i, i + commentEnd.length());
					state = atStart ? LOOK_FOR_COMMENT_START : INSERT_COMMENT_START;
					break;
				case INSERT_COMMENT_START:
					buf.insert(i, commentStart);
					i += commentStart.length();
					state = LOOK_FOR_COMMENT_START;
					break;
				case INSERT_COMMENT_END:
					buf.insert(i, commentEnd);
					i += commentEnd.length();
					state = LOOK_FOR_COMMENT_END;
					break;
				case LOOK_FOR_COMMENT_END:
					i = buf.indexOf(commentEnd, i);
					if(i == -1)
					{
						buf.append(commentStart);
					}
					else
					{
						state = REMOVE_COMMENT_END;
					}
					break;
				case LOOK_FOR_COMMENT_START:
					i = buf.indexOf(commentStart, i);
					if(i == -1)
					{
						buf.append(commentEnd);
					}
					else
					{
						state = REMOVE_COMMENT_START;
					}
					break;
				default:
					throw new IllegalStateException("unknown state "+state);
			}
		}

		boolean newEdit = false;
		try
		{
			newEdit = lockBuffer(buffer);
			buffer.remove(start, end - start);
			buffer.insert(start, buf.toString());
			return buf.length();
		}
		finally
		{
			// only unlock if this method started the edit
			if(newEdit)
			{
				unlockBuffer(buffer);
			}
		}
	} //}}}

	//{{{ unlockBuffer() method
	/**
	 * If the specified <code>JEditBuffer</code> is currently inside a compound edit,
	 * this method will end it, unlock the buffer and return <code>true</code>,
	 * otherwise it will return <code>false</code>
	 *
	 * @param buffer  a jEdit buffer
	 * @return        <code>true</code> if this method unlocked the buffer, <code>false</code>
	 *      if it was already unlocked
	 */
	private static boolean unlockBuffer(JEditBuffer buffer)
	{
		if(buffer.insideCompoundEdit())
		{
			buffer.endCompoundEdit();
			buffer.writeUnlock();
			return true;
		}
		return false;
	}//}}}

	//{{{ usesRangeComments() method
	/**
	 * Determines if the given buffer has defined range comment settings.
	 *
	 * @param buffer  a jEdit buffer.
	 * @return        <code>true</code> if <code>buffer</code> uses range comments, <code>
	 *      false</code> otherwise.
	 */
	private static boolean usesRangeComments(JEditBuffer buffer)
	{
		return buffer.getStringProperty("commentStart") != null &&
			buffer.getStringProperty("commentEnd") != null;
	} //}}}
}

