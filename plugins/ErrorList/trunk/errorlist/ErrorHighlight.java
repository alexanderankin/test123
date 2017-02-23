
/*
 * ErrorHighlight.java - Highlights error locations in text area
 * :tabSize=4:indentSize=4:noTabs=false:
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


import java.awt.*;

// {{{ Imports
import javax.swing.text.Segment;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.*;
// }}}


public class ErrorHighlight extends TextAreaExtension
{

	// {{{ ErrorHighlight constructor
	public ErrorHighlight( EditPane editPane )
	{
		this.editPane = editPane;
		seg = new Segment();
		point = new Point();
	}	//}}}

	// {{{ paintScreenLineRange() method
	@Override
	public void paintValidLine( Graphics2D gfx, int screenLine,
	int physicalLine, int start, int end, int y )
	{
		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if ( errorSources == null )
		{
			return;
		}

		FontMetrics fm = editPane.getTextArea().getPainter().getFontMetrics();

		for ( int i = 0; i < errorSources.length; i++ )
		{
			ErrorSource.Error[] errors = errorSources[i].getLineErrors( editPane.getBuffer().getSymlinkPath(), physicalLine, physicalLine );
			if ( errors == null )
			{
				continue;
			}

			for ( int j = 0; j < errors.length; j++ )
			{
				paintError( errors[j], gfx, physicalLine,
				start, end, y + fm.getAscent() );
			}
		}
	}	//}}}

	// {{{ getToolTipText() method
	@Override
	public String getToolTipText( int x, int y )
	{
		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if ( !editPane.getBuffer().isLoaded() )
		{
			return null;
		}

		JEditTextArea textArea = editPane.getTextArea();

		int offset = textArea.xyToOffset( x, y );
		if ( offset == -1 )
		{
			return null;
		}

		int line = textArea.getLineOfOffset( offset );

		for ( int i = 0; i < errorSources.length; i++ )
		{
			ErrorSource.Error[] lineErrors = errorSources[i].getLineErrors( editPane.getBuffer().getSymlinkPath(), line, line );

			if ( lineErrors == null )
			{
				continue;
			}

			int lineStart = textArea.getLineStartOffset( line );

			for ( int j = 0; j < lineErrors.length; j++ )
			{
				ErrorSource.Error error = lineErrors[j];
				int start = error.getStartOffset();
				int end = error.getEndOffset();

				if ( ( offset >= start + lineStart && offset <= end + lineStart ) || ( start == 0 && end == 0 ) )
				{
					return error.getErrorMessage();
				}
			}
		}

		return null;
	}	//}}}

	// {{{ Private members
	private EditPane editPane;
	private Segment seg;
	private Point point;

	// {{{ paintError() method
	private void paintError( ErrorSource.Error error, Graphics2D gfx, int line, int _start, int _end, int y )
	{
		JEditTextArea textArea = editPane.getTextArea();

		int lineStart = textArea.getLineStartOffset( line );

		int start = error.getStartOffset();
		int end = error.getEndOffset();

		if ( start == 0 && end == 0 )
		{
			textArea.getLineText( line, seg );
			for ( int j = 0; j < seg.count; j++ )
			{
				if ( Character.isWhitespace( seg.array[seg.offset + j] ) )
				{
					start++;
				}
				else
				{
					break;
				}
			}

			end = seg.count;
		}

		if ( start + lineStart >= _end || end + lineStart <= _start )
		{
			return;
		}

		int startX;

		if ( start + lineStart >= _start )
		{
			startX = textArea.offsetToXY( line, start, point ).x;
		}
		else
		{
			startX = 0;
		}

		int endX;

		if ( end + lineStart >= _end )
		{
			endX = textArea.offsetToXY( line, _end - lineStart - 1, point ).x;
		}
		else
		{
			endX = textArea.offsetToXY( line, end, point ).x;
		}

		gfx.setColor( ErrorListPlugin.getErrorColor( error.getErrorType() ) );
		if ( "squiggle".equals( jEdit.getProperty( "error-list.underlineStyle" ) ) )
		{
			paintSquiggle( gfx, startX, endX, y + 2 );
		}
		else
		{
			paintLine( gfx, startX, endX, y + 1 );
		}
	}	//}}}

	// {{{ paintLine() method
	private static void paintLine( Graphics2D gfx, int x1, int x2, int y )
	{
		gfx.drawLine( x1, y, x2, y );
	}	//}}}

	// {{{ paintSquiggle() method
	protected static void paintSquiggle( Graphics gfx, int x1, int x2, int y )
	{
		int x = x1;
		int delta = -2;
		while ( x < x2 ) {
			gfx.drawLine( x, y, x + 2, y + delta );
			y += delta;
			delta = -delta;
			x += 2;
		}
	}	//}}}
}
