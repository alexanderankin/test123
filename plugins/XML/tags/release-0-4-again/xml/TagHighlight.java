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

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.awt.event.MouseEvent;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaHighlight;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

import org.gjt.sp.util.Log;


public class TagHighlight
	implements TextAreaHighlight
{
	// (EditPane, TagHighlight) association
	private static Hashtable highlights = new Hashtable();

	private static DocumentListener documentHandler = new DocumentHandler();

	private static Color tagHighlightColor = GUIUtilities.parseColor(
		jEdit.getProperty("xml.tag-highlight-color"));

	private boolean documentUpdated = false;
	private int caretPos  = -1;
	private int firstLine = -1;
	private MatchTag.TagAttribute match = null;

	private JEditTextArea textArea;
	private TextAreaHighlight next;
	private boolean enabled = false;


	private TagHighlight() {}


	public void init(JEditTextArea textArea, TextAreaHighlight next)
	{
		this.textArea = textArea;
		this.next = next;
	}


	public void paintHighlight(Graphics gfx, int virtualLine, int y)
	{
		if (this.isEnabled())
		{
			Buffer buffer = this.textArea.getBuffer();
			int physicalLine = buffer.virtualToPhysical(virtualLine);

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

			if (this.documentUpdated ||
				this.caretPos != this.textArea.getCaretPosition() ||
				this.firstLine != buffer.virtualToPhysical(this.textArea.getFirstLine())
			)
			{
				this.updateCaretPosition();
				this.documentUpdated = false;
			}

			if (this.match != null)
			{
				int match_start_line = this.textArea.getLineOfOffset(this.match.startpos);
				int match_end_line   = this.textArea.getLineOfOffset(this.match.endpos);

				if (match_start_line <= physicalLine && physicalLine <= match_end_line)
				{
					int x0 = this.textArea.offsetToX(
						physicalLine,
						Math.max(this.match.startpos, this.textArea.getLineStartOffset(physicalLine))
						- this.textArea.getLineStartOffset(physicalLine)
					);
					int x1 = this.textArea.offsetToX(
						physicalLine,
						Math.min(this.match.endpos, this.textArea.getLineEndOffset(physicalLine) - 1)
						- this.textArea.getLineStartOffset(physicalLine)
					);
					TextAreaPainter painter = this.textArea.getPainter();
					FontMetrics fm = painter.getFontMetrics();
					int descent = fm.getDescent();
					int y0 = y + descent + 1;
					gfx.setColor(tagHighlightColor);
					gfx.drawRect(x0, y0, (x1 - x0) - 1, fm.getHeight() - 1);
				}
			}
		}

		if (this.next != null)
			this.next.paintHighlight(gfx, virtualLine, y);
	}


	public String getToolTipText(MouseEvent evt)
	{
		if (this.next == null) { return null; }

		return this.next.getToolTipText(evt);
	}


	private void updateTextArea()
	{
		Buffer buffer = this.textArea.getBuffer();
		int physicalFirst = buffer.virtualToPhysical(
			this.textArea.getFirstLine()
		);
		int physicalLast  = buffer.virtualToPhysical(
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
		Buffer buffer  = this.textArea.getBuffer();
		this.match	 = null;
		this.caretPos  = this.textArea.getCaretPosition();
		this.firstLine = buffer.virtualToPhysical(this.textArea.getFirstLine());

		int firstLineStartOffset = this.textArea.getLineStartOffset(
			this.firstLine
		);
		int lastLineEndOffset	= this.textArea.getLineEndOffset(Math.min(
			 buffer.virtualToPhysical(this.textArea.getFirstLine() + this.textArea.getVisibleLines() - 1)
		   , this.textArea.getLineCount() - 1
		));

		if (!(this.caretPos >= firstLineStartOffset && this.caretPos < lastLineEndOffset)) {
			return;
		}

		String visibleText = this.textArea.getText(
			firstLineStartOffset, (lastLineEndOffset - 1) - firstLineStartOffset
		);

		MatchTag.TagAttribute tag_attr =
			MatchTag.getSelectedTag(
				this.caretPos - firstLineStartOffset,
				visibleText
			);

		/*
		if (tag_attr != null) {
			Log.log(Log.DEBUG, this, "**** Tag:start:end = " + tag_attr.tag + ":" + tag_attr.startpos + ":" + tag_attr.endpos);
		}
		*/

		if (tag_attr != null && (tag_attr.tag).charAt(0) != '/') {
			MatchTag.TagAttribute endtag_attr = null;
			endtag_attr = MatchTag.findEndTag(visibleText, tag_attr.tag, tag_attr.endpos + 1, 0);
			if (endtag_attr != null) {
				// Log.log(Log.DEBUG, this, "**** EndTag:start:end = " + endtag_attr.tag + ":" + endtag_attr.startpos + ":" + endtag_attr.endpos);

				this.match = endtag_attr;
				this.match.startpos += firstLineStartOffset;
				this.match.endpos   += firstLineStartOffset;
			}
		}

		if (tag_attr != null && (tag_attr.tag).charAt(0) == '/') {
			MatchTag.TagAttribute starttag_attr = null;
			starttag_attr = MatchTag.findStartTag(visibleText, (tag_attr.tag).substring(1), (tag_attr.startpos - 1), 0);
			if (starttag_attr != null) {
				// Log.log(Log.DEBUG, this, "**** StartTag:start:end = " + starttag_attr.tag + ":" + starttag_attr.startpos + ":" + starttag_attr.endpos);

				this.match = starttag_attr;
				this.match.startpos += firstLineStartOffset;
				this.match.endpos   += firstLineStartOffset;
			}
		}

		this.updateTextArea();
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
		buffer.putBooleanProperty("xml.tag-highlight",enabled);

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
					highlight.documentUpdated = true;
					highlight.setEnabled(enabled);
					highlight.updateTextArea();
				}
			}
		}
	}

	public static TextAreaHighlight addHighlightTo(EditPane editPane)
	{
		TagHighlight textAreaHighlight = new TagHighlight();
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
		buffer.addDocumentListener(TagHighlight.documentHandler);
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
		buffer.removeDocumentListener(TagHighlight.documentHandler);
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


	private static class DocumentHandler implements DocumentListener
	{
		public void insertUpdate(DocumentEvent e)
		{
			this.documentUpdated(e.getDocument());
		}


		public void removeUpdate(DocumentEvent e)
		{
			this.documentUpdated(e.getDocument());
		}


		public void changedUpdate(DocumentEvent e)
		{
			this.documentUpdated(e.getDocument());
		}


		private void documentUpdated(Document doc)
		{
			// Propagate the changes to all textareas
			View[] views = jEdit.getViews();
			for (int i = 0; i < views.length; i++)
			{
				EditPane[] editPanes = views[i].getEditPanes();
				TagHighlight highlight;
				for (int j = 0; j < editPanes.length; j++)
				{
					if (editPanes[j].getBuffer() != doc)
						continue;

					highlight = (TagHighlight) highlights.get(editPanes[j]);

					highlight.documentUpdated = true;
				}
			}
		}
	}
}
