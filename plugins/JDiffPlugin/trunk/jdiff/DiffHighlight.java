/*
 * DiffHighlight.java
 * Copyright (c) 2000 Andre Kaplan
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


package jdiff;


import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import java.awt.event.MouseEvent;

import java.util.Hashtable;

import jdiff.util.Diff;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

import org.gjt.sp.util.Log;


public class DiffHighlight extends TextAreaExtension
{
    public static final Position LEFT  = new Position();
    public static final Position RIGHT = new Position();

    public static class Position {
        public Position() {}
    }

    // (EditPane, DiffHighlight) association
    private static Hashtable highlights = new Hashtable();

    private JEditTextArea textArea;
    private boolean enabled = false;
    private Diff.change edits;
    private Position position;


    private DiffHighlight(JEditTextArea textArea, Diff.change edits, Position position) {
        this.textArea = textArea;
        this.edits    = edits;
        this.position = position;
    }


    public void paintValidLine(
            Graphics2D gfx, final int screenLine, final int physicalLine,
            final int start, final int end, final int y
    ) {
        if (this.isEnabled())
        {
            try {
                if (    (this.textArea.getLineStartOffset(physicalLine) == -1)
                    ||  (this.textArea.getLineEndOffset(physicalLine) == -1)
                ) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            Diff.change hunk = this.edits;
            Color color;
            Color selectionColor;

            if (this.position == DiffHighlight.LEFT) {
                for (; hunk != null; hunk = hunk.link) {
                    if (hunk.line0 > physicalLine) {
                        break;
                    }

                    if (hunk.deleted == 0) {
                        if (hunk.line0 != physicalLine) { continue; }
                        color = JDiffPlugin.highlightInvalidColor;
                        TextAreaPainter painter = this.textArea.getPainter();
                        gfx.setColor(color);
                        gfx.drawLine(0, y, painter.getWidth() - 1, y);
                        continue;
                    }

                    if ((hunk.line0 + hunk.deleted - 1) < physicalLine) {
                        continue;
                    }

                    if (hunk.inserted == 0) {
                        color = JDiffPlugin.highlightDeletedColor;
                        selectionColor = JDiffPlugin.selectedHighlightDeletedColor;
                    } else {
                        color = JDiffPlugin.highlightChangedColor;
                        selectionColor = JDiffPlugin.selectedHighlightChangedColor;
                    }

                    TextAreaPainter painter = this.textArea.getPainter();
                    FontMetrics fm = painter.getFontMetrics();
                    gfx.setColor(color);
                    gfx.fillRect(0, y, painter.getWidth(), fm.getHeight());

                    // We paint the selected part of the hunk
                    Selection[] selections = this.textArea.getSelection();
                    for (int i = 0; i < selections.length; i++) {
                        this.paintSelectionHighlight(gfx, physicalLine, y, selectionColor, selections[i]);
                    }

                    break;
                 }
            } else { // DiffHighlight.RIGHT
                for (; hunk != null; hunk = hunk.link) {
                    if (hunk.line1 > physicalLine) {
                        break;
                    }

                    if (hunk.inserted == 0) {
                        if (hunk.line1 != physicalLine) { continue; }
                        color = JDiffPlugin.highlightInvalidColor;
                        TextAreaPainter painter = this.textArea.getPainter();
                        gfx.setColor(color);
                        gfx.drawLine(0, y, painter.getWidth() - 1, y);
                        continue;
                    }

                    if ((hunk.line1 + hunk.inserted - 1) < physicalLine) {
                        continue;
                    }

                    if (hunk.deleted == 0) {
                        color = JDiffPlugin.highlightInsertedColor;
                        selectionColor = JDiffPlugin.selectedHighlightInsertedColor;
                    } else {
                        color = JDiffPlugin.highlightChangedColor;
                        selectionColor = JDiffPlugin.selectedHighlightChangedColor;
                    }

                    TextAreaPainter painter = this.textArea.getPainter();
                    FontMetrics fm = painter.getFontMetrics();
                    gfx.setColor(color);
                    gfx.fillRect(0, y, painter.getWidth(), fm.getHeight());

                    // We paint the selected part of the hunk
                    Selection[] selections = this.textArea.getSelection();
                    for (int i = 0; i < selections.length; i++) {
                        this.paintSelectionHighlight(gfx, physicalLine, y, selectionColor, selections[i]);
                    }

                    break;
                 }
            }
        }
    }


    private void paintSelectionHighlight(
            Graphics2D gfx, final int physicalLine, final int y,
            Color selectionColor, Selection s
    ) {
        if ((physicalLine < s.getStartLine()) || (physicalLine > s.getEndLine())) {
            return;
        }

        TextAreaPainter painter = this.textArea.getPainter();
        FontMetrics fm = painter.getFontMetrics();

        int lineStart = this.textArea.getLineStartOffset(physicalLine);
        Point p1 = new Point();
        Point p2 = new Point();

        if (s instanceof Selection.Rect) {
            int lineLen = this.textArea.getLineLength(physicalLine);
            this.textArea.offsetToXY(physicalLine,Math.min(lineLen,
                s.getStart() - this.textArea.getLineStartOffset(
                s.getStartLine())), p1);
            this.textArea.offsetToXY(physicalLine,Math.min(lineLen,
                s.getEnd() - this.textArea.getLineStartOffset(
                s.getEndLine())), p2);

            if (p1.x > p2.x) {
                int tmp = p2.x;
                p2.x = p1.x;
                p1.x = tmp;
            }
        } else if (s.getStartLine() == s.getEndLine()) {
            this.textArea.offsetToXY(physicalLine, s.getStart() - lineStart, p1);
            this.textArea.offsetToXY(physicalLine, s.getEnd() - lineStart, p2);
        } else if (physicalLine == s.getStartLine()) {
            this.textArea.offsetToXY(physicalLine, s.getStart() - lineStart, p1);
            p2.x = painter.getWidth();
        } else if (physicalLine == s.getEndLine()) {
            p1.x = 0;
            this.textArea.offsetToXY(physicalLine, s.getEnd() - lineStart, p2);
        } else {
            p1.x = 0;
            p2.x = painter.getWidth();
        }

        if(p1.x == p2.x) {
            p2.x++;
        }

        gfx.setColor(selectionColor);
        gfx.fillRect(p1.x, y, p2.x - p1.x, fm.getHeight());
    }


    public boolean isEnabled() {
        return this.enabled;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }


    public Diff.change getEdits() {
        return this.edits;
    }


    public void setEdits(Diff.change edits) {
        this.edits = edits;
    }


    public Position getPosition() {
        return this.position;
    }


    public void setPosition(Position position) {
        this.position = position;
    }


    public void updateTextArea() {
        if (this.textArea == null) { return; }

        int first = this.textArea.getFirstLine();
        int last = first + this.textArea.getVisibleLines();
        this.textArea.invalidateLineRange(first, last);
    }


    /**
     * Tests if the diff highlights are enabled for an editPane
    **/
    public static boolean isDiffHighlightEnabledFor(EditPane editPane) {
        DiffHighlight highlight = (DiffHighlight) highlights.get(editPane);
        if (highlight != null) {
            return highlight.isEnabled();
        }

        return false;
    }


    /**
     * Sets diff highlighting to enabled or disabled for an editPane
    **/
    public static void setDiffHighlightFor(EditPane editPane, boolean enabled) {
        DiffHighlight highlight = (DiffHighlight) highlights.get(editPane);
        if (highlight != null) {
            highlight.setEnabled(enabled);
            highlight.updateTextArea();
        }
    }


    /**
     * Enables diff highlights for an editPane
    **/
    public static void enableDiffHighlightFor(EditPane editPane) {
        DiffHighlight.setDiffHighlightFor(editPane, true);
    }


    /**
     * Disables diff highlights for an editPane
    **/
    public static void disableDiffHighlightFor(EditPane editPane) {
        DiffHighlight.setDiffHighlightFor(editPane, false);
    }


    public static TextAreaExtension getHighlightFor(EditPane editPane) {
        return (TextAreaExtension ) highlights.get(editPane);
    }


    public static TextAreaExtension  addHighlightTo(EditPane editPane, Diff.change edits, Position position) {
        TextAreaExtension textAreaHighlight = new DiffHighlight(
            editPane.getTextArea(), edits, position
        );
        highlights.put(editPane, textAreaHighlight);
        return textAreaHighlight;
    }


    public static void removeHighlightFrom(EditPane editPane) {
        highlights.remove(editPane);
    }
}

