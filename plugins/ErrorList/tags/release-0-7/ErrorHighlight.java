/*
 * ErrorHighlight.java - "Wavy red underlines"
 * Copyright (C) 1999, 2000 Slava Pestov
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

import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;

public class ErrorHighlight implements TextAreaHighlight
{
	public static final Color WARNING_COLOR = new Color(0xffa800);

	public void init(JEditTextArea textArea, TextAreaHighlight next)
	{
		this.textArea = textArea;
		this.next = next;
	}

	public void paintHighlight(Graphics gfx, int line, int y)
	{
		int lineCount = textArea.getLineCount();
		if(line >= lineCount)
			return;

		Object[] errorSources = EditBus.getNamedList(
			ErrorSource.ERROR_SOURCES_LIST);

		if(errorSources == null)
			return;

		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource source = (ErrorSource)errorSources[i];
			ErrorSource.Error[] lineErrors = source.getLineErrors(
				(Buffer)textArea.getDocument(),line);

			if(lineErrors != null)
				paintLineErrors(lineErrors,gfx,line,y);
		}

		if(next != null)
			next.paintHighlight(gfx,line,y);
	}

	public String getToolTipText(MouseEvent evt)
	{
		Object[] errorSources = EditBus.getNamedList(
			ErrorSource.ERROR_SOURCES_LIST);
		if(errorSources == null)
			return (next != null ? next.getToolTipText(evt) : null);

		int y = evt.getY();
		int line = textArea.yToLine(y);
		int offset = -1;

		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource.Error[] lineErrors =
				((ErrorSource)errorSources[i]).getLineErrors(
				(Buffer)textArea.getDocument(),line);

			if(lineErrors == null)
				continue;

			// delay calling xToOffset() which is 'expensive'
			if(offset == -1)
				offset = textArea.xToOffset(line,evt.getX());

			for(int j = 0; j < lineErrors.length; j++)
			{
				ErrorSource.Error error = lineErrors[j];
				int start = error.getStartOffset();
				int end = error.getEndOffset();

				if(offset >= start && offset <= end
					|| (start == 0 && end == 0))
					return error.getErrorMessage();
			}
		}

		return (next != null ? next.getToolTipText(evt) : null);
	}

	// private members
	private JEditTextArea textArea;
	private TextAreaHighlight next;

	private void paintLineErrors(ErrorSource.Error[] lineErrors,
		Graphics gfx, int line, int y)
	{
		for(int i = 0; i < lineErrors.length; i++)
		{
			ErrorSource.Error error = lineErrors[i];

			int start = error.getStartOffset();
			int end = error.getEndOffset();

			start = textArea._offsetToX(line,start);
			if(end == 0)
				end = textArea._offsetToX(line,textArea.getLineLength(line));
			else
				end = textArea._offsetToX(line,end);

			gfx.setColor(error.getErrorType() == ErrorSource.WARNING
				? WARNING_COLOR : Color.red);
			paintWavyLine(gfx,y,start,end);
		}
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
