/*
 * ErrorHighlight.java - Highlights error locations in text area
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2003 Slava Pestov
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
import java.awt.*;
import org.gjt.sp.jedit.textarea.*;
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

	//{{{ paintScreenLineRange() method
	public void paintScreenLineRange(Graphics2D gfx, int firstLine,
		int lastLine, int[] physicalLines, int[] start, int[] end,
		int y, int lineHeight)
	{
		FontMetrics fm = textArea.getPainter().getFontMetrics();

		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if(errorSources == null)
			return;

		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource.Error[] errors = errorSources[i]
				.getLineErrors(textArea.getBuffer(),
				textArea.getPhysicalLineOfScreenLine(firstLine),
				textArea.getPhysicalLineOfScreenLine(lastLine));
			if(errors == null)
				continue;

			int errorListIndex = 0;

			for(int j = 0; j < physicalLines.length; j++)
			{
				//System.err.println("screen line: " + (j
				//	+ firstLine));
				// for each screen line, find range of errors
				// in errors array
				for(int k = errorListIndex;
					k < errors.length; k++)
				{
					int physicalLine = physicalLines[j];
					if(
						(textArea.getDisplayManager()
						.isLineVisible(physicalLine))
						&&
						(errors[k].getLineNumber()
						== physicalLine)
					)
					{
						System.err.println("up: " + j + "," + k);
						paintError(errors[k],gfx,
							physicalLine,
							start[j],
							end[j],
							y + j * lineHeight
							+ fm.getAscent()
						);
					}
					else
					{
						//System.err.println("down: " + j + "," + k);
						if(j + 1 != physicalLines.length
							&& physicalLines[j + 1]
							!= physicalLine)
						{
							errorListIndex = k;
						}
						break;
					}
				}
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
				textArea.getBuffer(),line,line);

			if(lineErrors == null)
				continue;

			int lineStart = textArea.getLineStartOffset(line);

			for(int j = 0; j < lineErrors.length; j++)
			{
				ErrorSource.Error error = lineErrors[j];
				int start = error.getStartOffset();
				int end = error.getEndOffset();

				if((offset >= start + lineStart
					&& offset <= end + lineStart)
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

	//{{{ paintError() method
	private void paintError(ErrorSource.Error error,
		Graphics2D gfx, int line, int _start,
		int _end, int y)
	{
		int lineStart = textArea.getLineStartOffset(line);

		int start = error.getStartOffset();
		int end = error.getEndOffset();

		if(start == 0 && end == 0)
		{
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

		if(start + lineStart >= _end || end + lineStart <= _start)
			return;

		int startX;

		if(start + lineStart >= _start)
			startX = textArea.offsetToXY(line,start,point).x;
		else
			startX = 0;

		int endX;

		if(end + lineStart >= _end)
			endX = textArea.offsetToXY(line,_end - lineStart - 1,point).x;
		else
			endX = textArea.offsetToXY(line,end,point).x;

		gfx.setColor(ErrorListPlugin.getErrorColor(error.getErrorType()));
		gfx.drawLine(startX,y + 1,endX,y + 1);
	} //}}}

	//}}}
}
