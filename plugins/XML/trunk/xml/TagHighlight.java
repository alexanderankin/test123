/*
 * TagHighlight.java
 * Copyright (c) 2000 Andre Kaplan
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;


public class TagHighlight implements TextAreaHighlight
{
	// (EditPane, TagHighlight) association
	private static Hashtable highlights = new Hashtable();

	private static BufferChangeAdapter bufferHandler = new BufferHandler();

	private static Color tagHighlightColor = GUIUtilities.parseColor(
		jEdit.getProperty("xml.tag-highlight-color"));

	private boolean bufferUpdated = false;
	private int caretPos  = -1;
	private int firstLine = -1;
	private MatchTag.TagAttribute match = null;

	private JEditTextArea textArea;
	private boolean enabled = false;


	private TagHighlight(JEditTextArea textArea)
	{
		this.textArea = textArea;
	}


	public void paintHighlight(Graphics gfx, int virtualLine, int y)
	{
		if (this.isEnabled())
		{
			Buffer buffer = this.textArea.getBuffer();
			if(!buffer.isLoaded())
				return;

			int physicalLine = textArea.virtualToPhysical(virtualLine);

			try
			{
				if (	(this.textArea.getLineStartOffset(physicalLine) == -1)
					||  (this.textArea.getLineEndOffset(physicalLine) == -1)
				)
				{
					return;
				}
			}
			catch (Exception e)
			{
				return;
			}

			if (this.bufferUpdated ||
				this.caretPos != this.textArea.getCaretPosition() ||
				this.firstLine != textArea.virtualToPhysical(this.textArea.getFirstLine())
			)
			{
				this.updateCaretPosition();
				this.bufferUpdated = false;
			}
			if (this.match != null) {
				int match_start_line = this.textArea.getLineOfOffset(this.match.start);
				if (match_start_line == physicalLine) {
					int match_end_line = this.textArea.getLineOfOffset(this.match.end);
					int nLines = match_end_line - match_start_line + 1;
					int nPoints = (nLines) * 4;
					int[] xs = new int[nPoints];
					int[] ys = new int[nPoints];
					FontMetrics fm = this.textArea.getPainter().getFontMetrics();

					for (int i = 0; i < nLines; ++i) {
						int i2 = i * 2;
						if (i == 0) {
							xs[0] = xs[1] = this.textArea.offsetToX(
								match_start_line,
								this.match.start - this.textArea.getLineStartOffset(match_start_line)
							);
							ys[0] = ys[nPoints - 1] = y + fm.getDescent();
						} else {
							xs[i2] = xs[i2+1] = this.textArea.offsetToX(match_start_line + i, 0);
							ys[i2] = ys[nPoints-(i2+1)] = ys[i2-1];
						}
						if (i == nLines - 1) {
							xs[nPoints-(i2+1)] = xs[nPoints-(i2+2)] = this.textArea.offsetToX(
								match_start_line + i,
								this.match.end - this.textArea.getLineStartOffset(match_start_line + i)
							);
							ys[i2+1] = ys[nPoints-(i2+2)] = ys[i2] + fm.getHeight() - 1;
						} else {
							xs[nPoints-(i2+1)] = xs[nPoints-(i2+2)] = this.textArea.offsetToX(
								match_start_line + i,
								this.textArea.getLineEndOffset(match_start_line + i) - 1
							);
							ys[i2+1] = ys[nPoints-(i2+2)] = ys[i2] + fm.getHeight();
						}
					}
					gfx.setColor(tagHighlightColor);
					gfx.drawPolygon(xs, ys, nPoints);
				}
			}
		}
	}


	public String getToolTipText(MouseEvent evt)
	{
		return null;
	}


	private void updateTextArea()
	{
		Buffer buffer = this.textArea.getBuffer();
		int physicalFirst = textArea.virtualToPhysical(
			this.textArea.getFirstLine()
		);
		int physicalLast  = textArea.virtualToPhysical(
			this.textArea.getFirstLine() + this.textArea.getVisibleLines()
		);
		this.textArea.invalidateLineRange(physicalFirst, physicalLast);
	}

	private boolean isEnabled()
	{
		return enabled;
	}

	private void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	private void updateCaretPosition()
	{
		/* if(textArea.getVisibleLines() == 0)
			return;

		Buffer buffer  = this.textArea.getBuffer();
		this.match	 = null;
		this.caretPos  = this.textArea.getCaretPosition();
		this.firstLine = textArea.virtualToPhysical(this.textArea.getFirstLine());

		int firstLineStartOffset = this.textArea.getLineStartOffset(
			this.firstLine
		);
		int lastLineEndOffset	= this.textArea.getLineEndOffset(Math.min(
			 textArea.virtualToPhysical(this.textArea.getFirstLine() + this.textArea.getVisibleLines() - 1)
		   , this.textArea.getLineCount() - 1
		));

		if (!(this.caretPos >= firstLineStartOffset && this.caretPos < lastLineEndOffset)) {
			return;
		}

		String visibleText = this.textArea.getText(
			firstLineStartOffset, (lastLineEndOffset - 1) - firstLineStartOffset
		);

		MatchTag.TagAttribute tagAttr =
			MatchTag.getSelectedTag(
				this.caretPos - firstLineStartOffset,
				visibleText
			); */


		/*
		if (tagAttr != null) {
			Log.log(Log.DEBUG, this, "**** Tag:start:end = " + tagAttr.tag + ":" + tagAttr.start + ":" + tagAttr.end);
		}
		*/


		/* if (tagAttr != null && tagAttr.type != MatchTag.T_STANDALONE_TAG) {
			MatchTag.TagAttribute matchingTagAttr = MatchTag.getMatchingTagAttr(visibleText, tagAttr);
			if (matchingTagAttr != null) {
				//Log.log(Log.DEBUG, this, "**** MatchingTag:start:end = " + matchingTagAttr.tag + ":" + matchingTagAttr.start + ":" + matchingTagAttr.end);

				this.match = matchingTagAttr;
				this.match.start += firstLineStartOffset;
				this.match.end   += firstLineStartOffset + 1;
			}
		}

		this.updateTextArea(); */
	}

	/**
	* Tests if the tag highlights are enabled for an editPane
	**/
	public static boolean isTagHighlightEnabledFor(EditPane editPane) {
		Buffer buffer = editPane.getBuffer();
		return buffer.getBooleanProperty("xml.tag-highlight");
	}


	/**
	 * Gets TagHighlight for an editPane
	**/
	public static TagHighlight getTagHighlightFor(EditPane editPane)
	{
		return (TagHighlight) highlights.get(editPane);
	}


	/**
	 * Sets tag highlighting to enabled or disabled for a editPane
	**/
	public static void setTagHighlightFor(EditPane editPane, boolean enabled)
	{
		Buffer buffer   = editPane.getBuffer();
		buffer.setBooleanProperty("xml.tag-highlight",enabled);

		// Propagate the change to all edit panes with the same buffer
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
		{

			EditPane[] editPanes = views[i].getEditPanes();
			for (int j = 0; j < editPanes.length; j++)
			{
				if (editPanes[j].getBuffer() != buffer) { continue; }
				TagHighlight highlight;
				highlight = (TagHighlight) highlights.get(editPanes[j]);
				if (highlight != null && highlight.isEnabled() != enabled)
				{
					highlight.bufferUpdated = true;
					highlight.setEnabled(enabled);
					highlight.updateTextArea();
				}
			}
		}
	}

	public static TextAreaHighlight addHighlightTo(EditPane editPane)
	{
		TagHighlight textAreaHighlight = new TagHighlight(
			editPane.getTextArea());
		textAreaHighlight.setEnabled(jEdit.getBooleanProperty(
			"xml.tag-highlight"));
		highlights.put(editPane, textAreaHighlight);
		return textAreaHighlight;
	}

	public static void removeHighlightFrom(EditPane editPane)
	{
		highlights.remove(editPane);
	}

	public static void bufferCreated(Buffer buffer)
	{
		buffer.addBufferChangeListener(TagHighlight.bufferHandler);
	}

	public static void bufferLoaded(Buffer buffer)
	{
		boolean enabled = buffer.getBooleanProperty("xml.tag-highlight");

		// Propagate the changes to all textareas
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
		{
			EditPane[] editPanes = views[i].getEditPanes();
			TagHighlight highlight;
			for (int j = 0; j < editPanes.length; j++)
			{
				if (editPanes[j].getBuffer() != buffer)
					continue;

				highlight = (TagHighlight) highlights.get(editPanes[j]);

				highlight.setEnabled(enabled);
			}
		}
	}

	public static void bufferClosed(Buffer buffer)
	{
		buffer.removeBufferChangeListener(TagHighlight.bufferHandler);
	}

	public static void bufferChanged(EditPane editPane)
	{
		Buffer buffer = editPane.getBuffer();
		boolean enabled = buffer.getBooleanProperty("xml.tag-highlight");

		TagHighlight tagHighlight =
			TagHighlight.getTagHighlightFor(editPane);

		tagHighlight.setEnabled(enabled);
	}


	public static void propertiesChanged()
	{
		Color newTagHighlightColor = GUIUtilities.parseColor(
			jEdit.getProperty("xml.tag-highlight-color")
		);

		boolean tagHighlightColorChanged = !(newTagHighlightColor.equals(tagHighlightColor));

		if (!tagHighlightColorChanged)
			return;

		tagHighlightColor = newTagHighlightColor;

		// Propagate the changes to all textareas
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
		{
			EditPane[] editPanes = views[i].getEditPanes();
			TagHighlight highlight;
			for (int j = 0; j < editPanes.length; j++)
			{
				highlight = (TagHighlight) highlights.get(editPanes[j]);

				if (highlight.isEnabled())
					highlight.updateTextArea();
			}
		}
	}


	private static class BufferHandler extends BufferChangeAdapter
	{
		public void contentInserted(Buffer buffer, int startLine,
			int offset, int numLines, int length)
		{
			this.bufferUpdated(buffer);
		}


		public void contentRemoved(Buffer buffer, int startLine,
			int offset, int numLines, int length)
		{
			this.bufferUpdated(buffer);
		}


		private void bufferUpdated(Buffer buffer)
		{
			// Propagate the changes to all textareas
			View[] views = jEdit.getViews();
			for (int i = 0; i < views.length; i++)
			{
				EditPane[] editPanes = views[i].getEditPanes();
				TagHighlight highlight;
				for (int j = 0; j < editPanes.length; j++)
				{
					if (editPanes[j].getBuffer() != buffer)
						continue;

					highlight = (TagHighlight) highlights.get(editPanes[j]);

					highlight.bufferUpdated = true;
				}
			}
		}
	}
}
