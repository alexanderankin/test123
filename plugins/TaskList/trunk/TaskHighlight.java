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

import java.awt.event.*;
import java.awt.*;

import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;

import org.gjt.sp.util.Log;

import java.util.Hashtable;

public class TaskHighlight implements TextAreaHighlight
{

	public void init(JEditTextArea textArea, TextAreaHighlight next)
	{
		this.textArea = textArea;
		this.next = next;
	}

	public void paintHighlight(Graphics gfx, int line, int y)
	{
		int lineCount = textArea.getVirtualLineCount();
		if(line >= lineCount)
			return;

		Hashtable taskMap = TaskListPlugin.requestTasksForBuffer(
			textArea.getBuffer());

		int physicalLine = textArea.getBuffer().virtualToPhysical(line);

		if(taskMap != null)
		{
			Integer _line = new Integer(physicalLine);

			Task task = (Task)taskMap.get(_line);
			if(task != null)
			{
				underlineTask(task, gfx, physicalLine, y);
			}
		}

		if(next != null)
			next.paintHighlight(gfx, line, y);
	}

	public String getToolTipText(MouseEvent evt)
	{
		if(this.next == null)
			return null;

		return this.next.getToolTipText(evt);
	}

	private JEditTextArea textArea;
	private TextAreaHighlight next;

	private void underlineTask(Task task,
		Graphics gfx, int line, int y)
	{

		int start = task.getStartOffset();
		int end = task.getEndOffset();

		//Log.log(Log.DEBUG, TaskHighlight.class,
		//	"line=" + line + ",y=" + y + ",start=" +
		//	start + ",end=" + end + ",task=" + task);//##

		start = textArea.offsetToX(line, start);
		end = textArea.offsetToX(line, end);

		gfx.setColor(TaskListPlugin.getHighlightColor());
		paintWavyLine(gfx, y, start, end);
	}

	private void paintWavyLine(Graphics gfx, int y, int start, int end)
	{
		y += textArea.getPainter().getFontMetrics().getHeight();

		for(int i = start; i < end; i+= 6)
		{
			gfx.drawLine(i,y + 3,i + 3,y + 1);
			gfx.drawLine(i + 3,y + 1,i + 6,y + 3);
		}
	}

}
