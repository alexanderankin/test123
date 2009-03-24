/*
* Highlighter.java - The Highlighter is the texteara painter
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2004, 2008 Matthieu Casanova
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package gatchan.highlight;

//{{{ Imports
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.search.SearchMatcher;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.util.SegmentCharSequence;

import javax.swing.text.Segment;
import java.awt.*;
import java.util.regex.PatternSyntaxException;
//}}}

/**
 * The Highlighter is the TextAreaExtension that will look for some String to
 * highlightList in the textarea and draw a rectangle in it's background.
 *
 * @author Matthieu Casanova
 * @version $Id: Highlighter.java,v 1.19 2006/07/24 09:26:52 kpouer Exp $
 */
class Highlighter extends TextAreaExtension implements HighlightChangeListener
{
	private final JEditTextArea textArea;
	private final Segment tempLineContent = new Segment();
	private final Segment lineContent = new Segment();
	private final Point point = new Point();
	private FontMetrics fm;

	private final HighlightManager highlightManager;
	private AlphaComposite blend;
	private float alpha;
	public static boolean square;

	public static Color squareColor;

	//{{{ Highlighter constructor
	Highlighter(JEditTextArea textArea)
	{
		alpha = ((float)jEdit.getIntegerProperty(HighlightOptionPane.PROP_ALPHA, 50)) / 100f;
		blend = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		highlightManager = HighlightManagerTableModel.getManager();
		this.textArea = textArea;
	} //}}}

	//{{{ setAlphaComposite() method
	public void setAlphaComposite(float alpha)
	{
		if (this.alpha != alpha)
		{
			this.alpha = alpha;
			blend = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		}
	} //}}}

	//{{{ paintScreenLineRange() method
	@Override
	public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight)
	{
		fm = textArea.getPainter().getFontMetrics();
		if (highlightManager.isHighlightEnable() &&
		    highlightManager.countHighlights() != 0 ||
		    HighlightManagerTableModel.currentWordHighlight.isEnabled())
			super.paintScreenLineRange(gfx, firstLine, lastLine, physicalLines, start, end, y, lineHeight);
	} //}}}

	//{{{ paintValidLine() method
	/**
	 * Called by the text area when the extension is to paint a
	 * screen line which has an associated physical line number in
	 * the buffer. Note that since one physical line may consist of
	 * several screen lines due to soft wrap, the start and end
	 * offsets of the screen line are passed in as well.
	 *
	 * @param gfx The graphics context
	 * @param screenLine The screen line number
	 * @param physicalLine The physical line number
	 * @param start The offset where the screen line begins, from
	 * the start of the buffer
	 * @param end The offset where the screen line ends, from the
	 * start of the buffer
	 * @param y The y co-ordinate of the top of the line's
	 * bounding box
	 * @since jEdit 4.0pre4
	 */
	@Override
	public void paintValidLine(Graphics2D gfx,
				   int screenLine,
				   int physicalLine,
				   int start,
				   int end,
				   int y)
	{
		textArea.getLineText(physicalLine, lineContent);
		if (lineContent.count == 0)
			return;

		int startOffset = textArea.getLineStartOffset(physicalLine);
		int endOffset = textArea.getLineEndOffset(physicalLine);
		int screenToPhysicalOffset = start - startOffset;
		lineContent.offset += screenToPhysicalOffset;
		lineContent.count -= screenToPhysicalOffset;
		lineContent.count -= endOffset - end;

		JEditBuffer buffer = textArea.getBuffer();
		tempLineContent.array = lineContent.array;
		tempLineContent.offset = lineContent.offset;
		tempLineContent.count = lineContent.count;
		try
		{
			highlightManager.getReadLock();
			for (int i = 0; i < highlightManager.countHighlights(); i++)
			{
				Highlight highlight = highlightManager.getHighlight(i);
				highlight(highlight, buffer, gfx, screenLine, physicalLine, y, screenToPhysicalOffset);
				tempLineContent.offset = lineContent.offset;
				tempLineContent.count = lineContent.count;
			}
		}
		finally
		{
			highlightManager.releaseLock();
		}
		tempLineContent.offset = lineContent.offset;
		tempLineContent.count = lineContent.count;

		highlight(HighlightManagerTableModel.currentWordHighlight, buffer, gfx, screenLine, physicalLine, y, screenToPhysicalOffset);
	} //}}}

	//{{{ highlight() method
	private void highlight(Highlight highlight,
			       JEditBuffer buffer,
			       Graphics2D gfx,
			       int screenLine,
			       int physicalLine,
			       int y,
			       int screenToPhysicalOffset)
	{
		if (!highlight.isEnabled() ||
		    !highlight.isValid() ||
		    (highlight.getScope() == Highlight.BUFFER_SCOPE &&
		     highlight.getBuffer() != buffer))
		{
			return;
		}

		SearchMatcher searchMatcher = highlight.getSearchMatcher();
		boolean subsequence = highlight.isHighlightSubsequence();
		try
		{
			int i = 0;
			SearchMatcher.Match match = null;
			while (true)
			{
				match = searchMatcher.nextMatch(new SegmentCharSequence(tempLineContent),
								(i == 0),
								true,
								match == null,
								false);
				if (match == null || match.end == match.start)
					break;
				_highlight(highlight.getColor(), gfx, physicalLine, match.start + i + screenToPhysicalOffset, match.end + i + screenToPhysicalOffset, y);
				highlight.updateLastSeen();
				if (subsequence)
				{
					tempLineContent.count -= match.start + 1;
					tempLineContent.offset += match.start + 1;
					i += match.start + 1;
				}
				else
				{
					tempLineContent.count -= match.end;

					tempLineContent.offset += match.end;
					i += match.end;
				}

				if (tempLineContent.count <= 0)
					break;
			}
		}
		catch (PatternSyntaxException e)
		{
			// the regexp was invalid
			highlight.setValid(false);
		}
	} //}}}

	//{{{ _highlight() method
	private void _highlight(Color highlightColor,
				Graphics2D gfx,
				int physicalLine,
				int startOffset,
				int endOffset,
				int y)
	{
		Point p = textArea.offsetToXY(physicalLine, startOffset, point);
		if (p == null)
		{
			// The start offset was not visible
			return;
		}
		int startX = p.x;

		p = textArea.offsetToXY(physicalLine, endOffset, point);
		if (p == null)
		{
			// The end offset was not visible
			return;
		}
		int endX = p.x;
		Color oldColor = gfx.getColor();
		Composite oldComposite = gfx.getComposite();
		gfx.setColor(highlightColor);
		gfx.setComposite(blend);
		gfx.fillRect(startX, y, endX - startX, fm.getHeight() - 1);

		if (square)
		{
			gfx.setColor(squareColor);
			gfx.drawRect(startX, y, endX - startX, fm.getHeight() - 1);
		}

		gfx.setColor(oldColor);
		gfx.setComposite(oldComposite);
	} //}}}

	//{{{ highlightUpdated() method
	public void highlightUpdated(boolean highlightEnabled)
	{
		int firstLine = textArea.getFirstPhysicalLine();
		int lastLine = textArea.getLastPhysicalLine();
		textArea.invalidateLineRange(firstLine, lastLine);
	} //}}}
}
