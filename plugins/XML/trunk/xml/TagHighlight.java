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
import java.awt.*;
import java.awt.event.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;
//}}}

public class TagHighlight implements TextAreaHighlight
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
	} //}}}

	//{{{ paintHighlight() method
	public void paintHighlight(Graphics gfx, int virtualLine, int y)
	{
		if(virtualLine >= textArea.getVirtualLineCount())
			return;

		int physicalLine = textArea.virtualToPhysical(virtualLine);

		/*if(current != null)
			paintHighlight(gfx,physicalLine,y,current);*/

		if(match != null)
			paintHighlight(gfx,physicalLine,y,match);
	} //}}}

	//{{{ getToolTipText() method
	public String getToolTipText(MouseEvent evt)
	{
		return null;
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
	//}}}

	//{{{ paintHighlight() method
	private void paintHighlight(Graphics gfx, int physicalLine, int y,
		MatchTag.Tag tag)
	{
		int tagStartLine = buffer.getLineOfOffset(tag.start);
		int tagEndLine = buffer.getLineOfOffset(tag.end);

		if(physicalLine < tagStartLine || physicalLine > tagEndLine)
			return;

		FontMetrics fm = textArea.getPainter().getFontMetrics();
		int height = fm.getHeight();
		int top = y + fm.getDescent() + fm.getLeading();

		int x1, x2;

		if(tagStartLine == physicalLine)
			x1 = tag.start - buffer.getLineStartOffset(tagStartLine);
		else
			x1 = 0;

		if(tagEndLine == physicalLine)
			x2 = tag.end - buffer.getLineStartOffset(tagEndLine);
		else
			x2 = buffer.getLineLength(physicalLine);

		x1 = textArea.offsetToX(physicalLine,x1);
		x2 = textArea.offsetToX(physicalLine,x2);

		gfx.setColor(tagHighlightColor);

		gfx.drawLine(x1,top,x1,top + height - 1);
		gfx.drawLine(x2,top,x2,top + height - 1);

		if(tagStartLine == physicalLine)
			gfx.drawLine(x1,top,x2,top);
		else
		{
			int prevX1, prevX2;

			if(tagStartLine == physicalLine - 1)
				prevX1 = tag.start - buffer.getLineStartOffset(tagStartLine);
			else
				prevX1 = 0;

			prevX2 = buffer.getLineLength(physicalLine - 1);

			prevX1 = textArea.offsetToX(physicalLine - 1,prevX1);
			prevX2 = textArea.offsetToX(physicalLine - 1,prevX2);

			gfx.drawLine(Math.min(x1,prevX1),top,
				Math.max(x1,prevX1),top);
			gfx.drawLine(Math.min(x2,prevX2),top,
				Math.max(x2,prevX2),top);
		}

		if(tagEndLine == physicalLine)
			gfx.drawLine(x1,top + height - 1,x2,top + height - 1);
	}

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
