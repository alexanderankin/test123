/*
 * ErrorOverview.java - Error overview component
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;

public class ErrorOverview extends JPanel
{
	//{{{ ErrorOverview constructor
	public ErrorOverview(final JEditTextArea textArea)
	{
		super(new BorderLayout());
		this.textArea = textArea;

		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				int line = yToLine(evt.getY());
				if(line >= 0 && line < textArea.getLineCount())
				{
					textArea.setCaretPosition(
						textArea.getLineStartOffset(
						line)
					);
				}
			}
		});
	} //}}}

	//{{{ invalidateLine() method
	public void invalidateLine(int line)
	{
		repaint(0,lineToY(line),getWidth(),HILITE_HEIGHT);
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		ToolTipManager.sharedInstance().registerComponent(this);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		ToolTipManager.sharedInstance().unregisterComponent(this);
	} //}}}

	//{{{ getToolTipText() method
	public String getToolTipText(MouseEvent evt)
	{
		Buffer buffer = textArea.getBuffer();
		int lineCount = buffer.getLineCount();
		int line = yToLine(evt.getY());
		if(line >= 0 && line < textArea.getLineCount())
		{
			ErrorSource[] errorSources = ErrorSource.getErrorSources();
			for(int i = 0; i < errorSources.length; i++)
			{
				ErrorSource.Error[] errors = errorSources[i]
					.getLineErrors(buffer.getPath(),
					line,line);
				// if there is no exact match, try next and
				// prev lines
				if(errors == null && line != 0)
					errors = errorSources[i]
					.getLineErrors(buffer.getPath(),
					line - 1,line - 1);
				if(errors == null && line != lineCount - 1)
					errors = errorSources[i]
					.getLineErrors(buffer.getPath(),
					line + 1,line + 1);
				if(errors != null)
					return errors[0].getErrorMessage();
			}
		}

		return null;
	} //}}}

	//{{{ paintComponent() method
	public void paintComponent(Graphics gfx)
	{
		super.paintComponent(gfx);

		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if(errorSources == null)
			return;

		Buffer buffer = textArea.getBuffer();

		Rectangle clip = gfx.getClipBounds();

		int lineCount = buffer.getLineCount();
		int line1 = yToLine(clip.y);
		int line2 = yToLine(clip.y + clip.height);

		if(line1 < 0)
			line1 = 0;
		if(line2 >= lineCount)
			line2 = lineCount - 1;

		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource.Error[] errors = errorSources[i].getLineErrors(
				buffer.getPath(),line1,line2);
			if(errors == null)
				continue;

			for(int j = 0; j < errors.length; j++)
			{
				ErrorSource.Error error = errors[j];
				int line = error.getLineNumber();
				if(line < line1 || line > line2)
					System.err.println("WTF: " + line);
				int y = lineToY(line);

				gfx.setColor(ErrorListPlugin.getErrorColor(
					errors[0].getErrorType()));
				gfx.fillRect(0,y,getWidth(),HILITE_HEIGHT);
			}
		}
	} //}}}

	//{{{ getPreferredSize() method
	public Dimension getPreferredSize()
	{
		return new Dimension(10,0);
	} //}}}

	//{{{ Private members
	private static final int WIDTH = 10;
	private static final int HILITE_HEIGHT = 2;
	private JEditTextArea textArea;

	//{{{ lineToY() method
	private int lineToY(int line)
	{
		return (line * getHeight()) / textArea.getLineCount();
	} //}}}

	//{{{ yToLine() method
	private int yToLine(int y)
	{
		return (y * textArea.getLineCount()) / getHeight();
	} //}}}

	//}}}
}
