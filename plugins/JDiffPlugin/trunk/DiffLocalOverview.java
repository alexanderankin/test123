/*
 * DiffLocalOverview.java
 * Copyright (C) 2000 Andre Kaplan, original code by mike dillon
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


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import jdiff.util.Diff;

import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public class DiffLocalOverview extends DiffOverview
{
    public DiffLocalOverview(
        Diff.change edits,
        int lineCount0,
        int lineCount1,
        JEditTextArea textArea0,
        JEditTextArea textArea1
    ) {
        super(edits, lineCount0, lineCount1, textArea0, textArea1);
    }


    public void paint(Graphics gfx) {
        int line0  = this.textArea0.getFirstLine();
        int count0 = this.textArea0.getVisibleLines();
        int line1  = this.textArea1.getFirstLine();
        int count1 = this.textArea1.getVisibleLines();

        Rectangle size = getBounds();

        gfx.setColor(getBackground());
        gfx.fillRect(0, 0, size.width, size.height);

        Rectangle inner = new Rectangle(4, 4, size.width - 8, size.height - 8);

        int lines = Math.max(count0, count1);
        double pxlPerLine = ((double) inner.height) / lines;

        Rectangle left = new Rectangle(
            inner.x,
            inner.y,
            inner.width / 3,
            Math.max(1, (int) Math.round(pxlPerLine * count0))
        );
        Rectangle right = new Rectangle(
            inner.x + (inner.width - left.width),
            inner.y,
            left.width,
            Math.max(1, (int) Math.round(pxlPerLine * count1))
        );

        gfx.setColor(Color.black);
        gfx.drawRect(left.x - 1, left.y - 1, left.width + 1, left.height + 1);
        gfx.drawRect(right.x - 1, right.y - 1, right.width + 1, right.height + 1);

        gfx.setColor(Color.white);
        gfx.fillRect(left.x, left.y, left.width, left.height);
        gfx.fillRect(right.x, right.y, right.width, right.height);

        Color color;

        Diff.change hunk = this.edits;
        for (; hunk != null; hunk = hunk.link) {
            if ((hunk.line0 + hunk.deleted - 1) < line0) {
                continue;
            }

            if (hunk.line0 >= (line0 + count0)) {
                break;
            }

            if (hunk.deleted == 0) {
                color = JDiffPlugin.invalidHunkColor;
            } else if (hunk.inserted == 0) {
                color = JDiffPlugin.deletedHunkColor;
            } else {
                color = JDiffPlugin.changedHunkColor;
            }

            int leftOffset = Math.max(0, hunk.line0 - line0);
            int leftCount  = Math.min(
                hunk.deleted - Math.max(0, line0 - hunk.line0),
                count0       - Math.max(0, hunk.line0 - line0) // leftOffset
            );
            left.y  = inner.y + (int) Math.round(leftOffset * pxlPerLine);
            left.height  = Math.max(1, (int) Math.round(leftCount * pxlPerLine));
            gfx.setColor(color);
            gfx.fillRect(left.x, left.y, left.width, left.height);
        }

        hunk = this.edits;
        for (; hunk != null; hunk = hunk.link) {
            if ((hunk.line1 + hunk.inserted - 1) < line1) {
                continue;
            }

            if (hunk.line1 >= (line1 + count1)) {
                break;
            }

            if (hunk.inserted == 0) {
                color = JDiffPlugin.invalidHunkColor;
            } else if (hunk.deleted == 0) {
                color = JDiffPlugin.insertedHunkColor;
            } else {
                color = JDiffPlugin.changedHunkColor;
            }

            int rightOffset = Math.max(0, hunk.line1 - line1);
            int rightCount  = Math.min(
                hunk.inserted - Math.max(0, line1 - hunk.line1),
                count1        - Math.max(0, hunk.line1 - line1) // rightOffset
            );
            right.y  = inner.y + (int) Math.round(rightOffset * pxlPerLine);
            right.height  = Math.max(1, (int) Math.round(rightCount * pxlPerLine));
            gfx.setColor(color);
            gfx.fillRect(right.x, right.y, right.width, right.height);
        }

        hunk = this.edits;
        for (; hunk != null; hunk = hunk.link) {
            if (hunk.line0 < line0) {
                continue;
            }

            if (hunk.line1 < line1) {
                continue;
            }

            if ((hunk.line0 + hunk.deleted - 1) < line0) {
                continue;
            }

            if ((hunk.line1 + hunk.inserted - 1) < line1) {
                continue;
            }

            if (hunk.line0 >= (line0 + count0)) {
                break;
            }

            if (hunk.line1 >= (line1 + count1)) {
                break;
            }

            int leftOffset = hunk.line0 - line0;
            int rightOffset = hunk.line1 - line1;
            int y0 = inner.y + (int) Math.round(leftOffset * pxlPerLine);
            int y1 = inner.y + (int) Math.round(rightOffset * pxlPerLine);
            gfx.setColor(Color.black);
            gfx.drawLine(left.x + left.width + 1, y0, right.x - 1, y1);
        }
    }
}
