/*
 * DiffGlobalVirtualOverview.java
 * Copyright (C) 2000, 2001, 2002 Andre Kaplan
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
import java.awt.Graphics;
import java.awt.Rectangle;

import jdiff.util.Diff;

import org.gjt.sp.jedit.textarea.FoldVisibilityManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public class DiffGlobalVirtualOverview extends DiffOverview
{
    public DiffGlobalVirtualOverview(
        Diff.change edits,
        int lineCount0,
        int lineCount1,
        JEditTextArea textArea0,
        JEditTextArea textArea1
    ) {
        super(edits, lineCount0, lineCount1, textArea0, textArea1);
    }


    public void paint(Graphics gfx) {
        FoldVisibilityManager foldVisibilityManager0 = this.textArea0.getFoldVisibilityManager();
        FoldVisibilityManager foldVisibilityManager1 = this.textArea1.getFoldVisibilityManager();

        int virtualLineCount0 = this.textArea0.getVirtualLineCount();
        int virtualLineCount1 = this.textArea1.getVirtualLineCount();

        int lineCount0 = this.textArea0.getLineCount();
        int lineCount1 = this.textArea1.getLineCount();

        Rectangle size = getBounds();

        gfx.setColor(getBackground());
        gfx.fillRect(0, 0, size.width, size.height);

        Rectangle inner = new Rectangle(4, 4, size.width - 8, size.height - 8);

        int lines = Math.max(virtualLineCount0, virtualLineCount1);
        double pxlPerLine = ((double) inner.height) / lines;

        Rectangle left = new Rectangle(
            inner.x,
            inner.y,
            inner.width / 3,
            Math.max(1, (int) Math.round(pxlPerLine * virtualLineCount0))
        );
        Rectangle right = new Rectangle(
            inner.x + (inner.width - left.width),
            inner.y,
            left.width,
            Math.max(1, (int) Math.round(pxlPerLine * virtualLineCount1))
        );

        Rectangle cursor = new Rectangle(
            inner.x + inner.width / 2 - 1, inner.y, 2, 0
        );

        Color leftColor  = JDiffPlugin.overviewInvalidColor;
        Color rightColor = JDiffPlugin.overviewInvalidColor;

        gfx.setColor(Color.black);
        gfx.drawRect(left.x - 1, left.y - 1, left.width + 1, left.height + 1);
        gfx.drawRect(right.x - 1, right.y - 1, right.width + 1, right.height + 1);

        gfx.setColor(Color.white);
        gfx.fillRect(left.x, left.y, left.width, left.height);
        gfx.fillRect(right.x, right.y, right.width, right.height);

        Diff.change hunk = this.edits;

        int virtualLeftHeight  = 0;
        int virtualRightHeight = 0;
        int virtualLeftOffset  = 0;
        int virtualRightOffset = 0;
        for (; hunk != null; hunk = hunk.link) {

            if ((hunk.line0 + hunk.deleted) >= lineCount0) { break; }
            if ((hunk.line1 + hunk.inserted) >= lineCount1) { break; }

            virtualLeftOffset  = foldVisibilityManager0.physicalToVirtual(hunk.line0);
            virtualRightOffset = foldVisibilityManager1.physicalToVirtual(hunk.line1);

            if (hunk.inserted == 0 && hunk.deleted != 0) { // DELETE
               leftColor  = JDiffPlugin.overviewDeletedColor;
               rightColor = JDiffPlugin.overviewInvalidColor;
            } else if (hunk.inserted != 0 && hunk.deleted == 0) { // INSERT
               leftColor  = JDiffPlugin.overviewInvalidColor;
               rightColor = JDiffPlugin.overviewInsertedColor;
            } else { // CHANGE
               leftColor  = JDiffPlugin.overviewChangedColor;
               rightColor = JDiffPlugin.overviewChangedColor;
            }

            virtualLeftHeight = 0;
            if (hunk.deleted != 0) {
                virtualLeftHeight = (
                      foldVisibilityManager0.physicalToVirtual(Math.min(lineCount0 - 1, hunk.line0 + hunk.deleted))
                    - virtualLeftOffset
                );
            }

            virtualRightHeight = 0;
            if (hunk.inserted != 0) {
                virtualRightHeight = (
                      foldVisibilityManager1.physicalToVirtual(Math.min(lineCount1 - 1, hunk.line1 + hunk.inserted))
                    - virtualRightOffset
                );
            }

            left.y  = inner.y + (int) Math.round(virtualLeftOffset * pxlPerLine);
            right.y = inner.y + (int) Math.round(virtualRightOffset * pxlPerLine);
            left.height  = Math.max(1, (int) Math.round(virtualLeftHeight * pxlPerLine));
            right.height = Math.max(1, (int) Math.round(virtualRightHeight * pxlPerLine));
            gfx.setColor(leftColor);
            gfx.fillRect(left.x, left.y, left.width, left.height);
            gfx.setColor(rightColor);
            gfx.fillRect(right.x, right.y, right.width, right.height);

            gfx.setColor(Color.black);
            gfx.drawLine(left.x + left.width + 1, left.y, right.x - 1, right.y);
        }

        // Display the textArea cursor
        this.paintCursor(gfx);
    }


    public void paintCursor(Graphics gfx) {
        int virtualLineCount0 = this.textArea0.getVirtualLineCount();
        int virtualLineCount1 = this.textArea1.getVirtualLineCount();

        Rectangle size = getBounds();

        Rectangle inner = new Rectangle(4, 4, size.width - 8, size.height - 8);

        int lines = Math.max(virtualLineCount0, virtualLineCount1);
        double pxlPerLine = ((double) inner.height) / lines;

        int virtualFirstLine0 = this.textArea0.getFirstLine();
        int virtualLastLine0  = (
              this.textArea0.getFirstLine()
            + this.textArea0.getVisibleLines() - 1
        );
        Rectangle leftCursor = new Rectangle(
            inner.x, inner.y + ((int) Math.round(pxlPerLine * virtualFirstLine0)),
            inner.width / 3,
            Math.max(1, (int) Math.round(pxlPerLine * Math.min(virtualLineCount0, virtualLastLine0 - virtualFirstLine0 + 1)))
        );

        int virtualFirstLine1 = this.textArea1.getFirstLine();
        int virtualLastLine1  = (
              this.textArea1.getFirstLine()
            + this.textArea1.getVisibleLines() - 1
        );
        Rectangle rightCursor = new Rectangle(
            inner.x + (inner.width - leftCursor.width),
            inner.y + ((int) Math.round(pxlPerLine * virtualFirstLine1)),
            leftCursor.width,
            Math.max(1, (int) Math.round(pxlPerLine * Math.min(virtualLineCount1, virtualLastLine1 - virtualFirstLine1 + 1)))
        );

        gfx.setColor(JDiffPlugin.leftCursorColor);
        gfx.drawRect(leftCursor.x, leftCursor.y, leftCursor.width - 1, leftCursor.height - 1);
        gfx.setColor(JDiffPlugin.rightCursorColor);
        gfx.drawRect(rightCursor.x, rightCursor.y, rightCursor.width - 1, rightCursor.height - 1);
    }
}

