/*
 * TaskHighlight.java - TaskList plugin
 * Copyright (C) 2001 Oliver Rutherfurd
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
 *
 * $Id$
 */

package tasklist;

//{{{ imports
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;
//}}}

/**
 * A class extending jEdit's TextAreaExtension class
 * that, when enabled, draws a wavy line underscoring a task item
 * appearing in the text of a buffer.
 */
public class TaskHighlight extends TextAreaExtension
{
	//{{{ constructor
	/**
	 * Constructs a TextHighlight object
	 * @param textArea The text area
	 */
	public TaskHighlight(JEditTextArea textArea)
	{
		super();
		this.textArea = textArea;
		this.highlightEnabled = jEdit.getBooleanProperty("tasklist.highlight.tasks");
		this.seg = new Segment();
		this.point = new Point();
	}//}}}

	//{{{ isEnabled() method
	/**
	 * Returns whether highlighting of task items is currently enabled.
	 *
	 * @return the value true or false indicating whther highlighting
	 * is enabled
	 */
	public boolean isEnabled()
	{
		return highlightEnabled;
	}//}}}

	//{{{ setEnabled(boolean enabled) method
	/**
	 * Set whether highlighting of task items is enabled; does not
	 * redraw the text area after a change in state.
	 *
	 * @param the new state for task highlighting
	 */
	public void setEnabled(boolean enabled)
	{
		highlightEnabled = enabled;
	}//}}}

	//{{{ paintValidLine() method
	/**
	 * Called by the text area to paint a highlight on a task line
	 * (when highlighting is enabled)
	 * @param gfx The graphics context
	 * @param screenLine The screen line number
	 * @param physicalLine The physical line number
	 * @param start The offset where the screen line begins, from the start of the buffer
	 * @param end The offset where the screen line ends, from the start of the buffer
	 * @param y The y co-ordinate of the top of the line's bounding box
	 */
	public void paintValidLine(Graphics2D gfx, int screenLine,
								int physicalLine, int start, int end, int y)
	{
		// Log.log(Log.DEBUG,this,"paintValidLine() for line " +
		//	String.valueOf(physicalLine));
		Buffer buffer = textArea.getBuffer();
		if(!highlightEnabled || !buffer.isLoaded() ||
			physicalLine >= buffer.getLineCount())
		{
			return;
		}
		Hashtable taskMap =
			TaskListPlugin.requestTasksForBuffer(buffer);

		if(taskMap != null)
		{
			Task task = null;
			Integer _line = new Integer(physicalLine);
			if(!buffer.isDirty())
			{
				task =  (Task)taskMap.get(_line);
			}
			else
			{
				Enumeration enum = taskMap.elements();
				while(enum.hasMoreElements())
				{
					Task _task = (Task)enum.nextElement();
					if(_task.getLineNumber() == _line.intValue())
					{
						task = _task;
						break;
					}
				}
			}
			if(task != null)
			{
				// Log.log(Log.DEBUG,this,"Found task where physical line = "
				//	+ String.valueOf(physicalLine));
				FontMetrics fm = textArea.getPainter().getFontMetrics();
				y -= (fm.getDescent() + fm.getLeading());
				underlineTask(task, gfx, physicalLine, start, end, y);
			}
		}
	}//}}}

	//{{{ getToolTipText() method
	/**
	 * Returns the tool tip to display at the specified location.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	 public java.lang.String getToolTipText(int x, int y)
	 {
		 return super.getToolTipText(x, y);
	}//}}}

	//{{{ private members
	/**
	 * The textArea on which the highlight will be drawn.
	 */
	private JEditTextArea textArea;

	/**
	 * A flag indicating whether highlighting of task items
	 * is currently enabled.
	 */
	private boolean highlightEnabled;

	/**
	 *
	 * A portion of text to be highlighted.
	 */
	private Segment seg;

	/**
	 * A point for anchor the highlighting of taks text
	 */
	private Point point;
	//}}}

	//{{{ underlineTask() method
	/**
	 * Implements underlining of task items through a call to
	 * paintWavyLine()
	 *
	 * @param task the Task that is the subject of highlighting
	 * @param gfx The graphics context
	 * @param line The physical line number
	 * @param _start The offset where the line begins
	 * @param _end The offset where the line ends
	 * @param y The y co-ordinate of the line
	 */
	private void underlineTask(Task task,
		Graphics2D gfx, int line, int _start, int _end, int y)
	{
		// Log.log(Log.DEBUG,this,"Calling underlineTask() for line "
		//	+ String.valueOf(line) + "....");
		int start = task.getStartOffset();
		int end = task.getEndOffset();

		if(start == 0 && end == 0)
		{
			// Log.log(Log.DEBUG,this,"Reseting start and end in underlineTask()....");
			textArea.getLineText(line,seg);
			for(int j = 0; j < seg.count; j++)
			{
				if(Character.isWhitespace(seg.array[seg.offset + j]))
					start++;
				else
					break;
			}
			end = seg.count;
		}

		//if(start >= _end || end <= _start)
		//		return;

		if(start + textArea.getLineStartOffset(line) >= _start)
			start = textArea.offsetToXY(line,start,point).x;
		else
			start = 0;
		if(end + textArea.getLineStartOffset(line) >= _end)
			end = textArea.offsetToXY(line,_end - 1,point).x;
		else
			end = textArea.offsetToXY(line,end,point).x;

		gfx.setColor(TaskListPlugin.getHighlightColor());
		paintWavyLine(gfx,y,start,end);
	}//}}}

	//{{{ paintWavyLine() method
	/**
	 * Draws a wavy line at the indicated coordinates
	 *
	 * @param gfx The graphics context
	 * @param y The y-coordinate representing the lower bound of the wavy line
	 * @param start The x-coordinate of the start of the line
	 * @param end The x-coordinate of the end of the line
	 */
	private void paintWavyLine(Graphics2D gfx, int y, int start, int end)
	{
		// Log.log(Log.DEBUG,this,"Calling paintWavyLine()....");
		y += textArea.getPainter().getFontMetrics().getHeight();

		for(int i = start; i < end; i+= 6)
		{
			gfx.drawLine(i,y + 3,i + 3,y + 1 );
			gfx.drawLine(i + 3,y + 1,i + 6,y + 3);
		}
	}//}}}

}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
