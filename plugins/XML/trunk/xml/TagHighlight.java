/*
 * TagHighlight.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (c) 2000 Andre Kaplan
 * Portions copyright (c) 2002 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import javax.swing.event.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import sidekick.SideKickPlugin;
import xml.parser.*;
//}}}

public class TagHighlight extends TextAreaExtension
{
	//{{{ TagHighlight constructor
	public TagHighlight(View view)
	{
		this.view = view;
		this.textArea = view.getTextArea();
		this.buffer = view.getBuffer();

		bufferHandler = new BufferHandler();
		caretHandler = new CaretHandler();

		timer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateHighlight();
			}
		});

		textArea.getPainter().addExtension(this);

		buffer.addBufferChangeListener(bufferHandler);
		textArea.addCaretListener(caretHandler);
		updateHighlightWithDelay();

		returnValue = new Point();
	} //}}}

	//{{{ dispose() method
	public void dispose()
	{
		timer.stop();
		buffer.removeBufferChangeListener(bufferHandler);
		textArea.getPainter().removeExtension(this);
		textArea.removeCaretListener(caretHandler);
	} //}}}

	//{{{ paintValidLine() method
	public void paintValidLine(Graphics2D gfx, int screenLine,
		int physicalLine, int start, int end, int y)
	{
		if(match != null)
			paintHighlight(gfx,screenLine,physicalLine,start,end,y,match);
	} //}}}

	//{{{ propertiesChanged() method
	public static void propertiesChanged()
	{
		tagHighlightColor = jEdit.getColorProperty(
			"xml.tag-highlight-color");
	} //}}}

	//{{{ Private members
	private static Color tagHighlightColor;

	//{{{ Instance variables
	private Timer timer;

	private BufferHandler bufferHandler;
	private CaretHandler caretHandler;
	private TagParser.Tag current;
	private TagParser.Tag match;
	private boolean bufferChanged;

	private View view;
	private JEditTextArea textArea;
	private Buffer buffer;

	private Point returnValue;
	//}}}

	//{{{ paintHighlight() method
	private void paintHighlight(Graphics gfx, int screenLine, int physicalLine,
		int start, int end, int y, TagParser.Tag tag)
	{
		if(tag.start >= end || tag.end < start)
			return;

		int tagStartLine = textArea.getScreenLineOfOffset(tag.start);
		int tagEndLine = textArea.getScreenLineOfOffset(tag.end);

		FontMetrics fm = textArea.getPainter().getFontMetrics();
		int height = fm.getHeight();

		int x1, x2;

		if(tagStartLine == screenLine)
		{
			x1 = tag.start - buffer.getLineStartOffset(
				buffer.getLineOfOffset(tag.start));
		}
		else
			x1 = 0;

		if(tagEndLine == screenLine)
		{
			x2 = tag.end - buffer.getLineStartOffset(
				buffer.getLineOfOffset(tag.end));
		}
		else
		{
			x2 = textArea.getScreenLineEndOffset(screenLine)
				- textArea.getScreenLineStartOffset(screenLine);
		}

		x1 = textArea.offsetToXY(physicalLine,x1,returnValue).x;
		x2 = textArea.offsetToXY(physicalLine,x2,returnValue).x;

		gfx.setColor(tagHighlightColor);

		gfx.drawLine(x1,y,x1,y + height - 1);
		gfx.drawLine(x2,y,x2,y + height - 1);

		if(tagStartLine == screenLine || screenLine == 0)
			gfx.drawLine(x1,y,x2,y);
		else
		{
			int prevX1, prevX2;

			if(tagStartLine == screenLine - 1)
			{
				prevX1 = tag.start - buffer.getLineStartOffset(
					buffer.getLineOfOffset(tag.start));
			}
			else
				prevX1 = 0;

			prevX2 = textArea.getScreenLineEndOffset(screenLine - 1)
				- textArea.getScreenLineStartOffset(screenLine - 1);

			prevX1 = textArea.offsetToXY(physicalLine - 1,prevX1,returnValue).x;
			prevX2 = textArea.offsetToXY(physicalLine - 1,prevX2,returnValue).x;

			gfx.drawLine(Math.min(x1,prevX1),y,
				Math.max(x1,prevX1),y);
			gfx.drawLine(Math.min(x2,prevX2),y,
				Math.max(x2,prevX2),y);
		}

		if(tagEndLine == screenLine)
			gfx.drawLine(x1,y + height - 1,x2,y + height - 1);
	} //}}}

	//{{{ updateHighlightWithDelay() method
	private void updateHighlightWithDelay()
	{
		if(timer.isRunning())
			timer.stop();

		if(!buffer.isLoaded())
			return;

		timer.setInitialDelay(300);
		timer.setRepeats(false);
		timer.start();
	} //}}}

	//{{{ updateHighlight() method
	private void updateHighlight()
	{
		if(buffer != view.getBuffer())
		{
			// fix the race condition
			return;
		}

		int caret = textArea.getCaretPosition();

		if(bufferChanged || current == null
			|| caret <= current.start
			|| caret >= current.end)
		{

			if(match != null)
			{
				if(match.start < buffer.getLength()
					&& match.end <= buffer.getLength())
				{
					textArea.invalidateLineRange(
						textArea.getLineOfOffset(match.start),
						textArea.getLineOfOffset(match.end)
					);
				}

				match = null;
			}

			if(XmlPlugin.isDelegated(textArea))
				return;

			String text = textArea.getText();

			current = TagParser.getTagAtOffset(text,caret);

			if(current == null)
			{
				match = null;
			}
			else
			{
				match = TagParser.getMatchingTag(text,current);

				if(match != null)
				{
					if(textArea.getScreenLineOfOffset(match.start) == -1)
					{
						int line = buffer.getLineOfOffset(match.start);

						view.getStatus().setMessageAndClear(
							jEdit.getProperty("view.status.bracket",
							new Object[] {
							new Integer(line),
							text.substring(
							match.start,match.end) }));
					}

					textArea.invalidateLineRange(
						buffer.getLineOfOffset(match.start),
						buffer.getLineOfOffset(match.end)
					);
				}
			}
		}

		bufferChanged = false;
	} //}}}

	//}}}

	//{{{ BufferHandler class
	class BufferHandler extends BufferChangeAdapter
	{
		public void contentInserted(Buffer buffer, int startLine,
			int offset, int numLines, int length)
		{
			bufferChanged = true;
			if(match != null)
			{
				if(match.start >= offset)
					match.start += length;
				if(match.end >= offset)
					match.end += length;
			}

			updateHighlightWithDelay();
		}

		public void contentRemoved(Buffer buffer, int startLine,
			int offset, int numLines, int length)
		{
			bufferChanged = true;
			if(match != null)
			{
				if(match.start >= offset)
				{
					if(match.start < offset + length)
						match.start = offset;
					else
						match.start -= length;
				}
				if(match.end >= offset)
				{
					if(match.end < offset + length)
						match.end = offset;
					else
						match.end -= length;
				}
			}

			updateHighlightWithDelay();
		}
	} //}}}

	//{{{ CaretHandler class
	class CaretHandler implements CaretListener
	{
		public void caretUpdate(CaretEvent evt)
		{
			// hack
			if(textArea.getBuffer() != buffer)
				return;

			updateHighlightWithDelay();
		}
	} //}}}
}
