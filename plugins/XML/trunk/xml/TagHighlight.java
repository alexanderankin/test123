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
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;
//}}}

public class TagHighlight extends TextAreaExtension
{
	//{{{ TagHighlight constructor
	public TagHighlight(View view, JEditTextArea textArea)
	{
		this.view = view;
		this.textArea = textArea;
		textArea.addCaretListener(new CaretHandler());

		bufferHandler = new BufferHandler();
		buffer = textArea.getBuffer();
		buffer.addBufferChangeListener(bufferHandler);

		timer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateHighlight();
			}
		});

		returnValue = new Point();
	} //}}}

	//{{{ paintValidLine() method
	public void paintValidLine(Graphics2D gfx, int screenLine,
		int physicalLine, int start, int end, int y)
	{
		if(match != null)
			paintHighlight(gfx,screenLine,physicalLine,start,end,y,match);
	} //}}}

	//{{{ bufferChanged() method
	public void bufferChanged(Buffer buffer)
	{
		this.buffer.removeBufferChangeListener(bufferHandler);
		this.buffer = buffer;
		buffer.addBufferChangeListener(bufferHandler);
		current = match = null;
		updateHighlightWithDelay();
	} //}}}

	//{{{ propertiesChanged() method
	public static void propertiesChanged()
	{
		tagHighlightColor = jEdit.getColorProperty(
			"xml.tag-highlight-color");
		tagHighlightEnabled = jEdit.getBooleanProperty(
			"xml.tag-highlight");
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private static Color tagHighlightColor;
	private static boolean tagHighlightEnabled;

	private Timer timer;

	private BufferHandler bufferHandler;
	private MatchTag.Tag current;
	private MatchTag.Tag match;
	private boolean bufferChanged;

	private View view;
	private JEditTextArea textArea;
	private Buffer buffer;

	private Point returnValue;
	//}}}

	//{{{ paintHighlight() method
	private void paintHighlight(Graphics gfx, int screenLine, int physicalLine,
		int start, int end, int y, MatchTag.Tag tag)
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
		if(!tagHighlightEnabled || !buffer.isLoaded()
			|| buffer.getProperty("xml.parser") == null)
		{
			return;
		}

		if(timer.isRunning())
			timer.stop();

		timer.setInitialDelay(100);
		timer.setRepeats(false);
		timer.start();
	} //}}}

	//{{{ updateHighlight() method
	private void updateHighlight()
	{
		int caret = textArea.getCaretPosition();

		if(bufferChanged || current == null
			|| caret < current.start
			|| caret > current.end)
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

			String text = textArea.getText();

			current = MatchTag.getSelectedTag(caret,text);

			if(current == null)
			{
				match = null;
			}
			else
			{
				match = MatchTag.getMatchingTag(text,current);

				if(match != null)
				{
					int line = textArea.physicalToVirtual(
						buffer.getLineOfOffset(match.start));
					if(line < textArea.getFirstLine()
						|| line >= textArea.getFirstLine()
						+ textArea.getVisibleLines() - 1)
					{
						view.getStatus().setMessageAndClear(
							jEdit.getProperty("view.status.bracket",
							new String[] { text.substring(
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
