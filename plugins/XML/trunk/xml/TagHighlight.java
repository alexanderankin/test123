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
		if(match != null && virtualLine < textArea.getVirtualLineCount())
		{
			int physicalLine = textArea.virtualToPhysical(virtualLine);

			FontMetrics fm = textArea.getPainter().getFontMetrics();
			int height = fm.getHeight();

			int matchStartLine = buffer.getLineOfOffset(match.start);
			int matchEndLine = buffer.getLineOfOffset(match.end);

			if(physicalLine < matchStartLine || physicalLine > matchEndLine)
				return;

			int x1, x2;

			if(matchStartLine == physicalLine)
				x1 = match.start - buffer.getLineStartOffset(matchStartLine);
			else
				x1 = 0;

			if(matchEndLine == physicalLine)
				x2 = match.end - buffer.getLineStartOffset(matchEndLine);
			else
				x2 = buffer.getLineLength(physicalLine);

			x1 = textArea.offsetToX(physicalLine,x1);
			x2 = textArea.offsetToX(physicalLine,x2);

			gfx.setColor(tagHighlightColor);
			gfx.drawRect(x1,y + fm.getDescent() + fm.getLeading(),
				x2 - x1,height - 1);
		}
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
	private MatchTag.TagAttribute current;
	private MatchTag.TagAttribute match;

	private View view;
	private JEditTextArea textArea;
	private Buffer buffer;

	private boolean bufferChanged;
	//}}}

	//{{{ updateHighlightWithDelay() method
	private void updateHighlightWithDelay()
	{
		if(!tagHighlightEnabled || !buffer.isLoaded()
			|| buffer.getProperty("xml.parser") == null)
		{
			return;
		}

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

		if(timer.isRunning())
			timer.stop();

		timer.setInitialDelay(250);
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
			System.err.println("updating match");
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
			updateHighlightWithDelay();
		}

		public void contentRemoved(Buffer buffer, int startLine,
			int offset, int numLines, int length)
		{
			bufferChanged = true;
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
