/*
 * ErrorHighlight.java - "Wavy red underlines"
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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

package errorlist;

 //{{{ Imports
import javax.swing.text.Segment;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
//}}}

public class ErrorHighlight implements TextAreaHighlight
{
	//{{{ ErrorHighlight constructor
	public ErrorHighlight(JEditTextArea textArea)
	{
		this.textArea = textArea;
		seg = new Segment();
	} //}}}

	//{{{ paintHighlight() method
	public void paintHighlight(Graphics gfx, int line, int y)
	{
		ErrorSource[] errorSources = ErrorSource.getErrorSources();

		int lineCount = textArea.getVirtualLineCount();
		if(line < lineCount && textArea.getBuffer().isLoaded())
		{
			int physicalLine = textArea.virtualToPhysical(line);

			for(int i = 0; i < errorSources.length; i++)
			{
				ErrorSource source = errorSources[i];
				ErrorSource.Error[] lineErrors = source.getLineErrors(
					textArea.getBuffer(),physicalLine);

				if(lineErrors != null)
					paintLineErrors(lineErrors,gfx,physicalLine,y);
			}
		}
	} //}}}

	//{{{ getToolTipText() method
	public String getToolTipText(MouseEvent evt)
	{
		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if(!textArea.getBuffer().isLoaded())
			return null;

		int y = evt.getY();
		int line = textArea.virtualToPhysical(textArea.yToLine(y));
		int offset = -1;

		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource.Error[] lineErrors =
				errorSources[i].getLineErrors(
				textArea.getBuffer(),line);

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

		return null;
	} //}}}

	//{{{ Private members
	private JEditTextArea textArea;
	private Segment seg;

	//{{{ paintLineErrors() method
	private void paintLineErrors(ErrorSource.Error[] lineErrors,
		Graphics gfx, int line, int y)
	{
		for(int i = 0; i < lineErrors.length; i++)
		{
			ErrorSource.Error error = lineErrors[i];

			int start = error.getStartOffset();
			int end = error.getEndOffset();

			if(start == 0)
			{
				textArea.getLineText(line,seg);
				for(int j = 0; j < seg.count; j++)
				{
					if(Character.isWhitespace(seg.array[seg.offset + j]))
						start++;
					else
						break;
				}
			}

			start = textArea.offsetToX(line,start);
			if(end == 0)
				end = textArea.offsetToX(line,textArea.getLineLength(line));
			else
				end = textArea.offsetToX(line,end);

			gfx.setColor(ErrorListPlugin.getErrorColor(error.getErrorType()));
			paintWavyLine(gfx,y,start,end);
		}
	} //}}}

	//{{{ paintWavyLine() method
	private void paintWavyLine(Graphics gfx, int y, int start, int end)
	{
		y += textArea.getPainter().getFontMetrics().getHeight();

		for(int i = start; i < end; i+= 6)
		{
			gfx.drawLine(i,y + 3,i + 3,y + 1);
			gfx.drawLine(i + 3,y + 1,i + 6,y + 3);
		}
	} //}}}

	//}}}
}
