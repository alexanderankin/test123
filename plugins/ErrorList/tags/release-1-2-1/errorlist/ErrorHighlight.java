/*
 * ErrorHighlight.java - "Wavy red underlines"
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2000, 2001, 2002 Slava Pestov
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

public class ErrorHighlight extends TextAreaExtension
{
	//{{{ ErrorHighlight constructor
	public ErrorHighlight(JEditTextArea textArea)
	{
		this.textArea = textArea;
		seg = new Segment();
		point = new Point();
	} //}}}

	//{{{ paintValidLine() method
	public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine,
		int start, int end, int y)
	{
		ErrorSource[] errorSources = ErrorSource.getErrorSources();

		FontMetrics fm = textArea.getPainter().getFontMetrics();
		y += (fm.getHeight() - fm.getDescent() - fm.getLeading());

		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource source = errorSources[i];
			ErrorSource.Error[] lineErrors = source.getLineErrors(
				textArea.getBuffer(),physicalLine);

			if(lineErrors != null)
			{
				paintLineErrors(lineErrors,gfx,physicalLine,
					start,end,y);
			}
		}
	} //}}}

	//{{{ getToolTipText() method
	public String getToolTipText(int x, int y)
	{
		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if(!textArea.getBuffer().isLoaded())
			return null;

		int offset = textArea.xyToOffset(x,y);
		if(offset == -1)
			return null;

		int line = textArea.getLineOfOffset(offset);

		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource.Error[] lineErrors =
				errorSources[i].getLineErrors(
				textArea.getBuffer(),line);

			if(lineErrors == null)
				continue;

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
	private Point point;

	//{{{ paintLineErrors() method
	private void paintLineErrors(ErrorSource.Error[] lineErrors,
		Graphics2D gfx, int line, int _start, int _end, int y)
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

			if(start + textArea.getLineStartOffset(line) >= _start)
				start = textArea.offsetToXY(line,start,point).x;
			else
				start = 0;

			if(end == 0 || end + textArea.getLineStartOffset(line) >= _end)
				end = textArea.offsetToXY(line,textArea.getLineLength(line),point).x;
			else
				end = textArea.offsetToXY(line,end,point).x;

			gfx.setColor(ErrorListPlugin.getErrorColor(error.getErrorType()));
			paintWavyLine(gfx,y,start,end);
		}
	} //}}}

	//{{{ paintWavyLine() method
	private void paintWavyLine(Graphics2D gfx, int y, int start, int end)
	{
		for(int i = start; i < end; i+= 6)
		{
			gfx.drawLine(i,y + 3,i + 3,y + 1);
			gfx.drawLine(i + 3,y + 1,i + 6,y + 3);
		}
	} //}}}

	//}}}
}
