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

import org.gjt.sp.jedit.Buffer;
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
        Buffer buffer0 = this.textArea0.getBuffer();
        Buffer buffer1 = this.textArea1.getBuffer();

        int virtualLine0  = this.textArea0.getFirstLine();
        int virtualLine1  = this.textArea1.getFirstLine();
        int count0 = this.textArea0.getVisibleLines();
        int count1 = this.textArea1.getVisibleLines();

        Rectangle size = getBounds();

        gfx.setColor(getBackground());
        gfx.fillRect(0, 0, size.width, size.height);

        Rectangle inner = new Rectangle(4, 0, size.width - 8, size.height);

        int lines = Math.max(count0, count1);
        int pxlPerLine = this.textArea0.getPainter().getFontMetrics().getHeight();

        Rectangle rect0 = new Rectangle(
            inner.x,
            inner.y,
            inner.width / 3,
            Math.max(1, pxlPerLine * count0)
        );
        Rectangle border0 = new Rectangle(rect0);

        Rectangle rect1 = new Rectangle(
            inner.x + (inner.width - (inner.width / 3)),
            inner.y,
            inner.width / 3,
            Math.max(1, pxlPerLine * count1)
        );
        Rectangle border1 = new Rectangle(rect1);

        gfx.setColor(Color.white);
        gfx.fillRect(rect0.x, rect0.y, rect0.width, rect0.height);
        gfx.fillRect(rect1.x, rect1.y, rect1.width, rect1.height);

        Color color;

        Diff.change hunk = this.edits;
        for (int i0 = 0; i0 < count0; i0++) {
            int physicalLine0 = buffer0.virtualToPhysical(virtualLine0 + i0);

            for (; hunk != null; hunk = hunk.link) {
                if ((hunk.line0 + Math.max(0, hunk.deleted - 1)) < physicalLine0) {
                    continue;
                }

                if (hunk.line0 > physicalLine0) {
                    break;
                }

                if (hunk.deleted == 0) {
                    color = JDiffPlugin.invalidHunkColor;
                    rect0.height = 1;
                } else {
                    if (hunk.inserted == 0) {
                        color = JDiffPlugin.deletedHunkColor;
                    } else {
                        color = JDiffPlugin.changedHunkColor;
                    }

                    rect0.height = Math.max(1, pxlPerLine);
                }

                rect0.y = inner.y + (i0 * pxlPerLine);
                gfx.setColor(color);
                gfx.fillRect(rect0.x, rect0.y, rect0.width, rect0.height);

                break;
            }
        }

        hunk = this.edits;
        for (int i1 = 0; i1 < count1; i1++) {
            int physicalLine1 = buffer1.virtualToPhysical(virtualLine1 + i1);

            for (; hunk != null; hunk = hunk.link) {
                if ((hunk.line1 + Math.max(0, hunk.inserted - 1)) < physicalLine1) {
                    continue;
                }

                if (hunk.line1 > physicalLine1) {
                    break;
                }

                if (hunk.inserted == 0) {
                    color = JDiffPlugin.invalidHunkColor;
                    rect1.height = 1;
                } else {
                    if (hunk.deleted == 0) {
                        color = JDiffPlugin.insertedHunkColor;
                    } else {
                        color = JDiffPlugin.changedHunkColor;
                    }

                    rect1.height = Math.max(1, pxlPerLine);
                }

                rect1.y = inner.y + (i1 * pxlPerLine);
                gfx.setColor(color);
                gfx.fillRect(rect1.x, rect1.y, rect1.width, rect1.height);

                break;
            }
        }

        hunk = this.edits;
        for (int i0 = 0, i1 = 0; (hunk != null) && (i0 < count0) && (i1 < count1); ) {
            int physicalLine0 = buffer0.virtualToPhysical(virtualLine0 + i0);
            int physicalLine1 = buffer1.virtualToPhysical(virtualLine1 + i1);

            for (; hunk != null; hunk = hunk.link) {
                if (hunk.line0 < physicalLine0) {
                    continue;
                }

                if (hunk.line1 < physicalLine1) {
                    continue;
                }

                if ((hunk.line0 + hunk.deleted) < physicalLine0) {
                    continue;
                }

                if ((hunk.line1 + hunk.inserted) < physicalLine1) {
                    continue;
                }

                if (hunk.line0 > physicalLine0) {
                    i0++;
                    break;
                }

                if (hunk.line1 > physicalLine1) {
                    i1++;
                    break;
                }

                int y0 = inner.y + (i0 * pxlPerLine);
                int y1 = inner.y + (i1 * pxlPerLine);
                gfx.setColor(Color.black);
                gfx.drawLine(rect0.x + rect0.width + 1, y0, rect1.x - 1, y1);
            }
        }

        gfx.setColor(Color.black);
        gfx.drawRect(border0.x - 1, border0.y, border0.width + 1, border0.height - 1);
        gfx.drawRect(border1.x - 1, border1.y, border1.width + 1, border1.height - 1);
    }
}
