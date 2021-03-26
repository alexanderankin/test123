/*
 * ErrorOverview.java - Error overview component
 * :tabSize=4:indentSize=4:noTabs=false:
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
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;

public class ErrorOverview extends JPanel
{
	//{{{ ErrorOverview constructor
	public ErrorOverview(final EditPane editPane)
	{
		super(new BorderLayout());
		this.editPane = editPane;
		setRequestFocusEnabled(false);

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent evt)
			{
				int line = yToLine(evt.getY());
				JEditTextArea textArea = editPane.getTextArea();
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
	@Override
	public void addNotify()
	{
		super.addNotify();
		ToolTipManager.sharedInstance().registerComponent(this);
	} //}}}

	//{{{ removeNotify() method
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		ToolTipManager.sharedInstance().unregisterComponent(this);
	} //}}}

	//{{{ getToolTipText() method
	@Override
	public String getToolTipText(MouseEvent evt)
	{
		Buffer buffer = editPane.getBuffer();
		int lineCount = buffer.getLineCount();
		int line = yToLine(evt.getY());
		JEditTextArea textArea = editPane.getTextArea();
		if(line >= 0 && line < textArea.getLineCount())
		{
			ErrorSource[] errorSources = ErrorSource.getErrorSources();
			for (ErrorSource errorSource : errorSources)
			{
				ErrorSource.Error[] errors = errorSource
					.getLineErrors(buffer.getSymlinkPath(),
						line, line);
				// if there is no exact match, try next and
				// prev lines
				if (errors == null && line != 0)
					errors = errorSource
						.getLineErrors(buffer.getPath(),
							line - 1, line - 1);
				if (errors == null && line != lineCount - 1)
					errors = errorSource
						.getLineErrors(buffer.getPath(),
							line + 1, line + 1);
				if (errors != null)
					return errors[0].getErrorMessage();
			}
		}

		return null;
	} //}}}

	//{{{ paintComponent() method
	@Override
	public void paintComponent(Graphics gfx)
	{
		super.paintComponent(gfx);

		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if(errorSources == null)
			return;

		Buffer buffer = editPane.getBuffer();

		Rectangle clip = gfx.getClipBounds();

		int lineCount = buffer.getLineCount();
		int line1 = yToLine(clip.y);
		int line2 = yToLine(clip.y + clip.height);

		if(line1 < 0)
			line1 = 0;
		if(line2 >= lineCount)
			line2 = lineCount - 1;

		for (ErrorSource errorSource : errorSources)
		{
			ErrorSource.Error[] errors = errorSource.getLineErrors(
				buffer.getSymlinkPath(), line1, line2);
			if (errors == null)
				continue;

			for (ErrorSource.Error error : errors)
			{
				int line = error.getLineNumber();
				if (line < line1 || line > line2)
					System.err.println("WTF: " + line);
				int y = lineToY(line);

				gfx.setColor(ErrorListPlugin.getErrorColor(
					error.getErrorType()));
				gfx.fillRect(0, y, getWidth(), HILITE_HEIGHT);
			}
		}
	} //}}}

	//{{{ getPreferredSize() method
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(10,0);
	} //}}}

	//{{{ Private members
	private static final int WIDTH = 10;	// TODO: this isn't used, remove it
	private static final int HILITE_HEIGHT = 2;
	private final EditPane editPane;

	//{{{ lineToY() method
	private int lineToY(int line)
	{
		return (line * getHeight()) / editPane.getBuffer().getLineCount();
	} //}}}

	//{{{ yToLine() method
	private int yToLine(int y)
	{
		return (y * editPane.getBuffer().getLineCount()) / getHeight();
	} //}}}

	//}}}
}
